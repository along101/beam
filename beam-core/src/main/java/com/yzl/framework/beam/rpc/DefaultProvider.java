package com.yzl.framework.beam.rpc;

import com.yzl.framework.beam.common.BeamMessageConstant;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.exception.BeamBizException;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.util.ExceptionUtil;
import com.yzl.framework.beam.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class DefaultProvider<T> extends AbstractProvider<T> {

    public DefaultProvider(Class<T> interfaceClass, T serviceInstance, Map<String, String> parameters) {
        super(interfaceClass, serviceInstance, parameters);
    }

    @Override
    protected Response invoke(Request request) {
        DefaultResponse response = new DefaultResponse();
        Method method = lookupMethod(request.getMethodName(), request.getParameterTypes());
        if (method == null) {
            BeamServiceException exception =
                    new BeamServiceException("Service method not exist: "
                            + request.getInterfaceName()
                            + "#" + ReflectUtil.getMethodSignature(request.getMethodName(), request.getParameterTypes()),
                            BeamMessageConstant.SERVICE_UNFOUND);
            response.setException(exception);
            return response;
        }

        try {
            Object value = method.invoke(this.serviceInstance, request.getArguments());
            response.setValue(value);
        } catch (Exception e) {
            if (e.getCause() != null) {
                response.setException(new BeamBizException("provider call process error", e.getCause()));
            } else {
                response.setException(new BeamBizException("provider call process error", e));
            }
            //服务发生错误时，显示详细日志
            log.error("Exception caught when during method invocation. request:" + request.toString(), e);
        } catch (Throwable t) {
            // 如果服务发生Error，将Error转化为Exception，防止拖垮调用方
            if (t.getCause() != null) {
                response.setException(new BeamServiceException("provider has encountered a fatal error!", t.getCause()));
            } else {
                response.setException(new BeamServiceException("provider has encountered a fatal error!", t));
            }
            //对于Throwable,也记录日志
            log.error("Exception caught when during method invocation. request:" + request.toString(), t);
        }

        if (response.getException() != null) {
            //是否传输业务异常栈
            boolean transExceptionStack = MapUtils.getBoolean(parameters, URLParamType.transExceptionStack.getName(), URLParamType.transExceptionStack.getBooleanValue());
            if (!transExceptionStack) {//不传输业务异常栈
                ExceptionUtil.setMockStackTrace(response.getException().getCause());
            }
        }
        return response;
    }

}
