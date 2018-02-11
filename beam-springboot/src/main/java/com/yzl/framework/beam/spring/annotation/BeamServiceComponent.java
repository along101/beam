package com.yzl.framework.beam.spring.annotation;

import com.yzl.framework.beam.annotation.BeamService;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by along on 2017/12/20.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@BeamService
public @interface BeamServiceComponent {
}
