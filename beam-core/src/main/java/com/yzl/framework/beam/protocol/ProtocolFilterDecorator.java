package com.yzl.framework.beam.protocol;

import com.yzl.framework.beam.common.BeamMessageConstant;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.core.BinderDefine;
import com.yzl.framework.beam.core.BinderDefines;
import com.yzl.framework.beam.core.BinderSupporter;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.filter.Filter;
import com.yzl.framework.beam.rpc.*;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class ProtocolFilterDecorator implements Protocol {

    private Protocol protocol;
    private List<Filter> filters;

    public ProtocolFilterDecorator(Protocol protocol, List<Filter> filters) {
        if (protocol == null) {
            throw new BeamFrameworkException("Protocol is null when construct ProtocolFilterDecorator",
                    BeamMessageConstant.FRAMEWORK_INIT_ERROR);
        }
        this.protocol = protocol;
        this.filters = filters;
        initFilters();
    }

    @Override
    public String getName() {
        return protocol.getName();
    }

    @Override
    public <T> Exporter<T> export(Provider<T> provider, URL serviceUrl) {
        return protocol.export(decorateWithFilter(provider, serviceUrl), serviceUrl);
    }

    @Override
    public <T> Refer<T> refer(Class<T> interfaceClass, URL referUrl, URL serviceUrl) {
        return decorateWithFilter(protocol.refer(interfaceClass, referUrl, serviceUrl), referUrl);
    }

    public void destroy() {
        protocol.destroy();
    }

    private <T> Refer<T> decorateWithFilter(Refer<T> refer, URL referUrl) {
        List<Filter> filters = getFilters(referUrl);
        Refer<T> lastRefer = refer;
        for (Filter filter : filters) {
            final Filter filterFinal = filter;
            final Refer lastReferFinal = lastRefer;
            lastRefer = new Refer<T>() {
                @Override
                public Response call(Request request) {
                    return filterFinal.filter(lastReferFinal, request);
                }

                @Override
                public void destroy() {
                    refer.destroy();
                }

                @Override
                public URL getReferUrl() {
                    return refer.getReferUrl();
                }

                @Override
                public void init() {
                    refer.init();
                }

                @Override
                public Class<T> getInterface() {
                    return refer.getInterface();
                }

                @Override
                public boolean isAvailable() {
                    return refer.isAvailable();
                }

                @Override
                public URL getServiceUrl() {
                    return refer.getServiceUrl();
                }
            };
        }
        return lastRefer;
    }

    private <T> Provider<T> decorateWithFilter(final Provider<T> provider, URL serviceUrl) {
        List<Filter> filters = getFilters(serviceUrl);
        if (filters == null || filters.size() == 0) {
            return provider;
        }
        Provider<T> lastProvider = provider;
        for (Filter filter : filters) {
            final Filter filterFinal = filter;
            final Provider<T> lastProviderFinal = lastProvider;
            lastProvider = new Provider<T>() {
                @Override
                public Response call(Request request) {
                    return filterFinal.filter(lastProviderFinal, request);
                }

                @Override
                public void destroy() {
                    provider.destroy();
                }

                @Override
                public Method lookupMethod(String methodName, String[] parameterTypes) {
                    return provider.lookupMethod(methodName, parameterTypes);
                }

                @Override
                public Class<T> getInterface() {
                    return provider.getInterface();
                }

                @Override
                public void init() {
                    provider.init();
                }

                @Override
                public T getImpl() {
                    return provider.getImpl();
                }

                @Override
                public URL getServiceUrl() {
                    return provider.getServiceUrl();
                }

                @Override
                public void setServiceUrl(URL serviceUrl) {
                    provider.setServiceUrl(serviceUrl);
                }

            };
        }
        return lastProvider;
    }

    private void initFilters() {
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }

        Map<String, BinderDefine<?>> binderMap = BinderDefines.getInstance().getBinderDefines(Filter.class);
        for (BinderDefine<?> binderDefine : binderMap.values()) {
            Filter filter = (Filter) BinderSupporter.newInstance(binderDefine.getBinderClass());
            if (!this.filters.contains(filter)) {
                this.filters.add(filter);
            }
        }
    }

    /**
     * @param url referUrl or serviceUrl
     * @return
     */
    private List<Filter> getFilters(URL url) {
        List<Filter> enableFilers = new ArrayList<>();
        for (Filter filter : this.filters) {
            Boolean urlFilterEnable = url.getBooleanParameter(URLParamType.filter.name());
            if ((urlFilterEnable == null && filter.defaultEnable())
                    || BooleanUtils.isTrue(urlFilterEnable)) {
                enableFilers.add(filter);
            }
        }

        enableFilers.sort(new Comparator<Filter>() {
            @Override
            public int compare(Filter o1, Filter o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        Collections.reverse(enableFilers);
        return enableFilers;
    }

}
