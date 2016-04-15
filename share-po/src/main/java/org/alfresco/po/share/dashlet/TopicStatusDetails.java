
/**
 * Holds topic status details in My discussions dashlet
 */
package org.alfresco.po.share.dashlet;

public class TopicStatusDetails
{
    private String creationTime;
    private String updateTime;
    private String numberOfReplies;
    private String replyDetails;

    /**
     * @param creationTime String
     * @param updateTime String
     */
    public TopicStatusDetails(String creationTime, String updateTime)
    {
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public String getCreationTime()
    {
        return creationTime;
    }

    public String getUpdateTime()
    {
        return updateTime;
    }

    public String getNumberOfReplies()
    {
        return numberOfReplies;
    }

    public String getReplyDetails()
    {
        return replyDetails;
    }

    public void setCreationTime(String creationTime)
    {
        this.creationTime = creationTime;
    }

    public void setUpdateTime(String updateTime)
    {
        this.updateTime = updateTime;
    }

    public void setNumberOfReplies(String numberOfReplies)
    {
        this.numberOfReplies = numberOfReplies;
    }

    public void setReplyDetails(String replyDetails)
    {
        this.replyDetails = replyDetails;
    }

}
