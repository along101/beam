package com.yzl.framework.beam.spring.autoconfig;

import com.yzl.framework.beam.direct.DirectUrlCaller;
import com.yzl.framework.beam.direct.DirectUrlCallerSupport;
import com.yzl.framework.beam.direct.UrlRepository;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.proxy.DirectInvocationHandler;
import com.yzl.framework.beam.proxy.JdkProxyFactory;
import com.yzl.framework.beam.rpc.Protocol;
import com.yzl.framework.beam.rpc.URL;
import com.yzl.framework.beam.util.ReflectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeamUrlClientManager {

    @Getter
    @Setter
    private Protocol protocol;
    @Getter
    @Setter
    private UrlRepository urlRepository;
    @Getter
    @Setter
    private String urlKeyPrefix = "";

    public BeamUrlClientManager(Protocol protocol, UrlRepository urlRepository) {
        this.protocol = protocol;
        this.urlRepository = urlRepository;
    }

    private Map<String, Object> clients = new ConcurrentHashMap<>();

    public Object getOrCreateClientProxy(String interfaceName, String urlKey, Map<String, String> parameters) {
        urlKey = checkAndGetUrlKey(interfaceName, urlKey);
        URL url = getURL(urlKey, parameters);
        String urlFullStr = url.toFullStr();
        Object proxy = clients.get(urlFullStr);
        if (proxy == null) {
            proxy = createClientProxy(interfaceName, urlKey, parameters);
            clients.put(urlFullStr, proxy);
        }
        return proxy;
    }

    public URL getURL(String urlKey, Map<String, String> parameters) {
        URL url = parseUrl(urlKey);
        if (url == null) {
            url = urlRepository.getUrl(urlKey);
        }
        url.addParameters(parameters);
        url.addParameter("urlKey", urlKey);
        return url;
    }

    public URL parseUrl(String urlKey) {
        URL url = URL.valueOf(urlKey);
        if (StringUtils.isNotBlank(url.getProtocol()) && StringUtils.isNotBlank(url.getHost())) {
            return url;
        }
        return null;
    }

    protected String checkAndGetUrlKey(String interfaceName, String urlKey) {
        if (StringUtils.isBlank(urlKey)) {
            urlKey = urlKeyPrefix + interfaceName;
        }
        return urlKey;
    }

    public Object createClientProxy(String interfaceName, String urlKey, Map<String, String> parameters) {
        Class<?> clazz;
        try {
            clazz = ReflectUtil.forName(interfaceName);
        } catch (ClassNotFoundException e) {
            throw new BeamFrameworkException("Can't find class " + interfaceName);
        }

        urlKey = checkAndGetUrlKey(interfaceName, urlKey);

        DirectUrlCaller<?> directUrlCaller;

        URL url = parseUrl(urlKey);
        //直接配置的是url
        if (url != null) {
            url.addParameters(parameters);
            directUrlCaller = new DirectUrlCaller<>(clazz, protocol);
            directUrlCaller.init(url);
        } else {
            //配置的是urlKey，从urlRepository中获取
            directUrlCaller = new DirectUrlCaller<>(clazz, protocol);
            DirectUrlCallerSupport support = new DirectUrlCallerSupport(directUrlCaller, this.urlRepository, urlKey, parameters);
            support.init();
        }

        DirectInvocationHandler<?> invocationHandler = new DirectInvocationHandler<>(directUrlCaller);
        return new JdkProxyFactory().getProxy(clazz, invocationHandler);
    }

}
