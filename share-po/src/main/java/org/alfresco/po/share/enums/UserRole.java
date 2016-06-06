/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.enums;

import java.util.NoSuchElementException;

/**
 * This enums used to describe the user roles.
 * 
 * @author cbairaajoni
 * @since v1.0
 */
public enum UserRole
{
    ALL("All"),
    MANAGER("Manager"),
    EDITOR("Editor"),
    CONSUMER("Consumer"),
    COLLABORATOR("Collaborator"),
    COORDINATOR("Coordinator"),
    CONTRIBUTOR("Contributor"),
    SITECONSUMER("Site Consumer"),
    SITECONTRIBUTOR("Site Contributor"),
    SITEMANAGER("Site Manager"),
    SITECOLLABORATOR("Site Collaborator");

    private String roleName;

    private UserRole(String role)
    {
        roleName = role;
    }

    public String getRoleName()
    {
        return roleName;
    }

    /**
     * helper method to
     * return enum for the
     * given value
     */
    public static UserRole getUserRoleforName(String name)
    {
        for (UserRole role : UserRole.values())
        {
            if (role.getRoleName().equalsIgnoreCase(name))
            {
                return role;
            }
        }

        throw new NoSuchElementException("No Role for value - " + name);
    }

    /**
     * helper method to
     * return siteRole for API requests in correct format (without spaces)
     */
    public String getSiteRole()
    {
        return roleName.replace(" ", "");
    }
}