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

package org.springframework.extensions.webeditor.taglib;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Loads default taglib properties from a bundle
 * 
 * @author muzquiano
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
                urlPrefix = readProperty(bundle, "webeditor.taglib.urlprefix");
                
                String _editingEnabled = readProperty(bundle, "webeditor.taglib.editing.enabled");
                if (_editingEnabled != null)
                {
                    editingEnabled = Boolean.valueOf(_editingEnabled);
                }
                
                String _debugEnabled = readProperty(bundle, "webeditor.taglib.debug.enabled");
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
     * @param bundle
     * @param key
     * @return
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
