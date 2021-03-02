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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * Represent elements found on the html page relating to the
 * sub navigation bar that appears on the site pages.
 * 
 * @author Michael Suzuki
 * @author mbhave
 * @since 1.0
 */
public class SiteNavigation extends AbstractSiteNavigation
{
    private Log logger = LogFactory.getLog(SiteNavigation.class);

    
    /**
     * Mimics the action of selecting the site
     * project library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectSiteProjectLibrary()
    {
        extractLink(PROJECT_LIBRARY).click();
        return factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
    }
    
    /**
     * Mimics the action of selecting the site
     * document library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectDocumentLibrary()
    {
        documentLibrary.click();
        return getCurrentPage();
    }

//    /**
//     * Mimics the action of selecting the site
//     * document library link.
//     * 
//     * @return HtmlPage site document lib page object
//     */
//    public HtmlPage selectSiteWikiPage()
//    {
//        extractLink(WIKI).click();
//        return new WikiPage(driver);
//    }
//
//    /**
//     * Mimics the action of selecting the site
//     * calendar link.
//     * 
//     * @return HtmlPage site calendar page object
//     */
//    public HtmlPage selectSiteCalendarPage()
//    {
//        extractLink(CALENDAR).click();
//        return new CalendarPage(driver);
//    }

    /**
     * Mimics the action clicking the configure button.
     * This Features available only in the Enterprise 4.2 and Cloud 2.
     */
    public void selectConfigure()
    {
        WebElement config = findAndWait(By.id("HEADER_SITE_CONFIGURATION_DROPDOWN"));
    	config.click();
    }

    @FindBy(id="HEADER_SITE_CONFIGURATION_DROPDOWN") Link configMore;
    /**
     * Mimics the action of clicking more button.
     */
    public void selectSiteConfigMore()
    {
        configMore.click();
    }

    @FindBy(id="HEADER_CUSTOMIZE_SITE_text") Link customizeSite;
    /**
     * Mimics the action of clicking the Customize Site.
     * This features is not available in the cloud.
     * 
     * @return {@link CustomizeSitePage}
     */
    public HtmlPage selectCustomizeSite()
    {
        selectConfigure();
        customizeSite.click();
        return factoryPage.instantiatePage(driver, CustomizeSitePage.class);
    }
    
    
    @FindBy(id="HEADER_CUSTOMIZE_SITE_DASHBOARD") private Link customizeDashboard;
    /**
     * @return {@link CustomiseSiteDashboardPage}
     */
    public HtmlPage selectCustomizeDashboard()
    {
        selectConfigure();
        customizeDashboard.click();
        return factoryPage.instantiatePage(driver, CustomiseSiteDashboardPage.class);
    }

    private WebElement extractLink(final String title)
    {
        List<WebElement> list = driver.findElements(By.cssSelector("span.alf-menu-bar-label-node"));
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

    @FindBy(id="HEADER_EDIT_SITE_DETAILS_text") Link editSiteDetails;
    /**
     * Selects on edit site details link.
     * 
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectEditSite()
    {
        selectConfigure();
        editSiteDetails.click();
        return factoryPage.instantiatePage(driver, EditSitePage.class);
    }

    @FindBy(id="HEADER_SITE_MEMBERS_text") Link siteMembers;
    /**
     * This method is used to select the site Members link.
     * 
     * @return SiteMembersPage site Members page object
     */
    public HtmlPage selectMembers()
    {
        siteMembers.click();
        return factoryPage.instantiatePage(driver, SiteMembersPage.class);
    }
    
    @FindBy(id="HEADER_SITE_INVITE")Link invite;
    /**
     * Clicks on invite users button
     * 
     */
    public HtmlPage selectInvite()
    {
        invite.click();
        return factoryPage.getPage(driver);
    }
    

    @FindBy(id="HEADER_SITE_DOCUMENTLIBRARY") Link documentLibrary;
    /**
     * Clicks on add user to site button
     * 
     * @return {@link AddUsersToSitePage}
     */
    public HtmlPage selectAddUser()
    {
    	selectInvite();
        return getCurrentPage();
    }

    /**
     * Check if the site navigation has document library link highlighted.
     * 
     * @return if link is highlighted
     */
    public boolean isDocumentLibraryActive()
    {
        boolean val = isLinkActive(documentLibrary);
        return val;
    }
    
     /**
     * Mimics the action of selecting the site
     * Site Dashboard link under More drop down.
     * 
     * @return HtmlPage site calendar page object
     */
    public HtmlPage selectSiteDashboardPage()
    {
        clickMoreIfExist();
        driver.findElement(SITE_DASHBOARD_LINK).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the site
     * calendar link.
     * 
     * @return HtmlPage site calendar page object
     */
    public HtmlPage selectCalendarPage()
    {
        clickMoreIfExist();
        driver.findElement(CALENDAR_LINK).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the site
     * members link.
     * 
     * @return HtmlPage site members page object
     */
    public HtmlPage selectMembersPage()
    {
        clickMoreIfExist();
        driver.findElement(MEMBERS_LINK).click();
        return getCurrentPage();
    }

    @FindBy(id="HEADER_SITE_WIKI-PAGE_text")Link wiki;
    /**
     * Mimics the action of selecting the site
     * members link.
     * 
     * @return HtmlPage site members page object
     */
    public HtmlPage selectWikiPage()
    {
        clickMoreIfExist();
        wiki.click();
        return getCurrentPage();
    }

    @FindBy(id="HEADER_SITE_DISCUSSIONS-TOPICLIST_text")Link disscussion;
    /**
     * Mimics the action of selecting the site
     * discussions link.
     * 
     * @return HtmlPage site members page object
     */
    public HtmlPage selectDiscussionsPage()
    {
        clickMoreIfExist();
        disscussion.click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the site
     * Blog link.
     * 
     * @return HtmlPage site members page object
     */
    public HtmlPage selectBlogPage()
    {
        clickMoreIfExist();
        driver.findElement(BLOG_LINK).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the site
     * Links link.
     * 
     * @return HtmlPage site members page object
     */
    public HtmlPage selectLinksPage()
    {
        clickMoreIfExist();
        driver.findElement(LINKS_LINK).click();
        return getCurrentPage();
    }

    /**
     * Mimics the action of selecting the site
     * Data Lists link.
     * 
     * @return HtmlPage site members page object
     */
    public HtmlPage selectDataListPage()
    {
        clickMoreIfExist();
        driver.findElement(DATA_LISTS_LINK).click();
        return getCurrentPage();
    }

    /**
     * Check if Delete Site link is displayed in the site configuration drop down
     * 
     * @return
     */
    public boolean isDeleteSiteDisplayed()
    {
        selectConfigure();
        return isLinkDisplayed(DELETE_SITE);
    }

   /**
     * Mimics the action of join Site.
     * 
     * @return {@link DashBoardPage}
     */
    public HtmlPage joinSite()
    {
        try
        {
            selectConfigure();
            driver.findElement(JOIN_SITE).click();
            driver.findElement(By.xpath("//span[text()='OK']")).click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find Join Site Link.");
    }
    
    /**
     * Check if Wiki link is displayed in the site navigation header
     * 
     * @return
     */
    public boolean isWikiDisplayed()
    {
        return isLinkDisplayed(WIKI_LINK);
    }

    /**
     * Mimics the action of clicking on Delete Site link in site configuration drop down.
     *
     * @return {@link DeleteSitePage}
     */
    public HtmlPage selectDeleteSite()
    {
        selectConfigure();
        driver.findElement(DELETE_SITE).click();
        driver.findElement(By.id("ALF_SITE_SERVICE_DIALOG_CONFIRMATION_label")).click();
        return getCurrentPage();
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
            selectConfigure();
            driver.findElement(LEAVE_SITE).click();
            WebElement okButton = driver.findElement(By.xpath("//span[text()='OK']"));
            okButton.click();
            okButton.click();
            return getCurrentPage();
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
                WebElement element = driver.findElement(SITE_MORE_PAGES);
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

    @SuppressWarnings("unchecked")
    @Override
    public SiteNavigation render(RenderTime timer)
    {
        
        return this;
    }

}
