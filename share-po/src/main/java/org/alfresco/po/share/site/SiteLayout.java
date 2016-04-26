package org.alfresco.po.share.site;

import org.openqa.selenium.By;

/**
 * Different types Site Layouts.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public enum SiteLayout
{

    THREE_COLUMN_WIDE_CENTRE(By.cssSelector("button[id*='select-button-dashboard-3-column']"));

    private By by;

    private SiteLayout(By by)
    {
        this.by = by;
    }

    /**
     * Get Locator for the given {@link SiteLayout}.
     * 
     * @return {@link By}
     */
    public By getLocator()
    {
        return this.by;
    }

}
