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

package org.springframework.extensions.surf.persister;

import org.dom4j.Document;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelObjectPersister;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;

/**
 * Abstract implementation of a model object persister.
 * <p>
 * Provided to enable ease-of-extension by developers for customer
 * object persisters.  This class serves as a foundation and really
 * only provides essential helper functions.
 * <p>
 * Surf provides persister implementations that are largely file-based.
 * Customizations may seek to incorporate persisters that interact with
 * databases or Alfresco content models directly.
 * 
 * @see StoreObjectPersister
 * @see CachedStoreObjectPersister
 * 
 * @author muzquiano
 * @author kevinr
 */
public abstract class AbstractObjectPersister implements ModelObjectPersister, BeanNameAware
{
    protected static final Class<?>[] MODELOBJECT_CLASSES = new Class[] {
        String.class, ModelPersisterInfo.class, Document.class };    
        
    private String id = null;
    
    private boolean isEnabled = true;
    
    private WebFrameworkConfigElement webFrameworkConfig;
    
    private PersisterService persisterService;
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name)
    {
        this.id = name;        
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#init(org.alfresco.web.framework.ModelPersistenceContext)
     */
    public void init(ModelPersistenceContext context) 
    {
        // no default initialization
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#reset()
     */
    public void reset()
    {
        // no default reset behaviour
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#removeObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean removeObject(ModelPersistenceContext context, ModelObject object)
        throws ModelObjectPersisterException    
    {
        return removeObject(context, object.getTypeId(), object.getId());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#hasObject(org.alfresco.web.framework.ModelPersistenceContext, org.alfresco.web.framework.ModelObject)
     */
    public boolean hasObject(ModelPersistenceContext context, ModelObject object)
         throws ModelObjectPersisterException
    {
        return hasObject(context, object.getTypeId(), object.getId());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#getId()
     */
    public String getId()
    {
        return this.id;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObjectPersister#isEnabled()
     */
    public boolean isEnabled()
    {
        return this.isEnabled;
    }
    
    /**
     * Disables the persister
     */
    protected void disable()
    {
        this.isEnabled = false;
    }
        
    /* **************************************
     *                                      *
     * GETTERS AND SETTERS FOR SPRING BEANS *
     *                                      *
     ************************************** */
    
    public void setPersisterService(PersisterService persisterService)
    {
        this.persisterService = persisterService;
    }

    public void setWebFrameworkConfig(WebFrameworkConfigElement webFrameworkConfig)
    {
        this.webFrameworkConfig = webFrameworkConfig;
    }

    protected WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        // This if block has been included to support the legacy application context configuration
        // which may stay be used.
        if (this.webFrameworkConfig == null)
        {
            this.webFrameworkConfig = this.serviceRegistry.getWebFrameworkConfiguration();
        }
        return this.webFrameworkConfig;
    }

    protected PersisterService getPersisterService()
    {
        // This if block has been included to support the legacy application context configuration
        // which may stay be used.
        if (this.persisterService == null)
        {
            this.persisterService = this.serviceRegistry.getPersisterService();
        }
        return this.persisterService;
    }
    
    /**
     * @deprecated Please configure the Spring application context to set a PersisterService and WebFrameworkConfigElement explicitly
     */
    private WebFrameworkServiceRegistry serviceRegistry;
    
    /**
     * @deprecated Please configure the Spring application context to set a PersisterService and WebFrameworkConfigElement explicitly
     */
    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }
    
    /**
     * @deprecated Please configure the Spring application context to set a PersisterService and WebFrameworkConfigElement explicitly 
     */
    public WebFrameworkServiceRegistry getServiceRegistry()
    {
        return this.serviceRegistry;
    }
}
