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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.InformationEventForm;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Site Calendar dashlet web elements
 * 
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Enterprise-only" })
public class SiteCalendarDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_CALENDAR_DASHLET = "site-calendar";
    private SiteCalendarDashlet siteCalendarDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the upcoming events for this site.";
    CalendarPage calendarPage;
    CustomizeSitePage customizeSitePage;
    Calendar calendar;

    @BeforeClass
    public void setUp() throws Exception
    {
        calendar = Calendar.getInstance();
        siteName = "siteCalendarDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        siteUtil.createSite(driver, username, password, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_CALENDAR, 2).render();
        siteCalendarDashlet = siteDashBoard.getDashlet(SITE_CALENDAR_DASHLET).render();
        assertNotNull(siteCalendarDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon()
    {
        siteCalendarDashlet.clickOnHelpIcon();
        assertTrue(siteCalendarDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = siteCalendarDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        siteCalendarDashlet.closeHelpBallon();
        assertFalse(siteCalendarDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyEventsCount()
    {
        assertEquals(siteCalendarDashlet.getEventsCount(), 0);
    }

    @Test(dependsOnMethods = "verifyEventsCount")
    public void verifyIsEventDisplayed()
    {
        assertFalse(siteCalendarDashlet.isEventsDisplayed("gogno-1235456"));
    }

    /**
     * Tests to verify the method isEventsWithDetailDisplayed()
     * 
     * <br/><br/>author bogdan.bocancea
     */
    @Test(dependsOnMethods = "verifyEventsCount")
    public void verifyEventCreated()
    {
        String event_dashlet = "event_dashlet";

        navigateToSiteDashboard();

        // customize site
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite().render();
        List<SitePageType> addPageTypes = new ArrayList<>();
        // add site calendar dashlet
        addPageTypes.add(SitePageType.CALENDER);
        customizeSitePage.addPages(addPageTypes);

        // navigate to calendar page
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage().render();

        int lastDate = calendar.getActualMaximum(Calendar.DATE);
        int todayDate = calendar.get(Calendar.DATE);

        // Create multiple day event
        int anotherDate;
        if (lastDate >= todayDate)
        {
            anotherDate = todayDate - 3;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event_dashlet, event_dashlet, event_dashlet,
                    String.valueOf(anotherDate), "7:00 AM", String.valueOf(todayDate), "9:00 AM", null, false).render();
        }
        else
        {
            anotherDate = todayDate + 3;
            calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event_dashlet, event_dashlet, event_dashlet,
                    String.valueOf(todayDate), "7:00 AM", String.valueOf(anotherDate), "9:00 AM", null, false).render();
        }

        // verify the event is present
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event_dashlet), "The " + event_dashlet
                + " isn't correctly displayed on the day tab");

        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event_dashlet).render();
        Assert.assertTrue(eventInfo.getWhatDetail().contains(event_dashlet), "The " + event_dashlet
                + " isn't correctly displayed on the information form what field.");

        String endDate = eventInfo.getEndDateTime().replace(" at", " ");

        navigateToSiteDashboard();

        String part3 = endDate.substring(endDate.lastIndexOf(", ") + 2).trim();
        String[] parts_last = part3.split(" ");
        String hour = parts_last[2];
        String time = parts_last[3];
        String startTime = "7:00 AM";
        String endTime = hour + " " + time;

        // compare results
        siteCalendarDashlet = siteDashBoard.getDashlet(SITE_CALENDAR_DASHLET).render();
        boolean result = siteCalendarDashlet.isEventsWithDetailDisplayed(event_dashlet, startTime, endTime);
        Assert.assertTrue(result);

    }

    @Test(dependsOnMethods = "verifyEventCreated")
    public void verifyIsEventsWithHeaderDisplayedNeg()
    {
        assertFalse(siteCalendarDashlet.isEventsWithHeaderDisplayed("test-negative"));
    }

}
