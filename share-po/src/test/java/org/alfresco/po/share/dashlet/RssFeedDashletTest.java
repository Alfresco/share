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
package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for RSS Feed dashlet web elements
 * 
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class RssFeedDashletTest extends AbstractSiteDashletTest
{
    private static final String RSS_FEED_DASHLET = "rss-feed";
    private RssFeedDashlet rssFeedDashlet = null;
    private String defaultTitle = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    RssFeedUrlBoxPage rssFeedUrlBoxPage = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the RSS feed of your choice. Click the edit icon on the dashlet to change the feed.";
    private static final String EXP_TITLE_BY_DEFAULT = "Alfresco Blog";
    private static final String CUSTOM_RSS_URL = "http://rss.cnn.com/rss/edition_europe.rss";
    private static final String EXP_CUSTOM_RSS_TITLE = "CNN.com - Europe";
    String rssUrl = "http://feeds.reuters.com/reuters/businessNews";
    String rssTitle = "Reuters: Business News";

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs(username, password);
        siteName = "rssFeedDashletTest" + System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test(groups = "Enterprise-only")
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();;
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.RSS_FEED, 1).render();
        rssFeedDashlet = siteDashBoard.getDashlet(RSS_FEED_DASHLET).render();
        assertNotNull(rssFeedDashlet);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "instantiateDashlet")
    public void verifyTitle()
    {
        defaultTitle = rssFeedDashlet.getTitle();
        assertNotNull(defaultTitle);
        assertEquals(defaultTitle, EXP_TITLE_BY_DEFAULT);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "verifyTitle")
    public void verifyHelpIcon()
    {
        rssFeedDashlet.clickOnHelpIcon();
        assertTrue(rssFeedDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = rssFeedDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        rssFeedDashlet.closeHelpBallon();
        assertFalse(rssFeedDashlet.isBalloonDisplayed());
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "verifyHelpIcon")
    public void clickConfigureButton()
    {
        rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure().render();
        assertNotNull(rssFeedUrlBoxPage);
    }

    @Test(dependsOnMethods = "clickConfigureButton")
    public void configExternalRss()
    {
        rssFeedUrlBoxPage.fillURL(CUSTOM_RSS_URL);
        rssFeedUrlBoxPage.clickOk();
        for (int i = 0; i < 1000; i++)
        {
            if (!defaultTitle.equals(rssFeedDashlet.getTitle()))
            {
                break;
            }
        }
        assertEquals(rssFeedDashlet.getTitle(), EXP_CUSTOM_RSS_TITLE);
    }

    @Test(dependsOnMethods = "configExternalRss")
    public void getNrOfHeadlines() throws InterruptedException
    {
        rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure().render();
        rssFeedUrlBoxPage.fillURL(rssUrl);
        rssFeedUrlBoxPage.selectNrOfItemsToDisplay(RssFeedUrlBoxPage.NrItems.Five);
        assertTrue(rssFeedUrlBoxPage.isOkButtonEnabled());
        assertFalse(rssFeedUrlBoxPage.isLinkNewWindowSelected());
        rssFeedUrlBoxPage.selectOpenLinkNewWindow();
        assertTrue(rssFeedUrlBoxPage.isLinkNewWindowSelected());
        rssFeedUrlBoxPage.selectOpenLinkNewWindow();
        rssFeedUrlBoxPage.clickOk();

        for (int i = 0; i < 1000; i++)
        {
            if (rssFeedDashlet.getTitle().equals(rssTitle))
            {
                break;
            }
        }

        List<ShareLink> links = rssFeedDashlet.getHeadlineLinksFromDashlet();
        assertEquals(links.size(), 5);
    }

}
