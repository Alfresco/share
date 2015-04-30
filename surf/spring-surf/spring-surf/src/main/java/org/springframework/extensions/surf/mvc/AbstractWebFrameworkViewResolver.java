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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.uri.UriTemplateListIndex;
import org.springframework.extensions.webscripts.DefaultURLHelper;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.URLHelper;
import org.springframework.extensions.webscripts.URLHelperFactory;
import org.springframework.extensions.webscripts.servlet.mvc.AbstractWebScriptViewResolver;

/**
 * <p>Abstract Spring MVC implementation of a view resolver for Surf.</p>
 * <p>Developers who wish to implement custom Spring view resolvers may
 * wish to extend their own view implementations from this class so
 * as to gain access to member convenience functions for working
 * with Surf services.</p>
 * 
 * @author muzquiano
 * @author David Draper
 * @author Kevin Roast
 */
public abstract class AbstractWebFrameworkViewResolver extends AbstractWebScriptViewResolver implements BeanNameAware
{
    private static Log logger = LogFactory.getLog(AbstractWebFrameworkViewResolver.class);
    
    private TemplatesContainer templatesContainer;
    
    private WebFrameworkConfigElement webframeworkConfigElement;    

    /** 
     * The web framework service registry.
     * @deprecated 
     */
    private WebFrameworkServiceRegistry webFrameworkServiceRegistry;

    private ModelObjectService modelObjectService;
    
    private RenderService webFrameworkRenderService;
    
    private ResourceService webFrameworkResourceService;
    
    /**
     * <p>The {@link URLHelperFactory} is used to construct {@link UrlHelper}s. If this is not set by the application 
     * context then the result will be that a {@link DefaultURLHelper} will only ever be created. It is only necessary
     * to set a specific {@link URLHelperFactory} if a custom {@link URLHelper} implementation is required.</p>
     */
    private URLHelperFactory urlHelperFactory;
    
    /**
     * <p>This variable will be set to the bean name set when defining this class as a Spring bean. It
     * is set by the <code>setBeanName</code> method which is defined by the <code>BeanNameAware</code>
     * interface that this class implements. The bean name is only used for identifying the bean in 
     * log messages to assist debugging.</p>
     */
    private String beanName;
    
    /**
     * <p>This is used to determine whether or not the view matches any of the templates that this view handler supports.</p>
     */
    private UriTemplateListIndex uriTemplateIndex = null;
    
    /**
     * <p>Gets the <code>UriTemplateListIndex</code> for the view resolver</p>
     * @return The <code>UriTemplateListIndex</code> for the view resolver
     */
    public UriTemplateListIndex getUriTemplateListIndex()
    {
        return this.uriTemplateIndex;
    }
    
    /**
     * <p>Sets the <code>UriTemplateListIndex</code></p>
     * @param uriTemplateIndex The <code>UriTemplateListIndex</code> to set.
     */
    public void setUriTemplateIndex(UriTemplateListIndex uriTemplateIndex)
    {
        this.uriTemplateIndex = uriTemplateIndex;
    }
    
    /**
     * <p>A list of the valid prefixes that can be used to identify the type of view. Each inheriting view resolver
     * will check the view against these prefixes to determine whether or not it can handle the request.</p>
     */
    private List<String> prefixes = new ArrayList<String>();
    
    /**
     * <p>A list of the valid request parameters that can be used to identify the type of view. Each inheriting view
     * resolver will check the view URI for these request parameters to determine whether or not it can handle the
     * request</p>
     */
    private List<String> reqParms = new ArrayList<String>();
    
    /**
     * Constructor
     */
    @SuppressWarnings({ "rawtypes" })
    public AbstractWebFrameworkViewResolver()
    {
        Class viewClass = requiredViewClass();
        setViewClass(viewClass);
    }
    
    public void addPrefix(String prefix)
    {
        this.prefixes.add(prefix);
    }
    
    public void addReqParm(String reqParm)
    {
        this.reqParms.add(reqParm);
    }
    
    /**
     * <p>Processes the supplied raw view String to return a value that does not include any prefixes. Each
     * extending view resolver can populate the <code>prefixes</code> list of prefixes that indicate that it can handle a view that
     * contains them. If the raw view begins with a specified prefix then the prefix is removed and the "processed"
     * String is returned to the caller.</p>
     * <p>Some view resolvers can also handle views when the view name is defined as a request parameter. The names
     * of these request parameters are added to the <code>reqParms</list> and if found in the URL then their value is returned
     * as the processed view</p>
     * <p>If no matching prefixes or request parameters are found then <code>null</code> is returned</p>
     *
     * @param viewName The name of the requested view.
     * @param prefixes The prefixes to check the view name for.
     * @param reqParms The request parameters to check the view name for.
     * @return A matching id or null if the view requested cannot be resolved to an id.
     */
    public String processView(String viewName)
    {
        String id = null;
        
        if (prefixes != null)
        {
            // Check the view name for matching prefixes...
            checkAllPrefixes: for (String currPrefix: prefixes)
            {
                if (viewName.startsWith(currPrefix))
                {
                    id = viewName.substring(currPrefix.length());
                    break checkAllPrefixes;
                }
            }
        }
        else
        {
            logger.warn("No valid prefixes were provided by class: " + this.getClass().getName());
        }
        
        if (reqParms != null)
        {
            // Check the view name for matching request parameters (the loop will only be entered
            // if an id has not already been resolved). Declare the RequestContext (required for
            // getting the parameters) but only if an id has not yet been found.
            RequestContext context = null;
            if (id == null) context = ThreadLocalRequestContext.getRequestContext();
            Iterator<String> reqParmIterator = reqParms.iterator();
            while(id == null && reqParmIterator.hasNext())
            {
                id = context.getParameter(reqParmIterator.next());
            }
        }
        else
        {
            logger.warn("No valid request parameters were provided by class: " + this.getClass().getName());
        }
        
        return id;
    }

    /**
     * Retrieves the page object with the given page id.
     *
     * If a page object doesn't exist, this supports the short-cut method of wiring directly to a template file on disk.
     * If a template file exists on disk with the same id as the page, we create in-memory disposal objects to represent
     * the page and the template instance required to arrive to that template.
     *
     * This provides a quick way to get started building templated sites.
     *
     * @param pageId
     *
     * @return Page object or null if not found
     */
    protected Page lookupPage(final String pageId)
    {
        Page page = modelObjectService.getPage(pageId);
        if (page == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Failed get page from ModelObjectService with id: " + pageId);
            
            // if a page object doesn't exist, we support a short-cut
            // if a template (ftl or php) exists on disk, we'll create disposable objects to represent the page and template
            TemplateProcessorRegistry templateProcessorRegistry = templatesContainer.getTemplateProcessorRegistry();
            String validTemplatePath = templateProcessorRegistry.findValidTemplatePath(pageId);
            if (validTemplatePath != null)
            {
                TemplateProcessor templateProcessor = templateProcessorRegistry.getTemplateProcessor(validTemplatePath);
                if (templateProcessor != null)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Found template that matches missing page id - will create temp page to match.");
                    
                    // we have both discovered a template as well as a template processor that can handle
                    // this path, auto-wire a dummy template instance to the template and wire that to a
                    // dummy page to allow processing to proceed without intermediate model objects
                    TemplateInstance templateInstance = modelObjectService.newTemplate(pageId);
                    templateInstance.setTemplateTypeId(pageId);
                    
                    page = modelObjectService.newPage(pageId);
                    page.setTemplateId(pageId);
                }
            }
        }
        
        return page;
    }

    /**
     * Performs a match of the given URI to a template. If a match is found, the variables representing
     * representing the tokens and the values extracted from the supplied URI are returned.
     *
     * @param uriTemplateIndex The <code>UriTemplateListIndex</code> to match the URI against.
     * @param uri The URI to compare against the template.
     *
     * @return map of tokens to values or null if no match found
     */
    public Map<String, String> matchUriTemplate(String uri)
    {
        Map<String,String> tokens = null;
        if (uriTemplateIndex != null)
        {
            tokens = uriTemplateIndex.findMatch(uri);
            
            // Set the tokens in the current request. If this method returns anything other than null
            // (which won't happen if this code block gets executed) then these tokens will be needed
            // in the associated view. Therefore they should be stored now to prevent them needed to be
            // generated again.
            ThreadLocalRequestContext.getRequestContext().setUriTokens(tokens);
        }
        return tokens;
    }

    /**
     * <p>Creates a <code>UriTemplateListIndex</code> populated with configuration data found in config elements
     * matching the target provided.</p>
     *
     * @param serviceRegistry service registry
     * @param targetElement The type of UriTemplate configurations to look for.
     *
     * @return map of tokens to values or null if no match found
     */
    public UriTemplateListIndex generateUriTemplateListIndexFromConfig(WebFrameworkServiceRegistry serviceRegistry, String targetElement)
    {
        UriTemplateListIndex uriTemplateIndex = null;
        Config config = serviceRegistry.getConfigService().getConfig("UriTemplate");
        if (config != null)
        {
            ConfigElement uriConfig = config.getConfigElement(targetElement);
            if (uriConfig != null)
            {
                uriTemplateIndex = new UriTemplateListIndex(uriConfig);
            }
        }
        return uriTemplateIndex;
    }

    /**
     * Determines whether or not this view resolver can handle the requested view.
     */
    @Override
    protected boolean canHandle(String viewName, Locale locale)
    {
        boolean canHandle = false;
        
        String uri = processView(viewName);
        if (uri != null)
        {
            // Update the URI to add a "/" prefix. This is necessary because the UriTemplate specification in JSR-311 requires
            // that all URIs must start with a forward slash, therefore all the component view templates start with a forward slash
            // so in order to find a match, the source URI must start with a forward slash...
            uri = "/" + uri;
            
            // match for tokens
            Map<String,String> uriTokens = matchUriTemplate(uri);
            if (uriTokens != null)
            {
                canHandle = true;
            }
        }
        
        return canHandle;
    }
    
    
    /* *************************************************************************************
     *                                                                                     *
     * ACCESSOR METHODS FOR SPRING PROPERTIES...                                           *
     *                                                                                     *
     ***************************************************************************************/
    
    public TemplatesContainer getTemplatesContainer()
    {
        return templatesContainer;
    }

    public void setTemplatesContainer(TemplatesContainer templatesContainer)
    {
        this.templatesContainer = templatesContainer;
    }
    
    public WebFrameworkConfigElement getWebframeworkConfigElement()
    {
        return webframeworkConfigElement;
    }

    public void setWebframeworkConfigElement(WebFrameworkConfigElement webframeworkConfigElement)
    {
        this.webframeworkConfigElement = webframeworkConfigElement;
    }

    /**
     * @return
     * @deprecated
     */
    public WebFrameworkServiceRegistry getWebFrameworkServiceRegistry()
    {
        return webFrameworkServiceRegistry;
    }
    
    /**
     * @deprecated This method only persists as it is used by DynamicPageViewResolver in the Web Quick Start application.
     * @return
     */
    public WebFrameworkServiceRegistry getServiceRegistry()
    {
        return webFrameworkServiceRegistry;
    }    

    public void setServiceRegistry(WebFrameworkServiceRegistry webFrameworkServiceRegistry)
    {
        this.webFrameworkServiceRegistry = webFrameworkServiceRegistry;
    }
    
    public ModelObjectService getModelObjectService()
    {
        return modelObjectService;
    }

    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }
    
    public RenderService getWebFrameworkRenderService()
    {
        return webFrameworkRenderService;
    }

    public void setWebFrameworkRenderService(RenderService webFrameworkRenderService)
    {
        this.webFrameworkRenderService = webFrameworkRenderService;
    }
    
    public ResourceService getWebFrameworkResourceService()
    {
        return webFrameworkResourceService;
    }

    public void setWebFrameworkResourceService(ResourceService webFrameworkResourceService)
    {
        this.webFrameworkResourceService = webFrameworkResourceService;
    }
    
    /**
     * <p>This method is required to implement the the <code>BeanNameAware</code> interface and will be
     * invoked by the Spring framework to set the configured name of this class when defined as a Spring
     * bean.</p>
     */
    public void setBeanName(String name)
    {
        this.beanName = name;    
    }
    
    /**
     * <p>Returns the id given to this class when it is defined as a Spring bean. If this class has not
     * bean instantiated by the Spring framework then this will return null.</p>
     * @return The "id" property given to this class when defining it as a Spring bean.
     */
    public String getBeanName()
    {        
        return this.beanName;
    }

    public URLHelperFactory getUrlHelperFactory()
    {
        return urlHelperFactory;
    }

    public void setUrlHelperFactory(URLHelperFactory urlHelperFactory)
    {
        this.urlHelperFactory = urlHelperFactory;
    }
}
