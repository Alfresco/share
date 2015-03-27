/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.site.document.ManagePermissionsPage.UserSearchPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
/** Once it is fixed can we remove the group called BuildBrokenBug and comment. 
 * (JIRA Issue: WEBDRONE-284) 
 *  Add group names back. 
 **/
@Test(groups = { "Enterprise4.2", "Cloud2", "BambooBug", "AutomationBug"})
public class ManagePermissionsTest extends AbstractTest
{
    private static String FNAME = "Administrator";
    private String siteName;
    private File sampleFile;
    private ManagePermissionsPage pageUnderTest;
    private UserSearchPage pageReturned;
    @BeforeClass
    public void beforeClass() throws IOException
    {
        if(alfrescoVersion.isCloud()){
            FNAME = "Auto";
        }
        siteName = "site" + System.currentTimeMillis();

        ShareUtil.loginAs(drone, shareUrl, username, password).render();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        SitePage sitePage = drone.getCurrentPage().render();

        DocumentLibraryPage documentLibPage = sitePage.getSiteNav().selectSiteDocumentLibrary().render();
        sampleFile = SiteUtil.prepareFile();
        UploadFilePage upLoadPage = documentLibPage.getNavigation().selectFileUpload().render();
        documentLibPage = upLoadPage.uploadFile(sampleFile.getCanonicalPath()).render();
        DocumentDetailsPage docDetailPage = documentLibPage.selectFile(sampleFile.getName()).render();
        pageUnderTest = docDetailPage.selectManagePermissions().render();

    }

    @AfterClass
    public void afterClass()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void toggleInheritPermissionTest()
    {
        pageUnderTest = pageUnderTest.toggleInheritPermission(false, ButtonType.Yes).render();
        assertTrue(!pageUnderTest.isInheritPermissionEnabled(), "The Inherit permissio table should not be displayed.");

        pageUnderTest = pageUnderTest.toggleInheritPermission(true, ButtonType.No).render();
        assertTrue(pageUnderTest.isInheritPermissionEnabled(), "The Inherit permissio table should be displayed.");
        
    }

    @Test(dependsOnMethods = "toggleInheritPermissionTest")
    public void selectAddUserTest()
    {
        pageReturned = pageUnderTest.selectAddUser().render();
        assertTrue(pageReturned instanceof UserSearchPage);
    }
    
    @Test(dependsOnMethods = "selectAddUserTest")
    public void searchUserTest()
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        UserSearchPage searchPage = pageReturned;
        pageUnderTest = searchPage.searchAndSelectUser(userProfile).render();
        assertTrue(pageUnderTest.isDirectPermissionForUser(userProfile), "User did not get added to 'Locally Set Permissions' table as user");
    }
    
    @Test(dependsOnMethods="searchUserTest", expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Name cannot be null")
    public void setAccessTypeWithNullName()
    {
        String name = null;
        pageUnderTest.setAccessType(name, UserRole.COLLABORATOR);
        pageUnderTest.setAccessType(name, UserRole.CONSUMER);
    }

    @Test(dependsOnMethods="setAccessTypeWithNullName", expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="UserProfile cannot be null")
    public void setAccessTypeWithNullProfile()
    {
        UserProfile name = null;
        pageUnderTest.setAccessType(name, UserRole.COLLABORATOR);
        pageUnderTest.setAccessType(name, UserRole.CONSUMER);
    }

    @Test(dependsOnMethods="setAccessTypeWithNullProfile", expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Access type cannot be null")
    public void setAccessTypeWithNullRole()
    {
        String name = FNAME;
        pageUnderTest.setAccessType(name, null);
    }
    
    @Test(dependsOnMethods = "setAccessTypeWithNullRole")
    public void setAccessTypeTest()
    {
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
        userProfile.setfName(FNAME);
        userProfile.setlName("");
        UserRole userRole = UserRole.COLLABORATOR;
        String userFullName = FNAME;
        
        //Test ManagePermissionsPage.setAccessType(UserProfile, UserRole)
        pageUnderTest.setAccessType(userFullName, userRole);
        DocumentDetailsPage pageReturned = (DocumentDetailsPage) pageUnderTest.selectSave();
        pageReturned.render();
        assertTrue(pageReturned instanceof DocumentDetailsPage);
        pageUnderTest = pageReturned.selectManagePermissions().render();
        UserRole role = pageUnderTest.getAccessType();
        assertTrue(UserRole.COLLABORATOR.equals(role),
                "Access type should have been '" + userRole + "' but was - " + pageUnderTest.getAccessType());
        pageReturned = (DocumentDetailsPage) pageUnderTest.selectCancel();
        pageReturned.render();
        assertTrue(pageReturned instanceof DocumentDetailsPage);
        
        //Test ManagePermissionsPage.setAccessType(String, UserRole)
        pageUnderTest = pageReturned.selectManagePermissions().render();
        pageUnderTest.setAccessType(userProfile, userRole);
        pageReturned = (DocumentDetailsPage) pageUnderTest.selectSave();
        pageReturned.render();
        assertTrue(pageReturned instanceof DocumentDetailsPage);
        pageUnderTest = pageReturned.selectManagePermissions().render();
        role = pageUnderTest.getAccessType();
        assertTrue(UserRole.COLLABORATOR.equals(role),
                "Access type should have been '" + userRole + "' but was - " + pageUnderTest.getAccessType());
        pageReturned = (DocumentDetailsPage) pageUnderTest.selectCancel();
        pageReturned.render();
        assertTrue(pageReturned instanceof DocumentDetailsPage);
    }
    
    @Test(dependsOnMethods = "setAccessTypeTest")
    public void updateRoleTest()
    {
        pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();
        Assert.assertFalse(pageUnderTest.isUserExistForPermission(username));       
        assertTrue(pageUnderTest.isUserExistForPermission(FNAME));
        
        List<String> roles = pageUnderTest.getListOfUserRoles(FNAME);
        //[Editor, Consumer, Collaborator, Coordinator, Contributor, Site Consumer, Site Contributor, Site Manager, Site Collaborator]
        Assert.assertTrue(roles.contains(UserRole.EDITOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.CONSUMER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.COORDINATOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.CONTRIBUTOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECONSUMER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECONTRIBUTOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITEMANAGER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECOLLABORATOR.getRoleName()));
        
        assertTrue(pageUnderTest.updateUserRole(FNAME, UserRole.CONSUMER));        
        ((DocumentDetailsPage) pageUnderTest.selectCancel()).render();        
    }
    
    
    @Test(dependsOnMethods = "updateRoleTest")
    public void getInheritedPermissions()
    {
        pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();       
        String role = pageUnderTest.getInheritedPermissions().get("site_"+siteName.toLowerCase()+"_"+ StringUtils.replace(UserRole.SITEMANAGER.getRoleName().trim(), " ", ""));
        Assert.assertEquals(role, UserRole.SITEMANAGER.getRoleName());
    }
    @Test(dependsOnMethods = "getInheritedPermissions")
    public void getExistingPermissionTest()
    {
        String userName = "EVERYONE";
        AlfrescoVersion version = drone.getProperties().getVersion();
        if(version.isCloud())
        {
            userName = "INTERNAL_USERS";
        }
        UserRole role = pageUnderTest.getExistingPermissionForInheritPermission(userName);
        Assert.assertEquals(role, UserRole.SITECONSUMER);
    }
    
    @Test(dependsOnMethods = "getExistingPermissionTest")
    public void deletePermissionTest()
    {
        String userName = "Administrator";
        AlfrescoVersion version = drone.getProperties().getVersion();
        if(version.isCloud())
        {
            userName = "Auto Account";
        }
        if(drone.getCurrentPage() instanceof ManagePermissionsPage)
        {
            
        }
        else
        {
            pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();
        }
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(username);
     
        pageUnderTest = pageUnderTest.selectAddUser().searchAndSelectUser(userProfile).render();
        
        Assert.assertTrue(pageUnderTest.isDeleteActionPresent(userName, UserRole.SITEMANAGER));
        pageUnderTest = pageUnderTest.deleteUserWithPermission(userName, UserRole.SITEMANAGER);
        pageUnderTest.selectCancel().render();
    }
    
    @Test(dependsOnMethods = "getExistingPermissionTest")
    public void deleteExistingPermissionTest()
    {
        String userName = "Administrator";
        AlfrescoVersion version = drone.getProperties().getVersion();
        if(version.isCloud())
        {
            userName = "Auto Account";
        }
        Assert.assertTrue(pageUnderTest.deleteUserOrGroupFromPermission(userName, UserRole.COLLABORATOR));
    }
    
    @Test(dependsOnMethods = "deleteExistingPermissionTest")
    public void isEveryOnePresent()
    {
        
        pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();    
        ManagePermissionsPage.UserSearchPage userSearchPage = pageUnderTest.selectAddUser().render();
        Assert.assertTrue(userSearchPage.isEveryOneDisplayed("<>?:\"|}{+_)(*&^%$#@!~;"));
        pageUnderTest.selectCancel();
    }
    
    @Test(dependsOnMethods = "isEveryOnePresent")
    public void getSearchErrorMessage()
    {
        pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();    
        ManagePermissionsPage.UserSearchPage userSearchPage = pageUnderTest.selectAddUser().render();
        Assert.assertEquals(userSearchPage.getSearchErrorMessage(""), "Enter at least 3 character(s)");
        pageUnderTest.selectCancel();
    }
    
    @Test(dependsOnMethods = "getSearchErrorMessage")
    public void usersExistInSearchResults()
    {
        pageUnderTest = ((DocumentDetailsPage)drone.getCurrentPage()).selectManagePermissions().render();    
        ManagePermissionsPage.UserSearchPage userSearchPage = pageUnderTest.selectAddUser().render();
        Assert.assertTrue(userSearchPage.usersExistInSearchResults(username, username));
        pageUnderTest.selectCancel();
    }
}