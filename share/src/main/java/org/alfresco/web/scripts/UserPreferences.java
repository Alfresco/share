/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
 */
package org.alfresco.web.scripts;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Script host object to retrieve the current user preferences JSON response.
 * <p>
 * The preferences service response is stored and cached in the current RequestContext.
 * 
 * @author Kevin Roast
 */
public class UserPreferences extends BaseProcessorExtension
{
    private static final String USER_PREFERENCES = "_alfUserPreferences";

    /**
     * @return JSON preferences strings for the current user
     */
    public String getValue()
    {
        // the test for cached user preferences is per request thread
        // therefore no locking is required around the test logic
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        String prefs = (String)rc.getValue(USER_PREFERENCES);
        if (prefs == null)
        {
            // set to a safe empty value for now - but don't cache the response
            // this is valid to return for the guest user and on server response failure
            prefs = "{}";
            
            if (!AuthenticationUtil.isGuest(rc.getUserId()))
            {
                // retrieve the preferences and store on the request context for later usage within
                // the scope of the current request - multiple components may need the values
                try
                {
                    Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", rc.getUserId(), ServletUtil.getSession());
                    Response response = conn.call("/api/people/" + URLEncoder.encode(rc.getUserId()) + "/preferences");
                    if (response.getStatus().getCode() == Status.STATUS_OK)
                    {
                        prefs = response.getResponse();
                        rc.setValue(USER_PREFERENCES, prefs);
                    }
                }
                catch (ConnectorServiceException e)
                {
                    throw new AlfrescoRuntimeException("Unable to retrieve user preferences: " + e.getMessage(), e);
                }
            }
        }
        return prefs;
    }
}