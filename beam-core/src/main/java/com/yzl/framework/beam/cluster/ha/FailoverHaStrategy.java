package com.yzl.framework.beam.cluster.ha;


import com.yzl.framework.beam.cluster.LoadBalance;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.core.SpiBinder;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.rpc.Refer;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;
import com.yzl.framework.beam.rpc.URL;
import com.yzl.framework.beam.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@SpiBinder(name = "failover")
@Slf4j
public class FailoverHaStrategy<T> extends AbstractHaStrategy<T> {

    @Override
    public String getStrategyName() {
        return "failover";
    }

    @Override
    public Response call(Request request, LoadBalance<T> loadBalance) {

        List<Refer<T>> refers = loadBalance.selectToHolder(request);
        if (refers.isEmpty()) {
            throw new BeamServiceException(String.format("FailoverHaStrategy No refers for request:%s, loadbalance:%s", request,
                    loadBalance));
        }
        URL referUrl = refers.get(0).getReferUrl();
        // 先使用method的配置
        int tryCount = referUrl.getMethodParameter(request.getMethodName(), URLParamType.retries.getName(),
                URLParamType.retries.getIntValue());
        // 如果有问题，则设置为不重试
        if (tryCount < 0) {
            tryCount = 0;
        }

        for (int i = 0; i <= tryCount; i++) {
            Refer refer = refers.get(i % refers.size());
            try {
                request.setRetries(i);
                return refer.call(request);
            } catch (RuntimeException e) {
                // 对于业务异常，直接抛出
                if (ExceptionUtil.isBizException(e)) {
                    throw e;
                } else if (i >= tryCount) {
                    throw e;
                }
                log.warn(String.format("FailoverHaStrategy Call false for request:%s error=%s", request, e.getMessage()));
            }
        }

        throw new BeamFrameworkException("FailoverHaStrategy.call should not come here!");
    }

}
