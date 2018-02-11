package com.yzl.framework.beam.transport;

import com.codahale.metrics.Histogram;
import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.filter.AccessMetricsFilter;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.serialize.ProtobufSerializationFactory;
import com.yzl.framework.beam.serialize.Serialization;
import com.yzl.framework.beam.serialize.SerializationFactory;
import com.yzl.framework.beam.util.MathUtil;
import com.yzl.framework.beam.util.ReflectUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ServletEndpoint extends AbstractServletEndpoint {

    @Setter
    protected SerializationFactory serializationFactory;

    public ServletEndpoint(URL baseUrl) {
        super(baseUrl);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        if (this.serializationFactory == null)
            this.serializationFactory = new ProtobufSerializationFactory();
    }

    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String key = getProviderKey(getInterfaceName(httpRequest), getVersion(httpRequest));
        Provider<?> provider = this.providers.get(key);
        Request request = convert(httpRequest);
        if (provider == null) {
            transportException(new BeamServiceException("Can not find provider by key: " + key), request, httpResponse);
            return;
        }
        Response response;
        try {
            response = provider.call(request);
            transportResponse(request, response, httpRequest, httpResponse);
        } catch (Exception e) {
            transportException(e, request, httpResponse);
            log.error("Request error, requestId={}", request, e);
        }
    }

    protected Request convert(HttpServletRequest httpRequest) {
        DefaultRequest request = new DefaultRequest();
        request.setRequestId(MathUtil.parseLong(httpRequest.getHeader(URLParamType.requestId.name()), 0L));
        request.setInterfaceName(getInterfaceName(httpRequest));
        request.setMethodName(getMethodName(httpRequest));
        request.setParameterTypes(getParameterTypes(httpRequest));
        request.setReturnType(getReturnType(httpRequest));
        request.setAttachments(getAttachments(httpRequest));

        RpcContext.getContext().setRequest(request);
        Provider<?> provider = this.providers.get(getProviderKey(request));
        if (provider != null) {
            request.setArguments(getRequestArguments(httpRequest, request, provider));
        }
        return request;
    }

    protected String getInterfaceName(HttpServletRequest httpRequest) {
        String path = StringUtils.substringBeforeLast(httpRequest.getPathInfo(), BeamConstants.PATH_SEPARATOR);
        return StringUtils.substringAfterLast(path, BeamConstants.PATH_SEPARATOR);
    }

    protected String getMethodName(HttpServletRequest httpRequest) {
        return StringUtils.substringAfterLast(httpRequest.getPathInfo(), BeamConstants.PATH_SEPARATOR);
    }

    protected String[] getParameterTypes(HttpServletRequest httpRequest) {
        String parameterTypes = httpRequest.getHeader(URLParamType.parameterTypes.name());
        if (parameterTypes != null) {
            return parameterTypes.split(BeamConstants.SEPERATOR_ARRAY);
        }
        return null;
    }

    protected Map<String, String> getAttachments(HttpServletRequest httpRequest) {
        Map<String, String> attachments = new HashMap<>();
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headName);
            attachments.put(headName, headerValue);
        }
        attachments.put(URLParamType.clientHost.getName(), httpRequest.getRemoteAddr());
        return attachments;
    }

    protected Object[] getRequestArguments(HttpServletRequest httpRequest, Request request, Provider<?> provider) {
        Method method = provider.lookupMethod(request.getMethodName(), request.getParameterTypes());
        if (method == null) {
            throw new BeamServiceException(String.format("Can not find method %s#%s", request.getInterfaceName(), request.getMethodName()));
        }
        if (method.getParameterCount() == 0) {
            //Request Payload 埋点 TODO 改到过滤器中
            metricRequestPayload(0);
            return new Object[0];
        } else if (method.getParameterCount() == 1) {
            Serialization serialization = this.getSerialization(httpRequest);
            try {
                byte[] data = IOUtils.toByteArray(httpRequest.getInputStream());
                //Request Payload 埋点 TODO 改到过滤器中
                metricRequestPayload(data.length);
                Object argument = serialization.deserialize(data, method.getParameterTypes()[0]);
                return new Object[]{argument};
            } catch (IOException e) {
                throw new BeamServiceException("Deserialize request parameter error.", e);
            }
        } else {
            throw new BeamServiceException(String.format("Interface method %s#%s parameter count must less or equal 1.", request.getInterfaceName(), method.getName()));
        }
    }

    protected Serialization getSerialization(HttpServletRequest httpRequest) {
        return this.serializationFactory.newInstance(httpRequest.getHeader(URLParamType.serialization.getName()));
    }

    protected Class<?> getReturnType(HttpServletRequest httpRequest) {
        String returnType = httpRequest.getHeader(URLParamType.returnType.name());
        if (returnType != null) {
            try {
                return ReflectUtil.forName(returnType);
            } catch (ClassNotFoundException e) {
                throw new BeamServiceException("Can not find returnType " + returnType, e);
            }
        }
        return null;
    }

    protected String getVersion(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(URLParamType.version.name());
    }

    protected String getProviderKey(Request request) {
        return getProviderKey(request.getInterfaceName(), MapUtils.getString(request.getAttachments(), URLParamType.version.name()));
    }

    protected void transportResponse(Request request, Response response, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        httpResponse.setStatus(BeamConstants.HTTP_OK);
        try (OutputStream out = httpResponse.getOutputStream()) {
            Serialization serialization = this.getSerialization(httpRequest);
            byte[] data = serialization.serialize(response.getValue());
            out.write(data);
            out.flush();
            //response payload 埋点 TODO 改到过滤器中
            metricResponsePayload(data.length);
        } catch (IOException e) {
            log.error("write response error.request: {}", request, e);
        }
    }

    protected void transportException(Exception e, Request request, HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(BeamConstants.HTTP_EXPECTATION_FAILED);
        httpServletResponse.setHeader(URLParamType.exceptionClassHeader.getName(), e.getClass().getName());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStream out = httpServletResponse.getOutputStream()) {
            e.printStackTrace(new PrintStream(baos));
            byte[] data = baos.toByteArray();
            out.write(data);
            out.flush();
            //response payload 埋点 TODO 改到过滤器中
            metricResponsePayload(data.length);
        } catch (Exception e1) {
            log.error("write response error.request: {}", request, e);
        }
    }

    /**
     * 由于过滤器位置的问题，服务端metricFilter拿不到request和response的payload，需要在这里通过Context传递
     * TODO 修改过滤器结构，去掉此处的埋点，统一在过滤器中埋点
     *
     * @param size
     */
    protected void metricRequestPayload(long size) {
        RpcContext.getContext().putAttribute(AccessMetricsFilter.requestPayloadName, size);
    }

    protected void metricResponsePayload(long size) {
        Histogram histogram = (Histogram) RpcContext.getContext().getAttribute(AccessMetricsFilter.responseHistogram);
        if (histogram != null) {
            histogram.update(size);
        }
    }

}
