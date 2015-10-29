/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share.site;

import org.alfresco.po.AbstractTest;

import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class Tests the common functionalities in NewFolderPage
 * 
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class NewFolderPageTest extends AbstractTest
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static String siteName;
    private static DocumentLibraryPage documentLibPage;

    /**
     * Pre test setup: Site creation, file upload, folder creation
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("====prepare====");
        }
        
        siteName = "site" + System.currentTimeMillis();


        shareUtil.loginAs(driver, shareUrl, username, password).render();
        
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        
        SitePage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void teardown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

    @Test(groups = { "alfresco-one" })
    public void createNewFolderWithValidation() throws Exception
    {
        String folderName = "New Folder";

        NewFolderPage newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        newFolderPage = newFolderPage.createNewFolderWithValidation("", folderName, folderName).render();
        Assert.assertTrue(newFolderPage.getMessage(NewFolderPage.Fields.NAME).length() > 0);
        documentLibPage = newFolderPage.selectCancel().render();

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolderWithValidation(folderName, folderName, folderName).render();
        Assert.assertNotNull(documentLibPage);

        newFolderPage = documentLibPage.getNavigation().selectCreateNewFolder().render();
        documentLibPage = newFolderPage.createNewFolderWithValidation(folderName + "-1").render();
        Assert.assertNotNull(documentLibPage);
    }
}
