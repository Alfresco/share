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

package org.springframework.extensions.surf.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.webscripts.connector.User;

/**
 * The default Web Framework implementation of UserFactory.
 * 
 * This is a very uninteresting user factory which simply returns
 * unauthenticated users.  In other words, all users essentially
 * authenticate which allows a site to run in "guest" mode (NOP users).
 * 
 * @author muzquiano
 */
public class DefaultUserFactory extends AbstractUserFactory
{
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#authenticate(javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    @Override
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String)
     */
    @Override
    public User loadUser(RequestContext context, String userId, String endpointId) throws UserFactoryException
    {
        return this.getGuestUser(context);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String)
     */
    @Override
    public User loadUser(RequestContext context, String userId) throws UserFactoryException
    {
        return this.getGuestUser(context);
    }
}