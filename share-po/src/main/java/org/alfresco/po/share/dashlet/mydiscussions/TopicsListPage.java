/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */

package org.alfresco.po.share.dashlet.mydiscussions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Class that represents topics list page
 *
 * @author jcule
 */
public class TopicsListPage extends SitePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static By NEW_TOPICS_TITLE = By.cssSelector("div.listTitle");
    private static By RSS_LINK = By.xpath("//a[contains(@id,'rssFeed')]");

    @SuppressWarnings("unchecked")
    @Override
    public TopicsListPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(NEW_TOPICS_TITLE),
                getVisibleRenderElement(RSS_LINK));
        return this;
    }


    @SuppressWarnings("unchecked")
    @Override
    public TopicsListPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Get rss feed url from link and navigate to.
     *
     * @return RssFeedPage
     */
    public HtmlPage selectRssFeed(String username, String password)
    {
        checkNotNull(username);
        checkNotNull(password);
        try
        {
            String currentUrl = driver.getCurrentUrl();
            String rssUrl = findAndWait(RSS_LINK).getAttribute("href");
            String protocolVar = PageUtils.getProtocol(currentUrl);
            rssUrl = rssUrl.replace(protocolVar, String.format("%s%s:%s@", protocolVar, URLEncoder.encode(username, "UTF-8"), URLEncoder.encode(password, "UTF-8")));
            driver.navigate().to(rssUrl);
            return factoryPage.instantiatePage(driver, RssFeedPage.class);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Can't encode to url password or username.", e);
        }
        throw new PageOperationException("Not able to select RSS Feed option");
    }

}
