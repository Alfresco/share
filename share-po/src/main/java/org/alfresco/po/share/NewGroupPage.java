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

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author nshah
 */
public class NewGroupPage extends SharePage
{
    private static final String TEXT_INPUT_IDENTIFIER = "input[id$='default-create-shortname']";
    private static final String TEXT_INPUT_DISPLAY_NAME = "input[id$='default-create-displayname']";
    private static final String BUTTON_CREATE_GROUP = "button[id$='-creategroup-ok-button-button']";
    private static final String BUTTON_CREATE_GROUP_CANCEL = "button[id$='-creategroup-cancel-button-button']";
    private static final String BUTTON_CREATE_ANOTHER_GROUP = "button[id$='-creategroup-another-button-button']";

    public enum ActionButton
    {
        CREATE_GROUP,
        CREATE_ANOTHER,
        CANCEL_GROUP;
    }

    protected NewGroupPage(WebDrone drone)
    {
        super(drone);

    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGroupPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(By.cssSelector(TEXT_INPUT_IDENTIFIER)), getVisibleRenderElement(By.cssSelector(BUTTON_CREATE_GROUP)),
                    getVisibleRenderElement(By.cssSelector(TEXT_INPUT_DISPLAY_NAME)), getVisibleRenderElement(By.cssSelector(BUTTON_CREATE_GROUP_CANCEL)));
        }
        catch (NoSuchElementException e)
        {
        }
        catch (TimeoutException e)
        {
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGroupPage render(long time)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGroupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private void setIdentifier(String identifier)
    {
        if (StringUtils.isEmpty(identifier))
        {
            throw new PageException("Enter value of Identifier");
        }

        WebElement input = drone.findAndWait(By.cssSelector(TEXT_INPUT_IDENTIFIER));
        input.clear();
        input.sendKeys(identifier);
    }

    private void setDisplayName(String displayName)
    {
        if (StringUtils.isEmpty(displayName))
        {
            throw new PageException("Enter value of DisplayName");
        }
        WebElement input = drone.findAndWait(By.cssSelector(TEXT_INPUT_DISPLAY_NAME));
        input.clear();
        input.sendKeys(displayName);
    }

    private HtmlPage clickButton(ActionButton groupButton)
    {
        switch (groupButton)
        {
            case CREATE_GROUP:
                drone.findAndWait(By.cssSelector(BUTTON_CREATE_GROUP)).click();
                waitUntilAlert();
                return new GroupsPage(drone);

            case CREATE_ANOTHER:
                drone.findAndWait(By.cssSelector(BUTTON_CREATE_ANOTHER_GROUP)).click();
                waitUntilAlert();
                return new NewGroupPage(drone);

            case CANCEL_GROUP:
                drone.findAndWait(By.cssSelector(BUTTON_CREATE_GROUP_CANCEL)).click();
                return new GroupsPage(drone);
        }
        throw new PageException("Wrong Page!!");

    }

    /**
     * @param identifier
     * @param displayName
     * @param groupButton
     * @return
     */
    public HtmlPage createGroup(String identifier, String displayName, ActionButton groupButton)
    {
        setIdentifier(identifier);
        setDisplayName(displayName);
        return clickButton(groupButton);
    }

}
