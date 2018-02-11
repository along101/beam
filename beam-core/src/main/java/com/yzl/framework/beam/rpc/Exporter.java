package com.yzl.framework.beam.rpc;

public interface Exporter<T> {

    URL getServiceUrl();

    Provider<T> getProvider();

}
