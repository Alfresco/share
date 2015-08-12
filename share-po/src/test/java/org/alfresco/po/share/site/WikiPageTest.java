/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.site.document.TinyMceEditor.FormatType;
import org.alfresco.po.share.site.wiki.WikiPage;

import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author nshah
 *
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class WikiPageTest extends AbstractSiteDashletTest
{

    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    WikiPage wikiPage;
    RssFeedPage rssFeedPage;
    String wikiTitle = "Wiki_Page_1";
    List<String> textLines = new ArrayList<String>();
    List<String> tagsToAdd = new ArrayList<>();
    
    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "wiki" + System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }
    @Test
    public void selectCustomizeDashboard()
    {
        siteDashBoard.getSiteNav().selectConfigure();
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.WIKI);
        siteDashBoard = customizeSitePage.addPages(addPageTypes).render();
        wikiPage = siteDashBoard.getSiteNav().selectWikiPage().render();
        Assert.assertTrue(wikiPage.getTitle().contains("Wiki"));
    }

    @Test(dependsOnMethods="selectCustomizeDashboard")
    public void testWikiPageDisplay()
    {
    	wikiPage.clickOnNewPage();
    	Assert.assertTrue(wikiPage.isWikiPageDisplayed());
    }
    
    @Test(dependsOnMethods="testWikiPageDisplay")
    public void testBulletListOfWikiPage()
    {
        wikiPage.createWikiPageTitle(wikiTitle);
        textLines.add("This is a new Wiki text!");
        wikiPage.insertText(textLines);
        TinyMceEditor tinyMceEditor = wikiPage.getTinyMCEEditor();
        tinyMceEditor.clickTextFormatter(FormatType.BULLET);
        Assert.assertEquals(textLines.get(0), tinyMceEditor.getText());
        Assert.assertTrue(tinyMceEditor.getContent().contains("<li>" + textLines.get(0) + "</li>"));
         wikiPage.clickSaveButton();
    }
    
    @Test(dependsOnMethods = "testBulletListOfWikiPage", enabled = false)
    public void testFontStyle() throws Exception
    {
    	wikiPage.clickFontStyle();
    	Assert.assertEquals(textLines.get(0),wikiPage.retrieveWikiText("FONT"));
    	wikiPage.clickOnRemoveFormatting();
    	Assert.assertEquals(textLines.get(0),wikiPage.retrieveWikiText(""));
    }
    
    @Test(dependsOnMethods = "testFontStyle", enabled = false)
    public void testFontSize() throws Exception
    {
        wikiPage.clickFontSize();
        Assert.assertEquals(textLines.get(0), wikiPage.retrieveWikiText("FONT"));
        wikiPage.clickOnRemoveFormatting();
        Assert.assertEquals(textLines.get(0),wikiPage.retrieveWikiText(""));
        wikiPage.clickSaveButton();
    }

    @Test (dependsOnMethods = "testBulletListOfWikiPage")
    public void testEditWikiPage()
    {
        textLines.add(1, "This is edited Wiki text");
        tagsToAdd.add(0, "tag1");
        wikiPage.editWikiPage(textLines.get(1), tagsToAdd);
        wikiPage.clickDetailsLink();
        Assert.assertTrue(wikiPage.isWikiDetailsCorrect(wikiTitle, textLines.get(1))
            && wikiPage.getTagName().equalsIgnoreCase(tagsToAdd.get(0)), "The wiki hasn't been edited.");
    }

    @Test (dependsOnMethods = "testEditWikiPage")
    public void testViewVersion()
    {
        Double versionToView = 1.0;
        wikiPage.viewVersion(versionToView).render();
        Assert.assertTrue(wikiPage.getWikiText().contentEquals(textLines.get(0)));
        Assert.assertTrue(versionToView.equals(wikiPage.getCurrentWikiVersion()));
    }

    @Test (dependsOnMethods = "testViewVersion", groups="ChromeIssue")
    public void testRssFeedButton()
    {
        rssFeedPage = wikiPage.clickRssFeedBtn(username, password).render();
        Assert.assertNotNull(rssFeedPage);
        wikiPage = (WikiPage)rssFeedPage.clickOnFeedContent(wikiTitle);
        Assert.assertNotNull(wikiPage);
    }

    @Test (dependsOnMethods = "testRssFeedButton", groups="ChromeIssue")
    public void testOpenMainPage()
    {
        boolean isMainPage = wikiPage.openMainPage();
        Assert.assertNotNull(wikiPage);
        Assert.assertTrue(isMainPage, "Main Page isn't opened");
    }
}
