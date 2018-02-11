package com.yzl.framework.beam.spring.test.client;

import com.yzl.framework.beam.annotation.BeamRouteClient;
import com.yzl.framework.beam.proto.Simple;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试只接入客户端
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class)
public class ClientTest {

    @BeamRouteClient
    private Simple simple;

    @Test
    public void testSayHello() {
        System.out.println(simple);
        Assert.assertNotNull(simple);
    }
}
