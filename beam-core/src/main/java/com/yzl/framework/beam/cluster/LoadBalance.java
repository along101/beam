
package com.yzl.framework.beam.cluster;

import com.yzl.framework.beam.core.Scope;
import com.yzl.framework.beam.core.Spi;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;

import java.util.List;

@Spi(scope = Scope.PROTOTYPE)
public interface LoadBalance<T> {

    void onRefresh(List<Refer<T>> refers);

    Refer<T> select(Request request);

    List<Refer<T>> selectToHolder(Request request);

    String getAlgorithm();

}
