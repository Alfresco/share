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

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.AbstractRenderer;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.Renderable;
import org.springframework.extensions.surf.types.Chrome;

/**
 * Bean responsible for rendering chrome
 *
 * @author David Draper
 * @author muzquiano
 */
public class ChromeRenderer extends AbstractRenderer
{
    /**
     * <p>Overrides the method provided by <code>AbstractRenderer</code> to set a <code>RENDER_TYPE</code> entry into the
     * <code>RequestContext</code> where the value indicates whether Component or Region chrome is being rendered. This is
     * done so that the chrome implementation can use EITHER the <{@code}componentInclude> or <{@code}regionInclude> tag
     * without error (previously using the wrong type of include in the chrome would result in either an infinite loop
     * or an OutOfMemoryException).</p>
     * <p>After setting the <code>RENDER_TYPE</code> the <code>AbstractRenderer</code> implementation of the method is called
     * to direct the rendering request appropriately.</p>
     *
     * @param context The current context (to which the <code>RENDER_TYPE</code> will be added)
     * @param focus The current focus to render
     * @throws RendererExecutionException if the <code>CURRENT_RENDERER</code> key has not been set. This key should have
     * been set somewhere in the context ancestry (typically when a <code>RegionRenderer</code> or <code>ComponentRenderer</code>
     * is invoked.
     */
    @Override
    public void render(RequestContext context, ModelObject object, RenderFocus focus) throws RendererExecutionException
    {
        // Get the last rendered set (this should either be a ComponentRenderer or a RegionRenderer) and depending upon
        // the type set the RENDER_TYPE to either indicate that a component or region is being rendered...
        Object value = context.getValue(WebFrameworkConstants.CURRENT_RENDERER);
        if (value != null && value instanceof String)
        {
            String currentRenderer = (String) value;
            if (currentRenderer.equals(ComponentRenderer.class.getName()))
            {
                context.setValue(WebFrameworkConstants.RENDER_TYPE, WebFrameworkConstants.RENDER_COMPONENT);
            }
            else if (currentRenderer.equals(RegionRenderer.class.getName()))
            {
                context.setValue(WebFrameworkConstants.RENDER_TYPE, WebFrameworkConstants.RENDER_REGION);
            }
            else if (currentRenderer.equals(WebFrameworkConstants.RENDER_SUB_COMPONENT))
            {
                // Although this isn't the successful comparison of a renderer, it should still work providing
                // that the RenderService correctly sets the render type value in the context...
                context.setValue(WebFrameworkConstants.RENDER_TYPE, WebFrameworkConstants.RENDER_SUB_COMPONENT);
            }
            else
            {
                // Sanity check - a CURRENT_RENDERER should have been set (this should be set by the AbstractRenderer
                // implementation of render. If it has not been set then it indicates that region or component rendering
                // has been bypassed or the code changed (it is possible to bypass the setting of this value if body, all or
                // header methods are called directly.
                // TODO: Consider changing the access to the "body", "all" and "header" methods to "protected" to ensure that render is always called.
                throw new RendererExecutionException("The current context has not been provided with a current renderer value");
            }
        }
        super.render(context, object, focus);
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractRenderer#body(org.alfresco.web.framework.render.RendererContext)
     */
    public void body(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        Chrome chrome = (Chrome) object;
        getRenderService().processRenderable(context, RenderFocus.BODY, object, (Renderable) chrome);
    }    
}