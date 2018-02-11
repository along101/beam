package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.common.URLParamType;
import com.yzl.framework.beam.rpc.Provider;
import com.yzl.framework.beam.rpc.URL;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractServletEndpoint extends HttpServlet implements Endpoint {
    @Getter
    protected Map<String, Provider<?>> providers = new ConcurrentHashMap<>();

    @Getter
    protected URL baseUrl;

    AbstractServletEndpoint(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public URL export(Provider<?> provider, URL serviceUrl) {
        URL newServiceUrl = doExport(provider, serviceUrl);
        String key = getProviderKey(provider.getInterface().getName(), newServiceUrl.getParameter(URLParamType.version.name()));
        providers.put(key, provider);
        return newServiceUrl;
    }

    protected URL doExport(Provider<?> provider, URL serviceUrl) {
        URL newServiceUrl = baseUrl.createCopy();
        String basePath = StringUtils.removeEnd(baseUrl.getPath(), BeamConstants.PATH_SEPARATOR);
        newServiceUrl.setProtocol(serviceUrl.getProtocol());
        newServiceUrl.setPath(provider.getInterface().getName());
        newServiceUrl.getParameters().putAll(serviceUrl.getParameters());
        newServiceUrl.addParameter(URLParamType.basePath.name(), basePath);
        return newServiceUrl;
    }

    protected String getProviderKey(String interfaceName, String version) {
        return StringUtils.isBlank(version) ? interfaceName : interfaceName + "?version=" + version;
    }
}
