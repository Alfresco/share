/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.po.share.search;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;
import java.util.NoSuchElementException;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * Advance search Abstract contains all the common functions of the search
 * forms.
 *
 * @author Subashni Prasanna
 * @since 1.6
 */
public class AdvanceSearchPage extends SharePage
{
    protected static final By KEYWORD_SEARCH = By.cssSelector("input[id$='default-search-text']");
    protected static final By NAME_SEARCH = By.cssSelector("input[id$='prop_cm_name']");
    protected static final By TITLE_SEARCH = By.cssSelector("textarea[id$='prop_cm_title']");
    protected static final By DESCRIPTION_SEARCH = By.cssSelector("textarea[id$='prop_cm_description']");
    protected static final By MODIFIER_SEARCH = By.cssSelector("input[id$='prop_cm_modifier']");
    protected static final By MODIFIER_FROM_SEARCH = By.cssSelector("input[id$='cntrl-date-from']");
    protected static final By MODIFIER_TO_SEARCH = By.cssSelector("input[id$='cntrl-date-to']");
    protected static final By CONTENT_MIME_TYPE = By.cssSelector("select[id$='prop_mimetype']");
    protected static final By SEARCH_BUTTON = By.cssSelector("button[id$='search-button-1-button']");
    protected static final By FOLDER_SEARCH_MENU = By.cssSelector("div[id$='default-selected-form-list']");
    protected static final By FOLDER_SEARCH_MENU_ITEM = By.cssSelector("ul.first-of-type");
    protected static final By FOLDER_MENU_LIST = By.cssSelector("span[class$='yuimenuitemlabel']");
    protected static final By CONTENT_SEARCH_FORM_DROPDOWN = By.cssSelector("button[id$='selected-form-button-button']");
    protected static final By LOOK_FOR_DRP_DWN = By.cssSelector(".bd ul");
    protected static final By BACK_TO_RESULTS_LINK = By.cssSelector("#HEADER_SEARCH_BACK_TO_RESULTS");
    //protected static final By BACK_TO_SITE_LINK = By.cssSelector("#HEADER_SEARCH_BACK_TO_SITE_DASHBOARD");
    protected static final By BACK_TO_SITE_LINK = By.cssSelector("#HEADER_SEARCH_BACK_TO_SITE_DEFAULT_text");
    private final RenderElement contentSearchFormDropdownElement = getVisibleRenderElement(CONTENT_SEARCH_FORM_DROPDOWN);
    private final RenderElement searchButtonElement = getVisibleRenderElement(SEARCH_BUTTON);

    /**
     * Check whether Search button is displayed correctly.
     *
     * @return true if page is displayed correctly
     */
    protected boolean isSearchButtonDisplayed()
    {
        try
        {
            return driver.findElement(SEARCH_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Check whether Date Modifier are not displayed.
     *
     * @return true if search button
     */
    protected boolean isDateModifierFromDisplayed()
    {
        try
        {
            return driver.findElement(MODIFIER_FROM_SEARCH).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Content keyword is Displayed and enter search text in the keyword.
     *
     * @param keyWordSearchText String
     */
    public void inputKeyword(final String keyWordSearchText)
    {
        if (keyWordSearchText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement keyWordElement = findElementDisplayed(KEYWORD_SEARCH);
        keyWordElement.clear();
        keyWordElement.sendKeys(keyWordSearchText);
    }

    /**
     * Get the value entered in the keyword field.
     *
     * @return String
     */
    public String getKeyword()
    {
        return findElementDisplayed(KEYWORD_SEARCH).getAttribute("value");
    }

    @FindBy(css="input[id$='prop_cm_name']") TextInput name;
    /**
     * Enter the text value in the Name field.
     *
     * @param nameSearchText String
     */
    public void inputName(final String nameSearchText)
    {
        if (nameSearchText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement nameElement = findElementDisplayed(NAME_SEARCH);
        nameElement.clear();
        nameElement.sendKeys(nameSearchText);
//        name.clear();
//        name.sendKeys(nameSearchText);
    }

    /**
     * Get the value entered in the Name field.
     *
     * @return String
     */
    public String getName()
    {
        return name.getText();
    }

    /**
     * Enter the text value in the title field.
     *
     * @param titleSearchText String
     */
    public void inputTitle(final String titleSearchText)
    {
        if (titleSearchText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement titleElement = findElementDisplayed(TITLE_SEARCH);
        titleElement.clear();
        titleElement.sendKeys(titleSearchText);
    }

    /**
     * Get the value enetered in the title field.
     *
     * @return String
     */
    public String getTitle()
    {
        return findElementDisplayed(TITLE_SEARCH).getAttribute("value");
    }

    /**
     * Enter the text value in the description field.
     *
     * @param descriptionSearchText String
     */
    public void inputDescription(final String descriptionSearchText)
    {
        if (descriptionSearchText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement descriptionElement = findElementDisplayed(DESCRIPTION_SEARCH);
        descriptionElement.clear();
        descriptionElement.sendKeys(descriptionSearchText);
    }

    /**
     * Get the value entered in the description field.
     *
     * @return String
     */
    public String getDescription()
    {
        return findElementDisplayed(DESCRIPTION_SEARCH).getAttribute("value");
    }

    /**
     * Enter the text value in the modifier field.
     *
     * @param modifierSearchText String
     */
    public void inputModifier(final String modifierSearchText)
    {
        if (modifierSearchText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement modifierElement = findElementDisplayed(MODIFIER_SEARCH);
        modifierElement.clear();
        modifierElement.sendKeys(modifierSearchText);
    }

    /**
     * Get the value entered in the modifier field.
     *
     * @return String .
     */
    public String getModifier()
    {
        return findElementDisplayed(MODIFIER_SEARCH).getAttribute("value");
    }

    /**
     * Enter the date in the from date field.
     *
     * @param fromDateText String
     */
    public void inputFromDate(final String fromDateText)
    {
        if (fromDateText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement modifierFromElement = findElementDisplayed(MODIFIER_FROM_SEARCH);
        modifierFromElement.clear();
        modifierFromElement.sendKeys(fromDateText);
    }

    /**
     * Get the value entered in the from date field.
     *
     * @return String
     */
    public String getFromDate()
    {
        return findElementDisplayed(MODIFIER_FROM_SEARCH).getAttribute("value");
    }

    /**
     * Enter the date in the To date field.
     *
     * @param toDateText String
     */
    public void inputToDate(final String toDateText)
    {
        if (toDateText == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement modifierToElement = findElementDisplayed(MODIFIER_TO_SEARCH);
        modifierToElement.clear();
        modifierToElement.sendKeys(toDateText);
    }

    /**
     * Get the value entered in the To date Field.
     *
     * @return String
     */
    public String getToDate()
    {
        return findElementDisplayed(MODIFIER_TO_SEARCH).getAttribute("value");
    }

    /**
     * Select the Mime type.
     *
     * @param mimeType String
     */
    public void selectMimeType(final String mimeType)
    {
        if (mimeType == null)
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement dropDown = driver.findElement(CONTENT_MIME_TYPE);
        Select select = new Select(dropDown);
        select.selectByVisibleText(mimeType);
    }

    /**
     * Click on Search button.
     *
     * @return {@link SharePage}
     */
    public HtmlPage clickSearch()
    {
        driver.findElement(SEARCH_BUTTON).click();
        waitUntilElementDisappears(SEARCH_BUTTON, 1);
        return getCurrentPage();
    }

    /**
     * This function will help to find element in the content and folder search form.
     *
     * @return - WebElement
     */
    protected WebElement findElementDisplayed(By elementId)
    {
        List<WebElement> elementList = driver.findElements(elementId);
        for (WebElement elementSelected : elementList)
        {
            if (elementSelected.isDisplayed())
            {
                return elementSelected;
            }
        }
        throw new NoSuchElementException("Element Not found");
    }

    /**
     * Select Folder search in the drop down.
     *
     * @return {@link HtmlPage}
     */
    public HtmlPage searchLink(final String searchType)
    {
        try
        {
            driver.findElement(CONTENT_SEARCH_FORM_DROPDOWN).click();
            WebElement searchMenudropdown = findAndWait(FOLDER_SEARCH_MENU);
            WebElement menuWebElement = searchMenudropdown.findElement(FOLDER_SEARCH_MENU_ITEM);
            List<WebElement> menuList = menuWebElement.findElements(FOLDER_MENU_LIST);
            for (WebElement searchelement : menuList)
            {
                if (searchType.equalsIgnoreCase("Folders") && searchType.equals(searchelement.getText()))
                {
                    searchelement.click();
                    return getCurrentPage();
                }
                else if ((searchType.equalsIgnoreCase("CRM Attachments") && searchType.equals(searchelement.getText())))
                {
                    searchelement.click();
                    return getCurrentPage();
                }
            }
            throw new PageException(searchType + " Search Page not found");
        }
        catch (TimeoutException te)
        {
            throw new PageException("Search link is not visible", te);
        }
    }

    private boolean isLookForDropDownCorrect()
    {
        boolean isContent = false;
        boolean isFolder = false;
        try
        {
            findElementDisplayed(LOOK_FOR_DRP_DWN);
        }
        catch (NoSuchElementException nse)
        {
            driver.findElement(CONTENT_SEARCH_FORM_DROPDOWN).click();
        }
        WebElement searchMenudropdown = findAndWait(FOLDER_SEARCH_MENU);
        WebElement menuWebElement = searchMenudropdown.findElement(FOLDER_SEARCH_MENU_ITEM);
        List<WebElement> menuList = menuWebElement.findElements(FOLDER_MENU_LIST);
        for (WebElement searchelement : menuList)
        {
            if (searchelement.getText().equalsIgnoreCase(getValue("advanced.search.content")))
                isContent = true;
            else if (searchelement.getText().equalsIgnoreCase(getValue("advanced.search.folders")))
                isFolder = true;
        }
        return isContent && isFolder;
    }

    /**
     * Check whether Modified From Date is valid or not.
     *
     * @return true if date is valid otherwise false.
     */
    public boolean isValidFromDate()
    {
        try
        {
            return findElementDisplayed(MODIFIER_FROM_SEARCH).getAttribute("class").equalsIgnoreCase("date-entry");
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    private boolean isPageCorrect()
    {
        return findElementDisplayed(CONTENT_SEARCH_FORM_DROPDOWN).isDisplayed() && isLookForDropDownCorrect()
            && isSearchButtonDisplayed() && findElementDisplayed(KEYWORD_SEARCH).isDisplayed() && name.isDisplayed()
            && findElementDisplayed(TITLE_SEARCH).isDisplayed() && findElementDisplayed(DESCRIPTION_SEARCH).isDisplayed();
    }

    /**
     * Method to verify whether all elements are present on the page
     *
     * @return true if page is correct
     */
    public boolean isAdvSearchPageCorrectlyDisplayed()
    {
        boolean isCorrect;
        SharePage page = getCurrentPage().render();
        if (page instanceof AdvanceSearchFolderPage)
            isCorrect = isPageCorrect();
        else
            isCorrect = isPageCorrect() && findElementDisplayed(CONTENT_MIME_TYPE).isDisplayed() && findElementDisplayed(MODIFIER_FROM_SEARCH).isDisplayed()
                && findElementDisplayed(MODIFIER_TO_SEARCH).isDisplayed() && findElementDisplayed(MODIFIER_SEARCH).isDisplayed();
        return isCorrect;
    }

    /**
     * Method to navigate back to results
     *
     * @return SiteResultsPage
     */
    public HtmlPage clickBackToResults()
    {
        try
        {
            WebElement backLink = driver.findElement(BACK_TO_RESULTS_LINK);
            backLink.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + BACK_TO_RESULTS_LINK);
        }
        return getCurrentPage();
    }

    /**
     * Method to navigate back to site
     *
     * @return SharePage
     */
    public HtmlPage clickBackToSite()
    {
        try
        {
            WebElement backLink = driver.findElement(BACK_TO_SITE_LINK);
            backLink.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + BACK_TO_SITE_LINK);
        }
        return getCurrentPage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, contentSearchFormDropdownElement, searchButtonElement);
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
