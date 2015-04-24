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

import java.util.Map;

import org.springframework.extensions.surf.ModelObject;

/**
 * Interface for a Theme object type
 * 
 * @author muzquiano
 */
public interface Theme extends ModelObject
{
    // type
    public static String TYPE_ID = "theme";
    
    public static String CSS_TOKENS = "css-tokens";
    
    /**
     * Gets the page id given the specified page type. If the theme supplies a
     * specific page for a given page type it will be returned, if not null.
     * 
     * @param pageTypeId the page type id
     * 
     * @return the page id
     */
    public String getPageId(String pageTypeId);
    
    /**
     * Sets the page id for a page type.
     * 
     * @param pageTypeId the page type id
     * @param pageId the page id
     */
    public void setDefaultPageId(String pageTypeId, String pageId);
    
    /**
     * @returns A {@link Map} of CSS tokens to substitution values. These are used when processing
     * CSS source files so that a common CSS file can be modified per theme.
     */
    public abstract Map<String, String> getCssTokens();
}
