package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.rpc.URL;

public class NonHttpClientFactory extends HttpClientFactory {
    @Override
    public HttpClient createHttpClient(URL referUrl, URL serviceUrl) {
        throw new BeamFrameworkException("NonHttpClientFactory can not creare HttpClient.");
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
