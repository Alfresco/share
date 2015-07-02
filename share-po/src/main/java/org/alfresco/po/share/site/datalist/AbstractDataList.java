package org.alfresco.po.share.site.datalist;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * An abstract of Data List
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractDataList extends SharePage
{
    private static final Log logger = LogFactory.getLog(AbstractDataList.class);

    protected static final By NEW_ITEM_LINK = By.cssSelector("button[id$='newRowButton-button']");
    protected static final By LIST_TABLE = By.cssSelector("table");
    protected static final By EDIT_LINK = By.cssSelector(".onActionEdit>a");
    protected static final By DUPLICATE_LINK = By.cssSelector(".onActionDuplicate>a");
    protected static final By DELETE_LINK = By.cssSelector(".onActionDelete>a");
    private static final By CONFIRM_DELETE = By.xpath("//span[@class='button-group']/span[1]/span/button");
    @SuppressWarnings("unused")
    private static final By CANCEL_DELETE = By.xpath("//span[@class='button-group']/span[2]/span/button");
    private static final By ITEM_CONTAINER = By.cssSelector("table>tbody>tr[class*='dt']");
    private static final By CHECKBOX = By.cssSelector("input[type='checkbox']");

    protected AbstractDataList(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractDataList render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractDataList render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractDataList render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to locate an item in the table
     *
     * @param fieldValue String
     */
    protected WebElement locateItemActions(String fieldValue)
    {

        if (!(fieldValue == null))
        {
            try
            {
                WebElement theItem = drone.findAndWait(By.xpath(String.format("//td//div[text()='%s']/../../td[contains(@class, 'actions')]", fieldValue)));
                drone.mouseOver(theItem);
                return theItem;
            }
            catch (NoSuchElementException nse)
            {
                throw new ShareException("Unable to locate " + fieldValue + "item");
            }
        }
        else
        {
            throw new UnsupportedOperationException("Field value parameter cannot be empty");
        }
    }

    /**
     * Method to duplicate an item
     *
     * @param fieldValue String
     */
    public void duplicateAnItem(String fieldValue)
    {
        try
        {
            WebElement theItem = locateItemActions(fieldValue);
            theItem.findElement(DUPLICATE_LINK).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("unable to find the " + DUPLICATE_LINK);
        }
        logger.info("Duplicated the " + fieldValue + " item");
    }

    private ShareDialogue clickDelete(String fieldValue)
    {
        try
        {
            WebElement theItem = locateItemActions(fieldValue);
            theItem.findElement(DELETE_LINK).click();
            return new ShareDialogue(drone).render();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }

    public void confirmDelete()
    {
        try
        {
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert(5);
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find" + CONFIRM_DELETE);
        }
    }

    /**
     * Method to delete an item
     *
     * @param fieldValue String
     */
    public void deleteAnItemWithConfirm(String fieldValue)
    {
        clickDelete(fieldValue);
        confirmDelete();
        logger.info("Deleted the " + fieldValue + " item");
    }

    /**
     * Method to click edit for item
     *
     * @param fieldValue String
     */
    public void clickEditItem(String fieldValue)
    {
        try
        {
            WebElement theItem = locateItemActions(fieldValue);
            theItem.findElement(EDIT_LINK).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + EDIT_LINK);
        }
    }

    /**
     * Method to select New Item link
     */
    protected void selectNewItem()
    {
        try
        {
            waitUntilAlert();
            drone.findAndWait(NEW_ITEM_LINK).click();
            logger.info("Selected new item");
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + NEW_ITEM_LINK);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding " + NEW_ITEM_LINK);
        }
    }

    private boolean isDisplayed(By locator)
    {
        try
        {
            return drone.find(locator).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify whether New Item link is displayed
     *
     * @return boolean
     */
    public boolean isNewItemEnabled()
    {
        String someButton = drone.findAndWait(NEW_ITEM_LINK).getAttribute("disabled");
        if (someButton.contains("true"))
        {
            return false;
        }
        else
            return true;
    }

    /**
     * Method to verify whether duplicate item link is available
     *
     * @param itemName String
     * @return boolean
     */
    public boolean isDuplicateDisplayed(String itemName)
    {
        locateItemActions(itemName);
        return isDisplayed(DUPLICATE_LINK);
    }

    /**
     * Method to verify whether edit item link is available
     *
     * @param itemName String
     * @return boolean
     */
    public boolean isEditDisplayed(String itemName)
    {
        return locateItemActions(itemName).findElement(EDIT_LINK).isDisplayed();
    }

    /**
     * Method to verify whether delete item link is available
     *
     * @param itemName String
     * @return boolean
     */
    public boolean isDeleteDisplayed(String itemName)
    {
        locateItemActions(itemName);
        return isDisplayed(DELETE_LINK);
    }

    /**
     * Method to get the count of items in the list
     *
     * @return number of items
     */
    public int getItemsCount()
    {
        try
        {
            List<WebElement> allItems = drone.findAll(ITEM_CONTAINER);
            return allItems.size() - 1;
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + ITEM_CONTAINER);
        }
    }

    protected WebElement locateAnItem(String fieldValue)
    {
        WebElement theItem;
        try
        {
            theItem = drone.findAndWait(By.xpath(String.format("//td//div[text()='%s']/../..", fieldValue)));
            drone.mouseOver(theItem);
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find the item");
        }
        return theItem;
    }

    /**
     * Method to verify whether item is selected
     *
     * @param itemName String
     * @return boolean
     */
    public boolean isCheckBoxSelected(String itemName)
    {
        logger.info("Verifying whether checkbox for " + itemName + "is selected");
        try
        {
            WebElement theItem = locateAnItem(itemName);
            return theItem.findElement(By.cssSelector("input[name='fileChecked']")).isSelected();
        }
        catch (NoSuchElementException nse)
        {
            logger.info("The item is unchecked");
        }
        return false;
    }

    /**
     * Method to select item's checkbox
     *
     * @param itemName String
     */
    public void selectAnItem(String itemName)
    {
        try
        {
            WebElement theItem = locateAnItem(itemName);
            theItem.findElement(CHECKBOX).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find " + CHECKBOX);
        }
    }
}
