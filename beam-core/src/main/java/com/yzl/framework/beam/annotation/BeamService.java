package com.yzl.framework.beam.annotation;

import java.lang.annotation.*;

/**
 * Created by yinzuolong on 2017/12/2.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeamService {
    String path() default "";
}
