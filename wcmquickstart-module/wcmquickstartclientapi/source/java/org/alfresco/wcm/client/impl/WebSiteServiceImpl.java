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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.SectionFactory;
import org.alfresco.wcm.client.UgcService;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.WebSiteService;
import org.alfresco.wcm.client.impl.cache.SimpleCache;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

/**
 * Web site service implementation
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public class WebSiteServiceImpl extends WebSiteService
{
    private static final Log log = LogFactory.getLog(WebSiteServiceImpl.class);
    /** Query for all web sites */
    private static final String QUERY_WEB_ROOTS = "select f.cmis:objectId, w.ws:hostName, w.ws:hostPort, t.cm:title, t.cm:description, w.ws:webAppContext, w.ws:siteConfig "
            + "from cmis:folder as f "
            + "join ws:website as w on w.cmis:objectId = f.cmis:objectId "
            + "join cm:titled as t on t.cmis:objectId = f.cmis:objectId";

    /** Web site cache */
    private Map<String, WebSite> webSiteCache;
    private long webSiteCacheRefeshedAt;

    /** Cache timeout values (seconds) */
    private int webSiteCacheRefreshAfter = 60;
    private int webSiteSectionCacheRefreshAfter = 60;

    private SectionFactory sectionFactory;
    private AssetFactory assetFactory;
    private WebScriptCaller webscriptCaller;
    private SimpleCache<String, String> formIdCache;

    private String logoFilename;

    /**
     * Set the number of seconds after which the web site cache will refresh.
     * 
     * @param webSiteCacheRefreshAfter
     *            seconds
     */
    public void setWebSiteCacheRefreshAfter(int webSiteCacheRefreshAfter)
    {
        this.webSiteCacheRefreshAfter = webSiteCacheRefreshAfter;
    }

    /**
     * Set the number of seconds after which the web site section cache will
     * refresh.
     * 
     * @param webSiteSectionCacheRefreshAfter
     *            seconds
     */
    public void setWebSiteSectionCacheRefreshAfter(int webSiteSectionCacheRefreshAfter)
    {
        this.webSiteSectionCacheRefreshAfter = webSiteSectionCacheRefreshAfter;
    }

    /**
     * @see org.alfresco.wcm.client.WebSiteService#getWebSite(java.lang.String,
     *      int)
     */
    public WebSite getWebSite(String hostName, int hostPort)
    {
        return getWebSite(hostName, hostPort, null);
    }

    public WebSite getWebSite(String hostName, int hostPort, String contextPath)
    {
        if (contextPath == null)
        {
            contextPath = "/";
        } 
        else if (!contextPath.startsWith("/"))
        {
            contextPath = "/" + contextPath;
        }
        String key = (hostName + ":" + hostPort).toLowerCase() + contextPath;
        WebSite website = getWebSiteCache().get(key);
        if (website == null)
        {
            //It would be fairly unusual for us to have received a request for a specific host and port that
            //doesn't map to a website in the repo. Could it be that our cache is out of date?
            //We'll refresh the cache once now just to check.
            refreshWebsiteCache();
            website = getWebSiteCache().get(key);
        }
        if (website == null)
        {
            log.warn("Received a request for unrecognised host+port: " + key);
        }
        return website;
    }

    /**
     * @see org.alfresco.wcm.client.WebSiteService#getWebSites()
     */
    public Collection<WebSite> getWebSites()
    {
        return getWebSiteCache().values();
    }

    /**
     * Gets the web site cache
     * 
     * @return Map<String, WebSite> map of web sites by host:port
     */
    private Map<String, WebSite> getWebSiteCache()
    {
        if (webSiteCache == null || webSiteCacheExpired() == true)
        {
            refreshWebsiteCache();
        }
        return webSiteCache;
    }

    private void refreshWebsiteCache()
    {
        Map<String, WebSite> newCache = new HashMap<String, WebSite>(5);

        Session session = CmisSessionHelper.getSession();

        // Execute query
        if (log.isDebugEnabled())
        {
            log.debug("About to run CMIS query: " + QUERY_WEB_ROOTS);
        }            
        ItemIterable<QueryResult> results = session.query(QUERY_WEB_ROOTS, false);
        for (QueryResult result : results)
        {
            // Get the details of the returned object
            String id = result.getPropertyValueById(PropertyIds.OBJECT_ID);
            String hostName = result.getPropertyValueById(WebSite.PROP_HOSTNAME);
            BigInteger hostPort = result.getPropertyValueById(WebSite.PROP_HOSTPORT);
            String context = result.getPropertyValueById(WebSite.PROP_CONTEXT);
            if (context == null)
            {
                context = "";
            }
            if (context.startsWith("/"))
            {
                context = context.substring(1);
            }
            if(hostPort == null)
            {
                // Default to port 80 if not set
                hostPort = new BigInteger("80");
            }
            String key = (hostName + ":" + hostPort.toString()).toLowerCase() + "/" + context;

            String title = result.getPropertyValueById(Asset.PROPERTY_TITLE);
            String description = result.getPropertyValueById(Asset.PROPERTY_DESCRIPTION);
            List<String> configList = result.getPropertyMultivalueById(WebSite.PROP_SITE_CONFIG);
            Map<String,String> configProperties = parseSiteConfig(configList);

            WebsiteInfo siteInfo = getWebsiteInfo(id);

            WebSiteImpl webSite = new WebSiteImpl(id, hostName, hostPort.intValue(),
                    webSiteSectionCacheRefreshAfter);
            webSite.setRootSectionId(siteInfo.rootSectionId);
            webSite.setTitle(title);
            webSite.setDescription(description);
            webSite.setContext(context);
            webSite.setSectionFactory(sectionFactory);
            webSite.setConfig(configProperties);
            webSite.setUgcService(createUgcService(session, siteInfo));
            
            newCache.put(key, webSite);

            // Find the logo asset id
            Asset logo = assetFactory.getSectionAsset(siteInfo.rootSectionId, logoFilename, true);
            webSite.setLogo(logo);
        }

        webSiteCacheRefeshedAt = System.currentTimeMillis();
        webSiteCache = newCache;
    }

    protected UgcService createUgcService(Session session, WebsiteInfo siteInfo)
    {
        UgcServiceCmisImpl ugcService = new UgcServiceCmisImpl(session
                .createObjectId(siteInfo.feedbackFolderId));
        ugcService.setFormIdCache(formIdCache);
        return ugcService;
    }

    private Map<String, String> parseSiteConfig(List<String> configList)
    {
        Map<String, String> result = new TreeMap<String, String>();
        if (configList != null)
        {
            for (String configValue : configList) 
            {
                //Make sure we cater for empty values when parsing the name/value pairs
                String[] split = configValue.split("=", -1);
                if (split.length == 2)
                {
                    result.put(split[0], split[1]);
                }
            }
        }
        return result;
    }

    private WebsiteInfo getWebsiteInfo(String websiteid)
    {
        String feedbackFolderId = websiteid;
        String rootSectionId = websiteid;
        try
        {
            WebscriptParam[] params = new WebscriptParam[] {
                    new WebscriptParam("websiteid", websiteid)
            };
            JSONObject jsonObject = webscriptCaller.getJsonObject("websiteinfo", Arrays.asList(params));
            if (jsonObject != null)
            {
                JSONObject data = (JSONObject) jsonObject.get("data");
                feedbackFolderId = data.getString("feedbackfolderid");
                rootSectionId = data.getString("rootsectionid");
            }
        } 
        catch (Exception ex)
        {
            log.error("Error while attempting to retrieve feedback folder for website " + websiteid, ex);
        }
        return new WebsiteInfo(rootSectionId,feedbackFolderId);
    }

    /**
     * Indicates whether the web site cache has expired.
     * 
     * @return boolean true if expired, false otherwise
     */
    private boolean webSiteCacheExpired()
    {
        boolean result = true;
        long now = System.currentTimeMillis();
        long difference = now - webSiteCacheRefeshedAt;
        long calcValue = webSiteCacheRefreshAfter * 1000;
        if (difference <= calcValue)
        {
            result = false;
        }
        return result;
    }

    /**
     * Set the logo image filename pattern, eg logo.%
     * 
     * @param logo
     */
    public void setLogoFilename(String logoFilename)
    {
        this.logoFilename = logoFilename;
    }

    public void setSectionFactory(SectionFactory sectionFactory)
    {
        this.sectionFactory = sectionFactory;
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

	public void setWebscriptCaller(WebScriptCaller webscriptCaller)
    {
        this.webscriptCaller = webscriptCaller;
    }

    public void setFormIdCache(SimpleCache<String, String> formIdCache)
    {
        this.formIdCache = formIdCache;
    }

    public final static class WebsiteInfo
    {
        public final String rootSectionId;
        public final String feedbackFolderId;

        public WebsiteInfo(String rootSectionId, String feedbackFolderId)
        {
            this.rootSectionId = rootSectionId;
            this.feedbackFolderId = feedbackFolderId;
        }
    }
}
