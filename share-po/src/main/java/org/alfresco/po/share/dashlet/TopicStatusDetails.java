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
     * @param createdBy
     * @param creationTime
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
