package com.yzl.framework.beam.demo.client;

import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.spring.annotation.BeamRouteClientScan;
import com.yzl.framework.beam.spring.annotation.BeamUrlClientScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@BeamRouteClientScan(basePackageClasses = {Simple.class})
@BeamUrlClientScan(basePackageClasses = {Simple.class})
public class SpringBootClient {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootClient.class, args);
    }

}
