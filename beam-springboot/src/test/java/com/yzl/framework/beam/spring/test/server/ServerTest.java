package com.yzl.framework.beam.spring.test.server;

import com.yzl.framework.beam.annotation.BeamService;
import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.proto.Simple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试只接入服务端
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class)
public class ServerTest {

    @BeamService
    private Simple simple;

    @Test
    public void testServer() {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName("yzl").build();
        System.out.println(simple.sayHello(helloRequest));
    }


}
