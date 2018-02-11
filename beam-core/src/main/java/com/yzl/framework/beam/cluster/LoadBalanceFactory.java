package com.yzl.framework.beam.cluster;

public interface LoadBalanceFactory<T> {

    LoadBalance<T> newInstance(String name);
}
