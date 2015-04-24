/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @param asset
     * @param request
     * @param response
     * @return boolean true if browser has old copy and so content should be
     *         rendered
     * @throws IOException
     * @throws ParseException
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
