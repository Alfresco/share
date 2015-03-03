package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.ShareLink;

/**
 * Represents a row of data in my activity dashlet.
 * The activity row is made of a user link, a document,
 * description of activity and finally the site link.
 * 
 * @author Michael Suzuki
 * @since 1.5
 */
public class ActivityShareLink
{
    private final String description;
    private final ShareLink user;
    private final ShareLink document;
    private final ShareLink site;

    /**
     * Constructor.
     * 
     * @param user {@link ShareLink} user link
     * @param document {@link ShareLink} document link
     * @param site {@link ShareLink} site link
     */
    public ActivityShareLink(final ShareLink user, final ShareLink document, final ShareLink site, final String description)
    {
        if (null == user)
        {
            throw new UnsupportedOperationException("Use share link is required");
        }
        if (null == document)
        {
            throw new UnsupportedOperationException("Document share link is required");
        }
        if (null == site)
        {
            throw new UnsupportedOperationException("Site share link is required");
        }
        this.user = user;
        this.document = document;
        this.site = site;
        this.description = description;
    }

    /**
     * Constructor.
     * 
     * @param user {@link ShareLink} user link
     * @param site {@link ShareLink} site link
     */
    public ActivityShareLink(final ShareLink user, final ShareLink site, final String description)
    {
        if (null == user)
        {
            throw new UnsupportedOperationException("Use share link is required");
        }
        if (null == site)
        {
            throw new UnsupportedOperationException("Site share link is required");
        }
        this.user = user;
        this.document = null;
        this.site = site;
        this.description = description;
    }

    /**
     * Constructor.
     *
     * @param user {@link ShareLink} user link
     *
     */
    public ActivityShareLink(final ShareLink user, final String description)
    {
        if (null == user)
        {
            throw new UnsupportedOperationException("Use share link is required");
        }

        this.user = user;
        this.description = description;
        this.document = null;
        this.site = null;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityShareLink [description=");
        builder.append(description);
        builder.append(user.toString());
        builder.append(document.toString());
        builder.append(site.toString());
        builder.append("]");
        return builder.toString();
    }

    public String getDescription()
    {
        return description;
    }

    public ShareLink getUser()
    {
        return user;
    }

    public ShareLink getDocument()
    {
        return document;
    }

    public ShareLink getSite()
    {
        return site;
    }

}
