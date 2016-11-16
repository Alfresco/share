/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.ShareLink;

/**
 * Represents a row of data in my activity dashlet.
 * The activity row is made of a user link, a document,
 * description of activity and finally the site link.
 * 
 * @author Michael Suzuki
 * @author mbhave
 * @since 1.5
 */
public class ActivityShareLink
{
    private final String description;
    private final ShareLink user;
    private final ShareLink document;
    private final ShareLink site;
    private final String group;

    /**
     * Constructor.
     * 
     * @param user {@link ShareLink} user link
     * @param document {@link ShareLink} document link
     * @param site {@link ShareLink} site link
     */
    public ActivityShareLink(final ShareLink user, final ShareLink document, final ShareLink site, final String group, final String description)
    {
        this.user = user;
        this.document = document;
        this.site = site;
        this.group = group;
        this.description = description;
    }
    
    /**
     * Constructor.
     * 
     * @param user {@link ShareLink} user link
     * @param document {@link ShareLink} document link
     * @param site {@link ShareLink} site link
     */
    public ActivityShareLink(final String group, final ShareLink site, final String description)
    {
        this.group = group;
        this.site = site;
        this.description = description;
        this.user = null;
        this.document = null;
    }
    
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
        this.group = null;
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
        this.group = null;
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
        this.group = null;
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

	public String getGroup() {
		return group;
	}

}
