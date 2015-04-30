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
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.RenderService;

/**
 * @author muzquiano
 * @author David Draper
 */
public class ComponentTag extends RenderServiceTag
{
    private static final long serialVersionUID = -53733105000358307L;

    private String component = null;
    private String chrome = null;
    private boolean chromeless = false;

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
        this.component = null;
        this.chrome = null;
        this.chromeless = false;
    }
    
    public void setComponent(String componentId)
    {
        this.component = componentId;
    }
    
    public String getComponent()
    {
        return this.component;
    }
    
    public void setChrome(String chrome)
    {
        this.chrome = chrome;
    }

    public String getChrome()
    {
        return this.chrome;
    }
    
    public boolean isChromeless()
    {
        return chromeless;
    }

    public void setChromeless(boolean chromeless)
    {
        this.chromeless = chromeless;
    }
    
    @Override
    protected int invokeRenderService(RenderService renderService, RequestContext renderContext, ModelObject object)
            throws RequestDispatchException
    {
        renderService.renderComponent(renderContext, RenderFocus.BODY, this.component, this.chrome, this.chromeless);           
        return SKIP_BODY;
    }
    
}
