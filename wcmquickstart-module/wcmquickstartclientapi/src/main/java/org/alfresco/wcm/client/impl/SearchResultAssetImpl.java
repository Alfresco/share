package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.SearchResult;
import org.alfresco.wcm.client.Section;

/**

 * @author Brian
 *
 */
public class SearchResultAssetImpl implements SearchResult
{
    private static final long serialVersionUID = -9077718168532110705L;

    private Asset delegate = null;
    private int score;
    
    @SuppressWarnings("unused")
    private SearchResultAssetImpl()
    {
    }
    
    SearchResultAssetImpl(Asset delegate, int score)
    {
        this.delegate = delegate;
        this.score = score;
    }

    public Section getContainingSection()
    {
        return delegate.getContainingSection();
    }

    public ContentStream getContentAsInputStream()
    {
        return delegate.getContentAsInputStream();
    }

    public String getDescription()
    {
        return delegate.getDescription();
    }

    public String getId()
    {
        return delegate.getId();
    }

    public String getMimeType()
    {
        return delegate.getMimeType();
    }

    public String getName()
    {
        return delegate.getName();
    }

    public Map<String, Serializable> getProperties()
    {
        return delegate.getProperties();
    }

    public Serializable getProperty(String name)
    {
        return delegate.getProperty(name);
    }

    public Asset getRelatedAsset(String relationshipName)
    {
        return delegate.getRelatedAsset(relationshipName);
    }

    public List<Asset> getRelatedAssets(String relationshipName)
    {
        return delegate.getRelatedAssets(relationshipName);
    }
    
    public Map<String,List<Asset>> getRelatedAssets() {
    	return delegate.getRelatedAssets();
    }    

    public long getSize()
    {
        return delegate.getSize();
    }

    public List<String> getTags()
    {
        return delegate.getTags();
    }

    public String getTitle()
    {
        return delegate.getTitle();
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    @Override
    public String getTemplate()
    {
        return delegate.getTemplate();
    }

    @Override
    public String getType()
    {
        return delegate.getType();
    }

    @Override
    public Map<String, Rendition> getRenditions()
    {
        return delegate.getRenditions();
    }
    
}
