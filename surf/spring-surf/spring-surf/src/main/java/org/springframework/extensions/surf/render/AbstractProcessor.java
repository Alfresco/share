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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.ProcessorContext.ProcessorDescriptor;
import org.springframework.extensions.webscripts.ProcessorModelHelper;

/**
 * Base implementation of a Processor.
 * 
 * @author Kevin Roast
 */
public abstract class AbstractProcessor implements Processor
{
    private static Log logger = LogFactory.getLog(AbstractProcessor.class);
    
    /**
     * <p>A <code>ProcessorModelHelper</code> is required to populate the model. It is supplied by the Spring Framework
     * providing that this bean is correctly configured.</p>
     */
    protected ProcessorModelHelper processorModelHelper;
    
    public void setProcessorModelHelper(ProcessorModelHelper processorModelHelper)
    {
        this.processorModelHelper = processorModelHelper;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#execute(org.alfresco.web.framework.render.ProcessorContext, org.alfresco.web.framework.render.RenderFocus)
     */
    public void execute(ProcessorContext processorContext, ModelObject object, RenderFocus focus)
        throws RendererExecutionException
    {
        if (focus == null || focus == RenderFocus.BODY)
        {
            executeBody(processorContext, object);
        }
        else if (focus == RenderFocus.ALL)
        {
            executeHeader(processorContext, object);
            executeBody(processorContext, object);
        }
        else if (focus == RenderFocus.HEADER)
        {
            executeHeader(processorContext, object);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext processorContext, ModelObject object)
        throws RendererExecutionException
    {
        // nothing
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#executeBody(org.alfresco.web.framework.render.ProcessorContext)
     */
    public abstract void executeBody(ProcessorContext processorContext, ModelObject object)
        throws RendererExecutionException;

    /**
     * <p>Helper method which returns the appropriate processor descriptor
     * from the given processor descriptor for the current render mode.</p>
     *
     * <p>For example, if the current render mode is "view", then this will
     * return the processor descriptor for the processor to be used during
     * "view" processing.  This descriptor contains all of the relevant
     * metadata for the processor about how to proceed.</p>
     *
     * <p>If the render mode is not "view" and a ProcessDescriptor cannot be found
     * then the ProcessDescriptor for the "view" render mode will be returned
     * (if available)</p>
     *
     * @param processorContext
     * @return processor descriptor
     */
    protected ProcessorDescriptor getRenderingDescriptor(ProcessorContext processorContext)
    {
        RequestContext context = processorContext.getRequestContext();
        RenderMode renderMode = context.getRenderMode();
        ProcessorDescriptor processDescriptor = processorContext.getDescriptor(renderMode);
        if (processDescriptor == null && renderMode != RenderMode.VIEW)
        {
        	// If a ProcessDescriptor has been requested for any RenderMode other than VIEW and
        	// no result can be found, then we will attempt to get the ProcessDescriptor for the
        	// VIEW RenderMode.
        	//
        	// This code has been added because when requesting a component that provides a non-VIEW
        	// mode but uses Chrome that does not process the same mode then an error will occur.
        	// This means that we are introducing the concept of VIEW as being the "default" render
        	// and this may not be the intention of the developer, however... since all tutorial examples
        	// use "view" as the mode this should not be a problem and should also ensure backwards
        	// compatibility. At the very least we will log a warning message so that the developer
        	// can see what has happened.
            logger.warn("Could not retrieve ProcessDescriptor for RenderMode: " + renderMode + ", attempting to retrieve using RenderMode: VIEW");
        	processDescriptor = processorContext.getDescriptor(RenderMode.VIEW);
        }

        return processDescriptor;
    }

    /**
     * Returns a configuration property from the rendering
     * processor descriptor.
     *
     * @param processorContext
     * @param propertyName
     *
     * @return property value as string
     */
    protected String getProperty(ProcessorContext processorContext, String propertyName)
    {
        ProcessorDescriptor descriptor = getRenderingDescriptor(processorContext);
        String value = descriptor.get(propertyName);

        // allow for simple variable substitution
        // ${mode.view.uri} = uri property value where mode="view"
        if (value != null)
        {
            String modeViewUri = getScalarProperty(processorContext, "uri", RenderMode.VIEW);
            if (value.indexOf("${mode.view.uri}") != -1)
            {
                value = value.replace("${mode.view.uri}", modeViewUri);
            }
        }

        return value;
    }

    private String getScalarProperty(ProcessorContext processorContext, String propertyName, RenderMode renderMode)
    {
        ProcessorDescriptor descriptor = processorContext.getDescriptor(renderMode);
        return descriptor.get(propertyName);
    }
}
