package com.yzl.framework.beam.spring.properties;

import com.yzl.framework.beam.util.NetUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "beam.registry")
public class RegistryProperties {

    private String name;
    private String basePath = "beam";
    private String clusterName;
    private String appId;
    private String appName;
    private String instanceId;
    private String host = NetUtils.getLocalIp();
    private int port = 8080;

    private Map<String, String> tags = new HashMap<>();

}
