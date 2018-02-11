package com.yzl.framework.beam.direct;

import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.rpc.URL;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class DirectUrlCallerSupport implements UrlChangeListener {

    private DirectUrlCaller directUrlCaller;
    private UrlRepository urlRepository;
    private String urlKey;
    private Map<String, String> parameters;

    public DirectUrlCallerSupport(DirectUrlCaller directUrlCaller,
                                  UrlRepository urlRepository,
                                  String urlKey,
                                  Map<String, String> parameters) {
        this.directUrlCaller = directUrlCaller;
        this.urlRepository = urlRepository;
        this.urlKey = urlKey;
        this.parameters = parameters;
        if (StringUtils.isNotBlank(urlKey)) {
            this.urlKey = urlKey;
        } else {
            this.urlKey = this.directUrlCaller.getInterfaceClass().getName();
        }
    }

    public void init() {
        URL url = urlRepository.getUrl(this.urlKey);
        if (url == null) {
            throw new BeamFrameworkException("Can't find url for interface " + directUrlCaller.getInterfaceClass().getName());
        }
        url.addParameters(parameters);
        this.directUrlCaller.init(url);
        this.urlRepository.subscribe(urlKey, this);
    }

    @Override
    public void onChange(String key, URL newUrl) {
        this.directUrlCaller.init(newUrl);
    }
}
