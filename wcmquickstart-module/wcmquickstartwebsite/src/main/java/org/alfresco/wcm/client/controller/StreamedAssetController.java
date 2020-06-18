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
package org.alfresco.wcm.client.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.util.UrlUtils;
import org.alfresco.wcm.client.view.StreamedAssetView;
import org.alfresco.wcm.client.viewresolver.DynamicPageViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * StreamedAssetController uses an id in the url to look-up an asset in the
 * repository, eg an image. It then returns a view object which can render a
 * stream.
 * 
 * @author Chris Lack
 */
public class StreamedAssetController extends AbstractController
{
    private UrlUtils urlUtils;
    private AssetFactory assetFactory;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        // Get the asset Id from the url
        String uri = request.getPathInfo();
        String objectId = urlUtils.getAssetIdFromShortUrl(uri);
        String renditionName = request.getParameter("rendition");
        boolean attach = Boolean.parseBoolean(request.getParameter("attach"));

        // Fetch the asset from the repository
        Asset asset = assetFactory.getAssetById(objectId);
        if (asset == null)
        {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        // Decide if the request should redirect to the full url
        String template = asset.getTemplate();
        if (template != null && renditionName == null && 
                !DynamicPageViewResolver.RAW_TEMPLATE_NAME.equalsIgnoreCase(template))
        {
            String fullUri = urlUtils.getUrl(asset);
            RedirectView redirect = new RedirectView(fullUri, true, false);
            redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return new ModelAndView(redirect);
        }

        // Return a StreamedAssetView to render the stream
        return new ModelAndView(new StreamedAssetView(asset, renditionName, attach));
    }
 
    public void setUrlUtils(UrlUtils urlUtils)
    {
        this.urlUtils = urlUtils;
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }
}
