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

package org.springframework.extensions.surf.site;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.AbstractProcessor;
import org.springframework.extensions.surf.render.ProcessorContext;

/**
 * Processor class which renders a null result.
 * 
 * Used to render vacant "empty" regions when there are no bound components.
 * A common use case for application that are not "design focused".
 * 
 * @author Kevin Roast
 */
public class EmptyRegionRenderer extends AbstractProcessor
{
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext pc, ModelObject object)
        throws RendererExecutionException
    {
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeBody(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeBody(ProcessorContext pc, ModelObject object)
        throws RendererExecutionException
    {
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#exists(org.alfresco.web.framework.render.ProcessorContext)
     */
    public boolean exists(ProcessorContext pc, ModelObject object)
    {
        return true;
    }
}