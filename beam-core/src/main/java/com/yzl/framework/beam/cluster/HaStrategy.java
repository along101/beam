package com.yzl.framework.beam.cluster;


import com.yzl.framework.beam.core.Scope;
import com.yzl.framework.beam.core.Spi;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;

@Spi(scope = Scope.PROTOTYPE)
public interface HaStrategy<T> {

    String getStrategyName();

    Response call(Request request, LoadBalance<T> loadBalance);

}
