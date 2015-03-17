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
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.webdrone.WebDrone;

/**
 * Holds the information about the Simple View inside site content dashlet.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public class SimpleViewInformation
{

    private ShareLink thumbnail;
    private ShareLink contentDetail;
    private String contentStatus;
    private ShareLink user;
    private boolean previewDisplayed;
    private WebDrone drone;

    public SimpleViewInformation(WebDrone drone,
                                 final ShareLink thumbnail,
                                 final ShareLink contentDetail,
                                 final ShareLink user,
                                 final String contentStatus,
                                 final boolean previewDisplayed)
    {

        if (null == drone)
        {
            throw new UnsupportedOperationException("Drone is required, It can't be null.");
        }

        if (null == thumbnail)
        {
            throw new UnsupportedOperationException("Thumbnail link is required");
        }

        if (null == contentDetail)
        {
            throw new UnsupportedOperationException("Content Details link is required");
        }

        if (null == user)
        {
            throw new UnsupportedOperationException("User link is required");
        }

        this.drone = drone;
        this.thumbnail = thumbnail;
        this.contentDetail = contentDetail;
        this.user = user;
        this.contentStatus = contentStatus;
        this.previewDisplayed = previewDisplayed;
    }

    public ShareLink getThumbnail()
    {
        return thumbnail;
    }

    public ShareLink getContentDetail()
    {
        return contentDetail;
    }

    public ShareLink getUser()
    {
        return user;
    }

    public String getContentStatus()
    {
        return contentStatus;
    }

    public boolean isPreviewDisplayed()
    {
        return previewDisplayed;
    }

    /**
     * Mimics the action clicking the document link.
     * 
     * @return {@link DocumentDetailsPage}
     */
    public DocumentDetailsPage clickContentDetail()
    {
        this.contentDetail.click();
        return new DocumentDetailsPage(drone);
    }

    /**
     * Mimics the actions Click User Profile.
     * 
     * @return {@link MyProfilePage}
     */
    public MyProfilePage clickUser()
    {
        this.user.click();
        return new MyProfilePage(drone);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleViewInformation [thumbnail=");
        builder.append(thumbnail.toString());
        builder.append(", contentDetail=");
        builder.append(contentDetail.toString());
        builder.append(", contentStatus=");
        builder.append(contentStatus);
        builder.append(", user=");
        builder.append(user.toString());
        builder.append(", previewDisplayed=");
        builder.append(previewDisplayed);
        builder.append("]");
        return builder.toString();
    }
}