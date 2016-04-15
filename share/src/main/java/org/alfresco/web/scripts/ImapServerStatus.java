package org.alfresco.web.scripts;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Slingleton scripting host object provided to retrieve the value of the
 * IMAP Server enabled status from the Alfresco repository.
 * 
 * @author Kevin Roast
 */
@SuppressWarnings("serial")
public class ImapServerStatus extends SingletonValueProcessorExtension<Boolean>
{
    private static Log logger = LogFactory.getLog(ImapServerStatus.class);
    
    
    /**
     * @return the enabled status of the IMAP server
     */
    public boolean getEnabled()
    {
        return getSingletonValue();
    }
    
    @Override
    protected Boolean retrieveValue(String userId, String storeId) throws ConnectorServiceException
    {
        boolean enabled = false;
        
        // initiate a call to retrieve the server status from the repository
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());
        final Response response = conn.call("/imap/servstatus");
        if (response.getStatus().getCode() == Status.STATUS_OK)
        {
            enabled = (response.getText().equals("enabled"));
            logger.info("Successfully retrieved IMAP server status from Alfresco: " + response.getText());
        }
        else
        {
            throw new AlfrescoRuntimeException("Unable to retrieve IMAP server status from Alfresco: " + response.getStatus().getCode());
        }
        
        return enabled;
    }

    @Override
    protected String getValueName()
    {
        return "IMAP server status";
    }
}
