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

import java.util.Map;

import org.springframework.extensions.surf.RequestContext;

public final class ScriptLinkBuilder extends ScriptBase
{
    /**
     * Constructs a new ScriptLinkBuilder object.
     * 
     * @param context   The request context instance for the current request
     */
    public ScriptLinkBuilder(RequestContext context)
    {
        super(context);
    }
        
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableWrappedMap(context.getValuesMap());
        }
        
        return this.properties;
    }

    
    // --------------------------------------------------------------
    // JavaScript Properties
        
    /**
     * Constructs a link to a given page instance.
     * This will automatically use the default format.
     * 
     * @param pageId The id of the page instance
     */
    public String page(String pageId)
    {
        return context.getLinkBuilder().page(context, pageId);
    }

    /**
     * Constructs a link to a given page for a given format.
     * 
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     */
    public String page(String pageId, String formatId)
    {
        return context.getLinkBuilder().page(context, pageId, formatId);
    }

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * 
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     */
    public String page(String pageId, String formatId, String objectId)
    {
        return context.getLinkBuilder().page(context, pageId, formatId, objectId);      
    }

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     * @param params A map of name/value pairs to be appended to the URL
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params)
    {
        return context.getLinkBuilder().page(context, pageId, formatId, objectId, params);      
    }

    /**
     * Constructs a link to a given page for the default format.
     * The provided parameters are appended to the URL.
     * 
     * @param pageId The id of the page instance
     * @param params A map of name/value pairs to be appended to the URL
     */
    public String page(RequestContext context, String pageId, Map<String, String> params)
    {
        return context.getLinkBuilder().page(context, pageId, null, null, params);      
    }
    
    /**
     * Constructs a link to a given page type.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     */
    public String pageType(String pageTypeId)
    {
        return context.getLinkBuilder().pageType(context, pageTypeId);      
    }

    /**
     * Constructs a link to a given page type for a given format.
     * 
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     */    
    public String pageType(String pageTypeId, String formatId)
    {
        return context.getLinkBuilder().pageType(context, pageTypeId, formatId);
    }

    /**
     * Constructs a link to a given page type for a given format.
     * The provided object is passed in as context.
     * 
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     */    
    public String pageType(String pageTypeId, String formatId, String objectId)
    {
        return context.getLinkBuilder().pageType(context, pageTypeId, formatId, objectId);      
    }

    /**
     * Constructs a link to a given page type for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     * @param params A map of name/value pairs to be appended to the URL
     */
    public String pageType(String pageTypeId, 
            String formatId, String objectId, Map<String, String> params)
    {
        return context.getLinkBuilder().pageType(context, pageTypeId, formatId, objectId, params);      
    }
    
    /**
     * Constructs a link to a given object.
     * This will automatically use the default format.
     * 
     * @param objectId The id of the object
     */    
    public String object(String objectId)
    {
        return context.getLinkBuilder().object(context, objectId);      
    }

    /**
     * Constructs a link to a given object.
     * This will automatically use the default format.
     * 
     * @param objectId The id of the object
     * @param formatId The id of the format to render
     */        
    public String object(String objectId, String formatId)
    {
        return context.getLinkBuilder().object(context, objectId, formatId);        
    }

    /**
     * Constructs a link to a given object.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param objectId The id of the object
     * @param formatId The id of the format to render
     * @param params A map of name/value pairs to be appended to the URL
     */    
    public String object(String objectId, String formatId, Map<String, String> params)
    {
        return context.getLinkBuilder().object(context, objectId, formatId, params);
    }
    
    /**
     * Constructs a link to a resources at a given relative uri.
     * 
     * @param context
     * @param uri
     * 
     * @return the 
     */
    public String resource(String uri)
    {
        return context.getLinkBuilder().resource(context, uri);        
    }
    
}