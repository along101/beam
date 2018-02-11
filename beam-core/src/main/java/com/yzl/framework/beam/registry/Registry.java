package com.yzl.framework.beam.registry;

import com.yzl.framework.beam.rpc.URL;

import java.util.Collection;
import java.util.List;

/**
 * Created by yinzuolong on 2017/12/7.
 */
public interface Registry {

    URL getRegistryUrl();

    void subscribe(URL referUrl, NotifyListener listener);

    void unsubscribe(URL referUrl, NotifyListener listener);

    List<URL> discover(URL referUrl);

    void register(URL serviceUrl);

    void unregister(URL serviceUrl);

    Collection<URL> getRegisteredServiceUrls();

    void init();

    void destroy();
}
