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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public class WebassetCollectionHelper implements WebSiteModel
{
    private static final Log log = LogFactory.getLog(WebassetCollectionHelper.class);
    
    private NodeService nodeService;
    private SearchService searchService;
    private NamespaceService namespaceService;
    private ContextParserService contextParserService;
    private String searchStore = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE.toString();
    
    /**
     * Set the node service
     * 
     * @param nodeService
     *            node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Set the search service
     * 
     * @param searchService
     *            search service
     */
    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Set the search store, must be a valid store reference string
     * 
     * @param searchStore
     *            search store
     */
    public void setSearchStore(String searchStore)
    {
        this.searchStore = searchStore;
    }

    /**
     * 
     * @param contextParserService
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
        this.contextParserService = contextParserService;
    }

    /**
     * Clear collection
     * 
     * @param collection
     *            collection node reference
     */
    public void clearCollection(NodeRef collection)
    {
        List<AssociationRef> assocs = nodeService.getTargetAssocs(collection, ASSOC_WEBASSETS);
        for (AssociationRef assoc : assocs)
        {
            nodeService.removeAssociation(collection, assoc.getTargetRef(), ASSOC_WEBASSETS);
        }
        nodeService.removeProperty(collection, PROP_CONTAINED_ASSETS);
    }

    /**
     * Refresh collection, clears all current members of the collection.
     * 
     * @param collection
     *            collection node reference
     */
    public void refreshCollection(NodeRef collection)
    {
        // Get the query language and max query size
        String queryLanguage = (String) nodeService.getProperty(collection, PROP_QUERY_LANGUAGE);
        String query = (String) nodeService.getProperty(collection, PROP_QUERY);
        Integer minsToRefresh = ((Integer) nodeService.getProperty(collection, PROP_MINS_TO_QUERY_REFRESH));
        minsToRefresh = minsToRefresh == null ? 30 : minsToRefresh;
        Integer maxQuerySize = ((Integer) nodeService.getProperty(collection, PROP_QUERY_RESULTS_MAX_SIZE));
        maxQuerySize = maxQuerySize == null ? 5 : maxQuerySize;

        if (query != null && query.trim().length() != 0)
        {
            // Clear the contents of the content collection
            clearCollection(collection);

            // Parse the query string
            query = contextParserService.parse(collection, query);

            SearchParameters searchParameters = new SearchParameters();

            if (queryLanguage.equals(SearchService.LANGUAGE_LUCENE))
            {
                //handle additional support for Lucene ordering with ORDER_ASC and ORDER_DESC
                String[] queryParts = query.split("\\s");
                for (String queryPart : queryParts)
                {
                    int firstColonIndex = queryPart.indexOf(':');
                    if (firstColonIndex == -1)
                    {
                        continue;
                    }
                    String name = queryPart.substring(0, firstColonIndex);
                    String value = (firstColonIndex < (queryPart.length() + 1)) ? queryPart.substring(firstColonIndex+1) : "";
                    boolean orderAscending = "ORDER_ASC".equals(name) || "ORDER".equals(name);
                    boolean orderDescending = "ORDER_DESC".equals(name);
                    if (!orderAscending && !orderDescending)
                    {
                        continue;
                    }
                    QName property = parsePropertyName(value);
                    if (property != null)
                    {
                        String sort = "@" + property.toString();
                        if (log.isDebugEnabled())
                        {
                            log.debug("Adding sort order: " + sort + (orderAscending ? " ASC" : " DESC"));
                        }
                        searchParameters.addSort(sort, orderAscending);
                    }
                }
                
            }

            // Build the query parameters
            searchParameters.addStore(new StoreRef(searchStore));
            searchParameters.setLanguage(queryLanguage);
            searchParameters.setMaxItems(maxQuerySize);
            searchParameters.setQuery(query);
            
            if (log.isDebugEnabled())
            {
                log.debug("About to run query for dynamic asset collection (" + collection + "): " + query);
            }
            ResultSet resultSet = null;
            try
            {
                // Execute the query
                resultSet = searchService.query(searchParameters);

                // Iterate over the results of the query
                int resultCount = 0;
                ArrayList<NodeRef> idList = new ArrayList<NodeRef>(maxQuerySize);
                for (NodeRef result : resultSet.getNodeRefs())
                {
                    if (maxQuerySize < 1 || resultCount < maxQuerySize)
                    {
                        // Only add associations to webassets
                        if (nodeService.hasAspect(result, ASPECT_WEBASSET) == true)
                        {
                            nodeService.createAssociation(collection, result, ASSOC_WEBASSETS);
                        }
                        idList.add(result);
                        resultCount++;
                    }
                    else
                    {
                        break;
                    }
                }
                nodeService.setProperty(collection, PROP_CONTAINED_ASSETS, idList);

                // Set the refreshAt property
                Calendar now = Calendar.getInstance();
                now.add(Calendar.MINUTE, minsToRefresh);
                nodeService.setProperty(collection, PROP_REFRESH_AT, now.getTime());

            }
            catch (Exception e)
            {
                log.error("Failed to complete update of dynamic asset collection (" + collection + "): " + query, e);
            }
            finally
            {
            	if (resultSet != null) {resultSet.close();}
            }
        }
    }

    private QName parsePropertyName(String value)
    {
        QName result = null;
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Attempting to parse property name: " + value);
            }
            StringBuilder sb = new StringBuilder();
            char[] valueArray = value.toCharArray();
            for (char ch : valueArray)
            {
                switch (ch)
                {
                case '\"':
                    break;
                    
                default:
                    sb.append(ch);
                    break;
                }
            }
            result = QName.createQName(sb.toString(), namespaceService);
        }
        catch(InvalidQNameException ex)
        {
        }
        return result;
    }

}