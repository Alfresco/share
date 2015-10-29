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

package org.springframework.extensions.surf.types;

import org.dom4j.Document;
import org.springframework.extensions.surf.ModelPersisterInfo;

/**
 * Default configuration implementation
 * 
 * @author muzquiano
 */
public class ConfigurationImpl extends AbstractModelObject implements Configuration
{
    /**
     * Instantiates a new configuration for the given xml document.
     * 
     * @param document the document
     */
    public ConfigurationImpl(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId() 
    {
        return TYPE_ID;
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Configuration#getSourceId()
     */
    public String getSourceId()
    {
        return getProperty(PROP_SOURCE_ID);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Configuration#setSourceId(java.lang.String)
     */
    public void setSourceId(String sourceId)
    {
        setProperty(PROP_SOURCE_ID, sourceId);
    }
}
