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

import javax.servlet.jsp.JspException;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.render.RenderService;

/**
 * <p>Renders an HTML anchor tag to a page or page type using the attributes provided. Correct usage of this tag should include
 * a body that will be rendered as the link - if no body is provided then nothing acionable will be rendered on the screen.</p>
 *
 * @author muzquiano
 * @author David Draper
 */
public class ObjectAnchorTag extends AbstractObjectTag
{
    private static final long serialVersionUID = 5263975705338049998L;

    private String target = null;

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return this.target;
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
        this.target = null;
    }
    
    /**
     * <p>Generates the URL to the requested resource (either a page or page type with optional object and format
     * request parameters) and then opens an HTML anchor tag using the generated URL as the HREF argument and setting
     * a target if provided. The body is then evaluated (which should render something to display as a link) and the
     * <code>doEndTag</code> method will close the HTML anchor tag.</p>
     *
     * @throws JspException wrapping any <code>IOException</code> that may occur writing the output.
     */    
    @Override
    protected int invokeRenderService(RenderService renderService, RequestContext requestContext, ModelObject object) throws Exception
    {
        try
        {
            String anchorStart = renderService.generateAnchorLink(getPageType(), getPage(), getObject(), getFormat(), target);
            pageContext.getOut().write(anchorStart);
        }
        catch (Exception ex)
        {
            throw new JspException(ex);
        }
        return EVAL_BODY_INCLUDE;
    }
    
    /**
     * <p>Closes the HTML anchor tag opened in <code>doStartTag</code></p>
     */
    public int doEndTag() throws JspException
    {
        try
        {
            pageContext.getOut().write("</A>");
        }
        catch (Exception ex)
        {
            throw new JspException(ex);
        }
        return EVAL_PAGE;
    }
}
