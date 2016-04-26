package org.alfresco.web.resolver.doclib;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.URLEncoder;

/**
 * Resolves which data url if to use when asking the repository for nodes in the document library's document list.
 *
 * @author ewinlof
 */
public class DefaultDoclistDataUrlResolver implements DoclistDataUrlResolver
{
    /**
     * The base path to the repository doclist webscript.
     */
    public String basePath = null;

    /**
     * The base path to the repository doclist webscript.
     *
     * @param basePath String
     */
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    /**
     * Returns the url to the repository doclist webscript to use.
     *
     * @param webscript The repository doclib2 webscript tp use, i.e. doclist or node
     * @param params doclib2 webscript specific parameters
     * @param args url parameters, i.e. pagination parameters
     * @return The url to use when asking the repository doclist webscript.
     */
    public String resolve(String webscript, String params, HashMap<String, String> args)
    {
        return basePath + "/" + webscript + "/" + URLEncoder.encodeUri(params) + getArgsAsParameters(args);
    }

    /**
     * Helper method that creates a url parameter string from a hash map.
     *
     * @param args The arguments that will be transformed to a string
     * @return A url parameter string
     */
    public String getArgsAsParameters(HashMap<String, String> args)
    {
        String urlParameters = "";
        // Need to reconstruct and encode original args
        if (args.size() > 0)
        {
            StringBuilder argsBuf = new StringBuilder(128);
            argsBuf.append('?');
            for (Map.Entry<String, String> arg: args.entrySet())
            {
                if (argsBuf.length() > 1)
                {
                     argsBuf.append('&');
                }
                argsBuf.append(arg.getKey())
                       .append('=')
                       .append(URLEncoder.encodeUriComponent(arg.getValue().replaceAll("%25","%2525")));
            }
            urlParameters = argsBuf.toString();
        }
        return urlParameters;
    }
}
