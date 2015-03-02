/*
 * Copyright (C) 2005-2015 Alfresco Software Limited
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

package org.alfresco.po.share.site.datalist.items;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * FeedBack Row Properties page object, holds all element of properties dialogue
 * Created by Sergiu Vidrascu on 05/01/15.
 */
public class VisitorFeedbackRowProperties extends ShareDialogue
{

        private Log logger = LogFactory.getLog(this.getClass());

        @RenderWebElement private static final By FORM_TITLE = By.cssSelector("div[id*='editDetails-dialogTitle']");
        private static final By COMMENT_FLAG = By.cssSelector("input[id*='ws_commentFlagged'][type='checkbox']");
        private static final By FEEDBACK_SUBJECT = By.cssSelector("input[id*='ws_feedbackSubject']");
        private static final By SAVE_BTN = By.cssSelector("button[id$='form-submit-button']");
        private static final By FORM_FIELDS = By.cssSelector(".form-field>input");

        public VisitorFeedbackRowProperties(WebDrone drone)
        {
                super(drone);
        }

        @SuppressWarnings("unchecked") @Override public VisitorFeedbackRowProperties render(RenderTime timer)
        {
                elementRender(timer, getVisibleRenderElement(FORM_TITLE));
                return this;
        }

        @SuppressWarnings("unchecked") @Override public VisitorFeedbackRowProperties render()
        {
                return render(new RenderTime(maxPageLoadingTime));
        }

        @SuppressWarnings("unchecked") @Override public VisitorFeedbackRowProperties render(long time)
        {
                return render(new RenderTime(time));
        }

        /**
         * Method to click comment Flag
         */
        public void clickCommentFlag()
        {
                try
                {
                        drone.find(COMMENT_FLAG).click();
                }
                catch (NoSuchElementException te)
                {
                        logger.debug("Unable to locate any element for visitor feedback list form");
                        throw new PageOperationException("Could not find the specified button. " + te.toString());
                }
        }

        public void setFeedbackSubject(String subject)
        {
                try
                {
                        WebElement element = drone.findAndWait(FEEDBACK_SUBJECT);
                        element.clear();
                        element.sendKeys(subject);
                }
                catch (TimeoutException e)
                {
                        throw new PageOperationException("Exceeded time to find feedback subject field. " + e.toString());
                }
        }

        /**
         * Method for clicking Save button
         */
        public void clickSave()
        {
                try
                {
                        drone.findAndWait(SAVE_BTN).click();
                }
                catch (TimeoutException te)
                {
                        throw new ShareException("Save button isn't displayed!");
                }
        }

        /**
         * Method to fill all the fields of an item
         *
         * @param data
         */
        public void setAllProperties(String data)
        {
                try
                {
                        List<WebElement> formFields = drone.findAndWaitForElements(FORM_FIELDS);
                        for (WebElement formField : formFields)
                        {
                                formField.clear();
                                formField.sendKeys(data);
                        }
                }
                catch (TimeoutException te)
                {
                        logger.error("Could not find the form fields");
                }
        }

        /**
         * Method for editing all properties
         *
         * @param newTitle
         */
        public void editAllProperties(String newTitle)
        {
                setAllProperties(newTitle);
                clickSave();
                waitUntilAlert();
        }

}
