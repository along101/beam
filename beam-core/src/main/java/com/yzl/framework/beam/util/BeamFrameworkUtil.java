package com.yzl.framework.beam.util;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.rpc.DefaultResponse;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;
import com.yzl.framework.beam.rpc.URL;

public class BeamFrameworkUtil {

    /**
     * 目前根据 group/interface/version 来唯一标示一个服务
     *
     * @param request
     * @return
     */
    public static String getServiceKey(Request request) {
        String version = getVersionFromRequest(request);
        return getServiceKey(request.getInterfaceName(), version);
    }

    public static String getVersionFromRequest(Request request) {
        return getValueFromRequest(request, URLParamType.version.name(), URLParamType.version.getValue());
    }

    public static String getValueFromRequest(Request request, String key, String defaultValue) {
        String value = defaultValue;
        if (request.getAttachments() != null && request.getAttachments().containsKey(key)) {
            value = request.getAttachments().get(key);
        }
        return value;
    }

    /**
     * 目前根据 interface/version 来唯一标示一个服务
     *
     * @param url
     * @return
     */
    public static String getServiceKey(URL url) {
        return getServiceKey(url.getPath(), url.getVersion());
    }

    /**
     * protocol key: protocol://host:port/interface/version
     *
     * @param url
     * @return
     */
    public static String getProtocolKey(URL url) {
        return url.getProtocol() + BeamConstants.PROTOCOL_SEPARATOR + url.getServerPortStr()
                + BeamConstants.PATH_SEPARATOR + url.getPath()
                + BeamConstants.PATH_SEPARATOR + url.getVersion();
    }

    /**
     * 输出请求的关键信息： requestId=** interface=** method=**(**)
     *
     * @param request
     * @return
     */
    public static String toString(Request request) {
        return "requestId=" + request.getRequestId()
                + " interface=" + request.getInterfaceName()
                + " method=" + ReflectUtil.getMethodSignature(request.getMethodName(), request.getParameterTypes());
    }

    /**
     * serviceKey: interface/version
     *
     * @param interfaceName
     * @param version
     * @return
     */
    private static String getServiceKey(String interfaceName, String version) {
        return interfaceName + BeamConstants.PATH_SEPARATOR + version;
    }

    public static Response buildErrorResponse(long requestId, Exception e) {
        DefaultResponse response = new DefaultResponse(requestId);
        response.setException(e);
        return response;
    }
}
