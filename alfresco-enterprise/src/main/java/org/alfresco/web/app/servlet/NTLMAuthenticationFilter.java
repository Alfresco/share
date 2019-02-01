/*
 * #%L
 * Alfresco Enterprise Remote API
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 * #L%
 */
package org.alfresco.web.app.servlet;

import org.alfresco.repo.web.auth.WebCredentials;
import org.alfresco.repo.webdav.auth.AuthenticationDriver;
import org.alfresco.repo.webdav.auth.BaseNTLMAuthenticationFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Web-client NTLM Authentication Filter Class
 * 
 * @author GKSpencer
 */
public class NTLMAuthenticationFilter extends BaseNTLMAuthenticationFilter
{
    // Debug logging
    private static Log logger = LogFactory.getLog(NTLMAuthenticationFilter.class);

    
	/* (non-Javadoc)
	 * @see org.alfresco.repo.webdav.auth.BaseNTLMAuthenticationFilter#init()
	 */
	@Override
    protected void init() throws ServletException
    {
        // Call the base NTLM filter initialization
        super.init();
        
        // Use the web client user attribute name
        setUserAttributeName(AuthenticationDriver.AUTHENTICATION_USER);      
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.webdav.auth.BaseSSOAuthenticationFilter#onValidateFailed(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
     */
    @Override
    protected void onValidateFailed(ServletContext sc, HttpServletRequest req, HttpServletResponse res, HttpSession session, WebCredentials credentials)
        throws IOException
    {
        super.onValidateFailed(sc, req, res, session, credentials);
        
        // Redirect to the login page if user validation fails
    	redirectToLoginPage(req, res);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.webdav.auth.BaseNTLMAuthenticationFilter#onLoginComplete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected boolean onLoginComplete(ServletContext sc, HttpServletRequest req, HttpServletResponse res, boolean userInit)
        throws IOException
    {
        String requestURI = req.getRequestURI();
        return true;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.webdav.auth.BaseSSOAuthenticationFilter#writeLoginPageLink(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override protected void writeLoginPageLink(ServletContext context, HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String redirectURL = req.getRequestURI();
        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final PrintWriter out = resp.getWriter();
        out.println("<html><head>");
        // Remove the auto refresh to avoid refresh loop, MNT-16931
        //         out.println("<meta http-equiv=\"Refresh\" content=\"0; url=" + redirectURL + "\">");
        // out.println("</head><body><p>Please <a href=\"" + redirectURL + "\">log in</a>.</p>");
        // MNT-20200 (LM-190130): Sanitise url on anchor
        out.println("</head><body>");
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS);
        out.println(policy.sanitize("<p>Please <a href=\"" + redirectURL + "\">log in</a>.</p>"));
        out.println("</body></html>");
        out.close();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.webdav.auth.BaseNTLMAuthenticationFilter#getLogger()
     */
    @Override
    final protected Log getLogger()
    {
        return logger;
    }
}
