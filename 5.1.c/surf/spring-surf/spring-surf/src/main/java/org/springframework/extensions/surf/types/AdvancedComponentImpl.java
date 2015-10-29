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
package org.springframework.extensions.surf.types;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.render.RenderUtil;

/**
 * @author David Draper
 */
public class AdvancedComponentImpl extends ComponentImpl implements AdvancedComponent
{
    private static final long serialVersionUID = 2691649813539118537L;

    private static final Log logger = LogFactory.getLog(AdvancedComponentImpl.class);
    
    /**
     * <p>When instantiating an {@link AdvancedComponent} from configuration intended for {@link Component}
     * this is the identifier that will be given to the single {@link SubComponent} generated.</p>
     */
    public static final String SUB_COMPONENT_ELEMENT_CONVERSION_ID = "default";
    
    /**
     * <p>Indicates whether or not this instance was instantiated from configuration specifically tailored
     * for an {@link AdvancedComponent} or for a {@link Component}.</p>
     */
    private boolean advancedConfig = false;
    
    /**
     * <p>Indicates whether or not this instance was instantiated from configuration specifically tailored
     * for an {@link AdvancedComponent} or for a {@link Component}.</p>
     */
    public boolean isAdvancedConfig()
    {
        return this.advancedConfig;
    }
    
    public AdvancedComponentImpl(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
        
        // In order to support Spring Surf applications built on earlier versions of the 
        // code we need to convert "regular" Components into AdvancedComponents by extracting
        // the data in the original Component configuration and using it to make a 
        // default ComponentElement.
        
        // We need to support Component-Types, plus all different rendering types of (e.g.
        // FreeMarker, JSP, WebScript, custom, etc).
        
        // We'll still need the scope, region id and source id in order for the component to be bound,
        // the only other information we need will be the URL/URI (which may be in the ComponentType so
        // not readily available) plus any properties.
        Element renderableElements = document.getRootElement().element(SUB_COMPONENTS);
        if (renderableElements == null)
        {
            // If there is no "sub-components" element in the component configuration file then we HAVE
            // to assume that it is an "old school" Component. In this case we will need to dynamically
            // create the AdvancedComponent data from the old style configuration.
            advancedConfig = false;
            
        }
        else
        {
            // Because there is a "sub-components" element in the component configuration file the 
            // we can assume that this is the new style Component in which case we don't need to do any additional
            // work because everything required should be defined through the new style configuration.
            advancedConfig = true;
        }
    }

    /**
     * <p>This is a {@link List} of {@link SubComponent} instances that will be rendered when this {@link AdvancedComponent}
     * is bound to a region.</p>
     */
    private List<SubComponent> subComponents = null;
    
    /** Lock to provide protection around the list of sub components */
    private ReadWriteLock subComponentsLock = new ReentrantReadWriteLock();
    
    /**
     * Retrieve the sub component list for this component.
     */
    public List<SubComponent> getSubComponents()
    {
        this.subComponentsLock.readLock().lock();
        try
        {
            if (subComponents == null)
            {
                this.subComponentsLock.readLock().unlock();
                this.subComponentsLock.writeLock().lock();
                try
                {
                    // check again as multiple threads could have been waiting on the write lock
                    if (subComponents == null)
                    {
                        if (advancedConfig)
                        {
                            applyConfig(getDocument().getRootElement());
                        }
                        else
                        {
                            subComponents = new ArrayList<SubComponent>();
                            
                            // An id isn't a mandatory field in a Component, but will ultimately be generated from the
                            // scope, region and source attributes. Since the id of a ComponentElement is NOT used to
                            // bind to a region it doesn't matter that the ComponentElement could share the same id as
                            // the AdvancedComponent.
                            String id = getId();
                            if (id == null)
                            {
                                id = RenderUtil.generateComponentId(getScope(), getRegionId(), getSourceId());
                            }
                            
                            // Get the URI of the component (this might not be correct, but we're going to assume that ComponentType
                            // trumps local URI - the original implementation in the Spring Surf RenderService is not clear on what
                            // the rule actually should be)...
                            
                            // Get the URI, component type id and processor id from the component. This information will be be set 
                            // in a new ComponentElement that will then represent the original Component configuration...
                            String uri = getURI();
                            String componentTypeId = getComponentTypeId();
                            String processorId = getProcessorId();
                            
                            // Create a new ComponentElement using the id defined for the component...
                            SubComponent componentElement = new SubComponent(SUB_COMPONENT_ELEMENT_CONVERSION_ID, id);
                            componentElement.setUri(uri);
                            componentElement.setComponentTypeId(componentTypeId);
                            componentElement.setProcessorId(processorId);
                            componentElement.setAllProperties(getModelProperties(), getCustomProperties()); // Set the properties...
                            subComponents.add(componentElement);
                        }
                    }
                }
                finally
                {
                    this.subComponentsLock.readLock().lock();
                    this.subComponentsLock.writeLock().unlock();
                }
            }
            return subComponents;
        }
        finally
        {
            this.subComponentsLock.readLock().unlock();
        }
    }

    /**
     * <p>Sets the the {@link List} of {@link SubComponent} instances that are owned by this
     * {@link AdvancedComponent}.</p>
     */
    public void setSubComponents(List<SubComponent> subComponents)
    {
        this.subComponentsLock.writeLock().lock();
        try
        {
            this.subComponents = subComponents;
        }
        finally
        {
            this.subComponentsLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>Initialises the {@link AdvancedComponentImpl} instance by parsing the supplied XML configuration {@link Element}.
     * This will build a list of {@link SubComponent} instances. Parsing of the configuration for each {@link SubComponent} 
     * is deferred to each instance created.</p> 
     */
    @SuppressWarnings("unchecked")
    public void applyConfig(Element componentEl)
    {
        List<SubComponent> componentElements = new ArrayList<SubComponent>();
        Element subComponentsEl = componentEl.element(SUB_COMPONENTS);
        if (subComponentsEl != null)
        {
            this.advancedConfig = true;
            List<Element> subComponentList = subComponentsEl.elements(SUB_COMPONENT);
            for (Element subComponentEl: subComponentList)
            {
                // Check the sub-component for an id...
                String subComponentId = subComponentEl.attributeValue(ID);
                if (subComponentId != null)
                {
                    SubComponent componentElement = new SubComponent(subComponentId, getId());
                    componentElement.applyConfiguration(subComponentEl);
                    componentElements.add(componentElement);
                }
                else
                {
                    if (logger.isWarnEnabled())
                    {
                        logger.warn("<" + COMPONENT + "> '" + this.getId() + "' is configured with a <" + SUB_COMPONENT + "> that does not have an '" + ID + "' attribute");
                    }
                }
            }
        }
        this.setSubComponents(componentElements);
    }
}