package com.yzl.framework.beam.cluster.support;

import com.yzl.framework.beam.cluster.Cluster;
import com.yzl.framework.beam.registry.NotifyListener;
import com.yzl.framework.beam.rpc.URL;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 注册中心监听器实现，
 */
@Slf4j
@Getter
public class ClusterListener implements NotifyListener {

    private Cluster cluster;

    public ClusterListener(Cluster cluster) {
        this.cluster = cluster;
    }

    public void init() {
    }

    @Override
    public synchronized void notify(URL registryUrl, List<URL> serviceUrls) {
        if (CollectionUtils.isEmpty(serviceUrls)) {
            log.warn("ClusterListener config change notify, urls is empty: registry={} service={}", registryUrl.getUri(),
                    cluster.getReferUrl().getIdentity());
        }
        log.info("ClusterListener config change notify: registry={} service={} serviceUrls={}", registryUrl.getUri(),
                cluster.getReferUrl().getIdentity(), getIdentities(serviceUrls));

        cluster.onRefresh(serviceUrls);
    }


    private String getIdentities(List<URL> urls) {
        if (urls == null || urls.isEmpty()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (URL url : urls) {
            builder.append(url.getIdentity()).append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }

}
