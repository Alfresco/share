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

package org.springframework.extensions.surf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Base resource content implementation
 * 
 * @author muzquiano
 * @author kevinr
 */
public class ResourceContentImpl implements ResourceContent
{
    final protected Resource resource;
    final protected String url;
    
    protected FrameworkBean frameworkUtil;
    
    /**
     * Constructor
     * 
     * @param resource
     * @param url
     */
    public ResourceContentImpl(Resource resource, String url, FrameworkBean frameworkUtil)
    {
        this.resource = resource;
        this.url = url;
        this.frameworkUtil = frameworkUtil;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.web.site.Content#getResource()
     */
    public Resource getResource()
    {
        return this.resource;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getReader()
     */
    public Reader getReader() throws IOException
    {
        Response response = getResponse();
        return new StringReader(response.getText());
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.Resource#getInputStream()
     */
    public InputStream getInputStream() 
        throws IOException
    {
        Response response = getResponse();
        return response != null ? response.getResponseStream() : null;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.resource.ResourceContent#getStringContent()
     */
    public String getStringContent() throws IOException
    {
        Response response = getResponse();
        return response != null ? response.getText() : null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceContent#getBytes()
     */
    public byte[] getBytes()
        throws IOException
    {
        Response response = getResponse();
        return response != null ? response.getText().getBytes(response.getEncoding()) : null;
    }
    
    
    /**
     * @return response object for this resource wrapper
     * @throws IOException
     */
    private Response getResponse() 
        throws IOException
    {
        Response response = null;
        
        String endpoint = resource.getEndpointId();
        
        // if there isn't an endpoint specified, we can try to use the http endpoint
        if (endpoint == null)
        {
            endpoint = "http";
        }
        
        Connector connector = null;
        
        // get the current request context
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        try
        {
            if (context == null)
            {
                connector = frameworkUtil.getConnector(endpoint);
            }
            else
            {
                connector = frameworkUtil.getConnector(context, endpoint);
            }
        }
        catch (ConnectorServiceException cse)
        {
            throw new IOException("Unable to obtain connector to endpoint: " + endpoint);
        }
        
        if (connector != null)
        {
            ConnectorContext connectorContext = new ConnectorContext();
            connectorContext.setMethod(HttpMethod.GET);
            
            response = connector.call(this.url, connectorContext);
        }
        
        return response;
    }
}
