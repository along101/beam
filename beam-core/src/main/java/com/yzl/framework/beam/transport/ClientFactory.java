package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.rpc.URL;

public interface ClientFactory {

    void init();

    void destroy();

    Client createClient(URL referUrl, URL serviceUrl);

}
