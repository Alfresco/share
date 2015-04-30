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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;
import org.dom4j.tree.DefaultDocument;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.extensibility.BasicExtensionModule;
import org.springframework.extensions.surf.extensibility.Customization;
import org.springframework.extensions.surf.extensibility.ExtensionModuleEvaluator;
import org.springframework.extensions.surf.extensibility.XMLHelper;
import org.springframework.extensions.surf.render.RenderUtil;

/**
 * <p>Representation of the configuration of a module that provides an extension. Modules consist
 * of {@link Customization} and {@link AdvancedComponent} instances. They can optionally be configured
 * with a {@link ExtensionModuleEvaluator} that determines whether or not the module should be 
 * applied to a request.</p>
 * 
 * @author David Draper
 */
public class ExtensionModule extends BasicExtensionModule
{
    private static final Log logger = LogFactory.getLog(ExtensionModule.class);
        
    private ModelPersisterInfo key = null;
    public ModelPersisterInfo getKeyPath()
    {
        return key;
    }

    public HashMap<String, AdvancedComponent> advancedComponents = new HashMap<String, AdvancedComponent>();
    
    @SuppressWarnings("unchecked")
    public ExtensionModule(Element element, ModelPersisterInfo key)
    {
        // Get the id and description...
        super(element);
        this.key = key;
        if (getId() != null)
        {
            // Parse the component extension configuration...
            List<Element> componentsList = element.elements(COMPONENTS);
            for (Element componentsEl: componentsList)
            {
                List<Element> componentList = componentsEl.elements(COMPONENT);
                for (Element componentEl: componentList)
                {
                    // Get the id from the id attribute...
                    String componentId = componentEl.attributeValue("id");
                    if (componentId == null)
                    {
                        // ...but if not provided, try to generate it...
                        String scope = XMLHelper.getStringData(Component.PROP_SCOPE, componentEl, false);
                        String region = XMLHelper.getStringData(Component.PROP_REGION_ID, componentEl, false);
                        String source = XMLHelper.getStringData(Component.PROP_SOURCE_ID, componentEl, false);
                        componentId = RenderUtil.generateComponentId(scope, region, source);
                    }
                    
                    if (componentId != null)
                    {
                        // Create a new AdvancedComponent object for this configured element, we don't need to provide
                        // info or document objects as this data will not exist outside the lifecycle of the server...
                        AdvancedComponentImpl advancedComponent = new AdvancedComponentImpl(componentId, this.key, new DefaultDocument(new BaseElement("dummy")));
                        advancedComponent.applyConfig(componentEl);
                        advancedComponents.put(componentId, advancedComponent);
                    }
                    else
                    {
                        if (logger.isErrorEnabled())
                        {
                            logger.error("A <" + COMPONENT + "> element was found with no identification in <" + ExtensionImpl.MODULE + "> '" + getId() + "'");
                        }
                    }
                }
            }
        }
        else
        {
            if (logger.isErrorEnabled())
            {
                logger.error("A <" + ExtensionImpl.MODULE + "> was found with no identification");
            }
        }
    }
    
    public HashMap<String, AdvancedComponent> getAdvancedComponents()
    {
        return this.advancedComponents;
    }
}
