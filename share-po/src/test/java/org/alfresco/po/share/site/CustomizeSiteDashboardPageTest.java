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
package org.alfresco.po.share.site;

import static org.alfresco.po.share.enums.Dashlets.SITE_MEMBERS;
import static org.alfresco.po.share.enums.Dashlets.SITE_NOTICE;
import static org.testng.Assert.*;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.*;

/**
 * Customize Site Dashboard Page Test.
 *
 * @author Shan Nagarajan
 * @since 1.6.1
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class CustomizeSiteDashboardPageTest extends AbstractSiteDashletTest
{
    private String user;

    SiteNavigation siteNavigation;
    DashBoardPage dashBoard;
    CustomiseSiteDashboardPage customizeSiteDashboardPage;

    @BeforeClass
    public void loadFile() throws Exception
    {
        user = "user-" + System.currentTimeMillis();
        createEnterpriseUser(user);
        shareUtil.loginAs(driver, shareUrl, user, UNAME_PASSWORD).render();
        siteName = "customizeSiteDashboardPage" + System.currentTimeMillis();
        siteUtil.createSite(driver, user, UNAME_PASSWORD, siteName, "description", "Public");
    }

    @BeforeMethod
    public void goToSiteDashboard()
    {
        siteDashBoard = siteUtil.navigateToSiteDashboardByUrl(driver, siteName).render();
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(user, UNAME_PASSWORD, siteName);
    }

    @Test
    public void selectCustomizeDashboard() throws Exception
    {
        assertTrue(resolvePage(driver).render() instanceof CustomiseSiteDashboardPage);
        assertFalse(customizeSiteDashboardPage.isGetStartedPanelDisplayed());
        assertFalse(customizeSiteDashboardPage.isShowOnDashboardDisplayed());
        assertFalse(customizeSiteDashboardPage.isHideFromDashboardDisplayed());
    }

    @Test
    public void checkDashletsColumns()
    {
        assertEquals(customizeSiteDashboardPage.getDashletsCountIn(1), 1);
        assertEquals(customizeSiteDashboardPage.getDashletsCountIn(2), 2);
        assertTrue(customizeSiteDashboardPage.isDashletInColumn(SITE_MEMBERS, 1));
        assertFalse(customizeSiteDashboardPage.isDashletInColumn(SITE_MEMBERS, 2));
    }

    @Test
    public void checkAddRemoveDashletToColumn()
    {
        siteDashBoard = customizeSiteDashboardPage.addDashlet(SITE_NOTICE, 1);
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertTrue(customizeSiteDashboardPage.isDashletInColumn(SITE_NOTICE, 1));
        siteDashBoard = customizeSiteDashboardPage.remove(SITE_NOTICE);
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertFalse(customizeSiteDashboardPage.isDashletInColumn(SITE_NOTICE, 1));
    }

    @Test
    public void checkAddNewLayout()
    {
        customizeSiteDashboardPage.selectChangeLayout();
        assertTrue(resolvePage(driver).render() instanceof CustomiseSiteDashboardPage);
        customizeSiteDashboardPage.selectNewLayout(4);
        assertTrue(resolvePage(driver).render() instanceof CustomiseSiteDashboardPage);
        assertEquals(customizeSiteDashboardPage.getDashletsCountIn(3), 0);
        assertEquals(customizeSiteDashboardPage.getDashletsCountIn(4), 0);
        customizeSiteDashboardPage.selectChangeLayout();
        customizeSiteDashboardPage.selectNewLayout(2);
    }

 /**
    @Test(dependsOnMethods = "checkAddNewLayout")
    public void checkAddAllDashlets()
    {
        siteDashBoard = customizeSiteDashboardPage.addAllDashlets();
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(1) == 5);
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(2) == 5);
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(3) == 5);
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(4) == 1);
    }

    @Test(dependsOnMethods = "checkAddAllDashlets")
    public void selectDashboard() throws Exception
    {
        customizeSiteDashboardPage.selectDashboard(SiteLayout.THREE_COLUMN_WIDE_CENTRE);
        assertTrue(resolvePage(driver).render() instanceof SiteDashboardPage);
    }
**/

}
