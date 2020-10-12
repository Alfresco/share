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
package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.util.UrlUtils;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive to output the url of an asset or section. Usage:
 * <@makeurl section=xxx/> or <@makeurl asset=xxx force=long|short rendition=rrr attach=true|false />
 * 
 * If the variant that specifies an asset rather than a section is used then there are three optional parameters
 * that may be specified:
 *   force: "long" forces the URL to be the full path to the asset, whereas as "short" forces the URL to 
 *      be of the form "asset/<id>/name.ext"
 *   rendition: If specified this causes a "rendition" query parameter to be appended to the URL with 
 *      the specified value. By default, when this is detected on a request it causes the named rendition 
 *      of the addressed asset to be delivered.
 *   attach: An optional boolean parameter that causes an "attach" query parameter to be appended to the 
 *      generated URL with the specified value. The default behaviour when such a URL is requested is to 
 *      return the asset as an attachment to the response. This typically causes a browser to prompt the 
 *      user to save the asset rather than opening the asset directly in the browser. 
 * 
 * @author Brian
 * @author Chris Lack
 */
public class UrlDirective implements TemplateDirectiveModel
{

    private UrlUtils urlUtils;

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException
    {
        if (params.size() < 1 && params.size() > 2)
            throw new TemplateModelException("url directive expects one or two parameters");

        StringModel assetParam = (StringModel) params.get("asset");
        StringModel sectionParam = (StringModel) params.get("section");


        if ((assetParam == null || !(assetParam.getWrappedObject() instanceof Asset))
                && (sectionParam == null || !(sectionParam.getWrappedObject() instanceof Section)))
        {
            throw new TemplateModelException("url directive expects asset or section parameter");
        }

        SimpleScalar forceParam = (SimpleScalar) params.get("force");
        String force = null;
        if (forceParam != null)
        {
            force = forceParam.getAsString();
        }

        // Get the request url
        String requestUrl = ((HttpRequestHashModel) env.getDataModel().get("Request")).getRequest().getContextPath();

        // Build the url for the asset/section
        String url;
        if (assetParam != null)
        {
            // Optional parameter for asset to get a rendition of it
            SimpleScalar renditionParam = (SimpleScalar) params.get("rendition");

            // Optional parameter for asset to attach it to the response
            SimpleScalar attachParam = (SimpleScalar) params.get("attach");

            if (renditionParam != null)
            {
                force = "short";
            }

            Asset asset = (Asset) assetParam.getWrappedObject();
            if ("short".equals(force))
            {
                url = requestUrl + urlUtils.getShortUrl(asset);
            }
            else if ("long".equals(force))
            {
                url = requestUrl + urlUtils.getLongUrl(asset);
            }
            else
            {
                url = requestUrl + urlUtils.getUrl(asset);
            }
            
            Map<String, SimpleScalar> queryParams = new TreeMap<String, SimpleScalar>();
            if (renditionParam != null)
            {
                queryParams.put("rendition", renditionParam);
            }
            if (attachParam != null)
            {
                queryParams.put("attach", attachParam);
            }
            url = appendQueryParams(url, queryParams);
        }
        else
        {
            Section section = (Section) sectionParam.getWrappedObject();
            url = requestUrl + urlUtils.getUrl(section);
        }

        env.getOut().write(url);
    }

    private String appendQueryParams(String url, Map<String, SimpleScalar> queryParams) throws IOException
    {
        if (queryParams == null || queryParams.isEmpty())
        {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        boolean first = true;
        for (Map.Entry<String, SimpleScalar> entry : queryParams.entrySet())
        {
            if (first)
            {
                sb.append('?');
            }
            else
            {
                sb.append('&');
            }
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(URLEncoder.encode(entry.getValue().getAsString(), "UTF-8"));
            first = false;
        }
        return sb.toString();
    }

    public void setUrlUtils(UrlUtils urlUtils)
    {
        this.urlUtils = urlUtils;
    }

}
