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
package org.alfresco.web.scripts;

import java.io.Serializable;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.EditionInterceptor;
import org.alfresco.web.site.EditionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Singleton scripting host object provided to retrieve the value of the
 * Sync Mode configuration from the Alfresco repository.
 * 
 * @author Kevin Roast
 */
@SuppressWarnings("serial")
public class SyncModeConfig extends SingletonValueProcessorExtension<String> implements Serializable
{
    private static Log logger = LogFactory.getLog(SyncModeConfig.class);
    
    
    /**
     * @return the Sync Mode configuration from the Alfresco repository.<p>
     *         Will be one of: CLOUD, ON_PREMISE, OFF
     */
    public String getValue()
    {
        return getSingletonValue();
    }

    @Override
    protected String retrieveValue(final String userId, final String storeId) throws ConnectorServiceException
    {
        String syncModeConfig = "OFF";
        
        // Sync requires Enterprise features. Sync config is missing on other editions, so default to off.
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        String edition = ((EditionInfo)rc.getValue(EditionInterceptor.EDITION_INFO)).getEdition();
        
        // Sync requires Enterprise features. Sync config is missing on other editions, so default to off.
        if (EditionInterceptor.ENTERPRISE_EDITION.equals(edition))
        {
            // initiate a call to retrieve the sync mode from the repository
            final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco");
            final Response response = conn.call("/enterprise/sync/config");
            if (response.getStatus().getCode() == Status.STATUS_OK)
            {
                try
                {
                    // extract sync mode
                    JSONObject json = new JSONObject(response.getResponse());
                    if (json.has("syncMode"))
                    {
                        syncModeConfig = json.getString("syncMode");
                        logger.info("Successfully retrieved Sync Mode configuration from Alfresco: " + syncModeConfig);
                    }
                    else
                    {
                        logger.error("Unexpected response from '/enterprise/sync/config' - did not contain expected 'syncMode' value.");
                    }
                }
                catch (JSONException e)
                {
                    throw new AlfrescoRuntimeException(e.getMessage(), e);
                }
            }
            else
            {
               throw new AlfrescoRuntimeException("Unable to retrieve Sync Mode configuration from Alfresco: " + response.getStatus().getCode());
            }
        }
        
        return syncModeConfig;
    }

    @Override
    protected String getValueName()
    {
        return "Sync Mode configuration";
    }
}
