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
package org.alfresco.po.share.dashlet;

import java.util.NoSuchElementException;

import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.enums.Dashlets;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 *
 * @author Michael Suzuki
 */
public final class FactoryShareDashlet
{
	@Autowired FactoryPage factoryPage;
	public Dashlet getDashlet(WebDriver driver, Class<?> pageClassToProxy)
	{
		return (Dashlet) factoryPage.instantiatePageElement(driver, pageClassToProxy);
	}
    /**
     * Gets the dashlet HTML element from the dashboard page.
     *
     * @param driver {@link WebDriver}
     * @return name dashlet title
     */
    public Dashlet getPage(final WebDriver driver, final String name)
    {
        //TODO Fix this to use a Map and rename the method as dashlet is not a page.
        try
        {
            if ("my-sites".equalsIgnoreCase(name) || Dashlets.MY_SITES.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MySitesDashlet.class);
            }
            if ("my-documents".equalsIgnoreCase(name) || Dashlets.MY_DOCUMENTS.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyDocumentsDashlet.class);
            }
            if ("activities".equalsIgnoreCase(name) || Dashlets.MY_ACTIVITIES.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyActivitiesDashlet.class);
            }
            if ("my-tasks".equalsIgnoreCase(name) || "tasks".equalsIgnoreCase(name) || Dashlets.MY_TASKS.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyTasksDashlet.class);
            }
            if ("site-members".equalsIgnoreCase(name) || Dashlets.SITE_MEMBERS.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteMembersDashlet.class);
            }
            if ("site-contents".equalsIgnoreCase(name) || Dashlets.SITE_CONTENT.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteContentDashlet.class);
            }
            if ("site-activities".equalsIgnoreCase(name) || Dashlets.SITE_ACTIVITIES.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteActivitiesDashlet.class);
            }
            if ("welcome-site".equalsIgnoreCase(name) || Dashlets.WELCOME_SITE.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteWelcomeDashlet.class);
            }
            if ("site-notice".equalsIgnoreCase(name) || Dashlets.SITE_NOTICE.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteNoticeDashlet.class);
            }
            if ("site-search".equalsIgnoreCase(name) || Dashlets.SITE_SEARCH.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteSearchDashlet.class);
            }
            if ("my-discussions".equalsIgnoreCase(name) || Dashlets.MY_DISCUSSIONS.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyDiscussionsDashlet.class);
            }
            if ("saved-search".equalsIgnoreCase(name) || Dashlets.SAVED_SEARCH.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SavedSearchDashlet.class);
            }
            if ("image-preview".equalsIgnoreCase(name) || Dashlets.IMAGE_PREVIEW.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, ImagePreviewDashlet.class);
            }
            if ("wiki".equalsIgnoreCase(name) || Dashlets.WIKI.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, WikiDashlet.class);
            }
            if ("web-view".equalsIgnoreCase(name) || Dashlets.WEB_VIEW.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, WebViewDashlet.class);
            }
            if ("rss-feed".equalsIgnoreCase(name) || Dashlets.RSS_FEED.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, RssFeedDashlet.class);
            }
            if ("site-links".equalsIgnoreCase(name) || Dashlets.SITE_LINKS.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteLinksDashlet.class);
            }
            if ("site-calendar".equalsIgnoreCase(name) || Dashlets.SITE_CALENDAR.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteCalendarDashlet.class);
            }
            if ("site-profile".equalsIgnoreCase(name) || Dashlets.SITE_PROFILE.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteProfileDashlet.class);
            }
            if ("addOns-rss".equalsIgnoreCase(name) || Dashlets.ALFRESCO_ADDONS_RSS_FEED.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, AddOnsRssFeedDashlet.class);
            }
            if ("site-content-report".equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteContentBreakdownDashlet.class);
            }
            if ("top-site-contributor-report".equalsIgnoreCase(name))
            {
                return getDashlet(driver, TopSiteContributorDashlet.class);
            }
            if ("my-calendar".equalsIgnoreCase(name) || Dashlets.MY_CALENDAR.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyCalendarDashlet.class);
            }
            if ("my-meeting-workspaces".equalsIgnoreCase(name) || Dashlets.MY_MEETING_WORKSPACES.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyMeetingWorkSpaceDashlet.class);
            }
            if ("my-profile".equalsIgnoreCase(name)|| Dashlets.MY_PROFILE.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, MyProfileDashlet.class);
            }
            if ("site-contents".equalsIgnoreCase(name) || Dashlets.SITE_CONTENT.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteContentDashlet.class);
            }
            if ("site-wqs".equalsIgnoreCase(name) || Dashlets.WEB_QUICK_START.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, SiteWebQuickStartDashlet.class);
            }
            if ("editing-content".equalsIgnoreCase(name) || Dashlets.CONTENT_I_AM_EDITING.getDashletName().equalsIgnoreCase(name))
            {
                return getDashlet(driver, EditingContentDashlet.class);
            }
            throw new PageException(String.format("%s does not match any known dashlet name", name));
        }
        catch (NoSuchElementException ex)
        {
            throw new PageException("Dashlet can not be matched to an existing alfresco dashlet object: " + name, ex);
        }
    }
}
