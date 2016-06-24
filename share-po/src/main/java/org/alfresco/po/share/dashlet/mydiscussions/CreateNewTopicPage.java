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

package org.alfresco.po.share.dashlet.mydiscussions;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * CreateNewTopicPage page object, holds all element of the HTML page relating to Create New Topic Page
 * 
 * @author jcule
 */
public class CreateNewTopicPage extends SharePage
{

    private static Log logger = LogFactory.getLog(CreateNewTopicPage.class);

    // Create New Topic title header
    private static final String CREATE_NEW_TOPIC_HEADER = "div[id$='_discussions-createtopic'] h1";

    // topic title
    private static final String CREATE_NEW_TOPIC_TITLE = "input[id$='_discussions-createtopic_x0023_default-title']";

    // topic tags
    private static final String TOPIC_TAG_INPUT = "input[id$='_discussions-createtopic_x0023_default-tag-input-field']";

    // add tags
    private static final String ADD_TOPIC_TAG_BUTTON = "button[id$='_discussions-createtopic_x0023_default-add-tag-button-button']";

    // save button
    private static final String SAVE_TOPIC_BUTTON = "button[id$='_discussions-createtopic_x0023_default-submit-button']";

    // cancel button
    private static final String CANCEL_TOPIC_BUTTON = "button[id$='_discussions-createtopic_x0023_default-cancel-button']";

    TinyMceEditor tinyMCEEditor;

    @SuppressWarnings("unchecked")
    @Override
    public CreateNewTopicPage render(RenderTime timer)
    {

        elementRender(timer, 
                getVisibleRenderElement(By.cssSelector(CREATE_NEW_TOPIC_HEADER)),
                getVisibleRenderElement(By.cssSelector(CREATE_NEW_TOPIC_TITLE)),
                getVisibleRenderElement(By.cssSelector(SAVE_TOPIC_BUTTON)));

        return this;

    }


    @SuppressWarnings("unchecked")
    @Override
    public CreateNewTopicPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Get TinyMCEEditor object to navigate TinyMCE functions.
     * TODO: move setText() from ConfigureSiteNoticeTinyMceEditor to TinyMceEditor
     * 
     * @return TinyMceEditor
     */
    public TinyMceEditor getTinyMCEEditor()
    {
        tinyMCEEditor.setTinyMce();
        return tinyMCEEditor;
    }

    /**
     * Gets page title
     */
    public String getPageTitle()
    {
        try
        {
            return driver.findElement(By.cssSelector(CREATE_NEW_TOPIC_HEADER)).getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find page title.", nse);
        }
        throw new PageOperationException("Error in finding the css for create new topic page title.");
    }

    public HtmlPage enterTopicTitle(String title)
    {
        try
        {
            WebElement inputField = driver.findElement(By.cssSelector(CREATE_NEW_TOPIC_TITLE));
            inputField.clear();
            inputField.sendKeys(title);
            return this;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to enter topic title.", nse);
        }
        throw new PageOperationException("Error in finding the css for topic title input field.");

    }

    /**
     * Enters tag value
     * 
     * @param tag String
     */
    public HtmlPage fillTagField(String tag)
    {
        try
        {
            WebElement inputField = driver.findElement(By.cssSelector(TOPIC_TAG_INPUT));
            inputField.clear();
            inputField.sendKeys(tag);
            return this;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to enter topic tag.", nse);
        }
        throw new PageOperationException("Error in finding the css for topic tag input field.");
    }

    /**
     * Clicks on Add Tag button
     */
    public HtmlPage addTag()
    {
        try
        {
            driver.findElement(By.cssSelector(ADD_TOPIC_TAG_BUTTON)).click();
            return this;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to click on Add Tag button on Create New Topic Page.", nse);
        }
        throw new PageOperationException("Cannot find Add Tag button on Create New Topic Page.");
    }

    /**
     * Clicks on Save button
     */
    public HtmlPage saveTopic()
    {
        try
        {
            findAndWait(By.cssSelector(SAVE_TOPIC_BUTTON)).click();
            waitUntilAlert();
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to click on Save topic button on Create New Topic Page.", nse);

        }
        throw new PageOperationException("Cannot find Save button on Create New Topic Page.");
    }

    /**
     * Clicks on Save button
     */
    public HtmlPage cancelTopic()
    {
        try
        {
            driver.findElement(By.cssSelector(CANCEL_TOPIC_BUTTON)).click();
            return getCurrentPage();

        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to click on Save topic button on Create New Topic Page.", nse);

        }
        throw new PageOperationException("Cannot find Save button on Create New Topic Page.");
    }
}
