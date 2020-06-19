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
