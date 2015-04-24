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
package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.Rendition;
import org.alfresco.wcm.client.SearchResult;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;

public class AssetFactoryWebscriptImpl implements AssetFactory
{
    private WebScriptCaller webscriptCaller;
    private SectionFactory sectionFactory;
    private CollectionFactory collectionFactory;
    private AssetFactory supportingAssetFactory;
    
    private ThreadLocal<List<WebscriptParam>> localParamList = new ThreadLocal<List<WebscriptParam>>() 
    {
        @Override
        protected List<WebscriptParam> initialValue()
        {
            return new ArrayList<WebscriptParam>();
        }

        @Override
        public List<WebscriptParam> get()
        {
            List<WebscriptParam> list = super.get();
            list.clear();
            return list;
        }
        
    };

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }

    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }

    public void setSupportingAssetFactory(AssetFactory supportingAssetFactory)
    {
        this.supportingAssetFactory = supportingAssetFactory;
    }

    @Override
    public SearchResults findByQuery(Query query)
    {
        SearchResultsImpl results = new SearchResultsImpl();
        results.setQuery(query);
        if (query != null && query.getSectionId() != null)
        {
            String phrase = query.getPhrase();
            String tag = query.getTag();
            //Return no results unless either phrase or tag has been specified
            if ((phrase != null && phrase.length() > 0) || (tag != null && tag.length() > 0))
            {
                List<WebscriptParam> params = localParamList.get();
                params.add(new WebscriptParam("sectionid", query.getSectionId()));
                WebSite currentSite = WebSiteService.getThreadWebSite(); 
                if (currentSite != null)
                {
                    params.add(new WebscriptParam("siteid", currentSite.getId()));
                }
                if (query.getTag() != null)
                {
                    params.add(new WebscriptParam("tag", query.getTag()));
                }
                if (query.getPhrase() != null)
                {
                    params.add(new WebscriptParam("phrase", query.getPhrase()));
                }
                params.add(new WebscriptParam("skip", Integer.toString(query.getResultsToSkip())));
                params.add(new WebscriptParam("max", Integer.toString(query.getMaxResults())));
                Map<String, Serializable> headerProps = new TreeMap<String, Serializable>();
                LinkedList<TreeMap<String, Serializable>> assets = searchAssetsInRepo(params, headerProps);

                Long totalResults = (Long) headerProps.get("totalResults");
                results.setTotalSize(totalResults == null ? assets.size() : totalResults);
                List<SearchResult> resultList = new ArrayList<SearchResult>(assets.size());
                for (TreeMap<String, Serializable> assetData : assets)
                {
                    resultList.add(buildSearchResult(assetData));
                }
                results.setResults(resultList);
            }
        }
        return results;
    }

    @Override
    public Asset getAssetById(String id)
    {
        return getAssetById(id, false);
    }

    @Override
    public Asset getAssetById(String id, boolean deferredLoad)
    {
        Asset asset = null;
        LinkedList<TreeMap<String, Serializable>> assetList = getAssetsFromRepo(new WebscriptParam("noderef", id));
        if (!assetList.isEmpty())
        {
            asset = buildAsset(assetList.get(0));
        }
        return asset;
    }

    @Override
    public List<Asset> getAssetsById(Collection<String> ids)
    {
        return getAssetsById(ids, false);
    }

    @Override
    public List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad)
    {
        List<Asset> results = new ArrayList<Asset>(ids.size());
        List<WebscriptParam> params = localParamList.get();
        for (String id : ids)
        {
            params.add(new WebscriptParam("noderef", id));
        }
        LinkedList<TreeMap<String, Serializable>> assetDataList = getAssetsFromRepo(params);
        for (TreeMap<String, Serializable> assetData : assetDataList)
        {
            results.add(buildAsset(assetData));
        }
        return results;
    }

    @Override
    public Date getModifiedTimeOfAsset(String assetId)
    {
        Date result = null;
        List<WebscriptParam> paramList = localParamList.get();
        paramList.add(new WebscriptParam("noderef", assetId));
        paramList.add(new WebscriptParam("modifiedTimeOnly", "true"));
        LinkedList<TreeMap<String, Serializable>> assetList = getAssetsFromRepo(paramList);
        if (!assetList.isEmpty())
        {
            TreeMap<String, Serializable> assetData = assetList.get(0);
            result = (Date) assetData.get("cm:modified");
        }
        return result;
    }

    @Override
    public Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds)
    {
        Map<String, Date> result = new TreeMap<String, Date>();
        List<WebscriptParam> params = localParamList.get();
        for (String id : assetIds)
        {
            params.add(new WebscriptParam("noderef", id));
        }
        params.add(new WebscriptParam("modifiedTimeOnly", "true"));
        LinkedList<TreeMap<String, Serializable>> assetList = getAssetsFromRepo(params);
        for (TreeMap<String, Serializable> asset : assetList)
        {
            result.put((String) asset.get("id"), (Date) asset.get("cm:modified"));
        }
        return result;
    }

    @Override
    public Map<String, Rendition> getRenditions(String assetId)
    {
        return supportingAssetFactory.getRenditions(assetId);
    }

    @Override
    public Asset getSectionAsset(String sectionId, String assetName)
    {
        return getSectionAsset(sectionId, assetName, false);
    }

    @Override
    public Asset getSectionAsset(String sectionId, String assetName, boolean wildcardsAllowedInName)
    {
        Asset asset = null;
        List<WebscriptParam> paramList = localParamList.get();
        WebSite currentSite = WebSiteService.getThreadWebSite(); 
        if (currentSite != null)
        {
            paramList.add(new WebscriptParam("siteid", currentSite.getId()));
        }
        paramList.add(new WebscriptParam("sectionid", sectionId));
        paramList.add(new WebscriptParam("nodename", assetName));
        LinkedList<TreeMap<String, Serializable>> assetList = getAssetsFromRepo(paramList);
        if (!assetList.isEmpty())
        {
            asset = buildAsset(assetList.get(0));
        }
        return asset;
    }

    @Override
    public Map<String, List<String>> getSourceRelationships(String assetId)
    {
        return supportingAssetFactory.getSourceRelationships(assetId);
    }

    private LinkedList<TreeMap<String, Serializable>> getAssetsFromRepo(WebscriptParam... params)
    {
        return getAssetsFromRepo(Arrays.asList(params));
    }

    private LinkedList<TreeMap<String, Serializable>> getAssetsFromRepo(List<WebscriptParam> params)
    {
        return getAssetsFromRepo(params, null);
    }

    private LinkedList<TreeMap<String, Serializable>> getAssetsFromRepo(List<WebscriptParam> params,
            Map<String, Serializable> header)
    {
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        webscriptCaller.post("webasset", deserializer, params);
        LinkedList<TreeMap<String, Serializable>> assetList = deserializer.getAssets();
        if (header != null)
        {
            header.putAll(deserializer.getHeader());
        }
        return assetList;
    }

    private LinkedList<TreeMap<String, Serializable>> searchAssetsInRepo(List<WebscriptParam> params,
            Map<String, Serializable> header)
    {
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        webscriptCaller.get("webassetsearch", deserializer, params);
        LinkedList<TreeMap<String, Serializable>> assetList = deserializer.getAssets();
        if (header != null && deserializer.getHeader() != null)
        {
            header.putAll(deserializer.getHeader());
        }
        return assetList;
    }

    @SuppressWarnings("unchecked")
    protected Asset buildAsset(TreeMap<String, Serializable> props)
    {
        AssetImpl asset = new AssetImpl();
        asset.setProperties(props);
        asset.setParentSectionIds((Collection<String>) props.get("ws:parentSections"));
        asset.setSectionFactory(sectionFactory);
        asset.setCollectionFactory(collectionFactory);
        asset.setAssetFactory(this);
        return asset;
    }

    protected SearchResult buildSearchResult(TreeMap<String, Serializable> props)
    {
        Asset asset = buildAsset(props);
        Long score = (Long) asset.getProperty("searchScore");
        if (score == null)
        {
            score = 0L;
        }
        SearchResultAssetImpl result = new SearchResultAssetImpl(asset, score.intValue());
        return result;
    }

    @Override
    public ContentStream getContentStream(String assetId)
    {
        return supportingAssetFactory.getContentStream(assetId);
    }

}
