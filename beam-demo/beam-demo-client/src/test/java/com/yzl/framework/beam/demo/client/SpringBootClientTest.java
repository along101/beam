package com.yzl.framework.beam.demo.client;

import com.yzl.framework.beam.annotation.BeamRouteClient;
import com.yzl.framework.beam.annotation.BeamUrlClient;
import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.proto.Simple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootClient.class,
        properties = {"beam.url.com.yzl.framework.beam.proto.Simple=http://localhost:8080/beam/com.yzl.framework.beam.proto.Simple"})
public class SpringBootClientTest {

    @BeamRouteClient
    private Simple routeSimple;

    @BeamUrlClient
    private Simple urlSimple;

    @Test
    public void testRouteSayHello() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName("yzl").build();
        for (int i = 0; i < 10; i++) {
            System.out.println(routeSimple.sayHello(helloRequest));
        }
    }

    @Test
    public void testUrlSayHello() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName("yzl").build();
        for (int i = 0; i < 10; i++) {
            System.out.println(urlSimple.sayHello(helloRequest));
        }
    }
}
