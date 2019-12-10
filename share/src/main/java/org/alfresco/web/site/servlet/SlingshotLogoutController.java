/*
 * #%L
 * Alfresco Share WAR
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
package org.alfresco.web.site.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.LogoutController;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.ModelAndView;

/**
 * Share specific override of the SpringSurf dologout controller.
 * <p>
 * The implementation ensures Alfresco tickets are removed if appropriate and
 * then delegates to the SpringSurf implementation for framework cleanup.
 * 
 * @author Kevin Roast
 */
public class SlingshotLogoutController extends LogoutController
{
    private static Log logger = LogFactory.getLog(SlingshotLogoutController.class);
    protected ConnectorService connectorService;
    
    
    /**
     * @param connectorService   the ConnectorService to set
     */
    public void setConnectorService(ConnectorService connectorService)
    {
        this.connectorService = connectorService;
    }
    
    
    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
           HttpSession session = request.getSession(false);
           if (session != null)
           {
               // retrieve the current user ID from the session
               String userId = (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
               
               if (userId != null)
               {
                  // get the ticket from the Alfresco connector
                  Connector connector = connectorService.getConnector(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID, userId, session);
                  String ticket = connector.getConnectorSession().getParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET);
                  
                  if (ticket != null)
                  {
                      // if we found a ticket, then expire it via REST API - not all auth will have a ticket i.e. SSO
                      Response res = connector.call("/api/login/ticket/" + ticket, new ConnectorContext(HttpMethod.DELETE));
                      if (logger.isDebugEnabled())
                          logger.debug("Expired ticket: " + ticket + " user: " + userId + " - status: " + res.getStatus().getCode());
                  }
               }
           }
        }
        finally
        {
            return super.handleRequestInternal(request, response);
        }
    }
}