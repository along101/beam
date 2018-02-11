package com.yzl.framework.beam.rpc;

public interface Protocol {

    String getName();

    <T> Exporter<T> export(Provider<T> provider, URL serviceUrl);

    <T> Refer<T> refer(Class<T> interfaceClass, URL referUrl, URL serviceUrl);

    void destroy();
}
