package com.yzl.framework.beam.transport;

import com.yzl.framework.beam.common.BeamConstants;
import com.yzl.framework.beam.exception.BeamFrameworkException;
import com.yzl.framework.beam.rpc.URL;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyServletEndpoint extends ServletEndpoint {

    @Getter
    private Server server;

    public JettyServletEndpoint(URL baseUrl) {
        super(baseUrl);
        initJettyServer();
    }

    private void initJettyServer() {
        server = new Server(baseUrl.getPort());
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        servletContextHandler.addServlet(DefaultServlet.class, "/");
        ServletHolder servletHolder = new ServletHolder(this);
        String pathSpec = StringUtils.removeEnd(baseUrl.getPath(), BeamConstants.PATH_SEPARATOR) + BeamConstants.PATH_SEPARATOR + "*";
        servletContextHandler.addServlet(servletHolder, pathSpec);
        try {
            server.start();
        } catch (Exception e) {
            throw new BeamFrameworkException("Start jetty server error.", e);
        }
    }
}
