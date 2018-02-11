package com.yzl.framework.beam.spring.annotation;

import com.yzl.framework.beam.spring.autoconfig.BeamRouteClientReferProcessor;
import com.yzl.framework.beam.spring.autoconfig.BeamRouteClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by yinzuolong on 2017/12/2.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({BeamRouteClientsRegistrar.class, BeamRouteClientReferProcessor.class})
public @interface BeamRouteClientScan {

    Class<?>[] basePackageClasses() default {};

    String[] basePackages() default {};
}
