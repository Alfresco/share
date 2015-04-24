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

import org.springframework.extensions.surf.render.RenderService;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>This class uses a <code>PresentationService</code> to render a link to the object defined by the supplied 
 * parameters. It is the FreeMarker equivalent of the custom JSP <code>ObjectLinkTag</code>.</p>
 * 
 * @author David Draper
 */
public class LinkFreeMarkerDirective extends RenderServiceFreeMarkerDirective
{
    /**
     * <p>Instantiates a new <code>LinkFreeMarkerDirective</code>. The <code>RenderService</code> will be 
     * used by calling its <code>generateLink</code> method to generate the output. The directive name is only 
     * needed for generating useful exception messages to assist debugging problems but an effort should be made 
     * to set it correctly</p>
     * 
     * @param directiveName The name of the directive represented by the instance of this class.
     * @param renderService A <code>RenderService</code> used to generate the output of the directive.
     */
    public LinkFreeMarkerDirective(String directiveName, RenderService renderService)
    {        
        // PLEASE NOTE: We're intentionally supplying null as the RenderContext argument to the super class
        // constructor simply because we know that it is not required. There seemed little point in creating
        // an additional class in the hierarchy to just support RenderService directives that didn't
        // require a RenderContext.
        super(directiveName, null, null, renderService);
    }
    
    /**
     * <p>This method is implemented to satisfy the <code>TemplateDirectiveModel</code> interface that is 
     * implemented by the <code>AbstractFreeMarkerDirective</code> in the class hierarchy. It retrieves the
     * any parameters that have been supplied when invoking the directive and passes them onto the
     * <code>PresentationService</code> to render the link text which is then output.</p>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void execute(Environment env, 
                        Map params, 
                        TemplateModel[] loopVars, 
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        // Get the properties - note that none of these are required which is a limitation of the original
        // API. This means that incorrect usage could result in failure to produce an anchor...
        String pageId = getStringProperty(params, "page", false);
        String pageTypeId = getStringProperty(params, "pageType", false);
        String objectId = getStringProperty(params, "object", false);
        String formatId = getStringProperty(params, "format", false);
        
        // Render the link and write it to the output stream...
        String link = getRenderService().generateLink(pageTypeId, pageId, objectId, formatId);
        env.getOut().write(link);
    }
}
