package com.yzl.framework.beam.proxy;

import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.rpc.DefaultRequest;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.RpcContext;
import com.yzl.framework.beam.util.BeamFrameworkUtil;
import com.yzl.framework.beam.util.ExceptionUtil;
import com.yzl.framework.beam.util.ReflectUtil;
import com.yzl.framework.beam.util.RequestIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public abstract class AbstractInvocationHandler<T> implements InvocationHandler {

    protected Class<T> interfaceClass;

    public AbstractInvocationHandler(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isLocalMethod(method)) {
            if ("toString".equals(method.getName())) {
                return proxyToString();
            }
            if ("equals".equals(method.getName())) {
                return proxyEquals(args[0]);
            }
            throw new BeamServiceException("can not invoke local method:" + method.getName());
        }

        DefaultRequest request = new DefaultRequest();
        request.setRequestId(RequestIdGenerator.getRequestId());
        request.setInterfaceName(this.interfaceClass.getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(ReflectUtil.getMethodParameterTypes(method));
        request.setArguments(args);
        request.setReturnType(method.getReturnType());

        RpcContext rpcContext = RpcContext.getContext();
        rpcContext.setRequest(request);
        try {
            return doInvoke(request);
        } catch (RuntimeException e) {
            if (ExceptionUtil.isBizException(e)) {
                Throwable t = e.getCause();
                // 只抛出Exception，防止抛出远程的Error
                if (t != null && t instanceof Exception) {
                    throw t;
                } else {
                    String msg = t == null
                            ? "Biz exception cause is null. origin error message : " + e.getMessage()
                            : ("Biz exception cause is throwable error:" + t.getClass() + ", error message:" + t.getMessage());
                    throw new BeamServiceException(msg);
                }
            } else {
                log.error("InvocationHandler invoke Error: interface={}, request={}", request.getInterfaceName(), BeamFrameworkUtil.toString(request), e);
                throw e;
            }
        }
    }

    protected boolean isLocalMethod(Method method) {
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                interfaceClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return false;
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    public abstract Object doInvoke(Request request);

    public abstract String proxyToString();

    public abstract boolean proxyEquals(Object o);

}
