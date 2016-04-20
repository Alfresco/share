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
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage.Fields;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify document CRUD is operating correctly.
 *
 * @author Michael Suzuki
 * @author Meenal Bhave
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "alfresco-one" })
public class EditDocumentPropertiesPageTest extends AbstractDocumentTest
{
    private String siteName;
    private String fileName;
    private String title;
    private File file;
    private File file2;
    private String tagName;
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    DocumentDetailsPage detailsPage;
    DocumentLibraryPage docLibPage;
    EditDocumentPropertiesPage editPropertiesPage;

    @AfterClass(groups = { "alfresco-one" })
    public void quit()
    {
        if (site != null)
        {
            siteUtil.deleteSite(username, password, siteName);
        }
        closeWebDriver();
    }

    /**
     * Pre test setup of a dummy file to upload.
     *
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        siteName = "editDocumentSiteTest" + System.currentTimeMillis();
        file = siteUtil.prepareFile();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        fileName = st.nextToken();
        title = "";
        tagName = siteName;

//        File file = siteUtil.prepareFile();
        fileName = file.getName();
        loginAs(username, password);
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
        site = createSite.createNewSite(siteName).render();
        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
        // DocumentLibraryPage docPage =
        // getDocumentLibraryPage(siteName).render();
        UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();

        file2 = siteUtil.prepareFile("EditProps");
        upLoadPage = docPage.getNavigation().selectFileUpload().render();
        docPage = upLoadPage.uploadFile(file2.getCanonicalPath()).render();

        docPage.selectFile(fileName).render();
    }

    @Test
    public void editPropertiesAndCancel() throws Exception
    {
        detailsPage = resolvePage(driver).render();
        EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPage.isEditPropertiesVisible());
        Assert.assertEquals(editPage.getName(), fileName);
        Assert.assertEquals(editPage.getDocumentTitle(), title);
        Assert.assertEquals(editPage.getDescription(), "");
        Assert.assertEquals(editPage.getAuthor(), "");
        Assert.assertFalse(editPage.hasTags());
        detailsPage = editPage.selectCancel().render();
        Assert.assertTrue(detailsPage.isDocumentDetailsPage());
    }

    @Test(dependsOnMethods = "editPropertiesAndCancel")
    public void editPropertiesAndSave() throws Exception
    {
        detailsPage = resolvePage(driver).render();
        EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPage.isEditPropertiesVisible());
        Assert.assertEquals(editPage.getName(), fileName);
        Assert.assertEquals(editPage.getDocumentTitle(), title);
        Assert.assertEquals(editPage.getDescription(), "");
        Assert.assertEquals(editPage.getMimeType(), "Plain Text");
        Assert.assertEquals(editPage.getAuthor(), "");
        Assert.assertFalse(editPage.hasTags());
        editPage.setAuthor("me");
        editPage.setDescription("my description");
        editPage.setDocumentTitle("my title");
        editPage.setName("my.txt");
        editPage.selectMimeType(MimeType.XHTML);
        TagPage tagPage = editPage.getTag().render();
        tagPage = tagPage.enterTagValue(tagName).render();
        EditDocumentPropertiesPage editpage = tagPage.clickOkButton().render();
        detailsPage = editpage.selectSave().render();
        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Author"), "me");
        Assert.assertEquals(properties.get("Description"), "my description");
        Assert.assertEquals(properties.get("Title"), "my title");
        Assert.assertEquals(properties.get("Name"), "my.txt");
        Assert.assertEquals(properties.get("Mimetype"), "XHTML");
        //Check if input fields show correct value
        List<String> tagName = detailsPage.getTagList();
        Assert.assertFalse((tagName.isEmpty()), "tag were not added correctly");
    }

    @Test(dependsOnMethods = "editPropertiesAndSave")
    public void checkInputFieldsHaveUpdatedValues() throws Exception
    {
        detailsPage = resolvePage(driver).render();
        EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
        Assert.assertTrue(editPage.isEditPropertiesVisible());
        Assert.assertEquals(editPage.getName(), "my.txt");
        Assert.assertEquals(editPage.getDocumentTitle(), "my title");
        Assert.assertEquals(editPage.getDescription(), "my description");
        Assert.assertEquals(editPage.getMimeType(), "XHTML");
        Assert.assertEquals(editPage.getAuthor(), "me");
        Assert.assertTrue(editPage.hasTags());

        editPage.clickOnCancel();
    }

    @Test(dependsOnMethods = "checkInputFieldsHaveUpdatedValues")
    public void editPropertiesWithValidationAndSave() throws Exception
    {
        detailsPage = resolvePage(driver).render();
        EditDocumentPropertiesPage editPage = detailsPage.selectEditProperties().render();
        editPage.setName("");
        editPage = editPage.selectSaveWithValidation().render();

        Map<Fields, String> messages = editPage.getMessages();

        Assert.assertEquals(messages.size(), 1);
        Assert.assertFalse(messages.get(EditDocumentPropertiesPage.Fields.NAME).isEmpty());

        editPage.setName("new.txt");
        TagPage tagPage = editPage.getTag().render();
        SharePopup shareErrorPopup = tagPage.enterTagValue("////").render();

        assertEquals(shareErrorPopup.getShareMessage(), "Could not create new item.");

        shareErrorPopup.clickOK().render();

        editPage = tagPage.clickOkButton().render();
        detailsPage = editPage.selectSaveWithValidation().render();

        Map<String, Object> properties = detailsPage.getProperties();
        Assert.assertEquals(properties.get("Name"), "new.txt");
    }

    @Test(dependsOnMethods = "editPropertiesWithValidationAndSave")
    public void editPropertiesOfDublinCoreAspect() throws Exception
    {
        detailsPage = resolvePage(driver).render();
        docLibPage = detailsPage.getSiteNav().selectDocumentLibrary().render();
        detailsPage = docLibPage.selectFile(file2.getName()).render();
        List<DocumentAspect> documentAspects = new ArrayList<DocumentAspect>();
        documentAspects.add(DocumentAspect.DUBLIN_CORE);
        documentAspects.add(DocumentAspect.INDEX_CONTROL);
        SelectAspectsPage selectAspectsPage = detailsPage.selectManageAspects();
        selectAspectsPage.add(documentAspects).render();
        selectAspectsPage.clickApplyChanges().render();
        detailsPage.render();

        editPropertiesPage = detailsPage.selectEditProperties().render();

        Assert.assertTrue(editPropertiesPage.getContributor().isEmpty());
        Assert.assertTrue(editPropertiesPage.getPublisher().isEmpty());
        Assert.assertTrue(editPropertiesPage.getCoverage().isEmpty());
        Assert.assertTrue(editPropertiesPage.getIdentifier().isEmpty());
        Assert.assertTrue(editPropertiesPage.getRights().isEmpty());
        Assert.assertTrue(editPropertiesPage.getSource().isEmpty());
        Assert.assertTrue(editPropertiesPage.getSubject().isEmpty());
        Assert.assertTrue(editPropertiesPage.getType().isEmpty());
    }

    @Test(dependsOnMethods = "editPropertiesOfDublinCoreAspect")
    public void addDublinCoreAspect() throws Exception
    {
        detailsPage = editPropertiesPage.clickCancel().render();
        SelectAspectsPage selectAspectsPage = detailsPage.selectManageAspects().render();

        List<DocumentAspect> aspectsToAdd = new ArrayList<>();
        aspectsToAdd.add(DocumentAspect.DUBLIN_CORE);

        selectAspectsPage = selectAspectsPage.add(aspectsToAdd).render();
        detailsPage = selectAspectsPage.clickApplyChanges().render();

        editPropertiesPage = detailsPage.selectEditProperties().render();

        editPropertiesPage.setContributor("test-contributor");
        editPropertiesPage.setPublisher("test-publisher");
        editPropertiesPage.setCoverage("test-coverage");
        editPropertiesPage.setIdentifier("test-identifier");
        editPropertiesPage.setRights("test-rights");
        editPropertiesPage.setSource("test-source");
        editPropertiesPage.setSubject("test-subject");
        editPropertiesPage.setType("test-type");

        detailsPage = editPropertiesPage.selectSave().render();
    }

    @Test(dependsOnMethods = "addDublinCoreAspect")
    public void verifyProperties() throws Exception
    {
        editPropertiesPage = detailsPage.selectEditProperties().render();

        Assert.assertEquals(editPropertiesPage.getContributor(), "test-contributor");
        Assert.assertEquals(editPropertiesPage.getPublisher(), "test-publisher");
        Assert.assertEquals(editPropertiesPage.getCoverage(), "test-coverage");
        Assert.assertEquals(editPropertiesPage.getIdentifier(), "test-identifier");
        Assert.assertEquals(editPropertiesPage.getRights(), "test-rights");
        Assert.assertEquals(editPropertiesPage.getSource(), "test-source");
        Assert.assertEquals(editPropertiesPage.getSubject(), "test-subject");
        Assert.assertEquals(editPropertiesPage.getType(), "test-type");
    }

    @Test(dependsOnMethods = "verifyProperties")
    public void checkIsTagInputVisible()
    {
        editPropertiesPage = editPropertiesPage.render();
        TagPage tagPage = editPropertiesPage.getTag().waitUntilAlert().render();
        assertTrue(tagPage.isTagInputVisible());
    }

    @Test(dependsOnMethods = "checkIsTagInputVisible")
    public void checkAllTagsCount()
    {
        TagPage tagPage = resolvePage(driver).render();
        assertTrue(tagPage.getAllTagsCount() > 0);
    }

    @Test(dependsOnMethods = "checkAllTagsCount")
    public void checkAddedTagsCount()
    {
        TagPage tagPage = resolvePage(driver).render();
        assertTrue(tagPage.getAddedTagsCount() == 0);
    }

    @Test(dependsOnMethods = "checkAllTagsCount")
    public void checkAllTagsName()
    {
        TagPage tagPage = resolvePage(driver).render();
        assertTrue(tagPage.getAllTagsName().size() > 0);
    }

    @Test(dependsOnMethods = "checkAllTagsName")
    public void checkRefreshTagList()
    {
        TagPage tagPage = resolvePage(driver).render();
        tagPage.refreshTags();
        tagPage.clickCancelButton();
    }
    
    @Test(dependsOnMethods = "checkRefreshTagList")
    public void checkEditCustomFieldsNoPropsToEdit()
    {
        editPropertiesPage = factoryPage.getPage(driver).render();
        Map<String, Object> properties = new HashMap<String, Object>();
        
        // No Pros
        editPropertiesPage.setProperties(properties);
        editPropertiesPage.selectSave().render();
    }
    
    @Test(dependsOnMethods = "checkEditCustomFieldsNoPropsToEdit")
    public void checkEditCustomFieldsEditTextArea()
    {
        
        DetailsPage detailsPage = factoryPage.getPage(driver).render();
        editPropertiesPage = detailsPage.selectEditProperties().render();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("cm_name", "newvalue");
        properties.put("cm_description", "this is new description");
        
        // Text
        editPropertiesPage.setProperties(properties);
        editPropertiesPage.selectSave().render();
    }
    
    @Test(dependsOnMethods = "checkEditCustomFieldsEditTextArea")
    public void checkEditCustomFieldsEditList()
    {
        
        DetailsPage detailsPage = factoryPage.getPage(driver).render();
        editPropertiesPage = detailsPage.selectEditProperties().render();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mimetype", "HTML");
        
        // Text
        editPropertiesPage.setProperties(properties);
        editPropertiesPage.selectSave().render();
    }
    
    @Test(dependsOnMethods = "checkEditCustomFieldsEditList", expectedExceptions=NoSuchElementException.class)
    public void checkEditCustomFieldsEditListInvalidOption()
    {
        
        DetailsPage detailsPage = factoryPage.getPage(driver).render();
        editPropertiesPage = detailsPage.selectEditProperties().render();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("mimetype", "----");
        
        // Text
        editPropertiesPage.setProperties(properties);
    }
    
    @Test(dependsOnMethods = "checkEditCustomFieldsEditListInvalidOption", expectedExceptions= PageException.class)
    public void checkEditCustomFieldsFieldNotFound()
    {
        
        editPropertiesPage = factoryPage.getPage(driver).render();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("cm_notrealfield", "expected exception");
        
        // Text
        editPropertiesPage.setProperties(properties);
    }

}
