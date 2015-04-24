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

package org.springframework.extensions.surf;

import java.util.Map;

/**
 * Link builder interface that defines methods for building URIs within Surf.
 * 
 * @author muzquiano
 */
public interface LinkBuilder
{
    /**
     * Constructs a link to a given page instance.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     */
    public String page(RequestContext context, String pageId);

    /**
     * Constructs a link to a given page for a given format.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     */
    public String page(RequestContext context, String pageId, 
            String formatId);

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId);    

    /**
     * Constructs a link to a given page for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param context The Request Context instance
     * @param pageId The id of the page instance
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     * @param params A map of name/value pairs to be appended to the URL
     */
    public String page(RequestContext context, String pageId, 
            String formatId, String objectId, Map<String, String> params);
        
    /**
     * Constructs a link to a given page type.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     */
    public String pageType(RequestContext context, String pageTypeId);

    /**
     * Constructs a link to a given page type for a given format.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     */    
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId);

    /**
     * Constructs a link to a given page type for a given format.
     * The provided object is passed in as context.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     */    
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId);    

    /**
     * Constructs a link to a given page type for a given format.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param context The Request Context instance
     * @param pageTypeId The type of the page
     * @param formatId The id of the format to render
     * @param objectId The id of the object
     * @param params A map of name/value pairs to be appended to the URL
     */
    public String pageType(RequestContext context, String pageTypeId, 
            String formatId, String objectId, Map<String, String> params);
    
    /**
     * Constructs a link to a given object.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param objectId The id of the object
     */    
    public String object(RequestContext context, String objectId);

    /**
     * Constructs a link to a given object.
     * This will automatically use the default format.
     * 
     * @param context The Request Context instance
     * @param objectId The id of the object
     * @param formatId The id of the format to render
     */        
    public String object(RequestContext context, String objectId,
            String formatId);

    /**
     * Constructs a link to a given object.
     * The provided object is passed in as context.
     * The provided parameters are appended to the URL.
     * 
     * @param context The Request Context instance
     * @param objectId The id of the object
     * @param formatId The id of the format to render
     * @param params A map of name/value pairs to be appended to the URL
     */    
    public String object(RequestContext context, String objectId,
            String formatId, Map<String, String> params);
    
    /**
     * Constructs a link to a resources at a given relative uri.
     * 
     * @param context
     * @param uri
     * 
     * @return the 
     */
    public String resource(RequestContext context, String uri);

}
