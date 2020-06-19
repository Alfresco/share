/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.awe.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webeditor.taglib.AbstractTemplateTag;

/**
 * Tag used to indicate an editable piece of content.
 * 
 * @author Gavin Cornwell
 */
public class MarkContentTag extends AbstractTemplateTag
{
    private static final long serialVersionUID = 1564711937667040715L;
    private static final Log logger = LogFactory.getLog(MarkContentTag.class);

    private String contentId;
    private String contentTitle;
    private String formId;
    private boolean nestedMarker = false;

    /**
     * Returns the identifier of the content to be edited
     * 
     * @return The identifier of the content to be edited
     */
    public String getId()
    {
        return this.contentId;
    }

    /**
     * Sets the identifier of the content to be edited
     * 
     * @param contentId The identifier of the content to be edited
     */
    public void setId(String contentId)
    {
        this.contentId = contentId;
    }

    /**
     * Returns the title of the content to be edited
     * 
     * @return The title of the content to be edited
     */
    public String getTitle()
    {
        return this.contentTitle;
    }

    /**
     * Sets the title of the content to be edited
     * 
     * @param title The title of the content to be edited
     */
    public void setTitle(String title)
    {
        this.contentTitle = title;
    }

    /**
     * Returns the identifier of the form to use to edit the content
     * 
     * @return The identifier of the form to use to edit the content
     */
    public String getFormId()
    {
        return this.formId;
    }

    /**
     * Sets the identifier of the form to use to edit the content
     * 
     * @param formId The identifier of the form to use to edit the content
     */
    public void setFormId(String formId)
    {
        this.formId = formId;
    }

    /**
     * Returns a flag to indicate whether the marker is nested within the content
     * to be edited.
     * 
     * @return true if the marker is nested
     */
    public boolean isNestedMarker()
    {
        return this.nestedMarker;
    }

    /**
     * Sets whether the marker is nested within the content to be edited.
     * 
     * @param nestedMarker true to indicate the marker is nested
     */
    public void setNestedMarker(boolean nestedMarker)
    {
        this.nestedMarker = nestedMarker;
    }

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

                // get the prefix URL to the AWE assets
                String urlPrefix = getWebEditorUrlPrefix();

                // generate a unique id for this marked content
                List<MarkedContent> markedContent = AlfrescoTagUtil.getMarkedContent(pageContext.getRequest());
                String markerIdPrefix = (String) this.pageContext.getRequest().getAttribute(
                     AlfrescoTagUtil.KEY_MARKER_ID_PREFIX);

                String redirectUrl = AlfrescoTagUtil.calculateRedirectUrl((HttpServletRequest) pageContext.getRequest());
                String editMarkerId = markerIdPrefix + "-" + (markedContent.size() + 1);

                // create marked content object and store
                MarkedContent content = new MarkedContent(editMarkerId, this.contentId, this.contentTitle, 
                     this.formId, this.nestedMarker);
                markedContent.add(content);

                AlfrescoTagUtil.writeMarkContentHtml(out, urlPrefix, redirectUrl, content);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Completed markContent rendering for: " + content);
                }
            }
            catch (IOException ioe)
            {
                throw new JspException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping markContent rendering as editing is disabled");
        }

        return SKIP_BODY;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    public void release()
    {
        super.release();
    }

}
