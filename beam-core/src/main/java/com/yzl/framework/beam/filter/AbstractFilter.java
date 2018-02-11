package com.yzl.framework.beam.filter;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.BeamMessageConstant;
import com.yzl.framework.beam.common.Info;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.util.NetUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractFilter implements Filter {

    protected URL getCallerUrl(Caller caller) {
        if (caller instanceof Provider) {
            return ((Provider<?>) caller).getServiceUrl();
        } else if (caller instanceof Refer) {
            return ((Refer<?>) caller).getReferUrl();
        }
        throw new BeamServiceException("Caller must be Provider or Refer, but is " + caller.getClass());
    }

    protected boolean isServer(Caller caller) {
        return caller instanceof Provider;
    }

    protected String getNodeType(Caller caller) {
        return caller instanceof Provider ? BeamConstants.NODE_TYPE_SERVICE : BeamConstants.NODE_TYPE_REFER;
    }

    protected String getClientHost(Caller caller, Request request) {
        return (caller instanceof Provider)
                ? request.getAttachments().get(URLParamType.clientHost.getName())
                : NetUtils.getLocalIp();
    }

    protected String getServerHost(Caller caller) {
        return (caller instanceof Provider)
                ? ((Provider<?>) caller).getServiceUrl().getServerPortStr()
                : ((Refer<?>) caller).getServiceUrl().getServerPortStr();
    }

    protected String getParameterTypes(Request request) {
        return StringUtils.join(request.getParameterTypes());
    }

    protected String getInterfaceVersion(Caller caller) {
        URL url = getCallerUrl(caller);
        return url.getVersion();
    }

    protected String getAppId() {
        return Info.getInstance().getAppId();
    }

    protected String getBeamVersion(Caller caller) {
        return Info.getInstance().getVersion();
    }

    protected String getProtocol(Caller caller) {
        URL url = getCallerUrl(caller);
        return url.getProtocol();
    }

    protected Long getRequestPayloadSize(Request request) {
        return (Long) RpcContext.getContext().getAttribute(AccessMetricsFilter.requestPayloadName);
    }

    protected Long getResponsePayloadSize(Response response) {
        return (Long) RpcContext.getContext().getAttribute(AccessMetricsFilter.responsePayloadName);
    }

    protected String getStatusCode(Caller caller, Response response) {
        if (caller instanceof Provider) {
            if (response == null) {
                return String.valueOf(BeamMessageConstant.SERVICE_DEFAULT_ERROR_CODE);
            } else if (response.getException() != null) {
                return String.valueOf(BeamMessageConstant.BIZ_DEFAULT_ERROR_CODE);
            } else {
                return String.valueOf(BeamMessageConstant.SUCCESS);
            }
        }
        return String.valueOf(response == null ? "" : response.getCode());
    }

    protected String getProcessTime(Caller caller, Response response, long requestTime) {
        return (caller instanceof Provider) ? String.valueOf(requestTime)
                : String.valueOf(response.getProcessTime());
    }

    protected String getRetries(Request request) {
        return String.valueOf(request.getRetries());
    }
}
