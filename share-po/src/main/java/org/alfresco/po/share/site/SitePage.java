package org.alfresco.po.share.site;

import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

/**
 * Abstract of an Alfresco Share site pages.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public abstract class SitePage extends SharePage
{
    /**
     * Checks that the current site is the same as
     * requested site name.
     * 
     * @return true if site pages match siteName
     */
    public boolean isSite(final String siteName)
    {
        String url = driver.getCurrentUrl();
        if (url != null && !url.isEmpty())
        {
            if (url.toLowerCase().contains(String.format("/site/%s/", siteName.toLowerCase())))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks the high lighted link in the site sub navigation
     * to verify which page we are on.
     * 
     * @return true if site document library is visible
     */
    public boolean isSitePage(final String pageTitle)
    {
        boolean displayed = false;
        try
        {
            String selector = "div.dijitSelected span";
            String title = findAndWait(By.cssSelector(selector)).getText();
            displayed = pageTitle.equalsIgnoreCase(title) ? true : false;
        }
        catch (NoSuchElementException e)
        {
            displayed = false;
        }
        return displayed;
    }

    /**
     * Get main navigation.
     * 
     * @return Navigation page object
     */
    public SiteNavigation getSiteNav()
    {
        return factoryPage.instantiatePage(driver, SiteNavigation.class).render();
    }

}
