package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.direct.UrlRepository;
import org.springframework.core.env.Environment;

public class SpringEnvUrlRepository extends UrlRepository {

    private Environment environment;

    public SpringEnvUrlRepository(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getUrlString(String urlKey) {
        return environment.getProperty(urlKey);
    }
}
