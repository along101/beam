package com.yzl.framework.beam.spring.test.combine;

import com.yzl.framework.beam.proto.Simple;
import com.yzl.framework.beam.spring.annotation.BeamRouteClientScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@BeamRouteClientScan(basePackageClasses = {Simple.class})
public class CombineApplication {
    public static void main(String[] args) {
        SpringApplication.run(CombineApplication.class, args);
    }
}
