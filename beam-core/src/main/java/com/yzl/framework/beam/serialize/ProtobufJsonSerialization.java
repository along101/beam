package com.yzl.framework.beam.serialize;

import com.google.protobuf.Message;
import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.util.ProtoBuffUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by yinzuolong on 2017/12/2.
 */
@SpiBinder(name = "protobuf.json")
public class ProtobufJsonSerialization extends ProtobufSerialization {

    public static final String NAME = "protobuf.json";

    @Override
    public byte[] serializeMessage(Message message) {
        try {
            String jsonString = ProtoBuffUtils.convertProtoBuffToJson(message);
            return jsonString.getBytes(BeamConstants.DEFAULT_CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding " + BeamConstants.DEFAULT_CHARACTER, e);
        }
    }

    @Override
    public <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz) {
        try {
            return ProtoBuffUtils.convertJsonToProtoBuff(new String(bytes, BeamConstants.DEFAULT_CHARACTER), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported Encoding " + BeamConstants.DEFAULT_CHARACTER, e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
