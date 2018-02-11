package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;

public interface Client {

    Response request(Request request);

}
