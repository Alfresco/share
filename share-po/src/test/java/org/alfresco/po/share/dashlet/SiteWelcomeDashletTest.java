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
import static org.testng.Assert.assertNotNull;

import org.alfresco.po.share.DashBoardPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test site welcome dashlet page elements.
 * 
 * @author Shan Nagarajan
 * @since  1.6.1
 */
@Deprecated // Welcome dashlet was removed starting with 5.0 / 5.1
@Listeners(FailedTestListener.class)
public class SiteWelcomeDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_WELCOME = "welcome-site";
    DashBoardPage dashBoard;

    @BeforeClass(groups = "alfresco-one")
    public void loadFile() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "SiteWelcomeDashletTests" + System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }
    
    @AfterClass(groups = "alfresco-one")
    public void deleteSite()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    // TEST REMOVED - SEE ACE-3485
    public void instantiateDashlet()
    {
        SiteWelcomeDashlet dashlet = factoryPage.instantiatePage(driver, SiteWelcomeDashlet.class);
        assertNotNull(dashlet);
    }

    /**
     * Test process of accessing my documents dashlet from the dash board view.
     * 
     * @throws Exception
     */
    // TEST REMOVED - SEE ACE-3485
    public void selectSiteWelcometDashlet() throws Exception
    {
        SiteWelcomeDashlet dashlet = siteDashBoard.getDashlet(SITE_WELCOME).render();
        assertEquals(dashlet.getOptions().size(), 4);
    }
    
    /**
     * Test the Remove welcome dashlet button.
     * Try to find the welcome Dashlet after removing it should throw Exception.
     * 
     * @throws Exception
     */
    // TEST REMOVED - SEE ACE-3485
    public void removeAndFindDashlet() throws Exception 
    {
        SiteWelcomeDashlet dashlet;
        dashlet = siteDashBoard.getDashlet(SITE_WELCOME).render();
        dashlet.removeDashlet().render();
        dashlet = siteDashBoard.getDashlet(SITE_WELCOME).render();
    }
    
    /**
     * Test the welcome dashlet in Cloud, which should throw an exception because it is not present.
     * 
     * @throws Exception
     */
    @Test(expectedExceptions = NoSuchDashletExpection.class)
    public void findDashlet() throws Exception 
    {
        siteDashBoard.getDashlet(SITE_WELCOME).render();
    }
}
