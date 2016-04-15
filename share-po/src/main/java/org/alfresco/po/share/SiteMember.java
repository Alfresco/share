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
