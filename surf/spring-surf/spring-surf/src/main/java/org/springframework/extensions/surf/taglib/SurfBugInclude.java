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
package org.springframework.extensions.surf.taglib;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.RenderService;

/**
 * <p>A custom JSP tag that should only be used in <code>SurfBug</code> instances to 
 * render whatever component is being debugged</p>
 * @author David Draper
 */
public class SurfBugInclude extends RenderServiceTag
{
    private static final long serialVersionUID = 7680743024251636060L;

    @Override
    protected int invokeRenderService(RenderService renderService, 
                                      RequestContext renderContext, 
                                      ModelObject object) throws RendererExecutionException
    {
        renderService.renderSurfBugInclude(renderContext);
        return SKIP_BODY;
    }
}
