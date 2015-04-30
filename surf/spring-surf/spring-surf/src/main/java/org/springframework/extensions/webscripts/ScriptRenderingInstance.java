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

package org.springframework.extensions.webscripts;

import java.io.Serializable;
import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;

/**
 * Wraps the renderer instance and provisions properties about the currently
 * rendering item to the script writer.
 * 
 * The following is valid:
 * 
 * var object = instance.object;
 * var objectId = instance.id;
 * var user = instance.user;
 * var renderingProperties = instance.properties;
 */
public final class ScriptRenderingInstance extends ScriptBase
{
    final private RequestContext renderContext;
    
    private ModelObject object;
    
    /**
     * Instantiates a new script renderer instance.
     * 
     * @param context the render context
     */
    public ScriptRenderingInstance(RequestContext context, ModelObject object)
    {
        super(context);
        
        this.renderContext = context;
        this.object = object;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            if (this.object != null)
            {
                this.properties = new ScriptableLinkedHashMap<String, Serializable>(
                        this.object.getProperties());
            }
        }
        
        return this.properties;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    /**
     * Gets the object.
     * 
     * @return the object
     */
    public ScriptModelObject getObject()
    {
        return new ScriptModelObject(this.renderContext, this.object);
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return this.context.getId();
    }

    /**
     * Gets the html id
     * 
     * @return
     */
    public String getHtmlId()
    {
        return (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
    }
    
    /**
     * Returns the names of request parameters
     * 
     * @return array of names (String)
     */
    public String[] getParameterNames()
    {
        int size = this.renderContext.getParameters().size();
        return this.renderContext.getParameters().keySet().toArray(new String[size]);
    }
    
    /**
     * Returns the value of a request parameter
     * 
     * @param name
     * @return
     */
    public Object getParameter(String name)
    {
        return (String) this.renderContext.getParameter(name);
    }
    
    /**
     * Returns a scriptable map of name/value pairs
     * 
     * @return
     */
    public Scriptable getParameters()
    {
        Map map = this.renderContext.getParameters();
        return ScriptHelper.toScriptableMap(map);
    }      
}
