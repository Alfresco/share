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
package org.alfresco.po.share.site;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.RepositoryPage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Create folder page object, holds all element of the HTML page relating to
 * share's create folder page.
 * 
 * @author Michael Suzuki, Jamie Allison
 * @since 1.0
 */
public class NewFolderPage extends ShareDialogue
{
    private final By folderTitleCss = By.cssSelector("input[id$='default-createFolder_prop_cm_title']");
    private final By name = By.cssSelector("input[id$='prop_cm_name']");
    private final By descriptionLocator = By.cssSelector("textarea[id$='default-createFolder_prop_cm_description']");
    private final By submitButton = By.cssSelector("button[id$='default-createFolder-form-submit-button']");
    private final By cancelButton = By.cssSelector("button[id$='createFolder-form-cancel-button']");
    private final By NOTIFICATION_MESSAGE = By.cssSelector("div[style*='visible']>div>div>span.message");

    private final RenderElement folderTitleElement = getVisibleRenderElement(folderTitleCss);
    private final RenderElement nameElement = getVisibleRenderElement(name);
    private final RenderElement descriptionElement = getVisibleRenderElement(descriptionLocator);
    private final RenderElement submitButtonElement = getVisibleRenderElement(submitButton);
    private final RenderElement cancelButtonElement = getVisibleRenderElement(cancelButton);

    public enum Fields
    {
        NAME, TITLE, DESCRIPTION;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewFolderPage render(RenderTime timer)
    {
        elementRender(timer, folderTitleElement, nameElement, descriptionElement, submitButtonElement, cancelButtonElement);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewFolderPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @see #createNewFolder(String, String)
     */
    public HtmlPage createNewFolder(final String folderName)
    {
        return createNewFolder(folderName, null);
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolder(final String folderName, final String description)
    {
        typeName(folderName);
        typeDescription(description);
        WebElement okButton = driver.findElement(submitButton);
        okButton.click();
        // Wait till the pop up disappears
        waitUntilMessageAppearAndDisappear("Folder");
        //DocumentLibraryPage page = factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
        RepositoryPage page = factoryPage.instantiatePage(driver, RepositoryPage.class);
        page.setShouldHaveFiles(true);
        return page;
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @param folderTitle options folder Title
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolder(final String folderName, final String folderTitle, final String description)
    {
        if (folderName == null || folderName.isEmpty())
        {
            throw new UnsupportedOperationException("Folder Name input required.");
        }
        typeTitle(folderTitle);
        return createNewFolder(folderName, description);
    }

    /**
     * @see #createNewFolderWithValidation(String, String)
     */
    public HtmlPage createNewFolderWithValidation(final String folderName)
    {
        return createNewFolderWithValidation(folderName, null);
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolderWithValidation(final String folderName, final String description)
    {
        return createNewFolderWithValidation(folderName, null, description);
    }

    /**
     * Create a new folder action by completing and submitting the form.
     * 
     * @param folderName mandatory folder name
     * @param description optional folder description
     * @param folderTitle optional folder Title
     * @return {@link HtmlPage} page response
     */
    public HtmlPage createNewFolderWithValidation(final String folderName, final String folderTitle, final String description)
    {
        String validationMessage = "";

        validationMessage += typeNameWithValidation(folderName);
        validationMessage += typeTitle(folderTitle);
        validationMessage += typeDescription(description);

        if (validationMessage.isEmpty())
        {
            WebElement okButton = driver.findElement(submitButton);
            okButton.click();

            // Wait till the pop up disappears
            waitUntilMessageAppearAndDisappear("Folder");
            DocumentLibraryPage page = factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
            page.setShouldHaveFiles(true);
            return page;
        }

        HtmlPage page = getCurrentPage();
        if (page instanceof ShareDialogue)
        {
            return getCurrentPage();
        }
        return page;
    }

    /**
     * Clear & Type Folder Name on the Text box.
     * 
     * @param folderName
     */
    public void typeName(final String folderName)
    {
        if (StringUtils.isEmpty(folderName))
        {
            throw new IllegalArgumentException("Folder Name input required.");
        }
        clearAndTypeInput(name, folderName);
    }

    private String typeNameWithValidation(final String folderName)
    {
        String fName = (folderName == null ? "" : folderName);

        return clearAndTypeInput(name, fName);
    }

    /**
     * Clear & Type the Folder Title for box.
     * 
     * @param folderTitle
     */
    public String typeTitle(final String folderTitle)
    {
        if (folderTitle != null && !folderTitle.isEmpty())
        {
            return clearAndTypeInput(folderTitleCss, folderTitle);
        }
        return "";
    }

    /**
     * Clear & Type the Description for box.
     * 
     * @param description
     */
    public String typeDescription(final String description)
    {
        if (description != null && !description.isEmpty())
        {
            return clearAndTypeInput(descriptionLocator, description);
        }
        return "";
    }

    private String clearAndTypeInput(By by, String text)
    {
        WebElement element = driver.findElement(by);
        element.clear();
        element.sendKeys(text);

        return getValidationMessage(element);
    }

    /**
     * Mimics the action of clicking the cancel button.
     * 
     * @return {@link HtmlPage} Page Response.
     */
    public HtmlPage selectCancel()
    {
        WebElement cancelElement = driver.findElement(cancelButton);
        String id = cancelElement.getAttribute("id");
        cancelElement.click();
        waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     */
    protected void waitUntilMessageAppearAndDisappear(String text)
    {
        waitUntilMessageAppearAndDisappear(text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     * @param timeInSeconds - Time to wait in seconds.
     */
    protected void waitUntilMessageAppearAndDisappear(String text, long timeInSeconds)
    {
        // waitUntilVisible(By.cssSelector("div.bd>span.message"), text, timeInSeconds);
        waitUntilElementDisappears(By.cssSelector("div.bd>span.message"), timeInSeconds);
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
                message = getMessage(name);
                break;
            case TITLE:
                message = getMessage(folderTitleCss);
                break;
            case DESCRIPTION:
                message = getMessage(descriptionLocator);
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
     * Mimics the action of clicking the save button.
     */
    public void selectSubmitButton()
    {
        WebElement okButton = findAndWait(submitButton);
        okButton.click();
    }

    /**
     * Method finds notification message
     * 
     * @return notification message string value
     */
    public String getNotificationMessage()
    {
        try
        {
            WebElement notifMessage = findAndWait(NOTIFICATION_MESSAGE);
            return notifMessage.getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Time out finding notification message.", toe);
        }

    }

    public void type(String text)
    {
        clearAndTypeInput(name, text);
    }

}
