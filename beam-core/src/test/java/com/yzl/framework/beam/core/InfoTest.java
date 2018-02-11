package com.yzl.framework.beam.core;

import com.yzl.framework.beam.common.Info;
import org.junit.Assert;
import org.junit.Test;

public class InfoTest {
    @Test
    public void testInfo() {
        Info info = Info.getInstance();
        System.out.println(info.getVersion());
        Assert.assertNotNull(info.getVersion());
    }
}
