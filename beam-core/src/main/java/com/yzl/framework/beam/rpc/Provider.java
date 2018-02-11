package com.yzl.framework.beam.rpc;

import java.lang.reflect.Method;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public interface Provider<T> extends Caller {

    Method lookupMethod(String methodName, String[] parameterTypes);

    Class<T> getInterface();

    T getImpl();

    URL getServiceUrl();

    void setServiceUrl(URL serviceUrl);

    void destroy();

    void init();

}