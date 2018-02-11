package com.yzl.framework.beam.rpc;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public interface Request {

    String getInterfaceName();

    String getMethodName();

    String[] getParameterTypes();

    Object[] getArguments();

    Class<?> getReturnType();

    Map<String, String> getAttachments();

    void setAttachment(String name, String value);

    long getRequestId();

    int getRetries();

    void setRetries(int retries);

}
