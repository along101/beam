package com.yzl.framework.beam.cluster.support;


import com.yzl.framework.beam.cluster.ClusterCaller;
import com.yzl.framework.beam.cluster.HaStrategy;
import com.yzl.framework.beam.cluster.LoadBalance;
import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.exception.BeamAbstractException;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.exception.BeamServiceException;
import com.yzl.framework.beam.protocol.ProtocolFilterDecorator;
import com.yzl.framework.beam.registry.Registry;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.util.ExceptionUtil;
import com.yzl.framework.beam.util.NetUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class DefaultClusterCaller<T> implements ClusterCaller<T> {

    private Class<T> interfaceClass;
    private Protocol protocol;
    private URL referUrl;
    private Registry registry;

    private AtomicReference<List<Refer<T>>> refers = new AtomicReference<>();

    @Setter
    private ClusterListener clusterListener;
    @Setter
    private LoadBalance<T> loadBalance;
    @Setter
    private HaStrategy<T> haStrategy;

    public DefaultClusterCaller(Class<T> interfaceClass, Protocol protocol, URL referUrl, Registry registry) {
        this.interfaceClass = interfaceClass;
        this.protocol = protocol;
        this.referUrl = referUrl;
        this.registry = registry;
    }

    @Override
    public void init() {
        //注意初始化顺序
        initProtocol();
        initReferUrl();
        initLoadBalance();
        initHaStrategy();
        initClusterSupport();
        //最后初始化refers
        initRefers();
    }

    protected void initProtocol() {
        if (this.protocol == null) {
            throw new BeamFrameworkException("protocol can not be null.");
        }
        this.protocol = getDecorateProtocol(this.protocol);
    }

    protected void initReferUrl() {
        URL newReferUrl = URL.builder().protocol(this.protocol.getName())
                .host(NetUtils.getLocalIp())
                .port(0)
                .path(this.interfaceClass.getName())
                .parameters(referUrl.getParameters() == null ? new HashMap<>() : referUrl.getParameters())
                .build();
        newReferUrl.addParameter(URLParamType.nodeType.name(), BeamConstants.NODE_TYPE_REFER);
        this.referUrl = newReferUrl;
    }

    protected void initClusterSupport() {
        if (this.clusterListener != null) {
            return;
        }
        this.clusterListener = new ClusterListener(this);
        //订阅服务，根据不同注册中心的实现，这可能会去更新cluster的refers
        this.registry.subscribe(referUrl, this.clusterListener);
    }

    protected void initRefers() {
        //如果refers没有初始化
        if (CollectionUtils.isEmpty(this.refers.get())) {
            List<URL> serviceUrls = this.registry.discover(referUrl);
            this.onRefresh(serviceUrls);
        }
    }

    protected void initLoadBalance() {
        if (this.loadBalance == null) {
            this.loadBalance = new DefaultLoadBalanceFactory<T>().newInstance(URLParamType.loadbalance.getValue());
        }
    }

    protected void initHaStrategy() {
        if (this.haStrategy == null) {
            this.haStrategy = new DefaultHaStrategyFactory<T>().newInstance(URLParamType.haStrategy.getValue());
        }
    }

    protected Protocol getDecorateProtocol(Protocol protocol) {
        if (protocol instanceof ProtocolFilterDecorator) {
            return protocol;
        }
        return new ProtocolFilterDecorator(protocol, null);
    }

    @Override
    public void destroy() {
        //销毁所有的refer
        if (this.refers.get() != null) {
            for (Refer refer : refers.get()) {
                refer.destroy();
            }
        }
    }

    @Override
    public Response call(Request request) {
        try {
            return haStrategy.call(request, loadBalance);
        } catch (Exception e) {
            throw callFalse(request, e);
        }
    }

    @Override
    public void onRefresh(List<URL> serviceUrls) {
        List<Refer<T>> newRefers = new ArrayList<>();
        List<Refer<T>> oldRefers = this.refers.get() == null ? new ArrayList<>() : this.refers.get();
        for (URL serviceUrl : serviceUrls) {
            if (!serviceUrl.canServe(referUrl)) {
                continue;
            }
            Refer<T> refer = getExistingRefer(serviceUrl);
            if (refer == null) {
                // serverURL, referURL的配置会被serverURL的配置覆盖
                URL referURL = serviceUrl.createCopy();
                referURL.addParameters(this.referUrl.getParameters());
                refer = protocol.refer(this.interfaceClass, referURL, serviceUrl);
            }
            if (refer != null) {
                newRefers.add(refer);
            }
        }
        this.refers.set(newRefers);
        loadBalance.onRefresh(newRefers);

        //关闭多余的refer
        List<Refer> delayDestroyRefers = new ArrayList<>();
        for (Refer refer : oldRefers) {
            if (newRefers.contains(refer)) {
                continue;
            }
            delayDestroyRefers.add(refer);
        }
        if (!delayDestroyRefers.isEmpty()) {
            ReferSupports.delayDestroy(delayDestroyRefers);
        }
    }


    /**
     * 一个serviceUrl对应一个refer,Url要完全相同
     *
     * @param serviceUrl
     * @return
     */
    private Refer<T> getExistingRefer(URL serviceUrl) {
        if (refers.get() == null) {
            return null;
        }
        for (Refer<T> refer : refers.get()) {
            if (ObjectUtils.equals(serviceUrl, refer.getServiceUrl())) {
                return refer;
            }
        }
        return null;
    }


    protected RuntimeException callFalse(Request request, Exception cause) {
        if (ExceptionUtil.isBizException(cause)) {
            return (RuntimeException) cause;
        }

        if (cause instanceof BeamAbstractException) {
            return (BeamAbstractException) cause;
        } else {
            return new BeamServiceException(String.format("ClusterSpi Call false for request: %s", request), cause);
        }
    }

    public List<Refer<T>> getRefers() {
        return this.refers.get();
    }

}
