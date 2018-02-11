package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;
import com.yzl.framework.beam.rpc.URL;

public abstract class HttpClientFactory implements ClientFactory {

    public abstract HttpClient createHttpClient(URL referUrl, URL serviceUrl);

    @Override
    public Client createClient(URL referUrl, URL serviceUrl) {
        return new Client() {
            @Override
            public Response request(Request request) {
                HttpClient httpClient = createHttpClient(referUrl, serviceUrl);
                return httpClient.request(request);
            }
        };
    }
}
