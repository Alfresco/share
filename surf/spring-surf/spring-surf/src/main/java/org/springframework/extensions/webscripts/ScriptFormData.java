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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.WebUtil;

/**
 * Represents form data that was posted back to the current
 * rendering context.  Form data can arrive via a multipart form post
 * or via a urlencoded form post.
 * 
 * This object disambiguates between the two and provides a common
 * interface for working with form-posted variables.  It takes into
 * account namespacing of form variables.
 * 
 * @author muzquiano
 */
public final class ScriptFormData extends ScriptBase
{
    private Map<String, FormField> fields;
    final private RequestContext renderContext;
    private ModelObject object;
    
    /**
     * Instantiates a new script form.
     * 
     * @param rendererContext the renderer context
     */
    public ScriptFormData(RequestContext context, ModelObject object)
    {
        super(context);
        
        this.renderContext = context;
        this.object = object;
        
        // load for this renderer instance
        this.load(ScriptForm.getPrefix(this.renderContext, object));
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.ScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        return null;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
        
    /**
     * Gets an element form field.
     * 
     * @param id the id
     * 
     * @return the binding
     */
    public FormField getField(String id)
    {
        return (FormField) fields.get(id);
    }
    
    /**
     * Gets the fields.
     * 
     * @return the fields
     */
    public Object[] getFields()
    {
        Object[] array = new Object[fields.size()];
        
        int i = 0;
        Iterator it = fields.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            FormField field = (FormField) fields.get(key);
            array[i] = field;
            i++;
        }

        return array;
    }
    
    /**
     * Gets the ids of all element form fields.
     * 
     * @return the field ids
     */
    public String[] getFieldIds()
    {
        return fields.keySet().toArray(new String[fields.keySet().size()]);
    }
    
    /**
     * Populates the object with form bindings for the given namespace
     */
    public void load(String prefix)
    {
        this.fields = new HashMap<String, FormField>(16, 1.0f);
        
        try
        {
            // attempt to look at multipart form post
            Content c = this.context.getRequestContent();
            if (c != null)
            {
                String content = c.getContent();
                if (content != null && content.length() > 0)
                {
                    // if we have a multipart form post, then convert the values
                    Map multiPartMap = WebUtil.buildQueryStringMap(content);
                    processMapIntoFields(multiPartMap, prefix);
                }
                
                // attempt to look at request parameters
                // if the post was received using urlencoding, the parameters
                // will have already been parsed and populated by the servlet
                // engine
                Map requestParametersMap = this.renderContext.getParameters();
                processMapIntoFields(requestParametersMap, prefix);
            }
        }
        catch (IOException ioe)
        {
            // allow this to fail silently, it means we can't load
        }
    }
    
    /**
     * Converts a map of name/value pairs into fields if they match
     * the specified prefix
     * 
     * @param map
     * @param prefix
     */
    protected void processMapIntoFields(Map map, String prefix)
    {
        if (map != null)
        {
            Iterator it = map.keySet().iterator();
            while (it.hasNext())
            {
                String prefixedId = (String) it.next();
                
                // if the prefix is null
                // or if the prefixed id starts with our prefix
                if ( (prefix == null) ||
                    (prefix != null && prefixedId.startsWith(prefix)) )
                {
                    Object value = map.get(prefixedId);
                    if (value instanceof String)
                    {
                        // register the form field
                        FormField field = new FormField(prefixedId, value);
                        this.fields.put(prefixedId, field);
                    }
                }
            }
        }
    }
    

    public class FormField implements Serializable
    {
        private String id;       
        private Object value;
        
        /**
         * Instantiates a new form field.
         * 
         * @param id the id
         */
        public FormField(String id)
        {
            this.id = id;
        }
        
        /**
         * Instantiates a new form field.
         * 
         * @param id the id
         * @param value the value
         */
        public FormField(String id, Object value)
        {
            this(id);

            this.value = value;            
        }
        
        /**
         * Sets the value.
         * 
         * @param value the new value
         */
        public void setValue(Object value)
        {
            this.value = value;
        }
        
        public Object getValue()
        {
            return this.value;
        }
        
        public String getId()
        {
            return ScriptForm.unprefix(id);
        }        
    }
}
