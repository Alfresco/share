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

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Page object to reflect Visitor Feedback list item
 *
 * @author Cristins.Axinte, Sergiu Vidrascu
 */

public class VisitorFeedbackRow extends SharePage
{
        private static Log logger = LogFactory.getLog(VisitorFeedbackRow.class);

        private static final By VISITOR_EMAIL_FIELD = By.cssSelector("td[class*='ws_visitorEmail']");
        private static final By FLAGGED_FIELD = By.cssSelector("td[class*='ws_commentFlagged']");
        private static final By COMMENT_FIELD = By.cssSelector("td[class*='ws_feedbackComment']");
        private static final By RELEVANT_ASSET_FIELD = By.cssSelector("td[class*='ws_relevantAssetAssoc']");
        private static final By VISITOR_NAME_FIELD = By.cssSelector("td[class*='ws_visitorName']");
        private static final By VISITOR_WEBSITE_FIELD = By.cssSelector("td[class*='ws_visitorWebsite']");
        private static final By DUPLICATE_BUTTON = By.cssSelector("a[Title=\"Duplicate\"]");
        private static final By EDIT_BUTTON = By.cssSelector("a[Title=\"Edit\"]");
        private static final By DUPLICATE_MESSAGE = By.xpath(".//span[contains(text(),'Item was duplicated')]");
        private WebElement webElement;

        public VisitorFeedbackRow(WebDrone drone)
        {
                super(drone);
        }

        /**
         * Constructor
         *
         * @param element {@link WebElement}
         * @param drone
         */
        public VisitorFeedbackRow(WebElement element, WebDrone drone)
        {
                super(drone);
                this.webElement = element;
                this.drone = drone;
        }

        @SuppressWarnings("unchecked") @Override public VisitorFeedbackRow render(RenderTime timer)
        {
                basicRender(timer);
                return this;
        }

        @SuppressWarnings("unchecked") @Override public VisitorFeedbackRow render()
        {
                return render(new RenderTime(maxPageLoadingTime));
        }

        @SuppressWarnings("unchecked") @Override public VisitorFeedbackRow render(long time)
        {
                return render(new RenderTime(time));
        }

        /**
         * Method to get visitor email
         *
         * @return String
         */
        public String getVisitorEmail()
        {
                try
                {
                        return webElement.findElement(VISITOR_EMAIL_FIELD).getText();
                }
                catch (NoSuchElementException te)
                {
                        logger.debug("Unable to locate any element for visitor feedback list form");
                        throw new ShareException("Unable to locate any element for visitor feedback list form");
                }
        }

        /**
         * Method to get flag of the comment
         *
         * @return String
         */
        public String getCommnetFlag()
        {
                try
                {
                        return webElement.findElement(FLAGGED_FIELD).getText();
                }
                catch (TimeoutException ex)
                {
                        throw new ShareException("Exceeded time to find the comment flag field", ex);
                }
        }

        /**
         * Method to get visitor's comment
         *
         * @return String
         */
        public String getVisitorComment()
        {
                try
                {
                        return webElement.findElement(COMMENT_FIELD).getText();
                }
                catch (TimeoutException ex)
                {
                        throw new ShareException("Exceeded time to find the visitor's comment", ex);
                }
        }

        /**
         * Method to get visitor's name
         *
         * @return String
         */
        public String getVisitorName()
        {
                try
                {
                        return webElement.findElement(VISITOR_NAME_FIELD).getText();
                }
                catch (TimeoutException ex)
                {
                        throw new ShareException("Exceeded time to find the visitor's name", ex);
                }
        }

        /**
         * Method to get visitor's website
         *
         * @return String
         */
        public String getVisitorWebsite()
        {
                try
                {
                        return webElement.findElement(VISITOR_WEBSITE_FIELD).getText();
                }
                catch (TimeoutException ex)
                {
                        throw new ShareException("Exceeded time to find the visitor's website", ex);
                }
        }

        /**
         * Method to get relevant asset file
         *
         * @return String
         */
        public String getRelevantAsset()
        {
                try
                {
                        return webElement.findElement(RELEVANT_ASSET_FIELD).getText();
                }
                catch (TimeoutException ex)
                {
                        throw new ShareException("Exceeded time to find the visitor's website", ex);
                }
        }

        /**
         * Method to click Duplicate on row
         *
         */
        public void clickDuplicateOnRow()
        {
                try
                {
                        webElement.click();
                        WebElement button = webElement.findElement(DUPLICATE_BUTTON);
                        drone.executeJavaScript(String.format("window.scrollTo('%s', 0)", button.getLocation().getX()));
                        button.click();
                }
                catch (NoSuchElementException te)
                {
                        logger.debug("Unable to locate any element for visitor feedback list form");
                        throw new PageOperationException("Could not find the specified button. " + te.toString());
                }
                catch (IllegalArgumentException ie)
                {
                        logger.debug("The Jscript in the function is not set");
                        throw new PageOperationException("Java script did not execute properly " + ie.toString());
                }
        }

        /**
         * Method to click Edit on row
         *
         */
        public void clickEditOnRow()
        {
                try
                {
                        webElement.click();
                        WebElement button = webElement.findElement(EDIT_BUTTON);
                        drone.executeJavaScript(String.format("window.scrollTo('%s', 0)", button.getLocation().getX()));
                        button.click();
                }
                catch (NoSuchElementException te)
                {
                        logger.debug("Unable to locate any element for visitor feedback list form");
                        throw new PageOperationException("Could not find the specified button. " + te.toString());
                }
                catch (IllegalArgumentException ie)
                {
                        logger.debug("The Jscript in the function is not set");
                        throw new PageOperationException("Java script did not execute properly " + ie.toString());
                }
        }

        /**
         * Method to verify if the Duplication message success appeared
         *
         * @return boolean
         */
        public boolean isDuplicateMessageDisplayed()
        {
                try
                {
                        return drone.findAndWait(DUPLICATE_MESSAGE).isDisplayed();
                }
                catch (TimeoutException e)
                {
                        return false;
                }
        }

}
