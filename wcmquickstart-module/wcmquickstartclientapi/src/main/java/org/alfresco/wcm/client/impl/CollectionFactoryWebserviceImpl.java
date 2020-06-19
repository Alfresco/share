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
package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.AssetCollectionFactory;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.Query;

/**
 * Get collection using a call to a web service. This is done to avoid having to
 * perform two queries - one for the collection meta data and one for the list
 * of assets ids within the collection. Open CMIS allows relationships to be
 * retrieved with an object by setting a parameter of an operational context
 * object passed to the query but this is only allowed if no joins are used.
 * 
 * @author Chris Lack
 * @author Brian Remmington
 */
public class CollectionFactoryWebserviceImpl implements AssetCollectionFactory
{
    private AssetFactory assetFactory;
    private WebScriptCaller webscriptCaller;

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

    /**
     * @see org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl#getCollection(String,
     *      String)
     */
    @Override
    public AssetCollection getCollection(String sectionId, String collectionName)
    {
        return getCollection(sectionId, collectionName, 0, -1);
    }

    /**
     * @see org.alfresco.wcm.client.impl.CollectionFactoryWebserviceImpl#getCollection(String,
     *      String, int, int)
     */
    @Override
    public AssetCollection getCollection(String sectionId, String collectionName, int resultsToSkip, int maxResults)
    {
        if (sectionId == null || sectionId.length() == 0)
        {
            throw new IllegalArgumentException("sectionId must be supplied");
        }
        if (collectionName == null || collectionName.length() == 0)
        {
            throw new IllegalArgumentException("collectionName must be supplied");
        }

        try
        {
            String scriptUri = "assetcollections/" + URLEncoder.encode(collectionName, "UTF-8");
            WebscriptParam[] params = new WebscriptParam[] { 
                    new WebscriptParam("sectionid", sectionId),
            };
            AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
            webscriptCaller.get(scriptUri, deserializer, params);
            LinkedList<TreeMap<String, Serializable>> assetCollectionList = deserializer.getAssets();

            AssetCollectionImpl collection = null;
            if (assetCollectionList != null && !assetCollectionList.isEmpty())
            {
                TreeMap<String,Serializable> assetCollectionData = assetCollectionList.get(0);
                collection = buildCollection(sectionId, assetCollectionData);

                // Get the list of ids of assets in the collection
                List<String> assetIds = collection.getAssetIds();

                Query query = new Query();
                query.setSectionId(sectionId);
                query.setMaxResults(maxResults);
                query.setResultsToSkip(resultsToSkip);
                collection.setQuery(query);

                if (assetIds.size() > 0)
                {
                    // If this is a paginated query then select the subset of ids
                    // for which the assets should be fetched.
                    if (maxResults != -1)
                    {
                        int end = resultsToSkip + maxResults;
                        assetIds = assetIds.subList(resultsToSkip, end > assetIds.size() ? assetIds.size() : end);
                    }

                    // Get the actual asset objects.
                    List<Asset> assets = assetFactory.getAssetsById(assetIds);
                    collection.setAssets(assets);
                }
            }
            return collection;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Error encoding URL", e);
        }
    }

    public Date getModifiedTimeOfAssetCollection(String assetCollectionId)
    {
        Date result = null;
        List<WebscriptParam> paramList = localParamList.get();
        paramList.add(new WebscriptParam("assetcollectionid", assetCollectionId));
        paramList.add(new WebscriptParam("modifiedTimeOnly", "true"));
        String scriptUri = "assetcollections";
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        webscriptCaller.get(scriptUri, deserializer, paramList);

        LinkedList<TreeMap<String, Serializable>> assetCollectionList = deserializer.getAssets();

        if (!assetCollectionList.isEmpty())
        {
            TreeMap<String, Serializable> assetCollectionData = assetCollectionList.get(0);
            result = (Date) assetCollectionData.get("cm:modified");
        }
        return result;
    }

    protected AssetCollectionImpl buildCollection(String sectionId, TreeMap<String, Serializable> assetCollectionData)
    {
        AssetCollectionImpl collection = new AssetCollectionImpl();
        collection.setProperties(assetCollectionData);
        collection.setPrimarySectionId(sectionId);
        return collection;
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }
}
