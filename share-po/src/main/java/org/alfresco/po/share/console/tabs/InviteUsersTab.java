package org.alfresco.po.share.console.tabs;

import org.alfresco.po.share.console.CloudConsolePage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
public class InviteUsersTab extends CloudConsolePage
{
    private static final By INVITE_BUTTON = By.cssSelector("li>a[href*='support/invite']");
    private static final By ADD_BUTTON = By.cssSelector("li>a[href*='invite/add']");

    private static final By EMAILS_TEXTAREA = By.cssSelector("textarea[id$='edit-invites-text']");
    private static final By BROWSE_BUTTON = By.cssSelector("input[id$='edit-invites-file']");
    private static final By START_BULK_INVITE_BUTTON = By.cssSelector("input[id$='edit-submit']");

    private static final By ANY_RESULTS = By.xpath("//div[contains(@class,'messages')]");
    private static final By SUCCESS_INVITES = By.xpath("//div[contains(@class,'messages')]/h2[contains(text(),'Status message')]/..");

    private static final RenderElement INVITE_BUTTON_RENDER = RenderElement.getVisibleRenderElement(INVITE_BUTTON);
    private static final RenderElement ADD_BUTTON_RENDER = RenderElement.getVisibleRenderElement(ADD_BUTTON);

    /**
     * Constructor.
     */
    public InviteUsersTab(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InviteUsersTab render(RenderTime timer)
    {
        elementRender(timer, INVITE_BUTTON_RENDER, ADD_BUTTON_RENDER);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InviteUsersTab render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InviteUsersTab render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private InviteUsersTab inputEmailsForInvitation(String[] userEmails)
    {
        if (userEmails == null)
        {
            throw new UnsupportedOperationException("userEmail(s) for invitation cannot be null");
        }
        WebElement textArea = drone.findAndWait(EMAILS_TEXTAREA);
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
        return this;
    }

    private Map<String, Boolean> getResults()
    {
        drone.findAndWait(ANY_RESULTS, maxPageLoadingTime);
        List<WebElement> successList;
        try
        {
            successList = drone.findAndWaitForElements(SUCCESS_INVITES);
        }
        catch (TimeoutException t)
        {
            // at the moment i should nit verify negative tests, if we need verify negative invites we should remove exception.
            throw new UnsupportedOperationException("No success invitations", t);
        }

        ArrayList<String> userSuccessList = new ArrayList<>();
        Map<String, Boolean> results = new HashMap<>();
        if (successList.size() != 1)
        {
            throw new UnsupportedOperationException("Users were not invited. Success message is not displayed");
        }
        else
        {
            String fullText = successList.get(0).getText();
            String[] separateUsers = fullText.split("\\n");
            for (String separateUser : separateUsers)
            {
                if (separateUser.contains("invited"))
                {
                    userSuccessList.add(separateUser.split(" ")[1]);
                }
            }
        }
        for (String successUser : userSuccessList)
        {
            results.put(successUser, true);
        }
        return results;
    }

    public Map<String, Boolean> executeCorrectBulkImport(String[] userEmails)
    {
        inputEmailsForInvitation(userEmails);
        WebElement startInviteButton = drone.findAndWait(START_BULK_INVITE_BUTTON);
        startInviteButton.click();
        return getResults();
    }

    public Map<String, Boolean> executeCorrectBulkImport(File file)
    {
        selectFileForUploading(file);
        WebElement startInviteButton = drone.findAndWait(START_BULK_INVITE_BUTTON);
        startInviteButton.click();
        return getResults();
    }

    private InviteUsersTab selectFileForUploading(File file)
    {
        if (file == null)
        {
            throw new UnsupportedOperationException("File cannot be null");
        }
        if (!file.exists())
        {
            throw new UnsupportedOperationException("File is not exists");
        }
        WebElement browse = drone.findAndWait(BROWSE_BUTTON);
        browse.sendKeys(file.getAbsolutePath());
        return this;
    }
}
