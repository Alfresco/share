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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;

import java.util.NoSuchElementException;

/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 *
 * @author Michael Suzuki
 */
public final class FactoryShareDashlet
{
    private FactoryShareDashlet()
    {
    }

    /**
     * Gets the dashlet HTML element from the dashboard page.
     *
     * @param drone {@link WebDrone}
     * @return name dashlet title
     */
    public static Dashlet getPage(final WebDrone drone, final String name)
    {
        try
        {
            if ("my-sites".equalsIgnoreCase(name) || Dashlets.MY_SITES.getDashletName().equalsIgnoreCase(name))
            {
                return new MySitesDashlet(drone);
            }
            if ("my-documents".equalsIgnoreCase(name) || Dashlets.MY_DOCUMENTS.getDashletName().equalsIgnoreCase(name))
            {
                return new MyDocumentsDashlet(drone);
            }
            if ("activities".equalsIgnoreCase(name) || Dashlets.MY_ACTIVITIES.getDashletName().equalsIgnoreCase(name))
            {
                return new MyActivitiesDashlet(drone);
            }
            if ("my-tasks".equalsIgnoreCase(name) || "tasks".equalsIgnoreCase(name) || Dashlets.MY_TASKS.getDashletName().equalsIgnoreCase(name))
            {
                return new MyTasksDashlet(drone);
            }
            if ("site-members".equalsIgnoreCase(name) || Dashlets.SITE_MEMBERS.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteMembersDashlet(drone);
            }
            if ("site-contents".equalsIgnoreCase(name) || Dashlets.SITE_CONTENT.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteContentDashlet(drone);
            }
            if ("site-activities".equalsIgnoreCase(name) || Dashlets.SITE_ACTIVITIES.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteActivitiesDashlet(drone);
            }
            if ("welcome-site".equalsIgnoreCase(name) || Dashlets.WELCOME_SITE.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteWelcomeDashlet(drone);
            }
            if ("site-notice".equalsIgnoreCase(name) || Dashlets.SITE_NOTICE.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteNoticeDashlet(drone);
            }
            if ("site-search".equalsIgnoreCase(name) || Dashlets.SITE_SEARCH.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteSearchDashlet(drone);
            }
            if ("my-discussions".equalsIgnoreCase(name) || Dashlets.MY_DISCUSSIONS.getDashletName().equalsIgnoreCase(name))
            {
                return new MyDiscussionsDashlet(drone);
            }
            if ("saved-search".equalsIgnoreCase(name) || Dashlets.SAVED_SEARCH.getDashletName().equalsIgnoreCase(name))
            {
                return new SavedSearchDashlet(drone);
            }
            if ("image-preview".equalsIgnoreCase(name) || Dashlets.IMAGE_PREVIEW.getDashletName().equalsIgnoreCase(name))
            {
                return new ImagePreviewDashlet(drone);
            }
            if ("wiki".equalsIgnoreCase(name) || Dashlets.WIKI.getDashletName().equalsIgnoreCase(name))
            {
                return new WikiDashlet(drone);
            }
            if ("web-view".equalsIgnoreCase(name) || Dashlets.WEB_VIEW.getDashletName().equalsIgnoreCase(name))
            {
                return new WebViewDashlet(drone);
            }
            if ("rss-feed".equalsIgnoreCase(name) || Dashlets.RSS_FEED.getDashletName().equalsIgnoreCase(name))
            {
                return new RssFeedDashlet(drone);
            }
            if ("site-links".equalsIgnoreCase(name) || Dashlets.SITE_LINKS.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteLinksDashlet(drone);
            }
            if ("data-lists".equalsIgnoreCase(name) || Dashlets.DATA_LISTS.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteDataListsDashlet(drone);
            }
            if ("site-calendar".equalsIgnoreCase(name) || Dashlets.SITE_CALENDAR.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteCalendarDashlet(drone);
            }
            if ("site-profile".equalsIgnoreCase(name) || Dashlets.SITE_PROFILE.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteProfileDashlet(drone);
            }
            if ("addOns-rss".equalsIgnoreCase(name) || Dashlets.ALFRESCO_ADDONS_RSS_FEED.getDashletName().equalsIgnoreCase(name))
            {
                return new AddOnsRssFeedDashlet(drone);
            }
            if ("site-content-report".equalsIgnoreCase(name))
            {
                return new SiteContentBreakdownDashlet(drone);
            }
            if ("top-site-contributor-report".equalsIgnoreCase(name))
            {
                return new TopSiteContributorDashlet(drone);
            }
            if ("my-calendar".equalsIgnoreCase(name) || Dashlets.MY_CALENDAR.getDashletName().equalsIgnoreCase(name))
            {
                return new MyCalendarDashlet(drone);
            }
            if ("my-meeting-workspaces".equalsIgnoreCase(name) || Dashlets.MY_MEETING_WORKSPACES.getDashletName().equalsIgnoreCase(name))
            {
                return new MyMeetingWorkSpaceDashlet(drone);
            }
            if ("my-profile".equalsIgnoreCase(name)|| Dashlets.MY_PROFILE.getDashletName().equalsIgnoreCase(name))
            {
                return new MyProfileDashlet(drone);
            }
            if ("site-contents".equalsIgnoreCase(name) || Dashlets.SITE_CONTENT.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteContentDashlet(drone);
            }
            if ("site-wqs".equalsIgnoreCase(name) || Dashlets.WEB_QUICK_START.getDashletName().equalsIgnoreCase(name))
            {
                return new SiteWebQuickStartDashlet(drone);
            }
            if ("editing-content".equalsIgnoreCase(name) || Dashlets.CONTENT_I_AM_EDITING.getDashletName().equalsIgnoreCase(name))
            {
                return new EditingContentDashlet(drone);
            }
            throw new PageException(String.format("%s does not match any known dashlet name", name));
        }
        catch (NoSuchElementException ex)
        {
            throw new PageException("Dashlet can not be matched to an existing alfresco dashlet object: " + name, ex);
        }
    }
}
