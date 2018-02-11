package com.yzl.framework.beam.core;

import lombok.Data;

@Data
public class BinderDefine<T> {

    private Class<T> interfaceClass;
    private String name;
    private Class<? extends T> binderClass;
    private Scope scope;
    private int order;

}