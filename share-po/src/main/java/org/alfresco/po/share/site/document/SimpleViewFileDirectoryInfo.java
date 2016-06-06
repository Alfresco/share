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
package org.alfresco.po.share.site.document;

import java.util.List;

import org.alfresco.po.HtmlPage;

/**
 * @author cbairaajoni
 */
public class SimpleViewFileDirectoryInfo extends SimpleDetailTableView
{

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        throw new UnsupportedOperationException("Description is not available in Simple View File Directory Info.");
    }


    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectFavourite()
     */
    @Override
    public void selectFavourite()
    {
        throw new UnsupportedOperationException("Favourite selection is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLike()
     */
    @Override
    public void selectLike()
    {
        throw new UnsupportedOperationException("Selecting Like functionality is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
    public boolean isLiked()
    {
        throw new UnsupportedOperationException("Like functionality is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isFavourite()
     */
    @Override
    public boolean isFavourite()
    {
        throw new UnsupportedOperationException("Favourites are not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getLikeCount()
     */
    @Override
    public String getLikeCount()
    {
        throw new UnsupportedOperationException("Cancelling Tag functionality is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<Categories> getCategories()
    {
        throw new UnsupportedOperationException("Categories are not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        throw new UnsupportedOperationException("ShareLink is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickOnCategoryNameLink(java.lang.String)
     */
    public DocumentLibraryPage clickOnCategoryNameLink(String categoryName)
    {
        throw new UnsupportedOperationException("ShareLink is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickCommentsLink()
     */
    @Override
    public HtmlPage clickCommentsLink()
    {
        throw new UnsupportedOperationException("CommentsLink is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsToolTip()
     */
    @Override
    public String getCommentsToolTip()
    {
        throw new UnsupportedOperationException("CommentsToolTip is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsCount()
     */
    @Override
    public int getCommentsCount()
    {
        throw new UnsupportedOperationException("Comments Count is not available in Simple View File Directory Info.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isCommentLinkPresent()
     */
    @Override
    public boolean isCommentLinkPresent()
    {
        throw new UnsupportedOperationException("Comments Link is not available in Simple View File Directory Info.");
    }
}
