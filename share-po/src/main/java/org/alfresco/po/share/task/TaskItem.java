package org.alfresco.po.share.task;

import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.openqa.selenium.WebDriver;
import org.alfresco.po.exception.PageOperationException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Representation of WorkFlow Item on Workflow details page
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public class TaskItem
{
    private String itemName;
    private ShareLink itemNameLink;
    private String description;
    private DateTime dateModified;
    private ShareLink viewMoreActionsLink;

    private static final By ITEM_NAME = By.cssSelector("h3.name");
    private static final By ITEM_NAME_LINK = By.cssSelector("h3.name a");
    private static final By ITEM_DESCRIPTION = By.cssSelector("div.description");
    private static final By ITEM_DATE_MODIFIED = By.cssSelector("div.viewmode-label");
    private static final By VIEW_MORE_ACTIONS_LINK = By.cssSelector("a.view_more_actions");

    public TaskItem(WebElement element, WebDriver driver, boolean isViewMoreActionDisplayed, FactoryPage factoryPage)
    {
        itemName = element.findElement(ITEM_NAME).getText();
        description = element.findElement(ITEM_DESCRIPTION).getText().split("Description: ")[1];
        dateModified = DateTimeFormat.forPattern("E d MMM yyyy HH:mm:ss").parseDateTime(element.findElement(ITEM_DATE_MODIFIED).getText().split("on:")[1].trim());
        itemNameLink = new ShareLink(element.findElement(ITEM_NAME_LINK), driver, factoryPage);

        if (isViewMoreActionDisplayed)
        {
            viewMoreActionsLink = new ShareLink(element.findElement(VIEW_MORE_ACTIONS_LINK), driver, factoryPage);
        }
        else
        {
            viewMoreActionsLink = null;
        }
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

    public ShareLink getItemNameLink()
    {
        return itemNameLink;
    }

    public ShareLink getViewMoreActionsLink()
    {
        if (viewMoreActionsLink != null)
        {
            return viewMoreActionsLink;
        }
        else
        {
            throw new PageOperationException("View More Actions Link is not available on this page");
        }
    }
}
