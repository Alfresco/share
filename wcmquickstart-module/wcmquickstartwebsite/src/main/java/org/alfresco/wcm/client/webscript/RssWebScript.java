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
package org.alfresco.wcm.client.webscript;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetCollection;
import org.alfresco.wcm.client.CollectionFactory;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.util.UrlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * RSS feed. Assumes that the page on which this java-backed web-script is placed
 * has a property named "collection" containing the name of a collection in the repository
 * under the current section.
 * @author Chris Lack
 */
public class RssWebScript extends AbstractWebScript
{
    private final static String FEED_TYPE = "rss_2.0";

    private static final Log log = LogFactory.getLog(RssWebScript.class.getName());
    private UrlUtils urlUtils;
    private CollectionFactory collectionFactory;  
    
    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res)
    throws IOException
    {
        Writer writer = null;
        try
        {
            writer = res.getWriter();
            
            // Get the section placed in the request context by ApplicationDataInterceptor
            RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
            Section section = (Section)requestContext.getValue("section");    
            WebSite webSite = (WebSite)requestContext.getValue("webSite");
            
            // Get the collection property from the page definition
            String collectionName = requestContext.getPage().getProperty("collection");
            if (collectionName == null) throw new WebScriptException("collectionName property must be supplied");
            
            // Fetch the named collection
            AssetCollection collection = (AssetCollection)collectionFactory.getCollection(section.getId(), collectionName);
            
            // Use ROME library to output the colleciton as a syndication feed
            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(FEED_TYPE);

            feed.setTitle(section.getTitle());
            feed.setLink(urlUtils.getWebsiteDomain(webSite)+urlUtils.getUrl(section));
            feed.setDescription(section.getDescription());

            List<SyndEntry> entries = new ArrayList<SyndEntry>();
            for (Asset asset : collection.getAssets())
            {
                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(asset.getTitle());
                entry.setLink(urlUtils.getWebsiteDomain(webSite)+urlUtils.getUrl(asset));
                entry.setUri(urlUtils.getWebsiteDomain(webSite)+urlUtils.getShortUrl(asset));
                entry.setPublishedDate((Date)asset.getProperties().get(Asset.PROPERTY_PUBLISHED_TIME));
                SyndContent description = new SyndContentImpl();
                description.setType("text/html");
                description.setValue(asset.getDescription());
                entry.setDescription(description);
                entries.add(entry);                
            }
            feed.setEntries(entries);

            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer);
            writer.flush();
        }
        catch (IOException e)
        {
            log.error("Unable to output rss", e);
        } 
        catch (FeedException e)
        {
            log.error("Unable to output rss", e);
        }
    }       
    
    public void setUrlUtils(UrlUtils urlUtils)
    {
        this.urlUtils = urlUtils;
    }    
    
    public void setCollectionFactory(CollectionFactory collectionFactory)
    {
        this.collectionFactory = collectionFactory;
    }
}
