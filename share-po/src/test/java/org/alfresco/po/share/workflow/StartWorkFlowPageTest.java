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
package org.alfresco.po.share.workflow;
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
import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify StartWorkflow page load.
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
@Listeners(FailedTestListener.class)
public class StartWorkFlowPageTest extends AbstractTest
{
    private String siteName;
    private File file;
    DocumentLibraryPage documentLibraryPage;

    /**
     * Pre test to create a site and document content with properties set.
     *
     * @throws Exception
     */
    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
    {
        siteName = "StartWorkFlow" + System.currentTimeMillis();
        file = siteUtil.prepareFile();
        loginAs(username, password);
        siteUtil.createSite(driver, username, password, siteName, "", "Public");
        SitePage site = resolvePage(driver).render();
        documentLibraryPage = site.getSiteNav().selectDocumentLibrary().render();
        UploadFilePage upLoadPage = documentLibraryPage.getNavigation().selectFileUpload().render();
        documentLibraryPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
    }

    /**
     * This Test case is to Test StartWorkFlowPage load.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2")
    public void navigateToStartWorkFlow() throws Exception
    {
        DocumentLibraryPage docsPage = resolvePage(driver).render();
        DocumentDetailsPage documentDetailsPage = docsPage.selectFile(file.getName()).render();
        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowPage().render();
        Assert.assertTrue(startWorkFlowPage.isWorkFlowTextPresent());
    }

    /**
     * This Test case is to Test StartWorkFlowPage load.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2", dependsOnMethods = "navigateToStartWorkFlow")
    public void selectStartWorkFlowIcon() throws Exception
    {
        documentLibraryPage = openSiteDocumentLibraryFromSearch(driver, siteName).render();
        DocumentDetailsPage documentDetailsPage = documentLibraryPage.selectFile(file.getName()).render();
        StartWorkFlowPage startWorkFlowPage = documentDetailsPage.selectStartWorkFlowIcon().render();
        Assert.assertTrue(startWorkFlowPage.isWorkFlowTextPresent());
    }

    /**
     * This Test case is to Test isWorkflowTypePresent method returns true with correct data.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectStartWorkFlowIcon")
    public void isWorkflowTypePresentWithValidData() throws Exception
    {
        StartWorkFlowPage startWorkFlowPage = resolvePage(driver).render();
        Assert.assertTrue(startWorkFlowPage.isWorkflowTypePresent(WorkFlowType.NEW_WORKFLOW));
    }

    /**
     * This Test case is to Test isWorkflowTypePresent method throws correct exception if user passes null value.
     *
     * @throws Exception
     */
    @Test(groups = "Enterprise4.2", dependsOnMethods = "isWorkflowTypePresentWithValidData", expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Workflow Type can not be null")
    public void isWorkflowTypePresentWithNullData() throws Exception
    {
            StartWorkFlowPage startWorkFlowPage = resolvePage(driver).render();
            startWorkFlowPage.isWorkflowTypePresent(null);
    }

    /**
     * This test is to check the Start workflow returns new task page .
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "isWorkflowTypePresentWithNullData", groups = "Enterprise4.2")
    public void checkCloudTaskOrReviewPage() throws Exception
    {
        StartWorkFlowPage startWorkFlowPage = resolvePage(driver).render();
        ((NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW)).render();
    }

    /**
     * TearDown method to delete the site
     */
    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, siteName);
    }

}
