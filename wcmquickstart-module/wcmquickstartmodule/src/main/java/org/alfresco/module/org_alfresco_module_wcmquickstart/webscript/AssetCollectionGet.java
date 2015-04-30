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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializer;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.AssetSerializerFactory;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Asset collection GET implementation
 * 
 * @author Roy Wetherall
 */
public class AssetCollectionGet extends AbstractWebScript implements WebSiteModel
{
    /** Parameter names */
    private static final String PARAM_COLLECTION_ID = "assetcollectionid";
    private static final String PARAM_COLLECTION_NAME = "name";
    private static final String PARAM_SECTION_ID = "sectionid";
    private static final String PARAM_MODIFIED_TIME_ONLY = "modifiedTimeOnly";

    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private AssetSerializerFactory assetSerializerFactory;

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setAssetSerializerFactory(AssetSerializerFactory assetSerializerFactory)
    {
        this.assetSerializerFactory = assetSerializerFactory;
    }

    /**
     * @see org.springframework.extensions.webscripts.DeclarativeWebScript#executeImpl(org.springframework.extensions.webscripts.WebScriptRequest,
     *      org.springframework.extensions.webscripts.Status,
     *      org.springframework.extensions.webscripts.Cache)
     */
    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        // Get the collection name
        String collectionId = req.getParameter(PARAM_COLLECTION_ID);

        NodeRef collection;
        
        if (collectionId == null)
        {
            Map<String, String> templateVars = req.getServiceMatch().getTemplateVars();
            String collectionName = templateVars.get(PARAM_COLLECTION_NAME);
            if (collectionName == null || collectionName.length() == 0)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "No collection name was provided on the URL.");
            }
    
            // Get the section id
            String sectionId = req.getParameter(PARAM_SECTION_ID);
            if (sectionId == null || sectionId.length() == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No section id parameter specified.");
            }
            else if (NodeRef.isNodeRef(sectionId) == false)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "No section id is not a valid Alfresco node reference. ( " + sectionId + ")");
            }
    
            // Get the section node reference
            NodeRef sectionNodeRef = new NodeRef(sectionId);
    
            // Get the collections node reference
            NodeRef collectionsNodeRef = fileFolderService.searchSimple(sectionNodeRef, "collections");
            if (collectionsNodeRef == null)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "The collections folder for the section " + sectionId
                        + " could not be found.");
            }
    
            // Look for the collection node reference
            collection = fileFolderService.searchSimple(collectionsNodeRef, collectionName);
            if (collection == null)
            {
                throw new WebScriptException(Status.STATUS_NOT_FOUND, "Unable to find collection " + collectionName
                        + " in section " + sectionId);
            }
        }
        else
        {
            if (collectionId.length() == 0)
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST, "No collection id parameter specified.");
            }
            else if (!NodeRef.isNodeRef(collectionId))
            {
                throw new WebScriptException(Status.STATUS_BAD_REQUEST,
                        "The collection id is not a valid Alfresco node reference. ( " + collectionId + ")");
            }
            collection = new NodeRef(collectionId);
        }
        try
        {
            boolean onlyModifiedTime = (req.getParameter(PARAM_MODIFIED_TIME_ONLY) != null);
            
            AssetSerializer assetSerializer = assetSerializerFactory.getAssetSerializer();
            res.setContentEncoding("UTF-8");
            res.setContentType(assetSerializer.getMimeType());
            Writer writer = res.getWriter();

            // Gather the collection data
            Map<QName, Serializable> collectionProps;
            if (onlyModifiedTime)
            {
                collectionProps = new HashMap<QName, Serializable>(3);
                collectionProps.put(ContentModel.PROP_MODIFIED, nodeService.getProperty(collection, 
                        ContentModel.PROP_MODIFIED));
            }
            else
            {
                collectionProps = nodeService.getProperties(collection);
                //If this asset collection already has a containedAssets property then we can use that directly
                //otherwise we'll spoof it from the associations...
                if (!collectionProps.containsKey(PROP_CONTAINED_ASSETS))
                {
                    List<AssociationRef> assocs = nodeService.getTargetAssocs(collection, ASSOC_WEBASSETS);
                    ArrayList<NodeRef> containedAssets = new ArrayList<NodeRef>(assocs.size()); 
                    for (AssociationRef assoc : assocs)
                    {
                        containedAssets.add(assoc.getTargetRef());
                    }
                    collectionProps.put(PROP_CONTAINED_ASSETS, containedAssets);
                }
            }
            
            assetSerializer.start(writer);
            QName typeName = nodeService.getType(collection);
            assetSerializer.writeNode(collection, typeName, collectionProps);
            assetSerializer.end();
        }
        catch (Throwable e)
        {
            throw createStatusException(e, req, res);
        }
    }
}
