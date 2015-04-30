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
package org.alfresco.wcm.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class for creating Sections from the repository
 * 
 * @author Chris Lack
 */
public class SectionFactoryWebscriptImpl extends AbstractCachingSectionFactoryImpl
{
    private ThreadLocal<List<WebscriptParam>> localParamList = new ThreadLocal<List<WebscriptParam>>()
    {
        @Override
        protected List<WebscriptParam> initialValue()
        {
            return new ArrayList<WebscriptParam>();
        }

        @Override
        public List<WebscriptParam> get()
        {
            List<WebscriptParam> list = super.get();
            list.clear();
            return list;
        }

    };

    private final static Log log = LogFactory.getLog(SectionFactoryWebscriptImpl.class);

    private WebScriptCaller webscriptCaller;

    public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }

    /**
     * Create a Section from a QueryResult
     * 
     * @param result
     *            query result
     * @return Section section object
     */
    @SuppressWarnings("unchecked")
    protected SectionDetails buildSection(TreeMap<String, Serializable> result)
    {
        SectionDetails sectionDetails = new SectionDetails();

        SectionImpl section = new SectionImpl();

        section.setProperties(result);

        List<String> tagSummary = (List<String>) result.get(PROPERTY_TAG_SUMMARY);
        section.setTags(createTags(tagSummary));
        section.setSectionFactory(this);
        section.setAssetFactory(getAssetFactory());
        section.setDictionaryService(getDictionaryService());
        section.setCollectionFactory(getCollectionFactory());

        sectionDetails.section = section;
        sectionDetails.objectTypeId = (String) result.get("type");

        // Don't set parent of webroot as it is conceptually the top of the tree
        if (!sectionDetails.objectTypeId.equals("ws:webroot"))
        {
            String parentId = (String) result.get("ws:parentId");
            section.setPrimarySectionId(parentId);
            sectionDetails.parentId = parentId;
        }

        return sectionDetails;
    }

    /**
     * Fetch a section and its children.
     * 
     * @param topSectionId
     *            the section id to start from
     * @return the section object with its children populated.
     */
    protected Map<String,Section> findSectionWithChildren(String topSectionId)
    {
        if (log.isDebugEnabled())
        {
            log.debug(Thread.currentThread().getName() + " loading section tree starting at " + topSectionId);
        }
        Map<String,Section> loadedSections = new TreeMap<String, Section>();
        List<WebscriptParam> params = localParamList.get();
        params.add(new WebscriptParam("sectionId", topSectionId));
        params.add(new WebscriptParam("includeChildren", "true"));
        WebSite currentSite = WebSiteService.getThreadWebSite();
        if (currentSite != null)
        {
            params.add(new WebscriptParam("siteId", currentSite.getId()));
        }
        AssetDeserializerXmlImpl deserializer = new AssetDeserializerXmlImpl();
        webscriptCaller.post("websection", deserializer, params);
        LinkedList<TreeMap<String, Serializable>> sectionList = deserializer.getAssets();

        List<SectionDetails> orderedList = new ArrayList<SectionDetails>();
        for (TreeMap<String, Serializable> result : sectionList)
        {
            SectionDetails sectionDetails = buildSection(result);
            orderedList.add(sectionDetails);
            loadedSections.put(sectionDetails.section.getId(), sectionDetails.section);
        }

        for (SectionDetails details : orderedList)
        {
            if (!details.section.getId().equals(topSectionId))
            {
                // Add child sections to parents
                SectionImpl parent = (SectionImpl) loadedSections.get(details.parentId);
                parent.addChild(details.section);
            }
        }
        return loadedSections;
    }
}
