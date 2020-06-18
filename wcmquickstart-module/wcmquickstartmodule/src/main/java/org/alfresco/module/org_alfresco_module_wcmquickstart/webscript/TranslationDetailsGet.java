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
