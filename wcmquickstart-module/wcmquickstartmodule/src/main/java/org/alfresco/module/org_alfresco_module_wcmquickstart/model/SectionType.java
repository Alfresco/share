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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.publish.PublishService;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParserService;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.copy.CopyBehaviourCallback;
import org.alfresco.repo.copy.CopyDetails;
import org.alfresco.repo.copy.CopyServicePolicies;
import org.alfresco.repo.copy.DefaultCopyBehaviourCallback;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.rendition.RenditionDefinition;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:section type behaviours.
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public class SectionType extends TransactionListenerAdapter implements WebSiteModel
{
    /** Transaction key */
    private static final String AFFECTED_CHILD_ASSOCS = "AFFECTED_CHILD_ASSOCS";
    private static final String COPY_NODES = "COPY_NODES";

    /** Log */
    private final static Log log = LogFactory.getLog(SectionType.class);

    private PolicyComponent policyComponent;
    private BehaviourFilter behaviourFilter;
    private PublishService publishService;
    private NodeService nodeService;
    private ContentService contentService;
    private DictionaryService dictionaryService;
    private FileFolderService fileFolderService;
    private RenditionService renditionService;
    private TransactionService transactionService;
    private ContextParserService contextParserService;
    private NamespaceService namespaceService;
    private MimetypeMap mimetypeMap;
    private SectionHierarchyProcessor sectionHierarchyProcessor;
    
    /** The section index page name */
    private String sectionIndexPageName = "index.html";

    /** The section's collection folder name */
    private String sectionCollectionsFolderName = "collections";

    /**
     * This is the list of collections that will be created automatically for
     * any new section.
     */
    private List<AssetCollectionDefinition> collectionDefinitions = Collections.emptyList();

    private Set<String> typesToIgnore = new TreeSet<String>();

    /**
     * Set the policy component
     * 
     * @param policyComponent
     *            policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    /**
     * Set the behaviour filter
     * 
     * @param behaviourFilter
     *            behaviour filter
     */
    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    /**
     * Set the publish service
     * 
     * @param publishService
     *            publish service
     */
    public void setPublishService(PublishService publishService)
    {
        this.publishService = publishService;
    }

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

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set the content service
     * 
     * @param contentService
     *            content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Set the file folder service
     * 
     * @param fileFolderSevice
     *            file folder service
     */
    public void setFileFolderService(FileFolderService fileFolderSevice)
    {
        this.fileFolderService = fileFolderSevice;
    }

    /**
     * Sets the rendition service
     * 
     * @param renditionService
     *            rendition service
     */
    public void setRenditionService(RenditionService renditionService)
    {
        this.renditionService = renditionService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * Set the mimetype map
     * 
     * @param mimetypeMap
     *            mimetype map
     */
    public void setMimetypeMap(MimetypeMap mimetypeMap)
    {
        this.mimetypeMap = mimetypeMap;
    }

    /**
     * Sets the section index page name
     * 
     * @param sectionIndexPageName
     *            section index page name
     */
    public void setSectionIndexPageName(String sectionIndexPageName)
    {
        this.sectionIndexPageName = sectionIndexPageName;
    }

    /**
     * Sets the section collection folder name
     * 
     * @param sectionCollectionsFolderName
     *            section collections folder name
     */
    public void setSectionCollectionsFolderName(String sectionCollectionsFolderName)
    {
        this.sectionCollectionsFolderName = sectionCollectionsFolderName;
    }

    /**
     * Sets the context parser service
     * 
     * @param contextParserService
     *            context parser service
     */
    public void setContextParserService(ContextParserService contextParserService)
    {
        this.contextParserService = contextParserService;
    }

    public void setSectionHierarchyProcessor(SectionHierarchyProcessor sectionHierarchyProcessor)
    {
        this.sectionHierarchyProcessor = sectionHierarchyProcessor;
    }

    /**
     * When a new content node is added into a section, behaviours configured by
     * this class normally cause it to be specialised to either an article or an
     * image. If you have types for which you don't want this to happen, supply
     * their names as prefixed qualified names ("ws:indexPage", for instance) to
     * this method.
     * 
     * @param typesToIgnore Set<String>
     */
    public void setTypesToIgnore(Set<String> typesToIgnore)
    {
        this.typesToIgnore = typesToIgnore;
    }

    /**
     * When a new section is created asset collections can be auto-created.
     * Inject the definitions of them here.
     * 
     * @param collectionDefinitions List<AssetCollectionDefinition>
     */
    public void setAssetCollectionDefinitions(List<AssetCollectionDefinition> collectionDefinitions)
    {
        if (collectionDefinitions == null)
        {
            this.collectionDefinitions = Collections.emptyList();
        }
        else
        {
            this.collectionDefinitions = collectionDefinitions;
        }
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        // Register the association behaviours
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnCreateNodePolicy.QNAME, WebSiteModel.TYPE_SECTION,
                new JavaBehaviour(this, "onCreateNode"));
        
        policyComponent.bindClassBehaviour(NodeServicePolicies.BeforeDeleteNodePolicy.QNAME, WebSiteModel.TYPE_SECTION,
                new JavaBehaviour(this, "beforeDeleteNode"));

        policyComponent.bindClassBehaviour(ContentServicePolicies.OnContentPropertyUpdatePolicy.QNAME,
                WebSiteModel.TYPE_SECTION, new JavaBehaviour(this, "onContentPropertyUpdate"));

        policyComponent.bindClassBehaviour(CopyServicePolicies.OnCopyNodePolicy.QNAME, WebSiteModel.TYPE_SECTION,
                new JavaBehaviour(this, "getCopyCallback", NotificationFrequency.EVERY_EVENT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onCreateChildAssociationEveryEvent", NotificationFrequency.EVERY_EVENT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnCreateChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onCreateChildAssociationTransactionCommit", NotificationFrequency.TRANSACTION_COMMIT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onDeleteChildAssociationEveryEvent", NotificationFrequency.EVERY_EVENT));

        policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteChildAssociationPolicy.QNAME,
                WebSiteModel.TYPE_SECTION, ContentModel.ASSOC_CONTAINS, new JavaBehaviour(this,
                        "onDeleteChildAssociationTransactionCommit", NotificationFrequency.TRANSACTION_COMMIT));
    }

    /**
     * 
     * @param childAssoc ChildAssociationRef
     * @param isNewNode boolean
     */
    public void onCreateChildAssociationEveryEvent(ChildAssociationRef childAssoc, boolean isNewNode)
    {
        if (log.isDebugEnabled())
        {
            log.debug("onCreateChildAssociationEveryEvent: ref == " + childAssoc + "; newNode == " + isNewNode);
        }
        NodeRef childNode = childAssoc.getChildRef();
        QName childNodeType = nodeService.getType(childNode);
        if (ContentModel.TYPE_FOLDER.equals(childNodeType))
        {
            // Down-cast created node to ws:section
            nodeService.setType(childNode, TYPE_SECTION);

            // Fire create section code
            processCreateNode(childNode);
        }

        recordAffectedChild(childAssoc);
    }

    /**
     * 
     * @param childAssoc ChildAssociationRef
     * @param isNewNode boolean
     */
    public void onCreateChildAssociationTransactionCommit(ChildAssociationRef childAssoc, boolean isNewNode)
    {
        processCommit(childAssoc);
    }

    /**
     * 
     * @param childAssoc ChildAssociationRef
     */
    public void onDeleteChildAssociationEveryEvent(ChildAssociationRef childAssoc)
    {
        recordAffectedChild(childAssoc);
    }

    /**
     * 
     * @param childAssoc ChildAssociationRef
     */
    public void onDeleteChildAssociationTransactionCommit(ChildAssociationRef childAssoc)
    {
        processCommit(childAssoc);
    }
    
    @SuppressWarnings("unchecked")
    public CopyBehaviourCallback getCopyCallback(QName classRef, CopyDetails copyDetails)
    {
        //We need to vary what behaviours we apply to a node if it is coming into existence via a copy.
        //Therefore, we'll record the noderef of the copy node here for use later.
        Set<NodeRef> copyNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport.getResource(COPY_NODES);
        if (copyNodeRefs == null)
        {
            copyNodeRefs = new HashSet<NodeRef>(89);
            AlfrescoTransactionSupport.bindResource(COPY_NODES, copyNodeRefs);
        }
        copyNodeRefs.add(copyDetails.getTargetNodeRef());
        return DefaultCopyBehaviourCallback.getInstance();
    }

    /**
     * 
     * @param childAssoc ChildAssociationRef
     */
    private void recordAffectedChild(ChildAssociationRef childAssoc)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Recording affected child of section " + childAssoc.getParentRef() + ":  "
                    + childAssoc.getChildRef());
        }
        @SuppressWarnings("unchecked")
        Set<ChildAssociationRef> affectedChildAssocs = (Set<ChildAssociationRef>) AlfrescoTransactionSupport.getResource(AFFECTED_CHILD_ASSOCS);
        if (affectedChildAssocs == null)
        {
            affectedChildAssocs = new HashSet<ChildAssociationRef>();
            AlfrescoTransactionSupport.bindResource(AFFECTED_CHILD_ASSOCS, affectedChildAssocs);
        }
        affectedChildAssocs.add(childAssoc);
    }

    /**
     * 
     * @param childNodeAssoc ChildAssociationRef
     */
    @SuppressWarnings("unchecked")
    private void processCommit(ChildAssociationRef childNodeAssoc)
    {
        Set<ChildAssociationRef> affectedNodeRefs = (Set<ChildAssociationRef>) AlfrescoTransactionSupport.getResource(AFFECTED_CHILD_ASSOCS);
        Set<NodeRef> copyNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport.getResource(COPY_NODES);

        Set<NodeRef> affectedSections = new HashSet<NodeRef>();
        if (affectedNodeRefs != null && affectedNodeRefs.remove(childNodeAssoc))
        {
            NodeRef childNode = childNodeAssoc.getChildRef();
            NodeRef parentNode = childNodeAssoc.getParentRef();
            if (log.isDebugEnabled())
            {
                log.debug("Processing commit of section child:  " + childNode);
            }
            if (nodeService.exists(childNode))
            {
                //Massage the content type and add the webasset aspect, but only if our parent node 
                //isn't the target of a copy in this transaction. If it is then we can assume that this processing 
                //has already taken place.
                if (copyNodeRefs == null || !copyNodeRefs.contains(parentNode))
                {
                    QName childNodeType = nodeService.getType(childNode);
                    if (ContentModel.TYPE_CONTENT.equals(childNodeType)
                            && !typesToIgnore.contains(childNodeType.toPrefixString(namespaceService)))
                    {
                        // Check to see if this is an image
                        ContentReader reader = contentService.getReader(childNode, ContentModel.PROP_CONTENT);
                        if (reader != null && reader.exists())
                        {
                            String mimetype = reader.getMimetype();
                            if (mimetype != null && mimetype.trim().length() != 0)
                            {
                                if (isImageMimetype(reader.getMimetype()) == true)
                                {
                                    // Make content node an image
                                    nodeService.setType(childNode, TYPE_IMAGE);
                                }
                                else if (mimetypeMap.isText(reader.getMimetype()) == true)
                                {
                                    // Make the content node an article
                                    nodeService.setType(childNode, TYPE_ARTICLE);
                                }
                            }
                        }
                    }
                    if (dictionaryService.isSubClass(childNodeType, ContentModel.TYPE_CONTENT)
                            && !typesToIgnore.contains(childNodeType.toPrefixString(namespaceService)))
                    {
                        // Apply the web asset aspect
                        nodeService.addAspect(childNode, ASPECT_WEBASSET, null);
                    }
                }

                boolean childIsWebAsset = nodeService.hasAspect(childNode, ASPECT_WEBASSET);
                boolean childIsSection = dictionaryService.isSubClass(nodeService.getType(childNode),
                        WebSiteModel.TYPE_SECTION);

                if (childIsSection)
                {
                    affectedSections.add(childNode);
                }
                if (childIsWebAsset)
                {
                    List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(childNode,
                            ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
                    ArrayList<NodeRef> parentSections = new ArrayList<NodeRef>(parentAssocs.size());
                    Set<NodeRef> ancestorSections = new HashSet<NodeRef>();
                    for (ChildAssociationRef assoc : parentAssocs)
                    {
                        NodeRef parent = assoc.getParentRef();
                        if (dictionaryService.isSubClass(nodeService.getType(parent), WebSiteModel.TYPE_SECTION))
                        {
                            parentSections.add(parent);
                            Collection<NodeRef> ancestors = (Collection<NodeRef>) nodeService.getProperty(parent,
                                    PROP_ANCESTOR_SECTIONS);
                            if (ancestors != null)
                            {
                                ancestorSections.addAll(ancestors);
                            }
                        }
                    }
                    try
                    {
                        behaviourFilter.disableBehaviour(childNode, ASPECT_WEBASSET);
                        behaviourFilter.disableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
                        ancestorSections.addAll(parentSections);
                        if (log.isDebugEnabled())
                        {
                            log.debug("Section child is a web asset (" + childNode + "). Setting parent section ids:  "
                                    + parentSections);
                        }
                        nodeService.setProperty(childNode, PROP_PARENT_SECTIONS, parentSections);
                        nodeService.setProperty(childNode, PROP_ANCESTOR_SECTIONS, new ArrayList<NodeRef>(
                                ancestorSections));
                    }
                    finally
                    {
                        behaviourFilter.enableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
                        behaviourFilter.enableBehaviour(childNode, ASPECT_WEBASSET);
                    }
                }
            }
        }
        if (!affectedSections.isEmpty())
        {
            AlfrescoTransactionSupport.bindListener(new SectionCommitTransactionListener(affectedSections));
        }
    }

    public RenditionDefinition cloneRenditionDefinition(RenditionDefinition source, NodeRef context)
    {
        RenditionDefinition clone = renditionService.createRenditionDefinition(source.getRenditionName(), source
                .getActionDefinitionName());
        clone.setExecuteAsynchronously(source.getExecuteAsychronously());
        clone.setParameterValues(source.getParameterValues());

        String pathTemplate = (String) source.getParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE);
        if (pathTemplate != null)
        {
            String resolvedPath = contextParserService.parse(context, pathTemplate);
            clone.setParameterValue(RenditionService.PARAM_DESTINATION_PATH_TEMPLATE, resolvedPath);
        }

        return clone;
    }

    /**
     * On create node behaviour
     * 
     * @param childAssocRef
     *            child association reference
     */
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        if (log.isDebugEnabled())
        {
            log.debug("onCreateNode " + childAssocRef);
        }
        processCreateNode(childAssocRef.getChildRef());
        recordAffectedChild(childAssocRef);
    }

    /**
     * Before delete node behaviour
     * 
     * @param nodeRef
     *            the section node reference
     */
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        // Enqueue nodes
        publishService.enqueueRemovedNodes(nodeRef);
    } 

    /**
     * On creation of a section node
     * 
     * @param section
     *            created child association reference
     */
    @SuppressWarnings("unchecked")
    public void processCreateNode(NodeRef section)
    {
        Set<NodeRef> copyNodeRefs = (Set<NodeRef>) AlfrescoTransactionSupport.getResource(COPY_NODES);
        if ((copyNodeRefs != null) && copyNodeRefs.contains(section))
        {
            //This node was created as a copy of another node. Assume that the index page and asset collections will
            //come across too. No action needed.
            return;
        }
        // Create an index page for the section
        FileInfo indexPage = fileFolderService.create(section, sectionIndexPageName, TYPE_INDEX_PAGE);
        ContentWriter writer = fileFolderService.getWriter(indexPage.getNodeRef());
        writer.setEncoding("UTF-8");
        writer.setMimetype(MimetypeMap.MIMETYPE_HTML);
        writer.putContent("");
        nodeService.addAspect(indexPage.getNodeRef(), ASPECT_WEBASSET, null);
        recordAffectedChild(nodeService.getPrimaryParent(indexPage.getNodeRef()));

        // Create the collections folder node
        FileInfo collectionFolder = fileFolderService.create(section, sectionCollectionsFolderName,
                TYPE_WEBASSET_COLLECTION_FOLDER);

        // and create any configured collections within that folder...
        for (AssetCollectionDefinition collectionDef : collectionDefinitions)
        {
            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(ContentModel.PROP_NAME, collectionDef.getName());
            props.put(ContentModel.PROP_TITLE, collectionDef.getTitle());
            if (collectionDef.getQuery() != null)
            {
                props.put(WebSiteModel.PROP_QUERY, collectionDef.getQuery());
                props.put(WebSiteModel.PROP_QUERY_LANGUAGE, collectionDef.getQueryType().getEngineName());
                props.put(WebSiteModel.PROP_QUERY_RESULTS_MAX_SIZE, collectionDef.getMaxResults());
                props.put(WebSiteModel.PROP_MINS_TO_QUERY_REFRESH, collectionDef.getQueryIntervalMinutes());
            }
            nodeService.createNode(collectionFolder.getNodeRef(), ContentModel.ASSOC_CONTAINS, QName.createQName(
                    NamespaceService.CONTENT_MODEL_1_0_URI, collectionDef.getName()),
                    WebSiteModel.TYPE_WEBASSET_COLLECTION, props);

        }
    }

    public void onContentPropertyUpdate(NodeRef nodeRef, QName propertyQName, ContentData beforeValue,
            ContentData afterValue)
    {
        // Hook in here to process tagscope information
        if (log.isDebugEnabled())
        {
            log.debug("onContentPropertyUpdate: " + nodeRef + ";  " + propertyQName + ";  " + afterValue.toString());
        }
    }

    /**
     * Indicates whether this is am image mimetype or not.
     * 
     * @param mimetype
     *            mimetype
     * @return boolean true if image mimetype, false otherwise
     */
    private boolean isImageMimetype(String mimetype)
    {
        return mimetype.startsWith("image");
    }

    private class SectionCommitTransactionListener extends TransactionListenerAdapter
    {
        private Set<NodeRef> sectionsToProcess = null;

        public SectionCommitTransactionListener(Set<NodeRef> affectedSections)
        {
            this.sectionsToProcess = affectedSections;
        }

        @Override
        public void afterCommit()
        {
            // For each section that has had its ancestors changed we need to
            // adjust any webassets directly
            // below it and any sections directly below it. We then need to
            // process all the affected subsections
            // in the same way
            final RetryingTransactionHelper.RetryingTransactionCallback<Object> work = 
                new RetryingTransactionHelper.RetryingTransactionCallback<Object>()
            {
                public Object execute() throws Throwable
                {
                    sectionHierarchyProcessor.process(sectionsToProcess);
                    return null;
                }
            };

            AuthenticationUtil.runAs(new RunAsWork<Object>()
            {
                @Override
                public Object doWork() throws Exception
                {
                    transactionService.getRetryingTransactionHelper().doInTransaction(work, false, true);
                    return null;
                }
            }, AuthenticationUtil.SYSTEM_USER_NAME);
        }
    }

}
