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
package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * @author Shan Nagarajan
 * @since 1.7.0
 */
public enum SitePageType
{	
    SITE_DASHBOARD("li[id$='dashboard'] img", "Site Dashboard"),
    WIKI("li[id$='_default-page-wiki-page'] img", "Wiki"),
    BLOG("li[id$='_default-page-blog-postlist'] img", "Blog"),
    CALENDER("li[id$='_default-page-calendar'] img", "Calendar"),
    DATA_LISTS("li[id$='_default-page-data-lists'] img", "Data Lists"), 
    DISCUSSIONS("li[id$='_default-page-discussions-topiclist'] img", "Discussions"),
    DOCUMENT_LIBRARY("li[id$='_default-page-documentlibrary'] img", "Document Library"),
    LINKS("li[id$='_default-page-links'] img", "Links");

    private String id;
    private String text;

    private SitePageType(String id, String text)
    {
        this.id = id;
        this.text = text;
    }

    public By getLocator()
    {
        return By.cssSelector(id);
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