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

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups = "alfresco-one")
public class EditSitePageTest extends AbstractTest
{
    private String siteName;
    private String siteName2;

    @BeforeClass(groups = "alfresco-one")
    public void setup() throws Exception
    {
        siteName = String.format("test-%d-editSite", System.currentTimeMillis());
        siteName2 = String.format("test-%d-editSite2", System.currentTimeMillis());

        DashBoardPage dashBoard = loginAs(username, password);
        dashBoard.render();
        siteUtil.createSite(driver, username, password, siteName,"", "Public");
        siteUtil.createSite(driver, username, password, siteName2,"", "Public");
        
    }

    @Test(groups="unit", priority = 1)
    public void testPage()
    {
        EditSitePage page = new EditSitePage();
        Assert.assertNotNull(page);
    }

    @Test(groups = "alfresco-one", priority = 2)
    public void testSelectSiteVisibility()
    {
        SiteFinderPage siteFinder = siteUtil.searchSite(driver, siteName);
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName);
        SiteDashboardPage siteDash = siteFinder.selectSite(siteName).render();
        EditSitePage siteDetails = siteDash.getSiteNav().selectEditSite().render();
        Assert.assertTrue(!siteDetails.isPrivate());
        siteDetails.selectSiteVisibility(true, false);
        siteDetails.selectOk();
        siteDetails = siteDash.getSiteNav().selectEditSite().render();
        Assert.assertTrue(siteDetails.isPrivate());
    }
    
    @Test(groups = "alfresco-one", priority = 3)
    public void testDuplicateSiteName()
    {
        EditSitePage siteDetails = factoryPage.getPage(driver).render();
        siteDetails.setSiteName(siteName2);
        Assert.assertTrue(siteDetails.isSiteUsedMessageDisplayed(), "Duplicate Sitename warning is not displayed");
        
        siteDetails.cancel();
    }
}