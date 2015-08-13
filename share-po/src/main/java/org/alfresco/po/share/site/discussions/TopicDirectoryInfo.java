/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site.discussions;

import org.alfresco.po.HtmlPage;

/**
 * @author Marina.Nenadovets
 */
public interface TopicDirectoryInfo
{
    /**
     * Click on View Topic
     *
     * @return TopicViewPage
     */
    HtmlPage viewTopic();

    /**
     * Click on Edit Topic
     *
     * @return NewTopicForm
     */
    HtmlPage editTopic();

    /**
     * Click on Delete Topic
     *
     * @return DiscussionsPage
     */
    HtmlPage deleteTopic();

    /**
     * Verify whether edit topic is displayed
     *
     * @return boolean
     */
    boolean isEditTopicDisplayed();

    /**
     * Verify whether delete topic is displayed
     *
     * @return boolean
     */
    boolean isDeleteTopicDisplayed();

    /**
     * Return count of replies if it's possible for this view.
     *
     * @return int
     */
    int getRepliesCount();

    /**
     * Mimic click on read link in footerBar if it's possible.
     *
     * @return TopicDetailsPage
     */
    HtmlPage clickRead();

    /**
     * Mimic click on Tag, if it's possible.
     *
     * @param tagName String
     * @return DiscussionsPage
     */
    HtmlPage clickOnTag(String tagName);
}
