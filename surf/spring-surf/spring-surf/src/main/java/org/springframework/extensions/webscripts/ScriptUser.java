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

package org.springframework.extensions.webscripts;

import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.webscripts.connector.User;

/**
 * Read-only root-scoped script object wrapping the current user for
 * the current thread of execution.
 * 
 * The following is equivalent:
 * 
 * var organization = user.organization;
 * var organization = user.properties.organization;
 * var organization = user.properties["organization"];
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class ScriptUser extends ScriptBase
{
    private final User user;
    private ScriptableMap<String, Boolean> capabilities;
    
    
    /**
     * Instantiates a new ScriptUser object which wraps a given request
     * context and framework user object.
     * 
     * @param context the render context
     * @param user the user
     */
    public ScriptUser(RequestContext context, User user)
    {
        super(context);
        
        // store a reference to the user object
        this.user = user;
    }
        
    /**
     * Provides an associative array of properties that can be accessed via
     * scripting by using the .properties accessor.
     * 
     * @return the properties
     */
    protected ScriptableMap buildProperties()
    {
        if (this.properties == null)
        {
            this.properties = new ScriptableWrappedMap(user.getProperties());
        }
        
        return this.properties;
    }
    
    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getId()
    {
        return this.user.getId();
    }
    
    /**
     * Gets the name (generally this is the username - i.e. same as id)
     * 
     * @return the name
     */
    public String getName()
    {
        return this.user.getName();
    }
    
    public String getFullName()
    {
        return this.user.getFullName();
    }
    
    public String getFirstName()
    {
        return this.user.getFirstName();
    }
    
    public void setFirstName(String value)
    {
        this.user.setFirstName(value);
    }
    
    public String getLastName()
    {
        return this.user.getLastName();
    }
    
    public void setLastName(String value)
    {
        this.user.setLastName(value);
    }

    public String getMiddleName()
    {
        return this.user.getMiddleName();
    }
    
    public void setMiddleName(String value)
    {
        this.user.setMiddleName(value);
    }
    
    public String getEmail()
    {
        return this.user.getEmail();
    }
    
    public void setEmail(String value)
    {
        this.user.setEmail(value);
    }
    
    public String getOrganization()
    {
        return this.user.getOrganization();
    }
    
    public void setOrganization(String value)
    {
        this.user.setEmail(value);
    }
    
    public String getJobTitle()
    {
        return this.user.getJobTitle();
    }
    
    public void setJobTitle(String value)
    {
        this.user.setJobTitle(value);
    }
    
    public String getLocation()
    {
        return this.user.getLocation();
    }
    
    public void setLocation(String value)
    {
        this.user.setLocation(value);
    }
    
    public String getBiography()
    {
        return this.user.getBiography();
    }
    
    public void setBiography(String value)
    {
        this.user.setBiography(value);
    }
    
    public String getTelephone()
    {
        return this.user.getTelephone();
    }
    
    public void setTelephone(String value)
    {
        this.user.setTelephone(value);
    }
    
    public String getMobilePhone()
    {
        return this.user.getMobilePhone();
    }
    
    public void setMobilePhone(String value)
    {
        this.user.setMobilePhone(value);
    }
    
    public String getSkype()
    {
        return this.user.getSkype();
    }
    
    public void setSkype(String value)
    {
        this.user.setSkype(value);
    }
    
    public String getInstantMsg()
    {
        return this.user.getInstantMsg();
    }
    
    public void setInstantMsg(String value)
    {
        this.user.setInstantMsg(value);
    }
    
    public String getGoogleUsername()
    {
        return this.user.getGoogleUsername();
    }
    
    public void setGoogleUsername(String value)
    {
        this.user.setGoogleUsername(value);
    }
    
    public String getCompanyPostcode()
    {
        return this.user.getCompanyPostcode();
    }
    
    public void setCompanyPostcode(String value)
    {
        this.user.setCompanyPostcode(value);
    }
    
    public String getCompanyTelephone()
    {
        return this.user.getCompanyTelephone();
    }
    
    public void setCompanyTelephone(String value)
    {
        this.user.setCompanyTelephone(value);
    }
    
    public String getCompanyFax()
    {
        return this.user.getCompanyFax();
    }
    
    public void setCompanyFax(String value)
    {
        this.user.setCompanyFax(value);
    }
    
    public String getCompanyEmail()
    {
        return this.user.getCompanyEmail();
    }
    
    public void setCompanyEmail(String value)
    {
        this.user.setCompanyEmail(value);
    }
    
    public String getCompanyAddress1()
    {
        return this.user.getCompanyAddress1();
    }
    
    public void setCompanyAddress1(String value)
    {
        this.user.setCompanyAddress1(value);
    }

    public String getCompanyAddress2()
    {
        return this.user.getCompanyAddress2();
    }
    
    public void setCompanyAddress2(String value)
    {
        this.user.setCompanyAddress2(value);
    }
    
    public String getCompanyAddress3()
    {
        return this.user.getCompanyAddress3();
    }
    
    public void setCompanyAddress3(String value)
    {
        this.user.setCompanyAddress3(value);
    }
    
    public boolean getIsAdmin()
    {
        return this.user.isAdmin();
    }
    
    public boolean getIsGuest()
    {
        return this.user.isGuest();
    }
        
    /**
     * Persist user changes
     */
    public void save()
    {
        this.user.save();
    }
    
    /**
     * @return the underlying User object - for access to additional methods on custom User objects
     */
    public User getNativeUser()
    {
        return this.user;
    }
    
    /**
     * Gets a map of capabilities (boolean assertions) for the user.
     * 
     * @return the capability map
     */
    @SuppressWarnings("unchecked")
    public ScriptableMap<String, Boolean> getCapabilities()
    {
        if (this.capabilities == null)
        {
            this.capabilities = new ScriptableWrappedMap(this.user.getCapabilities());
        }        
        return this.capabilities;
    }  
    
    /**
     * Retrieve a user object with populated details for the given user Id
     * 
     * @param userId
     * 
     * @return ScriptUser
     */
    public ScriptUser getUser(String userId)
    {
        try
        {
            User user = FrameworkUtil.getServiceRegistry().getUserFactory().loadUser(this.context, userId);
            return new ScriptUser(this.context, user);
        }
        catch (UserFactoryException err)
        {
            // unable to load user details - so cannot return a user to the caller
            return null;
        }
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return user.getProperties().toString();
    }    
}