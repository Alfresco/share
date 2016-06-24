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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

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
    DashBoardPage dashBoard;
    CustomiseSiteDashboardPage customizeSiteDashboardPage;

    @BeforeClass
    public void loadFile() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "customizeSiteDashboardPage" + System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test
    public void selectCustomizeDashboard() throws Exception
    {
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertTrue(resolvePage(driver).render() instanceof CustomiseSiteDashboardPage);
        assertFalse(customizeSiteDashboardPage.isGetStartedPanelDisplayed());
        assertFalse(customizeSiteDashboardPage.isShowOnDashboardDisplayed());
        assertFalse(customizeSiteDashboardPage.isHideFromDashboardDisplayed());
    }


    @Test(dependsOnMethods = "selectCustomizeDashboard")
    public void checkDashletCountInColumn()
    {
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(1) == 1);
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(2) == 2);
    }

    @Test(dependsOnMethods = "checkDashletCountInColumn")
    public void checkIsDashletInColumn()
    {
        assertTrue(customizeSiteDashboardPage.isDashletInColumn(SITE_MEMBERS, 1));
        assertFalse(customizeSiteDashboardPage.isDashletInColumn(SITE_MEMBERS, 2));
    }

    @Test(dependsOnMethods = "checkIsDashletInColumn")
    public void checkAddDashletToColumn()
    {
        siteDashBoard = customizeSiteDashboardPage.addDashlet(SITE_NOTICE, 1);
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertTrue(customizeSiteDashboardPage.isDashletInColumn(SITE_NOTICE, 1));
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(1) == 2);
    }

    @Test(dependsOnMethods = "checkAddDashletToColumn")
    public void checkRemoveDashlet()
    {
        siteDashBoard = customizeSiteDashboardPage.remove(SITE_NOTICE);
        customizeSiteDashboardPage = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        assertFalse(customizeSiteDashboardPage.isDashletInColumn(SITE_NOTICE, 1));
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(1) == 1);
    }


    @Test(dependsOnMethods = "checkRemoveDashlet")
    public void selectChangeLayout() throws Exception
    {
        customizeSiteDashboardPage.selectChangeLayout();
        assertTrue(resolvePage(driver).render() instanceof CustomiseSiteDashboardPage);
    }

    @Test(dependsOnMethods = "selectChangeLayout")
    public void checkAddNewLayout()
    {
        customizeSiteDashboardPage.selectNewLayout(4);
        assertTrue(resolvePage(driver).render() instanceof CustomiseSiteDashboardPage);
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(3) == 0);
        assertTrue(customizeSiteDashboardPage.getDashletsCountIn(4) == 0);
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
