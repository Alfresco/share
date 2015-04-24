/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.surf.mvc;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.ThreadLocalPreviewContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for managing requests for preview.
 * 
 * This allows the previewed sandbox and webapp to be specified via a
 * URL for the current session.  It then performs a redirect to the
 * Surf application.
 * 
 * Incoming URLs can be of the form:
 * 
 *    /preview/{sandboxId}
 *    /preview/{sandboxId}/{webappId}
 *    /preview?alfStoreId={sandboxId}
 *    /preview?alfStoreId={sandboxId}&alfWebappId={webappId}
 *    /preview?s={sandboxId}
 *    /preview?s={sandboxId}&w={webappId}
 * 
 * @author muzquiano
 */
public class PreviewController extends AbstractWebFrameworkController
{
    private static Log logger = LogFactory.getLog(PreviewController.class);
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractWebFrameworkController#getLogger()
     */
    public Log getLogger()
    {
        return logger;        
    }
    
    /**
     * <p>The <code>FrameworkUtil</code> is needed for resetting WebScripts. It is defined as a Spring Bean and is instantiated
     * and set by the Spring Framework.</p>
     */
    private FrameworkBean frameworkUtil;
        
    /**
     * <p>Setter required by the Spring Framework to set the <code>FrameworkUtil</code> bean used for resetting WebScripts</p>
     * @param frameworkUtil
     */
    public void setFrameworkUtil(FrameworkBean frameworkUtil)
    {
        this.frameworkUtil = frameworkUtil;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractWebFrameworkController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView createModelAndView(HttpServletRequest request, HttpServletResponse response)
    {
        // This servlet will only operate if the Web Framework instance
        // is in preview mode
        if (this.getWebFrameworkConfiguration().isPreviewEnabled())
        {
            boolean updated = false;
            
            // values we want to determine
            String storeId = null;
            String webappId = null;
            
            // try to determine values by parsing tokens
            String uri = request.getRequestURI();
            
            // skip server context path and build the path to the resource we are looking for
            uri = uri.substring(request.getContextPath().length());
            
            // validate and return the resource path - stripping the servlet context
            StringTokenizer t = new StringTokenizer(uri, "/");
            String servletName = t.nextToken();
            if (t.hasMoreTokens())
            {
                storeId = t.nextToken();
            }
            if (t.hasMoreTokens())
            {
                webappId = t.nextToken();
            }
            
            
            // try to determine values by looking at request parameters
            String _storeId = (String) request.getParameter(WebFrameworkConstants.STORE_ID_REQUEST_PARAM_NAME);
            if (_storeId == null)
            {
                _storeId = (String) request.getParameter("s");                
            }
            if (_storeId != null)
            {
                storeId = _storeId;
            }
            String _webappId = (String) request.getParameter(WebFrameworkConstants.WEBAPP_ID_REQUEST_PARAM_NAME);
            if (_webappId == null)
            {
                _webappId = (String) request.getParameter("w");
            }
            if (_webappId != null)
            {
                webappId = _webappId;
            }
                        
            // set values onto session
            if (storeId != null)
            {
                updated = true;
                request.getSession(true).setAttribute(WebFrameworkConstants.STORE_ID_SESSION_ATTRIBUTE_NAME, storeId);                
            }
            if (storeId != null && webappId != null)
            {
                updated = true;
                request.getSession(true).setAttribute(WebFrameworkConstants.WEBAPP_ID_SESSION_ATTRIBUTE_NAME, webappId);
            }
            
            
            // if updated, then lets do a full refresh
            if (updated)
            {
                // set onto thread local
                ThreadLocalPreviewContext sandboxContext = new ThreadLocalPreviewContext(storeId, webappId);
                
                // refresh the web scripts
                this.frameworkUtil.resetWebScripts();
            }

            // send redirect
            try {
                response.sendRedirect(request.getContextPath());
            }
            catch (IOException ioe){ }
        }
        
        return null;
    }
}
