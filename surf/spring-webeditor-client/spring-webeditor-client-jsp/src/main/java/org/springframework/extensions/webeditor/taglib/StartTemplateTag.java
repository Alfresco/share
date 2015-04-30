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

package org.springframework.extensions.webeditor.taglib;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tag used in the head section of a page to indicate that the page potentially
 * contains in-context editable content.
 * 
 * @author gavinc
 * @author muzquiano
 */
public class StartTemplateTag extends AbstractTemplateTag
{
    private static final long serialVersionUID = -7242916874303242800L;
    
    protected static final Log logger = LogFactory.getLog(StartTemplateTag.class);
    
    private String toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_TOP;

    /**
     * Returns the current value for the toolbar location
     * 
     * @return Toolbar location
     */
    public String getToolbarLocation()
    {
        return this.toolbarLocation;
    }

    /**
     * Sets the toolbar location
     * 
     * @param location Toolbar location
     */
    public void setToolbarLocation(String location)
    {
        if (location.equalsIgnoreCase(TemplateConstants.TOOLBAR_LOCATION_TOP))
        {
            this.toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_TOP;
        }
        else if (location.equalsIgnoreCase(TemplateConstants.TOOLBAR_LOCATION_LEFT))
        {
            this.toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_LEFT;
        }
        else if (location.equalsIgnoreCase(TemplateConstants.TOOLBAR_LOCATION_RIGHT))
        {
            this.toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_RIGHT;
        }
    }
    
    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException
    {
        if (isEditingEnabled())
        {
            // store the toolbar location into the request session
            this.pageContext.getRequest().setAttribute(TemplateConstants.REQUEST_ATTR_KEY_TOOLBAR_LOCATION, getToolbarLocation());

            try
            {
                Writer out = pageContext.getOut();

                // bootstrap WEF
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(getWebEditorUrlPrefix());
                out.write("/service/wef/bootstrap");
                if (isDebugEnabled())
                {
                    out.write("?debug=true");
                }
                
                // add in custom configuration
                includeCustomConfiguration(out);

				// end of bootstrap                
                out.write("\"></script>\n");
                
                if (logger.isDebugEnabled())
                    logger.debug("Completed startTemplate rendering");                
            }
            catch (IOException ioe)
            {
                throw new JspException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping startTemplate rendering as editing is disabled");
        }

        return SKIP_BODY;
    }

    /**
     * Allow tag extensions to insert custom javascript
     */
    public void includeCustomConfiguration(Writer out)
        throws IOException
    {
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    public void release()
    {
        super.release();

        this.toolbarLocation = null;
    }
}
