package com.yzl.framework.beam.cluster.support;

import com.yzl.framework.beam.cluster.HaStrategy;
import com.yzl.framework.beam.cluster.HaStrategyFactory;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.core.BinderSupporter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class DefaultHaStrategyFactory<T> implements HaStrategyFactory<T> {

    @Setter
    @Getter
    private String defaultName = URLParamType.haStrategy.getValue();

    @Override
    public HaStrategy<T> newInstance(String name) {
        if (StringUtils.isBlank(name)) {
            name = this.defaultName;
        }
        return BinderSupporter.generate(HaStrategy.class, name);
    }
}
