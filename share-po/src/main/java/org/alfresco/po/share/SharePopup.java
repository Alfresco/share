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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Share Error popup page object, holds all the methods relevant to Share Error popup
 * 
 * @author Meenal Bhave
 * @since 1.7.0
 */
public class SharePopup extends SharePage
{
    private static final String FAILURE_PROMPT = "div[id='prompt']";
    private static final String DEFAULT_BUTTON = "span.yui-button";
    private static final String ERROR_BODY = "div.bd";
    private static final By BUTTON_TAG_NAME = By.tagName("button");
    private By CLOSE_BUTTON = By.cssSelector("span.dijitDialogCloseIcon");

    @SuppressWarnings("unchecked")
    @Override
    public SharePopup render(RenderTime timer)
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
            if (isShareMessageDisplayed())
            {
                break;
            }
            timer.end();
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SharePopup render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Helper method to handle the error popup displayed in Share
     * Clicks on the OK button to return to the original page
     * 
     * @return SharePage
     * @throws ShareException with the Error message
     */
    public HtmlPage handleMessage() throws ShareException
    {
        if (isShareMessageDisplayed())
        {
            String message = getShareMessage();
            clickOK().render();
            throw new ShareException(message);
        }

        return getCurrentPage();
    }

    /**
     * Helper method to click on the OK button to return to the original page
     */
    public HtmlPage clickOK()
    {
        WebElement popupMessage = getErrorPromptElement();
        popupMessage.findElement(By.cssSelector(DEFAULT_BUTTON)).click();
        return getCurrentPage();
    }

    /**
     * Helper method to get the error message in the Share Error Popup
     * 
     * @return String Share Error Message displayed in the popup
     */
    public String getShareMessage()
    {
        try
        {
            WebElement errorMessage = getErrorPromptElement();
            return errorMessage.findElement(By.cssSelector(ERROR_BODY)).getText();
        }
        catch (NoSuchElementException nse)
        {
        }
        return null;
    }

    /**
     * Helper method to return true if Share Error popup is displayed
     * 
     * @return boolean <tt>true</tt> is Share Error popup is displayed
     */
    public boolean isShareMessageDisplayed()
    {
        try
        {
            WebElement message = getErrorPromptElement();
            if (message != null && message.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Helper method to return WebElement for the failure prompt
     * 
     * @return WebElement
     */
    private WebElement getErrorPromptElement()
    {
        try
        {
            WebElement errorMessage = driver.findElement(By.cssSelector(FAILURE_PROMPT));
            return errorMessage;
        }
        catch (NoSuchElementException nse)
        {
            return null;
        }
    }

    /**
     * Clicks on the No button for no upgrade document for google docs.
     *
     * @return HtmlPage
     */
    public HtmlPage cancelNo()
    {
        try
        {
            WebElement prompt = findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement cancelButton = findButton("No", elements);
            cancelButton.click();
        }
        catch (TimeoutException nse)
        {
            throw new TimeoutException("upgrade prompt was not found", nse);
        }
        waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Clicks on the submit button to allow upgrade document for google docs.
     *
     */
    public void clickYes()
    {
        try
        {
            WebElement prompt = findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("Yes", elements);
            okButton.click();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("upgrade prompt was not found", te);
        }
        catch (NoSuchElementException te)
        {
            throw new PageOperationException("authorisation prompt was not found", te);
        }
    }
    
    
    /**
     * Method to close the SharePopup
     */
    public void clickClose()
    {
        try
        {
            WebElement closeButton = findFirstDisplayedElement(CLOSE_BUTTON);
            closeButton.click();
        }
        catch(TimeoutException te)
        {
            throw new PageOperationException("Unable to Close the Popup", te);
        }
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
            List<WebElement> dialogButtons = findDisplayedElements(By.cssSelector(DEFAULT_BUTTON));

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
            return clickActionByName(buttonName);
        }
    }

}
