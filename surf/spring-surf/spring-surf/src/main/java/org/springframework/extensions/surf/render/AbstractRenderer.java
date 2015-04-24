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

package org.springframework.extensions.surf.render;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.ExtensibilityHttpResponse;

/**
 * <p>An abstract implementation of the Renderer interface that can be
 * extended by application developers for quick implementation.</p>
 *
 * @author David Draper
 * @author muzquiano
 */
public abstract class AbstractRenderer implements Renderer
{
    private RenderService renderService;

    public void setRenderService(RenderService renderService)
    {
        this.renderService = renderService;
    }

    /**
     * Gets the render service.
     *
     * @return the render service
     */
    public RenderService getRenderService()
    {
        return this.renderService;
    }

    /**
     * <p>This method directs rendering requests to the appropriate method for the <code>RenderFocus</code> provided. It
     * should only be overridden in exceptional circumstances as actual rendering should be carried out in the <code>body</code>
     * and <code>header</code> methods.</p>
     *
     * @param context The current <code>RequestContext</code> that contains all the information required to perform rendering.
     * @param object The current object to be rendered.
     * @param focus The focus to be rendered.
     *
     * @throws RendererExecutionException If the concrete rendering subclass methods fail to render the request.
     */
    public void render(RequestContext context, ModelObject object, RenderFocus focus) throws RendererExecutionException
    {
        // Set the class name of the current renderer in the context. This was originally added so that the componentInclude
        // and regionInclude tags would be able to determine the type of object being rendered to ensure that they were being
        // used correctly (and avoid going into an infinite loop or generating an OutOfMemoryException.
        context.setValue(WebFrameworkConstants.CURRENT_RENDERER, this.getClass().getName());
        if (focus == null || focus == RenderFocus.BODY)
        {
            body(context, object);
        }
        else if (focus == RenderFocus.ALL)
        {
            all(context, object);
        }
        else if (focus == RenderFocus.HEADER)
        {
            header(context, object);
        }
    }

    /**
     * <p>Renders both the header and body focus by calling the <code>header</code> and <code>body</code> methods
     * in succession. This method is invoked from the <code>render</code> method when a <code>RenderFocus</code>
     * of "ALL" is provided.</p>
     *
     * @param context The current <code>RequestContext</code> that contains all the information required to perform rendering.
     * @param object The current object to be rendered.
     * @param focus The focus to be rendered.
     *
     * @throws RendererExecutionException if thrown from the <code>header</code> or <code>body</code> methods.
     */
    public void all(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        header(context, object);
        body(context, object);
    }

    /**
     * <p>This method should be overridden by concrete subclasses to render the header focus for artifact
     * being handled by the renderer.</p>
     *
     * @param context The current <code>RequestContext</code> that contains all the information required to perform rendering.
     * @param object The current object to be rendered.
     *
     * @throws RendererExecutionException If rendering cannot be completed without error.
     */
    public void header(RequestContext context, ModelObject object) throws RendererExecutionException
    {
        // No default action.
    }

    /**
     * <p>This method must be implemented by concrete subclasses to render the body focus for artifact
     * being handled by the renderer.</p>
     *
     * @param context The current <code>RequestContext</code> that contains all the information required to perform rendering.
     * @param object The current object to be rendered.
     *
     * @throws RendererExecutionException If rendering cannot be completed without error.
     */
    public abstract void body(RequestContext context, ModelObject object) throws RendererExecutionException;

    /**
     * Commits the given string to the response output stream
     *
     * @param response the response
     * @param str the string
     *
     * @throws RendererExecutionException
     */
    protected void print(HttpServletResponse response, String str)
        throws RendererExecutionException
    {
        try
        {
            response.getWriter().print(str);
        }
        catch (IOException ex)
        {
            throw new RendererExecutionException("Unable to print string to response: " + str, ex);
        }
    }

    /**
     * Commits the given string to the response output stream
     *
     * @param context the render context
     * @param str the string
     *
     * @throws RendererExecutionException
     */
    protected static void print(RequestContext context, String str)
        throws RendererExecutionException
    {
        try
        {
            HttpServletResponse response = null;
            ExtensibilityModel extModel = context.getCurrentExtensibilityModel();
            if (extModel == null)
            {
                response = context.getResponse();
            }
            else
            {
                // TODO: Could probably improve this by not spinning off a new instance each time...
                response = new ExtensibilityHttpResponse(context.getResponse(), extModel);
            }
            response.getWriter().print(str);
        }
        catch (IOException ex)
        {
            throw new RendererExecutionException("Unable to output string to response: " + str, ex);
        }
    }
}