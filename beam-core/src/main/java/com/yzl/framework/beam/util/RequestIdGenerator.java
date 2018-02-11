package com.yzl.framework.beam.util;


import com.yzl.framework.beam.common.IpIdGenerator;

public class RequestIdGenerator {

    private static IpIdGenerator ipIdGenerator = new IpIdGenerator();

    /**
     * 获取 requestId
     *
     * @return
     */
    public static long getRequestId() {
        return ipIdGenerator.generateId();
    }
}
