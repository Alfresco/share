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
import org.springframework.extensions.surf.render.RenderService;

/**
 * <p>This tag will render the URL to the page or page type specified by the attributes provided. It DOES NOT
 * render an actual link (i.e. it does not render an HTML anchor tag) only the URL that represents a link
 * (relative to the server, i.e. it includes the context root of the application.</p>
 *
 * @author muzquiano
 * @author David Draper
 */
public class ObjectLinkTag extends AbstractObjectTag
{
    private static final long serialVersionUID = 6477844193223944439L;

    @Override
    protected int invokeRenderService(RenderService renderService, 
                                            RequestContext requestContext,
                                            ModelObject object) throws Exception
    {
        String link = renderService.generateLink(getPageType(), getPage(), getObject(), getFormat());
        pageContext.getOut().write(link);
        return SKIP_BODY;
    }
}
