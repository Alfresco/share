
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
