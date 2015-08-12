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
package org.alfresco.po.share.adminconsole;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.Collections;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class TagManagerPage extends AdminConsolePage
{

    private final static By SEARCH_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-button-button");
    private final static By SEARCH_INPUT = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-text");
    private final static By DASHLET_TAGS_LIST = By.cssSelector("div.dashlet.tags-List");
    private final static String SEARCH_RESULT_CSS = "tr[class~='yui-dt-rec']";
    private final static By SEARCH_RESULT = By.cssSelector(SEARCH_RESULT_CSS);
    public final By NO_RESULT = By.cssSelector("div.tags-list-info");
    private final static By RESULT_NAMES = By.cssSelector(SEARCH_RESULT_CSS + " > td[class*='name']");
    private final static By VISIBLE_EDIT_BUTTON = By.xpath("//a[@class='edit-tag edit-tag-active']");
    private final static By VISIBLE_DELETE_BUTTON = By.xpath("//a[@class='delete-tag delete-tag-active']");
    private final static By DELETE_BUTTON_POPUP = By.xpath("//button[text()='Delete']");


    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public TagManagerPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.alfresco.po.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public TagManagerPage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(SEARCH_BUTTON), getVisibleRenderElement(SEARCH_INPUT), getVisibleRenderElement(DASHLET_TAGS_LIST));

        return this;
    }

    /**
     * Click to the Search button
     *
     */
    public void clickSearchButton()
    {
        click(SEARCH_BUTTON);
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    /**
     * Fill Search field
     *
     */
    public void fillSearchField(String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(SEARCH_INPUT);
        inputField.clear();
        inputField.sendKeys(text);
    }

    /**
     * Check that after search has some results.
     *
     * @return true - if has results
     */
    public boolean isSearchResults()
    {
        List<WebElement> results;
        try
        {
            results = findAndWaitForElements(SEARCH_RESULT, 5000);
        }
        catch (StaleElementReferenceException e)
        {
            return isSearchResults();
        }
        catch (TimeoutException e)
        {
            results = Collections.emptyList();
        }
        return results.size() > 0;
    }


    /**
     * Check results by tag name.
     *
     * @param tagName - expect resultName
     * @return true if results has matches with expect
     */
    public boolean isInResults(String tagName)
    {
        checkNotNull(tagName);
        List<WebElement> resultsName = getTagsList();
        for (WebElement resultName : resultsName)
        {
            String name=resultName.findElement(By.cssSelector("a")).getAttribute("title");
            if (name.matches(tagName))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Get all results by displayed tags
     *
     * @return list of web elements
     */
    private List<WebElement> getTagsList()
    {
        try
        {
            return findAndWaitForElements(RESULT_NAMES, 5000);
        }
        catch (StaleElementReferenceException e)
        {
            return getTagsList();
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }

    }

    /**
     * Find tag on the result table
     *
     * @return web element
     */
    private WebElement getTagElement(String tagName)
    {
        List<WebElement> tags = getTagsList();

        for(WebElement tag : tags)
        {
            if(tag.findElement(By.cssSelector("a")).getAttribute("title").equals(tagName))
            {
                return tag;
            }
        }
        throw new PageOperationException(String.format("Tag [%s] didn't found on page.", tagName));
    }

    /**
     * Open Edit Form for tag
     *
     * @param tagName String
     * @return {@link EditTagForm}
     */
    private EditTagForm openEditTagForm(String tagName)
    {
        checkNotNull(tagName);
        WebElement tag = getTagElement(tagName);
        mouseOver(tag);
        click(VISIBLE_EDIT_BUTTON);
        return new EditTagForm();
    }

    /**
     * edit selected tag name
     *
     * @param tagName -
     *                name of tag that will by changed
     * @param newTagName -
     *                   new name of selected tag
     */
    public void editTag(String tagName, String newTagName)
    {
        checkNotNull(tagName);
        EditTagForm editTagForm = openEditTagForm(tagName);
        editTagForm.fillTagField(newTagName);
        editTagForm.clickOk();
        waitUntilAlert();
    }

    /**
     * delete selected tag name
     *
     * @param tagName -
     *                name of tag that will be deleted
     */
    public void deleteTag(String tagName)
    {
        checkNotNull(tagName);
        WebElement tag = getTagElement(tagName);
        mouseOver(tag);
        click(VISIBLE_DELETE_BUTTON);
        click(DELETE_BUTTON_POPUP);
        waitUntilAlert();
    }

    /**
     * search some tag
     *
     * @param tagName -
     *                name of tag
     */
    public void searchTag(String tagName)
    {
        try{
            checkNotNull(tagName);
            fillSearchField(tagName);
            clickSearchButton();
            if (!isSearchResults())
            {
                searchTag(tagName);
            }

        }
        catch (StaleElementReferenceException e)
        {
            searchTag(tagName);
        }
    }

}
