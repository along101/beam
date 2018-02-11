package com.yzl.framework.beam.common;


import com.yzl.framework.beam.exception.BeamServiceException;

public enum URLParamType {

    version("version", "1.0"),
    nodeType("nodeType", BeamConstants.NODE_TYPE_SERVICE),
    transExceptionStack("transExceptionStack", true),
    retries("retries", 0),
    loadbalance("loadbalance", "roundrobin"),
    haStrategy("haStrategy", "failover"),
    serialization("x-serialization", "protobuf.bin"),
    port("port", 0),
    httpSchema("httpSchema", "http"),
    httpVersion("statusLine.protocolVersion", "1.1"),
    httpStatusCode("statusLine.code", 0),
    httpReasonPhrase("statusLine.reasonPhrase", ""),
    accessLog("accessLog", true),
    clientHost("clientHost", ""),
    filter("filter", false),
    requestId("requestId", "0"),
    parameterTypes("parameterTypes", null),
    returnType("returnType", null),
    basePath("basePath", "/"),
    appId("appId", ""),
    beamVersion("beamVersion", ""),
    exceptionClassHeader("x-exception-class", BeamServiceException.class.getName()),
    //    responsePayloadSize("responsePayloadSize", 0),
    statusCode("code", 0),;

    private String name;
    private String value;
    private long longValue;
    private int intValue;
    private boolean boolValue;

    URLParamType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    URLParamType(String name, long longValue) {
        this.name = name;
        this.value = String.valueOf(longValue);
        this.longValue = longValue;
    }

    URLParamType(String name, int intValue) {
        this.name = name;
        this.value = String.valueOf(intValue);
        this.intValue = intValue;
    }

    URLParamType(String name, boolean boolValue) {
        this.name = name;
        this.value = String.valueOf(boolValue);
        this.boolValue = boolValue;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public boolean getBooleanValue() {
        return boolValue;
    }

}
