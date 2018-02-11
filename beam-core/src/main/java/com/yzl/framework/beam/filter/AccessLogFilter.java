package com.yzl.framework.beam.filter;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.rpc.Caller;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Access log filter
 *
 * 统计整个call的执行状况，最后执行.
 * 此filter会对性能产生一定影响，请求量较大时建议关闭。
 *
 * </pre>
 */
@Slf4j
@SpiBinder(name = "log")
public class AccessLogFilter extends AbstractFilter {

    @Override
    public boolean defaultEnable() {
        return true;
    }

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public Response filter(Caller caller, Request request) {
        boolean needLog = getCallerUrl(caller).getBooleanParameter(URLParamType.accessLog.getName(), URLParamType.accessLog.getBooleanValue());
        if (needLog) {
            long t = System.currentTimeMillis();
            Response response = null;
            try {
                response = caller.call(request);
                return response;
            } finally {
                long requestTime = System.currentTimeMillis() - t;
                logAccess(caller, request, response, requestTime);
            }
        } else {
            return caller.call(request);
        }
    }

    private void logAccess(Caller caller, Request request, Response response, long requestTime) {
        StringBuilder builder = new StringBuilder(128);
        //side 客户端/服务端
        append(builder, getNodeType(caller));
        //client host
        append(builder, getClientHost(caller, request));
        //server host
        append(builder, getServerHost(caller));
        //interface
        append(builder, request.getInterfaceName());
        //method
        append(builder, request.getMethodName());
        //parameterTypes
        append(builder, getParameterTypes(request));
        //version
        append(builder, getInterfaceVersion(caller));
        //beamVersion
        append(builder, getBeamVersion(caller));
        //protocol
        append(builder, getProtocol(caller));
        //appId
        append(builder, getAppId());
        //requestId
        append(builder, request.getRequestId());
        //request payload size
        append(builder, getRequestPayloadSize(request));
        //response payload size
        append(builder, getResponsePayloadSize(response));
        //status code
        append(builder, getStatusCode(caller, response));
        //requestTime
        append(builder, String.valueOf(requestTime));
        //processTime
        append(builder, getProcessTime(caller, response, requestTime));
        //retry count
        append(builder, getRetries(request));

        log.info(builder.substring(0, builder.length() - 1));
    }

    private void append(StringBuilder builder, Object field) {
        if (field != null) {
            builder.append(field.toString());
        }
        builder.append(BeamConstants.SEPERATOR_ACCESS_LOG);
    }

}
