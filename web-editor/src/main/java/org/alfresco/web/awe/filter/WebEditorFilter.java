package org.alfresco.web.awe.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webeditor.taglib.TemplateConstants;

/**
 * This filter provides AWE-specific context to the Spring Web Editor
 * tag libraries.
 * 
 * @author gavinc
 * @author muzquiano
 */
public class WebEditorFilter implements Filter
{
    private static final Log logger = LogFactory.getLog(WebEditorFilter.class);

    public static final String DEFAULT_CONTEXT_PATH = "/awe";
    
    private static final String PARAM_CONTEXT_PATH = "contextPath";
    private static final String PARAM_DEBUG = "debug";
    
    private String urlPrefix;
    private boolean debugEnabled = Boolean.FALSE;

    /*
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException
    {
        // set up spring web editor tag library objects
        request.setAttribute(TemplateConstants.REQUEST_ATTR_KEY_WEF_ENABLED, Boolean.TRUE);
        request.setAttribute(TemplateConstants.REQUEST_ATTR_KEY_URL_PREFIX, this.urlPrefix);
        request.setAttribute(TemplateConstants.REQUEST_ATTR_KEY_DEBUG_ENABLED, this.debugEnabled);

        if (logger.isDebugEnabled())
        {
            logger.debug("Setup request for Web Editor: (urlPrefix: " + this.urlPrefix + 
                        ", debug: " + this.debugEnabled + ")");
        }

        chain.doFilter(request, response);
    }

    /*
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException
    {
        String contextPathParam = config.getInitParameter(PARAM_CONTEXT_PATH);
        if (contextPathParam != null && contextPathParam.length() > 0)
        {
            if (contextPathParam.startsWith("/") == false)
            {
                contextPathParam = "/" + contextPathParam;
            }
        }

        // TODO: Read host and port information from config and use
        // on URL is present, for now just use the context path
        // as we are mandating that AWE is on the same server.

        if (contextPathParam != null)
        {
            this.urlPrefix = contextPathParam;
        }
        else
        {
            this.urlPrefix = DEFAULT_CONTEXT_PATH;
        }

        String debug = config.getInitParameter(PARAM_DEBUG);
        if (debug != null && debug.equalsIgnoreCase("true"))
        {
            this.debugEnabled = Boolean.TRUE;
        }

        if (logger.isDebugEnabled())
            logger.debug("Initialised Web Editor: (urlPrefix: " + this.urlPrefix + ", debug: " + this.debugEnabled + ")");
    }

    /*
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy()
    {
        // nothing to do
    }
}