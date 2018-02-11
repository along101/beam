package com.yzl.framework.beam.cluster;


import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.URL;

import java.util.List;

public interface Cluster<T> {

    Class<T> getInterfaceClass();

    void init();

    void destroy();

    void onRefresh(List<URL> serviceUrls);

    List<Refer<T>> getRefers();

    URL getReferUrl();

}
