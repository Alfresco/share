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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represents the file data in Table View in the Dcoument Library.
 * 
 * Get a TableViewFileDirectoryInfo instance from:
 * {@link DocumentLibraryPage#getLeftMenus()}
 * 
 * @author Jamie Allison
 * @since  4.3.0
 */
public class TableViewFileDirectoryInfo extends SimpleDetailTableView
{

    private static final String CREATOR = "td.yui-dt-col-cmcreator>div>span>a";
    private static final String CREATED = "td.yui-dt-col-cmcreated>div>span";
    private static final String MODIFIER = "td.yui-dt-col-cmmodifier>div>span>a";
    private static final String MODIFIED = "td.yui-dt-col-modified>div>span";

    public TableViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);

        FILENAME_IDENTIFIER = "td.yui-dt-col-name span>a";
        TITLE = "td.yui-dt-col-cmtitle>div>span";
        FILE_DESC_IDENTIFIER = "td.yui-dt-col-cmdescription>div>span";
        MORE_ACTIONS = drone.getElement("more.actions");
        CONTENT_ACTIONS = "td.yui-dt-col-actions";
        rowElementXPath = "../../..";
        EDIT_CONTENT_NAME_ICON = "td[class*='-col-name'] span[title='Rename']";
        resolveStaleness();
    }

    /**
     * {@inheritDoc}
     */
    public String getCreator()
    {
        try
        {
            return findAndWait(By.cssSelector(CREATOR)).getText();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageOperationException("Unable to find content column creator");
    }

    /**
     * {@inheritDoc}
     */
    public HtmlPage selectCreator()
    {
        WebElement creatorLink = findElement(By.cssSelector(CREATOR));
        creatorLink.click();

        return FactorySharePage.resolvePage(drone);
    }

    /**
     * {@inheritDoc}
     */
    public String getCreated()
    {
        try
        {
            return findAndWait(By.cssSelector(CREATED)).getText();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageOperationException("Unable to find content column created");
    }

    /**
     * {@inheritDoc}
     */
    public String getModifier()
    {
        try
        {
            return findAndWait(By.cssSelector(MODIFIER)).getText();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageOperationException("Unable to find content column modifier");
    }

    /**
     * {@inheritDoc}
     */
    public HtmlPage selectModifier()
    {
        WebElement creatorLink = findElement(By.cssSelector(MODIFIER));
        creatorLink.click();

        return FactorySharePage.resolvePage(drone);
    }

    /**
     * {@inheritDoc}
     */
    public String getModified()
    {
        try
        {
            return findAndWait(By.cssSelector(MODIFIED)).getText();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageOperationException("Unable to find content column created");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        throw new UnsupportedOperationException("ShareLink is not available in Table View File Directory Info.");
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

    @Override
    public String getVersionInfo()
    {
        throw new UnsupportedOperationException("Version info not available in Table view.");
    }
}