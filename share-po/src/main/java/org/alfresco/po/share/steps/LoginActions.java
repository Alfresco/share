
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
