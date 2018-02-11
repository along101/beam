package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.cluster.HaStrategyFactory;
import com.yzl.framework.beam.cluster.LoadBalanceFactory;
import com.yzl.framework.beam.cluster.support.DefaultHaStrategyFactory;
import com.yzl.framework.beam.cluster.support.DefaultLoadBalanceFactory;
import com.yzl.framework.beam.protocol.beam.BeamApacheHttpClientFactory;
import com.yzl.framework.beam.serialize.ProtobufSerializationFactory;
import com.yzl.framework.beam.serialize.SerializationFactory;
import com.yzl.framework.beam.transport.HttpClientFactory;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BeamClientAutoConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public HttpClientFactory createHttpClientFactory(HttpClientConnectionManager clientConnectionManager,
                                                     SerializationFactory serializationFactory) {
        BeamApacheHttpClientFactory factory = new BeamApacheHttpClientFactory();
        factory.setConnectionManager(clientConnectionManager);
        factory.setSerializationFactory(serializationFactory);
        factory.setConnectTimeout(Integer.parseInt(environment.getProperty("apache.httpclient.connectTimeout", "2000")));
        factory.setSocketTimeout(Integer.parseInt(environment.getProperty("apache.httpclient.socketTimeout", "10000")));
        factory.setConnectionRequestTimeout(Integer.parseInt(environment.getProperty("apache.httpclient.connectionRequestTimeout", "-1")));
        factory.setRetryCount(Integer.parseInt(environment.getProperty("apache.httpclient.retryCount", "0")));
        factory.init();

        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        int maxTotal = Integer.parseInt(environment.getProperty("apache.httpclient.connection.pool.maxTotal", "500"));
        int maxPerRoute = Integer.parseInt(environment.getProperty("apache.httpclient.connection.pool.maxPerRoute", "20"));
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        return connectionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public SerializationFactory buildSerializationFactory() {
        ProtobufSerializationFactory serializationFactory = new ProtobufSerializationFactory();
        return serializationFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalanceFactory<?> buildLoadBalanceFactory() {
        DefaultLoadBalanceFactory<?> loadBalanceFactory = new DefaultLoadBalanceFactory<>();
        return loadBalanceFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public HaStrategyFactory<?> buildHaStrategyFactory() {
        DefaultHaStrategyFactory<?> haStrategyFactory = new DefaultHaStrategyFactory<>();
        return haStrategyFactory;
    }
}
