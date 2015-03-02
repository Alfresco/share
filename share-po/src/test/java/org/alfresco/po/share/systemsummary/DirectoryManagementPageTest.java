/*
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
package org.alfresco.po.share.systemsummary;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.systemsummary.directorymanagement.AuthType;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryInfoRow;
import org.alfresco.po.share.systemsummary.directorymanagement.DirectoryManagementPage;
import org.alfresco.po.share.systemsummary.directorymanagement.EditLdapFrame;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * @author Aliaksei Boole
 */
@Test(groups = "Enterprise-only")
public class DirectoryManagementPageTest extends AbstractTest
{
    private static final String AUTH_CHAIN_NAME = "testAuth";
    private DirectoryManagementPage directoryManagementPage;
    // Open LDAP
    public static final String LDAP_OPEN_URL = "ldap://172.30.40.61:3268";
    public static final String USER_NAME_FORMAT_OPEN = "%s,CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String ADMIN_USER_NAME_OPEN = "admin";
    public static final String SECURITY_NAME_PRINCIPAL_OPEN = "CN=admin,CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String USER_SEARCH_BASE_OPEN = "CN=Users,DC=qalab,DC=alfresco,DC=org";
    public static final String GROUP_SEARCH_BASE_OPEN = "CN=Users,DC=qalab,DC=alfresco,DC=org";
    // LDAP Query
    public static final String GROUP_QUERY = "(objectclass=group)";
    public static final String USER_QUERY = "(objectclass=user)";
    // Sync options
    public static final String GROUP_TYPE = "group";
    public static final String PERSON_TYPE = "user";
    public static final String MODIFY_TS_ATTR = "whenChanged";
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss'.0Z'";
    public static final String USER_ID_ATTR = "sAMAccountName";
    public static final String USER_ORG_ID = "company";
    public static final String USER_LAST_NAME = "sn";
    public static final String USER_FIRST_NAME = "givenName";
    public static final String GROUP_DISPLAY = "displayName";
    // Security Credentials
    public static final String SECURITY_CREDENTIALS = "alfresco";

    @Test
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) ShareUtil.navigateToSystemSummary(drone, shareUrl, username, password);
        directoryManagementPage = sysSummaryPage.openConsolePage(AdminConsoleLink.DirectoryManagement).render();
        assertNotNull(directoryManagementPage);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnManagementPagePO()
    {
        directoryManagementPage = drone.getCurrentPage().render();
        assertNotNull(directoryManagementPage);
    }

    @Test(dependsOnMethods = "checkDroneReturnManagementPagePO")
    public void checkAddAuthChain()
    {
        directoryManagementPage = drone.getCurrentPage().render();
        directoryManagementPage.addAuthChain(AuthType.OPEN_LDAP, AUTH_CHAIN_NAME);
        assertNotNull(directoryManagementPage.getDirectoryInfoRowBy(AUTH_CHAIN_NAME));
    }

    @Test(dependsOnMethods = "checkAddAuthChain")
    public void checkInfoRow()
    {
        directoryManagementPage = drone.getCurrentPage().render();
        DirectoryInfoRow directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(AUTH_CHAIN_NAME);
        assertEquals(directoryInfoRow.getSyncStatus(), "True");
        assertEquals(directoryInfoRow.getEnabled(), "True");
        assertEquals(directoryInfoRow.getType(), "OpenLDAP");
    }

    @Test(dependsOnMethods = "checkInfoRow")
    public void checkEditAuthChain()
    {
        directoryManagementPage = drone.getCurrentPage().render();
        DirectoryInfoRow directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(AUTH_CHAIN_NAME);
        EditLdapFrame editLdapFrame = directoryInfoRow.clickEdit().render();
        directoryManagementPage = editLdapFrame.clickClose();
    }

    @Test(dependsOnMethods = "checkEditAuthChain")
    public void editAuthChain()
    {
        directoryManagementPage = drone.getCurrentPage().render();
        DirectoryInfoRow directoryInfoRow = directoryManagementPage.getDirectoryInfoRowBy(AUTH_CHAIN_NAME);
        EditLdapFrame editLdapFrame = directoryInfoRow.clickEdit().render();
        editLdapFrame.fillLdapUrl(LDAP_OPEN_URL);
        editLdapFrame.fillAdminUserName(ADMIN_USER_NAME_OPEN);
        editLdapFrame.fillUserNameFormat(USER_NAME_FORMAT_OPEN);
        editLdapFrame.fillSecurityNamePrincipal(SECURITY_NAME_PRINCIPAL_OPEN);
        editLdapFrame.fillUserSearchBase(USER_SEARCH_BASE_OPEN);
        editLdapFrame.fillGroupSearchBase(GROUP_SEARCH_BASE_OPEN);
        editLdapFrame.fillSecurityCredentials(SECURITY_CREDENTIALS);
        editLdapFrame.fillGroupQuery(GROUP_QUERY);
        editLdapFrame.fillPersonQuery(USER_QUERY);
        EditLdapFrame.AdvSettings advSettings = editLdapFrame.openAdvSettings();
        advSettings.fillGroupType(GROUP_TYPE);
        advSettings.fillPersonType(PERSON_TYPE);
        advSettings.fillModifyTS(MODIFY_TS_ATTR);
        advSettings.fillTimeStampFormat(TIMESTAMP_FORMAT);
        advSettings.fillUserIdAttr(USER_ID_ATTR);
        advSettings.fillUserOrganisationId(USER_ORG_ID);
        advSettings.fillUserLastNameAttr(USER_LAST_NAME);
        advSettings.fillUserFirstNameAttr(USER_FIRST_NAME);
        advSettings.fillGroupDisplayNameAttr(GROUP_DISPLAY);
        editLdapFrame.clickSave();
    }

    @Test(dependsOnMethods = "editAuthChain")
    public void runTestSync()
    {
        directoryManagementPage = drone.getCurrentPage().render();
        String testResult = directoryManagementPage.runTestSyncFor(AUTH_CHAIN_NAME);
        assertEquals(testResult, "Test Passed");
    }

    @Test(dependsOnMethods = "runTestSync")
    public void removeAuthChain()
    {
        drone.refresh();
        directoryManagementPage = drone.getCurrentPage().render();
        directoryManagementPage.removeAuthChain(AUTH_CHAIN_NAME);
        try
        {

            directoryManagementPage.getDirectoryInfoRowBy(AUTH_CHAIN_NAME);
            fail("Auth Chain don't deleted.");
        }
        catch (PageOperationException e)
        {
            // testPassed
        }
    }

    @Test(dependsOnMethods = "removeAuthChain")
    public void testSaveAndVerifyIfStatusIsDisplayed()
    {
        drone.refresh();
        directoryManagementPage = drone.getCurrentPage().render();
        directoryManagementPage = directoryManagementPage.clickSave();
        Assert.assertFalse(directoryManagementPage.isSyncStatusDisplayed());
    }

}
