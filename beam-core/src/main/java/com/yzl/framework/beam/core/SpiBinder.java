package com.yzl.framework.beam.core;

import com.yzl.framework.beam.common.BeamConstants;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpiBinder {
    String name() default BeamConstants.DEFAULT_BINDER_NAME;

    int order() default 0;
}
