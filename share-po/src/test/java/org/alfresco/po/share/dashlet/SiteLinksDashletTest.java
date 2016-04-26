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
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.links.LinksDetailsPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Site Links dashlet web elements
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Enterprise-only" })
public class SiteLinksDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_LINKS_DASHLET = "site-links";
    private SiteLinksDashlet siteLinksDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows links relevant to this site. The list is compiled by site members. Clicking a link opens it in a new window.";
    LinksDetailsPage linksDetailsPage = null;

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "siteLinksDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();;
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_LINKS, 1).render();
        siteLinksDashlet = siteDashBoard.getDashlet(SITE_LINKS_DASHLET).render();
        assertNotNull(siteLinksDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon()
    {
        siteLinksDashlet.clickOnHelpIcon();
        assertTrue(siteLinksDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = siteLinksDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        siteLinksDashlet.closeHelpBallon();
        assertFalse(siteLinksDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyLinksCount()
    {
        assertEquals(siteLinksDashlet.getLinksCount(), 0);
    }

    @Test(dependsOnMethods = "verifyLinksCount")
    public void verifyIsLinkDisplayed()
    {
        assertFalse(siteLinksDashlet.isLinkDisplayed("no link azazaza!"));
    }

    @Test(dependsOnMethods = "verifyIsLinkDisplayed")
    public void createLinkFromDashlet()
    {
        linksDetailsPage = siteLinksDashlet.createLink("name", "google.com").render();
        assertNotNull(linksDetailsPage);
    }

    @Test(dependsOnMethods = "createLinkFromDashlet")
    public void secondVerify()
    {
        navigateToSiteDashboard();
        assertEquals(siteLinksDashlet.getLinksCount(), 1);
        assertTrue(siteLinksDashlet.isLinkDisplayed("name"));
    }

}
