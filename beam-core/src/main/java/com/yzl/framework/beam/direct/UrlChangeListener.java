package com.yzl.framework.beam.direct;

import com.yzl.framework.beam.rpc.URL;

public interface UrlChangeListener {

    void onChange(String key, URL newUrl);
}
