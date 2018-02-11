package com.yzl.framework.beam.filter;

import com.yzl.framework.beam.core.BinderDefine;
import com.yzl.framework.beam.core.BinderDefines;
import com.yzl.framework.beam.core.BinderSupporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultFiltersSupport {

    public static List<Filter> getDefaultFilters() {
        List<Filter> filters = new ArrayList<>();
        Map<String, BinderDefine<?>> binderDefines = BinderDefines.getInstance().getBinderDefines(Filter.class);
        for (BinderDefine<?> binderDefine : binderDefines.values()) {
            Filter filter = (Filter) BinderSupporter.newInstance(binderDefine.getBinderClass());
            filters.add(filter);
        }
        return filters;
    }
}
