/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
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
