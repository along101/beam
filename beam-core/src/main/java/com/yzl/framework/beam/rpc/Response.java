package com.yzl.framework.beam.rpc;

import java.util.Map;

/**
 * Created by yinzuolong on 2017/12/2.
 */
public interface Response {

    Object getValue();

    Exception getException();

    long getRequestId();

    int getCode();

    long getProcessTime();

    void setProcessTime(long time);

    int getTimeout();

    Map<String, String> getAttachments();

    void setAttachment(String key, String value);

}
