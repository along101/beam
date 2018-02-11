package com.yzl.framework.beam.spring.test.urlclient;

import com.yzl.framework.beam.annotation.BeamUrlClient;
import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.spring.annotation.BeamUrlClientConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试只接入客户端
 */
@BeamUrlClientConfig(clazz = Simple.class, urlKey = "beam.url.Simple")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ClientApplication.class,
        properties = {"beam.url.Simple=http://localhost:8080/beam/com.yzl.framework.beam.proto.Simple",
                "beam.url.com.yzl.framework.beam.proto.Simple=http://localhost:8080/beam/com.yzl.framework.beam.proto.Simple",
                "Simple=http://localhost:8080/beam/com.yzl.framework.beam.proto.Simple"})
public class ClientTest {

    @BeamUrlClient
    private Simple simple1;

    @BeamUrlClient(urlKey = "beam.url.com.yzl.framework.beam.proto.Simple")
    private Simple simple2;

    @BeamUrlClient(urlKey = "Simple")
    private Simple simple3;

    @BeamUrlClient(urlKey = "http://localhost:8080/beam/com.yzl.framework.beam.proto.Simple")
    private Simple simple4;

    @Test
    public void testInject() {
        Assert.assertNotNull(simple1);
        Assert.assertNotNull(simple2);
        Assert.assertNotNull(simple3);
        Assert.assertNotNull(simple4);

        Assert.assertEquals(simple1, simple2);
    }
}
