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
 * Tag used at the end of the body section of a page to indicate the end
 * of a page that potentially contains editable Alfresco content.
 * 
 * @author Gavin Cornwell
 */
public class EndTemplateTag extends AbstractTemplateTag
{
    private static final long serialVersionUID = -2917015141188997203L;
    
    protected static final Log logger = LogFactory.getLog(EndTemplateTag.class);

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException
    {
        if (isEditingEnabled())
        {
            try
            {
                Writer out = pageContext.getOut();

                // get the toolbar location from the request session
                String toolbarLocation = getToolbarLocation();

                // render config required for ribbon and marked content
                out.write("<script type=\"text/javascript\">\n");
                out.write("WEF.ConfigRegistry.registerConfig('org.springframework.extensions.webeditor.ui.ribbon',\n");
                out.write("{ position: \"");
                out.write(toolbarLocation);
                out.write("\" });\n");
                
                // add in custom configuration
                includeCustomConfiguration(out);
                
                // close render config               
                out.write("\n</script>");

                // request all the resources
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(getWebEditorUrlPrefix());
                out.write("/service/wef/resources\"></script>\n");
                
                if (logger.isDebugEnabled())
                    logger.debug("Completed endTemplate rendering");
            }
            catch (IOException ioe)
            {
                throw new JspException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping endTemplate rendering as editing is disabled");
        }

        return SKIP_BODY;
    }
    
    /**
     * Extension point for allowing inheriting classes to inject custom
     * configuration script
     * 
     * @param out writer
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
    }
}
