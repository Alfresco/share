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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.AbstractTest;

import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SitePage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
@Test(groups="Enterprise-only")
public class ChangeTypePageTest extends AbstractTest
{

    private FolderDetailsPage folderDetailsPage;
    private DocumentLibraryPage documentLibPage;
    private String siteName;
    private ChangeTypePage changeTypePage;

    @BeforeClass
    public void prepare() throws Exception
    {
        siteName = "site" + System.currentTimeMillis();
        String folderName = "The first folder";
        shareUtil.loginAs(driver, shareUrl, username, password).render();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        documentLibPage = ((SitePage) resolvePage(driver).render()).getSiteNav().selectDocumentLibrary().render();
        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder();
        documentLibPage = newFolderPage.createNewFolder(folderName, folderName).render();
        FileDirectoryInfo thisRow = documentLibPage.getFileDirectoryInfo(folderName);
        folderDetailsPage = thisRow.selectViewFolderDetails().render();
        changeTypePage = folderDetailsPage.selectChangeType().render();
    }

    @AfterClass
    public void teardown() throws Throwable
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(priority = 0)
    public void getTypesTest() throws Throwable
    {
        List<String> types = changeTypePage.getTypes();
        assertTrue(types.contains("Select type..."));
    }

    @Test(dependsOnMethods = "getTypesTest")
    public void isChangeTypeDisplayedTest()
    {
        assertTrue(changeTypePage.isChangeTypeDisplayed(), "The dialog should be displyed");
    }

    @Test(dependsOnMethods = "isChangeTypeDisplayedTest")
    public void selectCancelTest() throws Throwable
    {
        folderDetailsPage = changeTypePage.selectCancel().render();
        assertTrue(folderDetailsPage.isBrowserTitle("Folder Details"), "The dialog should be displyed");
    }
}
