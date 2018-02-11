package com.yzl.framework.beam.serialize;

/**
 * Created by along on 2017/12/17.
 */
public interface SerializationFactory {

    Serialization newInstance(String name);

}
