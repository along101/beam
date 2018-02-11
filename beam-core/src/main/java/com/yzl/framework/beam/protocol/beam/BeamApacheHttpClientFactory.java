package com.yzl.framework.beam.protocol.beam;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.BeamMessageConstant;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.exception.*;
import com.yzl.framework.beam.filter.AccessMetricsFilter;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.serialize.ProtobufSerializationFactory;
import com.yzl.framework.beam.serialize.Serialization;
import com.yzl.framework.beam.serialize.SerializationFactory;
import com.yzl.framework.beam.transport.HttpClient;
import com.yzl.framework.beam.transport.HttpClientFactory;
import com.yzl.framework.beam.util.ExceptionUtil;
import com.yzl.framework.beam.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Slf4j
public class BeamApacheHttpClientFactory extends HttpClientFactory {

    private int connectTimeout = 2000;
    private int socketTimeout = 10000;
    private int connectionRequestTimeout = -1;
    private int retryCount = 0;
    private boolean requestSentRetryEnabled = false;
    private int poolMaxTotal = 500;
    private int poolMaxPreRoute = 20;

    private HttpClientConnectionManager connectionManager;
    private CloseableHttpClient httpClient;

    private SerializationFactory serializationFactory;

    public BeamApacheHttpClientFactory() {
    }

    @Override
    public void init() {
        if (this.connectionManager == null) {
            this.connectionManager = buildConnectionManager();
        }
        if (this.serializationFactory == null) {
            this.serializationFactory = new ProtobufSerializationFactory();
        }
        if (this.httpClient == null) {
            this.httpClient = buildHttpClient();
        }
    }

    @Override
    public void destroy() {
        this.connectionManager.closeExpiredConnections();
        this.connectionManager.shutdown();
    }

    @Override
    public HttpClient createHttpClient(URL referUrl, URL serviceUrl) {
        return new HttpClient() {
            @Override
            public Response sendRequest(Request request) {
                long start = System.currentTimeMillis();
                DefaultResponse response = null;
                try {
                    response = sendHttpRequest(referUrl, serviceUrl, request);
                } catch (Exception e) {
                    response = new DefaultResponse(request.getRequestId());
                    response.setException(e);
                } finally {
                    if (response != null) {
                        response.setProcessTime(System.currentTimeMillis() - start);
                    }
                }
                return response;
            }
        };
    }

    protected DefaultResponse sendHttpRequest(URL referUrl, URL serviceUrl, Request request) {
        request.getAttachments().putAll(RpcContext.getContext().getRpcAttachments());
        DefaultResponse response = new DefaultResponse(request.getRequestId());
        CloseableHttpResponse httpResponse = null;
        HttpPost post = null;
        try {
            CloseableHttpClient httpClient = this.getHttpClient();
            //beam协议只做post请求
            post = buildPost(referUrl, serviceUrl, request);
            RpcContext.getContext().putAttribute(AccessMetricsFilter.requestPayloadName, new Long(post.getEntity().getContentLength()));
            try {
                httpResponse = httpClient.execute(post);
            } catch (IOException e) {
                throw new BeamServiceException("Error send http post, requestId=" + request.getRequestId(), e);
            }
            //设置响应头
            setResponseHeaders(httpResponse, response);
            //设置status
            setStatus(httpResponse, response);

            //设置响应body
            setResult(httpResponse, request, response);
        } catch (Exception e) {
            response.setException(e);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                if (post != null) {
                    post.releaseConnection();
                }
            } catch (IOException e) {
                log.warn("Error close httpResponse, requestId={}", request.getRequestId(), e);
            }
        }
        return response;
    }

    protected Exception getResponseException(Request request, String exceptionClass, byte[] content) {
        String errorMessage = new String(content, StandardCharsets.UTF_8);
        if (StringUtils.isBlank(exceptionClass)) {
            return new BeamServiceException(errorMessage);
        }
        if (StringUtils.equals(exceptionClass, BeamBizException.class.getName())) {
            return new BeamBizException(errorMessage);
        } else if (StringUtils.equals(exceptionClass, BeamServiceException.class.getName())) {
            return new BeamServiceException(errorMessage);
        } else if (StringUtils.equals(exceptionClass, BeamFrameworkException.class.getName())) {
            return new BeamFrameworkException(errorMessage);
        } else {
            return new BeamServiceException(exceptionClass + " : " + errorMessage);
        }
    }

    protected Object getResponseValue(Request request, byte[] content) {
        Class<?> returnType = request.getReturnType();
        Serialization serialization = this.buildSerialization(request);
        try {
            return serialization.deserialize(content, returnType);
        } catch (IOException e) {
            throw new BeamServiceException("Deserialize response content error, requestId=" + request.getRequestId(), e);
        }
    }

    protected void setResult(CloseableHttpResponse httpResponse, Request request, DefaultResponse response) {
        HttpEntity entity = httpResponse.getEntity();
        byte[] content;
        try {
            content = IOUtils.toByteArray(entity.getContent());
            RpcContext.getContext().putAttribute(AccessMetricsFilter.responsePayloadName, new Long(content.length));
        } catch (IOException e) {
            throw new BeamServiceException("Error read response content, requestId=" + response.getRequestId(), e);
        }
        //  根据http状态码做不同的处理
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            response.setValue(getResponseValue(request, content));
            response.setCode(BeamMessageConstant.SUCCESS);
        } else if (statusCode >= 300 && statusCode < 400) {
            response.setException(new BeamServiceException(new BeamMessage(statusCode,
                    BeamMessageConstant.SERVICE_REDIRECT_ERROR_CODE, "Http request redirect error.")));
            response.setCode(BeamMessageConstant.SERVICE_REDIRECT_ERROR_CODE);
        } else if (statusCode == 404) {
            response.setException(new BeamServiceException(BeamMessageConstant.SERVICE_UNFOUND));
            response.setCode(BeamMessageConstant.SERVICE_UNFOUND_ERROR_CODE);
        } else if (statusCode >= 400 && statusCode < 600) {//4xx,5xx处理逻辑是一样的
            Header header = httpResponse.getFirstHeader(URLParamType.exceptionClassHeader.getName());
            String exceptionClass = header == null ? null : header.getValue();
            Exception e = getResponseException(request, exceptionClass, content);
            int code = BeamMessageConstant.SERVICE_DEFAULT_ERROR_CODE;
            if (e == null) {
                e = new BeamServiceException(new BeamMessage(statusCode,
                        BeamMessageConstant.SERVICE_UNKNOW_ERROR_CODE, "Unknow error, status code is " + statusCode));
            } else if (ExceptionUtil.isBeamException(e)) {
                code = ((BeamAbstractException) e).getErrorCode();
                code = code == 0 ? BeamMessageConstant.SERVICE_DEFAULT_ERROR_CODE : code;
            }
            response.setException(e);
            response.setCode(code);
        } else {
            response.setException(new BeamServiceException(new BeamMessage(statusCode,
                    BeamMessageConstant.SERVICE_UNKNOW_ERROR_CODE, "Unknow error.")));
            response.setCode(BeamMessageConstant.SERVICE_UNKNOW_ERROR_CODE);
        }
    }

    protected void setStatus(CloseableHttpResponse httpResponse, Response response) {
        StatusLine statusLine = httpResponse.getStatusLine();
        response.setAttachment(URLParamType.httpVersion.name(), statusLine.getProtocolVersion().toString());
        response.setAttachment(URLParamType.httpStatusCode.name(), String.valueOf(statusLine.getStatusCode()));
        response.setAttachment(URLParamType.httpReasonPhrase.name(), statusLine.getReasonPhrase());
    }

    protected void setResponseHeaders(CloseableHttpResponse httpResponse, Response response) {
        Header[] headers = httpResponse.getAllHeaders();
        for (Header header : headers) {
            response.setAttachment(header.getName(), header.getValue());
        }
    }

    protected URI buildUri(Request request, URL serviceUrl) {
        try {
            String path = BeamConstants.PATH_SEPARATOR;
            String basePath = serviceUrl.getParameter(URLParamType.basePath.name(), URLParamType.basePath.getValue());
            if (StringUtils.isNotBlank(basePath)) {
                path = path + StringUtils.removeStart(basePath, BeamConstants.PATH_SEPARATOR) + BeamConstants.PATH_SEPARATOR;
            }
            path = path + serviceUrl.getPath() + BeamConstants.PATH_SEPARATOR + request.getMethodName();
            URIBuilder builder = new URIBuilder();
            return builder.setScheme(MapUtils.getString(serviceUrl.getParameters(), URLParamType.httpSchema.getName(), URLParamType.httpSchema.getValue()))
                    .setHost(serviceUrl.getHost())
                    .setPort(serviceUrl.getPort())
                    .setPath(path)
                    .build();
        } catch (Exception e) {
            throw new BeamFrameworkException("build request uri error.", e);
        }
    }

    protected HttpPost buildPost(URL referUrl, URL serviceUrl, Request request) {
        URI uri = buildUri(request, serviceUrl);
        //TODO 根据referUrl和serviceUrl设置post请求参数
        HttpPost post = buildPost(uri.toString());
        for (Header header : buildHeaders(request)) {
            post.addHeader(header);
        }
        post.setHeader("connection", "Keep-Alive");
        post.setEntity(buildHttpEntity(request));
        return post;
    }

    protected HttpEntity buildHttpEntity(Request request) {
        if (request.getArguments().length == 0) {
            return EntityBuilder.create().build();
        }
        //这里因为是beam协议，请求参数只能是一个protobuf Message
        if (request.getArguments().length > 1) {
            throw new BeamServiceException(String.format("Interface method %s#%s  parameter count must less or equal 1, but has %s :",
                    request.getInterfaceName(),
                    ReflectUtil.getMethodSignature(request.getMethodName(), request.getParameterTypes()),
                    request.getArguments().length));
        }
        try {
            Serialization serialization = this.buildSerialization(request);
            byte[] data = serialization.serialize(request.getArguments()[0]);
            EntityBuilder entityBuilder = EntityBuilder.create().setBinary(data);
            return entityBuilder.build();
        } catch (IOException e) {
            throw new BeamServiceException("Can not serialize request Argument: " + request.getArguments()[0], e);
        }
    }

    protected Serialization buildSerialization(Request request) {
        String serializationParam = getSerializationParam(request);
        return this.getSerializationFactory().newInstance(serializationParam);
    }

    protected String getSerializationParam(Request request) {
        return MapUtils.getString(request.getAttachments(),
                URLParamType.serialization.getName(), URLParamType.serialization.getValue());
    }

    protected List<Header> buildHeaders(Request request) {
        List<Header> headers = new ArrayList<>();
        Map<String, String> attachments = request.getAttachments();
        for (String key : attachments.keySet()) {
            Header header = new BasicHeader(key, attachments.get(key));
            headers.add(header);
        }
        String requestId = String.valueOf(request.getRequestId());
        headers.add(new BasicHeader(URLParamType.requestId.name(), requestId));
        if (request.getParameterTypes() != null) {
            headers.add(new BasicHeader(URLParamType.parameterTypes.name(),
                    StringUtils.join(request.getParameterTypes())));
        }
        if (request.getReturnType() != null) {
            headers.add(new BasicHeader(URLParamType.returnType.name(), request.getReturnType().getName()));
        }
        return headers;
    }

    protected HttpPost buildPost(String url) {
        HttpPost post = new HttpPost(url);
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(this.getConnectTimeout())
                .setSocketTimeout(this.getSocketTimeout())
                .setConnectionRequestTimeout(this.getConnectionRequestTimeout());
        post.setConfig(builder.build());
        return post;
    }

    protected CloseableHttpClient buildHttpClient() {
        RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(this.getRetryCount(), this.isRequestSentRetryEnabled());
        return HttpClients.custom()
                .disableContentCompression()
                .setConnectionManager(this.getConnectionManager())
                .setDefaultRequestConfig(config)
                .setRetryHandler(retryHandler)
                .disableCookieManagement()
                .build();
    }

    protected HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(this.getPoolMaxTotal());
        connectionManager.setDefaultMaxPerRoute(this.getPoolMaxPreRoute());
        return connectionManager;
    }

}
