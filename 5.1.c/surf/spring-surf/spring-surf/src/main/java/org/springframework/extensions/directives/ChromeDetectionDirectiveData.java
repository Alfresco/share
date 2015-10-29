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

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityDirectiveData;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;

/**
 * <p>An {@link ExtensibilityDirectiveData} implementation that creates a {@link ChromeDetectionContentModelElement}
 * so that the nested contents of the calling {@link ExtensibilityDirective} will be wrapped in a <{@code}div> element 
 * with a unique id when Surf Region Chrome is disabled.</p>
 * 
 * @author David Draper
 */
public class ChromeDetectionDirectiveData extends DefaultExtensibilityDirectiveData
{
    public ChromeDetectionDirectiveData(String id, 
                                        String action, 
                                        String target, 
                                        String directiveName,
                                        TemplateDirectiveBody body, 
                                        Environment env, 
                                        WebFrameworkConfigElement config, 
                                        RequestContext context)
    {
        super(id, action, target, directiveName, body, env);
        this.config = config;
        this.context = context;
    }

    /**
     * <p>The {@link WebFrameworkConfigElement} used to determine what the default Surf Region Chrome is set to.</p>
     */
    private WebFrameworkConfigElement config;
    
    /**
     * @return The {@link WebFrameworkConfigElement} used to determine what the default Surf Region Chrome is set to.
     */
    public WebFrameworkConfigElement getConfig()
    {
        return config;
    }

    /**
     * <p>The {@link RequestContext} used to retrieve the unique ID for the <{@code}div> element to wrap the directives 
     * nested content in.<p>
     */
    private RequestContext context;
    
    /**
     * @return The {@link RequestContext} used to retrieve the unique ID for the <{@code}div> element to wrap the directives 
     * nested content in.
     */
    public RequestContext getContext()
    {
        return context;
    }

    /**
     * <p>Creates and returns new {@link ChromeDetectionContentModelElement} instance.</p>
     */
    @Override
    public ContentModelElement createContentModelElement()
    {
        String htmlId = null;
        if (context != null && (config.getDefaultRegionChrome() == null || !config.getDefaultRegionChrome().equals("slingshot-region-chrome")))
        {
            htmlId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
        }
        return new ChromeDetectionContentModelElement(getId(), getDirectiveName(), htmlId);
    }
}
