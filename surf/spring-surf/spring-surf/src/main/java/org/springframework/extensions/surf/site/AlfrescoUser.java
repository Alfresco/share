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

import java.util.Map;

import org.springframework.extensions.webscripts.connector.User;

/**
 * User object extended to add avatar reference property.
 * 
 * @author Kevin Roast
 */
public class AlfrescoUser extends User
{
    public static String PROP_AVATARREF = "avatar";
    
    protected final Map<String, Boolean> immutability;
    
    
    /**
     * Instantiates a new user.
     * 
     * @param id            The user id
     * @param capabilities  Map of string keyed capabilities given to the user
     * @param immutability  Optional map of property qnames to immutability
     */
    public AlfrescoUser(String id, Map<String, Boolean> capabilities, Map<String, Boolean> immutability)
    {
        super(id, capabilities);
        this.immutability = immutability;
    }
    
    /**
     * @return  the avatarRef
     */
    public String getAvatarRef()
    {
        return getStringProperty(PROP_AVATARREF);
    }

    /**
     * @param avatarRef the avatarRef to set
     */
    public void setAvatarRef(String avatarRef)
    {
        setProperty(PROP_AVATARREF, avatarRef);
    }
    
    /**
     * @param property to test for immutability either full QName or assumed 'cm' namespace
     * @return true if the property is immutable
     */
    public boolean isImmutableProperty(final String property)
    {
        boolean immutable = false;
        if (this.immutability != null && property != null && property.length() != 0)
        {
            if (property.charAt(0) == '{' && property.indexOf('}') != -1)
            {
                // full qname based property
                immutable = this.immutability.containsKey(property);
            }
            else
            {
                // assume 'cm' namespace default
                immutable = this.immutability.containsKey("{http://www.alfresco.org/model/content/1.0}" + property);
            }
        }
        return immutable;
    }
}
