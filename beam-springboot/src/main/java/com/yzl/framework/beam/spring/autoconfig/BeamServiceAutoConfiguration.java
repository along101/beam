package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.rpc.URL;
import com.yzl.framework.beam.transport.ServletEndpoint;
import com.yzl.framework.beam.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Import({BeamServiceProcessor.class})
public class BeamServiceAutoConfiguration {
    @Autowired
    private Environment env;

    @Bean
    @ConditionalOnMissingBean(ServletEndpoint.class)
    public ServletEndpoint createServletEndpoint() {
        int port = Integer.parseInt(env.getProperty("server.port", "8080"));
        String path = env.getProperty("beam.service.basePath", "beam");
        URL baseUrl = URL.builder()
                .host(NetUtils.getLocalIp())
                .port(port)
                .path(path)
                .parameters(new HashMap<>()).build();
        return new ServletEndpoint(baseUrl);
    }

    @Bean
    public ServletRegistrationBean registerServlet(ServletEndpoint servletEndpoint) {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setServlet(servletEndpoint);
        Map<String, String> initParams = new HashMap<>();
        registrationBean.setInitParameters(initParams);
        List<String> urlMappings = new ArrayList<>();
        //先把开头的/删除，在加上/，保护下
        String path = BeamConstants.PATH_SEPARATOR + StringUtils.removeStart(servletEndpoint.getBaseUrl().getPath(), BeamConstants.PATH_SEPARATOR);
        path = StringUtils.removeEnd(path, BeamConstants.PATH_SEPARATOR);
        urlMappings.add(path + "/*");
        registrationBean.setUrlMappings(urlMappings);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

}
