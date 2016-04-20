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