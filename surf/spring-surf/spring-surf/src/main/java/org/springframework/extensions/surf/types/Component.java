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

package org.springframework.extensions.surf.types;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;

/**
 * Interface for a Component object type
 * 
 * @author muzquiano
 */
public interface Component extends ModelObject, Comparable<Component>, SurfBugData
{
    // type
    public static String TYPE_ID = "component";
    
    // properties
    public static String PROP_REGION_ID = "region-id";
    public static String PROP_SOURCE_ID = "source-id";
    public static String PROP_SCOPE = "scope";
    public static String PROP_COMPONENT_TYPE_ID = "component-type-id";
    public static String PROP_CHROME = "chrome";
    public static String PROP_URL = "url";
    public static String PROP_URI = "uri";
    public static String PROP_GUID = "guid";
    public static String PROP_INDEX = "index"; // Index only comes into play when a component is assigned to a component group
    
    
    /**
     * Gets the region id.
     * 
     * @return the region id
     */
    public String getRegionId();

    /**
     * Sets the region id.
     * 
     * @param regionId the new region id
     */
    public void setRegionId(String regionId);

    /**
     * Gets the source id.
     * 
     * @return the source id
     */
    public String getSourceId();

    /**
     * Sets the source id.
     * 
     * @param sourceId the new source id
     */
    public void setSourceId(String sourceId);

    /**
     * Gets the scope.
     * 
     * @return the scope
     */
    public String getScope();

    /**
     * Sets the scope.
     * 
     * @param scope the new scope
     */
    public void setScope(String scope);

    /**
     * Gets the component type id.
     * 
     * @return the component type id
     */
    public String getComponentTypeId();

    /**
     * Sets the component type id.
     * 
     * @param componentTypeId the new component type id
     */
    public void setComponentTypeId(String componentTypeId);

    /**
     * Gets the chrome.
     * 
     * @return the chrome
     */
    public String getChrome();
    
    /**
     * Sets the chrome.
     * 
     * @param chrome the new chrome
     */
    public void setChrome(String chrome);
    
    /**
     * Gets the URL.
     * 
     * @return the URL
     */
    public String getURL();
    
    /**
     * Sets the URL.
     * 
     * @param url the new URL
     */
    public void setURL(String url);
    
    /**
     * Gets the URI
     * @return
     */
    public String getURI();
    
    /**
     * Sets the URI.
     */
    public void setURI(String uri);
    
    /**
     * Gets the GUID.
     * 
     * @return the GUID
     */
    public String getGUID();
    
    /**
     * Sets the GUID.
     * 
     * @param guid the new GUID
     */
    public void setGUID(String guid);

    /**
     * Gets the source object.
     * 
     * @param context request context
     * 
     * @return the object
     */
    public Object getSourceObject(RequestContext context);

    /**
     * Gets the component type.
     * 
     * @param context the context
     * 
     * @return the component type
     */
    public ComponentType getComponentType(RequestContext context);
    
    /**
     * Gets the index of the component. This is only used for ordering components when they are part of
     * a component group and does not have any affect when a component is bound directly to a region.
     * @return
     */
    public String getIndex();

    /**
     * Sets the index of the component. This is only used for ordering components when they are part of
     * a component group and does not have any affect when a component is bound directly to a region.
     * @return
     */
    public void setIndex(String index);
    
}