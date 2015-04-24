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

package org.springframework.extensions.surf.render.bean;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.RegionRendererExecutionException;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.AbstractRenderer;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.types.Chrome;

/**
 * Bean responsible for rendering a region.
 * 
 * The bean should set up render context state and then hand off to a
 * region chrome renderer.  If no chrome is present, then it call call
 * through to the RenderUtil helper method directly.
 * 
 * @author muzquiano
 * @author David Draper
 */
public class RegionRenderer extends AbstractRenderer
{
    private static final Log logger = LogFactory.getLog(RegionRenderer.class);
    
    private ChromeRenderer chromeRenderer;
    
    public void setChromeRenderer(ChromeRenderer chromeRenderer)
    {
        this.chromeRenderer = chromeRenderer;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#header(org.alfresco.web.framework.render.RenderContext)
     */
    public void header(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        if (logger.isDebugEnabled())
        {
            super.header(context, object);
        }
        
        String regionId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
        try
        {
            getRenderService().renderRegionComponents(context, object, false);
        }
        catch (Exception ex)
        {
            throw new RegionRendererExecutionException("Unable to render region: " + regionId, ex);
        }
    }    
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#body(org.alfresco.web.framework.render.RendererContext)
     */
    public void body(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        String regionId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
        String regionChromeId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID);
        Boolean chromeless = (Boolean) context.getValue(WebFrameworkConstants.RENDER_DATA_CHROMELESS);

        try
        {
            if (chromeless)
            {
                getRenderService().renderRegionComponents(context, object, chromeless);
            }
            else
            {
                // fetch the appropriate chrome instance
                Chrome chrome = getRenderService().getRegionChrome(regionId, regionChromeId);
                
                // if we have chrome, process it
                if (chrome != null)
                {
                    this.chromeRenderer.render(context, chrome, RenderFocus.BODY);
                }
                else
                {
                    // call through directly to renderRegionComponents - as this is
                    // what the regionInclude tag in the region chrome will do
                    getRenderService().renderRegionComponents(context, object, chromeless);
                }
            }
            
            
            // post process call
            postProcess(context);
        }
        catch (Exception ex)
        {
            throw new RegionRendererExecutionException("Unable to render region: " + regionId, ex);
        }
    }    
    
    /**
     * Post-processing of regions
     */
    public void postProcess(RequestContext context)
        throws IOException
    {
    }
}