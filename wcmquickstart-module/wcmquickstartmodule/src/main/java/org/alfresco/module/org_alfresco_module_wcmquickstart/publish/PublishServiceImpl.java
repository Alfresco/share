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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.transfer.NodeCrawler;
import org.alfresco.service.cmr.transfer.NodeCrawlerFactory;
import org.alfresco.service.cmr.transfer.TransferDefinition;
import org.alfresco.service.cmr.transfer.TransferFailureException;
import org.alfresco.service.cmr.transfer.TransferService2;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublishServiceImpl implements PublishService
{
    private final static Log log = LogFactory.getLog(PublishServiceImpl.class);
    private final static String PUBLISH_QUEUE_NAME = "publishingQueue";
    private final static Set<String> DEFAULT_ASPECTS_TO_EXCLUDE = new TreeSet<String>();
    
    static 
    {
        DEFAULT_ASPECTS_TO_EXCLUDE.add("fm:discussable");
    }
    
    private TransactionService transactionService;
    private SiteHelper siteHelper;
    private NodeService nodeService;
    private TransferService2 transferService;
    private NamespaceService namespaceService;
    private TransferPathMapper pathMapper;
    private NodeCrawlerFactory nodeCrawlerFactory;
    private NodeCrawlerConfigurer crawlerConfigurer;
    private String transferTargetName = "Internal Target";
    private Set<String> aspectsToExclude = DEFAULT_ASPECTS_TO_EXCLUDE;
    private int maxPublishAttempts = 3;

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setTransferService(TransferService2 transferService)
    {
        this.transferService = transferService;
    }

    public void setTransferTargetName(String transferTargetName)
    {
        this.transferTargetName = transferTargetName;
    }

    public void setPathMapper(TransferPathMapper pathMapper)
    {
        this.pathMapper = pathMapper;
    }

    public void setNodeCrawlerFactory(NodeCrawlerFactory nodeCrawlerFactory)
    {
        this.nodeCrawlerFactory = nodeCrawlerFactory;
    }

    public void setCrawlerConfigurer(NodeCrawlerConfigurer crawlerConfigurer)
    {
        this.crawlerConfigurer = crawlerConfigurer;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void setAspectsToExclude(Collection<String> aspectsToExclude)
    {
        if (aspectsToExclude == null)
        {
            this.aspectsToExclude = new TreeSet<String>();
        }
        else
        {
            this.aspectsToExclude = new TreeSet<String>(aspectsToExclude);
        }
    }

    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    public void setMaxPublishAttempts(int maxPublishAttempts)
    {
        this.maxPublishAttempts = maxPublishAttempts;
    }

    public void enqueuePublishedNodes(final NodeRef... nodes)
    {
        enqueueNodes(false, nodes);
    }

    public void enqueuePublishedNodes(Collection<NodeRef> nodes)
    {
        enqueuePublishedNodes(nodes.toArray(new NodeRef[nodes.size()]));
    }

    public void enqueueRemovedNodes(final NodeRef... nodes)
    {
        AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
        {
            public Void doWork() throws Exception
            {
                enqueueNodes(true, nodes);
                return null;
            }
        });
    }

    public void enqueueRemovedNodes(Collection<NodeRef> nodes)
    {
        enqueueRemovedNodes(nodes.toArray(new NodeRef[nodes.size()]));
    }

    public void enqueueNodes(boolean remove, final NodeRef... nodes)
    {
        if ((nodes != null) && (nodes.length > 0))
        {
            if (log.isDebugEnabled())
            {
                log.debug("Request to enqueue these nodes for publishing: " + Arrays.asList(nodes));
            }
            NodeRef publishingQueue = siteHelper.getWebSiteContainer(nodes[0], PUBLISH_QUEUE_NAME);
            if (publishingQueue != null && !nodeService.hasAspect(publishingQueue, ContentModel.ASPECT_PENDING_DELETE)) // DW
            {
                for (NodeRef node : nodes)
                {
                    Map<QName, Serializable> props = new HashMap<QName, Serializable>();
                    String name = GUID.generate();
                    props.put(ContentModel.PROP_NAME, name);
                    //Storing noderefs of deleted nodes doesn't work, so we'll store a text representation instead...
                    props.put(WebSiteModel.PROP_QUEUED_NODE, node.toString());
                    props.put(WebSiteModel.PROP_QUEUED_NODE_FOR_REMOVAL, remove);
                    nodeService.createNode(publishingQueue, ContentModel.ASSOC_CONTAINS, QName.createQName(
                            WebSiteModel.NAMESPACE, name), WebSiteModel.TYPE_PUBLISH_QUEUE_ENTRY, props);
                }
            }
        }
    }
    
    public void publishQueue(final NodeRef websiteId)
    {
        if (websiteId == null)
        {
            throw new IllegalArgumentException("websiteId == " + websiteId);
        }
        // Locate the target location, and set up an appropriate transfer path
        // mapping
        NodeRef targetSite = null;
        List<AssociationRef> targets = nodeService.getTargetAssocs(websiteId,
                WebSiteModel.ASSOC_PUBLISH_TARGET);
        if (!targets.isEmpty())
        {
            targetSite = targets.get(0).getTargetRef();
            Path sourcePath = nodeService.getPath(websiteId);
            Path targetPath = nodeService.getPath(targetSite);
            pathMapper.addPathMapping(sourcePath, targetPath);

            Set<NodeRef> nodesToTransfer = new HashSet<NodeRef>(89);
            Set<NodeRef> nodesToRemoveOnTransfer = new HashSet<NodeRef>(89);
            NodeRef queue = siteHelper.getWebSiteContainer(websiteId, PUBLISH_QUEUE_NAME);
            if (queue != null)
            {
                List<ChildAssociationRef> publishedNodes = nodeService.getChildAssocs(queue,
                        ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                for (ChildAssociationRef assoc : publishedNodes)
                {
                    NodeRef queueEntry = assoc.getChildRef();
                    NodeRef node = new NodeRef((String) nodeService.getProperty(queueEntry, WebSiteModel.PROP_QUEUED_NODE));
                    boolean remove = (Boolean) nodeService.getProperty(queueEntry, WebSiteModel.PROP_QUEUED_NODE_FOR_REMOVAL);
                    if (remove)
                    {
                        nodesToRemoveOnTransfer.add(node);
                    }
                    else
                    {
                        nodesToTransfer.add(node);
                    }
                }
                if (!nodesToTransfer.isEmpty() || !nodesToRemoveOnTransfer.isEmpty())
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("PublishService is about to crawl these nodes: " + nodesToTransfer);
                    }
                    //Given the nodes that have been supplied, find any others that we will want to transfer too
                    // (note that we don't do any crawling of nodes that are to be removed)
                    NodeCrawler crawler = nodeCrawlerFactory.getNodeCrawler();
                    configureNodeCrawler(crawler);
                    nodesToTransfer = crawler.crawl(nodesToTransfer);

                    if (log.isDebugEnabled())
                    {
                        log.debug("PublishService has crawled the queued nodes and is about to transfer these nodes: " + nodesToTransfer);
                    }
                    
                    TransferDefinition def = new TransferDefinition();
                    def.setNodes(nodesToTransfer);
                    def.setNodesToRemove(nodesToRemoveOnTransfer);
                    Set<QName> aspectQNames = new HashSet<QName>();
                    for (String aspectToExclude : aspectsToExclude)
                    {
                        aspectQNames.add(QName.createQName(aspectToExclude, namespaceService));
                    }
                    def.setExcludedAspects(aspectQNames);
                    
                    try
                    {
                        transferService.transfer(transferTargetName, def);
                    }
                    catch (TransferFailureException e)
                    {
                        final List<ChildAssociationRef> finalPublishedNodes = publishedNodes;
                        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Object>()
                        {

                            @Override
                            public Object execute() throws Throwable
                            {
                                for (ChildAssociationRef childRef : finalPublishedNodes)
                                {
                                    Integer failedAttCount = (Integer) nodeService.getProperty(childRef.getChildRef(), WebSiteModel.PROP_FAILED_ATTEMPTS_COUNT);
                                    if (failedAttCount == null)
                                    {
                                        failedAttCount = 0;
                                    }

                                    failedAttCount++;
                                    if (failedAttCount == maxPublishAttempts)
                                    {
                                        nodeService.deleteNode(childRef.getChildRef());
                                    }
                                    else
                                    {
                                        nodeService.setProperty(childRef.getChildRef(), WebSiteModel.PROP_FAILED_ATTEMPTS_COUNT, failedAttCount);
                                    }
                                }
                                return null;
                            }

                        }, false, true);

                        throw e;
                    }
    
                    // If we get here then the transfer must have completed. Delete
                    // the queue entries that we have processed
                    for (ChildAssociationRef assoc : publishedNodes)
                    {
                        nodeService.deleteNode(assoc.getChildRef());
                    }
                }
            }
            else
            {
                log.warn("Discovered a website node that is outside of a Share site. Skipping. " + websiteId);
            }

        } 
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("Request has been made to publish from a site that has no target configured: "
                    + websiteId);
            }
        }
    }
    
    /**
     * Set up the supplied node crawler to find other nodes that should be published too.
     * Override this if necessary, or (preferably) inject a different configurer
     * @param crawler
     */
    protected void configureNodeCrawler(NodeCrawler crawler)
    {
        crawlerConfigurer.configure(crawler);
    }

    public String getTransferTargetName()
    {
        return transferTargetName;
    }
}
