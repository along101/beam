package com.yzl.framework.beam.direct;

import com.yzl.framework.beam.rpc.*;
import lombok.Getter;

@Getter
public class DirectUrlCaller<T> implements Caller {

    private volatile Refer<T> refer;

    private Class<T> interfaceClass;

    private Protocol protocol;

    public DirectUrlCaller(Class<T> interfaceClass, Protocol protocol) {
        this.interfaceClass = interfaceClass;
        this.protocol = protocol;
    }

    public void init(URL url) {
        this.refer = this.protocol.refer(interfaceClass, url, url);
    }

    @Override
    public Response call(Request request) {
        return this.refer.call(request);
    }


}
