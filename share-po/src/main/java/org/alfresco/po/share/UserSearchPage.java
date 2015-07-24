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
package org.alfresco.po.share;

import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public UserSearchPage(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public UserSearchPage render(final long time)
    {
        return render(new RenderTime(time));
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
    public UserSearchPage searchFor(final String user)
    {
        // Null check
        if (user == null)
        {
            throw new UnsupportedOperationException("user name is required");
        }

        WebElement input = drone.findAndWait(By.cssSelector(USER_SEARCH_BOX));
        input.clear();
        input.sendKeys(user);
        WebElement button = drone.findAndWait(By.cssSelector(USER_SEARCH_BUTTON));
        button.click();
        return new UserSearchPage(drone);
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
            WebElement element = drone.find(By.cssSelector(USER_SEARCH_RESULTS_STATUS));
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
            return drone.find(By.cssSelector(USER_SEARCH_RESULTS_ROW)).isDisplayed();
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
            WebElement resultStatus = drone.find(By.cssSelector(USER_SEARCH_RESULTS_STATUS));
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
            WebElement newUserButton = drone.find(By.cssSelector(NEW_USER_BUTTON));
            newUserButton.click();
            return new NewUserPage(drone);
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
    public UploadFilePage selectUploadUserCSVFile()
    {
        try
        {
            WebElement button = drone.find(By.cssSelector(UPLOAD_USER_CSV_BUTTON));
            button.click();
        }
        catch (NoSuchElementException te)
        {
        }
        return new UploadFilePage(drone);
    }

    /**
     * Clicks on Upload User CSV File Button.
     * 
     * @return UserSearchPage
     */
    public UserSearchPage openUploadUserCSVFile()
    {
        try
        {
            WebElement button = drone.find(By.cssSelector(UPLOAD_USER_CSV_BUTTON));
            button.click();

        }
        catch (NoSuchElementException te)
        {
        }
        return new UserSearchPage(drone).render();
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
        WebElement upload = drone.findAndWait(CSV_UPLOAD_FILE);
        upload.sendKeys(filePath);//"file:///" +

        WebElement uploadButton = drone.findAndWait(CSV_CONFIRM_UPLOAD_BUTTON);
        uploadButton.click();

        return drone.getCurrentPage().render();

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
            List<WebElement> csvUsers = drone.findAndWaitForElements(CSV_UPLOADED_USERNAMES);
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
            drone.findAndWait(CSV_GO_BACK_BUTTON);
            if (drone.isElementDisplayed(CSV_NO_RECORDS_FOUND))
                return false;
            else if (drone.isElementDisplayed(CSV_UPLOADED_USERNAMES))
                return true;

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
    public UserSearchPage clickGoBack()
    {
        try
        {
            WebElement goBack = drone.find(CSV_GO_BACK_BUTTON);
            goBack.click();

        }
        catch (NoSuchElementException e)
        {
            logger.error("Go Back button is not present on the page");
        }
        return new UserSearchPage(drone).render();
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
            return drone.find(By.cssSelector("span.message")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (StaleElementReferenceException ser)
        {
            drone.refresh();
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
            WebElement errMessage = drone.findAndWait(ERROR_MESSAGE);

            if (errMessage != null && errMessage.isDisplayed())
            {
                errMessage.getText().equals("Enter at least 1 character(s)");
                drone.waitUntilElementDisappears(ERROR_MESSAGE, (WAIT_TIME_3000) / 1000);
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
    public UserProfilePage clickOnUser(String userName)
    {
        if (StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("user name is required");
        }

        try
        {
            drone.findAndWait(By.partialLinkText(userName)).click();
            return new UserProfilePage(drone);
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Unable to find the userName to open the userProfie Page for : " + userName);
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
            List<WebElement> usersList = drone.findAndWaitForElements(By.cssSelector("tr>td>div.yui-dt-liner>a"));

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
