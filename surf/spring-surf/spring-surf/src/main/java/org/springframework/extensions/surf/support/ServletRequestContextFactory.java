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

package org.springframework.extensions.surf.support;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.LinkBuilderFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModuleHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

/**
 * Constructs a RequestContext for a servlet web request
 *
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class ServletRequestContextFactory extends AbstractRequestContextFactory
{
    /**
     * @deprecated This has been deprecated because we should avoid loading beans from the 
     * application context directly. Instead we should be relying on Spring to be configured
     * to inject the LinkBuilderFactory via the <code>setLinkBuilderFactory</code> method.
     */
    private static final String SERVLET_LINKBUILDER_FACTORY_ID = "webframework.factory.linkbuilder.servlet";
    
    /**
     * <p>A <code>LinkBuilder</code> is required for each <code>ServletRequest</code> that this
     * factory instantiates. The application context should be configured so that a <code>LinkBuilderFactory</code>
     * is set as a property of this <code>ServletRequestContextFactory</code>.
     */
    private LinkBuilderFactory linkBuilderFactory = null;
        
    /**
     * <p>This method has been provided to allow Spring to inject the required <code>LinkBuilderFactory</code>.
     * @param linkBuilderFactory
     */
    public void setLinkBuilderFactory(LinkBuilderFactory linkBuilderFactory)
    {
        this.linkBuilderFactory = linkBuilderFactory;
    }

    private ExtensibilityModuleHandler extensibilityModuleHandler = null;

    public void setExtensibilityModuleHandler(ExtensibilityModuleHandler extensibilityModuleHandler)
    {
        this.extensibilityModuleHandler = extensibilityModuleHandler;
    }
    
    private DependencyHandler dependencyHandler = null;
    
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }

    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    private ConfigService configService;
    
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContextFactory#canHandle(org.springframework.web.context.request.WebRequest)
     */
    public boolean canHandle(WebRequest webRequest)
    {
        return (webRequest instanceof DispatcherServletWebRequest);
    }
    
    /**
     * <p>Creates the link builder to be used by this request context factory.</p>
     * 
     * @return link builder
     * @deprecated Use Spring configuration to set a new <code>LinkBuilder</code> rather than calling (or overriding) this method.
     */
    public LinkBuilder createLinkBuilder()
    {
        ServletLinkBuilderFactory linkBuilderFactory = (ServletLinkBuilderFactory)this.getApplicationContext().getBean(SERVLET_LINKBUILDER_FACTORY_ID);
        LinkBuilder linkBuilder = linkBuilderFactory.newInstance();
        return linkBuilder;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.support.AbstractRequestContextFactory#newInstance(org.springframework.web.context.request.WebRequest)
     */
    @SuppressWarnings("unchecked")
    public RequestContext newInstance(WebRequest webRequest)
        throws RequestContextException
    {
        if (!(webRequest instanceof ServletWebRequest))
        {
            throw new RequestContextException("ServletRequestContextFactory can only produce RequestContext instances for ServletWebRequest objects.");
        }
        
        ServletWebRequest dispatcherRequest = (ServletWebRequest) webRequest;
        HttpServletRequest request = dispatcherRequest.getRequest();
        
        try
        {
            request.setCharacterEncoding("UTF-8");
        }
        catch (UnsupportedEncodingException encErr)
        {
            // use default is this occurs
        }
        
        // Defensive code - we should ideally be using Spring to provide us with a LinkBuilderFactory. However, for backwards compatibility
        // with any custom configuration adopted from the original Spring Surf configuration we will call the deprecated method if 
        // a LinkBuilderFactory has not been provided.
        LinkBuilder linkBuilder;
        if (this.linkBuilderFactory == null)
        {
            linkBuilder = createLinkBuilder();
        }
        else
        {
            linkBuilder = linkBuilderFactory.newInstance();
        }
        
        // Create the servlet request context
        ServletRequestContext context = buildServletRequestContext(linkBuilder);
        webRequest.setAttribute(RequestContext.ATTR_REQUEST_CONTEXT, context, WebRequest.SCOPE_REQUEST);        
        
        // method
        context.method = request.getMethod();
        
        // scheme
        context.scheme = request.getScheme();
        
        // context path
        context.contextPath = request.getContextPath();
        
        // Set the servlet context path - this will typically be the path of the Spring MVC request dispatcher...
        context.setServletContextPath(request.getServletPath());
        
        // uri
        context.uri = request.getRequestURI();
        
        // Set the extensibility handler for the request...
        context.setExtensibilityModuleHandler(this.extensibilityModuleHandler);
        context.setDependencyHandler(this.dependencyHandler);
        context.setWebFrameworkConfigElement(this.webFrameworkConfigElement);
        context.setConfigService(this.configService);
        
        // Copy in request parameters
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String parameterName = (String) parameterNames.nextElement();
            String parameterValue = request.getParameter(parameterName);
            if (parameterValue != null)
            {
                context.parametersMap.put(parameterName, parameterValue);
            }
        }
        
        // Copy in request attributes
        Enumeration attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements())
        {
            String attributeName = (String) attributeNames.nextElement();
            Object attributeValue = request.getAttribute(attributeName);
            if (attributeValue != null && attributeValue instanceof Serializable)
            {
                context.attributesMap.put(attributeName, (Serializable)attributeValue);
            }
        }
        
        // Copy in request headers
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String headerName = (String) headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerValue != null)
            {
                context.headersMap.put(headerName, headerValue);
            }
        }
        
        return context;
    }
    
    protected ServletRequestContext buildServletRequestContext(LinkBuilder linkBuilder)
    {
        return new ServletRequestContext(webFrameworkServiceRegistry, frameworkUtils, linkBuilder);
    }
}