package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.registry.Registry;
import com.yzl.framework.beam.registry.support.DirectRegistry;
import com.yzl.framework.beam.rpc.URL;
import com.yzl.framework.beam.spring.properties.RegistryProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO 加上配置属性定义
@Configuration
@EnableConfigurationProperties({RegistryProperties.class})
public class BeamRegistryAutoConfiguration {
    @Autowired
    private Environment environment;

    @Autowired
    private RegistryProperties registryProperties;

    @Bean
    @ConditionalOnProperty(name = "beam.registry.name", havingValue = "direct", matchIfMissing = true)
    public Registry createDirectRegistry() {
        String hostConfig = environment.getProperty("beam.registry.direct.address", "localhost:8080");
        String basePath = registryProperties.getBasePath();
        String[] hosts = StringUtils.split(hostConfig, ",");
        List<URL> directUrls = new ArrayList<>();
        for (String hostPort : hosts) {
            String hostStr = StringUtils.substringBeforeLast(hostPort, ":");
            String portStr = StringUtils.substringAfterLast(hostPort, ":");
            int port = Integer.parseInt(StringUtils.isNotBlank(portStr) ? portStr : "8080");
            URL directUrl = URL.builder()
                    .host(hostStr)
                    .port(port)
                    .path(basePath)
                    .parameters(new HashMap<>())
                    .build();
            directUrls.add(directUrl);
        }
        return new DirectRegistry(directUrls);
    }

}
