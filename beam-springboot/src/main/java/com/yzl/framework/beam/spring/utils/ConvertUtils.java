package com.yzl.framework.beam.spring.utils;

import java.util.HashMap;
import java.util.Map;

public class ConvertUtils {

    public static Map<String, String> convertMap(Map<String, Object> annotationAttributes) {
        Map<String, String> map = new HashMap<>();
        for (String key : annotationAttributes.keySet()) {
            String value = annotationAttributes.get(key).toString();
            map.put(key, value);
        }
        return map;
    }
}
