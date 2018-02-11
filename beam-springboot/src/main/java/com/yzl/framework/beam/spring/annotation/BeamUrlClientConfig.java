package com.yzl.framework.beam.spring.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(BeamUrlClientConfigs.class)
public @interface BeamUrlClientConfig {

    Class<?> clazz();

    String urlKey() default "";
}
