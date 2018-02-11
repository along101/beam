package com.yzl.framework.beam.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Data
@Slf4j
public class Info {

    private static volatile Info info;
    private static String INFO_FILE = "META-INF/beam-info.properties";

    public static Info getInstance() {
        if (info != null) {
            return info;
        }
        synchronized (Info.class) {
            if (info != null) {
                return info;
            }
            info = new Info();
        }
        return info;
    }

    private String version;
    private String appId;
    private String metricPrefix = BeamConstants.METRIC_NAME;

    private Info() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(INFO_FILE)) {
            Properties p = new Properties();
            p.load(in);
            this.version = p.getProperty("version");
        } catch (Exception e) {
            log.error("Can not load info", e);
        }
    }
}
