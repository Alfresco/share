/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

package org.alfresco.po.share.user;

import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * As part of 42 new features the user can recover or completely delete from the
 * repository using my profile trashcan. TrashCanItem will provide all the
 * functionalities for each of trashcan items Name, UserFullName, ItemPath,
 * DeletionDate
 * 
 * @author Subashni Prasanna
 * @since 1.7.0
 */
public class TrashCanItem extends HtmlElement
{
    private WebElement webElement;
    private static final By TRASHCAN_ITEM_NAME = By.cssSelector("div.name");
    private static final By TRASHCAN_ITEM_DESC = By.cssSelector("td[class$='yui-dt-col-description'] div div.desc a");
    private static final By TRASHCAN_DESC_LIST = By.cssSelector("td[class$='yui-dt-col-description'] div div.desc");
    private static final By TRASHCAN_ITEM_FILE = By.cssSelector("img[src*='-file']");
    private static final By TRASHCAN_ITEM_FOLDER = By.cssSelector("img[src*='-folder']");
    private static final By TRASHCAN_TIEM_SITE = By.cssSelector("img[src*='-site']");
    protected static final By TRASHCAN_SELECT_ITEM_CHECKBOX = By.cssSelector("input[id^='checkbox']");
    protected static final By TRASHCAN_BUTTON = By.cssSelector("td[class$='yui-dt-col-actions yui-dt-last'] button");
    private boolean deleteInitiator = false;

    /**
     * Constructor
     * 
     * @param element {@link WebElement}
     * @param drone
     */
    public TrashCanItem(WebElement element, WebDrone drone)
    {
        super(drone);
        webElement = element;
    }

    /**
     * Method to get Filename of the trashcan Item
     * 
     * @return - String - Filename
     * @throws - PageOperationException
     */
    public String getFileName() throws PageOperationException
    {
        String name = "";
        try
        {
            name = webElement.findElement(TRASHCAN_ITEM_NAME).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("TrashCan Description is not available", nse);
        }
        return name;
    }

    /**
     * Method to check whether the trashCan item is file
     * 
     * @return - Boolean
     */
    public boolean isTrashCanItemFile()
    {
        try
        {
            return webElement.findElement(TRASHCAN_ITEM_FILE).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check whether the trashCan item folder
     * 
     * @return Boolean
     */
    public boolean isTrashCanItemFolder()
    {
        try
        {
            return webElement.findElement(TRASHCAN_ITEM_FOLDER).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check whether the trashCan item is Site
     * 
     * @return - Boolean
     */
    public boolean isTrashCanItemSite()
    {
        try
        {
            return webElement.findElement(TRASHCAN_TIEM_SITE).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to get Description and Folder name
     * 
     * @return - String - UserFullName
     * @throws - PageOperationException
     */
    public String getUserFullName() throws PageOperationException
    {
        String userFullName = "";
        try
        {
            userFullName = webElement.findElement(TRASHCAN_ITEM_DESC).getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("userName for the item cannot be found", nse);
        }
        return userFullName;
    }

    /**
     * Method to get Date
     * 
     * @return - String
     * @throws - PageOperationException
     */
    public String getDate() throws PageOperationException
    {
        String deletionDate = "";
        try
        {
            for (WebElement desc : webElement.findElements(TRASHCAN_DESC_LIST))
            {
                String descText = desc.getText();
                if (descText.contains("Deleted on"))
                {
                    deletionDate = descText.replace("Deleted on ", "").replace("by", "").trim();
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Date could not be found", nse);
        }
        return deletionDate;
    }

    /**
     * Method to get FolderPath
     * 
     * @return - String
     * @throws - PageOperationException
     */
    public String getFolderPath() throws PageOperationException
    {
        String itemPath = "";
        try
        {
            for (WebElement desc : webElement.findElements(TRASHCAN_DESC_LIST))
            {
                String descText = desc.getText();
                if (descText.contains("/"))
                {
                    itemPath = descText;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Date could not be found", nse);
        }
        return itemPath;
    }

    /**
     * select check box to delete an item
     * 
     * @param - itemToBeChecked,folderPath
     * @return - TrashCanPage
     * @throws PageOperationException
     */

    public TrashCanPage selectTrashCanItemCheckBox() throws PageOperationException
    {
        try
        {
            webElement.findElement(TRASHCAN_SELECT_ITEM_CHECKBOX).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("The trashcan list is empty or cannot select an item for the given filename and folder name", nse);
        }
        return new TrashCanPage(drone);
    }

    /**
     * Is check box selected
     * 
     * @return - Boolean
     */
    public boolean isCheckBoxSelected()
    {
        try
        {
            return (webElement.findElement(TRASHCAN_SELECT_ITEM_CHECKBOX).isSelected());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

    }

    /**
     * This method will click on the recover button and that item will be
     * recovered from TrashCan
     * 
     * @param - itemToBeRecovered, actionType
     * @return - TrashCanPage
     * @throws - PageOperationException
     */
    public HtmlPage selectTrashCanAction(TrashCanValues trashCanActionType) throws PageOperationException
    {
        String buttonName;
        HtmlPage returnPage = null;
        try
        {
            List<WebElement> buttonList = webElement.findElements(TRASHCAN_BUTTON);
            for (WebElement button : buttonList)
            {
                buttonName = button.getText();
                if (buttonName.equalsIgnoreCase(trashCanActionType.getTrashCanValues()))
                {
                    returnPage = submit(By.id(button.getAttribute("id")), ElementState.INVISIBLE);
                    if (drone.isElementDisplayed(By.cssSelector("div.bd>span.message")))
                    {
                        String text = drone.find(By.cssSelector("div.bd>span.message")).getText();
                        if (text.contains("Failed to recover"))
                        {
                            throw new ShareException("Failed to recover");
                        }

                    }
                    break;
                }
            }
            if (trashCanActionType.equals(TrashCanValues.DELETE))
            {
                return new TrashCanDeleteConfirmationPage(drone, deleteInitiator);
            }
            return returnPage;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("The trashcan list is empty so cannot select an item", nse);
        }
    }

    /**
     * <li>Click the element which passed and wait for given ElementState on the same element.</li> <li>If the Element State not changed, then render the
     * {@link SharePopup} Page, if it is rendered the return {@link SharePopup} page.</li>
     * 
     * @param locator
     * @param elementState
     * @return {@link HtmlPage}
     */
    protected HtmlPage submit(By locator, ElementState elementState)
    {
        try
        {
            WebElement button = drone.find(locator);
            String id = button.getAttribute("id");
            button.click();
            By locatorById = By.id(id);
            long elementWaitInSeconds = ((WebDroneImpl) drone).getDefaultWaitTime() / 1000;
            long popupRendertime = ((WebDroneImpl) drone).getDefaultWaitTime() / 1000;
            RenderTime time = new RenderTime(maxTime);
            time.start();
            while (true)
            {
                try
                {
                    switch (elementState)
                    {
                        case INVISIBLE:
                            drone.waitUntilElementDisappears(locatorById, elementWaitInSeconds);
                            break;
                        case DELETE_FROM_DOM:
                            drone.waitUntilElementDeletedFromDom(locatorById, elementWaitInSeconds);
                            break;
                        default:
                            throw new UnsupportedOperationException(elementState + "is not currently supported by submit.");
                    }
                }
                catch (TimeoutException e)
                {
                    SharePopup errorPopup = new SharePopup(drone);
                    try
                    {
                        errorPopup.render(new RenderTime(popupRendertime));
                        return errorPopup;
                    }
                    catch (PageRenderTimeException exception)
                    {
                        continue;
                    }
                }
                finally
                {
                    time.end();
                }
                break;
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException te)
        {
        }
        throw new PageException("Not able to find the Page, may be locator missing in the page : " + locator.toString());
    }
}
