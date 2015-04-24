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
package org.springframework.extensions.config;

import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.support.DefaultUserFactory;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.extensions.webscripts.TemplateConfigModel;
import org.springframework.web.context.ServletContextAware;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.ObjectWrapper;

/**
 * 
 * @author David Draper
 */
public class ConfigBeanFactory implements ApplicationContextAware, ServletContextAware
{
    /**
     * <p>A <code>ConfigService</code> instance is required to obtain the <code>RemoteConfigElement</code> and
     * <code>WebFrameworkConfigElement</code>. An instance needs to be provided through Spring configuration.</p>
     */
    private ConfigService configService;

    /**
     * <p>This method is provided so that the Spring Framework can set the <code>ConfigService</code> required to 
     * obtain the <code>RemoteConfigElement</code> and <code>WebFrameworkConfigElement</code>.
     * @param configService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * <p>The <code>ApplicationContext</code> is required to obtain the <code>UserFactory</code> returned by this Bean Factory</p>
     */
    private ApplicationContext applicationContext;
    
    /**
     * <p>This method is provided so that the Spring Framework can set the <code>ApplicationContext</code> required to
     * obtain a <code>UserFactory</code></p>
     * 
     * @param applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    /**
     * <p>The <code>ServletContext</code> is required to instantiate the <code>TagLibFactory</code> returned by the <code>getTagLibFactory</code>
     * method. It is set in the associated setter method because this class implements the <code>ServletContextAware</code> interface.</p> 
     */
    private ServletContext servletContext;
    
    /**
     * <p>This method is provided so that the Spring Framework can set the <code>ServletContext</code> required to instantiate a
     * <code>TagLibFactory</code></p>
     * @param servletContext
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    /**
     *<p>A local copy of the <code>WebFrameworkConfigElement</code> is maintained so that it does not need to be repeatedly
     *retrieved. This is instantiated by the <code>getWebFrameworkConfig</code> method the first time that it is called.</p>
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    /**
     * <p>A factory method made available to Spring to generate a <code>RemoteConfigElement</code> Spring Bean</p>
     * @return The "remote" element from the "Remote" Config returned by the <code>ConfigService</code>
     */
    public RemoteConfigElement getRemoteConfig()
    {
        Config remoteConfig = configService.getConfig("Remote");
        RemoteConfigElement remoteConfigElement = (RemoteConfigElement) remoteConfig.getConfigElement("remote");
        return remoteConfigElement;
    }
    
    /**
     * <p>A factory method made available to Spring to generate a <code>WebFrameworkConfigElement</code> Spring Bean</p>
     * @return The "web-framework" element from the "WebFramework" Config returned by the <code>ConfigService</code>
     */
    public WebFrameworkConfigElement getWebFrameworkConfig()
    {
        if (this.webFrameworkConfigElement != null)
        {
            // No action required, the WebFrameworkConfigElement has already been obtained from the ConfigService.
        }
        else
        {
            // Get the WebFrameworkConfigElement if not previously retrieved...
            Config webFrameworkConfig = configService.getConfig("WebFramework");
            this.webFrameworkConfigElement = (WebFrameworkConfigElement) webFrameworkConfig.getConfigElement("web-framework");
        }        
        return webFrameworkConfigElement;        
    }
    
    /**
     * <p>Gets the UserFactory defined in the WebFrameworkConfigElement (return by the <code>getWebFrameworkConfig</code> method. 
     * If a UserFactory has not been defined in the configuration element then a new instance of <code>DefaultUserFactory</code>
     * will be returned.
     *  
     * @return A <code>UserFactory</code>
     */
    public UserFactory getUserFactory()
    {        
        UserFactory userFactory = null;
        String defaultUserFactoryId = getWebFrameworkConfig().getDefaultUserFactoryId();
        if (defaultUserFactoryId != null)
        {
            // If a default UserFactory has been defined in the configuration then obtain it from the application context. 
            userFactory = (UserFactory) applicationContext.getBean(defaultUserFactoryId);
        }
        else
        {
            // If a default UserFactory has not been defined in the configuration then create a DefaultUserFactory.
            userFactory = new DefaultUserFactory();
        }
        return userFactory;
    }
    
    /**
     * <p>Instantiates and returns a new <code>TagLibFactory</code> using the <code>ServletContext</code> supplied by the 
     * Spring Framework because this class implements the <code>ServetContextAware</code> interface.
     * @return A new instance of a <code>TagLibFactory</code>
     */
    public TaglibFactory getTabLibFactory()
    {
        TaglibFactory tagLibFactory = new TaglibFactory(servletContext);
        return tagLibFactory;
    }
    
    /**
     * <p>Instantiates an returns a new <code>ScriptConfigModel</code> using the <code>ConfigService</code> provided by the
     * Spring framework. This allows the <code>ScriptConfigModel</code> to be defined as a Spring Bean that is a dependency of the 
     * <code>ProcessorModelHelper</code> Spring bean.</p>
     * 
     * @return A new instance of a <code>ScriptConfigModel</code>
     */
    public ScriptConfigModel getScriptConfigModel()
    {
        ScriptConfigModel scriptConfigModel = new ScriptConfigModel(configService, null);
        return scriptConfigModel;
    }
    
    /**
     * <p>Instantiates an returns a new <code>TemplateConfigModel</code> using the <code>ConfigService</code> provided by the
     * Spring framework. This allows the <code>TemplateConfigModel</code> to be defined as a Spring Bean that is a dependency of the 
     * <code>ProcessorModelHelper</code> Spring bean.</p>
     * 
     * @return A new instance of a <code>ScriptConfigModel</code>
     */    
    public TemplateConfigModel getTemplateConfigModel()
    {
        TemplateConfigModel templateConfigModel = new TemplateConfigModel(configService, null);
        return templateConfigModel;
    }
    
    /**
     * <p>Instantiates a new instance of <code>ServletContextHashModel</code>.</p>
     * @return
     */
    public ServletContextHashModel getServletContextHashModel()
    {
        // build the servlet context hash model
        GenericServlet servlet = new GenericServletAdapter();
        try {
            servlet.init(new DelegatingServletConfig(servletContext));
        }
        catch (ServletException ex) {
            throw new BeanInitializationException("Initialization of GenericServlet adapter failed", ex);
        }        
        ServletContextHashModel servletContextHashModel = new ServletContextHashModel(servlet, ObjectWrapper.DEFAULT_WRAPPER);
        return servletContextHashModel;
    }
    
    /**
     * Simple adapter class that extends {@link GenericServlet}.
     * Needed to support generic JSP tag libraries in FreeMarker.
     */
    private class GenericServletAdapter extends GenericServlet 
    {
        private static final long serialVersionUID = 3669237557425980138L;

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) 
        {
            // no-op
        }
    }

    /**
     * Internal implementation of the {@link ServletConfig} interface,
     * to be passed to the servlet adapter.
     */
    private class DelegatingServletConfig implements ServletConfig 
    {
        private ServletContext servletContext = null;
        
        public DelegatingServletConfig(ServletContext servletContext)
        {
            this.servletContext = servletContext;
        }
        
        public String getServletName() 
        {
            return FrameworkBean.class.getSimpleName();
        }

        public ServletContext getServletContext() 
        {
            return servletContext;
        }

        public String getInitParameter(String paramName) 
        {
            return null;
        }

        @SuppressWarnings("unchecked")
        public Enumeration getInitParameterNames() 
        {
            return Collections.enumeration(Collections.EMPTY_SET);
        }
    }
}
