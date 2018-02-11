package com.yzl.framework.beam.registry.support;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.registry.AbstractRegistry;
import com.yzl.framework.beam.registry.NotifyListener;
import com.yzl.framework.beam.rpc.URL;
import com.yzl.framework.beam.util.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class LocalRegistry extends AbstractRegistry {

    final private ConcurrentMap<String, Set<URL>> registeredServices = new ConcurrentHashMap<>();
    final private ConcurrentMap<String, Set<URL>> registeredRefers = new ConcurrentHashMap<>();

    private ConcurrentHashMap<URL, NotifyListener> subscribeListeners = new ConcurrentHashMap<>();

    public LocalRegistry() {
        super(URL.builder()
                .protocol(BeamConstants.REGISTRY_PROTOCOL_LOCAL)
                .host(NetUtils.LOCALHOST).port(BeamConstants.DEFAULT_INT_VALUE).path(BeamConstants.PATH_SEPARATOR)
                .build());
        this.registryUrl.addParameter(URLParamType.nodeType.getName(), BeamConstants.NODE_TYPE_REGISTRY);
    }

    @Override
    public void doSubscribe(URL referUrl, NotifyListener listener) {
        this.registeredRefers.putIfAbsent(getRegistryKey(referUrl), Collections.synchronizedSet(new HashSet<>()));
        this.registeredRefers.get(getRegistryKey(referUrl)).add(referUrl);

        this.subscribeListeners.putIfAbsent(referUrl, listener);

        List<URL> serviceUrls = discover(referUrl);
        if (!CollectionUtils.isEmpty(serviceUrls)) {
            listener.notify(getRegistryUrl(), serviceUrls);
        }

        log.info("LocalRegistry subscribe: referUrl={}", referUrl.getIdentity());
    }

    @Override
    public void doUnsubscribe(URL referUrl, NotifyListener listener) {
        this.subscribeListeners.remove(referUrl);
        log.info("LocalRegistry unsubscribe: serviceUrl={}", referUrl.getIdentity());
    }

    @Override
    public List<URL> doDiscover(URL referUrl) {
        return new ArrayList<>(registeredServices.get(getRegistryKey(referUrl)));
    }

    @Override
    public void doRegister(URL serviceUrl) {
        String registryKey = getRegistryKey(serviceUrl);
        this.registeredServices.putIfAbsent(registryKey, Collections.synchronizedSet(new HashSet<>()));
        this.registeredServices.get(registryKey).add(serviceUrl);

        log.info("LocalRegistry register: serviceUrl={}", serviceUrl);

        notifyListeners(serviceUrl);
    }

    @Override
    public void doUnregister(URL serviceUrl) {
        Set<URL> serviceUrls = this.registeredServices.get(getRegistryKey(serviceUrl));
        if (serviceUrls == null) {
            return;
        }
        serviceUrls.remove(serviceUrl);

        log.info("LocalRegistry unregister: serviceUrl={}", serviceUrl.getIdentity());

        notifyListeners(serviceUrl);
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    private Map<String, Set<URL>> getCopyUrls(ConcurrentMap<String, Set<URL>> urls) {
        Map<String, Set<URL>> copyMap = new HashMap<>(urls.size());
        for (Map.Entry<String, Set<URL>> entry : urls.entrySet()) {
            String key = entry.getKey();
            Set<URL> copySet = Collections.synchronizedSet(new HashSet<>(entry.getValue().size()));
            for (URL url : entry.getValue()) {
                copySet.add(url.createCopy());
            }
            copyMap.put(key, copySet);
        }
        return copyMap;
    }

    private void notifyListeners(URL serviceUrl) {
        List<URL> discoverUrls = discover(serviceUrl);

        for (Map.Entry<URL, NotifyListener> entry : subscribeListeners.entrySet()) {
            URL referUrl = entry.getKey();
            NotifyListener notifyListener = entry.getValue();
            if (StringUtils.equals(getRegistryKey(serviceUrl), getRegistryKey(referUrl))) {
                try {
                    notifyListener.notify(getRegistryUrl(), discoverUrls);
                } catch (Exception e) {
                    log.warn(String.format("Exception when notify listerner %s, changedUrl: %s", referUrl.getIdentity(), serviceUrl.getIdentity()), e);
                }
            }
        }
    }

    private String getRegistryKey(URL url) {
        String key = url.getPath();
        if (StringUtils.isNotBlank(url.getVersion())) {
            key += "?version=" + url.getVersion();
        }
        return key;
    }


    public Map<String, Set<URL>> getCopyRegisteredRefers() {
        return getCopyUrls(this.registeredRefers);
    }

    public Map<String, Set<URL>> getCopyRegisteredServices() {
        return getCopyUrls(this.registeredServices);
    }
}
