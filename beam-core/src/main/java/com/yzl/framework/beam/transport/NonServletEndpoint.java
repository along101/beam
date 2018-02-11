package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.rpc.Provider;
import com.yzl.framework.beam.rpc.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * 空的ServletEndpoint，用于客户端调用构造协议
 */
public class NonServletEndpoint extends AbstractServletEndpoint {
    private static final String MESSAGE = "NonServletEndpoint can not invoke method.";

    public NonServletEndpoint() {
        super(null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public Map<String, Provider<?>> getProviders() {
        return super.getProviders();
    }

    @Override
    public void init() {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public URL export(Provider<?> provider, URL serviceUrl) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected URL doExport(Provider<?> provider, URL serviceUrl) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public void destroy() {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public String getInitParameter(String name) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public ServletConfig getServletConfig() {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public ServletContext getServletContext() {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public String getServletInfo() {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public void init(ServletConfig config) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public void log(String msg) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public void log(String message, Throwable t) {
        throw new BeamFrameworkException(MESSAGE);
    }

    @Override
    public String getServletName() {
        throw new BeamFrameworkException(MESSAGE);
    }
}
