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

import static org.alfresco.po.share.enums.Dashlets.ALFRESCO_ADDONS_RSS_FEED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.RssFeedUrlBoxPage.NrItems;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class SiteAddOnsRssFeedDashletTest extends AbstractSiteDashletTest
{
    private static final String DASHLET_NAME = "addOns-rss";
    private AddOnsRssFeedDashlet rssFeedDashlet = null;
    private String defaultTitle = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    RssFeedUrlBoxPage rssFeedUrlBoxPage = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the latest news from Alfresco Add-ons. Click the edit icon on the dashlet to configure the feed.";
    private static final String EXP_TITLE_BY_DEFAULT = "Alfresco Add-ons RSS Feed";
    private static final String CUSTOM_RSS_URL = "http://projects.apache.org/feeds/atom.xml";
    private static final String EXP_CUSTOM_RSS_TITLE = "Apache Software Foundation Project Releases";
    private static final String headerInfo = "Find, rate, and contribute Alfresco add-ons and extensions. Visit the Alfresco Add-ons Home Page";
    String rssUrl = "http://feeds.reuters.com/reuters/businessNews";
    String rssTitle = "Reuters: Business News";

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "AddOnsRssDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();;
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(ALFRESCO_ADDONS_RSS_FEED, 1).render();
        rssFeedDashlet = siteDashBoard.getDashlet(DASHLET_NAME).render();
        assertNotNull(rssFeedDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void getDefaultTitle()
    {
        defaultTitle = rssFeedDashlet.getTitle();
        assertNotNull(defaultTitle);
        assertEquals(defaultTitle, EXP_TITLE_BY_DEFAULT);
    }

    @Test(dependsOnMethods = "getDefaultTitle")
    public void verifyHelpIcon()
    {
        rssFeedDashlet.clickOnHelpIcon();
        assertTrue(rssFeedDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = rssFeedDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        rssFeedDashlet.closeHelpBallon();
        assertFalse(rssFeedDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyHeaderInfo()
    {
        assertTrue(rssFeedDashlet.getHeaderInfo().equals(headerInfo));
    }

    @Test(dependsOnMethods = "verifyHeaderInfo")
    public void clickConfigureButton()
    {
        rssFeedUrlBoxPage = rssFeedDashlet.clickConfigure().render();
        assertNotNull(rssFeedUrlBoxPage);
    }

    @Test(dependsOnMethods = "clickConfigureButton")
    public void configExternalRss() throws InterruptedException
    {
        rssFeedUrlBoxPage.fillURL(CUSTOM_RSS_URL);
        rssFeedUrlBoxPage.clickOk();
        for (int i = 0; i < 1000; i++)
        {
            if (rssFeedDashlet.getTitle().equals(EXP_CUSTOM_RSS_TITLE))
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
        rssFeedUrlBoxPage.selectNrOfItemsToDisplay(NrItems.Five);
        rssFeedUrlBoxPage.clickOk();

        for (int i = 0; i < 1000; i++)
        {
            if (rssFeedDashlet.getTitle().equals(rssTitle))
            {
                break;
            }
        }

        List<ShareLink> links = rssFeedDashlet.getHeadlineLinksFromDashlet();
        assertTrue(links.size() > 4);
    }

}
