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

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.AbstractExtensibilityDirective;
import org.springframework.extensions.surf.render.RenderService;

public abstract class RenderServiceExtensibilityDirective extends AbstractExtensibilityDirective
{
    /**
     * <p>A <code>RenderService</code> is required to generate the output for this directive. 
     * It should be provided when instantiating the class.</p>
     */
    private RenderService renderService;
    
    /**
     * <p>Returns the <code>RenderService</code> supplied when instantiating the class.</p>
     * @return A <code>RenderService</code>
     */
    public RenderService getRenderService()
    {
        return renderService;
    }

    /**
     * <p>A <code>RequestContext</code> is typically needed as an argument of the <code>PresentationService</code>
     * methods that are used to generate the output for this directive. It should be provided when instantiating 
     * the class.</p>
     */
    private RequestContext requestContext;
    
    /**
     * <p>Returns the <code>RequestContext</code> supplied when instantiating the class</p>
     * @return A <code>RequestContext</code>
     */
    public RequestContext getRequestContext()
    {
        return requestContext;
    }
    
    private ModelObject object;
    
    public ModelObject getObject()
    {
        return object;
    }
    
    public RenderServiceExtensibilityDirective(String directiveName, 
                                               ExtensibilityModel model, 
                                               RequestContext context, 
                                               ModelObject object,  
                                               RenderService renderService)
    {
        super(directiveName, model);
        this.requestContext = context;
        this.object = object;
        this.renderService = renderService;
    }
}
