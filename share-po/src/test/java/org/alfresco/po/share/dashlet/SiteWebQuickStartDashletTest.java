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

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.SitePage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test web quick start dashlet page elements.
 * 
 * @author Cristina Axinte
 * 
 */
@Test(groups = { "check", "alfresco-one" })
@Listeners(FailedTestListener.class)
public class SiteWebQuickStartDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private SiteWebQuickStartDashlet wqsDashlet = null;

    @BeforeClass
    public void setUp() throws Exception
    {
        loginAs("admin", "admin");

    }

    @AfterClass
    public void deleteSite()
    {
        // siteUtil.deleteSite(username, password, siteName);
    }
    
    @BeforeMethod
    public void siteAndWQSDashletSetup()
    {
         siteName= this.getClass().getSimpleName()+ System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
         SitePage site = resolvePage(driver).render();
         customiseSiteDashBoard = site.getSiteNav().selectCustomizeDashboard().render();
         siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.WEB_QUICK_START, 1).render();
         wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
    }

    @Test
    public void instantiateDashlet()
    {
        SiteWebQuickStartDashlet wqsDashlet = factoryPage.instantiatePage(driver, SiteWebQuickStartDashlet.class);
        Assert.assertNotNull(wqsDashlet);
    }
    
    @Test
    public void clickImportButttonPositive()
    {
        resolvePage(driver).render();
        wqsDashlet.clickImportButtton();
        assertNotNull(wqsDashlet);

    }

    @Test
    public void verifyIsWQSHelpLinkDisplayedPositive()
    {
        SitePage site = resolvePage(driver).render();
        wqsDashlet.clickImportButtton();
        site = resolvePage(driver).render();
        site.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(wqsDashlet.isWQSHelpLinkDisplayed());
    }

    @Test
    public void getSelectedWebsiteDataPositive()
    {
        resolvePage(driver).render();
        assertEquals(wqsDashlet.getSelectedWebsiteData(), "Finance");
    }

    @Test
    public void selectWebsiteDataOptionPositive() throws Exception
    {
        resolvePage(driver).render();
        wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.GOVERNMENT);
        wqsDashlet = siteDashBoard.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
        assertEquals(wqsDashlet.getSelectedWebsiteData(), "Government");
    }

    @Test
    public void verifyIsWQSHelpLinkDisplayedNegative()
    {
        resolvePage(driver).render();
        assertFalse(wqsDashlet.isWQSHelpLinkDisplayed());
    }

}
