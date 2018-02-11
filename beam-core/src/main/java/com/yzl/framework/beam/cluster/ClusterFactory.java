package com.yzl.framework.beam.cluster;

/**
 * Created by liujingyu on 2017/12/14.
 */
public interface ClusterFactory {
     Cluster newInstance(String haStrategy, String loadBalance);
}
