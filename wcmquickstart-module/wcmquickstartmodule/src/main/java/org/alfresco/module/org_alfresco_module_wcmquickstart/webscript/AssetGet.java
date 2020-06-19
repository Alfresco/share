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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializer;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializerFactory;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Asset GET implementation
 */
public class AssetGet extends AbstractWebScript
{
    private static final String PARAM_NODEREF = "noderef";
    private static final String PARAM_MODIFIED_TIME_ONLY = "modifiedTimeOnly";
    private static final String PARAM_SITE_ID = "siteid";
    private static final String PARAM_SECTION_ID = "sectionid";
    private static final String PARAM_NODE_NAME = "nodename";

    private static final Log log = LogFactory.getLog(AssetGet.class);

    private NodeService nodeService;
    private SearchService searchService;
    private AssetSerializerFactory assetSerializerFactory;
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

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        try
        {
            List<NodeRef> foundNodes = new ArrayList<NodeRef>();
            String[] nodeRefs = req.getParameterValues(PARAM_NODEREF);
            boolean onlyModifiedTime = (req.getParameter(PARAM_MODIFIED_TIME_ONLY) != null);

            if (nodeRefs == null || nodeRefs.length == 0)
            {
                String sectionIdText = req.getParameter(PARAM_SECTION_ID);
                String nodeName = req.getParameter(PARAM_NODE_NAME);
                if (sectionIdText == null || sectionIdText.length() == 0 || nodeName == null || nodeName.length() == 0)
                {
                    throw new WebScriptException("Either noderef or sectionid and nodename are required parameters");
                }

                if (log.isDebugEnabled())
                {
                    log.debug("Received request for named asset in section " + sectionIdText + ": " + nodeName
                            + (onlyModifiedTime ? "   (modified time only)" : ""));
                }

                NodeRef siteId = null;
                String siteIdText = req.getParameter(PARAM_SITE_ID);
                if (siteIdText == null)
                {
                    siteId = siteHelper.getRelevantWebSite(new NodeRef(sectionIdText));
                }
                else
                {
                    siteId = new NodeRef(siteIdText);
                }

                String query = "+@ws\\:parentSections:\"" + sectionIdText + "\" +@cm\\:name:\"" + nodeName + "\"";
                SearchParameters searchParameters = new SearchParameters();
                searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                searchParameters.setLanguage(SearchService.LANGUAGE_LUCENE);
                searchParameters.setQuery(query);
                List<Locale> locales = siteHelper.getWebSiteLocales(siteId);
                for (Locale locale : locales)
                {
                    searchParameters.addLocale(locale);
                }
                ResultSet rs = null;
                
                try
                {
                    rs = searchService.query(searchParameters);
                    if (rs.length() > 0)
                    {
                        foundNodes.add(rs.getNodeRef(0));
                    }
                }
                finally
                {
                	if (rs != null) {rs.close();}
                }
            }
            else
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Received request for specific node(s): " + Arrays.toString(nodeRefs)
                            + (onlyModifiedTime ? "   (modified time only)" : ""));
                }
                for (String nodeRefString : nodeRefs)
                {
                    try
                    {
                        NodeRef nodeRef = new NodeRef(nodeRefString);
                        if (nodeService.exists(nodeRef)
                                && (nodeService.getProperty(nodeRef, ContentModel.PROP_NODE_UUID) != null))
                        {
                            foundNodes.add(nodeRef);
                        }
                    }
                    catch (Exception ex)
                    {
                        // Safe to ignore
                    }
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
                    // Build up map of properties including relationships and
                    // renditions
                    properties = nodeService.getProperties(nodeRef);
                    List<AssociationRef> associations = nodeService.getTargetAssocs(nodeRef,
                            RegexQNamePattern.MATCH_ALL);
                    if (!associations.isEmpty())
                    {
                        HashMap<QName, List<NodeRef>> sourceRelationshipMap = new HashMap<QName, List<NodeRef>>();
                        for (AssociationRef assoc : associations)
                        {
                            QName assocType = assoc.getTypeQName();
                            List<NodeRef> endpoints = sourceRelationshipMap.get(assocType);
                            if (endpoints == null)
                            {
                                endpoints = new ArrayList<NodeRef>();
                                sourceRelationshipMap.put(assocType, endpoints);
                            }
                            endpoints.add(assoc.getTargetRef());
                        }
                        properties.put(QName.createQName(WebSiteModel.NAMESPACE, "sourceRelationships"),
                                sourceRelationshipMap);
                    }

//                    List<ChildAssociationRef> renditionAssocs = nodeService.getChildAssocs(nodeRef,
//                            RenditionModel.ASSOC_RENDITION, RegexQNamePattern.MATCH_ALL);
//                    if (!renditionAssocs.isEmpty())
//                    {
//                        HashMap<QName, NodeRef> renditionsMap = new HashMap<QName, NodeRef>();
//                        for (ChildAssociationRef renditionAssoc : renditionAssocs)
//                        {
//                            QName assocName = renditionAssoc.getQName();
//                            renditionsMap.put(assocName, renditionAssoc.getChildRef());
//                        }
//                        properties.put(QName.createQName(WebSiteModel.NAMESPACE, "renditions"), renditionsMap);
//                    }
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
}
