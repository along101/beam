package com.yzl.framework.beam.cluster;

import com.yzl.framework.beam.cluster.loadbalance.RandomLoadBalance;
import com.yzl.framework.beam.cluster.support.DefaultLoadBalanceFactory;
import org.junit.Assert;
import org.junit.Test;

public class DefaultLoadBalanceFactoryTest {

    @Test
    public void testFactory() {
        LoadBalance<Object> loadbalance = new DefaultLoadBalanceFactory<>().newInstance("random");
        Assert.assertTrue(loadbalance instanceof RandomLoadBalance);
    }
}
