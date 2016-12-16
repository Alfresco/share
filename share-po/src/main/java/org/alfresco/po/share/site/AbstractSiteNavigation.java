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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Abstract site navigation for the different types
 * of site navigation, base to collaboration based sites
 *
 * @author Michael Suzuki
 * @version 1.7.1
 */
public abstract class AbstractSiteNavigation extends SharePage
{
    protected static final By CUSTOMISE_DASHBOARD_BTN = By.cssSelector("div[class^='page-title']>div>span>span>a[href$='customise-site-dashboard']");
    protected static final By CUSTOMIZE_SITE_DASHBOARD = By.cssSelector("#HEADER_CUSTOMIZE_SITE_DASHBOARD_text");
    protected static final By EDIT_SITE_DETAILS = By.cssSelector("#HEADER_EDIT_SITE_DETAILS_text");
    protected static final By CANCEL_SITE_REQUEST = By.cssSelector("#HEADER_CANCEL_JOIN_SITE_REQUEST_text");
    protected static final By DELETE_SITE = By.cssSelector("#HEADER_DELETE_SITE_text");
    protected static final By LEAVE_SITE = By.cssSelector("#HEADER_LEAVE_SITE_text");
    protected static final By JOIN_SITE = By.cssSelector("#HEADER_JOIN_SITE_text");
    protected static final By MORE_BUTTON_LINK = By.cssSelector(".links>div>div>ul>li>a");
    protected static final String SITE_DASHBOARD = "Site Dashboard";
    protected static final By SITE_DASHBOARD_NAVIGATION = By.cssSelector("#HEADER_SITE_DASHBOARD");
    protected static final String DASHBOARD = "Dashboard";
    protected static final String PROJECT_LIBRARY = "Project Library";
    protected static final String INVITE_BUTTON = "a[href$='invite']";
    protected static final String CUSTOMIZE_LINK_TEXT = "Customize Site";
    protected static final String WIKI = "Wiki";
    protected static final String CALENDAR = "Calendar";
    protected static final By CALENDAR_LINK = By.cssSelector("#HEADER_SITE_CALENDAR_text");
    protected static final By BLOG_LINK = By.cssSelector("#HEADER_SITE_BLOG-POSTLIST_text");
    protected static final By LINKS_LINK = By.cssSelector("#HEADER_SITE_LINKS_text");
    protected static final By DATA_LISTS_LINK = By.cssSelector("#HEADER_SITE_DATA-LISTS_text");
    protected static final By SITE_MEMBERS = By.cssSelector("div#HEADER_SITE_MEMBERS");
    protected static final By MEMBERS_LINK = By.cssSelector("span#HEADER_SITE_MEMBERS_text");
    protected static final By DOCLIB_LINK = By.cssSelector("#HEADER_SITE_DOCUMENTLIBRARY_text");
    protected static final By WIKI_LINK = By.cssSelector("#HEADER_SITE_WIKI-PAGE_text");
    protected static final By SITE_DASHBOARD_LINK = By.cssSelector("#HEADER_SITE_DASHBOARD_text");
    protected static final By SITE_MORE_PAGES = By.cssSelector("span#HEADER_SITE_MORE_PAGES_text");
    protected static final String SITE_LINK_NAV_PLACEHOLER = "div.site-navigation > span:nth-of-type(%d) > a";
    public static final String LABEL_DOCUMENTLIBRARY_TEXT = "span#HEADER_SITE_DOCUMENTLIBRARY_text";

    /**
     * Check if the site navigation link is highlighted.
     *
     * @param by selector of site nav link
     * @return if link is highlighted
     */
    public boolean isLinkActive(Link element)
    {
        if (element == null)
        {
            throw new IllegalArgumentException("By selector is required");
        }
        String value = element.getWrappedElement().getAttribute("class");
        if (value != null && !value.isEmpty())
        {
            return value.contains(getValue("web.element.highlighted"));
        }
        return false;
    }

    /**
     * Check if the site dashboard navigation link is highlighted.
     *
     * @return if link is highlighted
     */
    public boolean isDashboardActive()
    {
        return isLinkActive(dashboard);
    }

    /**
     * Action of selecting on Site Dash board link.
     */
    public HtmlPage selectSiteDashBoard()
    {
        //bottleneck. Simple click() sometimes not work.
        try
        {
            mouseOver(dashboard.getWrappedElement());
            dashboard.click();
        }
        catch (StaleElementReferenceException e)
        {
            selectSiteDashBoard();
        }
        return getCurrentPage();
    }

    @FindBy(id="HEADER_SITE_DASHBOARD") Link dashboard;
    /**
     * Checks if dash board link is displayed.
     *
     * @return true if displayed
     */
    public boolean isDashboardDisplayed()
    {
        return isLinkDisplayed(SITE_DASHBOARD_NAVIGATION);
    }
    
    /**
     * Checks if site members is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public boolean isSelectSiteMembersDisplayed()
    {
        return isLinkDisplayed(SITE_MEMBERS);
    }

    /**
     * Select the drop down on the page and clicks on the link.
     *
     * @param title String title label of site nav
     * @return HtmlPage page object result of selecting the link.
     */
    protected HtmlPage select(final String title)
    {
        WebElement link = driver.findElement(By.linkText(title));
        link.click();
        return getCurrentPage();
    }

    /**
     * Select the drop down on the page and clicks on the link.
     *
     * @param by css locator
     */
    protected void select(final By by)
    {
        WebElement link = driver.findElement(by);
        link.click();
    }

    /**
     * Checks if item is displayed.
     *
     * @return true if displayed
     */
    public boolean isLinkDisplayed(final By by)
    {
        if (by != null)
        {
            try
            {
                return driver.findElement(by).isDisplayed();
            }
            catch (NoSuchElementException nse)
            {
            }
        }
        return false;
    }
}
