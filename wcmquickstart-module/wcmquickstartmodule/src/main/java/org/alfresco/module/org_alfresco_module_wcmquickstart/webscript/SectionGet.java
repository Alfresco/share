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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
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
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Section GET implementation
 */
public class SectionGet extends AbstractWebScript
{
    private static final String PARAM_MODIFIED_TIME_ONLY = "modifiedTimeOnly";
    private static final String PARAM_SECTION_ID = "sectionId";
    private static final String PARAM_SITE_ID = "siteId";
    private static final String PARAM_INCLUDE_CHILDREN = "includeChildren";
    private static final QName PROP_PARENT_ID = QName.createQName(WebSiteModel.NAMESPACE, "parentId");

    private NodeService nodeService;
    private SearchService searchService;
    private AssetSerializerFactory assetSerializerFactory;
    private SiteHelper siteHelper;
    private NamespaceService namespaceService;

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

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        try
        {
            List<NodeRef> foundNodes = new ArrayList<NodeRef>(50);
            Set<NodeRef> foundNodesSet = new HashSet<NodeRef>(179);
            
            String[] sectionIds = req.getParameterValues(PARAM_SECTION_ID);
            boolean onlyModifiedTime = (req.getParameter(PARAM_MODIFIED_TIME_ONLY) != null);
            boolean includeChildren = (req.getParameter(PARAM_INCLUDE_CHILDREN) != null);
            String siteIdText = req.getParameter(PARAM_SITE_ID);

            if (sectionIds == null || sectionIds.length == 0)
            {
                res.setStatus(Status.STATUS_BAD_REQUEST);
                throw new WebScriptException("sectionid is a required parameter");
            }
            
            NodeRef siteId = null;
            if (siteIdText == null)
            {
                siteId = siteHelper.getRelevantWebSite(new NodeRef(sectionIds[0]));
            }
            else
            {
                siteId = new NodeRef(siteIdText);
            }

            for (String nodeRefString : sectionIds)
            {
                try
                {
                    NodeRef nodeRef = new NodeRef(nodeRefString);
                    if (nodeService.exists(nodeRef)
                            && (nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_UUID) != null) &&
                            !foundNodesSet.contains(nodeRef))
                    {
                        foundNodesSet.add(nodeRef);
                        foundNodes.add(nodeRef);
                        if (includeChildren)
                        {
                            List<NodeRef> descendants = findDescendants(siteId, nodeRef);
                            for (NodeRef descendant : descendants)
                            {
                                if (!foundNodesSet.contains(descendant))
                                {
                                    foundNodesSet.add(descendant);
                                    foundNodes.add(descendant);
                                }
                            }
                        }
                    }
                }
                catch (Exception ex)
                {
                    // Safe to ignore
                }
            }

            res.setContentEncoding("UTF-8");
            Writer writer = res.getWriter();
            AssetSerializer assetSerializer = assetSerializerFactory.getAssetSerializer();
            res.setContentType(assetSerializer.getMimeType());
            assetSerializer.start(writer);
            for (NodeRef nodeRef : foundNodes)
            {
                QName typeName = nodeService.getType(nodeRef);
                Map<QName, Serializable> properties;
                if (onlyModifiedTime)
                {
                    properties = new HashMap<QName, Serializable>(3);
                    properties.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(nodeRef,
                            ContentModel.PROP_MODIFIED));
                }
                else
                {
                    properties = nodeService.getProperties(nodeRef);
                    properties.put(PROP_PARENT_ID, nodeService.getPrimaryParent(nodeRef).getParentRef());
                }
                assetSerializer.writeNode(nodeRef, typeName, properties);
            }
            assetSerializer.end();
        }
        catch (Throwable e)
        {
            throw createStatusException(e, req, res);
        }
    }

    private List<NodeRef> findDescendants(NodeRef siteId, NodeRef rootSectionId)
    {
        List<NodeRef> foundNodes = new ArrayList<NodeRef>(200);
        String query = "+PATH:\"" + nodeService.getPath(siteId).toPrefixString(namespaceService) + "//*\" +TYPE:\"" + WebSiteModel.TYPE_SECTION + "\" +@ws\\:ancestorSections:\"" + rootSectionId + "\"";
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
        searchParameters.setQuery(query);
        searchParameters.addSort("@"+WebSiteModel.PROP_ORDER_INDEX.toString(), true);

        ResultSet rs = null;
        try
        {
            rs = searchService.query(searchParameters);
            for (ResultSetRow row : rs)
            {
                foundNodes.add(row.getNodeRef());
            }
            return foundNodes;
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }
    }
}
