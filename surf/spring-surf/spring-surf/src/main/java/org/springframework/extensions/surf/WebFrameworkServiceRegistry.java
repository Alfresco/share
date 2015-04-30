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

package org.springframework.extensions.surf;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.persister.PersisterService;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.connector.ConnectorService;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.ServletContextHashModel;

/**
 * Service Registry for Web Framework
 * 
 * This service provides getters for all Web Framework services and
 * helper beans.
 * 
 * @author muzquiano
 * @author Dave Draper
 */
public class WebFrameworkServiceRegistry
{
    // web framework service registry singleton
    public static final String WEB_FRAMEWORK_SERVICE_REGISTRY_ID = "webframework.service.registry";
    
    // core service beans
    private ConfigService configService;

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    private RenderService webFrameworkRenderService;
    
    public void setWebFrameworkRenderService(RenderService webFrameworkRenderService)
    {
        this.webFrameworkRenderService = webFrameworkRenderService;
    }
    
    private ResourceService webFrameworkResourceService;
    
    public void setWebFrameworkResourceService(ResourceService webFrameworkResourceService)
    {
        this.webFrameworkResourceService = webFrameworkResourceService;
    }
    
    private ConnectorService connectorService;
    
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    private PresetsManager presetsManager;
    
    public void setPresetsManager(PresetsManager presetsManager)
    {
        this.presetsManager = presetsManager;
    }
    
    private ScriptRemote scriptRemote;
    
    public void setScriptRemote(ScriptRemote scriptRemote)
    {
        this.scriptRemote = scriptRemote;
    }
    
    private PersisterService persisterService;
    
    public void setPersisterService(PersisterService persisterService)
    {
        this.persisterService = persisterService;
    }
    
    private ObjectPersistenceService objectPersistenceService;
    
    public void setObjectPersistenceService(ObjectPersistenceService objectPersistenceService)
    {
        this.objectPersistenceService = objectPersistenceService;
    }
    
    private ModelObjectService modelObjectService;
    
    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }

    public void setTemplatesContainer(TemplatesContainer templatesContainer)
    {
        this.templatesContainer = templatesContainer;
    }
    
    // templates container
    private TemplatesContainer templatesContainer;
    
    /**
     * Gets the config service.
     * 
     * @return the config service
     */
    public ConfigService getConfigService()
    {
        return this.configService;
    }
    
    /**
     * Gets the web framework render service.
     * 
     * @return the web framework render service
     */
    public RenderService getRenderService()
    {
        return this.webFrameworkRenderService;
    }
    
    /**
     * Gets the resource service.
     * 
     * @return the resource service
     * @deprecated
     */
    public ResourceService getResourceService()
    {
        return this.webFrameworkResourceService;
    }
    
    /**
     * Gets the connector service.
     * 
     * @return the connector service
     */
    public ConnectorService getConnectorService()
    {
        return this.connectorService;
    }

    /**
     * Gets the persister service
     * 
     * @return the persister service
     * @deprecated
     */
    public PersisterService getPersisterService()
    {
        return this.persisterService;
    }
    
    /**
     * Gets the object persistence service
     * 
     * @return the object persistence service
     * @deprecated
     */
    public ObjectPersistenceService getObjectPersistenceService()
    {
        return this.objectPersistenceService;
    }
    
    /**
     * Gets the model object service
     * 
     * @return the model object service
     * @deprecated
     */
    public ModelObjectService getModelObjectService()
    {
        return this.modelObjectService;
    }
    
    /**
     * Gets the presets manager.
     * 
     * @return the presets manager
     */
    public PresetsManager getPresetsManager()
    {
        return this.presetsManager;
    }
    
    /**
     * Gets the script remote.
     * 
     * @return the script remote
     */
    public ScriptRemote getScriptRemote()
    {
        return this.scriptRemote;
    }
    
    /**
     * Gets the templates container.
     * 
     * @return the templates container
     */
    public TemplatesContainer getTemplatesContainer()
    {
        return this.templatesContainer;
    }
    

    
    /* 
     * PLEASE NOTE:
     * The following fields/methods are deprecated because they perform function which can be 
     * achieved through sensible Spring Bean configuration.
     */
    
    /**
     * Web framework webscripts container
     * @deprecated
     */
    private Container webFrameworkContainer;
    
    /**
     * Gets the web framework container.
     * 
     * @return the web framework container
     * @deprecated Use Spring configuration to access the Web Framework Container (Bean ref "webscripts.container")
     */
    public Container getWebFrameworkContainer()
    {
        return this.webFrameworkContainer;
    }
    
    /**
     * @param webFrameworkContainer
     * @deprecated
     */
    public void setWebFrameworkContainer(Container webFrameworkContainer)
    {
        this.webFrameworkContainer = webFrameworkContainer;
    }
    
    /**
     * @deprecated
     */
    private RemoteConfigElement remoteConfigElement;
    
    /**
     * @deprecated
     * @param remoteConfigElement
     */
    public void setRemoteConfigElement(RemoteConfigElement remoteConfigElement)
    {
        this.remoteConfigElement = remoteConfigElement;
    }

    /**
     * Gets the remote configuration.
     * 
     * @return the remote configuration
     * @deprecated Use Spring configuration to access the RemoteConfigElement (Bean ref "remote.config.element")
     */
    public RemoteConfigElement getRemoteConfigElement()
    {
        return this.remoteConfigElement;
    }
    
    /**
     * @deprecated
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;

    /**
     * @deprecated
     * @param webFrameworkConfigElement
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }
    
    /**
     * Gets the web framework configuration.
     * 
     * @return the web framework configuration
     * @deprecated Use Spring configuration to access the WebFrameworkConfigElement (Bean ref "webframework.config.element") 
     */
    public WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        return this.webFrameworkConfigElement;
    }
    
    /**
     * @deprecated
     */
    private UserFactory userFactory;
    
    /**
     * Gets the user factory.
     * 
     * @return the user factory
     * @deprecated Use Spring configuration to access the UserFactory (Bean ref "user.factory")
     */
    public UserFactory getUserFactory()
    {
        return this.userFactory;
    }
    
    /**
     * Sets the user factory.
     * 
     * @param userFactory the new user factory
     * @deprecated
     */
    public void setUserFactory(UserFactory userFactory)
    {
        this.userFactory = userFactory;
    }
    
    /**
     * @deprecated
     */
    private static TaglibFactory taglibFactory;
    
    /**
     * Gets the taglib factory.
     * 
     * @return the taglib factory
     * @deprecated Use Spring configuration to access the TabLibFactory (Bean ref "taglib.factory")
     */
    public TaglibFactory getTaglibFactory()
    {
        return taglibFactory;
    }
    
    /**
     * @deprecated 
     * @param taglibFactory
     */
    public static void setTaglibFactory(TaglibFactory taglibFactory)
    {
        WebFrameworkServiceRegistry.taglibFactory = taglibFactory;
    }
    
    /**
     * @deprecated
     */
    private static ServletContextHashModel servletContextHashModel;
    
    /**
     * @deprecated
     * @param servletContextHashModel
     */
    public static void setServletContextHashModel(ServletContextHashModel servletContextHashModel)
    {
        WebFrameworkServiceRegistry.servletContextHashModel = servletContextHashModel;
    }

    /**
     * Gets the servlet context hash model.
     * 
     * @return the servlet context hash model
     * @deprecated Use Spring configuration to access the ServletContextHashModel (Bean ref "servletContext.hashModel")
     */
    public ServletContextHashModel getServletContextHashModel()
    {
        return servletContextHashModel;
    }        
    
}
