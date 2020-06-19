/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
 */
package org.alfresco.po.wqs;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by Lucian Tuca on 11/18/2014.
 */
public class WcmqsBlogPostPage extends WcmqsAbstractArticlePage
{
    private static final Logger logger = Logger.getLogger(WcmqsBlogPostPage.class);
    private final By CREATE_ARTICLE = By.cssSelector("a[class='alfresco-content-new']");
    private final By TITLE = By.xpath(".//div/h2");
    private final By CONTENT = By.xpath(".//div/div[2]/p");
    private final By DELETE_LINK = By.cssSelector("a[class=alfresco-content-delete]");
    private final By DELETE_CONFIRM_OK = By.xpath("//button[contains(text(),'Ok')]");
    private final By DELETE_CONFIRM_CANCEL = By.xpath("//button[contains(text(),'Cancel')]");
    private final By DELETE_CONFIRM_WINDOW = By.id("prompt_c");
    private final By DELETE_CONFIRM_TITLE = By.cssSelector("div[id='prompt_c']>div[id='prompt']>div.hd");
    private final By TOGGLE_EDIT_MARKERS = By.id("awe--show-hide-edit-markers-button");
    private final By ORIENTATION = By.id("WEF-Ribbon--ribbon-placement-button");
    private final By ORIENTATION_LEFT = By.cssSelector("div[class='bd']>ul[class='first-of-type']>li[index='1']>a");
    private final By ORIENTATION_TOP = By.cssSelector("div[class='bd']>ul[class='first-of-type']>li[index='0']>a");
    private final By ORIENTATION_RIGHT = By.cssSelector("div[class='bd']>ul[class='first-of-type']>li[index='2']>a");
    private final By ALFRESCO_WEB_EDITOR = By.id("wef-ribbon-container");
    private final By ALFRESCO_CONTENT_MARKERS = By.cssSelector("span[class='alfresco-content-marker']");
    private final By AWE_CREATE = By.id("awe--quickcreate-button");
    private final By AWE_CREATE_ARTICLE = By.cssSelector("div[class='bd']>ul[class='first-of-type']>li>a");
    private final By AWE_EDIT = By.id("awe--quickedit-button");
    private final By AWE_EDIT_ARTICLE = By.cssSelector("div[class='bd']>ul[class='first-of-type']>li>a");
    private final By PAGE_LOGO = By.cssSelector("#logo>a");
    private final By COMMENT_FORM = By.cssSelector(".blog-comment-fieldset");
    private final By VISITOR_NAME = By.cssSelector("input[name='visitorName']");
    private final By VISITOR_EMAIL = By.cssSelector("input[name='visitorEmail']");
    private final By VISITOR_WEBSITE = By.cssSelector("input[name='visitorWebsite']");
    private final By FEEDBACK_COMMENT = By.cssSelector("textarea.bc-textarea");
    private final By POST_BUTTON = By.cssSelector("input.bc-submit");
    private final By ADD_SUCCESS_MESSAGE = By.cssSelector("div.contact-success");
    private final By FORM_PROBLEMS_MESSAGE = By.cssSelector("div.contact-error");
    private final By INVALID_INPUT_MESSAGES = By.xpath("//span[contains(@class,\"contact-error-value\")]");
    private final By REPORT_POST = By.cssSelector(".comments-report>a");
    private final By COMMENT_FEEDBACK_SUBJECT = By.cssSelector(".comments-text");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public WcmqsBlogPostPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsBlogPostPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(CONTENT));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsBlogPostPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsBlogPostPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to click on Create article
     * 
     * @return WcmqsEditPage
     */
    public WcmqsEditPage createArticle()
    {
        try
        {
            drone.findAndWait(CREATE_ARTICLE).click();
            return new WcmqsEditPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to create article. " + e.toString());
        }
    }

    /**
     * Method that retuns the post title
     * 
     * @return String
     */
    public String getTitle()
    {
        try
        {
            return drone.findAndWait(TITLE).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the post title. " + e.toString());
        }
    }

    /**
     * Method that retuns the post content
     * 
     * @return String
     */
    public String getContent()
    {
        try
        {
            return drone.findAndWait(CONTENT).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find post content. " + e.toString());
        }
    }

    /**
     * Method that retuns the value of The name Form field
     * 
     * @return String
     */
    public String getVisitorName()
    {
        try
        {
            return drone.findAndWait(VISITOR_NAME).getAttribute("value");
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time find the requested field " + e.toString());
        }
    }

    /**
     * Method that types the visitor name
     */
    public void setVisitorName(String visitorName)
    {
        try
        {
            WebElement element = drone.findAndWait(VISITOR_NAME);
            element.clear();
            element.sendKeys(visitorName);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find name field. " + e.toString());
        }
    }

    /**
     * Method that retuns the value of The name Form field
     * 
     * @return String
     */
    public String getVisitorEmail()
    {
        try
        {
            return drone.findAndWait(VISITOR_EMAIL).getAttribute("value");
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time find the requested field " + e.toString());
        }
    }

    /**
     * Method that types the visitor email
     */
    public void setVisitorEmail(String visitorEmail)
    {
        try
        {
            WebElement element = drone.findAndWait(VISITOR_EMAIL);
            element.clear();
            element.sendKeys(visitorEmail);

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find email field. " + e.toString());
        }
    }

    /**
     * Method that retuns the value of The name Form field
     * 
     * @return String
     */
    public String getVisitorWebsite()
    {
        try
        {
            return drone.findAndWait(VISITOR_WEBSITE).getAttribute("value");
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time find the requested field " + e.toString());
        }
    }

    /**
     * Method that types the visitor website
     */
    public void setVisitorWebsite(String visitorWebsite)
    {
        try
        {
            drone.findAndWait(VISITOR_WEBSITE).sendKeys(visitorWebsite);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find email field. " + e.toString());
        }
    }

    /**
     * Presses the delete button while you are in blog editing
     */
    public void deleteArticle()
    {

        try
        {
            drone.findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
    }

    /**
     * Verifies if delete confirmation window is displayed
     * 
     * @return boolean
     */
    public boolean isDeleteConfirmationWindowDisplayed()
    {
        try
        {

            drone.waitForElement(DELETE_CONFIRM_WINDOW, SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            WebElement importMessage = drone.find(By.id("prompt_c"));
            return true;
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    public HtmlPage confirmArticleDelete()
    {
        try
        {
            drone.findAndWait(DELETE_CONFIRM_OK).click();
            Thread.sleep(1000);
        }
        catch (TimeoutException | InterruptedException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
        return FactoryWqsPage.resolveWqsPage(drone);
    }

    public void cancelArticleDelete()
    {
        try
        {
            drone.findAndWait(DELETE_CONFIRM_CANCEL).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
    }

    /**
     * Method that types the visitor comment
     */
    public void setVisitorComment(String comment)
    {
        try
        {
            drone.findAndWait(FEEDBACK_COMMENT).sendKeys(comment);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find email field. " + e.toString());
        }
    }

    /**
     * Method to click on Post Button
     * 
     * @return WcmqsBlogPostPage
     */
    public WcmqsBlogPostPage clickPostButton()
    {
        try
        {
            drone.findAndWait(POST_BUTTON).click();
            return this;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click the post button. " + e.toString());
        }
    }

    /**
     * Method to click on report the last added post
     * 
     * @return WcmqsBlogPostPage
     */
    public WcmqsBlogPostPage reportLastCreatedPost()
    {
        try
        {
            List<WebElement> messages = drone.findAndWaitForElements(REPORT_POST);
            messages.get(messages.size() - 1).click();
            return this;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to report the post article. " + e.toString());
        }
    }

    /**
     * Method to verify the add comment successful message
     */
    public boolean isAddCommentMessageDisplay()
    {
        try
        {
            WebElement message = drone.findAndWait(ADD_SUCCESS_MESSAGE);
            return message.isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the leave comment form is displayed
     */
    public boolean isLeaveCommentFormDisplayed()
    {
        try
        {
            WebElement message = drone.findAndWait(COMMENT_FORM);
            return message.isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify that the form main error message on invalid input is displayed
     * 
     * @return Boolean
     */
    public boolean isFormProblemsMessageDisplay()
    {
        try
        {
            WebElement message = drone.findAndWait(FORM_PROBLEMS_MESSAGE);
            return message.isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to get input field errors
     * 
     * @return WcmqsEditPage
     */
    public List<String> getFormErrorMessages()
    {
        try
        {
            ArrayList<String> errorString = new ArrayList<String>();
            List<WebElement> messages = drone.findAndWaitForElements(INVALID_INPUT_MESSAGES);
            for (WebElement message : messages)
            {
                errorString.add(message.getText());
            }
            return errorString;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the input field errors " + e.toString());
        }
    }

    public WcmqsComment getCommentSection(String visitorName, String commentText)
    {
        return new WcmqsComment(drone, visitorName, commentText);
    }

    public void clickToggleEditMarkers()
    {
        try
        {
            drone.findAndWait(TOGGLE_EDIT_MARKERS).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click Edit Toggle Markers. " + e.toString());
        }

    }

    public boolean isEditMarkersDisplayed()
    {
        try
        {
            drone.findAndWait(ALFRESCO_CONTENT_MARKERS);
            return true;
        }
        catch (TimeoutException e)
        {
            return false;
        }

    }

    public void changeOrientationLeft()
    {
        try
        {
            drone.findAndWait(ORIENTATION).click();
            drone.findAndWait(ORIENTATION_LEFT).click();

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click change orientation to LEFT. " + e.toString());
        }
    }

    public void changeOrientationTop()
    {
        try
        {
            drone.findAndWait(ORIENTATION).click();
            drone.findAndWait(ORIENTATION_TOP).click();

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click change orientation to TOP. " + e.toString());
        }
    }

    public void changeOrientationRight()
    {
        try
        {
            drone.findAndWait(ORIENTATION).click();
            drone.findAndWait(ORIENTATION_RIGHT).click();

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click change orientation to RIGHT. " + e.toString());
        }
    }

    public boolean isAWEOrientedLeft()
    {
        try
        {
            WebElement awe = drone.findAndWait(ALFRESCO_WEB_EDITOR);
            String aweClass = awe.getAttribute("class");

            if (aweClass.endsWith("left"))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Alfresco Web Editor. " + e.toString());
        }
    }

    public boolean isAWEOrientedTop()
    {
        try
        {
            WebElement awe = drone.findAndWait(ALFRESCO_WEB_EDITOR);
            String aweClass = awe.getAttribute("class");

            if (aweClass.endsWith("top"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Alfresco Web Editor. " + e.toString());
        }
    }

    public boolean isAWEOrientedRight()
    {
        try
        {
            WebElement awe = drone.findAndWait(ALFRESCO_WEB_EDITOR);
            String aweClass = awe.getAttribute("class");

            if (aweClass.endsWith("right"))
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Alfresco Web Editor. " + e.toString());
        }
    }

    public WcmqsEditPage clickAWECreateArticle()
    {
        try
        {
            drone.findAndWait(AWE_CREATE).click();
            drone.findAndWait(AWE_CREATE_ARTICLE).click();
            return new WcmqsEditPage(drone).render();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click AWE Create article" + e.toString());
        }

    }

    public WcmqsEditPage clickAWEEditArticle()
    {
        try
        {
            drone.findAndWait(AWE_EDIT).click();
            drone.findAndWait(AWE_EDIT_ARTICLE).click();
            return new WcmqsEditPage(drone).render();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to click AWE Edit article" + e.toString());
        }
    }

    /**
     * Method to get all feedback comments in
     * 
     * @return
     */
    public List<String> getFeedBackComments()
    {
        try
        {
            ArrayList<String> errorString = new ArrayList<String>();
            List<WebElement> messages = drone.findAndWaitForElements(COMMENT_FEEDBACK_SUBJECT);
            for (WebElement message : messages)
            {
                errorString.add(message.getText());
            }
            return errorString;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the feedback Subjects " + e.toString());
        }
    }

    public String getDeleteConfirmationTitle()
    {
        try
        {
            return drone.find(DELETE_CONFIRM_TITLE).getText();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("The Confirmation Delete Title was not found", nse);
        }
        return null;
    }

}
