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
package org.alfresco.po.share.site.discussions;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of Topic form
 *
 * @author Marina Nenadovets
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTopicForm extends SharePage
{

    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By DEFAULT_CONTENT_TOOLBAR = By.cssSelector("div[id$='default-content_toolbargroup']>span");
    protected static final By CANCEL_BUTTON = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-cancel-button");
    protected static final By FORM_TITLE = By.cssSelector(".page-form-header>h1");
    protected static final By TITLE_FIELD = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-title");
    protected static final String TOPIC_FORMAT_IFRAME = ("template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-content_ifr");
    protected static final By SAVE_BUTTON = (By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-submit-button"));
    protected static final By TAG_INPUT = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-tag-input-field");
    protected static final By ADD_TAG_BUTTON = By.cssSelector("#template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-add-tag-button");
    protected static final String TOPIC_TAG = "//a[@class='taglibrary-action']/span[text()='%s']";


    public AbstractTopicForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public AbstractTopicForm render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Check if topic form is displayed or not.
     *
     * @return boolean
     */
    protected boolean isTopicFormDisplayed()
    {
        try
        {
            return findAndWait(CANCEL_BUTTON).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out finding " + CANCEL_BUTTON.toString(), toe);
            }
        }
        catch (ElementNotVisibleException visibleException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Element Not Visible: " + CANCEL_BUTTON.toString(), visibleException);
            }
        }
        return false;
    }

    /**
     * Check content tool bar is displayed.
     *
     * @return true if displayed
     */
    protected boolean isTinyMCEDisplayed()
    {
        try
        {
            return findAndWait(DEFAULT_CONTENT_TOOLBAR).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + DEFAULT_CONTENT_TOOLBAR.toString(), toe);
        }
        throw new PageException("Page is not rendered");
    }

    /**
     * Method to retrieve the title of a form
     *
     * @return String
     */

    public String getTitle()
    {
        try
        {
            return findAndWait(FORM_TITLE).getText();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find page title");
        }
        throw new PageException("Page is not rendered");
    }

    /**
     * Method to set String input in the field
     *
     * @param input WebElement
     * @param value String
     */

    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find " + input);
        }
    }

    /**
     * Method to click on any element by its locator
     *
     * @param locator By
     */
    protected void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    /**
     * Method to set Title field
     *
     * @param title String
     */
    public void setTitleField(final String title)
    {
        setInput(findAndWait(TITLE_FIELD), title);
    }

    /**
     * Insert text in topic text area.
     *
     * @param txtLines String
     */
    public void insertText(String txtLines)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txtLines);
            executeJavaScript(setCommentJs);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method for clicking Save button
     */
    public void clickSave()
    {
        WebElement saveButton = findAndWait(SAVE_BUTTON);
        try
        {
            saveButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find Save button");
        }
    }

    /**
     * Method to add tag
     *
     * @param tag String
     */
    protected void addTag(final String tag)
    {
        try
        {
            WebElement tagField = findAndWait(TAG_INPUT);
            tagField.clear();
            tagField.sendKeys(tag);
            driver.findElement(ADD_TAG_BUTTON).click();
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find tag input");
        }
    }

    /**
     * Method for removing tag
     * method validate by DiscussionsPageTest.removeTags
     *
     * @param tag String
     */
    protected void removeTag(String tag)
    {
        String tagXpath = String.format(TOPIC_TAG, tag);
        WebElement element;
        try
        {
            element = findAndWait(By.xpath(tagXpath));
            element.click();
            waitUntilElementDisappears(By.xpath(tagXpath), 3000);
        }
        catch (NoSuchElementException e)
        {
            logger.debug("Unable to find tag");
            throw new PageException("Unable to find tag " + tag + "");
        }
    }
}
