package com.yzl.framework.beam.demo.server.service;

import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.spring.annotation.BeamServiceComponent;
import org.apache.commons.lang3.RandomUtils;

@BeamServiceComponent
public class SimpleImpl implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
