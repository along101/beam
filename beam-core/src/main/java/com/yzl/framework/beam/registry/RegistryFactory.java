package com.yzl.framework.beam.registry;

import com.yzl.framework.beam.rpc.URL;

public interface RegistryFactory {

    Registry getRegistry(URL registryUrl);
}
