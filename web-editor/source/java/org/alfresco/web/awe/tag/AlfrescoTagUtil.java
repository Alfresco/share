package org.alfresco.web.awe.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Tag utilities for Alfresco Web Editor
 * 
 * @author muzquiano
 */
public class AlfrescoTagUtil
{
    public static final String KEY_MARKER_ID_PREFIX = "awe_marker_id_prefix";
    public static final String KEY_EDITABLE_CONTENT = "awe_editable_content";

    /**
     * Returns the list of marked content that has been discovered.
     * <p>
     * This list is built up as each markContent tag is encountered.
     * </p>
     * 
     * @return List of MarkedContent objects
     */
    @SuppressWarnings("unchecked")
    public static List<MarkedContent> getMarkedContent(ServletRequest request)
    {
        List<MarkedContent> markedContent = (List<MarkedContent>) request.getAttribute(KEY_EDITABLE_CONTENT);

        if (markedContent == null)
        {
            markedContent = new ArrayList<MarkedContent>();
            request.setAttribute(KEY_EDITABLE_CONTENT, markedContent);
        }

        return markedContent;
    }

    public static void writeMarkContentHtml(Writer out, String urlPrefix, String redirectUrl, MarkedContent content)
            throws IOException, UnsupportedEncodingException
    {
        String contentId = content.getContentId();
        String contentTitle = content.getContentTitle();
        String formId = content.getFormId();
        String editMarkerId = content.getMarkerId();
        
        // Hide initially, in case we need to log in or user does not want to
        // log in
        out.write("<span class=\"alfresco-content-marker\" style=\"display: none\" id=\"");
        out.write(editMarkerId);
        out.write("\">");

        // render edit link for content
        out.write("<a class=\"alfresco-content-edit\" href=\"");
        out.write(urlPrefix);
        out.write("/page/metadata?nodeRef=");
        out.write(contentId);
        out.write("&js=off");
        if (contentTitle != null)
        {
            out.write("&title=");
            out.write(URLEncoder.encode(contentTitle, "UTF-8"));
        }

        if (redirectUrl != null)
        {
            out.write("&redirect=");
            out.write(redirectUrl);
        }

        if (formId != null)
        {
            out.write("&formId=");
            out.write(formId);
        }

        out.write("\"><img src=\"");
        out.write(urlPrefix);
        out.write("/res/awe/images/edit.png\" alt=\"");
        out.write(encode(contentTitle == null ? "" : contentTitle));
        out.write("\" title=\"");
        out.write(encode(contentTitle == null ? "" : contentTitle));
        out.write("\"border=\"0\" /></a>");

        // render create link for content
        out.write("<a class=\"alfresco-content-new\" href=\"");
        out.write(urlPrefix);
        out.write("/page/metadata?nodeRef=");
        out.write(contentId);
        out.write("&js=off");
        if (contentTitle != null)
        {
            out.write("&title=");
            out.write(URLEncoder.encode(contentTitle, "UTF-8"));
        }

        if (redirectUrl != null)
        {
            out.write("&redirect=");
            out.write(redirectUrl);
        }

        if (formId != null)
        {
            out.write("&formId=");
            out.write(formId);
        }

        out.write("\"><img src=\"");
        out.write(urlPrefix);
        out.write("/res/awe/images/new.png\" alt=\"");
        out.write(encode(contentTitle == null ? "" : contentTitle));
        out.write("\" title=\"");
        out.write(encode(contentTitle == null ? "" : contentTitle));
        out.write("\"border=\"0\" /></a>");

        // render delete link for content
        out.write("<a class=\"alfresco-content-delete\" href=\"");
        out.write(urlPrefix);
        // TODO
        out.write("/page/metadata?nodeRef=");
        out.write(contentId);
        out.write("&js=off");
        if (contentTitle != null)
        {
            out.write("&title=");
            out.write(URLEncoder.encode(contentTitle, "UTF-8"));
        }

        if (redirectUrl != null)
        {
            out.write("&redirect=");
            out.write(redirectUrl);
        }

        if (formId != null)
        {
            out.write("&formId=");
            out.write(formId);
        }

        out.write("\"><img src=\"");
        out.write(urlPrefix);
        out.write("/res/awe/images/delete.png\" alt=\"");
        out.write(encode(contentTitle == null ? "" : contentTitle));
        out.write("\" title=\"");
        out.write(encode(contentTitle == null ? "" : contentTitle));
        out.write("\"border=\"0\" /></a>");

        out.write("</span>\n");
    }

    /**
     * Calculates the redirect url for form submission, this will
     * be the current request URL.
     * 
     * @return The redirect URL
     */
    public static String calculateRedirectUrl(HttpServletRequest request)
    {
        // NOTE: This may become configurable in the future, for now
        //       this just returns the current page's URI

        String redirectUrl = null;
        try
        {
            StringBuffer url = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null)
            {
                url.append("?").append(queryString);
            }

            redirectUrl = URLEncoder.encode(url.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            // just return null
        }

        return redirectUrl;
    }
    
    /**
     * Encodes the given string, so that it can be used within an HTML page.
     * 
     * @param string     the String to convert
     */
    public static String encode(String string)
    {
        if (string == null)
        {
            return "";
        }

        StringBuilder sb = null;      // create on demand
        String enc;
        char c;
        for (int i = 0; i < string.length(); i++)
        {
            enc = null;
            c = string.charAt(i);
            switch (c)
            {
                case '"': enc = "&quot;"; break;    //"
                case '&': enc = "&amp;"; break;     //&
                case '<': enc = "&lt;"; break;      //<
                case '>': enc = "&gt;"; break;      //>

                case '\u20AC': enc = "&euro;";  break;
                case '\u00AB': enc = "&laquo;"; break;
                case '\u00BB': enc = "&raquo;"; break;
                case '\u00A0': enc = "&nbsp;"; break;

                default:
                    if (((int)c) >= 0x80)
                    {
                        //encode all non basic latin characters
                        enc = "&#" + ((int)c) + ";";
                    }
                    break;
            }

            if (enc != null)
            {
                if (sb == null)
                {
                    String soFar = string.substring(0, i);
                    sb = new StringBuilder(i + 16);
                    sb.append(soFar);
                }
                sb.append(enc);
            }
            else
            {
                if (sb != null)
                {
                    sb.append(c);
                }
            }
        }

        if (sb == null)
        {
            return string;
        }
        else
        {
            return sb.toString();
        }
    }

    
}
