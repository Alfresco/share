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
