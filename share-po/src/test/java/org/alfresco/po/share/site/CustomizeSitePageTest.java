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
package org.alfresco.po.share.site;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Customize Site Page Test.
 * 
 * @author Shan Nagarajan
 * @since  1.7.0
 */
@Listeners(FailedTestListener.class)
public class CustomizeSitePageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomiseSiteDashboardPage customizeSiteDashboardPage;
    CustomizeSitePage customizeSitePage;
    WikiPage wikiPage;

    @BeforeClass(groups={"Enterprise4.1", "Enterprise-only"})
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "customizePage" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }
    
    @Test(groups="Enterprise-only")
    public void selectCustomizeSite() throws Exception
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        assertNotNull(customizeSitePage);
    }
    
    @Test(dependsOnMethods="selectCustomizeSite", groups="Enterprise-only")
    public void getAvailablePages()
    {
        List<SitePageType> availablePageTypes = customizeSitePage.getAvailablePages();
        assertNotNull(availablePageTypes);
        List<SitePageType> expectedPageTypes = new ArrayList<SitePageType>();
        Collections.addAll(expectedPageTypes, SitePageType.values());
        expectedPageTypes.remove(SitePageType.DOCUMENT_LIBRARY);
        assertEquals(availablePageTypes.size(), expectedPageTypes.size());
        assertEquals(availablePageTypes, expectedPageTypes);
    }
    
    @Test(dependsOnMethods="getAvailablePages", groups="Enterprise-only")
    public void getCurrentPages()
    {
        List<SitePageType> currentPageTypes = customizeSitePage.getCurrentPages();
        assertNotNull(currentPageTypes);
        List<SitePageType> expectedPageTypes = new ArrayList<SitePageType>();
        expectedPageTypes.add(SitePageType.DOCUMENT_LIBRARY);
        assertEquals(currentPageTypes.size(), expectedPageTypes.size());
        assertEquals(currentPageTypes, expectedPageTypes);
    }
    
    @Test(dependsOnMethods = "getCurrentPages", groups = "Enterprise-only")
    public void addPages()
    {
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.WIKI);
        customizeSitePage = FactorySharePage.resolvePage(drone).render();
        siteDashBoard = customizeSitePage.addPages(addPageTypes).render();
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> currentPages = customizeSitePage.getCurrentPages();
        assertTrue(currentPages.contains(SitePageType.WIKI), "Current pages are:" + currentPages);
    }
    
}
