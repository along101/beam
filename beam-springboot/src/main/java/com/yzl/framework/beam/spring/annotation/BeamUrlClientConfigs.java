package com.yzl.framework.beam.spring.annotation;

import com.yzl.framework.beam.spring.autoconfig.BeamUrlClientReferProcessor;
import com.yzl.framework.beam.spring.autoconfig.BeamUrlClientsConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({BeamUrlClientsConfig.class, BeamUrlClientReferProcessor.class})
public @interface BeamUrlClientConfigs {

    BeamUrlClientConfig[] value() default {};

}
