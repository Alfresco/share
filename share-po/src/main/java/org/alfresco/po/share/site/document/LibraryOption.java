/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

package org.alfresco.po.share.site.document;

/**
 * This class contains the CSS details of options in Document Library /
 * Repository Document.
 * 
 * @author Jamie Allison
 */
public enum LibraryOption
{
    SHOW_FOLDERS(".showFolders"),
    HIDE_FOLDERS(".hideFolders"),
    SHOW_BREADCRUMB(".showPath"),
    HIDE_BREADCRUMB(".hidePath"),
    RSS_FEED(".rss"),
    FULL_WINDOW(".fullWindow"),
    FULL_SCREEN(".fullScreen"),
    SIMPLE_VIEW(".view.simple"),
    DETAILED_VIEW(".view.detailed"),
    GALLERY_VIEW(".view.gallery"),
    FILMSTRIP_VIEW(".view.filmstrip"),
    TABLE_VIEW(".view.table"),
    AUDIO_VIEW(".view.audio"),
    MEDIA_VIEW(".view.media_table");

    private String linkValue;

    private LibraryOption(String link)
    {
        linkValue = link;
    }

    /**
     * Get value of CSS from the page type.
     * 
     * @param type
     * @return
     */
    public String getOption()
    {
        return linkValue;
    }
}