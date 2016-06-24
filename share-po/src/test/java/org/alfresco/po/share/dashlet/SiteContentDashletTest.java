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

import static org.alfresco.po.share.dashlet.SiteContentFilter.I_AM_EDITING;
import static org.alfresco.po.share.dashlet.SiteContentFilter.I_HAVE_RECENTLY_MODIFIED;
import static org.alfresco.po.share.dashlet.SiteContentFilter.MY_FAVOURITES;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.sitecontent.DetailedViewInformation;
import org.alfresco.po.share.dashlet.sitecontent.SimpleViewInformation;
import org.alfresco.po.share.site.document.DocumentDetailsPage;

import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test site content dashlet page elements.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.4
 */
@Test(groups={"check", "alfresco-one"})
@Listeners(FailedTestListener.class)
public class SiteContentDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_CONTENT = "site-contents";
    
    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "SiteConentDashletTests" + System.currentTimeMillis();
     }
    
    @BeforeClass
    public void loadFile() throws Exception
    {
        uploadDocument();
        navigateToSiteDashboard();
    }
    
    @AfterClass
    public void deleteSite()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    
    @Test
    public void instantiateDashlet()
    {
        SiteContentDashlet dashlet = dashletFactory.getDashlet(driver, SiteContentDashlet.class).render();
        Assert.assertNotNull(dashlet);
    }

    @Test(dependsOnMethods="instantiateDashlet", expectedExceptions = PageException.class)
    public void selectFake() throws Exception
    {
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(siteName).render();
        dashlet.select("bla");
    }

    /**
     * Test process of accessing my documents
     * dashlet from the dash board view.
     * @throws Exception 
     */
    @Test(dependsOnMethods="selectFake")
    public void selectSiteContentDashlet() throws Exception
    {
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        final String title = dashlet.getDashletTitle();
        Assert.assertEquals("Site Content", title);
    }

    @Test(dependsOnMethods = "selectSiteContentDashlet")
    public void getCurrentSiteContentFilters() throws Exception
    {
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        SiteContentFilter currentFilter = dashlet.getCurrentFilter();
        assertEquals(currentFilter, I_HAVE_RECENTLY_MODIFIED);
    }

    @Test(dependsOnMethods="getCurrentSiteContentFilters")
    public void selectContent() throws Exception
    {
        DocumentDetailsPage page = null;
        // This dashlet should not take over a minute to display content list.
        RenderTime timer = new RenderTime(60000);
        SiteContentDashlet dashlet;
        while (true)
        {
            timer.start();
            try
            {
                driver.navigate().refresh();
                dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
                if (!dashlet.getSiteContents().isEmpty()) break;
            }
            catch (PageException e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        List<ShareLink> list = dashlet.getSiteContents();
        Assert.assertNotNull(list);
        Assert.assertFalse(list.isEmpty());

        page = dashlet.select(fileName).click().render();

        Assert.assertNotNull(page);
        Assert.assertEquals(true, page.isDocumentDetailsPage());
    }
    
    @Test(dependsOnMethods="selectContent")
    public void getMySiteContentFilters() throws Exception
    {
        navigateToSiteDashboard();
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();

        Assert.assertTrue(dashlet.isDetailButtonDisplayed());
        Assert.assertTrue(dashlet.isSimpleButtonDisplayed());

        dashlet.clickFilterButtton();
        List<SiteContentFilter> list = dashlet.getFilters();
        Assert.assertNotNull(list);
        List<String> expectedValues = new ArrayList<String>();

        expectedValues.add("I've Recently Modified");
        expectedValues.add("I'm Editing");
        expectedValues.add("My Favorites");

        List<String> actualValues = new ArrayList<String>();

        for (SiteContentFilter link : list)
        {
            actualValues.add(link.getDescription());
        }

        for (String expectedValue : expectedValues)
        {
            Assert.assertTrue(actualValues.contains(expectedValue));
        }

        dashlet.clickHelpButton();
        String actualHelpBalloonMessage = "This dashlet makes it easy to keep track of your recent changes to library content in this site. Clicking the item name or thumbnail takes you to the details page so you can preview or work with the item.There are two views for this dashlet. The detailed view lets you:Mark an item as a favorite so it appears in Favorites lists for easy accessLike (and unlike) an itemJump to the item details page to leave a comment";
        Assert.assertEquals(actualHelpBalloonMessage, dashlet.getHelpBalloonMessage());
        dashlet.closeHelpBallon();
    }
    
    @Test(dependsOnMethods="getMySiteContentFilters")
    public void selectAllFilters() throws Exception
    {
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        siteDashBoard = dashlet.selectFilter(MY_FAVOURITES).render();
        dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        assertEquals(dashlet.getCurrentFilter(), MY_FAVOURITES);

        siteDashBoard = dashlet.selectFilter(I_HAVE_RECENTLY_MODIFIED).render();
        dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        assertEquals(dashlet.getCurrentFilter(), I_HAVE_RECENTLY_MODIFIED);

        siteDashBoard = dashlet.selectFilter(I_AM_EDITING).render();
        dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        assertEquals(dashlet.getCurrentFilter(), I_AM_EDITING);
    }
    
    @Test(dependsOnMethods="selectAllFilters")
    public void getSimpleViewInformation() throws Exception
    {
        navigateToSiteDashboard();
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        dashlet.clickSimpleView();
        int count = 0;
        boolean rendered = false;
        while (count < 10)
        {
            dashlet.selectFilter(SiteContentFilter.I_HAVE_RECENTLY_MODIFIED);
            try
            {
                count++;
                dashlet = dashlet.renderSimpleViewWithContent();
                rendered = true;
                break;
            }
            catch (TimeoutException timeoutException)
            {
            }
            catch (PageRenderTimeException exception)
            {
            }
            dashlet.render();
        }

        assertTrue(rendered, "Simple view not able render with contents");

        List<SimpleViewInformation> informations = dashlet.getSimpleViewInformation();
        assertNotNull(informations);
        assertEquals(informations.size(), 1);

        for (SimpleViewInformation simpleViewInformation : informations)
        {
            assertTrue(simpleViewInformation.getContentStatus().contains("Created"));
            assertNotNull(simpleViewInformation.getContentDetail());
            assertNotNull(simpleViewInformation.getThumbnail());
            assertNotNull(simpleViewInformation.getUser());
        }
    }
    
    @Test(dependsOnMethods="getSimpleViewInformation")
    public void getDetailedViewInformation() throws Exception
    {
        navigateToSiteDashboard();
        SiteContentDashlet dashlet = siteDashBoard.getDashlet(SITE_CONTENT).render();
        dashlet.clickDetailView();
        int count = 0;
        boolean rendered = false;
        while (count < 10)
        {
            dashlet.selectFilter(SiteContentFilter.I_HAVE_RECENTLY_MODIFIED);
            try
            {
                count++;
                dashlet = dashlet.renderDetailViewWithContent();
                rendered = true;
                break;
            }
            catch (TimeoutException timeoutException)
            {
            }
            catch (PageRenderTimeException exception)
            {
            }
            dashlet.render();
        }

        assertTrue(rendered, "Detail view not able render with contents");

        dashlet.clickDetailView();
        dashlet.renderDetailViewWithContent();
        
        List<DetailedViewInformation> informations = dashlet.getDetailedViewInformation();
        assertNotNull(informations);
        assertEquals(informations.size(), 1);

        for (DetailedViewInformation detailedViewInformation : informations)
        {
            assertTrue(detailedViewInformation.getContentStatus().contains("Created"));
            assertFalse(detailedViewInformation.isPreviewDisplayed());
            assertNotNull(detailedViewInformation.getContentDetail());
            assertNotNull(detailedViewInformation.getThumbnail());
            assertNotNull(detailedViewInformation.getUser());
            assertEquals(detailedViewInformation.getDescription(), "No Description");
            assertEquals(detailedViewInformation.getVersion(), 1.0);
            assertEquals(detailedViewInformation.getLikecount(), 1);
            assertEquals(detailedViewInformation.getFileSize(), "33 bytes");
            assertNotNull(detailedViewInformation.getLike());
            assertNotNull(detailedViewInformation.getFavorite());
            assertNotNull(detailedViewInformation.getComment());
        }
    }

}
