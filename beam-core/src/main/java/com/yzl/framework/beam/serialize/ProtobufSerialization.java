package com.yzl.framework.beam.serialize;

import com.google.protobuf.Message;
import com.yzl.framework.beam.exception.BeamServiceException;

/**
 * Created by along on 2017/12/17.
 */
public abstract class ProtobufSerialization implements Serialization {

    public abstract byte[] serializeMessage(Message message);

    public abstract <T extends Message> T deserializeMessage(byte[] bytes, Class<T> clazz);

    @Override
    public byte[] serialize(Object obj) {
        if (obj instanceof Message)
            return serializeMessage((Message) obj);
        throw new BeamServiceException(String.format("object [%s] is not protobuf Message, can not serialize.", obj));
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (!Message.class.isAssignableFrom(clazz)) {
            throw new BeamServiceException(String.format("class [%s] is not protobuf Message, can not serialize.", clazz.getName()));
        }
        Class<? extends Message> type = (Class<? extends Message>) clazz;
        return (T) deserializeMessage(bytes, type);
    }

}
