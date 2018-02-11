package com.yzl.framework.beam.protocol;

import com.yzl.framework.beam.rpc.*;

public abstract class AbstractProtocol implements Protocol {

    @Override
    public <T> Exporter<T> export(Provider<T> provider, URL serviceUrl) {
        serviceUrl.setProtocol(this.getProtocolName());
        URL newServiceUrl = this.deploy(provider, serviceUrl);
        Exporter<T> exporter = new DefaultExporter<>(provider, newServiceUrl);
        provider.setServiceUrl(newServiceUrl);
        return exporter;
    }

    protected abstract <T> URL deploy(Provider<T> provider, URL serviceUrl);

    protected abstract String getProtocolName();

    @Override
    public void destroy() {

    }
}
