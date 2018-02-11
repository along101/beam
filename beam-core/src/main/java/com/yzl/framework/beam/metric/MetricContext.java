package com.yzl.framework.beam.metric;

import com.codahale.metrics.MetricRegistry;

public class MetricContext {

    private final static MetricRegistry metricRegistry = new MetricRegistry();

    public static MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }
}
