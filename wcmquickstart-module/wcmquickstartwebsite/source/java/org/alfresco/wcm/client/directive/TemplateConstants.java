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
package org.alfresco.wcm.client.directive;

/**
 * Constants used by the JSP tag lib.
 * 
 * These are collected into a constants file so they can be referenced by custom tags.
 * 
 * @author muzquiano
 */
public class TemplateConstants
{
    // toolbar location
    public static final String TOOLBAR_LOCATION_TOP = "top";
    public static final String TOOLBAR_LOCATION_LEFT = "left";
    public static final String TOOLBAR_LOCATION_RIGHT = "right";
    
    // indicates whether the WEF framework is enabled
    public static final String REQUEST_ATTR_KEY_WEF_ENABLED = "wef_enabled";
    
    // indicates the URL 
    public static final String REQUEST_ATTR_KEY_URL_PREFIX = "wef_url_prefix";
    
    // indicates whether we are in debug mode
    public static final String REQUEST_ATTR_KEY_DEBUG_ENABLED = "wef_debug";
    
    // the toolbar location
    public static final String REQUEST_ATTR_KEY_TOOLBAR_LOCATION = "wef_toolbar_location";    
}
