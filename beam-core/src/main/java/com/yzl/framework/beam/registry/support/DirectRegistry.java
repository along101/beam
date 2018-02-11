package com.yzl.framework.beam.registry.support;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.registry.AbstractRegistry;
import com.yzl.framework.beam.registry.NotifyListener;
import com.yzl.framework.beam.rpc.URL;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 直连注册中心，服务发现url的host/port为该注册中心directUrls的host/port
 */
public class DirectRegistry extends AbstractRegistry {

    private Set<URL> subscribeUrls = Collections.synchronizedSet(new HashSet<>());
    private List<URL> directUrls;

    public DirectRegistry(List<URL> directUrls) {
        super(null);
        this.directUrls = directUrls;
        init();
    }

    @Override
    public void init() {
        this.directUrls = new ArrayList<>(directUrls);
        for (URL directUrl : directUrls) {
            directUrl.setProtocol("direct");
        }
        this.registryUrl = directUrls.get(0);
    }

    @Override
    public void destroy() {

    }

    @Override
    protected void doRegister(URL serviceUrl) {
        // do nothing
    }

    @Override
    protected void doUnregister(URL serviceUrl) {
        // do nothing
    }

    @Override
    protected void doSubscribe(URL referUrl, NotifyListener listener) {
        subscribeUrls.add(referUrl);
        listener.notify(this.getRegistryUrl(), doDiscover(referUrl));
    }

    @Override
    protected void doUnsubscribe(URL referUrl, NotifyListener listener) {
        subscribeUrls.remove(referUrl);
    }

    @Override
    protected List<URL> doDiscover(URL referUrl) {
        List<URL> serviceUrls = new ArrayList<>();
        for (URL directUrl : directUrls) {
            URL serviceUrl = directUrl.createCopy();
            serviceUrl.setProtocol(referUrl.getProtocol());
            serviceUrl.setPath(referUrl.getPath());
            serviceUrl.addParameter(URLParamType.nodeType.name(), BeamConstants.NODE_TYPE_SERVICE);
            String basePath = BeamConstants.PATH_SEPARATOR + StringUtils.removeStart(directUrl.getPath(), BeamConstants.PATH_SEPARATOR);
            serviceUrl.addParameterIfAbsent(URLParamType.basePath.name(), basePath);
            serviceUrls.add(serviceUrl);
        }
        return serviceUrls;
    }

}
