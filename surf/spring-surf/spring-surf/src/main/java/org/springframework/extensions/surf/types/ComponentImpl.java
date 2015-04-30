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
import org.springframework.extensions.surf.ModelHelper;
import org.springframework.extensions.surf.ModelPersisterInfo;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.render.AbstractRenderableModelObject;
import org.springframework.extensions.surf.render.RenderUtil;
import org.springframework.extensions.webscripts.WebScript;

/**
 * Default component implementation
 * 
 * @author muzquiano
 */
public class ComponentImpl extends AbstractRenderableModelObject implements Component
{
    private static final long serialVersionUID = -5779261897878106804L;

    // cached values
    private String regionId = null;
    private String scope = null;
    private String sourceId = null;
    private String componentTypeId = null;
    
    /**
     * Instantiates a new component for a given XML document.
     * 
     * @param document the document
     */
    public ComponentImpl(String id, ModelPersisterInfo key, Document document)
    {
        super(id, key, document);
        setGUID(id);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.model.AbstractModelObject#getTypeName()
     */
    public String getTypeId()
    {
        return TYPE_ID;
    }    

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getRegionId()
     */
    public String getRegionId()
    {
        if (this.regionId == null)
        {
            this.regionId = getProperty(PROP_REGION_ID);
        }
        return this.regionId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setRegionId(java.lang.String)
     */
    public void setRegionId(String regionId)
    {
        setProperty(PROP_REGION_ID, regionId);
        this.regionId = regionId;
        
        // regenerate the id for this component when the region id changes
        regenerateId();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getSourceId()
     */
    public String getSourceId()
    {
        if (this.sourceId == null)
        {
            this.sourceId = getProperty(PROP_SOURCE_ID); 
        }
        return this.sourceId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setSourceId(java.lang.String)
     */
    public void setSourceId(String sourceId)
    {
        setProperty(PROP_SOURCE_ID, sourceId);
        this.sourceId = sourceId;
        
        // regenerate the id for this component when the source id changes
        regenerateId();        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getScope()
     */
    public String getScope()
    {
        if (this.scope == null)
        {
            this.scope = getProperty(PROP_SCOPE);
        }
        return this.scope;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setScope(java.lang.String)
     */
    public void setScope(String scope)
    {
        setProperty(PROP_SCOPE, scope);
        this.scope = scope;
        
        // regenerate the id for this component when the source changes
        regenerateId();        
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getComponentTypeId()
     */
    public String getComponentTypeId()
    {
        if (this.componentTypeId == null)
        {
            this.componentTypeId = getProperty(PROP_COMPONENT_TYPE_ID);
            
            // default to web script component type
            if (this.componentTypeId == null)
            {
                this.componentTypeId = "webscript";
            }
        }
        
        return this.componentTypeId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setComponentTypeId(java.lang.String)
     */
    public void setComponentTypeId(String componentTypeId)
    {
        setProperty(PROP_COMPONENT_TYPE_ID, componentTypeId);
        this.componentTypeId = componentTypeId;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getChrome()
     */
    public String getChrome()
    {
        return getProperty(PROP_CHROME);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setChrome(java.lang.String)
     */
    public void setChrome(String chrome)
    {
        setProperty(PROP_CHROME, chrome);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getURL()
     */
    public String getURL()
    {
        String url = getProperty(PROP_URL);
        if (url == null)
        {
            url = getProperty(PROP_URI);
        }
        return url;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setURL(java.lang.String)
     */
    public void setURL(String url)
    {
        if (url != null)
        {
            setProperty(PROP_URL, url);
        }
    }
    
    public String getURI()
    {
        String uri = getProperty(PROP_URI);
        if (uri == null)
        {
            uri = getProperty(PROP_URL);
        }
        return uri;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setURL(java.lang.String)
     */
    public void setURI(String uri)
    {
        if (uri != null)
        {
            setProperty(PROP_URL, uri); // Note the use of PROP_URL - this allows URI and URL to be used interchangeably    
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getGUID()
     */
    public String getGUID()
    {
        return getProperty(PROP_GUID);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#setGUID(java.lang.String)
     */
    public void setGUID(String guid)
    {
        setProperty(PROP_GUID, guid);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getSourceObject(org.alfresco.web.framework.RequestContext)
     */
    public Object getSourceObject(RequestContext context)
    {
        return RenderUtil.getComponentBindingSourceObject(context, this);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.Component#getComponentType(org.alfresco.web.framework.RequestContext)
     */
    public ComponentType getComponentType(RequestContext context)
    {
        return context.getObjectService().getComponentType(getComponentTypeId());
    }
    
    /**
     * Regenerate the ID for this component
     */
    protected void regenerateId()
    {
        String id = RenderUtil.generateComponentId(this.scope, this.regionId, this.sourceId);
        if (id == null)
        {
            id = getGUID();
            if (id == null)
            {
                id = ModelHelper.newGUID();
                setGUID(id);
            }
        }
        this.id = id;
    }
    
    @Override
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.types.AbstractModelObject#setId(java.lang.String)
     */
    public void setId(String id)
    {
        // do not allow manual assignment of component ids
    }

    private WebScript resolvedWebScript;
    
    public void setResolvedWebScript(WebScript webScript)
    {
        this.resolvedWebScript = webScript;
        
    }

    public WebScript getResolvedWebScript()
    {
        return this.resolvedWebScript;
    }

    public String getIndex()
    {
        return getProperty(PROP_INDEX);
    }

    public void setIndex(String index)
    {
        setProperty(PROP_INDEX, index);
    }

    public int compareTo(Component o)
    {
        int oIndex = 50;
        int thisIndex = 50;
        try
        {
            oIndex = Integer.valueOf(o.getIndex());
        }
        catch(NumberFormatException e)
        {
            oIndex = 50;
        }
        
        try
        {
            thisIndex = Integer.valueOf(getIndex());
        }
        catch (NumberFormatException e)
        {
            thisIndex = 50;
        }
        
        return thisIndex - oIndex;
    }
}