package com.yzl.framework.beam.serialize;

import com.yzl.framework.beam.core.Scope;
import com.yzl.framework.beam.core.Spi;

import java.io.IOException;

/**
 * Created by yinzuolong on 2017/12/2.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

    String getName();
}
