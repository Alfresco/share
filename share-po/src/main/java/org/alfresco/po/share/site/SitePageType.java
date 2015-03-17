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
package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * @author Shan Nagarajan
 * @since 1.7.0
 */
public enum SitePageType
{
    WIKI("//li[contains(@id, '_default-page-wiki-page')]", "Wiki"),
    BLOG("//li[contains(@id, '_default-page-blog-postlist')]", "Blog"),
    CALENDER("//li[contains(@id, '_default-page-calendar')]/img", "Calendar"),
    DATA_LISTS("//li[contains(@id, '_default-page-data-lists')]", "Data Lists"),
    DISCUSSIONS("//li[contains(@id, '_default-page-discussions-topiclist')]", "Discussions"),
    DOCUMENT_LIBRARY("//li[contains(@id, '_default-page-documentlibrary')]", "Document Library"),
    LINKS("//li[contains(@id, '_default-page-links')]", "Links");

    private String id;
    private String text;

    private SitePageType(String id, String text)
    {
        this.id = id;
        this.text = text;
    }

    public By getLocator()
    {
        return By.xpath(id);
    }

    public String getXpath()
    {
        return id;
    }

    public String getDisplayText()
    {
        return text;
    }
}