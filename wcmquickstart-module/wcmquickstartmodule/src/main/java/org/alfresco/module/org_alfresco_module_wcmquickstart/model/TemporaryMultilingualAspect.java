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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.ml.MultilingualContentService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ws:temporaryMultilingual aspect behaviour. This handles turning a regular
 * node into a multilingual one
 * 
 * @author Nick Burch
 */
public class TemporaryMultilingualAspect implements NodeServicePolicies.OnAddAspectPolicy
{
    private static final Log log = LogFactory.getLog(TemporaryMultilingualAspect.class);

    /** Policy component */
    private PolicyComponent policyComponent;

    private BehaviourFilter behaviourFilter;
    
    private DictionaryService dictionaryService;
    
    private MultilingualContentService multilingualContentService;

    private SiteHelper siteHelper;

    private CopyService copyService;

    private NodeService nodeService;

    /**
     * Set the policy component
     * 
     * @param policyComponent policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    public void setMultilingualContentService(MultilingualContentService multilingualContentService)
    {
        this.multilingualContentService = multilingualContentService;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    public void setCopyService(CopyService copyService)
    {
        this.copyService = copyService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Init method. Binds model behaviours to policies.
     */
    public void init()
    {
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnAddAspectPolicy.QNAME,
                WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL, new JavaBehaviour(this, "onAddAspect",
                        NotificationFrequency.TRANSACTION_COMMIT));
        
        if(log.isDebugEnabled())
        {
            log.debug("Enabled behaviour on " + WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL);
        }
    }

    /**
     * Identify the locale of a node. This could be from a ws:language, or
     * could be from walking up the tree until we find one.
     */
    public Locale identifyLocale(NodeRef nodeRef)
    {
        // We can't help them if we don't have a noderef
        if(nodeRef == null)
        {
            return null;
        }
        
        // If this node is the site root, stop looking, we don't know...
        if(siteHelper.isTranslationParentLimitReached(nodeRef))
        {
            return null;
        }
        
        // If the node has the ws:language, use that
        String language = (String)nodeService.getProperty(nodeRef, WebSiteModel.PROP_LANGUAGE);
        if(language != null)
        {
            return new Locale(language);
        }
        
        // If it's a translated document, then try the sys:locale on it
        // (Ignore the locale on non translated documents, as that's likely
        //  to just be the system wide locale, and hence not much use)
        if(nodeService.hasAspect(nodeRef, ContentModel.ASPECT_MULTILINGUAL_DOCUMENT))
        {
            Locale locale = (Locale)nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE);
            if(locale != null && !"".equals(locale))
            {
                return locale;
            }
        }
        
        // Try the parent
        return identifyLocale( nodeService.getPrimaryParent(nodeRef).getParentRef() );
    }
    
    /**
     * When translating a section, copy over certain resources from the
     *  section we're a translation of
     */
    private void copyResourcesForSection(NodeRef newSection, NodeRef sourceSection)
    {
        // If this isn't a section, then we don't need to do anything
        QName type = nodeService.getType(newSection);
        if(! dictionaryService.isSubClass(type, WebSiteModel.TYPE_SECTION))
        {
            // No resources to copy
            return;
        }
        
        // Grab the two languages
        Locale srcLocale = (Locale)nodeService.getProperty(sourceSection, ContentModel.PROP_LOCALE);
        Locale dstLocale = (Locale)nodeService.getProperty(newSection, ContentModel.PROP_LOCALE);
        if(srcLocale == null || dstLocale == null)
        {
            log.warn("One of the sections lacks a locale, this shouldn't happen!");
            return;
        }
        
        // Process the collections
        NodeRef srcCollection = nodeService.getChildByName(
                sourceSection, ContentModel.ASSOC_CONTAINS, "collections"
        );
        NodeRef dstCollection = nodeService.getChildByName(
                newSection, ContentModel.ASSOC_CONTAINS, "collections"
        );
        if(srcCollection != null && dstCollection != null)
        {
            // Copy each child of the collection in turn
            for(ChildAssociationRef ref :nodeService.getChildAssocs(srcCollection))
            {
                if(ref.isPrimary())
                {
                    String name = (String)nodeService.getProperty(ref.getChildRef(), ContentModel.PROP_NAME);
                    if(nodeService.getChildByName(dstCollection, ref.getTypeQName(), name) != null)
                    {
                        // There's already something in the destination collection
                        //  with this name. Assume it's deliberate, and don't copy
                    }
                    else
                    {
                        // Copy the resource over
                        NodeRef copy = copyService.copy(
                                ref.getChildRef(),
                                dstCollection,
                                ref.getTypeQName(),
                                ref.getQName(),
                                true
                        );
                        nodeService.setProperty(copy, ContentModel.PROP_NAME, name);
                    }
                }
                else
                {
                    // Don't copy non primary associations
                    
                    // Note: If it's a static asset collection, we may in future want to try to
                    //  identify the equivalent translated assets, ask Brian R for details... 
                }
            }
        }
        else
        {
            // This shouldn't happen - aspect behaviour should have already triggered
            log.warn("Missing collections on WCM Section! Unable to migrate assets");
        }
        
        
        // Mark the index page as a translation
        NodeRef srcIndex = nodeService.getChildByName(
                sourceSection, ContentModel.ASSOC_CONTAINS, "index.html"
        );
        NodeRef dstIndex = nodeService.getChildByName(
                newSection, ContentModel.ASSOC_CONTAINS, "index.html"
        );
        if(srcIndex != null && dstIndex != null)
        {
            // Ensure the source one is translated
            if(! multilingualContentService.isTranslation(srcIndex))
            {
                multilingualContentService.makeTranslation(srcIndex, srcLocale);
            }
            
            // Now mark the new on as a translation
            multilingualContentService.addTranslation(dstIndex, srcIndex, dstLocale);
        }
        else
        {
            // This shouldn't happen - aspect behaviour should have already triggered
            log.warn("Missing index page on WCM Section! Unable to associate index pages");
        }
    }

    @Override
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeRef translationOf = (NodeRef)nodeService.getProperty(nodeRef, WebSiteModel.PROP_TRANSLATION_OF);
        Boolean initiallyOrphaned = (Boolean)nodeService.getProperty(nodeRef, WebSiteModel.PROP_INITIALLY_ORPHANED);
        
        // Try to identify the language for the node
        Locale locale = identifyLocale(nodeRef);
        
        if(locale == null)
        {
            log.warn("Asked to setup multilingual for " + nodeRef + " but no language given and no " +
                     "translated parent found, no translation added");
        }
        else
        {
            if(log.isDebugEnabled())
            {
                log.debug("Enabling translation in " + locale + " for " + nodeRef);
            }
        }
        
        // Tie things up with the ML Service
        if(translationOf != null)
        {
            if(! multilingualContentService.isTranslation(translationOf))
            {
                // The document we're a translation of isn't itself
                //  marked as a translation!
                throw new AlfrescoRuntimeException("Can't make a document a translation of node without a language");
            }
            
            // If this is an explicit translation, then tie that up with the ML Service
            if(locale != null)
            {
                // Mark this as a translation
                multilingualContentService.addTranslation(
                        nodeRef, translationOf, locale
                );
                
                // Now copy over the resources (collections etc)
                copyResourcesForSection(nodeRef, translationOf);
            }
        }
        else
        {
            if(locale != null)
            {
                // Mark this as being the first translation
                multilingualContentService.makeTranslation(nodeRef, locale);
            }
        }
        
        // If this node is initially orphaned, then create the intermediate folders
        //  that are missing for it
        if(initiallyOrphaned != null && initiallyOrphaned && translationOf != null)
        {
            // We currently have a situation like:
            //   Root:
            //     French  -> Folder1 -> Folder2 -> Document
            //     Spanish -> Folder1 -> (Orphan) Document
            // We need to identify the missing bits and fill them in
            //
            // The logic is:
            //  Start at the original document
            //  Work up until we find something that has a translation
            //   which is the parent of the new document
            //  If we find this, create new folders that fill in the
            //   gap in between, then move the translation
            //  If we don't, then something is wrong and don't create
            //   or move anything (shouldn't normally happen)
            ChildAssociationRef orphanRef = nodeService.getPrimaryParent(nodeRef); 
            NodeRef orphanParent = orphanRef.getParentRef();
            Locale originalLocale = (Locale)nodeService.getProperty(translationOf, ContentModel.PROP_LOCALE);
            
            // Holds the details of the folders we'll need to create
            //  on the translated side
            List<Pair<NodeRef,String>> parents = new ArrayList<Pair<NodeRef,String>>();
            
            NodeRef parent = nodeService.getPrimaryParent(translationOf).getParentRef();
            while(parent != null)
            {
                // If we hit the site root, stop
                if(siteHelper.isTranslationParentLimitReached(parent))
                {
                    break;
                }
                
                // If we hit something that's the translation of the orphan
                //  folder, then we've reached the point to stop
                if(multilingualContentService.isTranslation(parent))
                {
                    if(multilingualContentService.getTranslations(parent).values().contains(orphanParent))
                    {
                        // This folder is translated as where the orphan lives, so stop
                        break;
                    }
                    else
                    {
                        // This folder is translated, but not into the right language
                        // Keep going upwards
                    }
                }
                
                // Record this parent as missing
                String parentName = (String)nodeService.getProperty(parent, ContentModel.PROP_NAME);
                parents.add(new Pair<NodeRef,String>(parent, parentName));
                
                // One level up
                parent = nodeService.getPrimaryParent(parent).getParentRef();
            }
            
            // Create the folders, in reverse
            Collections.reverse(parents);
            NodeRef transParent = orphanParent;
            for(Pair<NodeRef,String> create : parents)
            {
                // Mark the original as being translated
                if(! multilingualContentService.isTranslation(create.getFirst()))
                {
                    multilingualContentService.makeTranslation(create.getFirst(), originalLocale);
                }
                
                // Is there already a folder with the right name that we can claim?
                // This would happen if they created the structure by hand already
                NodeRef transFolder = nodeService.getChildByName(
                        transParent,
                        ContentModel.ASSOC_CONTAINS,
                        create.getSecond()
                );
                if(transFolder == null)
                {
                    // It's not there yet, so create it
                    transFolder = nodeService.createNode(
                            transParent,
                            ContentModel.ASSOC_CONTAINS,
                            QName.createQName(create.getSecond()),
                            nodeService.getType(create.getFirst())
                    ).getChildRef();
                    nodeService.setProperty(transFolder, ContentModel.PROP_NAME, create.getSecond());
                }
                
                // Mark it as a translation
                multilingualContentService.addTranslation(
                        transFolder, create.getFirst(), locale
                );
                
                // Copy the resources
                copyResourcesForSection(transFolder, create.getFirst());
                
                // Ready for the next one
                transParent = transFolder;
            }
            
            // Finally, move the node to its new home
            nodeService.moveNode(
                    nodeRef, transParent, 
                    orphanRef.getTypeQName(), orphanRef.getQName()
            );
        }
        
        // Finally tidy up by removing the temp aspect
        nodeService.removeAspect(nodeRef, WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL); 
    }
}
