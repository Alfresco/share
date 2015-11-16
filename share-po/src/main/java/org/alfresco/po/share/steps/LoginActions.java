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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;

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
     * @param driver WebDriver Instance
     * @param userInfo String username, password
     * @return boolean true: if log in succeeds
     */
    public SharePage loginToShare(WebDriver driver, String[] userInfo, String shareUrl)
    {
        LoginPage loginPage;
        SharePage sharePage;
        try
        {
            if ((userInfo.length < 2))
            {
                throw new Exception("Invalid login details");
            }
            checkIfDriverIsNull(driver);
            driver.navigate().to(shareUrl);
            sharePage = getSharePage(driver);
            // Logout if already logged in
            try
            {
                loginPage = sharePage.render();
            }
            catch (ClassCastException e)
            {
                loginPage = logout(driver).render();
            }

            logger.info("Start: Login: " + userInfo[0] + " Password: " + userInfo[1]);

            loginPage.loginAs(userInfo[0], userInfo[1]);
            sharePage = factoryPage.getPage(driver).render();

            if (!sharePage.isLoggedIn())
            {
                throw new ShareException("Method isLoggedIn return false");
            }
        }
        catch (Exception e)
        {
            String errorMessage = "Failed: Login: " + userInfo[0] + " Password: " + userInfo[1];
            logger.info(errorMessage, e);
            throw new ShareException(errorMessage, e);
        }

        return sharePage;
    }

    /**
     * User Log out using logout URL Assumes User is logged in.
     * 
     * @param driver WebDriver Instance
     */
    public HtmlPage logout(WebDriver driver)
    {
        HtmlPage currentPage = null;
        checkIfDriverIsNull(driver);
        try
        {
            SharePage page = factoryPage.getPage(driver).render();
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
