/*
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
package org.springframework.extensions.directives;

import java.io.IOException;
import java.util.Map;

import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.extensibility.impl.AbstractFreeMarkerDirective;
import org.springframework.extensions.surf.types.Component;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>This directive is used for creating {@link Component} instances just before they are required by a region directive.
 * This allows developers to quickly reference WebScripts without the overhead of creating additional configuration files.</p>
 * 
 * @author David Draper
 *
 */
public class CreateComponentDirective extends AbstractFreeMarkerDirective
{
    public CreateComponentDirective(String directiveName)
    {
        super(directiveName);
    }

    /**
     * The name to reference this directive by in FreeMarker templates.
     */
    public static final String DIRECTIVE_NAME = "createComponent";
    
    /**
     * A {@link ModelObjectService} is required to create {@link Component} instances.
     */
    private ModelObjectService modelObjectService = null;
    
    /**
     * Setter provided so that the Spring application context can set the {@link ModelObjectService}.
     * @param modelObjectService
     */
    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void execute(Environment env, 
                        Map params, 
                        TemplateModel[] loopVars, 
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        String scope = getStringProperty(params, "scope", true);
        String regionId = getStringProperty(params, "regionId", true);
        String sourceId = getStringProperty(params, "sourceId", true);
        String uri = getStringProperty(params, "uri", true);
        if (this.modelObjectService.getComponent(scope, regionId, sourceId) == null)
        {
            Component component = this.modelObjectService.newComponent(scope, regionId, sourceId);
            component.setURI(uri);
            component.setURL(uri);
            try
            {
                this.modelObjectService.saveObject(component);
            }
            catch (ModelObjectPersisterException e)
            {
                // No action required if save failed.
            }
        }
    }

}
