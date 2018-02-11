package com.yzl.framework.beam.cluster.ha;

import com.yzl.framework.beam.cluster.LoadBalance;
import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;

@SpiBinder(name = "failfast")
public class FailfastHaStrategy extends AbstractHaStrategy {

    @Override
    public String getStrategyName() {
        return "failfast";
    }

    @Override
    public Response call(Request request, LoadBalance loadBalance) {
        Refer refer = loadBalance.select(request);
        return refer.call(request);
    }

}
