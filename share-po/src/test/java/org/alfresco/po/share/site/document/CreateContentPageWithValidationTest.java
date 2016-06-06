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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage.Fields;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Shan Nagarajan, Jamie Alison
 * @since 1.61.
 */
@Listeners(FailedTestListener.class)
public class CreateContentPageWithValidationTest extends AbstractDocumentTest
{
    private String siteName;
    private DocumentLibraryPage documentLibPage;
    DashBoardPage dashBoard;

    /**
     * Pre test setup of a dummy file to upload.
     * 
     * @throws Exception
     */
    @BeforeClass(groups="alfresco-one")
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password).render();
        siteName = "CreateContentPageWithValidationTest" + System.currentTimeMillis();
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
    }

    @AfterClass(groups="alfresco-one")
    public void teardown() throws Exception
    {
        if (siteName != null)
        {
            siteUtil.deleteSite(username, password, siteName);
        }
    }
    
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(groups="Enterprise-only")
    public void createContentWithNullName() throws Exception
    {
        SiteDashboardPage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(null);
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Test Doc");
        contentPage = contentPage.createWithValidation(contentDetails).render();
        assertNotNull(contentPage);
        assertFalse(contentPage.getMessage(Fields.NAME).isEmpty());
        documentLibPage = contentPage.cancel().render();
        assertNotNull(documentLibPage);
    }
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithNullName", groups="Enterprise-only")
    public void createContentWithEmptyName() throws Exception
    {
        SitePage sitePage = resolvePage(driver).render();
        documentLibPage = sitePage.getSiteNav().selectDocumentLibrary().render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Test Doc");
        contentPage = contentPage.createWithValidation(contentDetails).render();
        assertNotNull(contentPage);
        Map<Fields, String> messages = contentPage.getMessages();
        assertFalse(messages.get(Fields.NAME).isEmpty());
        documentLibPage = contentPage.cancel().render();
        assertNotNull(documentLibPage);
    }
    
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithEmptyName", expectedExceptions=UnsupportedOperationException.class, groups="Enterprise-only")
    public void createContentWithNullDetails() throws Exception
    {
        documentLibPage = resolvePage(driver).render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        contentPage.createWithValidation(null);
        contentPage = contentPage.cancel().render();
        assertNotNull(contentPage);
    }
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithNullDetails", groups="Enterprise-only")
    public void createContent() throws Exception
    {
        CreatePlainTextContentPage contentPage = resolvePage(driver).render();
        documentLibPage = contentPage.cancel().render();
        contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
    	ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Test Doc");
        DocumentDetailsPage detailsPage = contentPage.createWithValidation(contentDetails).render();
        assertNotNull(detailsPage);
        
        Map<String, Object> properties = detailsPage.getProperties();
        
        assertEquals(properties.get("Name"), "Test Doc");
        
        EditTextDocumentPage editPage = detailsPage.selectInlineEdit().render();
        contentDetails = editPage.getDetails();
        assertEquals(contentDetails.getName(), "Test Doc");
        contentDetails.setName("");
        InlineEditPage inlineEditPage = editPage.saveWithValidation(contentDetails).render();
        editPage = inlineEditPage.getInlineEditDocumentPage(MimeType.TEXT).render();
        Map<Fields, String> messages = editPage.getMessages();
        assertTrue(messages.size() == 1);
        assertNotNull(messages.get(CreatePlainTextContentPage.Fields.NAME));
        detailsPage = editPage.selectCancel();
        documentLibPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
   }

    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContent", groups="Enterprise-only")
    public void createHtmlContent() throws Exception
    {
        documentLibPage = resolvePage(driver).render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.HTML).render();

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Test Doc");
        contentPage = contentPage.createWithValidation(contentDetails).render();
        assertNotNull(contentPage);
        assertFalse(contentPage.getMessage(CreatePlainTextContentPage.Fields.NAME).isEmpty());

        contentDetails.setName("Test Html");
        DetailsPage detailsPage = contentPage.createWithValidation(contentDetails).render();
        Map<String, Object> properties = detailsPage.getProperties();
        
        assertEquals(properties.get("Name"), contentDetails.getName());
   }
}
