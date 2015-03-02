package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test for My Calendar dashlet
 * 
 * @author Bogdan.Bocancea
 */

@Listeners(FailedTestListener.class)
public class MyCalendarDashletTest extends AbstractSiteDashletTest
{
    private static final String MY_CALENDAR_DASHLET = "my-calendar";
    private MyCalendarDashlet myCalendarDashlet = null;
    private CustomiseUserDashboardPage customiseUserDashBoard = null;
    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows the upcoming events in sites you belong to.";
    private static final String EMPTY_DASHLET_MESSAGE = "No upcoming events";
    CalendarPage calendarPage;
    CustomizeSitePage customizeSitePage;
    private DashBoardPage dashBoard;
    private String userName;
    protected SiteDashboardPage siteDashBoard;

    @BeforeClass
    public void setUp() throws Exception
    {

        siteName = "MyCalendarDashletTest" + System.currentTimeMillis();
        userName = "MyCalendarDashletTest_User" + System.currentTimeMillis();

        createEnterpriseUser(userName);
        ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();

    }

    @AfterClass(groups = { "alfresco-one" })
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void instantiateMyCalendarDashlet()
    {
        SharePage page = drone.getCurrentPage().render();
        // customize user dashboard
        customiseUserDashBoard = page.getNav().selectCustomizeUserDashboard().render();
        // add My Calendar Dashlet
        dashBoard = customiseUserDashBoard.addDashlet(Dashlets.MY_CALENDAR, 1).render();
        // get My Calendar dashlet
        myCalendarDashlet = (MyCalendarDashlet) dashBoard.getDashlet(MY_CALENDAR_DASHLET).render();

        assertNotNull(myCalendarDashlet);

    }

    @Test(dependsOnMethods = "instantiateMyCalendarDashlet")
    public void getEmptyDashletMessage()
    {
        // get empty dashlet message
        String emptyDashletMessage = myCalendarDashlet.getEmptyDashletMessage();
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE, "Expected message '" + EMPTY_DASHLET_MESSAGE + "' isn't displayed");
    }

    @Test(dependsOnMethods = "getEmptyDashletMessage")
    public void verifyHelpIcon()
    {
        // verify help icon
        myCalendarDashlet.clickOnHelpIcon();
        assertTrue(myCalendarDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = myCalendarDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        myCalendarDashlet.closeHelpBallon();
        assertFalse(myCalendarDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon")
    public void verifyEventsCount()
    {
        // verify that there are no event on the dashlet
        assertEquals(myCalendarDashlet.getEventsCount(), 0);
    }

    @Test(dependsOnMethods = "verifyEventsCount")
    public void verifyEventCreated()
    {
        String event1 = "MyCalendarEvents";

        // Create public site
        SiteUtil.createSite(drone, siteName, "description", "Public");

        siteDashBoard = drone.getCurrentPage().render();
        // customize site
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<>();

        // add site calendar dashlet
        addPageTypes.add(SitePageType.CALENDER);
        customizeSitePage.addPages(addPageTypes);

        // navigate to calendar page
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event1, event1, event1, null, null, null, null, null, false);

        // verify the event is present
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");

        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1).render();
        Assert.assertTrue(eventInfo.getWhatDetail().contains(event1), "The " + event1 + " isn't correctly displayed on the information form what field.");

        String startDate = eventInfo.getStartDateTime().replace("at", "");
        String endDate = eventInfo.getEndDateTime().replace("at", "");

        String[] get_all_start = startDate.split(" ");
        String start_date = get_all_start[1];
        String start_month = get_all_start[2];
        String start_year = get_all_start[3];
        String start_hour = get_all_start[5];
        String start_type = get_all_start[6];

        String[] get_all_end = endDate.split(" ");
        String end_hour = get_all_end[5];
        String end_type = get_all_end[6];

        if (start_date.length() == 1)
        {
            start_date = "0" + start_date;
        }

        String comparing = event1 + "\n" + start_date + " " + start_month + " " + start_year + " " + start_hour + " " + start_type + " - " + end_hour + " "
                + end_type + "\n" + siteName;

        // navigate to MyCalendar
        dashBoard = dashBoard.getNav().selectMyDashBoard().render();
        myCalendarDashlet = dashBoard.getDashlet("my-calendar").render();

        // check results
        boolean result = myCalendarDashlet.isEventDetailsDisplayed(comparing);
        Assert.assertTrue(result);
        
        boolean repeating = myCalendarDashlet.isRepeating(event1);
        Assert.assertFalse(repeating);

        dashBoard = dashBoard.getNav().selectMyDashBoard().render();
        myCalendarDashlet = dashBoard.getDashlet("my-calendar").render();

        SiteDashboardPage siteDash = myCalendarDashlet.clickSite(siteName);

        boolean siteTitle = siteDash.isSiteTitle(siteName);
        Assert.assertTrue(siteTitle);

        dashBoard = dashBoard.getNav().selectMyDashBoard().render();
        myCalendarDashlet = dashBoard.getDashlet("my-calendar").render();

        // Click the event's name;
        CalendarPage calendarPage = myCalendarDashlet.clickEvent(event1);
        Assert.assertTrue(calendarPage.isSitePage("Calendar"));

    }

    @Test(dependsOnMethods = "verifyEventCreated")
    public void verifyIsEventDisplayed()
    {
        DateFormat sdfDayFormat = new SimpleDateFormat("d MMMM, Y");
        Date currentDate = new Date();
        String date = sdfDayFormat.format(currentDate);
        String eventDate = date + " 12:00 PM - 1:00 PM";
        String event1 = "MyCalendarEvents";

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MyCalendarDashlet myCalendarDashlet = dashBoard.getDashlet("my-calendar").render();

        Assert.assertTrue(myCalendarDashlet.isEventDisplayed(event1, eventDate, siteName ),event1
                + " isn't found or information for event isn't correct");

    }

    @Test(dependsOnMethods = "verifyIsEventDisplayed")
    public void verifyClickEventSiteName()
    {
        String event1 = "MyCalendarEvents";

        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard().render();
        MyCalendarDashlet myCalendarDashlet = dashBoard.getDashlet("my-calendar").render();
        SiteDashboardPage siteDash = myCalendarDashlet.clickEventSiteName(event1, siteName);

        boolean siteTitle = siteDash.isSiteTitle(siteName);
        Assert.assertTrue(siteTitle,"Expected site dashbord isn't opened");
    }
}
