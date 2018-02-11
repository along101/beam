package com.yzl.framework.beam.protocol;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.protocol.beam.BeamApacheHttpClientFactory;
import com.yzl.framework.beam.protocol.beam.BeamProtocol;
import com.yzl.framework.beam.rpc.Protocol;
import com.yzl.framework.beam.rpc.URL;
import com.yzl.framework.beam.transport.AbstractServletEndpoint;
import com.yzl.framework.beam.transport.HttpClientFactory;
import com.yzl.framework.beam.transport.ServletEndpoint;
import com.yzl.framework.beam.util.NetUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class BeamProtocolFactory implements ProtocolFactory {

    @Getter
    @Setter
    private AbstractServletEndpoint endpoint;
    @Getter
    @Setter
    private HttpClientFactory clientFactory;

    public BeamProtocolFactory() {
        init();
    }

    public BeamProtocolFactory(AbstractServletEndpoint endpoint, HttpClientFactory clientFactory) {
        this.endpoint = endpoint;
        this.clientFactory = clientFactory;
        init();
    }

    public void init() {
        if (this.endpoint == null) {
            URL url = URL.builder().host(NetUtils.getLocalIp())
                    .port(BeamConstants.DEFAULT_PORT)
                    .protocol(BeamConstants.DEFAULT_PROTOCOL)
                    .path(BeamConstants.PATH_SEPARATOR)
                    .parameters(new HashMap<>())
                    .build();
            this.endpoint = new ServletEndpoint(url);
        }
        if (this.clientFactory == null) {
            this.clientFactory = new BeamApacheHttpClientFactory();
        }
    }

    @Override
    public Protocol newInstance() {
        return new BeamProtocol(endpoint, clientFactory);
    }
}
