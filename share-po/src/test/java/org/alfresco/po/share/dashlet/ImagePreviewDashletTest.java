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

import java.io.File;

import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Image Preview dashlet web elements
 *
 * @author Marina.Nenadovets
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })

public class ImagePreviewDashletTest extends AbstractSiteDashletTest
{
    private static final String IMAGE_PREVIEW = "image-preview";
    private ImagePreviewDashlet imagePreviewDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private SelectImageFolderBoxPage selectImageFolderBoxPage = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows a thumbnail of each image in the document library. Clicking a thumbnail opens the image in the current window.";
    private static final String IMG_PREVIEW_TITLE = "Image Preview";

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "imagePreviewDashletTest" + System.currentTimeMillis();
        loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        openSiteDocumentLibraryFromSearch(driver, siteName);
        File jpg = siteUtil.prepareJpg(ImagePreviewDashlet.class.getName());
        uploadContent(driver, jpg.getAbsolutePath());
    }

    @Test
    public void instantiateDashlet()
    {
        navigateToSiteDashboard();
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();;
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.IMAGE_PREVIEW, 1).render();
        imagePreviewDashlet = siteDashBoard.getDashlet(IMAGE_PREVIEW).render();
        assertNotNull(imagePreviewDashlet);
    }


    @Test(dependsOnMethods = "instantiateDashlet")
    public void getTitle()
    {
        String actualTitle = imagePreviewDashlet.getTitle();
        assertEquals(actualTitle, IMG_PREVIEW_TITLE);
    }

    @Test(dependsOnMethods = "getTitle")
    public void verifyHelpIcon()
    {
        assertTrue(imagePreviewDashlet.isHelpIconDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyConfigureIcon()
    {
        assertTrue(imagePreviewDashlet.isConfigureIconDisplayed());
    }

    @Test(dependsOnMethods = "verifyConfigureIcon")
    public void selectHelpIcon()
    {
        imagePreviewDashlet.clickOnHelpIcon();
        assertTrue(imagePreviewDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = imagePreviewDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
    }

    @Test(dependsOnMethods = "selectHelpIcon")
    public void closeHelpIcon()
    {
        imagePreviewDashlet.closeHelpBallon();
        assertFalse(imagePreviewDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "closeHelpIcon")
    public void clickConfigureButton()
    {
        selectImageFolderBoxPage = imagePreviewDashlet.clickOnConfigure().render();
        assertNotNull(selectImageFolderBoxPage);
    }

    @Test(dependsOnMethods = "clickConfigureButton", expectedExceptions = PageRenderTimeException.class)
    public void clickCancelConfigure()
    {
        selectImageFolderBoxPage.clickCancel();
        selectImageFolderBoxPage.render();
    }
//
//    @Test(dependsOnMethods = "clickCancelConfigure")
//    public void verifyImageCount() throws Exception
//    {
//        Thread.sleep(20000);
//        driver.navigate().refresh();
//        siteDashBoard = resolvePage(driver).render();
//        imagePreviewDashlet = siteDashBoard.getDashlet(IMAGE_PREVIEW).render();
//        assertEquals(imagePreviewDashlet.getImagesCount(), 1);
//    }
//
//    @Test(dependsOnMethods = "verifyImageCount")
//    public void verifyIsDisplayed()
//    {
//        assertTrue(imagePreviewDashlet.isImageDisplayed(jpgName));
//    }
}
