/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.steps;

/**
 * Test Class to test SiteActions > utils
 * 
 * @author mbhave
 */

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SiteActionsTest extends AbstractTest
{
    private SiteActions siteActions = new SiteActions();
    private String  siteName = "swsdp";
    private String newSite = "site" + System.currentTimeMillis();

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs(username, password);
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testopenSiteDashBoard() throws Exception
    {
            SiteDashboardPage siteDashPage = siteActions.openSiteDashboard(drone, siteName);
            Assert.assertNotNull(siteDashPage);
    }
    
    @Test(groups = "Enterprise-only", priority=2)
    public void testopenSitesContentLibrary() throws Exception
    {
            DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(drone, siteName);
            Assert.assertNotNull(docLibPage);
    }
    
    @Test(groups = "Enterprise-only", priority=3)
    public void testCreateSite() throws Exception
    {
            siteActions.createSite(drone, newSite, newSite, "Public");
            DocumentLibraryPage docLibPage = siteActions.openSitesDocumentLibrary(drone, newSite);
            Assert.assertNotNull(docLibPage);
    }
}
