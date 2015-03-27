package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */
public class WikiPageDirectoryInfo extends HtmlElement
{
    private static final By EDIT_LINK = By.cssSelector(".actionPanel>div.editPage>a");
    private static final By DETAILS_LINK = By.cssSelector(".detailsPage>a");
    private static final By DELETE_LINK = By.cssSelector(".deletePage>a");

    /**
     * Constructor
     */
    protected WikiPageDirectoryInfo(WebDrone drone, WebElement webElement)
    {
        super(webElement, drone);
    }

    public WikiPage clickEdit()
    {
        findAndWait(EDIT_LINK).click();
        return new WikiPage(drone).render();

    }

    /**
     * Method to click Details link
     *
     * @return Wiki page
     */
    public WikiPage clickDetails()
    {
        try
        {
            findAndWait(DETAILS_LINK).click();
            return drone.getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DETAILS_LINK);
        }
    }

    /**
     * Method to click Delete
     *
     * @return Wiki page
     */
    public SharePopup clickDelete()
    {
        try
        {
            findAndWait(DELETE_LINK).click();
            return drone.getCurrentPage().render();

        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }

    /**
     * Method to verify whether edit wiki page is displayed
     *
     * @return boolean
     */
    public boolean isEditLinkPresent()
    {
        try
        {
            WebElement editLink = findElement(EDIT_LINK);
            return editLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to verify whether edit wiki page is displayed
     *
     * @return boolean
     */
    public boolean isDeleteLinkPresent()
    {
        try
        {
            WebElement deleteLink = findElement(DELETE_LINK);
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
}
