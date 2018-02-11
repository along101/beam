package com.yzl.framework.beam.serialization;

import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.serialize.ProtobufJsonSerialization;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class ProtobufJsonSerializationTest {

    @Test
    public void test1() throws UnsupportedEncodingException {
        Helloworld.HelloRequest request = Helloworld.HelloRequest.newBuilder().setName("yzl").build();
        ProtobufJsonSerialization serialization = new ProtobufJsonSerialization();
        byte[] data = serialization.serializeMessage(request);

        System.out.println(new String(data, "utf-8"));
        Helloworld.HelloRequest newRequest = serialization.deserializeMessage(data, Helloworld.HelloRequest.class);

        System.out.println(newRequest.getName());

    }
}
