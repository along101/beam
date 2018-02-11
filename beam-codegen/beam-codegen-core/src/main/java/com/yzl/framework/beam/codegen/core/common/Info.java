package com.yzl.framework.beam.codegen.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Info {

    private static volatile Info info;
    private static String INFO_FILE = "META-INF/beam-codegen.properties";

    public static Info getInstance() throws Exception {
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

    private Info() throws IOException {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(INFO_FILE)) {
            Properties p = new Properties();
            p.load(in);
            this.version = p.getProperty("version");
        }
    }

    public String getVersion() {
        return version;
    }
}
