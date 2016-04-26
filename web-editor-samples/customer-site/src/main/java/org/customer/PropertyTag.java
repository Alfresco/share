
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
