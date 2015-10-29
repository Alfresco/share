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
 * The "link" tag is provided so that multiple CSS resources requested by a component
 * can be batched up into a single "style" tag with multiple @import statements.
 * This mechanism is to workaround the MSIE bug described in KB262161 whereby IE browsers
 * will not parse more than 30 separate CSS resource tags.
 * 
 * @author mikeh
 * @author David Draper
 */
public class StylesheetTag extends RenderServiceTag
{
    private static final long serialVersionUID = -2372542871999800148L;

    private String rel = null;
    private String type = null;
    private String href = null;

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
        this.rel = null;
        this.type = null;
        this.href = null;
    }

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    public String getRel()
    {
        return this.rel;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public String getHref()
    {
        return href;
    }
    
    @Override
    protected int invokeRenderService(RenderService renderService, RequestContext renderContext, ModelObject object)
            throws Exception
    {
        renderService.updateStyleSheetImports(renderContext, href);
        return SKIP_BODY;
    }
}