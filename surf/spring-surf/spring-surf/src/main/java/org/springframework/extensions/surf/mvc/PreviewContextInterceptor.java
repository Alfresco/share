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

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement.RuntimeConfigDescriptor;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.ThreadLocalPreviewContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

public class PreviewContextInterceptor extends AbstractWebFrameworkInterceptor 
{
    private static Log logger = LogFactory.getLog(PreviewContextInterceptor.class);
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#preHandle(org.springframework.web.context.request.WebRequest)
     */
    public void preHandle(WebRequest webRequest) throws Exception
    {
        // only do this if we're in preview mode
        if (getWebFrameworkConfiguration().isPreviewEnabled())
        {
            // values we'd like to ascertain
            String storeId = null;
            String webappId = null;
            
            // if we have a session, lets try to pull values from there
            HttpSession session = ServletUtil.getSession(false);
            if (session != null)
            {
                storeId = (String) session.getAttribute(WebFrameworkConstants.STORE_ID_SESSION_ATTRIBUTE_NAME);
                webappId = (String) session.getAttribute(WebFrameworkConstants.WEBAPP_ID_SESSION_ATTRIBUTE_NAME);
            }
                        
            // if we didn't find a value, let's see if there are any defaults set
            if (storeId == null)
            {
                String runtimeId = getWebFrameworkConfiguration().getAutowireRuntimeId();
                if (runtimeId != null)
                {
                    RuntimeConfigDescriptor runtimeConfigDescriptor = getWebFrameworkConfiguration().getRuntimeConfigDescriptor(runtimeId);
                    if (runtimeConfigDescriptor != null)
                    {
                        storeId = runtimeConfigDescriptor.getStoreId();
                        if (storeId != null && storeId.length() > 0)
                        {
                            webappId = runtimeConfigDescriptor.getWebappId();
                            if (webappId != null && webappId.length() == 0)
                            {
                                webappId = null;
                            }
                        }
                    }
                }
            }    
                        
            // if we now have values, push down onto request context
            if (storeId != null)
            {
                ThreadLocalPreviewContext sandboxContext = new ThreadLocalPreviewContext(storeId, webappId);

                // debug
                if (logger.isDebugEnabled())
                {
                    logger.debug("Context[" + session.getId() + "] storeId = " + sandboxContext.getPreviewContext().getStoreId());
                    logger.debug("Context[" + session.getId() + "] webappId = " + sandboxContext.getPreviewContext().getWebappId());
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#postHandle(org.springframework.web.context.request.WebRequest, org.springframework.ui.ModelMap)
     */
    public void postHandle(WebRequest request, ModelMap model) throws Exception
    {
        // cleanup if we're in preview mode
        if (getWebFrameworkConfiguration().isPreviewEnabled())
        {
            // release the thread local variable
            ThreadLocalPreviewContext sandboxContext = ThreadLocalPreviewContext.getPreviewContext();
            if (sandboxContext != null)
            {
                sandboxContext.release();
            }
        }        
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#afterCompletion(org.springframework.web.context.request.WebRequest, java.lang.Exception)
     */
    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
    }
}