/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.po.share.enums.UserRole;

/**
 * Bean to hold the details role and Share Link for user.
 * 
 * @author Abhijeet Bharade
 * @version 1.7.0
 */

public class SiteMember
{
    private UserRole role;

    private ShareLink shareLink;

    public SiteMember()
    {
    }

    public SiteMember(UserRole role, ShareLink shareLink)
    {
        this.role = role;
        this.shareLink = shareLink;
    }

    public UserRole getRole()
    {
        return role;
    }

    public void setRole(UserRole role)
    {
        this.role = role;
    }

    public ShareLink getShareLink()
    {
        return shareLink;
    }

    public void setShareLink(ShareLink shareLink)
    {
        this.shareLink = shareLink;
    }
}
