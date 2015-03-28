/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.site.contentrule.createrules;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Aliaksei Boole
 */
public class EmailMessageForm extends HtmlElement
{
    class AddRecipientsForm extends HtmlElement
    {
        private final By ADD_RECIPIENTS_FORM = By.xpath("//div[contains(@class,'email-form authority-picker')]");

        private final By SEARCH_INPUT = By.xpath("//input[contains(@id,'emailForm-authority-finder-search-text')]");
        private final By SEARCH_BUTTON = By.xpath("//button[contains(@id,'emailForm-authority-finder-authority-search-button-button')]");
        private final By NO_RESULTS = By.xpath("//td[@class='yui-dt-empty']/div");
        private final By SEARCH_RESULT = By.xpath("//tr[contains(@class,'yui-dt-rec')]//h3[@class='itemname']");
        private final By ADD_BUTTON_RELATIVE = By.xpath("./../../../td[3]//button");


        public AddRecipientsForm(WebDrone drone)
        {
            super(drone);
        }

        /**
         * Search user and Add to recipients
         *
         * @param userName
         */
        public void addUserToRecipients(String userName)
        {
            try
            {
                fillField(userName, SEARCH_INPUT);
                drone.findAndWait(SEARCH_BUTTON).click();
                List<WebElement> searchResults = drone.findAndWaitForElements(SEARCH_RESULT);
                for (WebElement searchResult : searchResults)
                {
                    if (searchResult.getText().contains(userName))
                    {
                        searchResult.findElement(ADD_BUTTON_RELATIVE).click();
                    }
                }
            }
            catch (TimeoutException e)
            {
                throw new PageOperationException("Any users didn't find with search query[" + userName + "]");
            }
        }

        /**
         * Check that form display on page.
         *
         * @return true - if form displayed.
         */
        public boolean isDisplay()
        {
            try
            {
                return drone.findAndWait(ADD_RECIPIENTS_FORM, 2000).isDisplayed();
            }
            catch (TimeoutException e)
            {
                return false;
            }
        }
    }

    private static final By FORM = By.xpath("//div[contains(@id,'-emailForm-dialog') and contains(@class,'email-form')]");

    private static final By OK_BUTTON = By.xpath("//button[contains(@id,'emailForm-ok-button-button')]");
    private static final By CANCEL_BUTTON = By.xpath("//button[contains(@id,'emailForm-cancel-button-button')]");
    private static final By CLOSE_X = By.xpath("//div[contains(@id,'-emailForm-dialog')]/a");

    private static final By SELECT_TO = By.xpath("//button[contains(@id,'emailForm-selectRecipients-button-button')]");
    private static final By SUBJECT_INPUT = By.xpath("//input[contains(@id,'emailForm-subject')]");
    private static final By MESSAGE_TEXT_AREA = By.xpath("//textarea[contains(@id,'emailForm-message')]");
    private static final By USERS_WHO_MUST_GET_EMAIL = By.xpath("//a[@class='email-recipient-action']/span[1]");
    private static final By REMOVE_USER_ICON_RELATIVE = By.xpath("./../span[2]");

    public EmailMessageForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Fill field with email subject.
     *
     * @param subject email subject
     */
    public void fillSubjectField(String subject)
    {
        fillField(subject, SUBJECT_INPUT);
    }

    /**
     * Fill email message body
     *
     * @param text email message body
     */
    public void fillMessageArea(String text)
    {
        fillField(text, MESSAGE_TEXT_AREA);
    }

    /**
     * Check that usera with name 'userName' added to recipients
     *
     * @param userName -username
     * @return - true if added.
     */
    public boolean isUserAddedToRecipients(String userName)
    {
        return foundUserElementInRecipients(userName) != null;
    }

    /**
     * Remove user by name from recipients if user don't found throw exceptions.
     *
     * @param userName
     */
    public void removeUserFromRecipients(String userName)
    {
        this.isDisplay();
        WebElement userElement = foundUserElementInRecipients(userName);
        if (userElement != null)
        {
            drone.mouseOver(userElement);
            userElement.findElement(REMOVE_USER_ICON_RELATIVE).click();
        }
        else
        {
            throw new PageOperationException("User[" + userName + "] don't found in recipients.");
        }
    }

    private WebElement foundUserElementInRecipients(String userName)
    {
        checkNotNull(userName);
        List<WebElement> addedUsersList = drone.findAndWaitForElements(USERS_WHO_MUST_GET_EMAIL);
        for (WebElement addedUserElement : addedUsersList)
        {
            if (addedUserElement.getText().startsWith(userName))
            {
                return addedUserElement;
            }
        }
        return null;
    }

    /**
     * Search user and Add to recipients
     *
     * @param userName
     */
    public void addUserToRecipients(String userName)
    {
        drone.findAndWait(SELECT_TO).click();
        AddRecipientsForm addRecipientsForm = new AddRecipientsForm(drone);
        if (addRecipientsForm.isDisplay())
        {
            addRecipientsForm.addUserToRecipients(userName);
        }
    }

    /**
     * Mimic click on OK button.
     */
    public void clickOk()
    {
        click(OK_BUTTON);
    }

    /**
     * Mimic click on Cancel button.
     */
    public void clickCancel()
    {
        click(CANCEL_BUTTON);
    }

    /**
     * Mimic click on Close 'x' button.
     */
    public void clickClose()
    {
        click(CLOSE_X);
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(String text, By input)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(input);
        inputField.clear();
        inputField.sendKeys(text);
    }

    /**
     * Check that form display on page.
     *
     * @return true - if form displayed.
     */
    public boolean isDisplay()
    {
        try
        {
            return drone.findAndWait(FORM, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }
}
