
package org.alfresco.po.share.user;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

/**
 * User Notification page object has a checkbox to enable and disable
 * notification feed emails.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public class UserSitesPage extends SharePage
{
    private static final By NO_SITES_MESSAGE = By.cssSelector("div.viewcolumn p");

    private final Log logger = LogFactory.getLog(UserSitesPage.class);

    @SuppressWarnings("unchecked")
    @Override
    public UserSitesPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {
                try
                {
                    if (siteItems.isEmpty())
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                }

                if (driver.findElement(NO_SITES_MESSAGE).isDisplayed()
                        || driver.findElement(NO_SITES_MESSAGE).getText().equals(getValue("user.profile.sites.nosite")))
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserSitesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Get the navigation bar.
     * 
     * @return ProfileNavigation
     */
    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(driver, factoryPage);
    }

    /**
     * Return <code>true</code> if the No Site message is displayed on screen.
     * 
     * @return boolean
     */
    public boolean isNoSiteMessagePresent()
    {
        boolean present = false;
        try
        {
            present = driver.findElement(NO_SITES_MESSAGE).getText().equals(getValue("user.profile.sites.nosite"));

            if (present && !siteItems.isEmpty())
            {
                throw new PageException("No Sites message and site list displayed at the same time.");
            }

            return present;
        }
        catch (NoSuchElementException e)
        {
        }

        return present;
    }

    @FindAll({ @FindBy(css = "ul[id$='default-sites'] li") })
    List<WebElement> siteItems;

    /**
     * Get a list of sites.
     * 
     * @return A {@link List} of {@link UserSiteItem}.
     */
    public List<UserSiteItem> getSites()
    {
        List<UserSiteItem> sites = new ArrayList<>();
        try
        {
            for (WebElement el : siteItems)
            {
                UserSiteItem siteItem = (UserSiteItem) factoryPage.instantiatePageElement(driver, UserSiteItem.class);
                siteItem.setWrappedElement(el);
                sites.add(siteItem);
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find any sites", e);
        }
        return sites;
    }

    /**
     * Get a {@link UserSiteItem} for the named site
     * 
     * @param siteName String
     * @return UserSiteItem
     */
    public UserSiteItem getSite(String siteName)
    {
        List<UserSiteItem> sites = getSites();

        for (UserSiteItem site : sites)
        {
            String name = site.getSiteName();
            if (siteName.equals(name))
            {
                return site;
            }
        }

        throw new PageOperationException("Unable to find site: " + siteName);
    }
}
