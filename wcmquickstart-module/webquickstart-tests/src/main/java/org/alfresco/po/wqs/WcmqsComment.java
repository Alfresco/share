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

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Representation of comment from wcmqs site that can be used while leaving a comment to an article.
 *
 * @author Cristina Axinte
 */

public class WcmqsComment extends SharePage
{
    private final By NAME_FROM_COMMENT = By.cssSelector("ul.comments-wrapper h4");
    private final By DATE_OF_COMMENT = By.cssSelector("span.newslist-date");
    private final By TEXT_OF_COMMENT = By.cssSelector("span.comments-text");
    private final By REPORT_COMMENT = By.cssSelector("span.comments-report>a");
    private final By COMMENT_PLACEHOLDER = By.cssSelector("ul.comments-wrapper>li");
    protected WebElement commentPlaceholder;

    public WcmqsComment(WebDrone drone)
    {
        super(drone);
        this.commentPlaceholder = drone.findAndWait(COMMENT_PLACEHOLDER);
    }

    public WcmqsComment(WebDrone drone, String visitorName, String commentText)
    {
        super(drone);
        List<WebElement> commentSections = drone.findAndWaitForElements(COMMENT_PLACEHOLDER);
        for (WebElement commentElement : commentSections)
        {
            String name = commentElement.findElement(NAME_FROM_COMMENT).getText();
            String comment = commentElement.findElement(TEXT_OF_COMMENT).getText();
            if (name.equals(visitorName) && comment.equals(commentText))
            {
                this.commentPlaceholder = commentElement;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsComment render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsComment render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsComment render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method that returns visitor's name of the post content
     *
     * @return String
     */
    public String getNameFromContent()
    {
        try
        {
            return commentPlaceholder.findElement(NAME_FROM_COMMENT).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find name from comment. " + e.toString());
        }
    }

    /**
     * Method that returns the post content
     *
     * @return String
     */
    public String getCommentFromContent()
    {
        try
        {
            return commentPlaceholder.findElement(TEXT_OF_COMMENT).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find name from comment. " + e.toString());
        }
    }

    /**
     * Method that returns the post content
     *
     * @return String
     */
    public WcmqsBlogPostPage clickReportComment()
    {
        try
        {
            commentPlaceholder.findElement(REPORT_COMMENT).click();
            return new WcmqsBlogPostPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find report this post link. " + e.toString());
        }
    }

    /**
     * Method that returns the the number of comments
     *
     * @return String
     */
    public Integer getNumberOfCommentsOnPage()
    {
        try
        {
            return drone.findAndWaitForElements(COMMENT_PLACEHOLDER).size();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find report this post link. " + e.toString());
        }
    }


}
