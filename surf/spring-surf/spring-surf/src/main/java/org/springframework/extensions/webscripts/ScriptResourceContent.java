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

package org.springframework.extensions.webscripts;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.resource.ResourceContent;
import org.springframework.extensions.surf.resource.ResourceJSONContent;
import org.springframework.extensions.surf.resource.ResourceXMLContent;
import org.xml.sax.InputSource;

import freemarker.ext.dom.NodeModel;

/**
 * Script wrapper for resource content object.
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class ScriptResourceContent extends ScriptBase
{
    private static Log logger = LogFactory.getLog(ScriptResourceContent.class);
    
    final private ResourceContent resourceContent;
    final private ScriptResource resource;
    
    
    /**
     * Constructor
     * 
     * @param context
     * @param resource
     * @param resourceContent
     */
    public ScriptResourceContent(RequestContext context, ScriptResource resource, ResourceContent resourceContent)
    {
        super(context);
        
        this.resourceContent = resourceContent;
        this.resource = resource;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.WebFrameworkScriptBase#buildProperties()
     */
    protected ScriptableMap<String, Serializable> buildProperties()
    {
        return null;
    }
    
    
    // --------------------------------------------------------------
    // JavaScript Properties
    
    public ScriptResource getResource()
    {
        return resource;
    }
    
    public String getString()
    {
        String result = null;
        try
        {
            result = this.resourceContent.getStringContent();
        }
        catch (IOException ioe)
        {
            logger.error(ioe);
        }
        return result;
    }
    
    public String getXml()
    {
        String xml = null;
        
        if (resourceContent instanceof ResourceXMLContent)
        {
            try
            {
                xml = ((ResourceXMLContent)resourceContent).getXml();
            }
            catch (IOException ioe)
            {
                logger.error(ioe);
            }
        }
        
        return xml;
    }
    
    public NodeModel getXmlNodeModel()
    {
        NodeModel nodeModel = null;
        
        try
        {
            nodeModel = NodeModel.parse(new InputSource(new StringReader(getXml())));
        }
        catch (Throwable err)
        {
            logger.error(err);
        }
        
        return nodeModel;
    }
    
    public String getJson()
    {
        String jsonString = null;
        
        if (resourceContent instanceof ResourceJSONContent)
        {
            try
            {
                jsonString = ((ResourceJSONContent)resourceContent).getJSONString();
            }
            catch (IOException ioe)
            {
                logger.error(ioe);
            }
        }
        
        return jsonString;
    }
}