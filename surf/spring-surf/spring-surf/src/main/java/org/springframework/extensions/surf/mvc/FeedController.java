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

package org.springframework.extensions.surf.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.WebScriptsPlatformException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.Base64;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.CredentialsImpl;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for feed requests. Feed requests are authenticated via Basic HTTP auth
 * to allow apps such as feed reader to access RSS feeds within a SURF app without
 * full authentication. It effectively authenticates the Guest users against the current
 * user session with the supplied Basic HTTP auth settings.
 * 
 * Any page requests made will still need to provide full authentication as required
 * by their respective endpoints. The feed controller is tied to the "alfresco-feed" endpoint.
 * 
 * @author Kevin Roast
 */
public class FeedController extends UrlViewController
{
    private static final String ENDPOINT_ALFRESCO_FEED = "alfresco-feed";
    
    protected ConnectorService connectorService;
    
    
    /**
     * Sets the connector service.
     * 
     * @param connectorService the new connector service
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res)
    {
        // check for HTTP authorisation request (i.e. RSS feeds etc.)
        String authorization = req.getHeader("Authorization");
        if (authorization == null || authorization.length() == 0)
        {
            authorizedResponseStatus(res);
            
            // no further processing as authentication is required but not provided
            // the browser will now prompt the user for appropriate credentials
            return null;
        }
        else
        {
            // user has provided authentication details with the request
            String[] authParts = authorization.split(" ");
            // test for a "negotiate" header - we will then suggest "basic" as the auth mechanism
            if (authParts[0].equalsIgnoreCase("negotiate"))
            {
               authorizedResponseStatus(res);
               
               // no further processing as authentication is required but not provided
               // the browser will now prompt the user for appropriate credentials
               return null;
            }
            if (!authParts[0].equalsIgnoreCase("basic"))
            {
                throw new WebScriptsPlatformException("Authorization '" + authParts[0] + "' not supported.");
            }
            
            String[] values = new String(Base64.decode(authParts[1])).split(":");
            if (values.length == 2)
            {
                // assume username and password passed as the parts and
                // build an unauthenticated authentication connector then
                // apply the supplied credentials to it
                try
                {
                    // generate the credentials based on the auth details provided
                    Credentials credentials = new CredentialsImpl(ENDPOINT_ALFRESCO_FEED);
                    String username = values[0];
                    credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                    credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, values[1]);
                    CredentialVault vault = connectorService.getCredentialVault(req.getSession(), username);
                    vault.store(credentials);
                    
                    // set USER_ID into the session
                    req.getSession().setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);
                    
                    // override endpoint that will be used to retrieve user details
                    RequestContext context = ThreadLocalRequestContext.getRequestContext();
                    context.getAttributes().put(RequestContext.USER_ENDPOINT, ENDPOINT_ALFRESCO_FEED);
                    
                    // set cache control for rss feeds - some feed readers *must* have a value set and it
                    // must not be no-cache!
                    res.setHeader("Cache-Control", "max-age=600, must-revalidate");
                }
                catch (Exception err)
                {
                    throw new WebScriptsPlatformException("Failed to provision connector.", err);
                }
            }
            else
            {
                authorizedResponseStatus(res);
                
                return null;
            }
        }
        
        return super.handleRequestInternal(req, res);
    }
    
    private void authorizedResponseStatus(HttpServletResponse res)
    {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
                "Requested endpoint requires authentication.");
        res.setHeader("WWW-Authenticate", "Basic realm=\"Alfresco\"");
    }
}