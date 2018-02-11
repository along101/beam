package com.yzl.framework.beam.rpc;

import lombok.Getter;

@Getter
public class DefaultExporter<T> implements Exporter<T> {

    private URL serviceUrl;

    private Provider<T> provider;

    public DefaultExporter(Provider<T> provider, URL serviceUrl) {
        this.provider = provider;
        this.serviceUrl = serviceUrl;
    }

}
