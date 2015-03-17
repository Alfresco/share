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
package org.alfresco.po.share.site.datalist;


import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.DataLists;
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

/**
 * Site data list page object, holds all element of the HTML page
 * relating to share's site data list page.
 *
 * @author Michael Suzuki
 * @since 1.2
 */
public class DataListPage extends AbstractDataList
{
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By NEW_LIST_LINK = By.cssSelector("button[id$='default-newListButton-button']");
    private static final By LISTS_TYPES_CONTAINER = By.cssSelector("div[id$='itemTypesContainer']>div");
    private static final By DEFAULT_LISTS_CONTAINER = By.cssSelector("div[id*='default-lists']>div");
    private static final By LISTS_CONTAINER = By.cssSelector("div[id*='default-lists']>ul>li");
    private static final By NEW_LIST_FORM = By.cssSelector("div[id*='default-newList-form-fields']");
    private final static By DATA_LIST_DESCRIPTION = By.cssSelector("div[id$='_default-description']");
    private final static By NO_DATA_LIST_FOUND = By.cssSelector("div[class='no-lists']");
    private final static By SELECT_DROPDWN = By.cssSelector("button[id$='default-itemSelect-button-button']");
    private final static By SELECTED_ITEMS_DROPDWN = By.cssSelector("button[id$='selectedItems-button-button']");


    public DataListPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(NEW_LIST_LINK));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListPage render(long time)
    {
        return render(new RenderTime(time));
    }


    public enum selectOptions
    {
        SELECT_ALL(".selectAll"),
        INVERT_SELECT(".selectInvert"),
        SELECT_NONE(".selectNone");

        public final String select;

        selectOptions(String select)
        {
            this.select = select;
        }

        public By getLocator()
        {
            return By.cssSelector(select);
        }
    }

    public enum selectedItemsOptions
    {
        DUPLICATE(".onActionDuplicate"),
        DELETE(".onActionDelete"),
        DESELECT_ALL(".onActionDeselectAll");

        public final String option;

        selectedItemsOptions(String option)
        {
            this.option = option;
        }

        public By getLocator()
        {
            return By.cssSelector(option);
        }
    }

    /**
     * Method to click New List button
     *
     * @return New List form object
     */
    private NewListForm clickNewList()
    {
        try
        {
            drone.findAndWait(NEW_LIST_LINK, 40000).click();
        }
        catch (TimeoutException te)
        {
            logger.debug("Timed out finding " + NEW_LIST_LINK);
        }
        return new NewListForm(drone);
    }

    /**
     * Method to creata a Data List
     *
     * @param listType requires ListType
     * @param title    title of the List
     * @param desc     Description
     * @return DataList page object
     */
    public DataListPage createDataList(DataLists listType, String title, String desc)
    {
        logger.info("Creating a Data List of given type");
        if(!drone.isElementDisplayed(NEW_LIST_FORM))
        {
            clickNewList();
            waitUntilAlert();
        }
        List<WebElement> typeOptions = drone.findAndWaitForElements(LISTS_TYPES_CONTAINER);
        typeOptions.get(listType.ordinal()).click();
        NewListForm newListForm = new NewListForm(drone);
        newListForm.inputTitleField(title);
        newListForm.inputDescriptionField(desc);
        newListForm.clickSave();
        waitUntilAlert();
        return new DataListPage(drone);
    }

    /**
     * Method to select a data list  according to specified name
     *
     * @param name
     */
    public void selectDataList(String name)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//div[contains(@id,'default-lists')]//a[text()='%s']", name))).click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
    }

    /**
     * Method to verify whether New List button is displayed
     *
     * @return true if displayed
     */
    public boolean isNewListEnabled()
    {
        try
        {
            return drone.findAndWait(NEW_LIST_LINK).isEnabled();
        }
        catch (TimeoutException nse)
        {
            return false;
        }
    }

    public DataListDirectoryInfo getDataListDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']", title)), WAIT_TIME_3000);
            drone.mouseOverOnElement(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return new DataListDirectoryInfo(drone, row);
    }

    /**
     * Method for editing a data list
     * @param oldTitle
     * @param newTitle
     * @param newDescription
     * @return Data List page
     */
    public DataListPage editDataList (String oldTitle, String newTitle, String newDescription)
    {
        logger.info("Editing the data list " + oldTitle);
        NewListForm newListForm = getDataListDirectoryInfo(oldTitle).clickEdit();
        newListForm.inputTitleField(newTitle);
        newListForm.inputDescriptionField(newDescription);
        newListForm.clickSave();
        return new DataListPage(drone).render();
    }

    /**
     * Method for deleting a list with confirmation
     * @param title
     * @return Data List Page
     */
    public DataListPage deleteDataListWithConfirm (String title)
    {
        logger.info("Deleting " + title + "data list");
        try
        {
            getDataListDirectoryInfo(title).clickDelete();
            drone.findAndWait(SharePage.CONFIRM_DELETE).click();
            waitUntilAlert();
            return new DataListPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete a list");
        }
    }

    /**
     * Method to get a number of lists
     * @return number of lists
     */
    public int getListsCount ()
    {
        try
        {
            if(drone.isElementDisplayed(NEW_LIST_FORM))
            {
                return 0;
            }
            if(drone.isElementDisplayed(DEFAULT_LISTS_CONTAINER))
            {
                return 0;
            }
            return drone.findAndWaitForElements(LISTS_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find lists container");
        }
    }

    public boolean isEditDataListDisplayed(String list)
    {
        return getDataListDirectoryInfo(list).isEditDisplayed();
    }

    public boolean isDeleteDataListDisplayed(String list)
    {
        return getDataListDirectoryInfo(list).isDeleteDisplayed();
    }

    /**
     * Method to retrieve the description of a data list
     *
     * @return String
     */
    public String getDataListDescription(String title)
    {
        selectDataList(title);
        try
        {
            String dataListDescription = drone.findAndWait(DATA_LIST_DESCRIPTION).getText();
            if (!dataListDescription.isEmpty())
                return dataListDescription;
            else
                throw new IllegalArgumentException("Cannot find data list description");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the  data list description");
        }
    }

    /**
     * Method to check that no data list
     * 
     * @return true if no list created
     */
    public boolean isNoListFoundDisplayed()
    {
        try
        {
            WebElement noListFound = drone.findAndWait(NO_DATA_LIST_FOUND);
            return noListFound.isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method for click on cancel button
     * 
     * @return DataListPage
     */
    public DataListPage clickCancel()
    {
        NewListForm newListForm;
        if ((drone.getCurrentPage().render()) instanceof NewListForm)
        {
            newListForm = drone.getCurrentPage().render();
            newListForm.clickCancel();
        }
        return drone.getCurrentPage().render();
    }

    public List<String> getLists()
    {
        List<String> textValuesList = new ArrayList<>();
        try
        {
            List<WebElement> allLists = drone.findAll(LISTS_CONTAINER);
            for(WebElement allTheLists : allLists)
            {
                textValuesList.add(allTheLists.getText());
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find " + LISTS_CONTAINER);
        }
        return textValuesList;
    }

    public DataListTreeMenuNavigation getLeftMenus()
    {
        return new DataListTreeMenuNavigation(drone);
    }

    /**
     * Method to click Select Drop Down button
     */
    private void clickSelectDropDown()
    {
        try
        {
            drone.findAndWait(SELECT_DROPDWN).click();
        }
        catch (TimeoutException e)
        {
            throw new PageException("Not able to find " + SELECT_DROPDWN);
        }
    }

    /**
     * @return true is Select Menu Visible, else false.
     */
    public boolean isSelectMenuVisible()
    {
        try
        {
            return drone.findAndWait(SELECT_DROPDWN).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Mimics the action select All select dropdown.
     *
     * @return {@link org.alfresco.po.share.site.document.DocumentLibraryPage}
     */
    public DataListPage select(selectOptions option)
    {
        logger.info("Selecting " + option);
        try
        {
            clickSelectDropDown();
            if (isSelectMenuVisible())
            {
                drone.findAndWait(option.getLocator()).click();
                return drone.getCurrentPage().render();
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    private boolean isSelectedItemsDropDwnEnabled()
    {
        return drone.find(SELECTED_ITEMS_DROPDWN).isEnabled();
    }

    private void clickSelectedItemsDropDwn()
    {
        try
        {
            WebElement dropDown = drone.findAndWait(SELECTED_ITEMS_DROPDWN);
            dropDown.click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + SELECTED_ITEMS_DROPDWN);
        }
    }

    /**
     * Method to click Selected items drop down
     *
     * @param option
     * @return Share Page
     */
    public SharePage chooseSelectedItemOpt(selectedItemsOptions option)
    {
        logger.info("Selecting " + option);
        try
        {
            if(isSelectedItemsDropDwnEnabled())
            {
                clickSelectedItemsDropDwn();
                drone.findAndWait(option.getLocator()).click();
                waitUntilAlert();
            }
            else
            {
                throw new UnsupportedOperationException("None Items are selected");
            }
        }
        catch (TimeoutException e)
        {
            throw new PageException("Unable to find the " + option.getLocator());
        }
        return drone.getCurrentPage().render();
    }
}
