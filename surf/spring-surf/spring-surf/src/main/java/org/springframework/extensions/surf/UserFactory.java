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

package org.springframework.extensions.surf;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.webscripts.connector.User;

/**
 * Defines the user factory interface
 * 
 * @author muzquiano
 */
public interface UserFactory
{
    /** Guest user name key*/
    public static final String USER_GUEST = "guest";
    
    /** User object key in the session */
    public static String SESSION_ATTRIBUTE_KEY_USER_OBJECT = "_alf_USER_OBJECT";
    
    /** User name id key in the session */
    public static String SESSION_ATTRIBUTE_KEY_USER_ID = "_alf_USER_ID";
    
    /** flag to set in the user Session when an external authentication mechanism is used
     *  this informs the framework that user cannot Change Password or Logout in the usual way */
    public static final String SESSION_ATTRIBUTE_EXTERNAL_AUTH= "_alfExternalAuth";
    
    /**
     * Authenticates the given user credentials against the user provider
     * 
     * @param request
     * @param username
     * @param password
     * @return
     */
    public boolean authenticate(HttpServletRequest request, String username, String password);

    /**
     * Loads a user from the remote user store and store it into the session.
     * 
     * @param context
     * @param request
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User initialiseUser(RequestContext context, HttpServletRequest request)
        throws UserFactoryException;
    
    /**
     * Loads a user from the remote user store and store it into the session.
     * 
     * @param context
     * @param request
     * @param endpoint
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User initialiseUser(RequestContext context, HttpServletRequest request, String endpoint)
        throws UserFactoryException;
    
    /**
     * Loads a user from the remote user store and stores it into the session.
     * 
     * If the force flag is set, the current in-session user
     * object will be purged, forcing the user object to reload.
     * 
     * @param context
     * @param request
     * @param force
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User initialiseUser(RequestContext context, HttpServletRequest request, boolean force)
        throws UserFactoryException;
    
    /**
     * Loads a user from the remote user store and stores it into the session.
     * 
     * If the force flag is set, the current in-session user
     * object will be purged, forcing the user object to reload.
     * 
     * @param context
     * @param request
     * @param endpoint
     * @param force
     * 
     * @return User
     * 
     * @throws UserFactoryException
     */
    public User initialiseUser(RequestContext context, HttpServletRequest request, String endpoint, boolean force)
        throws UserFactoryException;

    /**
     * Loads a user object from the default endpoint.
     * 
     * @param context
     * @param userId
     * @return
     * @throws UserFactoryException
     */
    public User loadUser(RequestContext context, String userId)
        throws UserFactoryException;

    /**
     * Loads a user object from the given endpoint.
     * 
     * @param context
     * @param userId
     * @param endpointId
     * @return
     * @throws UserFactoryException
     */
    public User loadUser(RequestContext context, String userId, String endpointId)
        throws UserFactoryException;
}
