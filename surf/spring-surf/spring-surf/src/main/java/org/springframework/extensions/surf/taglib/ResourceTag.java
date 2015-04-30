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

import java.io.IOException;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderService;

/**
 * <p>Outputs the requested url representation of a resource. Named Resource Examples:</p>
 * <ul>
 * <li><{@code}alf:resource name="resourceName" /></li>
 * <li><{@code}alf:resource id="<{@code}protocol>://<{@code}endpoint>/<{@code}object id>" /></li>
 * <li><{@code}alf:resource protocol="<{@code}protocol>" endpoint="<{@code}endpoint>" object="<{@code}object id>" /></li>
 * <li><{@code}alf:resource name="resourceName" payload="metadata" /></li>
 * <li><{@code}alf:resource name="resourceName" payload="content" /></li>
 * </ul>
 * 
 * @author David Draper
 * @author muzquiano
 */
public class ResourceTag extends RenderServiceTag
{
    private static final long serialVersionUID = -8143039236653767731L;

    private String name = null;    
    private String id = null;    
    private String protocol = null;
    private String endpoint = null;
    private String object = null;    
    private String payload = null;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }
    
    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }
    
    public String getProtocol()
    {
        return this.protocol;
    }
    
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    public String getEndpoint()
    {
        return this.endpoint;
    }
    
    public void setObject(String object)
    {
        this.object = object;
    }
    
    public String getObject()
    {
        return this.object;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }
    
    public String getPayload()
    {
        return this.payload;
    }
    
    /**
     * <p>The life-cycle of a custom JSP tag is that the class is is instantiated when it is first required and
     * then re-used for all subsequent invocations. When a JSP has non-mandatory properties it means that the 
     * setters for those properties will not be called if the properties are not provided and the old values
     * will still be available which can corrupt the behaviour of the code. In order to prevent this from happening
     * we should override the <code>release</code> method to ensure that all instance variables are reset to their
     * initial state.</p>
     */
    @Override
    public void release()
    {
        super.release();
        this.endpoint = null;
        this.id = null;
        this.name = null;
        this.object = null;
        this.payload = null;
        this.protocol = null;
    }

    @Override
    protected int invokeRenderService(RenderService renderService, RequestContext renderContext, ModelObject modelObject)
            throws RequestDispatchException
    {
        String url = renderService.generateResourceURL(renderContext, modelObject, name, id, protocol, endpoint, object, payload);
        try
        {
            if (url != null)
            {
                pageContext.getOut().write(url);
            }
            else
            {
                // TODO: Output error message.
            }
                
        }
        catch (IOException e)
        {
            // TODO: Handle this gracefully!
        }
        return SKIP_BODY;
    }
}
