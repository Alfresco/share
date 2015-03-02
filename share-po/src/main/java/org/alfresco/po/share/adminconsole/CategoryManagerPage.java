/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Class associated with page in admin console 'Category Manager'
 *
 * @author Aliaksei Boole
 */
public class CategoryManagerPage extends AdminConsolePage
{

    private static By TITLE = By.xpath("//div[@class='title']");
    private static By CATEGORY_LIST = By.xpath("//div[@class='category']");
    private static By ALL_CATEGORIES = By.xpath("//span[@class='ygtvlabel']");
    private static By OPEN_SUB_CATEGORIES_LINK = By.xpath("./../../td//a");
    private static By VISIBLE_EDIT_BUTTON = By.xpath("//span[@class='insitu-edit-category' and contains(@style, 'visible;')]");
    private static By VISIBLE_ADD_BUTTON = By.xpath("//span[contains(@class,'insitu-add-') and contains(@style, 'visible;')]");
    private static By VISIBLE_DELETE_BUTTON = By.xpath("//span[@class='insitu-delete-category' and contains(@style, 'visible;')]");
    private static By NAME_CATEGORY_INPUT = By.xpath("//input[contains(@id,'form-field')]");
    private static By SAVE_CATEGORY_NAME = By.xpath("//form[@class='insitu-edit']/a[1]");
    private static By DELETE_POPUP_BUTTON = By.xpath("//span[@class='button-group']/span[1]/span/button");
    private static String CATEGORY_ROOT_SPACER = "//table[contains(@class, 'depth0')]";
    private static By CATEGORY_ROOT_SPACER_LINK = By.xpath(CATEGORY_ROOT_SPACER + "//a");

    /**
     * Instantiates a new admin console page(Category Manager).
     *
     * @param drone WebDriver browser client
     */
    public CategoryManagerPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CategoryManagerPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
                getVisibleRenderElement(TITLE),
                getVisibleRenderElement(CATEGORY_LIST),
                getVisibleRenderElement(ALL_CATEGORIES));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CategoryManagerPage render(long l)
    {
        checkArgument(l > 0);
        return render(new RenderTime(l));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CategoryManagerPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public List<WebElement> getListCategories()
    {
        try
        {
            return drone.findAndWaitForElements(ALL_CATEGORIES);
        }
        catch (StaleElementReferenceException e)
        {
            return getListCategories();
        }
    }

    private WebElement getCategory(String categoryName)
    {
        try
        {
            List<WebElement> categories = getListCategories();
            for (WebElement category : categories)
            {
                if (categoryName.equals(category.getText()))
                {
                    return category;
                }

            }
        }
        catch (StaleElementReferenceException e)
        {
            return getCategory(categoryName);
        }
        throw new PageOperationException(String.format("Category [%s] didn't found on page.", categoryName));
    }

    /**
     * Rename category name
     *
     * @param categoryName - old name for select
     * @param newName      - new category name
     */
    public void editCategory(String categoryName, String newName)
    {
        checkNotNull(categoryName, newName);
        WebElement category = getCategory(categoryName);
        drone.mouseOver(category);
        click(VISIBLE_EDIT_BUTTON);
        fillField(NAME_CATEGORY_INPUT, newName);
        click(SAVE_CATEGORY_NAME);
        waitUntilAlert();
        return;
    }

    /**
     * Add new category.
     *
     * @param parentCategory - parent category
     * @param categoryName   - adding category
     */
    public void addNewCategory(String parentCategory, String categoryName)
    {
        checkNotNull(parentCategory, categoryName);
        AddCategoryForm addCategoryForm = openAddCategoryForm(parentCategory);
        addCategoryForm.fillNameField(categoryName);
        addCategoryForm.clickOk();
        waitUntilAlert();
    }

    /**
     * Just open add category form for check it.
     *
     * @param categoryName - parent category
     * @return object associated with Category add form.
     */
    public AddCategoryForm openAddCategoryForm(String categoryName)
    {
        checkNotNull(categoryName);
        WebElement category = getCategory(categoryName);
        drone.mouseOver(category);
        click(VISIBLE_ADD_BUTTON);
        return new AddCategoryForm(drone);
    }

    /**
     * Open subCategory List.
     *
     * @param categoryName - parent category for opening
     */
    public void openSubCategoryList(String categoryName)
    {
        checkNotNull(categoryName);
        WebElement category = getCategory(categoryName);
        category.findElement(OPEN_SUB_CATEGORIES_LINK).click();
    }

    /**
     * Delete chosen category
     *
     * @param categoryName - Category that delete.
     */
    public void deleteCategory(String categoryName)
    {
        checkNotNull(categoryName);
        WebElement category = getCategory(categoryName);
        drone.mouseOver(category);
        click(VISIBLE_DELETE_BUTTON);
        click(DELETE_POPUP_BUTTON);
        waitUntilAlert();
    }

    /**
     * Check that category present on Page.
     *
     * @param categoryName
     * @return true - if present
     */
    public boolean isCategoryPresent(String categoryName)
    {
        checkNotNull(categoryName);
        try
        {
            return getCategory(categoryName) != null;
        }
        catch (PageOperationException e)
        {
            return false;
        }
    }

    /**
     * Return count categories on page
     *
     * @return count
     */
    public int getCategoriesCount()
    {
        try
        {
            return getListCategories().size();
        }
        catch (StaleElementReferenceException e)
        {
            return getCategoriesCount();
        }
    }

    private void click(By locator)
    {
        drone.waitUntilElementPresent(locator, 5);
        WebElement element = drone.findAndWait(locator);
        drone.executeJavaScript("arguments[0].click();", element);
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    /**
     * verify category root tree, if it collapsed that expand category root tree
     */
    public void expandCategoryRootTree()
    {

        try
        {
            WebElement spacer = drone.findAndWait(By.xpath(CATEGORY_ROOT_SPACER), 5000);

            if (!isCategoryRootTreeExpanded())
            {
                click(CATEGORY_ROOT_SPACER_LINK);
                drone.waitUntilElementPresent(ALL_CATEGORIES, 5);
                if (!spacer.getAttribute("class").contains("expanded"))
                    expandCategoryRootTree();
            }

        }
        catch (StaleElementReferenceException e)
        {
            expandCategoryRootTree();
        }

    }

    /**
     * Verify category root tree expanded or not
     *
     * @return boolean
     */
    public boolean isCategoryRootTreeExpanded()
    {
        try
        {
            WebElement spacer = drone.findAndWait(By.xpath(CATEGORY_ROOT_SPACER), 5000);
            return spacer.getAttribute("class").contains("expanded");
        }
        catch (StaleElementReferenceException e)
        {
            isCategoryRootTreeExpanded();
        }

        return false;
    }
}
