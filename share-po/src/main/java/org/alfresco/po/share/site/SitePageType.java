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