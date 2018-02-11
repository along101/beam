package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.rpc.Provider;
import com.yzl.framework.beam.rpc.URL;

import java.util.Map;

public interface Endpoint {

    Map<String, Provider<?>> getProviders();

    URL export(Provider<?> provider, URL serviceUrl);

}
