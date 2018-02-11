package com.yzl.framework.beam.cluster.support;

import com.yzl.framework.beam.cluster.LoadBalance;
import com.yzl.framework.beam.cluster.LoadBalanceFactory;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.core.BinderSupporter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class DefaultLoadBalanceFactory<T> implements LoadBalanceFactory {

    @Setter
    @Getter
    private String defaultName = URLParamType.loadbalance.getValue();

    @Override
    public LoadBalance<T> newInstance(String name) {
        if (StringUtils.isBlank(name)) {
            name = this.defaultName;
        }
        return BinderSupporter.generate(LoadBalance.class, name);
    }

}
