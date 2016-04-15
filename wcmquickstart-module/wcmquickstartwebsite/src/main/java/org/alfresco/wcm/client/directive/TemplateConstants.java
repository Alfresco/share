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
