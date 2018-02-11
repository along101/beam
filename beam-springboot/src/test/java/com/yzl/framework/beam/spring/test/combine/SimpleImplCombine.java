package com.yzl.framework.beam.spring.test.combine;

import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.spring.annotation.BeamServiceComponent;
import org.apache.commons.lang3.RandomUtils;

@BeamServiceComponent
public class SimpleImplCombine implements Simple {

    @Override
    public Helloworld.HelloReply sayHello(Helloworld.HelloRequest request) {
        String hello = "Hello Combine " + request.getName() + ". " + RandomUtils.nextInt();
        return Helloworld.HelloReply.newBuilder().setMessage(hello).build();
    }

}
