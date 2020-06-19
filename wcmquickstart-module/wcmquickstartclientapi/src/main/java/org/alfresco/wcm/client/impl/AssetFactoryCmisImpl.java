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
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.ContentStream;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.Resource;
import org.alfresco.wcm.client.SearchResult;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.SqlUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.spi.ObjectService;
import org.apache.chemistry.opencmis.commons.spi.RelationshipService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AssetFactoryCmisImpl implements AssetFactory
{
    private final static Log log = LogFactory.getLog(AssetFactoryCmisImpl.class);

    private SectionFactory sectionFactory;
    private CollectionFactory collectionFactory;

    private final static String COMMON_ASSET_SELECT_CLAUSE = "SELECT d.cmis:objectId, d.cmis:objectTypeId, d.cmis:name, d.cmis:contentStreamLength, "
            + "d.cmis:contentStreamMimeType, d.cmis:lastModificationDate, t.cm:title, t.cm:description, wa.ws:parentSections, wa.ws:publishedTime, "
            + "wa.ws:derivedCommentCount, wa.ws:derivedAverageRating, wa.ws:tags, a.cm:author ";

    private final static String COMMON_ASSET_FROM_CLAUSE = "FROM cmis:document AS d "
            + "JOIN ws:webasset AS wa ON d.cmis:objectId = wa.cmis:objectId "
            + "JOIN cm:author AS a ON d.cmis:objectId = a.cmis:objectId "
            + "JOIN cm:titled AS t ON d.cmis:objectId = t.cmis:objectId ";

    private final String assetByIdQueryPattern = COMMON_ASSET_SELECT_CLAUSE + COMMON_ASSET_FROM_CLAUSE
            + "WHERE d.cmis:objectId = ''{0}''";

    private final String assetsByIdQueryPattern = COMMON_ASSET_SELECT_CLAUSE + COMMON_ASSET_FROM_CLAUSE
            + "WHERE d.cmis:objectId IN ({0})";

    private final String assetBySectionAndNameQueryPattern = COMMON_ASSET_SELECT_CLAUSE + COMMON_ASSET_FROM_CLAUSE
            + "WHERE ANY wa.ws:parentSections IN (''{0}'') AND d.cmis:name = ''{1}''";

    private final String assetBySectionAndNameWildcardQueryPattern = COMMON_ASSET_SELECT_CLAUSE
            + COMMON_ASSET_FROM_CLAUSE + "WHERE ANY wa.ws:parentSections IN (''{0}'') AND d.cmis:name like ''{1}''";

    private final String assetByFtsQueryPattern = COMMON_ASSET_SELECT_CLAUSE + ", SCORE() " + COMMON_ASSET_FROM_CLAUSE
            + "WHERE IN_TREE(d, ''{0}'') AND (CONTAINS(d, ''{1}'') OR ANY wa.ws:tags IN ('''*'{1}'*''') "
            + "OR t.cm:title like ''%{1}%'' OR t.cm:description like ''%{1}%'' ) ";

    private final String assetByTagQueryPattern = COMMON_ASSET_SELECT_CLAUSE + ", SCORE() " + COMMON_ASSET_FROM_CLAUSE
            + "WHERE IN_TREE(d, ''{0}'') AND ANY wa.ws:tags IN (''{1}'') ";

    private final String searchOrderClause = " ORDER BY SEARCH_SCORE ASC";

    // private final String availabilityConstraint =
    // " AND wa.ws:available = true ";

    private final String modifiedTimeByAssetId = "SELECT d.cmis:lastModificationDate "
            + "FROM cmis:document AS d WHERE d.cmis:objectId = ''{0}''";

    private final String modifiedTimesByAssetIds = "SELECT d.cmis:objectId, d.cmis:lastModificationDate "
            + "FROM cmis:document AS d WHERE d.cmis:objectId IN ({0})";

    @Override
    public Asset getAssetById(String id, boolean deferredLoad)
    {
        Asset asset = null;
        if (deferredLoad)
        {
            asset = new DeferredLoadingAssetImpl(id, this);
        }
        else
        {
            ItemIterable<QueryResult> results = runQuery(MessageFormat.format(assetByIdQueryPattern, id));
            Iterator<QueryResult> iterator = results.iterator();
            if (iterator.hasNext())
            {
                QueryResult result = iterator.next();
                asset = buildAsset(result);
            }
        }
        return asset;
    }

    @Override
    public List<Asset> getAssetsById(Collection<String> ids, boolean deferredLoad)
    {
        List<Asset> assets;
        if (deferredLoad)
        {
            assets = new DeferredLoadingAssetListImpl(ids, this);
        }
        else
        {
            assets = new ArrayList<Asset>(ids.size());
            String idList = buildIdList(ids);
            ItemIterable<QueryResult> results = runQuery(MessageFormat.format(assetsByIdQueryPattern, idList));
            Iterator<QueryResult> iterator = results.iterator();
            while (iterator.hasNext())
            {
                QueryResult result = iterator.next();
                assets.add(buildAsset(result));
            }
        }
        return assets;
    }

    @Override
    public Asset getAssetById(String id)
    {
        return getAssetById(id, true);
    }

    @Override
    public List<Asset> getAssetsById(Collection<String> ids)
    {
        return getAssetsById(ids, true);
    }

    @Override
    public Asset getSectionAsset(String sectionId, String resourceName)
    {
        return getSectionAsset(sectionId, resourceName, false);
    }

    @Override
    public Asset getSectionAsset(String sectionId, String resourceName, boolean wildcardsAllowedInName)
    {
        Asset asset = null;
        ItemIterable<QueryResult> results = runQuery(MessageFormat
                .format((wildcardsAllowedInName ? assetBySectionAndNameWildcardQueryPattern
                        : assetBySectionAndNameQueryPattern), sectionId, resourceName));
        Iterator<QueryResult> iterator = results.iterator();
        if (iterator.hasNext())
        {
            QueryResult result = iterator.next();
            asset = buildAsset(result);
        }
        return asset;
    }

    @Override
    public SearchResults findByQuery(Query query)
    {
        // We search either by phrase or by tag but not by both. Choose the
        // appropriate CMIS query, falling back to constraining by
        // section only
        String cmisQuery = null;
        if ((query.getPhrase() != null) && (query.getPhrase().length() > 0))
        {
            cmisQuery = MessageFormat.format(assetByFtsQueryPattern, query.getSectionId(), query.getPhrase());
        }
        else if ((query.getTag() != null) && (query.getTag().length() > 0))
        {
            cmisQuery = MessageFormat.format(assetByTagQueryPattern, query.getSectionId(), query.getTag());
        }

        SearchResultsImpl searchResults = new SearchResultsImpl();
        searchResults.setQuery(new Query(query));

        if (cmisQuery != null)
        {
            ItemIterable<QueryResult> results = runQuery(cmisQuery, searchOrderClause);
            ItemIterable<QueryResult> page = results.skipTo(query.getResultsToSkip()).getPage(query.getMaxResults());
            List<SearchResult> foundAssets = new ArrayList<SearchResult>((int) page.getPageNumItems());
            for (QueryResult queryResult : page)
            {
                foundAssets.add(new SearchResultAssetImpl(buildAsset(queryResult), ((BigDecimal) queryResult
                        .getPropertyValueByQueryName("SEARCH_SCORE")).scaleByPowerOfTen(2).intValue()));
            }
            searchResults.setResults(foundAssets);
            searchResults.setTotalSize(results.getTotalNumItems());
        }

        return searchResults;
    }

    @Override
    public Map<String, List<String>> getSourceRelationships(String assetId)
    {
        Map<String, List<String>> result = new TreeMap<String, List<String>>();

        Session session = CmisSessionHelper.getSession();
        if (log.isDebugEnabled())
        {
            log.debug("About to run call CMIS relationship service for: " + assetId);
        }
        final RelationshipService relationshipService = session.getBinding().getRelationshipService();
        final OperationContext ctxt = session.getDefaultContext();

        // ACE-3265: get actual "cmis:objectId"
        ObjectService objectService = session.getBinding().getObjectService();
        Map<String, PropertyData<?>> cmisProperties = objectService.getProperties(session.getRepositoryInfo().getId(), assetId, null, null).getProperties();
        String objectId = (String) cmisProperties.get(PropertyIds.OBJECT_ID).getFirstValue();

        // fetch the relationships
        ObjectList relList = relationshipService.getObjectRelationships(session.getRepositoryInfo().getId(), objectId,
                true, RelationshipDirection.SOURCE, null, ctxt.getFilterString(), ctxt.isIncludeAllowableActions(),
                null, null, null);

        // convert relationship objects
        if (relList.getObjects() != null)
        {
            for (ObjectData rod : relList.getObjects())
            {
                Map<String, PropertyData<?>> props = rod.getProperties().getProperties();
                PropertyData<?> targetIdData = props.get(PropertyIds.TARGET_ID);
                PropertyData<?> assocTypeData = props.get(PropertyIds.OBJECT_TYPE_ID);

                if (targetIdData != null && assocTypeData != null)
                {
                    // The association type will have a prefix of "R:". Strip
                    // this off.
                    String assocType = assocTypeData.getFirstValue().toString();
                    if (assocType.startsWith("R:"))
                    {
                        assocType = assocType.substring(2);
                    }
                    List<String> currentTargets = result.get(assocType);
                    if (currentTargets == null)
                    {
                        currentTargets = new ArrayList<String>();
                        result.put(assocType, currentTargets);
                    }
                    currentTargets.add(targetIdData.getFirstValue().toString());
                }
            }
        }
        return result;
    }

    @Override
    public Date getModifiedTimeOfAsset(String assetId)
    {
        Date modifiedTime = null;
        String cmisQuery = MessageFormat.format(modifiedTimeByAssetId, assetId);
        ItemIterable<QueryResult> results = runQuery(cmisQuery);
        Iterator<QueryResult> iterator = results.iterator();
        if (iterator.hasNext())
        {
            QueryResult result = iterator.next();
            modifiedTime = SqlUtils.getDateProperty(result, Resource.PROPERTY_MODIFIED_TIME);
        }
        return modifiedTime;
    }

    @Override
    public Map<String, Date> getModifiedTimesOfAssets(Collection<String> assetIds)
    {
        Map<String, Date> map = new TreeMap<String, Date>();

        String idList = buildIdList(assetIds);
        String cmisQuery = MessageFormat.format(modifiedTimesByAssetIds, idList);
        ItemIterable<QueryResult> results = runQuery(cmisQuery);
        Iterator<QueryResult> iterator = results.iterator();

        Date modifiedTime;
        String id;
        while (iterator.hasNext())
        {
            QueryResult result = iterator.next();
            modifiedTime = SqlUtils.getDateProperty(result, Resource.PROPERTY_MODIFIED_TIME);
            id = (String) result.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue();
            map.put(id, modifiedTime);
        }
        return map;
    }

    private String buildIdList(Collection<String> assetIds)
    {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (String id : assetIds)
        {
            if (!first)
            {
                builder.append(',');
            }
            builder.append('\'');
            builder.append(id);
            builder.append('\'');
            first = false;
        }
        return builder.toString();
    }

    @Override
    public Map<String, org.alfresco.wcm.client.Rendition> getRenditions(String assetId)
    {
        Map<String, org.alfresco.wcm.client.Rendition> renditionMap = new TreeMap<String, org.alfresco.wcm.client.Rendition>();
        if (assetId == null || assetId.length() == 0)
        {
            throw new IllegalArgumentException("assetId = " + assetId);
        }
        Session session = CmisSessionHelper.getSession();
        OperationContext oc = session.createOperationContext();
        oc.setRenditionFilterString("*");
        List<Rendition> renditions = session.getObject(session.createObjectId(assetId), oc).getRenditions();
        for (Rendition rendition : renditions)
        {
            renditionMap.put(rendition.getKind(), new ContentStreamCmisRenditionImpl(rendition));
        }
        return renditionMap;
    }

    protected AssetImpl buildAsset(QueryResult result)
    {
        AssetImpl asset = new AssetImpl();
        Map<String, Serializable> properties = new TreeMap<String, Serializable>();

        properties.put(PropertyIds.OBJECT_ID, (String) result.getPropertyById(PropertyIds.OBJECT_ID).getFirstValue());
        properties.put(PropertyIds.OBJECT_TYPE_ID, (Serializable) result.getPropertyById(PropertyIds.OBJECT_TYPE_ID)
                .getFirstValue());
        properties.put(PropertyIds.NAME, (Serializable) result.getPropertyById(PropertyIds.NAME).getFirstValue());
        properties.put(PropertyIds.CONTENT_STREAM_LENGTH, (Serializable) result.getPropertyById(
                PropertyIds.CONTENT_STREAM_LENGTH).getFirstValue());
        properties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, (Serializable) result.getPropertyById(
                PropertyIds.CONTENT_STREAM_MIME_TYPE).getFirstValue());
        properties.put(Resource.PROPERTY_TITLE, (Serializable) result.getPropertyById(Resource.PROPERTY_TITLE)
                .getFirstValue());
        properties.put(Resource.PROPERTY_MODIFIED_TIME, SqlUtils.getDateProperty(result,
                Resource.PROPERTY_MODIFIED_TIME));
        properties.put(Resource.PROPERTY_DESCRIPTION, (Serializable) result.getPropertyById(
                Resource.PROPERTY_DESCRIPTION).getFirstValue());
        properties.put(Asset.PROPERTY_AVERAGE_RATING, (Serializable) result.getPropertyById(
                Asset.PROPERTY_AVERAGE_RATING).getFirstValue());
        properties.put(Asset.PROPERTY_COMMENT_COUNT, (Serializable) result
                .getPropertyById(Asset.PROPERTY_COMMENT_COUNT).getFirstValue());
        properties.put(Asset.PROPERTY_TAGS, (Serializable) result.getPropertyMultivalueById(Asset.PROPERTY_TAGS));
        properties.put(Asset.PROPERTY_PUBLISHED_TIME, SqlUtils.getDateProperty(result, Asset.PROPERTY_PUBLISHED_TIME));
        properties.put(Asset.PROPERTY_AUTHOR, (Serializable) result.getPropertyById(Asset.PROPERTY_AUTHOR)
                .getFirstValue());

        List<String> parentSectionIds = result.getPropertyMultivalueById(Asset.PROPERTY_PARENT_SECTIONS);
        if (parentSectionIds == null || parentSectionIds.isEmpty())
        {
            log.warn("Retrieved an asset that has no parent sections: " + properties.get(PropertyIds.OBJECT_ID));
        }
        asset.setProperties(properties);
        asset.setParentSectionIds(parentSectionIds);
        asset.setSectionFactory(sectionFactory);
        asset.setCollectionFactory(collectionFactory);
        asset.setAssetFactory(this);
        return asset;
    }

    private ItemIterable<QueryResult> runQuery(String query)
    {
        return runQuery(query, null, false);
    }

    private ItemIterable<QueryResult> runQuery(String query, String orderByClause)
    {
        return runQuery(query, orderByClause, false);
    }

    private ItemIterable<QueryResult> runQuery(String query, String orderByClause, boolean forceUnavailableAssets)
    {
        // Lack of time and complexities surrounding dynamic content collections
        // leads
        // to this availability check not being implemented in this version of
        // the quick start.
        // WebSite currentWebSite = WebSiteService.getThreadWebSite();
        //        
        //
        // if (!forceUnavailableAssets && (currentWebSite == null ||
        // !currentWebSite.isEditorialSite()))
        // {
        // //We want to constrain the query by whether the asset is available
        // query += availabilityConstraint;
        // }
        if (orderByClause != null)
        {
            query += orderByClause;
        }

        long start = 0L;
        if (log.isDebugEnabled())
        {
            log.debug("About to run CMIS query: " + query);
            start = System.currentTimeMillis();
        }
        Session session = CmisSessionHelper.getSession();
        ItemIterable<QueryResult> results = session.query(query, false);

        if (log.isDebugEnabled())
        {
            long end = System.currentTimeMillis();
            log.debug("CMIS query took " + (end - start) + "ms to return.");
        }
        return results;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
        sectionFactory.setAssetFactory(this);
    }

    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }

    @Override
    public ContentStream getContentStream(String assetId)
    {
        // Get the request thread's session
        Session session = CmisSessionHelper.getSession();

        // Fetch the Document object for this asset
        CmisObject object = session.getObject(new ObjectIdImpl(assetId));
        if (!(object instanceof Document))
        {
            throw new IllegalArgumentException("Object referenced by the uuid is not a document");
        }
        Document doc = (Document) object;
        if (doc == null)
            return null;

        // Return the content as a stream
        return new ContentStreamCmisImpl(doc.getContentStream());
    }
}
