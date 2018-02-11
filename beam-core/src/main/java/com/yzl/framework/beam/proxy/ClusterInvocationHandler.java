package com.yzl.framework.beam.proxy;

import com.yzl.framework.beam.cluster.ClusterCaller;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ClusterInvocationHandler<T> extends AbstractInvocationHandler<T> {

    private ClusterCaller<T> clusterCaller;

    public ClusterInvocationHandler(ClusterCaller<T> clusterCaller) {
        super(clusterCaller.getInterfaceClass());
        this.clusterCaller = clusterCaller;
    }

    @Override
    public Object doInvoke(Request request) {
        request.setAttachment(URLParamType.version.getName(), clusterCaller.getReferUrl().getVersion());
        Response response = clusterCaller.call(request);
        return response.getValue();
    }

    @Override
    public boolean proxyEquals(Object o) {
        return o != null && this.clusterCaller != null && o.equals(this.clusterCaller);
    }

    @Override
    public String proxyToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{protocol:").append(clusterCaller.getReferUrl().getProtocol());
        List<Refer<T>> refers = clusterCaller.getRefers();
        if (refers != null) {
            for (Refer<T> refer : refers) {
                sb.append("[").append(refer.getServiceUrl().getUri()).append(", available:").append(refer.isAvailable()).append("]");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
