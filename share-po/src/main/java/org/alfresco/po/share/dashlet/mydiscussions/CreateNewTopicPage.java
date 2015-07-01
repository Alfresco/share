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

package org.alfresco.po.share.dashlet.mydiscussions;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

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

    // topic text
    private static final String CREATE_NEW_TOPIC_FORMAT_IFRAME = "template_x002e_createtopic_x002e_discussions-createtopic_x0023_default-content_ifr";

    // topic tags
    private static final String TOPIC_TAG_INPUT = "input[id$='_discussions-createtopic_x0023_default-tag-input-field']";

    // add tags
    private static final String ADD_TOPIC_TAG_BUTTON = "button[id$='_discussions-createtopic_x0023_default-add-tag-button-button']";

    // save button
    private static final String SAVE_TOPIC_BUTTON = "button[id$='_discussions-createtopic_x0023_default-submit-button']";

    // cancel button
    private static final String CANCEL_TOPIC_BUTTON = "button[id$='_discussions-createtopic_x0023_default-cancel-button']";

    TinyMceEditor tinyMCEEditor = new TinyMceEditor(drone);

    public CreateNewTopicPage(WebDrone drone)
    {
        super(drone);
    }

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
    public CreateNewTopicPage render(long time)
    {
        return render(new RenderTime(time));
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
        tinyMCEEditor.setTinyMce(CREATE_NEW_TOPIC_FORMAT_IFRAME);
        return tinyMCEEditor;
    }

    /**
     * Gets page title
     */
    public String getPageTitle()
    {
        try
        {
            return drone.find(By.cssSelector(CREATE_NEW_TOPIC_HEADER)).getText();
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
            WebElement inputField = drone.find(By.cssSelector(CREATE_NEW_TOPIC_TITLE));
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
            WebElement inputField = drone.find(By.cssSelector(TOPIC_TAG_INPUT));
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
            drone.find(By.cssSelector(ADD_TOPIC_TAG_BUTTON)).click();
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
    public TopicDetailsPage saveTopic()
    {
        try
        {
            drone.findAndWait(By.cssSelector(SAVE_TOPIC_BUTTON)).click();
            waitUntilAlert();
            return new TopicDetailsPage(drone).render();
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
            drone.find(By.cssSelector(CANCEL_TOPIC_BUTTON)).click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to click on Save topic button on Create New Topic Page.", nse);

        }
        throw new PageOperationException("Cannot find Save button on Create New Topic Page.");
    }
}
