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

package org.springframework.extensions.surf.site;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.Page;

/**
 * Helper functions for web sites
 * 
 * @author muzquiano
 */
public class SiteUtil
{
    private static Log logger = LogFactory.getLog(FrameworkBean.class);
    
    private static final String DEFAULT_SITE_CONFIGURATION_ID = "default.site.configuration";

    /**
     * Returns the root page for the current request context
     * 
     * @param context the context
     * 
     * @return the root page
     */
    public static Page getRootPage(RequestContext context)
    {
        return getRootPage(context, getSiteConfiguration(context));        
    }
    
    /**
     * Returns the root page for the given site configuration
     * 
     * @param context
     * @param siteConfiguration
     * 
     * @return the root page instance
     */
    public static Page getRootPage(RequestContext context, Configuration siteConfiguration)
    {
        Page rootPage = null;
        
        // check the site configuration
        if (siteConfiguration != null)
        {
            String rootPageId = siteConfiguration.getProperty("root-page");
            if (rootPageId != null)
            {
                Page page = context.getObjectService().getPage(rootPageId);
                if (page != null)
                {
                    rootPage = page;
                }
            }
        }
                
        return rootPage;
    }

    /**
     * Returns the site configuration object to use for the current request.
     * 
     * At present, this is a very simple calculation since we either look to
     * the current application default site id or we use a default.
     * 
     * In the future, we will seek to support multiple site configurations
     * per web application (i.e. one might be for html, another for wireless,
     * another for print channel).
     * 
     * @param context the context
     * 
     * @return the site configuration
     */
    public static Configuration getSiteConfiguration(RequestContext context)
    {
        // try to load the site configuration id specified by the application default
        String siteConfigId = getConfig().getDefaultSiteConfigurationId();
        
        Configuration configuration = (Configuration) context.getObjectService().getConfiguration(siteConfigId);
        if (configuration == null)
        {
            // if nothing was found, try to load the "stock" configuration id
            if (!DEFAULT_SITE_CONFIGURATION_ID.equals(siteConfigId))
            {
                siteConfigId = DEFAULT_SITE_CONFIGURATION_ID;
                configuration = (Configuration) context.getObjectService().getConfiguration(siteConfigId);
            }
            
            if (configuration == null)
            {
                // if we still haven't found an object, then we can do an exhaustive lookup
                // this is a last resort effort to find the site config object                
                Map<String,ModelObject> configs = context.getObjectService().findConfigurations("site");
                if (configs != null && configs.size() > 0)
                {
                    configuration = (Configuration) configs.values().iterator().next();
                    
                    if (configuration != null && logger.isDebugEnabled())
                        logger.debug("Site configuration '" + configuration.getId() + "' discovered via exhaustive lookup.  Please adjust configuration files to optimize performance.");
                }                
            }
        }
        
        return configuration;
    }
        
    /**
     * Returns the web framework configuration element
     * 
     * @return the config
     */
    protected static WebFrameworkConfigElement getConfig()
    {
        return FrameworkUtil.getConfig();
    }
}