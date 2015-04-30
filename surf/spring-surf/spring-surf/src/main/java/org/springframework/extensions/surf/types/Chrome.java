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

/**
 * Interface for a Chrome object type
 * 
 * @author muzquiano
 */
public interface Chrome extends ModelObject
{
    // type
    public static String TYPE_ID = "chrome";
    
    // properties
    public static String PROP_CHROME_TYPE = "chrome-type";    

    /**
     * Gets the chrome type.
     * 
     * @return the chrome type
     */
    public String getChromeType();
    
    /**
     * Sets the chrome type.
     * 
     * @param chromeType the new chrome type
     */
    public void setChromeType(String chromeType);
}
