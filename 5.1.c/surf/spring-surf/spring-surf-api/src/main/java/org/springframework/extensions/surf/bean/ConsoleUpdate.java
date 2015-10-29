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

package org.springframework.extensions.surf.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.site.CacheUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

/**
 * Handles POST processing for console reset commands
 * 
 * @author muzquiano
 */
public class ConsoleUpdate extends DeclarativeWebScript implements ApplicationContextAware
{
    private WebFrameworkServiceRegistry serviceRegistry;
    private ApplicationContext applicationContext;
    
    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        List<String> tasks = new ArrayList<String>();
        
        // actions
        boolean resetWebscripts = false;
        boolean resetTemplates = false;
        boolean resetObjects = false;

        // reset index
        String reset = req.getParameter("reset");
        
        if ("webscripts".equalsIgnoreCase(reset))
        {
            resetWebscripts = true;
        }
        if ("templates".equalsIgnoreCase(reset))
        {
            resetTemplates = true;
        }
        if ("objects".equalsIgnoreCase(reset))
        {
            resetObjects = true;
        }
        if ("all".equalsIgnoreCase(reset))
        {
            resetWebscripts = true;
            resetTemplates = true;
            resetObjects = true;
        }
        
        // web script resets
        if (resetWebscripts)
        {
            // reset list of web scripts
            int previousCount = getContainer().getRegistry().getWebScripts().size();
            int previousFailures = getContainer().getRegistry().getFailures().size();
            getContainer().reset();
            tasks.add("Reset Web Scripts Registry; registered " + getContainer().getRegistry().getWebScripts().size() + " Web Scripts.  Previously, there were " + previousCount + ".");
            int newFailures = getContainer().getRegistry().getFailures().size();
            if (newFailures != 0 || previousFailures != 0)
            {
                tasks.add("Warning: found " + newFailures + " broken Web Scripts.  Previously, there were " + previousFailures + ".");
            }
        }
        
        // template resets
        if (resetTemplates)
        {
            TemplatesContainer container = serviceRegistry.getTemplatesContainer();
            container.reset();
            
            tasks.add("Reset Templates Registry.");
        }
        
        // surf registry
        if (resetObjects)
        {
            RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            CacheUtil.invalidateModelObjectServiceCache(rc);
            
            // we must reset the SpringMVC view resolvers - as they maintain a reference to View
            // object which could themselves reference pages or templates by ID
            Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, ViewResolver.class, true, false);
            for (ViewResolver resolver : matchingBeans.values())
            {
                if (resolver instanceof AbstractCachingViewResolver)
                {
                    ((AbstractCachingViewResolver)resolver).clearCache();
                }
            }
            
            tasks.add("Reset Surf Objects Registry.");
        }
        
        // create model for rendering
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        model.put("tasks", tasks);
        model.put("webscripts", getContainer().getRegistry().getWebScripts());
        model.put("failures", getContainer().getRegistry().getFailures());
        return model;
    }

}
