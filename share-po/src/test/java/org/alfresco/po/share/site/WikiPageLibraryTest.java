package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPage.Mode;
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
@Test(groups="Enterprise-only")
public class WikiPageLibraryTest extends AbstractTest
{

    DashBoardPage dashBoard;  
    WikiPage wikiPage;
    String siteName = "sample";
    String siteFullName = "Sample: Web Site Design Project";
    @BeforeClass
    public void searchSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        driver.navigate().to(shareUrl + "/page/site/swsdp/wiki-page?title=Main_Page");
        wikiPage = resolvePage(driver).render();
    }
    
    @Test
    public void testImageRenderingFromImageLibrary() throws Exception
    {
        List<String> txtLines = new ArrayList<String>();
        txtLines.add("Wiki Text line");
        wikiPage.clickOnNewPage();
        wikiPage.createWikiPageTitle("Wiki Page Image insertion test!!!!");
        wikiPage.insertText(txtLines);
        wikiPage.isImageLibraryDisplayed();
        wikiPage.clickImageOfLibrary();
        Assert.assertEquals(1, wikiPage.imageCount(Mode.ADD));
        wikiPage.clickSaveButton();
        
    }
    
    @AfterClass
    public void  deleteWikiPageAndLogOut()
    {
        wikiPage.deleteWiki();
        logout(driver);
    }
}
