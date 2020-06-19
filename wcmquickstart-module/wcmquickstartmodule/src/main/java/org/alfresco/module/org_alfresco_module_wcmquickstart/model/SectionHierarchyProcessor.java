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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for processing section hierarchies to calculate ancestor and parent sections.
 * @author Brian
 *
 */
public class SectionHierarchyProcessor implements WebSiteModel
{
    private static Log log = LogFactory.getLog(SectionHierarchyProcessor.class);

    private BehaviourFilter behaviourFilter;
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    private SearchService searchService;
    private TransactionService transactionService;
    private boolean initialised = false;

    public SectionHierarchyProcessor()
    {
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setSearchService(SearchService searchService)
    {
        this.searchService = searchService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @SuppressWarnings("unchecked")
    public void process(Set<NodeRef> affectedSections)
    {
        List<SectionAncestorRecord> sectionsToProcess = new LinkedList<SectionAncestorRecord>();
        for (NodeRef sectionId : affectedSections)
        {
            sectionsToProcess.add(new SectionAncestorRecord(sectionId, null));
        }

        Set<NodeRef> processedSections = new HashSet<NodeRef>(89);
        while (!sectionsToProcess.isEmpty())
        {
            SectionAncestorRecord record = sectionsToProcess.remove(0);
            NodeRef currentSection = record.getSectionId();
            Set<NodeRef> thisSectionsAncestors = record.getAncestorSections();

            if (thisSectionsAncestors == null)
            {
                thisSectionsAncestors = new HashSet<NodeRef>();
                NodeRef sectionParent = nodeService.getPrimaryParent(currentSection).getParentRef();

                if (dictionaryService.isSubClass(nodeService.getType(sectionParent), WebSiteModel.TYPE_SECTION))
                {
                    thisSectionsAncestors.add(sectionParent);

                    Collection<NodeRef> ancestors = (Collection<NodeRef>) nodeService.getProperty(sectionParent,
                            PROP_ANCESTOR_SECTIONS);
                    if (ancestors != null)
                    {
                        thisSectionsAncestors.addAll(ancestors);
                    }
                }
            }
            try
            {
                behaviourFilter.disableBehaviour(currentSection, TYPE_SECTION);
                behaviourFilter.disableBehaviour(currentSection, ContentModel.ASPECT_AUDITABLE);
                nodeService.setProperty(currentSection, PROP_ANCESTOR_SECTIONS, new ArrayList<NodeRef>(
                        thisSectionsAncestors));
            }
            finally
            {
                behaviourFilter.enableBehaviour(currentSection, ContentModel.ASPECT_AUDITABLE);
                behaviourFilter.enableBehaviour(currentSection, TYPE_SECTION);
            }

            ArrayList<NodeRef> childrensAncestors = new ArrayList<NodeRef>(thisSectionsAncestors);
            childrensAncestors.add(currentSection);

            List<ChildAssociationRef> childAssets = nodeService.getChildAssocsByPropertyValue(currentSection,
                    PROP_PARENT_SECTIONS, currentSection);
            for (ChildAssociationRef childAssoc : childAssets)
            {
                processWebAssetsSections(childAssoc.getChildRef(), childrensAncestors);
            }

            Set<QName> sectionTypes = new HashSet<QName>(dictionaryService.getSubTypes(TYPE_SECTION, true));
            List<ChildAssociationRef> subsections = nodeService.getChildAssocs(currentSection, sectionTypes);
            for (ChildAssociationRef subsection : subsections)
            {
                NodeRef child = subsection.getChildRef();
                if (!processedSections.contains(child))
                {
                    sectionsToProcess.add(new SectionAncestorRecord(child, childrensAncestors));
                }
            }
            processedSections.add(currentSection);
        }
    }

    @SuppressWarnings("unchecked")
    public void processWebAssetsSections(NodeRef childNode, ArrayList<NodeRef> knownAncestors)
    {
        if (childNode != null && nodeService.hasAspect(childNode, ASPECT_WEBASSET))
        {
            List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(childNode,
                    ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
            ArrayList<NodeRef> parentSections = new ArrayList<NodeRef>(parentAssocs.size());
            Set<NodeRef> ancestorSections = new HashSet<NodeRef>();
            if (parentAssocs.size() == 1 && knownAncestors != null)
            {
                parentSections.add(parentAssocs.get(0).getParentRef());
                try
                {
                    behaviourFilter.disableBehaviour(childNode, ASPECT_WEBASSET);
                    behaviourFilter.disableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
                    nodeService.setProperty(childNode, PROP_PARENT_SECTIONS, parentSections);
                    nodeService.setProperty(childNode, PROP_ANCESTOR_SECTIONS, knownAncestors);
                }
                finally
                {
                    behaviourFilter.enableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
                    behaviourFilter.enableBehaviour(childNode, ASPECT_WEBASSET);
                }
            }
            else
            {
                for (ChildAssociationRef assoc : parentAssocs)
                {
                    NodeRef parentNode = assoc.getParentRef();
                    if (dictionaryService.isSubClass(nodeService.getType(parentNode), WebSiteModel.TYPE_SECTION))
                    {
                        parentSections.add(parentNode);
                        Collection<NodeRef> ancestors = (Collection<NodeRef>) nodeService.getProperty(parentNode,
                                PROP_ANCESTOR_SECTIONS);
                        if (ancestors != null)
                        {
                            ancestorSections.addAll(ancestors);
                        }
                    }
                    ancestorSections.addAll(parentSections);
                    try
                    {
                        behaviourFilter.disableBehaviour(childNode, ASPECT_WEBASSET);
                        behaviourFilter.disableBehaviour(childNode, ContentModel.ASPECT_AUDITABLE);
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
    }

    public void init()
    {
        if (!initialised)
        {
            final RetryingTransactionHelper.RetryingTransactionCallback<Object> work = 
                new RetryingTransactionHelper.RetryingTransactionCallback<Object>()
            {
                public Object execute() throws Throwable
                {
                ResultSet rs = null;

                try
                {
                    Set<NodeRef> sectionsToProcess = new HashSet<NodeRef>();
                    rs = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
                    SearchService.LANGUAGE_LUCENE, "+TYPE:\"" + TYPE_WEB_ROOT + "\"");
                    for (ResultSetRow row : rs)
                    {
                        if (!nodeService.hasAspect(row.getNodeRef(), ASPECT_HAS_ANCESTORS))
                        {
                            sectionsToProcess.add(row.getNodeRef());
                        }
                    }
                    if (!sectionsToProcess.isEmpty())
                    {
                        process(sectionsToProcess);
                    }
               }
               finally
               {
                   if (rs != null) {rs.close();}
               }
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
            
            initialised = true;
        }
    }

    private static class SectionAncestorRecord
    {
        private final NodeRef sectionId;
        private final Set<NodeRef> ancestorSections;

        public SectionAncestorRecord(NodeRef sectionId, Collection<NodeRef> ancestorSections)
        {
            super();
            this.sectionId = sectionId;
            if (ancestorSections == null)
            {
                this.ancestorSections = null;
            }
            else
            {
                this.ancestorSections = new HashSet<NodeRef>(ancestorSections);
            }
        }

        public NodeRef getSectionId()
        {
            return sectionId;
        }

        public Set<NodeRef> getAncestorSections()
        {
            return ancestorSections;
        }

    }
}
