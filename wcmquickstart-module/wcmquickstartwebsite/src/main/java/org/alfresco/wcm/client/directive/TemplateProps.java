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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Loads default directive properties from a bundle
 * 
 * @author muzquiano
 * @author Chris Lack
 */
public class TemplateProps
{
    private static String urlPrefix = null;
    private static Boolean editingEnabled = null;
    private static Boolean debugEnabled = null;

    static
    {
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle ("webeditor");
            if (bundle != null)
            {
                urlPrefix = readProperty(bundle, "webeditor.directive.urlprefix");
                
                String _editingEnabled = readProperty(bundle, "webeditor.directive.editing.enabled");
                if (_editingEnabled != null)
                {
                    editingEnabled = Boolean.valueOf(_editingEnabled);
                }
                
                String _debugEnabled = readProperty(bundle, "webeditor.directive.debug.enabled");
                if (_debugEnabled != null)
                {
                    debugEnabled = Boolean.valueOf(_debugEnabled);
                }
            }
        }
        catch (MissingResourceException mre)
        {
            // it's fine if this occurs
            // the resource bundle isn't a required file
        }
    }    
    
    /**
     * Reads a property from a bundle.  Considers empty strings to be null.
     * If an exception occurs, null is returned.
     * 
     * @param bundle ResourceBundle
     * @param key String
     * @return String
     */
    private static String readProperty(ResourceBundle bundle, String key)
    {
        String value = null;
        
        try
        {
            if (key != null)
            {
                value = bundle.getString(key);             
                if ("".equals(value))
                {
                    value = null;
                }
            }
        }
        catch (Exception e) { }
        
        return value;
    }
    
    /**
     * @return the url prefix
     */
    public static String getUrlPrefix()
    {
        return urlPrefix;
    }
    
    /**
     * @return whether editing is enabled
     */
    public static Boolean isEditingEnabled()
    {
        return editingEnabled;
    }
    
    /**
     * @return whether debugging is enabled
     */
    public static Boolean isDebugEnabled()
    {
        return debugEnabled;
    }
}
