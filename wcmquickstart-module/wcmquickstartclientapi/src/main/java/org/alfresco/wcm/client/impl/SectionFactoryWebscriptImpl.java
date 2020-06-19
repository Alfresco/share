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
