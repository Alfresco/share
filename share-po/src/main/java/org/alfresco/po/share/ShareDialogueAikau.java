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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Share Dialogue page object, where Share Dialogue uses Aikau Framework, rather than YUI.
 * Holds all the methods relevant to Share Dialogue Page
 * 
 * @author Meenal Bhave
 * @since 1.0
 */
public class ShareDialogueAikau extends ShareDialogue
{
    private static final Log LOGGER = LogFactory.getLog(ShareDialogueAikau.class);

    private static By SHARE_DIALOGUE_PARENT = By.cssSelector("div.dijitDialog");
    private static final By SHARE_DIALOGUE_HEADER = By.cssSelector("div>span.dijitDialogTitle");
    private static final By SHARE_DIALOGUE_BODY = By.cssSelector("div>div.dialog-body");
    private static final By BUTTON_CLOSE = By.cssSelector("div>span.dijitDialogCloseIcon");
    private static final By DIALOGUE_BUTTONS = By.cssSelector("div>div.footer>span>span.dijitButtonNode>span>span.dijitButtonText");
    protected static final String BUTTON_FIRST = ">div>.footer .alfresco-buttons-AlfButton:first-of-type";
    protected static final String BUTTON_LAST = ">div>.footer .alfresco-buttons-AlfButton:last-of-type";
    protected static final String BUTTON_OK = "div>.footer .alfresco-buttons-AlfButton:first-of-type>span";
    protected static final String BUTTON_CANCEL = "div>.footer .alfresco-buttons-AlfButton:last-of-type>span";
    protected static By ERROR_MSG_DIALOG = By.cssSelector("span.alfresco-notifications-AlfNotification__message"); 
    /**
     * Helper method to click on the Close button to return to the original page
     */

    @Override
    public HtmlPage clickClose()
    {
        try
        {
            findFirstDisplayedElement(BUTTON_CLOSE).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able find the close button ", e);
        }
        catch (StaleElementReferenceException ser)
        {
            LOGGER.info("StaleElementReferenceException while finding Close Button", ser);
            return clickClose();
        }
        return factoryPage.getPage(driver);
    }

    /**
     * Click Button with the name
     * 
     * @param buttonName
     * @return HtmlPage
     */
    public HtmlPage clickActionByName(String buttonName)
    {
        boolean buttonFound = false;
        try
        {
            List<WebElement> dialogButtons = findDisplayedElements(DIALOGUE_BUTTONS);

            // Iterate over the dialogButtons and click the button that matches the named dialog button name
            for (WebElement button : dialogButtons)
            {
                if (buttonName.equalsIgnoreCase(StringUtils.trim(button.getText())))
                {
                    button.click();
                    buttonFound = true;
                    break;
                }
            }

            if (buttonFound)
            {
                return factoryPage.getPage(driver);
            }
            else
            {
                throw new PageOperationException("Not able find the button " + buttonName);
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able find the button ", e);
        }
        catch (StaleElementReferenceException ser)
        {
            LOGGER.info("StaleElementReferenceException while finding Dialogue Button", ser);
            return clickActionByName(buttonName);
        }
    }

    /**
     * Helper method to return Parent WebElement for the Share Dialogue
     * 
     * @return WebElement
     */
    private WebElement getDialogue()
    {
        try
        {
            return findFirstDisplayedElement(SHARE_DIALOGUE_PARENT);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Dialogue not found: ", nse);
        }
    }

    /**
     * Helper method to return true if Share Dialogue is displayed
     * 
     * @return boolean <tt>true</tt> is Share Dialogue is displayed
     */
    @Override
    public boolean isShareDialogueDisplayed()
    {
        try
        {
            WebElement dialogue = getDialogue();
            if (dialogue != null && dialogue.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            LOGGER.info("Share Dialogue Aikau Style is not displayed", nse);
        }
        return false;
    }

    /**
     * Helper method to return the Share Dialogue Title
     * 
     * @return String
     */
    @Override
    public String getDialogueTitle()
    {
        try
        {
            WebElement shareDialogueHeader = findFirstDisplayedElement(SHARE_DIALOGUE_HEADER);
            return shareDialogueHeader.getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("Unable to find the css ", nse);
        }
    }

    /**
     * Helper method to return WebElement for the Share Dialogue
     * 
     * @return String
     */
    public String getDialogueMessage()
    {
        try
        {
            WebElement shareDialogueBody = findFirstDisplayedElement(SHARE_DIALOGUE_BODY);
            return shareDialogueBody.getText();
        }
        catch (NoSuchElementException nse)
        {
            throw new NoSuchElementException("Unable to find the css ", nse);
        }
    }

    /**
     * Helper method to click on the Cancel button to return to the original page
     */
    @Override
    public HtmlPage clickCancel()
    {
        WebElement button = findFirstDisplayedElement(By.cssSelector(BUTTON_CANCEL));
        button.click();
        return factoryPage.getPage(driver);
    }

    /**
     * Method returns true if the field identified by the selector is displayed
     * 
     * @param selector
     * @return true if the field is displayed
     */
    public boolean isFieldBeingDisplayed(By selector)
    {
        try
        {
            // Get the Field
            WebElement field = driver.findElement(selector);

            if (field.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
            LOGGER.info("Message not displayed: ", e);
        }
        return false;
    }

    /**
     * Checks if the Element specified is enabled.
     * 
     * @return true, if it is enabled
     */
    public boolean isElementEnabled(By selector)
    {
        WebElement element = driver.findElement(selector);
        return !element.getAttribute("class").contains("dijitDisabled");
    }

    /**
     * Checks if the checkbox type input field is selected.
     * 
     * @return true, if the field is selected
     */
    public boolean isCheckBoxSelected(By selector)
    {
        try
        {
            WebElement checkBoxParent = findAndWait(selector);

            WebElement checkBox = checkBoxParent.findElement(By.cssSelector("input"));

            return checkBox.isSelected();
        }
        catch (Exception e)
        {
            LOGGER.info("Exception while checking if field is selected", e);
        }
        return false;
    }

    /**
     * Select the checkbox field.
     * 
     * @return the void
     */
    public void selectCheckBox(By selector)
    {
        try
        {
            WebElement checkBoxParent = findAndWait(selector);

            WebElement checkBox = checkBoxParent.findElement(By.cssSelector("input"));
            checkBox.click();
        }
        catch (TimeoutException toe)
        {
            throw new PageOperationException("Not visible Element: checkbox: " + selector, toe);
        }
    }

}
