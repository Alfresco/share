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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * User Search page object, holds all element of the html page relating to
 * share's Users page.
 * 
 * @author Meenal Bhave
 * @since 1.6.1
 */
public class UserSearchPage extends SharePage
{
    private static final Log logger = LogFactory.getLog(UserSearchPage.class);
    private static final String USER_SEARCH_BOX = "input[id$='admin-console_x0023_default-search-text']";
    private static final String USER_SEARCH_BUTTON = "button[id$='admin-console_x0023_default-search-button-button']";
    private static final String NEW_USER_BUTTON = "button[id$='admin-console_x0023_default-newuser-button-button']";
    private static final String UPLOAD_USER_CSV_BUTTON = "button[id$='admin-console_x0023_default-uploadusers-button-button']";
    private static final String USER_SEARCH_RESULTS_ROW = "tbody.yui-dt-data > tr";
    private static final String USER_SEARCH_RESULTS_STATUS = "div[id$='default-search-bar']";
    private static final By ERROR_MESSAGE = By.cssSelector("div.yui-dialog>div>div.bd>span.message");
    private static final By CSV_UPLOAD_FILE = By.cssSelector("input[id*='default-filedata-file']");
    private static final By CSV_CONFIRM_UPLOAD_BUTTON = By.xpath("//button[text()='Upload File']");
    private static final By CSV_UPLOADED_USERNAMES = By.cssSelector("td[class*='username']>div");
    private static final By CSV_NO_RECORDS_FOUND = By.xpath("//td[contains(@class, 'empty')]/div[text()='No records found.']");
    private static final By CSV_GO_BACK_BUTTON = By.cssSelector("button[id*='csv-goback-button-button']");

    @SuppressWarnings("unchecked")
    @Override
    public UserSearchPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                if (isSearchComplete() && !isMessageDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse)
            {
            }
            timer.end();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if Admin Console title is present on the page
     * 
     * @return true if exists
     */
    protected boolean isTitlePresent()
    {
        return isBrowserTitle("Admin Tools");
    }

    /**
     * Completes the search form on the user
     * finders page.
     * 
     * @param user String name
     * @return UserSearchPage page response
     */
    public HtmlPage searchFor(final String user)
    {
        // Null check
        if (user == null)
        {
            throw new UnsupportedOperationException("user name is required");
        }

        WebElement input = findAndWait(By.cssSelector(USER_SEARCH_BOX));
        input.clear();
        input.sendKeys(user);
        WebElement button = findAndWait(By.cssSelector(USER_SEARCH_BUTTON));
        button.click();
        return getCurrentPage();
    }

    /**
     * Checks if the search is complete by waiting for text 'Searching for' to dissapear.
     * 
     * @return true if search is complete
     */
    protected boolean isSearchComplete()
    {
        boolean searchComplete = false;
        try
        {
            WebElement element = driver.findElement(By.cssSelector(USER_SEARCH_RESULTS_STATUS));
            searchComplete = !element.getText().contains("Searching for");
        }
        catch (NoSuchElementException te)
        {
        }
        return searchComplete;
    }

    /**
     * Checks if results table is displayed
     * 
     * @return true if visible
     */
    private synchronized boolean isResultRowDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector(USER_SEARCH_RESULTS_ROW)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Checks the result message in the USER_SEARCH_RESULTS_STATUS.
     * 
     * @return String message
     */
    public String getResultsStatus()
    {
        String message = "";
        try
        {
            WebElement resultStatus = driver.findElement(By.cssSelector(USER_SEARCH_RESULTS_STATUS));
            message = resultStatus.getText();
        }
        catch (NoSuchElementException te)
        {
        }
        return message;
    }

    /**
     * Checks if no result message is displayed.
     * 
     * @return true if the no result message is not displayed
     */
    public boolean hasResults()
    {
        boolean hasResults = false;
        try
        {
            // Search for 0 results message
            String message = getResultsStatus();
            if (message != null)
            {
                hasResults = !(message.endsWith("found 0 results.") || message.equals("No Results."));
                if (hasResults)
                {
                    hasResults = isResultRowDisplayed();
                }
            }
        }
        catch (NoSuchElementException te)
        {
        }
        return hasResults;
    }

    /**
     * Clicks on New User button to invoke New User Page.
     * 
     * @return NewUserPage
     */
    public HtmlPage selectNewUser()
    {
        try
        {
            WebElement newUserButton = driver.findElement(By.cssSelector(NEW_USER_BUTTON));
            newUserButton.click();
            return getCurrentPage();
        }
        catch (NoSuchElementException te)
        {
        }
        throw new PageException("Not able to find the New User Link.");
    }

    /**
     * Clicks on Upload User CSV File Button.
     * 
     * @return NewUserPage
     */
    public HtmlPage selectUploadUserCSVFile()
    {
        try
        {
            WebElement button = driver.findElement(By.cssSelector(UPLOAD_USER_CSV_BUTTON));
            button.click();
        }
        catch (NoSuchElementException te)
        {
        }
        return getCurrentPage();
    }

    /**
     * Clicks on Upload User CSV File Button.
     * 
     * @return UserSearchPage
     */
    public HtmlPage openUploadUserCSVFile()
    {
        try
        {
            WebElement button = driver.findElement(By.cssSelector(UPLOAD_USER_CSV_BUTTON));
            button.click();

        }
        catch (NoSuchElementException te)
        {
        }
        return getCurrentPage();
    }

    /**
     * Open Upload User CSV Form. Select csv file and upload it
     * 
     * @param filePath -
     *            path to the csv file
     */
    public HtmlPage uploadCVSFile(String filePath)
    {
        openUploadUserCSVFile();
        WebElement upload = findAndWait(CSV_UPLOAD_FILE);
        upload.sendKeys(filePath);//"file:///" +

        WebElement uploadButton = findAndWait(CSV_CONFIRM_UPLOAD_BUTTON);
        uploadButton.click();

        return getCurrentPage();

    }

    /**
     * Get uploaded CVS user names.
     * CSV file was uploaded already and Upload Results page was opened
     * 
     * @return List<String> </>
     */
    public List<String> getUploadedCVSUsernames()
    {
        List<String> usernames = new ArrayList<>();
        if (isCSVResults())
        {
            List<WebElement> csvUsers = findAndWaitForElements(CSV_UPLOADED_USERNAMES);
            for (WebElement csvUser : csvUsers)
            {
                usernames.add(csvUser.getText());
            }
        }
        return usernames;
    }

    /**
     * Verify that Upload Results page contains any results
     * 
     * @return boolean if any results are displayed
     */
    private boolean isCSVResults()
    {
        try
        {
            findAndWait(CSV_GO_BACK_BUTTON);
            if (driver.findElement(CSV_NO_RECORDS_FOUND).isDisplayed())
            {
                return false;
            }
            else if (driver.findElement(CSV_UPLOADED_USERNAMES).isDisplayed())
            {
                return true;
            }
        }
        catch (TimeoutException ex)
        {
            return false;
        }
        return false;
    }

    /**
     * Click to the Go Back button
     * 
     * @return UserSearchPage
     */
    public HtmlPage clickGoBack()
    {
        try
        {
            WebElement goBack = driver.findElement(CSV_GO_BACK_BUTTON);
            goBack.click();

        }
        catch (NoSuchElementException e)
        {
            logger.error("Go Back button is not present on the page");
        }
        return getCurrentPage();
    }

    /**
     * Check if javascript message about successful user creation is displayed.
     * 
     * @return true if message displayed
     */
    protected boolean isMessageDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector("span.message")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (StaleElementReferenceException ser)
        {
            driver.navigate().refresh();
            return isMessageDisplayed();
        }
    }

    /**
     * Check if javascript message about empty user search string is displayed.
     * 
     * @return true if error message displayed
     */
    protected boolean isErrorDisplayed()
    {
        try
        {
            WebElement errMessage = findAndWait(ERROR_MESSAGE);

            if (errMessage != null && errMessage.isDisplayed())
            {
                errMessage.getText().equals("Enter at least 1 character(s)");
                waitUntilElementDisappears(ERROR_MESSAGE, (getDefaultWaitTime()) / 1000);
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    /**
     * Clicks on given userName present in userSearch List and it opens the User profile page.
     * 
     * @return UserProfilePage
     */
    public HtmlPage clickOnUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("user name is required");
        }
        By link = By.partialLinkText(userName);
        driver.findElement(link).click();
        //check link has gone
        driver.findElements(link);
        return factoryPage.instantiatePage(driver, UserProfilePage.class);
    }

    /**
     * Checks if user present in a search page
     * 
     * @param userName String
     * @return boolean
     */
    public boolean isUserPresent(String userName)
    {

        try
        {
            List<WebElement> usersList = findAndWaitForElements(By.cssSelector("tr>td>div.yui-dt-liner>a"));

            for (WebElement user : usersList)
            {
                if (user.getText().contains(userName))
                {
                    return true;
                }
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
}
