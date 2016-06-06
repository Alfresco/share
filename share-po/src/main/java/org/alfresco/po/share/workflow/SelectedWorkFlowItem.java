package org.alfresco.po.share.workflow;

import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.openqa.selenium.WebDriver;
import org.alfresco.po.exception.PageOperationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Representation of Selected Workflow Items details
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class SelectedWorkFlowItem
{
    private WebElement itemRow;
    private String itemName;
    private String description;
    private DateTime dateModified;

    private ShareLink itemNameLink;
    private ShareLink viewMoreActions;

    private static final By ITEM_NAME = By.cssSelector("h3.name");
    private static final By ITEM_NAME_LINK = By.cssSelector("h3.name a");
    private static final By ITEM_DESCRIPTION = By.cssSelector("div.description");
    private static final By ITEM_DATE_MODIFIED = By.cssSelector("div.viewmode-label");
    private static final By VIEW_MORE_ACTIONS = By.cssSelector("a[title='View More Actions']");
    private static final By REMOVE_BUTTON = By.cssSelector("a[title='Remove']");

    public SelectedWorkFlowItem(WebElement element, WebDriver driver, FactoryPage factoryPage)
    {
        itemRow = element;
        itemName = itemRow.findElement(ITEM_NAME).getText();
        description = itemRow.findElement(ITEM_DESCRIPTION).getText().split("Description: ")[1];
        dateModified = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(itemRow.findElement(ITEM_DATE_MODIFIED).getText().split("on:")[1].trim());
        itemNameLink = new ShareLink(itemRow.findElement(ITEM_NAME_LINK), driver, factoryPage);
        viewMoreActions = new ShareLink(itemRow.findElement(VIEW_MORE_ACTIONS), driver, factoryPage);
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getDescription()
    {
        return description;
    }

    public DateTime getDateModified()
    {
        return dateModified;
    }

    public void selectRemoveButton()
    {
        try
        {
            itemRow.findElement(REMOVE_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Remove\" Link", nse);
        }
    }

    public boolean isRemoveLinkPresent()
    {
        try
        {
            return itemRow.findElement(REMOVE_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public boolean isViewMoreActionsPresent()
    {
        try
        {
            return itemRow.findElement(VIEW_MORE_ACTIONS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public ShareLink getViewMoreActions()
    {
        return viewMoreActions;
    }

    public ShareLink getItemNameLink()
    {
        return itemNameLink;
    }

}
