package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.rpc.Provider;

public interface EndpointFactory {

    Endpoint createEndpoint(Provider<?> provider);
}
