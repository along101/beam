package com.yzl.framework.beam.spring.test.client;

import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.spring.annotation.BeamRouteClientScan;
import com.yzl.framework.beam.spring.autoconfig.BeamServiceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@BeamRouteClientScan(basePackageClasses = {Simple.class})
@EnableAutoConfiguration(exclude = {BeamServiceAutoConfiguration.class})
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
