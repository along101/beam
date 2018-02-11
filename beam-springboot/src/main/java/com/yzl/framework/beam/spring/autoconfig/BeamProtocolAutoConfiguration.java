package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.filter.Filter;
import com.yzl.framework.beam.protocol.ProtocolFilterDecorator;
import com.yzl.framework.beam.protocol.beam.BeamProtocol;
import com.yzl.framework.beam.rpc.Protocol;
import com.yzl.framework.beam.transport.AbstractServletEndpoint;
import com.yzl.framework.beam.transport.HttpClientFactory;
import com.yzl.framework.beam.transport.NonHttpClientFactory;
import com.yzl.framework.beam.transport.NonServletEndpoint;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeamProtocolAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean(Protocol.class)
    public Protocol createProtocol(ObjectProvider<List<Filter>> filters) {
        AbstractServletEndpoint endpoint;
        HttpClientFactory clientFactory;
        //TODO 目前只支持beam协议，以后支持多种协议
        if (applicationContext.getBeansOfType(AbstractServletEndpoint.class).size() == 0) {
            endpoint = createNonServletEndpoint();
        } else {
            endpoint = applicationContext.getBean(AbstractServletEndpoint.class);
        }
        if (applicationContext.getBeansOfType(HttpClientFactory.class).size() == 0) {
            clientFactory = createNonHttpClientFactory();
        } else {
            clientFactory = applicationContext.getBean(HttpClientFactory.class);
        }
        BeamProtocol beamProtocol = new BeamProtocol(endpoint, clientFactory);
        return new ProtocolFilterDecorator(beamProtocol, filters.getIfAvailable());
    }

    protected AbstractServletEndpoint createNonServletEndpoint() {
        return new NonServletEndpoint();
    }

    protected HttpClientFactory createNonHttpClientFactory() {
        return new NonHttpClientFactory();
    }
}
