package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.cluster.HaStrategy;
import com.yzl.framework.beam.cluster.HaStrategyFactory;
import com.yzl.framework.beam.cluster.LoadBalance;
import com.yzl.framework.beam.cluster.LoadBalanceFactory;
import com.yzl.framework.beam.cluster.support.DefaultClusterCaller;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.proxy.ClusterInvocationHandler;
import com.yzl.framework.beam.proxy.JdkProxyFactory;
import com.yzl.framework.beam.registry.Registry;
import com.yzl.framework.beam.rpc.Protocol;
import com.yzl.framework.beam.rpc.URL;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Map;

public class BeamRouteClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Class<?> interfaceClass;

    @Override
    public Object getObject() {
        Registry registry = getRegistry();
        Protocol protocol = getProtocol();
        URL referUrl = getReferUrl();

        DefaultClusterCaller<?> cluster = new DefaultClusterCaller<>(interfaceClass, protocol, referUrl, registry);

        LoadBalanceFactory<?> loadBalanceFactory = applicationContext.getBean(LoadBalanceFactory.class);
        LoadBalance loadBalance = loadBalanceFactory.newInstance(null);
        cluster.setLoadBalance(loadBalance);

        HaStrategyFactory<?> haStrategyFactory = applicationContext.getBean(HaStrategyFactory.class);
        HaStrategy haStrategy = haStrategyFactory.newInstance(null);
        cluster.setHaStrategy(haStrategy);

        cluster.init();
        ClusterInvocationHandler<?> invocationHandler = new ClusterInvocationHandler<>(cluster);
        //TODO 设置参数
        return new JdkProxyFactory().getProxy(interfaceClass, invocationHandler);
    }

    private Registry getRegistry() {
        //TODO 目前支持一个注册中心
        Map<String, Registry> registryMap = applicationContext.getBeansOfType(Registry.class);
        if (registryMap.size() > 1 || registryMap.size() == 0) {
            throw new BeamFrameworkException("Must has only one Registry bean, but has " + registryMap.size());
        }
        return new ArrayList<>(registryMap.values()).get(0);
    }

    private Protocol getProtocol() {
        //TODO 只支持一个BeamProtocol协议
        Map<String, Protocol> protocolMap = applicationContext.getBeansOfType(Protocol.class);
        if (protocolMap.size() > 1 || protocolMap.size() == 0) {
            throw new BeamFrameworkException("Must has only one BeamProtocol bean, but has " + protocolMap.size());
        }
        return new ArrayList<>(protocolMap.values()).get(0);
    }

    private URL getReferUrl() {
        //TODO 设置参数
        return URL.builder().build();
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}
