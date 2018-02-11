package com.yzl.framework.beam.rpc;

import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.util.BeamFrameworkUtil;

public abstract class AbstractRefer<T> implements Refer<T> {

    private Class<T> interfaceClass;
    protected URL referUrl;
    protected URL serviceUrl;

    public AbstractRefer(Class<T> interfaceClass, URL referUrl, URL serviceUrl) {
        this.interfaceClass = interfaceClass;
        this.referUrl = referUrl;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Response call(Request request) {
        if (!isAvailable()) {
            throw new BeamFrameworkException(this.getClass().getSimpleName() + " call Error: node is not available, serviceUrl=" + serviceUrl.getUri()
                    + " " + BeamFrameworkUtil.toString(request));
        }
        return doCall(request);
    }

    protected abstract Response doCall(Request request);

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public URL getReferUrl() {
        return this.referUrl;
    }

    @Override
    public URL getServiceUrl() {
        return this.serviceUrl;
    }

    @Override
    public Class<T> getInterface() {
        return this.interfaceClass;
    }
}
