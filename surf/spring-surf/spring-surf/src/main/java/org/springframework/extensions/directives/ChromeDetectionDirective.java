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
package org.springframework.extensions.directives;

import java.util.Map;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.AbstractExtensibilityDirective;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>This directive is used to determine whether or not Surf Region Chrome is enabled and if not will output the 
 * <{@code}div> element containing the unique HTML ID generated for the Component. This has been created to allow
 * the deprecation of Surf Region Chrome which can be disabled by setting the <{@code}region-chrome> configuration
 * to have no value which will eventually become the default Surf setting.</p>
 *  
 * @author David Draper
 */
public class ChromeDetectionDirective extends AbstractExtensibilityDirective
{
    public ChromeDetectionDirective(String directiveName, 
                                    ExtensibilityModel model, 
                                    WebFrameworkConfigElement webFrameworkConfig,
                                    RequestContext context)
    {
        super(directiveName, model);
        this.webFrameworkConfig = webFrameworkConfig;
    }

    /**
     * <p>The {@link WebFrameworkConfigElement} is required to determine the value of the <{@code}region-chrome> setting.</p>
     */
    private WebFrameworkConfigElement webFrameworkConfig;
    
    /**
     * @return The {@link WebFrameworkConfigElement} that this {@link ChromeDetectionDirective} was instantiated with.
     */
    public WebFrameworkConfigElement getConfig()
    {
        return webFrameworkConfig;
    }

    /**
     * <p>The {@link RequestContext} is required to retrieve the unique HTML ID created for the Component rendering.</p> 
     */
    private RequestContext context;
    
    /**
     * @return The {@link RequestContext} that this {@link ChromeDetectionDirective} was instantiated with.
     */
    public RequestContext getContext()
    {
        return context;
    }

    /**
     * <p>Creates a new {@link ChromeDetectionDirectiveData} instance which will in turn be used to create the
     * {@link ChromeDetectionContentModelElement} that will output the <{@code}div> element containing the 
     * unique HTML ID if required.</p>
     */
    @SuppressWarnings("rawtypes")
    @Override
    public ExtensibilityDirectiveData createExtensibilityDirectiveData(String id, 
                                                                       String action, 
                                                                       String target,
                                                                       Map params, 
                                                                       TemplateDirectiveBody body, 
                                                                       Environment env) throws TemplateException
    {
        return new ChromeDetectionDirectiveData(id, action, target, getDirectiveName(), body, env, webFrameworkConfig, context);
    }
}
