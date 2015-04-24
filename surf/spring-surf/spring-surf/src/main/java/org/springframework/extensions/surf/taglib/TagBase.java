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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.render.RenderContextRequest;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;

/**
 * @author muzquiano
 */
public abstract class TagBase extends BodyTagSupport implements Serializable
{
    private static final long serialVersionUID = -387841473754510763L;

    private PageContext pageContext = null;

    public void setPageContext(PageContext pageContext)
    {
        this.pageContext = pageContext;
    }

    protected PageContext getPageContext()
    {
        return this.pageContext;
    }

    public int doEndTag() throws JspException
    {
        return EVAL_PAGE;
    }
            
    /**
     * Gets the request context bound to the current request
     * 
     * @return
     * @throws JspException
     */
    protected RequestContext getRequestContext()
        throws JspException
    {
        return ThreadLocalRequestContext.getRequestContext();
    }
    
    protected RequestContext getRenderContext()
        throws JspException
    {
        HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();        
        return (RequestContext) request.getAttribute(RenderContextRequest.ATTRIB_RENDER_CONTEXT);
    }
    
    protected ModelObject getModelObject()
    {
        HttpServletRequest request = (HttpServletRequest) getPageContext().getRequest();        
        return (ModelObject) request.getAttribute(RenderContextRequest.ATTRIB_MODEL_OBJECT);        
    }
    
    protected RenderService getRenderService()
    {
        return ThreadLocalRequestContext.getRequestContext().getServiceRegistry().getRenderService();
    }

    protected JspWriter getOut()
    {
        return getPageContext().getOut();
    }

    protected void print(String str)
        throws JspException
    {
        try
        {
            JspWriter jspWriter = getOut();
            jspWriter.clearBuffer();
            jspWriter.print(str);
        }
        catch (Exception ex)
        {
            throw new JspException(ex);
        }
    }
    
    public void release()
    {
        this.pageContext = null;
        super.release();
    }    
}
