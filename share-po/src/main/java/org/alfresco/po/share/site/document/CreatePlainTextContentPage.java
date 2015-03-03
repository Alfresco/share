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
package org.alfresco.po.share.site.document;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Create content page object, Where user can create content.
 *
 * @author Shan Nagarajan,Subashni Prasanna
 * @since 1.6.1
 */
public class CreatePlainTextContentPage extends InlineEditPage
{
    protected static final By NAME = By.cssSelector("input[id$='default_prop_cm_name']");
    protected static final By TITLE = By.cssSelector("input[id$='default_prop_cm_title']");
    protected static final By DESCRIPTION = By.cssSelector("textarea[id$='default_prop_cm_description']");
    protected By CONTENT = By.cssSelector("textarea[id$='default_prop_cm_content']");

    protected static final By SUBMIT_BUTTON = By.cssSelector("button[id$='form-submit-button']");
    protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='form-cancel-button']");

    public enum Fields
    {
        NAME, TITLE, DESCRIPTION, CONTENT;
    }

    public CreatePlainTextContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreatePlainTextContentPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(NAME), getVisibleRenderElement(TITLE), getVisibleRenderElement(DESCRIPTION),
                getVisibleRenderElement(CONTENT), getVisibleRenderElement(SUBMIT_BUTTON), getVisibleRenderElement(CANCEL_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreatePlainTextContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public CreatePlainTextContentPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Create the content with name, title and description.
     *
     * @param name        - The Name of the Document
     * @param title       - The Title of the Document
     * @param description - Description
     * @param details     - Document Content
     * @return {@link DocumentDetailsPage}
     */
    public HtmlPage create(ContentDetails details)
    {
        if (details == null || details.getName() == null || details.getName().trim().isEmpty())
        {
            throw new UnsupportedOperationException("Name can't be null or empty");
        }

        createContent(details);
        WebElement createButton = drone.findAndWait(SUBMIT_BUTTON);
        String id = createButton.getAttribute("id");
        createButton.click();
        drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);

    }

    /**
     * Create the content with name, title and description. Checks the form fields for validation messages.
     * If there are any it does not save the form but returns it. If there are no validation messages it
     * saves the form and returns {@link CreatePlainTextContentPage}.
     *
     * @param details A ContentDetails object containing name, title description and content. The name field is mandatory.
     * @return If there are no validation messages {@link DocumentDetailsPage}, otherwise {@link CreatePlainTextContentPage}
     */
    public HtmlPage createWithValidation(ContentDetails details)
    {
        if (details == null)
        {
            throw new UnsupportedOperationException("ContentDetails can't be null");
        }

        createContent(details);

        boolean validationPresent = isMessagePresent(NAME);
        validationPresent = validationPresent || isMessagePresent(TITLE);
        validationPresent = validationPresent || isMessagePresent(DESCRIPTION);
        validationPresent = validationPresent || isMessagePresent(CONTENT);

        if (!validationPresent)
        {
            WebElement createButton = drone.findAndWait(SUBMIT_BUTTON);
            createButton.click();
            drone.waitUntilElementDisappears(SUBMIT_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Cancel button interaction on the form
     *
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage cancel(ContentDetails details)
    {
        createContent(details);
        WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
        cancelButton.click();
        return new DocumentLibraryPage(drone);
    }

    /**
     * Click the Cancel button without completing the form
     *
     * @return
     */
    public HtmlPage cancel()
    {
        WebElement cancelButton = drone.findAndWait(CANCEL_BUTTON);
        cancelButton.click();
        return FactorySharePage.resolvePage(drone);
    }

    protected void createContent(ContentDetails details)
    {
        if (details != null)
        {
            if (details.getName() != null)
            {
                WebElement nameElement = drone.find(NAME);
                nameElement.clear();
                nameElement.sendKeys(details.getName());
            }

            if (details.getTitle() != null)
            {
                WebElement titleElement = drone.find(TITLE);
                titleElement.clear();
                titleElement.sendKeys(details.getTitle());
            }

            if (details.getDescription() != null)
            {
                WebElement descriptionElement = drone.find(DESCRIPTION);
                descriptionElement.clear();
                descriptionElement.sendKeys(details.getDescription());
            }
            createContentField(details);
        }
    }

    protected void createContentField(ContentDetails details)
    {
        if (details != null && details.getContent() != null)
        {
            WebElement contentElement = drone.find(CONTENT);
            contentElement.clear();
            contentElement.sendKeys(details.getContent());
        }
    }

    /**
     * Returns the validation message, if any, for the given Field.
     *
     * @param field The reqired field
     * @return The validation message or an empty string if there is no message.
     */
    public String getMessage(Fields field)
    {
        String message = "";

        switch (field)
        {
            case NAME:
                message = getMessage(NAME);
                break;
            case TITLE:
                message = getMessage(TITLE);
                break;
            case DESCRIPTION:
                message = getMessage(DESCRIPTION);
                break;
            case CONTENT:
                message = getMessage(CONTENT);
                break;
        }

        return message;
    }

    private String getMessage(By locator)
    {
        String message = "";

        try
        {
            message = getValidationMessage(locator);
        }
        catch (NoSuchElementException e)
        {
        }

        return message;
    }

    /**
     * Returns a map of validation messages for all the fields in the form.
     *
     * @param field The reqired field
     * @return The validation message or an empty string if there is no message.
     */
    public Map<Fields, String> getMessages()
    {
        Map<Fields, String> messages = new HashMap<>();

        String message = getMessage(NAME);
        if (message.length() > 0)
        {
            messages.put(Fields.NAME, message);
        }

        message = getMessage(TITLE);
        if (message.length() > 0)
        {
            messages.put(Fields.TITLE, message);
        }

        message = getMessage(DESCRIPTION);
        if (message.length() > 0)
        {
            messages.put(Fields.DESCRIPTION, message);
        }

        message = getMessage(CONTENT);
        if (message.length() > 0)
        {
            messages.put(Fields.CONTENT, message);
        }

        return messages;
    }

    protected boolean isMessagePresent(By locator)
    {
        String message = getMessage(locator);

        if (message.length() > 0)
        {
            return true;
        }

        return false;
    }
}
