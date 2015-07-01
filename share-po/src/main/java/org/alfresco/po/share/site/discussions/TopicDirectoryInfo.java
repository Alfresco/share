package org.alfresco.po.share.site.discussions;

import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;

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
    TopicViewPage viewTopic();

    /**
     * Click on Edit Topic
     *
     * @return NewTopicForm
     */
    NewTopicForm editTopic();

    /**
     * Click on Delete Topic
     *
     * @return DiscussionsPage
     */
    DiscussionsPage deleteTopic();

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
    TopicDetailsPage clickRead();

    /**
     * Mimic click on Tag, if it's possible.
     *
     * @param tagName String
     * @return DiscussionsPage
     */
    DiscussionsPage clickOnTag(String tagName);
}
