package org.alfresco.po.share.site.calendar;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.calendar.CalendarPage.ActionEventVia;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests for Calendar page web elements
 * 
 * @author Sergey Kardash
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class CalendarPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    CalendarPage calendarPage = null;
    protected String folderName;
    protected String folderDescription;
    protected String event1 = "single_day_event1";
    protected String tag1 = "tag1";
    String edit_event1_what = "single_day_event1_edit_what";
    String edit_event1_where = "single_day_event1_edit_where";
    String edit_event1_description = "single_day_event1_edit_description";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "calendar" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addCalendarPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<>();
        addPageTypes.add(SitePageType.CALENDER);
        customizeSitePage.addPages(addPageTypes);
        try
        {
            saveScreenShot("addCalendarPage_after_add_pages_screen");
            savePageSource("addCalendarPage_after_add_pages_page_source");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();
        assertNotNull(calendarPage);
    }

    /**
     * Method for event creation
     */
    @Test(dependsOnMethods = "addCalendarPage", timeOut = 60000)
    public void testCreateEvent()
    {
        navigateToSiteDashboard();
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        // Create any single day event, e.g. event1
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event1, event1, event1, null, null, null, null, tag1, false);
    }

    @Test(dependsOnMethods = "testCreateEvent", timeOut = 60000)
    public void testIsEventPresent()
    {
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
    }

    @Test(dependsOnMethods = "testIsEventPresent", timeOut = 60000)
    public void testChooseDayTab()
    {
        calendarPage = calendarPage.chooseDayTab().render();
        assertNotNull(calendarPage, "Calendar page day tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the day tab");
    }

    @Test(dependsOnMethods = "testChooseDayTab", timeOut = 60000)
    public void testChooseWeekTab()
    {
        calendarPage = calendarPage.chooseWeekTab().render();
        assertNotNull(calendarPage, "Calendar page week tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.WEEK_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the week tab");
    }

    @Test(dependsOnMethods = "testChooseWeekTab", timeOut = 60000)
    public void testChooseMonthTab()
    {
        calendarPage = calendarPage.chooseMonthTab().render();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
    }

    @Test(dependsOnMethods = "testChooseMonthTab", timeOut = 60000)
    public void testChooseAgendaTab()
    {
        calendarPage = calendarPage.chooseAgendaTab().render();
        assertNotNull(calendarPage, "Calendar page agenda tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.AGENDA_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the agenda tab");

        int nrEvents = calendarPage.getTheNumOfEvents(ActionEventVia.AGENDA_TAB);
        Assert.assertTrue(nrEvents > 1);

    }

    @Test(dependsOnMethods = "testChooseAgendaTab", timeOut = 60000)
    public void testShowWorkingHours()
    {
        calendarPage = calendarPage.chooseWeekTab().render();
        assertNotNull(calendarPage, "Calendar page week tab isn't opened");

        calendarPage = calendarPage.showWorkingHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_WORKING_HOUR), "Not only working hours (7.00 - 18.00) are shown");
    }

    @Test(dependsOnMethods = "testShowWorkingHours", timeOut = 60000)
    public void testShowAllHours()
    {
        calendarPage = calendarPage.showAllHours().render();
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.FIRST_ALL_HOUR), "Not all hours are shown");
        Assert.assertTrue(calendarPage.checkTableHours(CalendarPage.HoursFromTable.LAST_ALL_HOUR), "Not all hours are shown");
    }

    @Test(dependsOnMethods = "testShowAllHours", timeOut = 60000)
    public void testIsShowAllItemsPresent()
    {
        Assert.assertTrue(calendarPage.isShowAllItemsPresent(), "Link 'Show All items' isn't displayed");
    }

    @Test(dependsOnMethods = "testIsShowAllItemsPresent", timeOut = 60000)
    public void testIsTagPresent()
    {
        Assert.assertTrue(calendarPage.isTagPresent(tag1), "Tag Link '" + tag1 + "' isn't displayed");
    }

    @Test(dependsOnMethods = "testIsTagPresent", timeOut = 60000)
    public void testEditEvent()
    {
        calendarPage = calendarPage.chooseMonthTab().render();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, event1), "The " + event1
                + " isn't correctly displayed on the month tab");
        calendarPage = calendarPage.editEvent(event1, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, CalendarPage.ActionEventVia.MONTH_TAB, edit_event1_what,
                edit_event1_where, edit_event1_description, null, null, null, null, null, false, null).render();
    }

    @Test(dependsOnMethods = "testEditEvent", timeOut = 60000)
    public void testCheckInformationEventForm()
    {
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, edit_event1_what).render();
        Assert.assertTrue(informationEventForm.getWhatDetail().contains(edit_event1_what), "The " + edit_event1_what
                + " isn't correctly displayed on the information form what field. Server B");
        Assert.assertTrue(informationEventForm.getWhereDetail().contains(edit_event1_where), "The " + edit_event1_where
                + " isn't correctly displayed on the information form where field. Server B");
        Assert.assertTrue(informationEventForm.getDescriptionDetail().contains(edit_event1_description), "The " + edit_event1_description
                + " isn't correctly displayed on the information form description field. Server B");
        calendarPage = informationEventForm.closeInformationForm();
        assertNotNull(calendarPage, "Calendar page month tab isn't opened");
    }

    /**
     * test to verify the Start and End Date Time
     * 
     * author Bogdan.Bocancea
     */
    @Test(groups = "Verifycalendar", timeOut = 60000)
    public void testStartEndDateInfoFields()
    {

        navigateToSiteDashboard();

        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        String event_2 = "event2";
        // Create any event
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        Calendar calendar = Calendar.getInstance();

        int todayDate = calendar.get(Calendar.DATE);
        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.DAY_TAB, event_2, event_2, event_2, String.valueOf(todayDate), null,
                String.valueOf(todayDate), null, null, false);
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event_2), "The " + event_2
                + " isn't correctly displayed on the day tab");

        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.DAY_TAB_SINGLE_EVENT, event_2).render();
        Assert.assertTrue(eventInfo.getWhatDetail().contains(event_2), "The " + event_2 + " isn't correctly displayed on the information form what field.");

        String starDate = eventInfo.getStartDateTime();
        String endDate = eventInfo.getEndDateTime();

        Assert.assertTrue(starDate.contains(String.valueOf(todayDate)));
        Assert.assertTrue(endDate.contains(String.valueOf(todayDate)));

        Assert.assertFalse(eventInfo.isRecurrencePresent());

        String recurrence = eventInfo.getRecurrenceDetail();
        Assert.assertTrue(recurrence.isEmpty());

        Assert.assertTrue(eventInfo.isDeleteButtonEnabled());
        Assert.assertTrue(eventInfo.isOkButtonEnabled());
    }

    /**
     * test to verify create event method with year and month
     * 
     * author Bogdan.Bocancea
     */
    @Test(groups = "Verifycalendar", timeOut = 60000)
    public void testCreateEventWithMonth()
    {

        ArrayList<String> monthValues = new ArrayList<String>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October", "November", "December"));

        String event_month = "event_month";
        navigateToSiteDashboard();

        // navigate to Site Calendar Page
        calendarPage = siteDashBoard.getSiteNav().selectCalendarPage();

        Calendar calendar = Calendar.getInstance();

        int currentMonth = calendar.get(Calendar.MONTH);
        String startMonth = monthValues.get(currentMonth);

        int currentYear = calendar.get(Calendar.YEAR);
        int todayDate = calendar.get(Calendar.DATE);

        calendar.add(Calendar.MONTH, 2);

        int nextMonth = calendar.get(Calendar.MONTH);
        int nextMonthsYear = calendar.get(Calendar.YEAR);

        String endMonth = monthValues.get(nextMonth);
        int lastDate = calendar.getActualMaximum(Calendar.DATE);

        calendarPage = calendarPage.createEvent(CalendarPage.ActionEventVia.MONTH_TAB, event_month, event_month, event_month, String.valueOf(currentYear),
                String.valueOf(startMonth), String.valueOf(todayDate), "7:00 AM", String.valueOf(nextMonthsYear), String.valueOf(endMonth),
                String.valueOf(lastDate), "9:00 AM", null, false);

        // verify event is present
        Assert.assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event_month), "The " + event_month
                + " isn't correctly displayed on the day tab");

        // verify the information event form
        InformationEventForm eventInfo = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_MULTIPLY_EVENT, event_month).render();
        Assert.assertTrue(eventInfo.getWhatDetail().contains(event_month), "The " + event_month
                + " isn't correctly displayed on the information form what field.");

        String starDate = eventInfo.getStartDateTime();
        String endDate = eventInfo.getEndDateTime();

        // verify that start month and end month are correct
        Assert.assertTrue(starDate.contains(String.valueOf(startMonth)));
        Assert.assertTrue(endDate.contains(String.valueOf(endMonth)));

    }

}