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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.Path;
import org.alfresco.wcm.client.PathResolutionDetails;
import org.alfresco.wcm.client.Query;
import org.alfresco.wcm.client.SearchResults;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base section interface implementation
 * 
 * @author Roy Wetherall
 */
public class SectionImpl extends ResourceBaseImpl implements Section
{
    private static final long serialVersionUID = -443446798048387948L;
    private final static Log log = LogFactory.getLog(SectionImpl.class);

    /** Section children */
    private List<Section> sections = new ArrayList<Section>();

    /** Sections by name */
    private Map<String, Section> sectionsByName = new TreeMap<String, Section>();

    private ConcurrentMap<String, String> assetIdByAssetName = new ConcurrentHashMap<String, String>(89);

    /** Top Tags */
    private List<Tag> tags = new ArrayList<Tag>();

    /** Collection node id for the section. */
    // private String collectionFolderId;

    /** Configuration map */
    private Map<String, String> configMap;

    /** Dictionary service */
    private DictionaryService dictionaryService;

    private Map<String, String> redirects;

    /**
     * @see org.alfresco.wcm.client.Section#getSections()
     */
    @Override
    public List<Section> getSections()
    {
        return Collections.unmodifiableList(sections);
    }

    /**
     * @see org.alfresco.wcm.client.Section#getSection(java.lang.String)
     */
    @Override
    public Section getSection(String sectionName)
    {
        return sectionsByName.get(sectionName);
    }

    /**
     * Set the tags used by this section (and below)
     * 
     * @param tags
     *            the tags used by the section
     */
    public void setTags(List<Tag> tags)
    {
        this.tags = tags;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getTags()
     */
    @Override
    public List<Tag> getTags()
    {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Set the dictionary service
     * 
     * @param dictionaryService DictionaryService
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getTemplateMappings()
     */
    public Map<String, String> getTemplateMappings()
    {
        return Collections.unmodifiableMap(configMap);
    }

    @Override
    public boolean getExcludeFromNav()
    {
        Boolean exclude = (Boolean) getProperty(PROPERTY_EXCLUDE_FROM_NAV);
        return (exclude == null) ? false : exclude.booleanValue();
    }

    /**
     * Sets the child sections. Package visibility since this is only used
     * during construction of the section hierarchy.
     * 
     * @param sections
     *            child sections
     */
    /* package */void setSections(List<Section> sections)
    {
        this.sections = sections;
        Map<String, Section> newMap = new TreeMap<String, Section>();
        for (Section section : sections)
        {
            newMap.put(section.getName(), section);
        }
        sectionsByName = newMap;
    }

    /**
     * @see org.alfresco.wcm.client.impl.ResourceBaseImpl#setProperties(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setProperties(Map<String, Serializable> props)
    {
        super.setProperties(props);

        // Extract the config map for convenience
        configMap = parseConfigProperties((List<String>) props.get(PROPERTY_SECTION_CONFIG));
        redirects = parseConfigProperties((List<String>) props.get(PROPERTY_REDIRECT_CONFIG));
    }

    /**
     * Parses section configuration properties from the name value pair string list
     * into a map.
     * 
     * @param configPropertyList List<String>
     * @return map of types and templates
     */
    private Map<String, String> parseConfigProperties(List<String> configPropertyList)
    {
        Map<String, String> result = new TreeMap<String, String>();
        if (configPropertyList != null)
        {
            for (String configValue : configPropertyList)
            {
                String[] split = configValue.split("=");
                if (split.length == 2)
                {
                    String name = split[0];
                    String value = split[1];
                    result.put(name, value);
                    // We cater for either "cmis:document" or "cm:content"
                    // interchangeably...
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
        }
        return result;
    }

    
    /**
     * Add child to a section
     * 
     * @param section
     *            child section
     */
    void addChild(Section section)
    {
        this.sections.add(section);
        this.sectionsByName.put(section.getName(), section);
    }

    /**
     * @see org.alfresco.wcm.client.Section#getCollectionFolderId()
     */
    /*
     * @Override public String getCollectionFolderId() { return
     * collectionFolderId; }
     */

    /**
     * @see org.alfresco.wcm.client.Section#getCollectionFolderId()
     */
    /*
     * public void setCollectionFolderId(String collectionFolderId) {
     * this.collectionFolderId = collectionFolderId; }
     */

    /**
     * @see org.alfresco.wcm.client.Section#getAsset(java.lang.String)
     */
    @Override
    public Asset getAsset(String resourceName)
    {
        Asset asset;
        if (resourceName == null || resourceName.length() == 0)
        {
            asset = getIndexPage();
        }
        else
        {
            String assetId = assetIdByAssetName.get(resourceName);
            if (assetId == null)
            {
                asset = getAssetFactory().getSectionAsset(getId(), resourceName);
                if (asset != null)
                {
                    assetIdByAssetName.putIfAbsent(resourceName, asset.getId());
                }
            }
            else
            {
                asset = getAssetFactory().getAssetById(assetId);
            }
        }
        return asset;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getIndexPage()
     */
    @Override
    public Asset getIndexPage()
    {
        return getAsset("index.html");
    }

    /**
     * @see org.alfresco.wcm.client.Section#getPath()
     */
    @Override
    public String getPath()
    {
        StringBuilder sb = new StringBuilder("/");
        Section section = this;
        while (section.getContainingSection() != null)
        {
            sb.insert(0, "/" + section.getName());
            section = section.getContainingSection();
        }
        return sb.toString();
    }

    @Override
    public SearchResults search(Query query)
    {
        return getAssetFactory().findByQuery(query);
    }

    @Override
    public SearchResults search(String phrase, int maxResults, int resultsToSkip)
    {
        Query query = createQuery();
        query.setPhrase(phrase);
        query.setMaxResults(maxResults);
        query.setResultsToSkip(resultsToSkip);
        return search(query);
    }

    @Override
    public SearchResults searchByTag(String tag, int maxResults, int resultsToSkip)
    {
        Query query = createQuery();
        query.setTag(tag);
        query.setMaxResults(maxResults);
        query.setResultsToSkip(resultsToSkip);
        return search(query);
    }

    /**
     * @see org.alfresco.wcm.client.Section#createQuery()
     */
    @Override
    public Query createQuery()
    {
        Query query = new Query();
        query.setSectionId(getId());
        return query;
    }

    /**
     * @see org.alfresco.wcm.client.Section#getTemplate(java.lang.String)
     */
    @Override
    public String getTemplate(String type)
    {
        type = dictionaryService.removeTypePrefix(type);
        return findTemplate(this, type);
    }

    @Override
    public AssetCollection getAssetCollection(String name)
    {
        return getCollectionFactory().getCollection(getId(), name);
    }

    @Override
    public AssetCollection getAssetCollection(String name, int resultsToSkip, int maxResults)
    {
        return getCollectionFactory().getCollection(getId(), name, resultsToSkip, maxResults);
    }

    /**
     * Find template for a section by type
     * 
     * @param section
     *            section
     * @param type
     *            content type
     * @return String template based on match
     */
    private String findTemplate(Section section, String type)
    {
        String template = null;

        // See if there is a template match on this section
        template = findTemplate(section.getTemplateMappings(), type);

        // If no template found check parent
        Section parent = section.getContainingSection();
        if (template == null && parent != null)
        {
            template = findTemplate(parent, type);
        }

        return template;
    }

    /**
     * Find the template for a given page type within a template map
     * 
     * @param templateMap Map<String, String>
     * @param type String
     * @return String
     */
    private String findTemplate(Map<String, String> templateMap, String type)
    {
        // Get the template from the map
        String template = templateMap.get(type);

        // If no template is found and we are not already checking for
        // cm:content ...
        if (template == null && dictionaryService.isRootType(type) == false)
        {
            // .. get the parent type name
            String parentType = dictionaryService.getParentType(type, true);
            if (parentType != null)
            {
                // .. and see if we can find a template for that
                template = findTemplate(templateMap, parentType);
            }
        }
        return template;
    }

    @Override
    public PathResolutionDetails resolvePath(String path)
    {
        PathResolutionDetailsImpl details = new PathResolutionDetailsImpl();

        String redirectLocation = checkRedirects(path);
        if (redirectLocation == null)
        {
            // The specified path doesn't match a redirect
            Asset asset = null;

            Path segmentedPath = new PathImpl(path);
            String[] sectionPath = segmentedPath.getPathSegments();
            String resourceName = segmentedPath.getResourceName();

            Section section = getSectionFactory().getSectionFromPathSegments(getId(), sectionPath);
            if (section != null)
            {
                if (resourceName != null && resourceName.length() > 0)
                {
                    String decodedResourceName;
                    try
                    {
                        decodedResourceName = URLDecoder.decode(resourceName, "UTF-8");
                        asset = section.getAsset(decodedResourceName);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        // UTF-8 is mandatory, so this won't happen
                    }
                    // If not found then try filename from URL just in case the
                    // name originally included
                    // + instead of spaces etc.
                    if (asset == null)
                    {
                        asset = section.getAsset(resourceName);
                    }
                }
                else
                {
                    asset = section.getIndexPage();
                }
            }
            details.setAsset(asset);
            details.setSection(section);
        }
        else
        {
            //This path matched a redirect
            details.setRedirect(true);
            details.setRedirectLocation(redirectLocation);
        }
        return details;
    }

    protected String checkRedirects(String path)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Checking for redirect on path " + path + "  Defined redirects are " + redirects);
        }
        return redirects.get(path);
    }
}
