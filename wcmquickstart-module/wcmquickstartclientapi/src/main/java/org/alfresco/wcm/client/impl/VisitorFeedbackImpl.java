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
package org.alfresco.wcm.client.impl;

import java.util.Date;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.AssetFactory;
import org.alfresco.wcm.client.VisitorFeedback;

public class VisitorFeedbackImpl implements VisitorFeedback
{
    private AssetFactory assetFactory;

    private String id;
    private String assetId;
    private String comment;
    private String feedbackType;
    private String subject;
    private Integer rating;
    private String visitorEmail;
    private String visitorName;
    private String visitorWebsite;
    private boolean commentFlagged;
    private Date postTime;
    private String successPage;
    
    private transient Asset asset;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAssetId()
    {
        return assetId;
    }

    public void setAssetId(String assetId)
    {
        this.assetId = assetId;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public Integer getRating()
    {
        return rating;
    }

    public void setRating(Integer rating)
    {
        this.rating = rating;
    }

    public String getVisitorEmail()
    {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail)
    {
        this.visitorEmail = visitorEmail;
    }

    public String getVisitorName()
    {
        return visitorName;
    }

    public void setVisitorName(String visitorName)
    {
        this.visitorName = visitorName;
    }

    public String getVisitorWebsite()
    {
        return visitorWebsite;
    }

    public void setVisitorWebsite(String visitorWebsite)
    {
        this.visitorWebsite = visitorWebsite;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getFeedbackType()
    {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType)
    {
        this.feedbackType = feedbackType;
    }

    public void setAssetFactory(AssetFactory assetFactory)
    {
        this.assetFactory = assetFactory;
    }

    @Override
    public Asset getAsset()
    {
        if (asset == null)
        {
            if (assetId != null)
            {
                asset = assetFactory.getAssetById(assetId);
            }
        }
        return asset;
    }

    @Override
    public void setAsset(Asset asset)
    {
        if (asset != null)
        {
            assetId = asset.getId();
        }
        else
        {
            assetId = null;
        }
        this.asset = asset;
    }

    @Override
    public boolean isCommentFlagged()
    {
        return commentFlagged;
    }

    @Override
    public void setCommentFlagged(boolean flagged)
    {
        this.commentFlagged = flagged;
    }

    @Override
    public Date getPostTime()
    {
        return postTime;
    }

    public void setPostTime(Date postTime)
    {
        this.postTime = postTime;
    }

	public void setSuccessPage(String success)
    {
	    this.successPage = success;
    }

    @Override
	public String getSuccessPage()
    {
	    return successPage;
    }
}
