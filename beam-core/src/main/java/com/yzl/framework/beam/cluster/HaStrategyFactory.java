package com.yzl.framework.beam.cluster;

public interface HaStrategyFactory<T> {

    HaStrategy<T> newInstance(String name);
}
