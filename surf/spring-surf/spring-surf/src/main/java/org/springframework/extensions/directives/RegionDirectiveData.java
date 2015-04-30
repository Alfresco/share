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

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.webscripts.ProcessorModelHelper;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>Overrides the default {@link DefaultExtensibilityDirectiveData} to provide a new <code>render</code> method
 * to use the {@link RenderService} to render the specified region</p>
 * 
 * @author David Draper
 */
public class RegionDirectiveData extends DefaultExtensibilityDirectiveData
{
    private static final Log logger = LogFactory.getLog(RegionDirectiveData.class);
    
    public static final String REGION_ID = "id";
    public static final String CHROME_ID = "chrome";
    public static final String SCOPE_ID = "scope";
    public static final String CHROMELESS = "chromeless";
    
    private String regionId;
    private String scope;
    private String chromeId;
    private String templateId;
    private RenderService renderService;
    private RequestContext context;
    private boolean chromeless = false;
    
    public RegionDirectiveData(String id, 
                               String action, 
                               String target,
                               String directiveName,
                               Map<String, Object> params,
                               RenderService renderService,
                               RequestContext context,
                               TemplateDirectiveBody body, 
                               Environment env) throws TemplateException
    {
        super(id, action, target, directiveName, body, env);
        
        this.regionId = DirectiveUtils.getStringProperty(params, REGION_ID, ProcessorModelHelper.REGION_DIRECTIVE_NAME, true);
        this.scope = DirectiveUtils.getStringProperty(params, SCOPE_ID, ProcessorModelHelper.REGION_DIRECTIVE_NAME, false);
        this.templateId = DirectiveUtils.getStringProperty(params, context.getTemplateId(), ProcessorModelHelper.REGION_DIRECTIVE_NAME, false);
        if (this.scope == null)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("A scope was not provided for region: '" + regionId + "' when processing templateId: '" + templateId + "' - setting global scope to prevent NPE");
            }
            this.scope = "global";
        }
        this.chromeId = DirectiveUtils.getStringProperty(params, CHROME_ID, ProcessorModelHelper.REGION_DIRECTIVE_NAME, false);
        this.chromeless = DirectiveUtils.getBooleanProperty(params, CHROMELESS, ProcessorModelHelper.REGION_DIRECTIVE_NAME, false);
        this.renderService = renderService;
        this.context = context;
    }
    
    @Override
    public void render(ModelWriter writer) throws TemplateException, IOException
    {
        renderService.renderRegion(context, RenderFocus.BODY, templateId, regionId, scope, chromeId, chromeless);
    }
}
