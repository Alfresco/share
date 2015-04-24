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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.SqlUtils;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class for creating Sections from the repository
 * 
 * @author Chris Lack
 * @author Brian Remmington
 */
public class SectionFactoryCmisImpl extends AbstractCachingSectionFactoryImpl
{
    private static final String COLUMNS = " f.cmis:objectId, f.cmis:name, t.cm:title, t.cm:description, f.cmis:objectTypeId, "
            + "f.cmis:parentId, f.ws:sectionConfig, f.ws:excludeFromNavigation, ts.cm:tagScopeSummary ";

    private final static Log log = LogFactory.getLog(SectionFactoryCmisImpl.class);

    private static final String QUERY_SECTION_WITH_CHILDREN = "select " + COLUMNS + ", o.ws:orderIndex as ord "
            + "from ws:section as f " + "join cm:titled as t on t.cmis:objectId = f.cmis:objectId "
            + "join ws:ordered as o on o.cmis:objectId = f.cmis:objectId "
            + "join cm:tagscope as ts on ts.cmis:objectId = f.cmis:objectId "
            + "where (in_tree(f, {0}) or f.cmis:objectId = {1}) " + "order by ord";
    /*
     * private static final String QUERY_COLLECTION_FOLDERS =
     * "select f.cmis:objectId, f.cmis:parentId "+ "from cmis:folder as f " +
     * "join ws:webassetCollectionFolder as c on c.cmis:objectId = f.cmis:objectId "
     * + "where in_tree(f, {0})";
     */

    /**
     * Create a Section from a QueryResult
     * 
     * @param result
     *            query result
     * @return Section section object
     */
    protected SectionDetails buildSection(QueryResult result)
    {
        SectionDetails sectionDetails = new SectionDetails();

        SectionImpl section = new SectionImpl();

        Map<String, Serializable> properties = new TreeMap<String, Serializable>();
        properties.put(PropertyIds.OBJECT_ID, (String) result.getPropertyValueById(PropertyIds.OBJECT_ID));
        properties.put(PropertyIds.NAME, (String) result.getPropertyValueById(PropertyIds.NAME));
        properties.put(Section.PROPERTY_TITLE, (String) result.getPropertyValueById(Section.PROPERTY_TITLE));
        properties.put(Section.PROPERTY_DESCRIPTION, 
                (String) result.getPropertyValueById(Section.PROPERTY_DESCRIPTION));
        properties.put(Section.PROPERTY_EXCLUDE_FROM_NAV, 
                (Boolean) result.getPropertyValueById(Section.PROPERTY_EXCLUDE_FROM_NAV));

        List<String> configList = result.getPropertyMultivalueById(Section.PROPERTY_SECTION_CONFIG);
        properties.put(Section.PROPERTY_SECTION_CONFIG, (Serializable) configList);

        section.setProperties(properties);
        List<String> tagSummary = result.getPropertyMultivalueById(PROPERTY_TAG_SUMMARY);
        section.setTags(createTags(tagSummary));
        section.setSectionFactory(this);
        section.setAssetFactory(getAssetFactory());
        section.setDictionaryService(getDictionaryService());
        section.setCollectionFactory(getCollectionFactory());

        sectionDetails.section = section;
        sectionDetails.objectTypeId = (String) result.getPropertyValueById(PropertyIds.OBJECT_TYPE_ID);

        // Don't set parent of webroot as it is conceptually the top of the tree
        if (!sectionDetails.objectTypeId.equals("F:ws:webroot"))
        {
            String parentId = (String) result.getPropertyValueById(PropertyIds.PARENT_ID);
            section.setPrimarySectionId(parentId);
            // TODO keep parent id in SectionDetails too as not accessible from
            // resource. Is this deliberate?
            sectionDetails.parentId = parentId;
        }

        return sectionDetails;
    }

    /**
     * Parses the section configuration from the name value pair string list
     * into a map.
     * 
     * @param sectionConfigList
     * @return Map<String, String> map of types and templates
     */
    private Map<String, String> parseSectionConfig(List<String> sectionConfigList)
    {
        Map<String, String> result = new TreeMap<String, String>();
        for (String configValue : sectionConfigList)
        {
            String[] split = configValue.split("=");
            if (split.length == 2)
            {
                String name = split[0];
                String value = split[1];
                result.put(name, value);
                //We cater for either "cmis:document" or "cm:content" interchangeably...
                if ("cmis:document".equals(name))
                {
                    result.put("cm:content", value);
                }
                else if ("cm:content".equals(name))
                {
                    result.put("cmis:document", value);
                }
            }
            else
            {
                // TODO log
            }
        }
        return result;
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
        Session session = CmisSessionHelper.getSession();
        Map<String,Section> loadedSections = new TreeMap<String, Section>();

        // Fetch sections
        String query = MessageFormat.format(QUERY_SECTION_WITH_CHILDREN, SqlUtils.encloseSQLString(topSectionId),
                SqlUtils.encloseSQLString(topSectionId));
        log.debug("About to run CMIS query: " + query);
        ItemIterable<QueryResult> results = session.query(query, false);

        List<SectionDetails> orderedList = new ArrayList<SectionDetails>();
        for (QueryResult result : results)
        {
            SectionDetails sectionDetails = buildSection(result);
            if (!sectionDetails.objectTypeId.equals("F:ws:webroot")
                    && !sectionDetails.objectTypeId.equals("F:ws:section"))
                continue;

            orderedList.add(sectionDetails);
            loadedSections.put(sectionDetails.section.getId(), sectionDetails.section);
        }

        // Add child sections to parents
        for (SectionDetails details : orderedList)
        {
            if (!details.section.getId().equals(topSectionId))
            {
                SectionImpl parent = (SectionImpl) loadedSections.get(details.parentId);
                parent.addChild(details.section);
            }
        }
        return loadedSections;
    }
}
