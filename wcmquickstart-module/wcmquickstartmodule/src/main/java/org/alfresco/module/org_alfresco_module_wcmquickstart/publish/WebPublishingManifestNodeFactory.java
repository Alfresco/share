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
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.transfer.TransferContext;
import org.alfresco.repo.transfer.manifest.TransferManifestDeletedNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNode;
import org.alfresco.repo.transfer.manifest.TransferManifestNodeFactory;
import org.alfresco.repo.transfer.manifest.TransferManifestNormalNode;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.transfer.TransferDefinition;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides a means to transfer nodes within a single repository. Since the transfer service
 * keeps UUIDs the same on both the source and target ends, this class carries out a transformation
 * on the UUIDs to ensure no conflicts occur.
 * This is based on a class originally written by Mark Rogers to enable unit tests.
 * 
 * @author Brian
 */
public class WebPublishingManifestNodeFactory implements TransferManifestNodeFactory, TransferPathMapper
{
    private final static Log log = LogFactory.getLog(WebPublishingManifestNodeFactory.class);
    /**
     * List of paths
     * 
     * <From Path, To Path>
     */
    private List<Pair<Path, Path>> pathMap = new ArrayList<Pair<Path, Path>>();

    /**
     * The node factory that does the work for real.
     */
    private TransferManifestNodeFactory realFactory;
    private NodeService nodeService;
    private DescriptorService descriptorService;
    private NodeRefMapper nodeRefMapper;


    public void setDelegate(TransferManifestNodeFactory realFactory)
    {
        this.realFactory = realFactory;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }

    public void setNodeRefMapper(NodeRefMapper nodeRefMapper)
    {
        this.nodeRefMapper = nodeRefMapper;
    }


    @Override
    public TransferManifestNode createTransferManifestNode(NodeRef nodeRef, TransferDefinition definition, TransferContext transferContext)
    {
        return createTransferManifestNode(nodeRef, definition, transferContext, false);
    }

    public TransferManifestNode createTransferManifestNode(NodeRef nodeRef, TransferDefinition definition, TransferContext transferContext, boolean forceDelete)
    {
        Date now = new Date();
        TransferManifestNode newNode = preProcess(nodeRef);
        if (newNode == null)
        {
            newNode = realFactory.createTransferManifestNode(nodeRef, definition, transferContext, forceDelete);
        }

        long start = 0L;
        if (log.isDebugEnabled())
        {
            start = System.currentTimeMillis();
        }
        NodeRef origNodeRef = newNode.getNodeRef();
        
        /**
         * Fiddle with the node ref to prevent a clash with the source
         */
        NodeRef mappedNodeRef = getMappedNodeRef(origNodeRef);
        newNode.setNodeRef(mappedNodeRef);
        newNode.setUuid(mappedNodeRef.getId());

        /**
         * Fiddle with the parent node ref and parent path.
         */
        ChildAssociationRef primaryParentAssoc = newNode.getPrimaryParentAssoc();
        NodeRef mappedParentNodeRef = getMappedNodeRef(primaryParentAssoc.getParentRef());
        Path parentPath = newNode.getParentPath();
        newNode.setParentPath(getMappedPath(parentPath));
        newNode.setPrimaryParentAssoc(new ChildAssociationRef(primaryParentAssoc.getTypeQName(), mappedParentNodeRef,
                primaryParentAssoc.getQName(), mappedNodeRef, primaryParentAssoc.isPrimary(),
                primaryParentAssoc.getNthSibling()));

        if (newNode instanceof TransferManifestNormalNode)
        {
            //Fiddle with the parent assocs
            TransferManifestNormalNode normalNode = (TransferManifestNormalNode) newNode;
            List<ChildAssociationRef> mappedParentAssocs = new ArrayList<ChildAssociationRef>();
            List<ChildAssociationRef> assocs = normalNode.getParentAssocs();
            for (ChildAssociationRef assoc : assocs)
            {
                ChildAssociationRef replace = new ChildAssociationRef(assoc.getTypeQName(), getMappedNodeRef(assoc.getParentRef()),
                        assoc.getQName(), mappedNodeRef, assoc.isPrimary(), assoc.getNthSibling());
                mappedParentAssocs.add(replace);
            }
            normalNode.setParentAssocs(mappedParentAssocs);

            //Fiddle with the UUID property
            Map<QName, Serializable> props = normalNode.getProperties();
            if (props.containsKey(ContentModel.PROP_NODE_UUID))
            {
                props.put(ContentModel.PROP_NODE_UUID, mappedNodeRef.getId());
            }
            //Set the published time if this is a web asset
            if (props.containsKey(WebSiteModel.PROP_PUBLISHED_TIME))
            {
                props.put(WebSiteModel.PROP_PUBLISHED_TIME, now);
            }
            
            //Re-process noderef properties
            for (Map.Entry<QName, Serializable> propEntry : props.entrySet())
            {
                Serializable value = propEntry.getValue();
                if (value instanceof List<?>)
                {
                    @SuppressWarnings("unchecked")
                    List<Serializable> collection = (List<Serializable>)value;
                    if (!collection.isEmpty() && (collection.get(0) instanceof NodeRef))
                    {
                        ArrayList<Serializable> newList = new ArrayList<Serializable>(collection.size());
                        for (Serializable thisValue : collection)
                        {
                            newList.add(getMappedNodeRef((NodeRef)thisValue));
                        }
                        props.put(propEntry.getKey(), newList);
                    }
                }
                else
                {
                    if (value instanceof NodeRef)
                    {
                        props.put(propEntry.getKey(), getMappedNodeRef((NodeRef)value));
                    }
                }
            }
            
            //Re-process the associations coming *to* this node
            List<AssociationRef> sourceAssocs = normalNode.getSourceAssocs();
            List<AssociationRef> newSourceAssocs = new ArrayList<AssociationRef>(sourceAssocs.size());
            for (AssociationRef sourceAssoc : sourceAssocs)
            {
                newSourceAssocs.add(new AssociationRef(sourceAssoc.getId(), getMappedNodeRef(sourceAssoc.getSourceRef()), 
                        sourceAssoc.getTypeQName(), mappedNodeRef));
            }
            normalNode.setSourceAssocs(newSourceAssocs);
            
            //Re-process the associations going *from* this node
            List<AssociationRef> targetAssocs = normalNode.getTargetAssocs();
            List<AssociationRef> newTargetAssocs = new ArrayList<AssociationRef>(targetAssocs.size());
            for (AssociationRef targetAssoc : targetAssocs)
            {
                newTargetAssocs.add(new AssociationRef(targetAssoc.getId(), mappedNodeRef, 
                        targetAssoc.getTypeQName(), getMappedNodeRef(targetAssoc.getTargetRef())));
            }
            normalNode.setTargetAssocs(newTargetAssocs);
        }
        else if (newNode instanceof TransferManifestDeletedNode)
        {
            processDeletedNode((TransferManifestDeletedNode)newNode);
        }
        if (log.isDebugEnabled())
        {
            log.debug("Time taken to adjust manifest node: " + (System.currentTimeMillis() - start) + "ms"); 
        }

        return newNode;
    }

    /**
     * This operation provides a hook point for implementations to avoid invoking the delegated manifest node factory.
     * If it returns null then the delegated factory will be invoked, otherwise it won't and the manifest node returned
     * from here will be used in the transfer instead.
     * @param nodeRef NodeRef
     * @return TransferManifestNode
     */
    protected TransferManifestNode preProcess(NodeRef nodeRef)
    {
        TransferManifestNode result = null;

        /*
         * The default implementation works around an issue (ALF-4333) that appears in the transfer service
         * on versions 3.3c, 3.3.0, 3.3.1, 3.3.2, and 3.3.3.
         */
        Descriptor descriptor = descriptorService.getServerDescriptor();
        if ("3".equals(descriptor.getVersionMajor()) && "3".equals(descriptor.getVersionMinor()))
        {
            //Are we a version lower than 3.3.4?
            if ("4".compareTo(descriptor.getVersionRevision()) > 0)
            {
                //Yes. Apply the workaround...
                NodeRef.Status status = nodeService.getNodeStatus(nodeRef);
                if (status.isDeleted())
                {
                    TransferManifestDeletedNode deletedNode = new TransferManifestDeletedNode();
                    deletedNode.setNodeRef(new NodeRef(StoreRef.STORE_REF_ARCHIVE_SPACESSTORE, nodeRef.getId()));
                    ChildAssociationRef dummyPrimaryParent = new ChildAssociationRef(ContentModel.ASSOC_CONTAINS, 
                            nodeRef, QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dummy"), 
                            nodeRef, true, -1);
                    deletedNode.setPrimaryParentAssoc(dummyPrimaryParent);
                    deletedNode.setParentPath(new Path());
                    result = deletedNode;
                }
            }
        }
        return result;
    }

    protected void processDeletedNode(TransferManifestDeletedNode newNode)
    {
    }

    /**
     * Get the mapped node ref
     * 
     * @param node NodeRef
     * @return the mapped node ref or null;
     */
    public NodeRef getMappedNodeRef(NodeRef node)
    {
       return nodeRefMapper.mapSourceNodeRef(node);
    }

    /**
     * Get mapped path
     */
    public Path getMappedPath(Path pathToConvert)
    {
        Path convertedPath = null;

        List<Pair<Path,Path>> possibleMatches = new ArrayList<Pair<Path,Path>>(pathMap);
        int pathIndex = 0;
        int pathSize = pathToConvert.size();
        
        while ((pathIndex < pathSize) && (convertedPath == null) && (!possibleMatches.isEmpty()))
        {
            Path.Element currentElement = pathToConvert.get(pathIndex);
            String currentElementString = currentElement.getElementString();

            int pairIndex = 0;
            while (pairIndex < possibleMatches.size())
            {
                Pair<Path,Path> currentMapping = possibleMatches.get(pairIndex);
                Path from = currentMapping.getFirst();
                if ((pathIndex >= from.size()) || !currentElementString.equals(from.get(pathIndex).getElementString()))
                {
                    possibleMatches.remove(pairIndex);
                }
                else
                {
                    if (pathIndex == (from.size() - 1))
                    {
                        //We've found a complete match!
                        Path to = currentMapping.getSecond();
                        convertedPath = new Path();
                        convertedPath.append(to);
                        while (++pathIndex < pathSize)
                        {
                            convertedPath.append(pathToConvert.get(pathIndex));
                        }
                    }
                    else
                    {
                        ++pairIndex;
                    }
                }
            }
            ++pathIndex;
        }
        if (convertedPath == null)
        {
            convertedPath = pathToConvert;
        }
        return convertedPath;
    }


    public void setPathMap(List<Pair<Path, Path>> pathMap)
    {
        this.pathMap = new ArrayList<Pair<Path,Path>>(pathMap);
    }

    public void addPathMapping(Path source, Path target)
    {
        addPathMapping(new Pair<Path, Path>(source, target));
    }
    
    public void addPathMapping(Pair<Path,Path> mapping)
    {
        //Check whether we already have a mapping for the source path.
        //Replace it if we have, add it if we haven't
        boolean found = false;
        Path source = mapping.getFirst();
        for (int index = 0; index < pathMap.size(); ++index)
        {
            Pair<Path,Path> pair = pathMap.get(index);
            if (pair.getFirst().equals(source))
            {
                pair.setSecond(mapping.getSecond());
                found = true;
                break;
            }
        }
        if (!found)
        {
            pathMap.add(mapping);
        }
    }
}
