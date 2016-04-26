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
