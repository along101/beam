package com.yzl.framework.beam.proxy;

import com.yzl.framework.beam.direct.DirectUrlCaller;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;

public class DirectInvocationHandler<T> extends AbstractInvocationHandler<T> {

    private DirectUrlCaller<T> directUrlCaller;

    public DirectInvocationHandler(DirectUrlCaller<T> directUrlCaller) {
        super(directUrlCaller.getInterfaceClass());
        this.directUrlCaller = directUrlCaller;
    }

    @Override
    public Object doInvoke(Request request) {
        Response response = directUrlCaller.call(request);
        return response.getValue();
    }

    @Override
    public String proxyToString() {
        if (directUrlCaller.getRefer() != null) {
            return directUrlCaller.getRefer().getServiceUrl().getUri();
        }
        return "null url";
    }

    @Override
    public boolean proxyEquals(Object o) {
        return o != null && this.directUrlCaller != null && o.equals(this.directUrlCaller);
    }
}
