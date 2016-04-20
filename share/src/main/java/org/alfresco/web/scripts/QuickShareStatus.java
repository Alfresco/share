package org.alfresco.web.scripts;

import java.io.Serializable;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Singleton scripting host object provided to retrieve the value of the
 * Quick Share enabled status configuration from the Alfresco repository.
 * 
 * @author sergey.shcherbovich
 */
@SuppressWarnings("serial")
public class QuickShareStatus extends SingletonValueProcessorExtension<Boolean> implements Serializable
{
    private static Log logger = LogFactory.getLog(QuickShareStatus.class);
    
    /**
     * @return the enabled status of the Quick Share feature
     */
    public boolean getEnabled()
    {
        return getSingletonValue();
    }

    @Override
    protected Boolean retrieveValue(final String userId, final String storeId) throws ConnectorServiceException
    {
        boolean enabled = false;
        
        // initiate a call to retrieve the dictionary from the repository
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());
        final Response response = conn.call("/quickshare/enabled");
        if (response.getStatus().getCode() == Status.STATUS_OK)
        {
            logger.info("Successfully retrieved quick share information from Alfresco.");
            
            try
            {
                // Extract quick share webscript information
                final JSONObject json = new JSONObject(response.getResponse());
                if (json.has("enabled"))
                {
                    enabled = json.getBoolean("enabled");
                }
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException(e.getMessage(), e);
            }
        }
        else
        {
            throw new AlfrescoRuntimeException("Unable to retrieve quick share information from Alfresco: " + response.getStatus().getCode());
        }
        
        return enabled;
    }

    @Override
    protected String getValueName()
    {
        return "Quick Share enabled";
    }
}
