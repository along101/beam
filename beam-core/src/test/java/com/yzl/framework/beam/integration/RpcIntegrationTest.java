package com.yzl.framework.beam.integration;

import com.codahale.metrics.ConsoleReporter;
import com.yzl.framework.beam.cluster.ClusterCaller;
import com.yzl.framework.beam.cluster.support.DefaultClusterCaller;
import com.yzl.framework.beam.direct.DirectUrlCaller;
import com.yzl.framework.beam.metric.MetricContext;
import com.yzl.framework.beam.proto.Helloworld;
import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.proto.SimpleImpl;
import com.yzl.framework.beam.protocol.ProtocolFilterDecorator;
import com.yzl.framework.beam.protocol.beam.BeamApacheHttpClientFactory;
import com.yzl.framework.beam.protocol.beam.BeamProtocol;
import com.yzl.framework.beam.proxy.ClusterInvocationHandler;
import com.yzl.framework.beam.proxy.DirectInvocationHandler;
import com.yzl.framework.beam.proxy.JdkProxyFactory;
import com.yzl.framework.beam.registry.Registry;
import com.yzl.framework.beam.registry.support.DirectRegistry;
import com.yzl.framework.beam.registry.support.LocalRegistry;
import com.yzl.framework.beam.rpc.*;
import com.yzl.framework.beam.transport.AbstractServletEndpoint;
import com.yzl.framework.beam.transport.JettyServletEndpoint;
import com.yzl.framework.beam.transport.NonServletEndpoint;
import com.yzl.framework.beam.util.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RpcIntegrationTest {

    @Test
    public void testServer() {
        //初始化注册中心
        LocalRegistry registry = new LocalRegistry();

        // 生成servlet
        URL baseUrl = URL.builder()
                .protocol("beam")
                .host(NetUtils.getLocalIp())
                .port(8080)
                .path("/beam")
                .parameters(new HashMap<>()).build();
        JettyServletEndpoint servletEndpoint = new JettyServletEndpoint(baseUrl);

        //初始化协议
        BeamProtocol beamProtocol = new BeamProtocol(servletEndpoint, null);
        beamProtocol.setEndpoint(servletEndpoint);
        Protocol protocol = new ProtocolFilterDecorator(beamProtocol, null);

        //初始化service
        URL serviceUrl = URL.builder().parameters(new HashMap<>()).build();
        Simple simple = new SimpleImpl();
        Provider<Simple> provider = new DefaultProvider<>(Simple.class, simple, serviceUrl.getParameters());
        Exporter<Simple> exporter = protocol.export(provider, serviceUrl);

        //注册服务
        registry.doRegister(exporter.getServiceUrl());

        //打印metric
        ConsoleReporter slf4jReporter = ConsoleReporter.forRegistry(MetricContext.getMetricRegistry())
                .build();
        slf4jReporter.start(10, TimeUnit.SECONDS);

        //Server线程join过来
//        servletEndpoint.getServer().join();

    }

    @Test
    public void testClient() {
        //启动服务端
        testServer();

        //初始化注册中心
        URL registryUrl = URL.builder()
                .host(NetUtils.getLocalIp())
                .port(8080)
                .path("/beam")
                .parameters(new HashMap<>())
                .build();
        Registry registry = new DirectRegistry(Collections.singletonList(registryUrl));

        //初始化协议
        AbstractServletEndpoint servletEndpoint = new NonServletEndpoint();
        BeamApacheHttpClientFactory httpClientFactory = new BeamApacheHttpClientFactory();
        httpClientFactory.init();
        BeamProtocol beamProtocol = new BeamProtocol(servletEndpoint, httpClientFactory);
        Protocol protocol = new ProtocolFilterDecorator(beamProtocol, null);

        //集群，referUrl只有配置有用
        URL referUrl = URL.builder().build();
        ClusterCaller<Simple> cluster = new DefaultClusterCaller<>(Simple.class, protocol, referUrl, registry);
        cluster.init();

        //代理
        ClusterInvocationHandler<Simple> invocationHandler = new ClusterInvocationHandler<>(cluster);
        Simple proxy = new JdkProxyFactory().getProxy(Simple.class, invocationHandler);
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName("yzl").build();
        Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);

        System.out.println(helloReply);
    }

    @Test
    public void testDirectCall() {

        //启动服务端
        testServer();

        BeamApacheHttpClientFactory httpClientFactory = new BeamApacheHttpClientFactory();
        httpClientFactory.init();
        BeamProtocol beamProtocol = new BeamProtocol(null, httpClientFactory);
        Protocol protocol = new ProtocolFilterDecorator(beamProtocol, null);

        String url = "http://localhost:8080/beam/" + Simple.class.getName();

        DirectUrlCaller<Simple> directUrlCaller = new DirectUrlCaller<>(Simple.class, protocol);
        directUrlCaller.init(URL.valueOf(url));

        DirectInvocationHandler<Simple> invocationHandler = new DirectInvocationHandler<>(directUrlCaller);

        Simple proxy = new JdkProxyFactory().getProxy(Simple.class, invocationHandler);
        Helloworld.HelloRequest helloRequest = Helloworld.HelloRequest.newBuilder()
                .setName("yzl").build();
        try {
            Helloworld.HelloReply helloReply = proxy.sayHello(helloRequest);
            System.out.println(helloReply);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

    }
}
