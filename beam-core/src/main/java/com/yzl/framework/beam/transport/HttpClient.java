package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;

public abstract class HttpClient implements Client {

    @Override
    public Response request(Request request) {
        return sendRequest(request);
    }

    public abstract Response sendRequest(Request request);
}
