
package org.alfresco.web.awe.tag;

import java.io.IOException;
import java.io.Writer;

/**
 * Tag used in the head section of a page to indicate that the page potentially
 * contains editable Alfresco content.
 * 
 * @author Gavin Cornwell
 */
public class StartTemplateTag extends org.springframework.extensions.webeditor.taglib.StartTemplateTag
{
    private static final long serialVersionUID = 1010262474360098833L;

    private static final String ALF = "alf_";

    /**
     * @see org.springframework.extensions.webeditor.taglib.StartTemplateTag#includeCustomConfiguration(java.io.Writer)
     */
    public void includeCustomConfiguration(Writer out) throws IOException
    {
        // store an id prefix to use in all content marker tags used on the page
        this.pageContext.getRequest().setAttribute(AlfrescoTagUtil.KEY_MARKER_ID_PREFIX, ALF + System.currentTimeMillis());
    }
}
