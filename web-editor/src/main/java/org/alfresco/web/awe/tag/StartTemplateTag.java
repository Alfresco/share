/*
 * #%L
 * Alfresco Web Editor
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */

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
