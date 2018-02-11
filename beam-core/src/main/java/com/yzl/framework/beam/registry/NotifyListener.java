package com.yzl.framework.beam.registry;


import com.yzl.framework.beam.rpc.URL;

import java.util.List;

public interface NotifyListener {

    void notify(URL registryUrl, List<URL> serviceUrls);
}
