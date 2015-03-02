package org.alfresco.po.share;

import org.alfresco.po.share.user.AccountSettingsPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * This page is verifies the various 
 * links present in the user menu
 * also verifies the rendering
 * @author hamara
 */
public class UserPage extends SharePage
{
    private static final String CHANGE_PASSWORD_FORM_ID = "form.change.password.id";
    @RenderWebElement
    private static final By USER_PAGE_CSS = By.cssSelector("#HEADER_USER_MENU");
    private static final By MY_PROFILE_CSS = By.cssSelector("td#HEADER_USER_MENU_PROFILE_text");
    private static final By STATUS_LINK_CSS = By.cssSelector("td#HEADER_USER_MENU_SET_STATUS_text");
    private static final By HELP_CSS = By.cssSelector("td#HEADER_USER_MENU_HELP_text");
    private static final By CHANGE_PASSWORD_CSS= By.cssSelector("td#HEADER_USER_MENU_CHANGE_PASSWORD_text");
    private static final By LOGOUT_CSS = By.cssSelector("td#HEADER_USER_MENU_LOGOUT_text");
    private static final By ACCOUNT_SETTINGS = By.cssSelector("td#CLOUD__NetworkAdminToolsLink_text>a.alfresco-navigation-_HtmlAnchorMixin");
    private Log logger = LogFactory.getLog(this.getClass());
   
    /**
     * Constructor.      
     * @param drone
     * WebDriver to access page
     */
    public UserPage(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public UserPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * verifies whether form is present.
     * @return true if form is present , else false.
     */
    public boolean formPresent()
    {
        try
        {
            return drone.findAndWaitById(CHANGE_PASSWORD_FORM_ID).isDisplayed();
        }
        catch (TimeoutException ex)
        {
        }
        return false;
    }
    
    /**
     * verifies whether MyProfile link is present.
     * @return true if MyProfile link present , else false.
     */
    public boolean isMyProfileLinkPresent()
    {
        try
        {
            return drone.find(MY_PROFILE_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
    
    /**
     * verifies whether Help link is present.
     * @return true if Help link present , else false.
     */
    public boolean isHelpLinkPresent()
    {
        try
        {
            return drone.find(HELP_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
    
    /**
     * verifies whether status link is present.
     * @return true if status link present , else false.
     */
    public boolean isSetStausLinkPresent()
    {
        try
        {
            return drone.find(STATUS_LINK_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * verifies whether change password link is present.
     * @return true if change password link present , else false.
     */
    public boolean isChangePassWordLinkPresent()
    {
        try
        {
            return drone.find(CHANGE_PASSWORD_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
    /**
     * verifies whether Logout link is present.
     * @return true if log out option present , else false.
     */
    public boolean isLogoutLinkPresent()
    {
        try
        {
            return drone.find(LOGOUT_CSS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
    
    /**
     * verifies whether accounts settings link is present.
     * @return true if log out option present , else false.
     */
    public boolean isAccountSettingsLinkPresent()
    {
        try
        {
            return drone.find(ACCOUNT_SETTINGS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }
    
    /**
     * Mimics the action of selecting my profile link is present.
     * @return {@link MyProfilePage}
     */
    public MyProfilePage selectMyProfile()
    {
        drone.findAndWait(MY_PROFILE_CSS).click();
        return new MyProfilePage(drone);
    }

    /**
     * Mimics the action of selecting logout link.
     * The page returned from a logout is a LoginPage.
     * @return {@link LoginPage} page response
     */
    public LoginPage logout()
    {
        try
        {
            drone.findAndWait(LOGOUT_CSS).click();
            return new LoginPage(drone);
            
        }       
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the LogOutLink");
    }

    /**
     * Mimics the action of selecting Account SoSettings link.
     * @return {AccountSettingsPage}
     */
    public AccountSettingsPage selectAccountSettingsPage()
    {
        if(!alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("This option is in cloud only, not available for Enterprise");
        }
        try
        {
            drone.findAndWait(ACCOUNT_SETTINGS).click();
            return new AccountSettingsPage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }       
       
        throw new PageOperationException("Not able to find the AccountSettings link");
    }

    /**
     * Mimics the action of selecting my profile link.
     * @return {@link ChangePasswordPage} change password page object
     */
    public ChangePasswordPage selectChangePassword()
    {
        try
        {
            drone.findAndWait(CHANGE_PASSWORD_CSS).click();
            return new ChangePasswordPage(drone); 
        }       
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to find the ChangePassword link");
    }
    

}