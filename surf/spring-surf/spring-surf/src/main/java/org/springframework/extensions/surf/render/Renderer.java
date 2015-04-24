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

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RendererExecutionException;

public interface Renderer
{
    /**
     * Executes the renderer in the given focus
     *
     * @param renderContext
     * @param focus
     *
     * @throws RendererExecutionException
     */
    public void render(RequestContext renderContext, ModelObject object, RenderFocus focus)
        throws RendererExecutionException;

    /**
     * Executes the renderer in the "all" mode
     *
     * @param rendererContext
     * @throws RendererExecutionException
     */
    public void all(RequestContext renderContext, ModelObject object)
        throws RendererExecutionException;

    /**
     * Executes the renderer in the "head" mode
     *
     * @param renderContext
     * @throws RendererExecutionException
     */
    public void header(RequestContext renderContext, ModelObject object)
        throws RendererExecutionException;

    /**
     * Executes the renderer in the "body" mode
     *
     * @param renderContext
     * @throws RendererExecutionException
     */
    public void body(RequestContext renderContext, ModelObject object)
        throws RendererExecutionException;
}
