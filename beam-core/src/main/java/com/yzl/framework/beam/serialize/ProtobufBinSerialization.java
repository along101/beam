package com.yzl.framework.beam.serialize;

import com.google.protobuf.Message;
import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.util.ProtoBuffUtils;

/**
 * Created by yinzuolong on 2017/12/2.
 */
@SpiBinder(name = "protobuf.bin")
public class ProtobufBinSerialization extends ProtobufSerialization {

    public static final String NAME = "protobuf.bin";

    @Override
    public byte[] serializeMessage(Message message) {
        return message.toByteArray();
    }

    @Override
    public <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz) {
        return ProtoBuffUtils.byteArrayToProtobuf(bytes, clazz);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
