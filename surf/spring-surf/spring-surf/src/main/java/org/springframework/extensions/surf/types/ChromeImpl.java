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
 * Default chrome implementation
 * 
 * @author muzquiano
 */
public class ChromeImpl extends AbstractRenderableModelObject implements Chrome
{
    private static final long serialVersionUID = -2095286118767991371L;

    /**
     * Instantiates a new chrome for a given XML document
     * 
     * @param document the document
     */
    public ChromeImpl(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.AbstractModelObject#getTypeId()
     */
    public String getTypeId()
    {
        return Chrome.TYPE_ID;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Chrome#getChromeType()
     */
    public String getChromeType()
    {
        return getProperty(PROP_CHROME_TYPE);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Chrome#setChromeType(java.lang.String)
     */
    public void setChromeType(String chromeType)
    {
        setProperty(PROP_CHROME_TYPE, chromeType);
    }
}
