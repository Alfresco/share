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
package org.customer;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag used to display the value of a property of an Alfresco node.
 * 
 * @author Gavin Cornwell
 */
public class PropertyTag extends AbstractCustomerTag
{
    private static final long serialVersionUID = -7972734141482504413L;
    private static final Log logger = LogFactory.getLog(PropertyTag.class);

    private String property;

    /**
     * Returns the name of the property to display
     * 
     * @return Name of the property to display
     */
    public String getProperty()
    {
        return property;
    }

    /**
     * Sets the name of the property to display
     * 
     * @param name The name of the property to display
     */
    public void setProperty(String name)
    {
        this.property = name;
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException
    {
        try
        {
            Writer out = pageContext.getOut();

            // setup http call to content webscript
            String url = this.getRepoUrl() + "/service/api/metadata?nodeRef=" + getNodeRef() + "&shortQNames=true";

            if (logger.isDebugEnabled())
                logger.debug("Getting metadata from: " + url);

            HttpClient client = getHttpClient();
            GetMethod getContent = new GetMethod(url);
            getContent.setDoAuthentication(true);

            try
            {
                // execute the method
                client.executeMethod(getContent);

                // get the JSON response
                String jsonResponse = getContent.getResponseBodyAsString();

                if (logger.isDebugEnabled())
                    logger.debug(jsonResponse);

                JSONObject json = new JSONObject(jsonResponse);
                JSONObject props = json.getJSONObject("properties");
                if (props.has(this.property))
                {
                    out.write(props.getString(this.property));
                }
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
        catch (JSONException je)
        {
            throw new JspException(je.toString());
        }

        return SKIP_BODY;
    }

    @Override
    public void release()
    {
        super.release();

        this.property = null;
    }
}
