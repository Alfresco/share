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
import org.springframework.extensions.surf.exception.ComponentRendererExecutionException;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.AbstractRenderer;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.types.Chrome;
import org.springframework.extensions.surf.types.Component;

/**
 * Bean responsible for rendering a component.
 * 
 * This entails rendering Chrome and then placing the component inside of that chrome.
 * 
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class ComponentRenderer extends AbstractRenderer
{
    private static final Log logger = LogFactory.getLog(ComponentRenderer.class);
    
    private ChromeRenderer chromeRenderer;
    
    public void setChromeRenderer(ChromeRenderer chromeRenderer)
    {
        this.chromeRenderer = chromeRenderer;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#header(org.alfresco.web.framework.render.RequestContext)
     */
    public void header(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        if (logger.isDebugEnabled())
        {
            super.header(context, object);
        }
        
        Component component = (Component) object;
        try
        {
            // Render the component WITHOUT chrome.
            getRenderService().processComponent(context, RenderFocus.HEADER, component, true);
        }
        catch (Exception ex)
        {
            throw new ComponentRendererExecutionException("Unable to render component: " + component.getId(), ex);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#body(org.alfresco.web.framework.render.RendererContext)
     */
    public void body(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        Component component = (Component) object;
        Chrome chrome = (Chrome) context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_CHROME);
        Boolean chromeless = (Boolean) context.getValue(WebFrameworkConstants.RENDER_DATA_CHROMELESS);
        try
        {
            // if we have chrome, render it
            if ((chromeless == null || !chromeless) && chrome != null)
            {            
                this.chromeRenderer.render(context, chrome, RenderFocus.BODY);
            }
            else
            {
                getRenderService().processComponent(context, RenderFocus.BODY, component, chromeless);
            }
            
            // post process call
            postProcess(context);
        }
        catch (Exception ex)
        {
            throw new ComponentRendererExecutionException("Unable to render component: " + component.getId(), ex);
        }
    }
    
    /**
     * Post-processing of components
     */
    public void postProcess(RequestContext context)
        throws IOException
    {
    }
}