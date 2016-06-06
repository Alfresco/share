/*
 * #%L
 * Alfresco Web Editor Samples
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

package org.customer;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Tag used to display some content from Alfresco.
 * 
 * @author Gavin Cornwell
 */
public class ContentTag extends AbstractCustomerTag
{
    private static final long serialVersionUID = -799036741074003523L;

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException
    {
        try
        {
            Writer out = pageContext.getOut();

            // setup http call to content webscript
            String url = this.getRepoUrl() + "/service/api/node/" + getNodeRef().replace("://", "/") + "/content";
            HttpClient client = getHttpClient();
            GetMethod getContent = new GetMethod(url);
            getContent.setDoAuthentication(true);

            try
            {
                // execute the method
                client.executeMethod(getContent);

                // render the content returned
                out.write(getContent.getResponseBodyAsString());
            }
            finally
            {
                getContent.releaseConnection();
            }
        }
        catch (IOException ioe)
        {
            throw new JspException(ioe.toString());
        }

        return SKIP_BODY;
    }
}
