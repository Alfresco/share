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

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.render.RenderService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>A FreeMarker directive that uses a {@link PresentationService} to render the region 
 * specified through properties supplied when invoking it.</p>
 * 
 * @author David Draper
 */
public class RegionFreemarkerTagDirective extends RenderServiceExtensibilityDirective
{
    /**
     * <p>Instantiates a new {@link RegionFreeMarkerTagDirective}. The {@link RenderService} will be 
     * used by calling its <code>renderRegion</code> method to generate the output. The directive name is only needed
     * for generating useful exception messages to assist debugging problems but an effort should be made to set it 
     * correctly</p>
     * 
     * @param directiveName The name of the directive represented by the instance of this class.
     * @param context A <code>RenderContext</code> required as an argument to the <code>RenderService.renderRegion</code> method.
     * @param renderService A <code>RenderService</code> used to generate the output of the directive by calling 
     * its <code>renderRegion</code> method.
     */
    public RegionFreemarkerTagDirective(String directiveName, 
                                        ExtensibilityModel model, 
                                        RequestContext context, 
                                        ModelObject object, 
                                        RenderService renderService)
    {
        super(directiveName, model, context, object, renderService);  
    }

    /**
     * <p>Overrides the the default directive to create a {@link RegionDirectiveData} object
     * for storing the extensibility directive data. This differs from the default implementation
     * in that it provides an alternative <code>render</code> method which allows us to delegate
     * to the associated {@link RenderService}.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ExtensibilityDirectiveData createExtensibilityDirectiveData(String id, 
                                                                       String action,
                                                                       String target,
                                                                       Map params, 
                                                                       TemplateDirectiveBody body, 
                                                                       Environment env) throws TemplateException
    {
        RenderService renderService = getRenderService();
        RequestContext context = getRequestContext();
        return new RegionDirectiveData(id, action, target, getDirectiveName(), params, renderService, context, body, env);
    }
}
