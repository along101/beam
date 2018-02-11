package com.yzl.framework.beam.protocol.beam;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.protocol.AbstractProtocol;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.transport.AbstractServletEndpoint;
import com.yzl.framework.beam.transport.HttpClient;
import com.yzl.framework.beam.transport.HttpClientFactory;
import com.yzl.framework.beam.transport.NonServletEndpoint;
import lombok.Getter;
import lombok.Setter;

public class BeamProtocol extends AbstractProtocol {

    @Getter
    @Setter
    private AbstractServletEndpoint endpoint;
    @Getter
    @Setter
    private HttpClientFactory clientFactory;

    public BeamProtocol(AbstractServletEndpoint endpoint, HttpClientFactory clientFactory) {
        if (endpoint == null) {
            endpoint = new NonServletEndpoint();
        }
        this.endpoint = endpoint;
        this.clientFactory = clientFactory;
    }

    @Override
    public String getName() {
        return "beam";
    }

    @Override
    public <T> Refer<T> refer(Class<T> interfaceClass, URL referUrl, URL serviceUrl) {
        HttpClient httpClient = clientFactory.createHttpClient(referUrl, serviceUrl);
        return new BeamRefer<>(interfaceClass, referUrl, serviceUrl, httpClient);
    }

    @Override
    protected <T> URL deploy(Provider<T> provider, URL serviceUrl) {
        return this.endpoint.export(provider, serviceUrl);
    }

    @Override
    protected String getProtocolName() {
        return BeamConstants.DEFAULT_PROTOCOL;
    }

    public static class BeamRefer<T> extends AbstractRefer<T> {
        private HttpClient httpClient;

        public BeamRefer(Class<T> interfaceClass, URL referUrl, URL serviceUrl, HttpClient httpClient) {
            super(interfaceClass, referUrl, serviceUrl);
            this.httpClient = httpClient;
        }

        @Override
        protected Response doCall(Request request) {
            return httpClient.request(request);
        }

        @Override
        public void init() {

        }

        @Override
        public void destroy() {

        }

    }
}
