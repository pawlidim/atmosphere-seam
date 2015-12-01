package org.atmosphere.seam;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.container.JBossWebCometSupport;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereFrameworkInitializer;
import org.atmosphere.cpr.AtmosphereRequestImpl;
import org.atmosphere.cpr.AtmosphereResponseImpl;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.servlet.ServletApplicationMap;
import org.jboss.servlet.http.HttpEvent;
import org.jboss.servlet.http.HttpEventServlet;

/**
 * This servlet supports Seam Component in Atmosphere Managedserivce it has been
 * tested on JBoss eap6.2 and seam 2.3
 * 
 * @author Omid Pourhadi
 *
 */
public class AtmosphereSeamServlet extends HttpServlet implements HttpEventServlet
{

    private ServletContext context;

    protected final AtmosphereFrameworkInitializer initializer;

    public AtmosphereSeamServlet()
    {
        this(false);
    }

    /**
     * Create an Atmosphere Servlet.
     *
     * @param isFilter
     *            true if this instance is used as an
     *            {@link org.atmosphere.cpr.AtmosphereFilter}
     */
    public AtmosphereSeamServlet(boolean isFilter)
    {
        this(isFilter, true);
    }

    /**
     * Create an Atmosphere Servlet.
     *
     * @param isFilter
     *            true if this instance is used as an
     *            {@link org.atmosphere.cpr.AtmosphereFilter}
     * @param autoDetectHandlers
     */
    public AtmosphereSeamServlet(boolean isFilter, boolean autoDetectHandlers)
    {
        initializer = new AtmosphereFrameworkInitializer(isFilter, autoDetectHandlers);
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        context = config.getServletContext();
        try
        {
            Lifecycle.setupApplication(new ServletApplicationMap(context));
            configureFramework(config, true);
            super.init(config);
        }
        finally
        {
            Lifecycle.cleanupApplication();
        }
    }

    protected void configureFramework(ServletConfig sc, boolean init) throws ServletException
    {
        initializer.configureFramework(sc, init, false, AtmosphereFramework.class);
    }

    @Override
    public void destroy()
    {
        initializer.destroy();
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}.
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doHead(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}.
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doTrace(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}.
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}.
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}.
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doPost(req, res);
    }

    /**
     * Delegate the request processing to an instance of
     * {@link org.atmosphere.cpr.AsyncSupport}.
     *
     * @param req
     *            the {@link javax.servlet.http.HttpServletRequest}
     * @param res
     *            the {@link javax.servlet.http.HttpServletResponse}
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        final HttpServletRequest request = req;
        final HttpServletResponse resp = res;
        new ContextualHttpServletRequest(request) {

            @Override
            public void process() throws Exception
            {
                initializer.framework().doCometSupport(AtmosphereRequestImpl.wrap(request), AtmosphereResponseImpl.wrap(resp));
            }
        }.run();
    }

    public void event(HttpEvent event) throws IOException, ServletException
    {
        event.getHttpServletRequest().setAttribute(JBossWebCometSupport.HTTP_EVENT, event);
    }

}
