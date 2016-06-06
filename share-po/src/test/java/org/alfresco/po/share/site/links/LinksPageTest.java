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
package org.alfresco.po.share.site.links;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests for Links page web elements
 *
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class LinksPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    LinksPage linksPage = null;
    LinksDetailsPage linksDetailsPage = null;
    AddLinkForm addLinkForm = null;
    String text = getClass().getSimpleName();
    String editedText = text + "edited";
    String url = "www.alfresco.com";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "links" + System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(groups = "Enterprise-only")
    public void addLinksPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.LINKS);
        customizeSitePage.addPages(addPageTypes).render();
        linksPage = siteDashBoard.getSiteNav().selectLinksPage().render();
        assertNotNull(linksPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "addLinksPage")
    public void createLink()
    {
        assertTrue(linksPage.isCreateLinkEnabled());
        linksDetailsPage = linksPage.createLink(text, url).render();
        assertEquals(linksDetailsPage.getLinkTitle(), text);
        assertNotNull(linksDetailsPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "createLink")
    public void editLink()
    {
        linksPage = linksDetailsPage.browseToLinksList().render();
        linksPage.editLink(text, editedText, editedText, editedText, true).render();
        assertEquals(linksDetailsPage.getLinkTitle(), editedText);
        assertNotNull(linksDetailsPage);
    }

    @Test(groups = "Enterprise-only", dependsOnMethods = "editLink")
    public void deleteLink()
    {
        linksPage = linksDetailsPage.browseToLinksList().render();
        int expNum = linksPage.getLinksCount()-1;
        linksPage.deleteLinkWithConfirm(editedText).render();
        assertEquals(linksPage.getLinksCount(), expNum);
    }

}
