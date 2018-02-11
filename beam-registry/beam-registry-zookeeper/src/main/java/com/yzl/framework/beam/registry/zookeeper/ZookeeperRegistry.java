package com.yzl.framework.beam.registry.zookeeper;

import com.yzl.framework.beam.registry.AbstractRegistry;
import com.yzl.framework.beam.registry.NotifyListener;
import com.yzl.framework.beam.rpc.URL;

import java.util.List;

public class ZookeeperRegistry extends AbstractRegistry {

    public ZookeeperRegistry(URL registryUrl) {
        super(registryUrl);
    }

    @Override
    protected void doRegister(URL serviceUrl) {

    }

    @Override
    protected void doUnregister(URL serviceUrl) {

    }

    @Override
    protected void doSubscribe(URL referUrl, NotifyListener listener) {

    }

    @Override
    protected void doUnsubscribe(URL referUrl, NotifyListener listener) {

    }

    @Override
    protected List<URL> doDiscover(URL referUrl) {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
