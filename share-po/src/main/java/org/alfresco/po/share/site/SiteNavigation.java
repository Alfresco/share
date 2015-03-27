/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import static org.alfresco.po.share.AlfrescoVersion.Enterprise41;

import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the html page relating to the
 * sub navigation bar that appears on the site pages.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteNavigation extends AbstractSiteNavigation
{
    private Log logger = LogFactory.getLog(SiteNavigation.class);

    protected final String siteMembersCSS = "#HEADER_SITE_MEMBERS_text";
    private final String documentLibLink;
    private final By customizeDashboardLink;

    /**
     * Constructor.
     */
    protected SiteNavigation(WebDrone drone)
    {
        super(drone);
        documentLibLink = getAlfrescoVersion().isDojoSupported() ? LABEL_DOCUMENTLIBRARY_PLACEHOLDER : String.format(SITE_LINK_NAV_PLACEHOLER, 3);
        customizeDashboardLink = getAlfrescoVersion().isDojoSupported() ? By.id("HEADER_CUSTOMIZE_SITE_DASHBOARD") : CUSTOMISE_DASHBOARD_BTN;
    }

    /**
     * Mimics the action of selecting the site
     * project library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectSiteProjectLibrary()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            extractLink(PROJECT_LIBRARY).click();
            return new DocumentLibraryPage(getDrone());
        }
        return select(PROJECT_LIBRARY);
    }

    /**
     * Mimics the action of selecting the site
     * document library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public DocumentLibraryPage selectSiteDocumentLibrary()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            // extractLink(drone.getLanguageValue("document.library")).click();

            if (drone.find(DOCLIB_LINK).isDisplayed())
            {
                extractLink(DOCUMENT_LIBRARY).click();
                return new DocumentLibraryPage(drone).render();
            }
            else
            {
                drone.findAndWait(SITE_MORE_PAGES).click();
                drone.findAndWait(DOCLIB_LINK).click();
                return new DocumentLibraryPage(drone).render();
            }

        }
        // return drone.getLanguageValue("document.library");
        return select(DOCUMENT_LIBRARY).render();
    }

    /**
     * Mimics the action of selecting the site
     * document library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectSiteWikiPage()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            extractLink(WIKI).click();
            return new WikiPage(getDrone());
        }
        return select(WIKI);
    }

    /**
     * Mimics the action of selecting the site
     * calendar link.
     * 
     * @return HtmlPage site calendar page object
     */
    public HtmlPage selectSiteCalendarPage()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            extractLink(CALENDAR).click();
            return new CalendarPage(getDrone());
        }
        return select(CALENDAR);
    }

    /**
     * Mimcs the action of selecting on the configuration
     * drop down that has been introduced in Alfresco Enterprise 4.2
     */
    private void selectConfigurationDropdown()
    {
        findElement(CONFIGURATION_DROPDOWN).click();
    }

    /**
     * Mimics the action clicking the configure button.
     * This Features available only in the Enterprise 4.2 and Cloud 2.
     */
    public void selectConfigure()
    {
        if (AlfrescoVersion.Enterprise41.equals(getAlfrescoVersion()))
        {
            throw new UnsupportedOperationException("It is not supported for this version of Alfresco : " + getAlfrescoVersion());
        }
        findAndWait(CONFIGURE_ICON).click();
    }

    /**
     * Mimics the action of clicking more button.
     */
    public void selectSiteConfigMore()
    {
        findElement(By.cssSelector(SITE_CONFIG_MORE)).click();
    }

    /**
     * Mimics the action of clicking the Customize Site.
     * This features is not available in the cloud.
     * 
     * @return {@link CustomizeSitePage}
     */
    public CustomizeSitePage selectCustomizeSite()
    {
        try
        {
            if (Enterprise41.equals(getAlfrescoVersion()))
            {
                selectSiteConfigMore();
                List<WebElement> elements = findAllWithWait(MORE_BUTTON_LINK);
                for (WebElement webElement : elements)
                {
                    ShareLink link = new ShareLink(webElement, getDrone());
                    if (CUSTOMIZE_LINK_TEXT.equalsIgnoreCase(link.getDescription()))
                    {
                        link.click();
                        break;
                    }
                }
            }
            else
            {
                selectConfigure();
                drone.find(CUSTOMIZE_SITE).click();
            }
            return new CustomizeSitePage(getDrone());
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find Customize Site Link.");
    }

    /**
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseSiteDashboardPage selectCustomizeDashboard()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            selectConfigurationDropdown();
        }
        drone.findAndWait(customizeDashboardLink).click();
        return new CustomiseSiteDashboardPage(getDrone());

    }

    private WebElement extractLink(final String title)
    {
        List<WebElement> list;
        if (alfrescoVersion.isCloud())
        {
            list = findElements(By.cssSelector("span"));
        }
        else
        {
            list = findElements(By.cssSelector("span.alf-menu-bar-label-node"));
        }
        for (WebElement element : list)
        {
            String name = element.getText();
            if (title.equalsIgnoreCase(name))
            {
                return element;
            }
        }
        throw new PageException("Unable to find " + title);
    }

    /**
     * Selects on edit site details link.
     * 
     * @return {@link HtmlPage} page response
     */
    public EditSitePage selectEditSite()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            selectConfigurationDropdown();
            drone.find(By.id("HEADER_EDIT_SITE_DETAILS_text")).click();
        }
        else
        {
            WebElement more = findElement(By.cssSelector("button[id$='_default-more-button']"));
            more.click();
            WebElement nav = findElement(By.cssSelector("div.links.title-button"));
            nav.findElement(By.cssSelector("ul.first-of-type>li>a.yuimenuitemlabel")).click();
        }
        return new EditSitePage(getDrone());
    }

    /**
     * This method is used to select the site Members link.
     * 
     * @return SiteMembersPage site Members page object
     */
    public SiteMembersPage selectMembers()
    {
        try
        {
            findElement(By.cssSelector(siteMembersCSS)).click();
            return new SiteMembersPage(getDrone()).render();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find the InviteMembersPage.", e);
        }
    }

    /**
     * This method returns the MembersPage object.
     * 
     * @return {@link InviteMembersPage}
     */
    public InviteMembersPage selectInvite()
    {
        try
        {
            if (Enterprise41 == getAlfrescoVersion())
            {
                findElement(By.cssSelector(INVITE_BUTTON)).click();
            }
            else
            {
                drone.findAndWait(By.cssSelector(".alf-user-icon")).click();
            }
        }
        catch (TimeoutException e)
        {
            throw new PageException("Unable to find the InviteMembersPage.", e);
        }
        return new InviteMembersPage(getDrone());
    }

    /**
     * Check if the site navigation has document library link highlighted.
     * 
     * @return if link is highlighted
     */
    public boolean isDocumentLibraryActive()
    {
        try
        {
            // This code needs to be removed when this cloud issue is fixed as
            // part of release-31
            // https://issues.alfresco.com/jira/browse/CLOUD-2092
            if (!alfrescoVersion.isCloud())
            {
                return isLinkActive(By.cssSelector(documentLibLink));
            }
            else
            {
                String active = "Hover";
                WebElement element = getDrone().findAndWait(By.cssSelector(documentLibLink));
                String value = element.getAttribute("class");
                if (value != null && !value.isEmpty())
                {
                    return value.contains(active);
                }
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    // this code is commented due to
    // https://issues.alfresco.com/jira/browse/CLOUD-2092
    // return isLinkActive(By.cssSelector(documentLibLink));

    /**
     * Mimics the action of selecting the site
     * calendar link.
     * 
     * @return HtmlPage site calendar page object
     */
    public CalendarPage selectCalendarPage()
    {
        clickMoreIfExist();
        drone.findAndWait(CALENDAR_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting the site
     * members link.
     * 
     * @return HtmlPage site members page object
     */
    public SiteMembersPage selectMembersPage()
    {
        clickMoreIfExist();
        drone.findAndWait(MEMBERS_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting the site
     * members link.
     * 
     * @return HtmlPage site members page object
     */
    public WikiPage selectWikiPage()
    {
        clickMoreIfExist();
        drone.findAndWait(WIKI_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting the site
     * discussions link.
     * 
     * @return HtmlPage site members page object
     */
    public DiscussionsPage selectDiscussionsPage()
    {
        clickMoreIfExist();
        drone.findAndWait(DISCUSSIONS_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting the site
     * Blog link.
     * 
     * @return HtmlPage site members page object
     */
    public BlogPage selectBlogPage()
    {
        clickMoreIfExist();
        drone.findAndWait(BLOG_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting the site
     * Links link.
     * 
     * @return HtmlPage site members page object
     */
    public LinksPage selectLinksPage()
    {
        clickMoreIfExist();
        drone.findAndWait(LINKS_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of selecting the site
     * Data Lists link.
     * 
     * @return HtmlPage site members page object
     */
    public SharePage selectDataListPage()
    {
        clickMoreIfExist();
        drone.findAndWait(DATA_LISTS_LINK).click();
        return drone.getCurrentPage().render();
    }

    /**
     * Mimics the action of leave Site.
     * 
     * @return {@link CustomizeSitePage}
     */
    public HtmlPage leaveSite()
    {
        try
        {
            if (Enterprise41.equals(getAlfrescoVersion()))
            {
                selectSiteConfigMore();
                drone.findAndWait(LEAVE_SITE).click();
            }
            else
            {
                selectConfigure();
                drone.find(LEAVE_SITE).click();
            }
            drone.findAndWait(By.xpath("//span[text()='OK']")).click();
            return drone.getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find Leave Site Link.");
    }
    
    /**
     * Checks if More drop down is displayed.
     *
     * @return <code>true</code> if displayed <code>false</code> otherwise
     */
    public boolean isMoreDisplayed()
    {
        return isLinkDisplayed(SITE_MORE_PAGES);
    }
    
    protected void clickMoreIfExist()
    {
        try
        {
            if(isMoreDisplayed())
            {
                WebElement element = drone.find(SITE_MORE_PAGES);
                element.click();
            }
        }
        catch (StaleElementReferenceException e)
        {
            clickMoreIfExist();
        }
        catch (NoSuchElementException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("More button is not found.", e);
            }
        }
    }

}