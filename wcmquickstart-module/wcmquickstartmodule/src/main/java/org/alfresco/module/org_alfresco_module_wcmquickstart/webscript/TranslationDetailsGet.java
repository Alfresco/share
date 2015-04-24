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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.util.SiteHelper;
import org.alfresco.service.cmr.ml.MultilingualContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Translation Details GET implementation
 */
public class TranslationDetailsGet extends DeclarativeWebScript
{
    private static final String PARAM_NODEREF = "nodeRef";
    private static final String PARAM_LOCALES = "locales";
    private static final String PARAM_NODE_LOCALE = "nodeLocale";
    private static final String PARAM_TRANSLATIONS = "translations";
    private static final String PARAM_TRANSLATION_ENABLED = "translationEnabled";
    private static final String PARAM_TRANSLATION_PARENTS = "translationParents";
    
    private static final Log log = LogFactory.getLog(TranslationDetailsGet.class);

    private NodeService nodeService;
    private SiteHelper siteHelper;
    private MultilingualContentService multilingualContentService;

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setSiteHelper(SiteHelper siteHelper)
    {
        this.siteHelper = siteHelper;
    }

    public void setMultilingualContentService(MultilingualContentService multilingualContentService)
    {
        this.multilingualContentService = multilingualContentService;
    }

    @Override
    public Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();

        // Grab the nodeRef to work on
        String nodeRefParam = req.getParameter(PARAM_NODEREF);
        if(nodeRefParam == null || nodeRefParam.length() == 0)
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "NodeRef not supplied but required");
        }
        NodeRef nodeRef = new NodeRef(nodeRefParam);
        if(! nodeService.exists(nodeRef))
        {
            throw new WebScriptException(Status.STATUS_NOT_FOUND, "NodeRef not found");
        }
        model.put(PARAM_NODEREF, nodeRef);
        
        // Get the locales for the site
        NodeRef website = siteHelper.getRelevantWebSite(nodeRef);
        List<Locale> locales = siteHelper.getWebSiteLocales(website);
        model.put(PARAM_LOCALES, locales);
        
        // Get the translations for the node
        Map<Locale,NodeRef> translations;
        boolean translationEnabled = false;
        Locale nodeLocale = null;
        if(multilingualContentService.isTranslation(nodeRef))
        {
            translations = multilingualContentService.getTranslations(nodeRef);
            translationEnabled = true;
            nodeLocale = (Locale)nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE);
        }
        else
        {
            translations = Collections.emptyMap();
        }
        model.put(PARAM_TRANSLATIONS, translations);
        model.put(PARAM_TRANSLATION_ENABLED, translationEnabled);
        
        // Find the nearest parent per locale for the node, as available
        // Also records if all the intermediate directories exist
        Map<Locale,Pair<NodeRef,Boolean>> parents = new HashMap<Locale, Pair<NodeRef,Boolean>>();
        NodeRef nodeParent = nodeService.getPrimaryParent(nodeRef).getParentRef();
        NodeRef current = nodeParent;
        while(current != null)
        {
            if(siteHelper.isTranslationParentLimitReached(current))
            {
                break;
            }
            
            if(multilingualContentService.isTranslation(current))
            {
                Map<Locale,NodeRef> parentTranslations = multilingualContentService.getTranslations(current);
                for(Locale locale : parentTranslations.keySet())
                {
                    // Record only the farthest out one for each language
                    if(! parents.containsKey(locale))
                    {
                        Pair<NodeRef,Boolean> details = new Pair<NodeRef, Boolean>(
                                parentTranslations.get(locale), (current == nodeParent)
                        );
                        parents.put(locale, details);
                    }
                }
 
                // If we don't have a locale yet, try to infer it from the parent
                // However, only pay attention to the locales of translated parents,
                //  we ignore the locale on non translated parents as that's usually just
                //  the server locale and not of much use
                if(nodeLocale == null)
                {
                    if(nodeService.hasAspect(current, ContentModel.ASPECT_MULTILINGUAL_DOCUMENT))
                    {
                        nodeLocale = (Locale)nodeService.getProperty(current, ContentModel.PROP_LOCALE);
                    }
                }
            }
            
            current = nodeService.getPrimaryParent(current).getParentRef();
        }
        model.put(PARAM_TRANSLATION_PARENTS, parents);
        model.put(PARAM_NODE_LOCALE, nodeLocale);
        
        return model;
    }
}
