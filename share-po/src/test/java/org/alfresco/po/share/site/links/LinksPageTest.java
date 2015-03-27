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
import org.alfresco.po.share.util.SiteUtil;
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
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
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
