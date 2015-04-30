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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.util.XMLUtil;

/**
 * XML resource content implementation
 * 
 * @author muzquiano
 */
public class ResourceXMLContentImpl extends ResourceContentImpl implements ResourceXMLContent 
{
    public ResourceXMLContentImpl(Resource resource, String url, FrameworkBean frameworkUtil)
    {
        super(resource, url, frameworkUtil);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceXMLContent#getXml()
     */
    public String getXml() throws IOException
    {
        return getStringContent();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.resource.ResourceXMLContent#getDocument()
     */
    public Document getDocument() throws DocumentException, IOException
    {
        return XMLUtil.parse(getXml());
    }
}
