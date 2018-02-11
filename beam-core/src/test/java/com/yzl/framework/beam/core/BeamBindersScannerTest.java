package com.yzl.framework.beam.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/6.
 */
public class BeamBindersScannerTest {
    @Test
    public void testScan() {
        Map<Class<?>, List<Class<?>>> map = SpiBindersScanner.scanBeamBinders();
        System.out.println(map);
        Assert.assertEquals(2, map.get(TestInterface.class).size());
    }
}
