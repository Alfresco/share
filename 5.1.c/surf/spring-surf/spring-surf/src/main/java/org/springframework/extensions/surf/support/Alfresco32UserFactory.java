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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AlfrescoUser;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.User;

/**
 * This factory extends the Alfresco User factory and provides proper
 * backward compatibility with Alfresco 3.2.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class Alfresco32UserFactory extends AlfrescoUserFactory
{
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String)
     */
    public User loadUser(RequestContext context, String userId, String endpointId)
        throws UserFactoryException
    {
        if (endpointId == null)
        {
            endpointId = ALFRESCO_ENDPOINT_ID;
        }
        
        AlfrescoUser user = null;
        try
        {
            // ensure we bind the connector to the current user name - if this is the first load
            // of a user we will use the userId as passed into the method 
            String currentUserId = context.getUserId();
            if (currentUserId == null)
            {
                currentUserId = userId;
            }
            
            // get a connector whose connector session is bound to the current session
            HttpSession session = ServletUtil.getSession();
            Connector connector = frameworkUtils.getConnector(session, currentUserId, endpointId);
            
            // build the REST URL to retrieve user details
            String uri = "/webframework/content/metadata?user=" + URLEncoder.encode(userId);
            
            // invoke and check for OK response
            Response response = connector.call(uri);
            if (Status.STATUS_OK != response.getStatus().getCode())
            {
                throw new UserFactoryException("Unable to create user - failed to retrieve user metadata: " + 
                        response.getStatus().getMessage(), (Exception)response.getStatus().getException());
            }
            
            // Load the user properties via the JSON parser
            JSONObject json = new JSONObject(response.getResponse());
            user = buildAlfrescoUser(json);
        }
        catch (Exception ex)
        {
            // unable to read back the user json object
            throw new UserFactoryException("Unable to retrieve user from repository", ex);
        }

        return user;
    }
    
    @Override
    protected AlfrescoUser buildAlfrescoUser(JSONObject json)
        throws JSONException, UserFactoryException
    {
        JSONObject properties = json.getJSONObject("properties");
        
        // assume guest capabilities
        // Alfresco 3.3 provides additional values for a user - fake them from 3.2
        Map<String, Boolean> capabilities = new HashMap<String, Boolean>(1, 1.0f);
        capabilities.put(User.CAPABILITY_GUEST, Boolean.TRUE);
        
        return constructAlfrescoUser(json, properties, capabilities, null);
    }
}
