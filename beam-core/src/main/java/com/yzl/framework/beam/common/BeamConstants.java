package com.yzl.framework.beam.common;

import java.util.regex.Pattern;

public class BeamConstants {

    public static final String METRIC_NAME = "beam";
    public static final String SEPERATOR_ARRAY = ",";
    public static final String SEPERATOR_ACCESS_LOG = "|";
    public static final String DEFAULT_BINDER_NAME = "DEFAULT";
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    public static final String METHOD_CONFIG_PREFIX = "method.";
    public static final String HOST_PORT_SEPARATOR = ":";
    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PATH_SEPARATOR = "/";
    public static final String NODE_TYPE_SERVICE = "service";
    public static final String NODE_TYPE_REFER = "refer";
    public static final String NODE_TYPE_REGISTRY = "registry";
    public static final String REGISTRY_PROTOCOL_LOCAL = "local";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final String DEFAULT_CHARACTER = "utf-8";
    public static final String DEFAULT_PROTOCOL = "beam";
    public static final int HTTP_EXPECTATION_FAILED = 419;
    public static final int HTTP_OK = 200;
    public static final int DEFAULT_PORT = 8080;



}
