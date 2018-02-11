package com.yzl.framework.beam.demo.client;

import com.yzl.framework.beam.annotation.BeamRouteClient;
import com.yzl.framework.beam.annotation.BeamUrlClient;
import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.proto.Simple;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class SayHelloController {

    @BeamRouteClient
    private Simple routeSimple;

    @BeamUrlClient
    private Simple urlSimple;

    @RequestMapping("/route/sayhello")
    public Object routeSayHello(@RequestParam("name") String name) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName(name).build();
        Helloworld.HelloReply helloReply = routeSimple.sayHello(helloRequest);
        return helloReply != null ? helloReply.toString() : "null";
    }

    @RequestMapping("/url/sayhello")
    public Object urlSayHello(@RequestParam("name") String name) {
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName(name).build();
        Helloworld.HelloReply helloReply = urlSimple.sayHello(helloRequest);
        return helloReply != null ? helloReply.toString() : "null";
    }

}
