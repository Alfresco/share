/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.po.share.dashlet;

import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.FOURTEEN_DAYS;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.SEVEN_DAYS;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.TODAY;
import static org.alfresco.po.share.dashlet.SiteActivitiesHistoryFilter.TWENTY_EIGHT_DAYS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.ALL_ITEMS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.COMMENTS;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.CONTENT;
import static org.alfresco.po.share.dashlet.SiteActivitiesTypeFilter.MEMBERSHIPS;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.EVERYONES_ACTIVITIES;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.IM_FOLLOWING;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.MY_ACTIVITIES;
import static org.alfresco.po.share.dashlet.SiteActivitiesUserFilter.OTHERS_ACTIVITIES;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;

import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test site activity dashlet page elements.
 *
 * @author Michael Suzuki
 * @since 1.5
 */
@Test(groups = { "alfresco-one" })
@Listeners(FailedTestListener.class)
public class SiteAcitivitiesDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_ACTIVITY = "site-activities";

    @BeforeClass(groups = { "alfresco-one" })
    public void loadFile() throws Exception
    {
        siteName = "SiteActivitiesDashletTests" + System.currentTimeMillis();
        uploadDocument();
        navigateToSiteDashboard();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void deleteSite()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test
    public void instantiateDashlet()
    {
        SiteActivitiesDashlet dashlet = dashletFactory.getDashlet(driver, SiteActivitiesDashlet.class).render();
        Assert.assertNotNull(dashlet);
    }

    /**
     * Test process of accessing my documents
     * dashlet from the dash board view.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = "instantiateDashlet")
    public void selectSiteActivityDashlet() throws Exception
    {
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals(title, "Site Activities");
    }

    @Test(dependsOnMethods = "selectSiteActivityDashlet")
    public void getActivities() throws IOException
    {
        SiteActivitiesDashlet dashlet = dashletFactory.getDashlet(driver, SiteActivitiesDashlet.class).render();
        RenderTime timer = new RenderTime(50000);
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(1000L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                driver.navigate().refresh();
                dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
                if (!dashlet.getSiteActivities(LinkType.User).isEmpty())
                    break;
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        List<ShareLink> userLinks = dashlet.getSiteActivities(LinkType.User);
        List<ShareLink> documentLinks = dashlet.getSiteActivities(LinkType.Document);
        Assert.assertNotNull(userLinks);
        if (userLinks.isEmpty())
            saveScreenShot("getActivities.empty");
        Assert.assertFalse(userLinks.isEmpty());

        Assert.assertNotNull(documentLinks);
        Assert.assertFalse(documentLinks.isEmpty());

        List<String> descriptions = dashlet.getSiteActivityDescriptions();
        Assert.assertNotNull(descriptions);
        Assert.assertFalse(descriptions.isEmpty());
    }

    @Test(dependsOnMethods = "getActivities")
    public void selectActivity() throws Exception
    {
        DocumentDetailsPage page = null;
        // This dashlet should not take over a minute to display activity list.
        RenderTime timer = new RenderTime(60000);
        SiteActivitiesDashlet dashlet;
        while (true)
        {
            timer.start();
            try
            {
                driver.navigate().refresh();
                dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
                if (!dashlet.getSiteActivities(LinkType.User).isEmpty())
                    break;
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        List<ShareLink> users = dashlet.getSiteActivities(LinkType.User);
        Assert.assertNotNull(users);
        Assert.assertFalse(users.isEmpty());

        List<ShareLink> documents = dashlet.getSiteActivities(LinkType.Document);
        Assert.assertNotNull(documents);
        Assert.assertFalse(documents.isEmpty());

        page = dashlet.selectActivityDocument(fileName).click().render();
        Assert.assertNotNull(page);
        Assert.assertEquals(true, page.isDocumentDetailsPage());
    }

    @Test(dependsOnMethods = "selectActivity", expectedExceptions = PageException.class)
    public void selectFake() throws Exception
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(siteName).render();
        dashlet.selectActivityDocument("bla");
    }

    @Test(dependsOnMethods = "selectFake", priority = 1)
    public void verifyRss()
    {
        String currentUrl = driver.getCurrentUrl();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        RssFeedPage rssFeedPage = dashlet.selectRssFeed(username, password);
        assertTrue(rssFeedPage.isSubscribePanelDisplay());
        driver.navigate().to(currentUrl);
    }

    @Test(dependsOnMethods = "selectFake", priority = 2)
    public void getAllUserFilters()
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        dashlet.clickUserButton();
        List<SiteActivitiesUserFilter> allUserFilters = dashlet.getUserFilters();
        List<SiteActivitiesUserFilter> expectedFilters = new ArrayList<SiteActivitiesUserFilter>();
        expectedFilters.add(MY_ACTIVITIES);
        expectedFilters.add(OTHERS_ACTIVITIES);
        expectedFilters.add(EVERYONES_ACTIVITIES);
        expectedFilters.add(IM_FOLLOWING);
        for (SiteActivitiesUserFilter siteActivitiesUserFilter : expectedFilters)
        {
            assertTrue(allUserFilters.contains(siteActivitiesUserFilter), "Filter Contains only: " + allUserFilters.toString() + " , But "
                    + siteActivitiesUserFilter.toString() + " also expected in the list");
        }
    }

    @Test(dependsOnMethods = "getAllUserFilters")
    public void getAllTypeFilters()
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        dashlet.clickTypeButton();
        List<SiteActivitiesTypeFilter> allTypeFilters = dashlet.getTypeFilters();
        assertTrue(allTypeFilters.contains(ALL_ITEMS));
        assertTrue(allTypeFilters.contains(COMMENTS));
        assertTrue(allTypeFilters.contains(CONTENT));
        assertTrue(allTypeFilters.contains(MEMBERSHIPS));
    }

    @Test(dependsOnMethods = "getAllTypeFilters")
    public void getAllHistoryFilters()
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        dashlet.clickHistoryButton();
        List<SiteActivitiesHistoryFilter> allHistoryFilters = dashlet.getHistoryFilters();
        assertTrue(allHistoryFilters.contains(TODAY));
        assertTrue(allHistoryFilters.contains(SEVEN_DAYS));
        assertTrue(allHistoryFilters.contains(FOURTEEN_DAYS));
        assertTrue(allHistoryFilters.contains(TWENTY_EIGHT_DAYS));
    }

    @Test(dependsOnMethods = "getAllHistoryFilters")
    public void selectUserFilter()
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectUserFilter(IM_FOLLOWING).render();
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentUserFilter(), IM_FOLLOWING);
        siteDashBoard = dashlet.selectUserFilter(MY_ACTIVITIES).render();
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentUserFilter(), MY_ACTIVITIES);
    }

    @Test(dependsOnMethods = "selectUserFilter")
    public void selectTypeFilter()
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectTypeFilter(CONTENT).render();
        assertEquals(dashlet.getCurrentTypeFilter(), CONTENT);
        siteDashBoard = dashlet.selectTypeFilter(ALL_ITEMS).render();
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentTypeFilter(), ALL_ITEMS);
    }

    @Test(dependsOnMethods = "selectTypeFilter")
    public void selectHistoryFilter()
    {
        navigateToSiteDashboard();
        SiteActivitiesDashlet dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        siteDashBoard = dashlet.selectHistoryFilter(TWENTY_EIGHT_DAYS).render();
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentHistoryFilter(), TWENTY_EIGHT_DAYS);
        siteDashBoard = dashlet.selectHistoryFilter(SEVEN_DAYS).render();
        dashlet = siteDashBoard.getDashlet(SITE_ACTIVITY).render();
        assertEquals(dashlet.getCurrentHistoryFilter(), SEVEN_DAYS);
    }
}
