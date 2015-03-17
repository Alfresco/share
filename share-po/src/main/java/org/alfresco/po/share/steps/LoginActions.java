/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.po.share.steps;

import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * login and Logout of share
 * 
 * @author sprasanna
 */
public class LoginActions extends CommonActions
{
    private static Log logger = LogFactory.getLog(SiteActions.class);
    public static long refreshDuration = 25000;
    final static String SITE_VISIBILITY_PUBLIC = "public";
    protected static final String SITE_VISIBILITY_PRIVATE = "private";
    protected static final String SITE_VISIBILITY_MODERATED = "moderated";

    protected static final String UNIQUE_TESTDATA_STRING = "sync";

    /**
     * User Log-in followed by deletion of session cookies Assumes User is *NOT* logged in.
     * 
     * @param drone WebDrone Instance
     * @param userInfo String username, password
     * @return boolean true: if log in succeeds
     */
    public SharePage loginToShare(WebDrone drone, String[] userInfo, String shareUrl)
    {
        LoginPage loginPage;
        SharePage sharePage;
        try
        {
            if ((userInfo.length < 2))
            {
                throw new Exception("Invalid login details");
            }

            checkIfDriverIsNull(drone);

            drone.navigateTo(shareUrl);

            sharePage = getSharePage(drone);
            // Logout if already logged in
            try
            {
                loginPage = sharePage.render();
            }
            catch (ClassCastException e)
            {
                loginPage = logout(drone).render();
            }

            logger.info("Start: Login: " + userInfo[0] + " Password: " + userInfo[1]);

            loginPage.loginAs(userInfo[0], userInfo[1]);
            sharePage = drone.getCurrentPage().render();

            if (!sharePage.isLoggedIn())
            {
                throw new ShareException("Method isLoggedIn return false");
            }
        }
        catch (Exception e)
        {
            String errorMessage = "Failed: Login: " + userInfo[0] + " Password: " + userInfo[1] + " Error: " + e;
            logger.info(errorMessage);
            throw new ShareException(errorMessage);
        }

        return sharePage;
    }

    /**
     * User Log out using logout URL Assumes User is logged in.
     * 
     * @param drone WebDrone Instance
     */
    public HtmlPage logout(WebDrone drone)
    {
        HtmlPage currentPage = null;
        checkIfDriverIsNull(drone);
        try
        {
            SharePage page = drone.getCurrentPage().render();
            currentPage = page.getNav().logout().render();
        }
        catch (Exception e)
        {
            // Already logged out.
            logger.info("already logged out" + e.getMessage());
        }
        return currentPage;
    }

}
