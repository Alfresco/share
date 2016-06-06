package org.alfresco.po.share.user;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * My profile page object, holds all element of the html page relating to
 * share's my profile page.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class MyProfilePage extends SharePage
{

    private final By editProfileButton = By.cssSelector("button[id$='-button-edit-button'], button[id$='-button-following-button']");
    private final By userName = By.cssSelector(".namelabel");
    private final By emailName = By.cssSelector(".fieldvalue");
    private static final By TRASHCAN_LINK = By.cssSelector("div>a[href='user-trashcan']");
    private static final By FOLLOWING_LINK = By.cssSelector("div>a[href='following']");

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(editProfileButton));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyProfilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    /**
     * Verify if home page banner web element is present
     *
     * @return true if exists
     */
    public boolean titlePresent()
    {
        boolean isPresent = false;
        String title = getPageTitle();
        isPresent = title.contains("Profile");
        return isPresent;
    }

    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(driver, factoryPage);
    }

    /**
     * Method to get element text for Username
     *
     * @return String
     */
    public String getUserName()
    {
        return getElementText(userName);
    }

    /**
     * Method to get element text for email.
     *
     * @return String
     */
    public String getEmailName()
    {
        return getElementText(emailName);
    }

    /**
     * Check that Trashcan link is displayed on page.
     *
     * @return true - if link displayed.
     */

    public boolean isTrashcanLinkDisplayed()
    {
        try
        {
            WebElement trashcanLink = findAndWait(TRASHCAN_LINK);
            return trashcanLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Check that Following link is displayed on page.
     *
     * @return true - if link displayed.
     */

    public boolean isFollowingLinkDisplayed()
    {
        try
        {
            WebElement followingLink = findAndWait(FOLLOWING_LINK);
            return followingLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    public HtmlPage openEditProfilePage()
    {
        findAndWait(editProfileButton).click();
        return factoryPage.instantiatePage(driver, EditProfilePage.class);
    }
}
