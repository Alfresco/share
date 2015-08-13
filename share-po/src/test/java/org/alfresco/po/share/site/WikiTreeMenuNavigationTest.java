package org.alfresco.po.share.site;

import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.PagesMenu.ALL;
import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.PagesMenu.MY_PAGES;
import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.PagesMenu.RECENTLY_ADDED;
import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.PagesMenu.RECENTLY_MODIFIED;
import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.TreeMenu.PAGES;
import static org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation.TreeMenu.TAGS;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.site.wiki.WikiTreeMenuNavigation;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests to verify Wiki Page list left hand tree menu navigation
 * is working correctly
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class WikiTreeMenuNavigationTest extends AbstractSiteDashletTest
{
    private String tagName1;
    private String tagName2;
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    WikiPageList wikiPageList;
    String wikiTitle1 = "Wiki_Page_1";
    String wikiTitle2 = "Wiki_Page_2";
    List<String> textLines = new ArrayList<>();
    List<String> tagsToAdd = new ArrayList<>();

    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "wikiTree" + System.currentTimeMillis();
        tagName1 = "tag1";
        tagName2 = "tag2";
        textLines.add("WikiTreeMenuNavigationTest");
        tagsToAdd.add(tagName1);

        loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<>();
        addPageTypes.add(SitePageType.WIKI);
        siteDashBoard = customizeSitePage.addPages(addPageTypes).render();
        WikiPage wikiPage = siteDashBoard.getSiteNav().selectWikiPage().render();
        wikiPage.createWikiPage(wikiTitle1, textLines, tagsToAdd).render();
        tagsToAdd.clear();
        tagsToAdd.add(tagName2);
        wikiPage.createWikiPage(wikiTitle2, textLines, tagsToAdd).render();
        wikiPageList = wikiPage.clickWikiPageListBtn().render();
    }

    @Test
    public void isMenuTreeVisible()
    {
        WikiTreeMenuNavigation treeMenuNav = wikiPageList.getLeftMenus();

        assertTrue(treeMenuNav.isMenuTreeVisible(PAGES));
        assertTrue(treeMenuNav.isMenuTreeVisible(TAGS));
    }

    @Test(dependsOnMethods = "isMenuTreeVisible")
    public void selectPageNode()
    {
        WikiTreeMenuNavigation treeMenuNav = wikiPageList.getLeftMenus();

        treeMenuNav.selectPageNode(ALL);
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle1));

        treeMenuNav.selectPageNode(RECENTLY_ADDED);
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle1));

        treeMenuNav.selectPageNode(RECENTLY_MODIFIED);
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle1));

        treeMenuNav.selectPageNode(MY_PAGES);
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle1));
    }

    @Test(dependsOnMethods = "selectPageNode")
    public void selectTagNode()
    {
        WikiTreeMenuNavigation treeMenuNav = wikiPageList.getLeftMenus();
        treeMenuNav.selectPageNode(ALL).render();

        treeMenuNav.selectTagNode(tagName1).render();
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle1));

        treeMenuNav.selectTagNode(tagName2).render();
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle2));

    }

    @Test(dependsOnMethods = "selectTagNode")
    public void showAllItems()
    {
        WikiTreeMenuNavigation treeMenuNav = wikiPageList.getLeftMenus();

        treeMenuNav.selectShowAllItems().render();
        assertTrue(wikiPageList.isWikiPagePresent(wikiTitle2) && wikiPageList.isWikiPagePresent(wikiTitle1));
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
}
