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

package org.springframework.extensions.surf.site;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.types.Theme;

/**
 * A helper class for working with themes.
 * 
 * This basically assists in synchronizing the current theme between
 * the request and the session.
 * 
 * It is useful for determining the current theme id during the execution
 * of a JSP component, for example, or within a custom Java bean.
 * 
 * @author muzquiano
 */
public class ThemeUtil
{
    /**
     * Gets the current theme id.
     * 
     * @param context the context
     * 
     * @return the current theme id
     */
    public static String getCurrentThemeId(RequestContext context)
    {
        String themeId = null;
        
        if (context.getTheme() != null)
        {
            themeId = context.getTheme().getId();
        }
        
        return themeId;
    }
    
    /**
     * Sets the current theme.
     * 
     * @param context the context
     * @param themeId the theme id
     */
    public static void setCurrentThemeId(RequestContext context, String themeId)
    {
        Theme theme = context.getObjectService().getTheme(themeId);
        if (theme != null)
        {
            context.setTheme(theme);
        }
    }
}
