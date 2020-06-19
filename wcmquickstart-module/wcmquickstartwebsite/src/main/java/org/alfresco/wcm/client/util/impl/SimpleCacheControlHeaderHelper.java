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
package org.alfresco.wcm.client.util.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Resource;
import org.alfresco.wcm.client.util.HeaderHelper;

public class SimpleCacheControlHeaderHelper extends HeaderHelper
{
    private long defaultExpiry = 300000L; // 5 mins in ms

    public void setDefaultExpiry(long defaultExpiry)
    {
        this.defaultExpiry = defaultExpiry;
    }

    /**
     * Set appropriate cache-control headers on the response for an asset and return true if it
     * should be rendered
     * 
     * @param asset Asset
     * @param attach boolean
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return boolean true if browser has old copy and so content should be
     *         rendered
     */
    @Override
    public boolean setHeaders(Asset asset, boolean attach, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            // Set headers
            Date modifiedDate = ((Date) asset.getProperty(Resource.PROPERTY_MODIFIED_TIME));
            long modifiedTime = modifiedDate.getTime();
            modifiedTime = (modifiedTime / 1000) * 1000; // remove ms
            
            response.addDateHeader("Last-Modified", modifiedTime);
            response.addDateHeader("Expires", new Date().getTime() + defaultExpiry);
            String etag = Long.toHexString(modifiedTime);
            response.addHeader("ETag", etag);

            // Check if the asset has been changed since the last request
            String requestIfNoneMatch = request.getHeader("If-None-Match");
            if (requestIfNoneMatch != null)
            {
                if (etag.equals(requestIfNoneMatch))
                {
                    response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                    return false;
                }
            }
            else
            {
                String requestIfModifiedSince = request.getHeader("If-Modified-Since");
                if (requestIfModifiedSince != null)
                {
                    Date requestDate = getDateFromHttpDate(requestIfModifiedSince);
                    if (requestDate.getTime() >= modifiedTime)
                    {
                        response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                        return false;
                    }
                }
            }
            if (attach)
            {
                String headerValue = "attachment";
                String attachFileName = asset.getName();
                if (attachFileName != null && attachFileName.length() > 0)
                {
                    headerValue += "; filename=" + attachFileName;
                }
                
                // set header based on filename - will force a Save As from the browse if it doesn't recognize it
                // this is better than the default response of the browser trying to display the contents
                response.setHeader("Content-Disposition", headerValue);
            }
            return true;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
