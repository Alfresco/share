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
package org.alfresco.wcm.client.view;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.util.HeaderHelper;
import org.alfresco.wcm.client.util.impl.SimpleCacheControlHeaderHelper;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Stream an asset for the view
 * 
 * @author Chris Lack
 * 
 */
public class StreamedAssetView extends AbstractUrlBasedView
{
    private static HeaderHelper headerHelper = new SimpleCacheControlHeaderHelper();
    
    private Asset asset;
    private boolean attach;
    private String renditionName; 

    public StreamedAssetView(Asset asset, String renditionName, boolean attach)
    {
        this.asset = asset;
        this.attach = attach;
        this.renditionName = renditionName;
    }
    
    public StreamedAssetView(Asset asset)
    {
        this(asset, null, false);
    }
    
    public StreamedAssetView(Asset asset, String renditionName)
    {
        this(asset, renditionName, false);
    }
    
    public StreamedAssetView(Asset asset, boolean attach)
    {
        this(asset, null, attach);
    }
    
    @Override
    protected boolean isUrlRequired()
    {
        return false;
    }

    public static void setHeaderHelper(HeaderHelper headerHelper)
    {
        StreamedAssetView.headerHelper = headerHelper;
    }
    
    /**
     * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        boolean render = headerHelper.setHeaders(asset, attach, request, response);
        
        ServletOutputStream out = null;
        if (render)
        {
            ContentStream contentStream = null;
            if (renditionName != null) 
            {
                Map<String,Rendition> renditions = asset.getRenditions();
                contentStream = renditions.get(renditionName);
            }
            else
            {
                contentStream = asset.getContentAsInputStream();
            }
            if (contentStream != null) 
            {
                String mimeType = contentStream.getMimeType();
                response.setContentType(mimeType == null ? "application/octet-stream" : mimeType);
                response.setContentLength((int)contentStream.getLength());
                out = response.getOutputStream();
                contentStream.output(out);
            }
        }
    }
}
