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

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.RequestContext;

/**
 * @author muzquiano
 */
final public class ProcessorContext
{
    final private RequestContext context;
    final private Map<RenderMode, ProcessorDescriptor> descriptors = new HashMap<RenderMode, ProcessorDescriptor>(4, 1.0f);

    public ProcessorContext(RequestContext context)
    {
        this.context = context;
    }

    public RequestContext getRequestContext()
    {
        return this.context;
    }

    public ProcessorDescriptor getDescriptor(RenderMode renderMode)
    {
    	ProcessorDescriptor processorDescriptor = this.descriptors.get(renderMode);
        return  processorDescriptor;
    }

    public void putDescriptor(RenderMode renderMode, ProcessorDescriptor descriptor)
    {
        this.descriptors.put(renderMode, descriptor);
    }

    public void removeDescriptor(RenderMode renderMode)
    {
        this.descriptors.remove(renderMode);
    }

    public void addDescriptor(RenderMode renderMode, Map<String, String> properties)
    {
        ProcessorDescriptor descriptor = new ProcessorDescriptor(properties);
        putDescriptor(renderMode, descriptor);
    }

    public void load(Renderable renderable)
    {
        RenderMode[] renderModes = renderable.getRenderModes();

        for (int i = 0; i < renderModes.length; i++)
        {
            Map<String, String> properties = renderable.getProcessorProperties(renderModes[i]);
            ProcessorDescriptor descriptor = new ProcessorDescriptor(properties);

            putDescriptor(renderModes[i], descriptor);
        }
    }

    public final static class ProcessorDescriptor
    {
        public final Map<String, String> properties;

        public ProcessorDescriptor()
        {
            this.properties = new HashMap<String, String>(4, 1.0f);
        }

        public ProcessorDescriptor(Map<String, String> properties)
        {
            this.properties = properties;
        }

        public void put(String key, String value)
        {
            this.properties.put(key, value);
        }

        public String get(String key)
        {
            return (String) this.properties.get(key);
        }

        public void remove(String key)
        {
            this.properties.remove(key);
        }

        public Map<String, String> map()
        {
            return this.properties;
        }
    }
}
