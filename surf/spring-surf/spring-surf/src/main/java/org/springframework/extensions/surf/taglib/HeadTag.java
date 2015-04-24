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

import java.io.UnsupportedEncodingException;

import javax.servlet.jsp.JspException;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RendererExecutionException;

/**
 * This tag is meant to be used during the processing of Template Instances.
 * 
 * The tag will look for components bound to this template and produce their
 * .head markup.  It will then print this to the output stream.
 * 
 * @author muzquiano
 */
public class HeadTag extends TagBase
{
    private static final long serialVersionUID = -6299508266443500315L;

    public int doStartTag() throws JspException
    {
        RequestContext context = getRequestContext();
        ModelObject object = getModelObject();

        try
        {
            print(getRenderService().renderTemplateHeaderAsString(context, object));
        }
        catch (RendererExecutionException ree)
        {
            throw new JspException("Unable to process downstream component head files", ree);
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new JspException("Unsupported encoding exception", uee);
        }
        
        return SKIP_BODY;
    }
}
