/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Share page object util
 * 
 * @author Michael Suzuki
 */
public class ShareUtil
{
    private static Log logger = LogFactory.getLog(ShareUtil.class);

    private static final String ADMIN_SYSTEMSUMMARY_PAGE = "alfresco/service/enterprise/admin";
    private static final String BULK_IMPORT_PAGE = "alfresco/service/bulkfsimport";
    private static final String BULK_IMPORT_IN_PLACE_PAGE = "alfresco/service/bulkfsimport/inplace";
    private static final String WEB_SCRIPTS_PAGE = "alfresco/service/index";
    private static final String TENANT_ADMIN_CONSOLE_PAGE = "alfresco/s/enterprise/admin/admin-tenantconsole";
    private static final String REPO_ADMIN_CONSOLE_PAGE = "alfresco/s/enterprise/admin/admin-repoconsole";
    private static final String WEBDAV_PAGE = "alfresco/webdav";

    /**
     * A simple Enum to request the required Alfresco version.
     *
     * @author Jamal Kaabi-Mofrad
     */
    public static enum RequiredAlfrescoVersion
    {
        CLOUD_ONLY, ENTERPRISE_ONLY;
    }

    /**
     * Use Logout on header bar and mimics action of logout on share.
     */
    public static synchronized void logout(final WebDrone drone)
    {
        SharePage page = drone.getCurrentPage().render();
        page.getNav().logout();
    }

    /**
     * Logs user into share.
     *
     * @param drone {@link WebDrone}
     * @param url Share url
     * @param userInfo username and password
     * @return {@link HtmlPage} page response
     */
    public static HtmlPage loginAs(final WebDrone drone, final String url, final String... userInfo)
    {
        drone.navigateTo(url);
        LoginPage lp = new LoginPage(drone).render();
        lp.loginAs(userInfo[0], userInfo[1]);
        return drone.getCurrentPage();
    }

    /**
     * Logs user into share from the current page.
     * 
     * @param drone WebDrone
     * @param userInfo String...
     * @return HtmlPage
     */
    public static HtmlPage logInAs(final WebDrone drone, final String... userInfo)
    {
        LoginPage lp = new LoginPage(drone).render();
        lp.loginAs(userInfo[0], userInfo[1]);
        return drone.getCurrentPage();
    }
    
    
    /**
     * A helper method to check the current running Alfresco version against the
     * required version.
     *
     * @param alfrescoVersion the currently running Alfresco version
     * @param requiredVersion the required version (CLOUD_ONLY |
     *            ENTERPRISE_ONLY)
     * @throws UnsupportedOperationException if the {@code requiredVersion} differs from the {@code alfrescoVersion}
     * @throws IllegalArgumentException if {@code requiredVersion} is invalid
     */
    public static void validateAlfrescoVersion(AlfrescoVersion alfrescoVersion, RequiredAlfrescoVersion requiredVersion) throws UnsupportedOperationException,
            IllegalArgumentException
    {
        boolean isCloud = alfrescoVersion.isCloud();
        switch (requiredVersion)
        {
            case CLOUD_ONLY:
                if (!isCloud)
                {
                    throw new UnsupportedOperationException("This operation is Cloud only, not available for Enterprise.");
                }
                break;
            case ENTERPRISE_ONLY:
                if (isCloud)
                {
                    throw new UnsupportedOperationException("This operation is Enterprise only, not available for Cloud.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognised Alfresco version: " + requiredVersion);
        }
    }

    /**
     * @param drone WebDrone
     * @param url String
     * @param userInfo String
     * @return HtmlPage
     */
    public static HtmlPage navigateToSystemSummary(final WebDrone drone, String url, final String... userInfo)
    {
        String protocolVar = PageUtils.getProtocol(url);
        String consoleUrlVar = PageUtils.getAddress(url);
        String systemUrl = String.format("%s%s:%s@%s/" + ADMIN_SYSTEMSUMMARY_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
        try {
            drone.navigateTo(systemUrl);
        } catch (Exception e) {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e + ". Param systemUrl was " + systemUrl);
            }
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Methods for navigation bulk import page
     *
     * @param drone WebDrone
     * @param inPlace boolean
     * @param userInfo String...
     * @return HtmlPage
     */
    public static HtmlPage navigateToBulkImport(final WebDrone drone, boolean inPlace, final String... userInfo)
    {
        String currentUrl = drone.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentUrl);
        String consoleUrlVar = PageUtils.getAddress(currentUrl);
        if (inPlace)
        {
            currentUrl = String.format("%s%s:%s@%s/" + BULK_IMPORT_IN_PLACE_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
            logger.info("Property 'currentUrl' is: " + currentUrl);
        }
        else
        {
            currentUrl = String.format("%s%s:%s@%s/" + BULK_IMPORT_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
            logger.info("Property 'currentUrl' is: " + currentUrl);
        }

        try
        {
            logger.info("Navigate to 'currentUrl': " + currentUrl);
            drone.navigateTo(currentUrl);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e + ". Param systemUrl was " + currentUrl);
            }
        }
        return drone.getCurrentPage().render();
    }
    
    /**
     * Function to create user on Enterprise using UI
     * 
     * @param uname - This should always be unique. So the user of this method needs to verify it is unique. 
     *                eg. - "testUser" + System.currentTimeMillis();
     * @return boolean
     * @throws Exception if error
     */
    public static boolean createEnterpriseUser(final WebDrone drone, final String uname, final String url, final String... userInfo) throws Exception
    {
        AlfrescoVersion alfrescoVersion = drone.getProperties().getVersion();
        if (alfrescoVersion.isCloud() || StringUtils.isEmpty(uname))
        {
            throw new UnsupportedOperationException("This method is not applicable for cloud");
        }       
        try
        {
            System.out.println("User Name: " + uname);
            DashBoardPage dashBoard = loginAs(drone, url, userInfo).render();
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            String userinfo = uname + "@test.com";
            newPage.inputFirstName(userinfo);
            newPage.inputLastName(userinfo);
            newPage.inputEmail(userinfo);
            newPage.inputUsername(uname);
            newPage.inputPassword("password");
            newPage.inputVerifyPassword("password");
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userinfo).render();
            return userCreated.hasResults();
        }
        finally
        {
            logout(drone);
        }
    }

    /**
     * Helper method to extract alfresco webscript url and direct webdrone to location. 
     * @param drone WebDrone
     * @param userInfo String
     * @return HtmlPage
     * @throws Exception
     */
    public static HtmlPage navigateToWebScriptsHome(final WebDrone drone,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(drone, WEB_SCRIPTS_PAGE, userInfo);
    }
    /**
     * Helper method to extract alfresco tenant admin console url and direct webdrone to location. 
     * @param drone WebDrone
     * @param userInfo String
     * @return HtmlPage
     * @throws Exception
     */
    public static HtmlPage navigateToTenantAdminConsole(final WebDrone drone,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(drone, TENANT_ADMIN_CONSOLE_PAGE, userInfo);
    }
    /**
     * Helper method to extract alfresco repository admin console url and direct webdrone to location. 
     * @param drone WebDrone
     * @param userInfo String...
     * @return HtmlPage
     * @throws Exception
     */
    public static HtmlPage navigateToRepositoryAdminConsole(final WebDrone drone,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(drone, REPO_ADMIN_CONSOLE_PAGE, userInfo);
    }

    /**
     * Helper method to extract alfresco repository admin console url and direct webdrone to location.
     * @param drone WebDrone
     * @param userInfo String...
     * @return HtmlPage
     * @throws Exception
     */
    public static HtmlPage navigateToWebDav(final WebDrone drone,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(drone, WEBDAV_PAGE, userInfo);
    }

    /**
     * Base helper method that extracts the url to required alfresco admin location.
     * Once extracted it formats it with the username and password to allow access to the page.
     * @param drone WebDrone
     * @param path String
     * @param userInfo String...
     * @return HtmlPage
     * @throws Exception
     */
    public static HtmlPage navigateToAlfresco(final WebDrone drone, final String path,final String... userInfo) throws Exception
    {
        WebDroneUtil.checkMandotaryParam("WebDrone", drone);
        WebDroneUtil.checkMandotaryParam("Path", path);
        WebDroneUtil.checkMandotaryParam("Username and password", userInfo);
        String currentUrl = drone.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentUrl);
        String consoleUrlVar = PageUtils.getAddress(currentUrl);
        currentUrl = String.format("%s%s:%s@%s/" + path, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
        drone.navigateTo(currentUrl);
        return drone.getCurrentPage().render();
    }
}
