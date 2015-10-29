package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * This page is verifies the various
 * links present in the user menu
 * also verifies the rendering
 * 
 * @author hamara
 */
public class UserPage extends SharePage
{
    private static final String CHANGE_PASSWORD_FORM_ID = "form.change.password.id";
    @RenderWebElement
    private static final By USER_PAGE_CSS = By.cssSelector("#HEADER_USER_MENU");
    private static final By MY_PROFILE_CSS = By.cssSelector("td#HEADER_USER_MENU_PROFILE_text");
    private static final By HELP_CSS = By.cssSelector("td#HEADER_USER_MENU_HELP_text");
    private static final By CHANGE_PASSWORD_CSS = By.cssSelector("td#HEADER_USER_MENU_CHANGE_PASSWORD_text");
    private static final By LOGOUT_CSS = By.cssSelector("td#HEADER_USER_MENU_LOGOUT_text");
    private static final By ACCOUNT_SETTINGS = By.cssSelector("td#CLOUD__NetworkAdminToolsLink_text>a.alfresco-navigation-_HtmlAnchorMixin");

    private static final By USER_DASHBOARD_LINK = By.linkText("User Dashboard");
    private static final By USE_CURRENT_PAGE_LINK = By.cssSelector("td#HEADER_USER_MENU_SET_CURRENT_PAGE_AS_HOME_text");
    private static final By USE_MY_DASHBOARD_LINK = By.cssSelector("td#HEADER_USER_MENU_SET_DASHBOARD_AS_HOME_text");

    private Log logger = LogFactory.getLog(this.getClass());

    @SuppressWarnings("unchecked")
    @Override
    public UserPage render(RenderTime timer)
    {
        basicRender(timer);
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * verifies whether form is present.
     * 
     * @return true if form is present , else false.
     */
    public boolean formPresent()
    {
        try
        {
            return findAndWaitById(CHANGE_PASSWORD_FORM_ID).isDisplayed();
        }
        catch (TimeoutException ex)
        {
        }
        return false;
    }

    /**
     * verifies whether MyProfile link is present.
     * 
     * @return true if MyProfile link present , else false.
     */
    public boolean isMyProfileLinkPresent()
    {
        try
        {
            return driver.findElement(MY_PROFILE_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether Help link is present.
     * 
     * @return true if Help link present , else false.
     */
    public boolean isHelpLinkPresent()
    {
        try
        {
            return driver.findElement(HELP_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether change password link is present.
     * 
     * @return true if change password link present , else false.
     */
    public boolean isChangePassWordLinkPresent()
    {
        try
        {
            return driver.findElement(CHANGE_PASSWORD_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether Logout link is present.
     * 
     * @return true if log out option present , else false.
     */
    public boolean isLogoutLinkPresent()
    {
        try
        {
            return driver.findElement(LOGOUT_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether accounts settings link is present.
     * 
     * @return true if log out option present , else false.
     */
    public boolean isAccountSettingsLinkPresent()
    {
        try
        {
            return driver.findElement(ACCOUNT_SETTINGS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether Use Current Page as a home page link is present.
     * 
     * @return true if Use Current Page out option present , else false.
     */
    public boolean isUseCurrentPagePresent()
    {
        try
        {
            return driver.findElement(USE_CURRENT_PAGE_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether Use My Dashboard as a home page link is present.
     * 
     * @return true if Use My Dashboard out option present , else false.
     */
    public boolean isUseMyDashboardPresent()
    {
        try
        {
            return driver.findElement(USE_MY_DASHBOARD_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Mimics the action of selecting my profile link is present.
     * 
     * @return {@link MyProfilePage}
     */
    public HtmlPage selectMyProfile()
    {
        findAndWait(MY_PROFILE_CSS).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting logout link.
     * The page returned from a logout is a LoginPage.
     * 
     * @return {@link LoginPage} page response
     */
    public HtmlPage logout()
    {
        try
        {
            findAndWait(LOGOUT_CSS).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the LogOutLink");
    }

    /**
     * Mimics the action of selecting Account SoSettings link.
     * 
     * @return {AccountSettingsPage}
     */
    public AccountSettingsPage selectAccountSettingsPage()
    {
        try
        {
            findAndWait(ACCOUNT_SETTINGS).click();
            return factoryPage.instantiatePage(driver, AccountSettingsPage.class);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }

        throw new PageOperationException("Not able to find the AccountSettings link");
    }

    /**
     * Mimics the action of selecting my profile link.
     * 
     * @return {@link ChangePasswordPage} change password page object
     */
    public ChangePasswordPage selectChangePassword()
    {
        try
        {
            findAndWait(CHANGE_PASSWORD_CSS).click();
            return getCurrentPage().render();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the ChangePassword link");
    }

    /**
     * Mimics the action of selecting User Dashboard link.
     * 
     * @return {@link HtmlPage} current page po
     */
    public HtmlPage selectUserDashboard()
    {
        try
        {
            findAndWait(USER_DASHBOARD_LINK).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the User Dashboard link");
    }

    /**
     * Mimics the action of selecting Use Current Page link.
     * 
     * @return {@link HtmlPage} current page po
     */
    public HtmlPage selectUseCurrentPage()
    {
        try
        {
            findAndWait(USE_CURRENT_PAGE_LINK).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the Use Current Page link");
    }

    /**
     * Mimics the action of selecting Use My Dashboard link.
     * 
     * @return {@link HtmlPage} user dashboard page po
     */
    public HtmlPage selectUseMyDashboardPage()
    {
        try
        {
            findAndWait(USE_MY_DASHBOARD_LINK).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the Use My Dashboard Page link");
    }

    /**
     * Click Help Link
     */
    public void clickHelp()
    {
        try
        {
            findAndWait(HELP_CSS).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Unable to find Help link.", ex);
            throw new PageException("Unable to find Help link");
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find Help link.", e);
            throw new PageOperationException("Not able to find the Help link", e);
        }
    }

}
