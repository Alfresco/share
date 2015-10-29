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

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.render.RenderService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>Outputs the requested url representation of a resource. Named Resource Examples:
 * <ul>
 * <li><@res name="resourceName" /></li>
 * <li><@res id="<protocol>://<{@code}endpoint>/<{@code}object id>" /></li>
 * <li><@res protocol="<{@code}protocol>" endpoint="<{@code}endpoint>" object="<{@code}object id>" /></li>
 * <li><@res name="resourceName" payload="metadata" /></li>
 * <li><@res name="resourceName" payload="content" /></li>
 * </ul>
 * </p>
 * @author David Draper
 * @author muzquiano
 */
public class ResourceFreemarkerTagDirective extends RenderServiceFreeMarkerDirective
{
    /**
     * <p>Instantiates a new <code>ResourceFreemarkerTagDirective</code>. The <code>RenderService</code> will be 
     * used by calling its <code>generateResourceURL</code> method to generate the URL to output. The directive name is 
     * only needed for generating useful exception messages to assist debugging problems but an effort should be made to
     * set it correctly</p>
     * 
     * @param directiveName The name of the directive represented by the instance of this class.
     * @param context A <code>RenderContext</code> required as an argument to the <code>RenderService</code> methods.
     * @param renderService A <code>RenderService</code> used to generate the output of the directive.
     */
    public ResourceFreemarkerTagDirective(String directiveName, RequestContext context, ModelObject object, RenderService renderService)
    {        
        super(directiveName, context, object, renderService);
    }

    /**
     * <p>This method is declared by the <code>TemplateDirectiveModel</code> interface that the abstract
     * superclass <code>AbstractFreeMarkerDirective</code> implements. It retrieves any properties supplied
     * when invoking the directive and passes them onto the <code>PresentationService</code> method.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(Environment env, 
                        Map params, 
                        TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        String name = getStringProperty(params, "name", false);
        String id = getStringProperty(params, "id", false);
        String protocol = getStringProperty(params,  "protocol", false);
        String endpoint = getStringProperty(params, "endpoint", false);
        String objectId = getStringProperty(params, "object", false);
        String payload = getStringProperty(params, "payload", false);
        String url = getRenderService().generateResourceURL(getRequestContext(), getObject(), name, id, protocol, endpoint, objectId, payload);
        env.getOut().write(url);       
    }
}
