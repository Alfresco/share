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
package org.alfresco.po.share.site.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Page object for searching user and selecting user. Ideally should not live w/o
 * ManagePersmissions instance. Hence its an inner class with private constructor.
 *
 * @author Abhijeet Bharade
 * @since 1.7.0
 */
public class UserSearchPage extends SharePage
{
    private static final int SEARCH_TEXT_MIN_LEN = 3;
    private final By SEARCH_USER_INPUT = By.cssSelector("div.search-text input");
    private final By SEARCH_USER_BUTTON = By.cssSelector("div.authority-search-button button");
    private final By saveButtonLocator = By.cssSelector("button[id$='-okButton-button']");
    private final By cancelButton = By.cssSelector("button[id$='-cancelButton-button']");
    @FindBy(css="div.finder-wrapper") WebElement searchContainerDiv;

    @SuppressWarnings("unchecked")
    @Override
    public UserSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserSearchPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                WebElement message = searchContainerDiv.findElement(By.cssSelector("tbody.yui-dt-message div"));
                if (message.isDisplayed() && message.getText().contains("Searching..."))
                {
                    continue;
                }
                else
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        elementRender(timer, RenderElement.getVisibleRenderElement(SEARCH_USER_BUTTON), RenderElement.getVisibleRenderElement(SEARCH_USER_INPUT));
        return this;
    }

    /**
     * @param userProfile
     * @return
     */
    public HtmlPage searchAndSelectUser(UserProfile userProfile)
    {
        return searchAndSelect("", userProfile);
    }

    /**
     * @param groupName
     * @return
     */
    public HtmlPage searchAndSelectGroup(String groupName)
    {
        return searchAndSelect(groupName, null);
    }

    /**
     * Returns if "EVERYONE" is available in search result.
     *
     * @param searchText
     * @return
     */
    public boolean isEveryOneDisplayed(String searchText)
    {
        try
        {
            // By USERNAME_SPAN = By.cssSelector("h3>span");

            for (UserSearchRow element : searchUserAndGroup(searchText))
            {
                if (element.getUserName().contains("EVERYONE"))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("User with username containing - '" + searchText + "' not found", nse);
        }
        return false;
    }

    /**
     * @param groupName
     * @param userProfile
     * @return
     */
    private HtmlPage searchAndSelect(String groupName, UserProfile userProfile)
    {
        String searchText = "";
        boolean isGroupSearch = false;
        if (!StringUtils.isEmpty(groupName))
        {
            searchText = groupName;
            isGroupSearch = true;
        }
        else if (!StringUtils.isEmpty(userProfile.getUsername()))
        {
            searchText = userProfile.getUsername();
        }
        else
        {
            throw new UnsupportedOperationException(" Name search text cannot be blank - min three characters required");
        }
        return selectUserOrGroup(searchUserAndGroup(searchText), searchText, isGroupSearch, userProfile);

    }

    /**
     * @param searchText
     * @return
     */
    public List<UserSearchRow> searchUserAndGroup(String searchText) throws UnsupportedOperationException
    {
        List<UserSearchRow> searchRows = new ArrayList<UserSearchRow>();
        try
        {
            driver.findElement(SEARCH_USER_INPUT).clear();
            driver.findElement(SEARCH_USER_INPUT).sendKeys(searchText);
            driver.findElement(SEARCH_USER_BUTTON).click();
            if (searchText.length() < SEARCH_TEXT_MIN_LEN)
            {
                WebElement element = driver.findElement(By.cssSelector(".message"));
                throw new UnsupportedOperationException(element.getText());
            }
            else
            {

                waitForElement(SEARCH_USER_INPUT, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                waitForElement(SEARCH_USER_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                waitForElement(cancelButton, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                waitForElement(saveButtonLocator, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                Thread.sleep(3000);
                List<WebElement> elems = driver.findElements(By.xpath("//tbody/tr/td[contains(@class, 'empty')]/div"));
                for (WebElement elem : elems)
                    if (elem.isDisplayed())
                    {
                        if (elem.getText().equals("No results"))
                            return null;
                    }

                this.render();
                By DATA_ROWS = By.cssSelector("div.finder-wrapper tbody.yui-dt-data tr");
                for (WebElement element : findAndWaitForElements(DATA_ROWS, maxPageLoadingTime))
                {
                    searchRows.add(new UserSearchRow(driver, element,factoryPage));
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("element not found", nse);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return searchRows;
    }

    /**
     * @param searchRows
     * @param searchText
     * @param isGroupSearch
     * @param userProfile
     * @return
     */
    public HtmlPage selectUserOrGroup(List<UserSearchRow> searchRows, String searchText, boolean isGroupSearch, UserProfile userProfile)
    {
        if (StringUtils.isEmpty(searchText))
        {
            throw new IllegalArgumentException("Search value is null");
        }
        for (UserSearchRow searchRow : searchRows)
        {
            if (isGroupSearch)
            {
                if (searchRow.getUserName().contains(searchText))
                {
                    searchRow.clickAdd();
                    return getCurrentPage();
                }

            }
            else
            {
                if (null == userProfile)
                {
                    throw new IllegalArgumentException("User profile is null");
                }
                else if (searchRow.getUserName().contains(searchText))
                {
                    String fullName = searchRow.getUserName();
                    String[] name = fullName.split(" ");
                    userProfile.setfName(name[0]);

                    if (name.length > 1)
                        userProfile.setlName(name[1]);
                    else
                        userProfile.setlName("");

                    return searchRow.clickAdd();
                }

            }
        }
        throw new PageException("User with username containing - '" + searchText + "' not found");
    }

    /**
     * Verify if user or group exist in the search list.
     *
     * @param searchText
     * @return
     */
    public boolean isUserOrGroupPresentInSearchList(String searchText)
    {
        try
        {
            // By USERNAME_SPAN = By.cssSelector("h3>span");

            for (UserSearchRow element : searchUserAndGroup(searchText))
            {
                if (element.getUserName().contains(searchText))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("User with username containing - '" + searchText + "' not found", nse);
        }
        return false;
    }

    /**
     * Returns the error message if there is one when searching for a user or group.
     *
     * @param searchText
     * @return The error message.  Empty if there is no message.
     */
    public String getSearchErrorMessage(String searchText) throws UnsupportedOperationException
    {
        String message = "";
        try
        {
            driver.findElement(SEARCH_USER_INPUT).clear();
            driver.findElement(SEARCH_USER_INPUT).sendKeys(searchText);
            driver.findElement(SEARCH_USER_BUTTON).click();
            WebElement element = driver.findElement(By.cssSelector(".message"));
            if (element != null)
            {
                message = element.getText();
            }

        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("element not found", nse);
        }
        return message;
    }

    /**
     * Returns true if all the userNames are in the search results.
     *
     * @param searchText
     * @param userNames
     * @return
     */
    public boolean usersExistInSearchResults(String searchText, String... userNames)
    {
        boolean matchNames = false;
        List<UserSearchRow> results = searchUserAndGroup(searchText);
        List<String> resultNames = new ArrayList<>();

        for (UserSearchRow userSearchRow : results)
        {
            String name = userSearchRow.getUserName();

            name = name.substring(name.indexOf('(') + 1, name.indexOf(')'));

            if (name.startsWith("GROUP_"))
            {
                name = name.substring(6);
            }

            resultNames.add(name);
        }

        List<String> names = Arrays.asList(userNames);

        matchNames = resultNames.containsAll(names);

        return matchNames;
    }
}
