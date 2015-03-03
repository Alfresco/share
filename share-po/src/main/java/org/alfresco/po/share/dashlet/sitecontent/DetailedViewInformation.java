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
package org.alfresco.po.share.dashlet.sitecontent;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.webdrone.WebDrone;

/**
 * Hold information about the Detailed View inside site content dashlet.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class DetailedViewInformation extends SimpleViewInformation
{
    private int likecount;
    private String fileSize;
    private ShareLink comment;
    private String description;
    private double version;
    private ShareLink like;
    private ShareLink favorite;
    private boolean favouriteEnabled;
    private boolean likeEnabled;
    private WebDrone drone;

    public DetailedViewInformation(WebDrone drone,
                                   final ShareLink thumbnail,
                                   final ShareLink contentDetail,
                                   final ShareLink user,
                                   final String contentStatus, 
                                   final ShareLink comment,
                                   final ShareLink like,
                                   final ShareLink favourite,
                                   int likeCount,
                                   String fileSize,
                                   String desc, 
                                   double version,
                                   boolean favouriteEnabled,
                                   boolean likeEnabled)
    {
        super(drone, thumbnail, contentDetail, user, contentStatus, false);

        if (null == drone)
        {
            throw new UnsupportedOperationException("Drone is required, It can't be null.");
        }

        if (null == comment)
        {
            throw new UnsupportedOperationException("Comment link is required");
        }

        if (null == like)
        {
            throw new UnsupportedOperationException("Like link is required");
        }

        if (null == favourite)
        {
            throw new UnsupportedOperationException("Favourite link is required");
        }

        this.drone = drone;
        this.comment = comment;
        this.like = like;
        this.favorite = favourite;
        this.likecount = likeCount;
        this.fileSize = fileSize;
        this.description = desc;
        this.version = version;
        this.favouriteEnabled = favouriteEnabled;
        this.likeEnabled = likeEnabled;
    }

    public boolean isLikeEnabled()
    {
        return likeEnabled;
    }

    public boolean isFavouriteEnabled()
    {
        return favouriteEnabled;
    }

    public int getLikecount()
    {
        return likecount;
    }

    public String getFileSize()
    {
        return fileSize;
    }

    public ShareLink getComment()
    {
        return comment;
    }

    public String getDescription()
    {
        return description;
    }

    public double getVersion()
    {
        return version;
    }

    public ShareLink getLike()
    {
        return like;
    }

    public ShareLink getFavorite()
    {
        return favorite;
    }

    /**
     * Mimics the action of clicking comment.
     * 
     * @return {@link DocumentDetailsPage}
     */
    public DocumentDetailsPage clickComment()
    {
        comment.click();
        return new DocumentDetailsPage(drone);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DetailedViewInformation [likecount=");
        builder.append(likecount);
        builder.append(", fileSize=");
        builder.append(fileSize);
        builder.append(", comment=");
        builder.append(comment.toString());
        builder.append(", description=");
        builder.append(description);
        builder.append(", version=");
        builder.append(version);
        builder.append(", like=");
        builder.append(like.toString());
        builder.append(", favorite=");
        builder.append(favorite.toString());
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
