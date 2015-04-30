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

package org.springframework.extensions.surf.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;

/**
 * Provides an HTTP Servlet Request implementation for the request
 * bundled inside of a render context.  This supports an easy way
 * to retrieve the render context instance.
 * 
 * @author muzquiano
 */
public class RenderContextRequest extends HttpServletRequestWrapper
{
    public static final String ATTRIB_RENDER_CONTEXT = "renderContext";
    public static final String ATTRIB_MODEL_OBJECT = "modelObject";
    
    protected RequestContext renderContext;
    private ModelObject object;
    
    public RenderContextRequest(RequestContext context, ModelObject object, HttpServletRequest request)
    {
        super(request);

        this.renderContext = context;
        this.object = object;
    }
    
    public Object getAttribute(String key)
    {
        Object value = null;
        
        if (ATTRIB_RENDER_CONTEXT.equals(key))
        {
            value = this.renderContext;
        }
        else if (ATTRIB_MODEL_OBJECT.equals(key))
        {
            value = this.object;
        }
        else
        {
            value = super.getAttribute(key);
        }
        
        return value;
    }
}