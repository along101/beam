package com.yzl.framework.beam.serialize;

import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.core.BinderSupporter;
import com.yzl.framework.beam.exception.BeamServiceException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by along on 2017/12/17.
 */
public class ProtobufSerializationFactory implements SerializationFactory {

    @Setter
    @Getter
    private String defaultName = URLParamType.serialization.getValue();

    private final Map<String, Serialization> cached = new ConcurrentHashMap<>();

    @Override
    public Serialization newInstance(String name) {
        if (StringUtils.isBlank(name)) {
            name = defaultName;
        }
        Serialization serialization = cached.get(name);
        if (serialization != null) {
            return serialization;
        }
        synchronized (cached) {
            serialization = cached.get(name);
            if (serialization != null) {
                return serialization;
            }
            serialization = BinderSupporter.generate(Serialization.class, name);
            if (serialization == null) {
                throw new BeamServiceException("Can not create Serialization instance by name: " + name);
            }
            cached.put(name, serialization);
            return serialization;
        }
    }
}
