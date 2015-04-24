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
import org.springframework.extensions.surf.render.AbstractRenderableModelObject;

/**
 * Default template type implementation
 * 
 * @author muzquiano
 */
public class TemplateTypeImpl extends AbstractRenderableModelObject implements TemplateType
{
    /**
     * Instantiates a new template type for a given XML document
     * 
     * @param document the document
     */
    public TemplateTypeImpl(String id, ModelPersisterInfo key, Document document)
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
     * @see org.alfresco.web.framework.types.TemplateType#getURI()
     */
    public String getURI()
    {
        return getProperty(PROP_URI);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.TemplateType#setURI(java.lang.String)
     */
    public void setURI(String uri)
    {
        setProperty(PROP_URI, uri);
    }
}
