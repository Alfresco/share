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
package org.alfresco.po.share;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Olga Antonik
 */
public class AddUserToGroupForm extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By USER_FINDER_INPUT = By.cssSelector("input[id*='search-peoplefinder-search-text']");
    private static final By USER_SEARCH_BUTTON = By.cssSelector("button[id*='search-peoplefinder-search-button-button']");
    private static final By ADD_USER_FORM = By.cssSelector("div[id*='peoplepicker_c']");
    private static final String ADD_BUTTON = "//tbody[@class='yui-dt-data']/tr//a[contains(text(),'%s')]/../../../..//button";
    private static final By CLOSE_X = By.xpath("//div[contains(@id,'-peoplepicker')]/a");

    /**
     * (non-Javadoc)
     *
     * @see org.alfresco.po.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddUserToGroupForm render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(USER_FINDER_INPUT), getVisibleRenderElement(USER_SEARCH_BUTTON));
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.alfresco.po.Render#render()
     */
    @SuppressWarnings("unchecked")
    @Override
    public AddUserToGroupForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    AddUserToGroupForm addUserToGroupForm;
    GroupsPage groupsPage;
    /**
     * Search user
     *
     * @param user String
     * @return AddUserToGroupForm
     */
    public AddUserToGroupForm searchUser(String user)
    {
        try
        {
            WebElement searchField = findAndWait(USER_FINDER_INPUT);
            searchField.clear();
            searchField.sendKeys(user);
            findAndWait(USER_SEARCH_BUTTON).click();

        }
        catch (TimeoutException te)
        {
            logger.error("Failed to find user search input field");
        }
        return addUserToGroupForm.render();
    }

    /**
     * Click to the Add button
     *
     * @param user String
     * @return GroupsPage
     */
    private GroupsPage clickAddButton(String user)
    {

        try
        {
            WebElement addButton = findAndWait(By.xpath(String.format(ADD_BUTTON, user)));
            addButton.click();

        }
        catch (TimeoutException e)
        {
            throw new PageException("Not found Element: Add User", e);
        }

        return groupsPage.render();
    }

    /**
     * Search and add user to the another group
     *
     * @param user String
     * @return GroupsPage
     */
    public GroupsPage addUser(String user)
    {
        try
        {
            checkNotNull(user);
            findAndWait(ADD_USER_FORM);
            AddUserToGroupForm addUserForm = searchUser(user).render();
            return addUserForm.clickAddButton(user).render();

        }
        catch (TimeoutException e)
        {
            throw new PageException("Add User form is not open", e);
        }

    }

    public void closeForm()
    {
        findAndWait(CLOSE_X).click();
    }

}
