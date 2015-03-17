package org.alfresco.po.share.site.datalist;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to hold elements of Data List Directory
 *
 * @author Marina.Nenadovets
 */
public class DataListDirectoryInfo extends HtmlElement
{
    private static final By EDIT_LINK = By.cssSelector("div[id$='default-lists']>ul>li>a>.edit");
    private static final By DELETE_LINK = By.cssSelector("div[id$='default-lists']>ul>li>a>.delete");
    /**
     * Constructor
     */
    protected DataListDirectoryInfo(WebDrone drone, WebElement webElement)
    {
        super(webElement,drone);
    }

    /**
     * Method to click Edit
     *
     * @return NewList Form
     */
    protected NewListForm clickEdit ()
    {
        try
        {
            findAndWait(EDIT_LINK).click();
            return new NewListForm (drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + EDIT_LINK);
        }
    }

    /**
     * Method for clicking Delete button
     *
     * @return DataList page
     */
    protected DataListPage clickDelete ()
    {
        try
        {
            findAndWait(DELETE_LINK).click();
            return new DataListPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }

    protected boolean isEditDisplayed()
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

    protected boolean isDeleteDisplayed()
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
