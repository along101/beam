package com.yzl.framework.beam.core;

import org.junit.Assert;
import org.junit.Test;

public class TestBinderDefines {

    @Test
    public void testBinderDefines() {
        BinderDefine<TestInterface> binderDefine = BinderDefines.getInstance().getBinderDefine(TestInterface.class, "test2");
        Assert.assertEquals(TestImpl2.class, binderDefine.getBinderClass());
    }
}
