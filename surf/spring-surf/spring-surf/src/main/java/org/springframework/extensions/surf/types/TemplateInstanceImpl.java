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
import org.springframework.extensions.surf.RequestContext;

/**
 * Default template instance implementation
 * 
 * @author muzquiano
 */
public class TemplateInstanceImpl extends AbstractModelObject implements TemplateInstance
{
    // cached values
    private String templateTypeId = null;
    
    /**
     * Instantiates a new template instance for a given XML document
     * 
     * @param document the document
     */
    public TemplateInstanceImpl(String id, ModelPersisterInfo key, Document document)
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
     * @see org.alfresco.web.framework.types.TemplateInstance#getTemplateType()
     */
    public String getTemplateTypeId()
    {
        if (this.templateTypeId == null)
        {
            this.templateTypeId = getProperty(PROP_TEMPLATE_TYPE);
            
            // default to freemarker template type
            if (this.templateTypeId == null)
            {
                this.templateTypeId = "freemarker";
            }
        }
        
        return this.templateTypeId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.TemplateInstance#setTemplateType(java.lang.String)
     */
    public void setTemplateTypeId(String templateType)
    {
        setProperty(PROP_TEMPLATE_TYPE, templateType);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.TemplateInstance#getTemplateType(org.alfresco.web.framework.RequestContext)
     */
    public TemplateType getTemplateType(RequestContext context)
    {
        // either 'global', template or page
        return context.getObjectService().getTemplateType(getTemplateTypeId());
    }
}
