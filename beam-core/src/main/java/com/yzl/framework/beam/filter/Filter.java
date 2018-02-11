package com.yzl.framework.beam.filter;


import com.yzl.framework.beam.core.Spi;
import com.yzl.framework.beam.rpc.Caller;
import com.yzl.framework.beam.rpc.Request;
import com.yzl.framework.beam.rpc.Response;

@Spi
public interface Filter {

    Response filter(Caller caller, Request request);

    boolean defaultEnable();

    int getOrder();
}
