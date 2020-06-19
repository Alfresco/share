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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.SectionHierarchyProcessor;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializer;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializerFactory;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * AssetSearch GET implementation
 */
public class AssetSearch extends AbstractWebScript
{
    private static Log log = LogFactory.getLog(AssetSearch.class);
    
    private static final String PARAM_SITE_ID = "siteid";
    private static final String PARAM_SECTION_ID = "sectionid";
    private static final String PARAM_PHRASE = "phrase";
    private static final String PARAM_TAG = "tag";
    private static final String PARAM_SKIP = "skip";
    private static final String PARAM_MAX = "max";

    private NodeService nodeService;
    private SearchService searchService;
    private AssetSerializerFactory assetSerializerFactory;
    private SectionHierarchyProcessor sectionHierarchyProcessor;
    private SiteHelper siteHelper;

    public void setAssetSerializerFactory(AssetSerializerFactory assetSerializerFactory)
    {
        this.assetSerializerFactory = assetSerializerFactory;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setSectionHierarchyProcessor(SectionHierarchyProcessor sectionHierarchyProcessor)
    {
        this.sectionHierarchyProcessor = sectionHierarchyProcessor;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
    	ResultSet rs = null;
    	
        try
        {
            sectionHierarchyProcessor.init();
            
            StringBuilder queryBuilder = new StringBuilder();
            
            String sectionId = req.getParameter(PARAM_SECTION_ID);
            String phrase = req.getParameter(PARAM_PHRASE);
            String tag = req.getParameter(PARAM_TAG);
            String resultsToSkip = req.getParameter(PARAM_SKIP);
            String maxResults = req.getParameter(PARAM_MAX);
            
            int skip = resultsToSkip == null ? 0 : Integer.parseInt(resultsToSkip);
            int max = maxResults == null ? 100 : Integer.parseInt(maxResults);
            
            if (sectionId == null || sectionId.length() == 0)
            {
                throw new WebScriptException("\"sectionid\" is a required parameter");
            }
            
            NodeRef siteId = null;
            String siteIdText = req.getParameter(PARAM_SITE_ID);
            if (siteIdText == null)
            {
                siteId = siteHelper.getRelevantWebSite(new NodeRef(sectionId));
            }
            else
            {
                siteId = new NodeRef(siteIdText);
            }

            
            List<NodeRef> ancestors = (List<NodeRef>) nodeService.getProperty(new NodeRef(sectionId), WebSiteModel.PROP_ANCESTOR_SECTIONS);
            if(ancestors != null && ancestors.size() != 0){
            	queryBuilder.append("+@ws\\:ancestorSections:\"");
            	queryBuilder.append(ancestors.get(ancestors.size()-1));
            	queryBuilder.append("\" ");
            }else{
            queryBuilder.append("+@ws\\:ancestorSections:\"");
            queryBuilder.append(sectionId);
            queryBuilder.append("\" ");
            }
            queryBuilder.append("+ASPECT:\"");
            queryBuilder.append(WebSiteModel.ASPECT_WEBASSET.toString());
            queryBuilder.append("\" ");
            
            if (phrase != null && phrase.length() != 0)
            {
                String[] tokens = phrase.split(" ");
                queryBuilder.append("+(");
                boolean emptyTag = tag == null || tag.isEmpty();
                for (String token : tokens)
                {
                    queryBuilder.append("+(");
                    queryBuilder.append("@cm\\:title:\"");
                    queryBuilder.append(token);
                    queryBuilder.append("\"^10 ");
                    queryBuilder.append("@cm\\:description:\"");
                    queryBuilder.append(token);
                    queryBuilder.append("\"^5 ");
                    if (emptyTag)
                    {
                        queryBuilder.append("@ws\\:tags:\"");
                        queryBuilder.append(token);
                        queryBuilder.append("\"^3 ");
                    }
                    queryBuilder.append("TEXT:\"");
                    queryBuilder.append(token);
                    queryBuilder.append("\") ");
                }
                queryBuilder.append(") ");
            }
            
            if (tag != null && tag.length() != 0)
            {
                queryBuilder.append("+@ws\\:tags:\"");
                queryBuilder.append(tag);
                queryBuilder.append("\" ");
            }
            
            String query = queryBuilder.toString();
            SearchParameters searchParameters = new SearchParameters();
            searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
            searchParameters.setQuery(query);
            List<Locale> locales = siteHelper.getWebSiteLocales(siteId);
            for (Locale locale : locales)
            {
                searchParameters.addLocale(locale);
            }

            long start = 0L;
            if (log.isDebugEnabled())
            {
                log.debug("About to run query: " + query);
                start = System.currentTimeMillis();
            }
            rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
                    SearchService.LANGUAGE_LUCENE, query);
            if (log.isDebugEnabled())
            {
                long timeTaken = System.currentTimeMillis() - start;
                log.debug("Found " + rs.length() + " results in " + timeTaken + "ms");
            }
            
            long totalResults = rs.length();
            int index = skip;
            int count = 0;
            
            res.setContentEncoding("UTF-8");
            res.setContentType("text/xml");
            Writer writer = res.getWriter();
            AssetSerializer assetSerializer = assetSerializerFactory.getAssetSerializer();
            assetSerializer.start(writer);
            Map<QName, Serializable> header = new HashMap<QName, Serializable>(3);
            header.put(QName.createQName("totalResults"), new Long(totalResults));
            assetSerializer.writeHeader(header);
            while (index < totalResults && count < max)
            {
                ResultSetRow row = rs.getRow(index);
                NodeRef nodeRef = row.getNodeRef();
                QName typeName = nodeService.getType(nodeRef);
                Map<QName,Serializable> props = nodeService.getProperties(nodeRef);
                props.put(QName.createQName("searchScore"), new Integer((int)(row.getScore() * 100)));
                assetSerializer.writeNode(nodeRef, typeName, props);
                ++index;
                ++count;
            }
            assetSerializer.end();
        }
        catch (Throwable e)
        {
            throw createStatusException(e, req, res);
        }
        finally
        {
        	if (rs != null) {rs.close();}
        }
    }
}
