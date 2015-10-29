package org.alfresco.po.share;

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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * New User page object, holds all element of the html page relating to
 * share's New User page. Enterprise only feature for the time being
 *
 * @author Meenal Bhave
 * @since 1.6.1
 */
public class InviteToAlfrescoPage extends SharePage
{
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final By EMAILS_TEXTAREA = By.cssSelector("textarea[id$='alf-id1-emails']");
    private static final By MESSAGE_TEXTAREA = By.cssSelector("textarea[id$='alf-id1-message']");
    private static final By INVITE_BUTTON = By.cssSelector("button[id$='alf-id1-submit-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='alf-id1-cancel-button']");

    @SuppressWarnings("unchecked")
    @Override
    public InviteToAlfrescoPage render(RenderTime timer)
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
            if (isPageLoaded())
            {
                break;
            }
            timer.end();
        }

        return this;
    }

    /**
     * Checks if the invite button is displayed.
     *
     * @return true if button is displayed
     */
    protected boolean isPageLoaded()
    {
        boolean groupsFrameLoaded = false;
        try
        {
            WebElement element = findAndWait(INVITE_BUTTON);
            groupsFrameLoaded = element.isDisplayed();
        }
        catch (NoSuchElementException te)
        {
        }
        return groupsFrameLoaded;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InviteToAlfrescoPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Enter email addresses
     * 
     * @param userEmails String[]
     */
    public void inputEmailsForInvitation(String[] userEmails)
    {
        if (userEmails == null)
        {
            throw new UnsupportedOperationException("userEmail(s) for invitation cannot be null");
        }
        WebElement textArea = findAndWait(EMAILS_TEXTAREA);
        textArea.clear();
        for (String userEmail : userEmails)
        {
            if (userEmail.equals(""))
            {
                throw new UnsupportedOperationException("userEmail can be empty");
            }
            textArea.sendKeys(userEmail);
            textArea.sendKeys(Keys.ENTER);
        }
    }

    /**
     * Enter email message
     * 
     * @param emailMessage String
     */
    public void inputMessage(String emailMessage)
    {
        WebElement input = findAndWait(MESSAGE_TEXTAREA);
        input.clear();
        input.sendKeys(emailMessage);
    }

    public String getMessageText()
    {
        WebElement input = findAndWait(MESSAGE_TEXTAREA);
        return input.getText();
    }

    /**
     * Clicks on Invite button on the invite to Alfresco site form.
     *
     * @return HtmlPage
     */
    public HtmlPage selectInvite()
    {
        try
        {
            logger.info("Click Invite button");
            WebElement element = findAndWait(INVITE_BUTTON);
            element.click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to Click Cancel Create User Button.");
    }
    
    /**
     * Clicks on the Cancel button on the invite to Alfresco site form.
     *
     * @return UserSearchPage
     */
    public HtmlPage cancelInviteToAlfresco()
    {
        try
        {
            WebElement element = findAndWait(CANCEL_BUTTON);
            element.click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to Click Cancel Create User Button.");
    }
}
