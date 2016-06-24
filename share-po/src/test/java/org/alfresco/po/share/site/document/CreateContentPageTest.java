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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document library page is operating correctly.
 * 
 * @author Shan Nagarajan
 * @since 1.61.
 */
@Listeners(FailedTestListener.class)
public class CreateContentPageTest extends AbstractDocumentTest
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
        dashBoard = loginAs(username,password).render();
        siteName = "CreateContentPageTest" + System.currentTimeMillis();
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
    @Test(expectedExceptions=UnsupportedOperationException.class, groups="alfresco-one")
    public void createContentWithNullName() throws Exception
    {
        CreatePlainTextContentPage contentPage = factoryPage.instantiatePage(driver, CreatePlainTextContentPage.class);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(null);
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        contentPage.create(contentDetails);
    }
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithNullName", expectedExceptions=UnsupportedOperationException.class, groups="alfresco-one")
    public void createContentWithEmptyName() throws Exception
    {
        CreatePlainTextContentPage contentPage = factoryPage.instantiatePage(driver, CreatePlainTextContentPage.class);
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        contentPage.create(contentDetails);
    }
    
    /**
     * Test case to create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithEmptyName", expectedExceptions=UnsupportedOperationException.class, groups="alfresco-one")
    public void createContentWithNullDetails() throws Exception
    {
        CreatePlainTextContentPage contentPage = factoryPage.instantiatePage(driver, CreatePlainTextContentPage.class);
        contentPage.create(null);
    }
    /**
     * Test case to create content with plain text.
     * Select manage aspects 
     * Add document aspect 
     * Verify added aspect removed from available aspects column
     * Verify added aspect added in selected aspects column
     * Click Apply changes
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContentWithNullDetails", groups="Enterprise-only")
    public void createContent() throws Exception
    {
        SiteDashboardPage page = resolvePage(driver).render();
        documentLibPage = page.getSiteNav().selectDocumentLibrary().render();
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc");
        contentDetails.setTitle("Test");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        assertNotNull(detailsPage);
        assertFalse(detailsPage.isViewOnGoogleMapsLinkVisible());
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(DocumentAspect.VERSIONABLE);
        aspects.add(DocumentAspect.CLASSIFIABLE);
        aspects.add(DocumentAspect.GEOGRAPHIC);
        aspectsPage = aspectsPage.add(aspects).render();
        assertFalse(aspectsPage.getAvailableSystemAspects().contains(DocumentAspect.VERSIONABLE));
        assertTrue(aspectsPage.getSelectedSystemAspects().contains(DocumentAspect.VERSIONABLE));
        assertTrue(aspectsPage.getSelectedSystemAspects().contains(DocumentAspect.GEOGRAPHIC));
        detailsPage = aspectsPage.clickApplyChanges().render();
        assertTrue(detailsPage.isViewOnGoogleMapsLinkVisible());
        EditTextDocumentPage editPage = detailsPage.selectInlineEdit();
        editPage.render();
        contentDetails = editPage.getDetails();
        assertEquals(contentDetails.getContent(), "Shan Test Doc");
        contentDetails.setContent("123456789");
        editPage.save(contentDetails).render();
        documentLibPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
   }
    
    /**
     * Test case to cancel create content with plain text. Select manage aspects
     * Add document aspect Verify added aspect removed from available aspects
     * column Verify added aspect added in selected aspects column Click cancel
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "createContent", groups = "Enterprise-only")
    public void createCancelContent() throws Exception
    {
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation()
                .selectCreateContent(ContentType.PLAINTEXT).render();
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("Test Doc Cancel");
        contentDetails.setTitle("Test Cancel");
        contentDetails.setDescription("Desc");
        contentDetails.setContent("Shan Test Doc");
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();
        assertNotNull(detailsPage);
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = new ArrayList<DocumentAspect>();
        aspects.add(DocumentAspect.VERSIONABLE);
        aspects.add(DocumentAspect.CLASSIFIABLE);
        aspectsPage = aspectsPage.add(aspects).render();
        assertFalse(aspectsPage.getAvailableSystemAspects().contains(DocumentAspect.VERSIONABLE));
        assertTrue(aspectsPage.getSelectedSystemAspects().contains(DocumentAspect.VERSIONABLE));
        detailsPage = aspectsPage.clickCancel().render();
        documentLibPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
    }

    /**
     * Test case to cancel create content with plain text.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods="createCancelContent", groups="Enterprise-only")
    public void cancelCreateContent() throws Exception
    {
        CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(ContentType.PLAINTEXT).render();
    	ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName("cancel create content");
        contentDetails.setTitle("cancel create content title");
        contentDetails.setDescription("cancel create content description");
        contentDetails.setContent("cancel create content - test content");
        DocumentLibraryPage documentLibraryPage = contentPage.cancel(contentDetails).render();
        assertNotNull(documentLibraryPage);
        assertFalse(documentLibraryPage.isItemVisble("cancel create content"));
    }
    
}
