package com.yzl.framework.beam.cluster.support;

import com.yzl.framework.beam.cluster.Cluster;
import com.yzl.framework.beam.cluster.ClusterFactory;

public class DefaultClusterFactory implements ClusterFactory {
    @Override
    public Cluster newInstance(String haStrategy, String loadBalance) {
        return null;
    }
}
