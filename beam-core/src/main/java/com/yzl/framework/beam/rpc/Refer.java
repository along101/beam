package com.yzl.framework.beam.rpc;

public interface Refer<T> extends Caller {

    Class<T> getInterface();

    boolean isAvailable();

    URL getReferUrl();

    URL getServiceUrl();

    void init();

    void destroy();
}
