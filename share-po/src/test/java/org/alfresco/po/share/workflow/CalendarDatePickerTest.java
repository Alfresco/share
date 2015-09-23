/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.workflow;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.test.FailedTestListener;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Unit test to verify selectDateFromCalendar method in WorkFlowPage.
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
public class CalendarDatePickerTest extends AbstractTest
{
    DashBoardPage dashBoardPage;
    MyWorkFlowsPage myWorkFlowsPage;
    StartWorkFlowPage startWorkFlowPage;
    private NewWorkflowPage newWorkflowPage = null;
    String workFlow1;
    String dueDate;
    DateTime due;
    

    @BeforeClass(groups = "Enterprise4.2")
    public void prepare() throws Exception
    {
        dashBoardPage = loginAs(username, password);

        workFlow1 = "MyWF-" + System.currentTimeMillis() + "-1";
        
        // Create a due date in 3 months time. Was hard coded to 17/0/2015.
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, 3);
        dueDate = String.format("%02d/%02d/%04d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH)+1,
            calendar.get(Calendar.YEAR));
        due = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(dueDate);
    }

    @Test(groups = "Enterprise4.2")
    public void enterRequiredApprovalPercentage() throws Exception
    {
        myWorkFlowsPage = dashBoardPage.getNav().selectWorkFlowsIHaveStarted().render();

        startWorkFlowPage = myWorkFlowsPage.selectStartWorkflowButton().render();
        newWorkflowPage = (NewWorkflowPage) startWorkFlowPage.getWorkflowPage(WorkFlowType.NEW_WORKFLOW);

        newWorkflowPage.selectDateFromCalendar(dueDate);
        Assert.assertEquals(newWorkflowPage.getDueDate(), dueDate);
    }

    @Test(groups = "Enterprise4.2", expectedExceptions = IllegalArgumentException.class, dependsOnMethods = "enterRequiredApprovalPercentage")
    public void selectDateFormCalendarWithException() throws Exception
    {
        newWorkflowPage.selectDateFromCalendar("");
    }

    @Test(groups = "Enterprise4.2", expectedExceptions = UnsupportedOperationException.class, dependsOnMethods = "selectDateFormCalendarWithException")
    public void selectDateFormCalendarWithUnsupportedException() throws Exception
    {
        DateTime dueDate = new DateTime().minusMonths(1).dayOfMonth().withMinimumValue();
        newWorkflowPage.selectDateFromCalendar(dueDate.toString("dd/MM/yyyy"));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectDateFormCalendarWithUnsupportedException")
    public void selectDateFormCalendarOnTheSameMonth() throws Exception
    {
        newWorkflowPage.closeCalendarDatePicker();
        DateTime dueDate = new DateTime();
        newWorkflowPage.selectDateFromCalendar(dueDate.toString("dd/MM/yyyy"));
        Assert.assertEquals(newWorkflowPage.getDueDate(), dueDate.toString("dd/MM/yyyy"));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectDateFormCalendarOnTheSameMonth")
    public void selectDateFormCalendarOnNextMonth() throws Exception
    {
        DateTime dueDate = new DateTime().plusMonths(1).dayOfMonth().withMinimumValue();
        newWorkflowPage.selectDateFromCalendar(dueDate.toString("dd/MM/yyyy"));
        Assert.assertEquals(newWorkflowPage.getDueDate(), dueDate.toString("dd/MM/yyyy"));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "selectDateFormCalendarOnNextMonth")
    public void getPriorityOptions()
    {
        List<String> options = newWorkflowPage.getPriorityOptions();
        Assert.assertEquals(options.size(), Priority.values().length);
        Assert.assertTrue(options.contains(Priority.HIGH.getPriority()));
        Assert.assertTrue(options.contains(Priority.LOW.getPriority()));
        Assert.assertTrue(options.contains(Priority.MEDIUM.getPriority()));
    }

    @Test(groups = "Enterprise4.2", dependsOnMethods = "getPriorityOptions")
    public void getSelectedPriorityOption()
    {
        newWorkflowPage.selectPriorityDropDown(Priority.MEDIUM);
        Assert.assertEquals(newWorkflowPage.getSelectedPriorityOption(), Priority.MEDIUM);

        newWorkflowPage.selectPriorityDropDown(Priority.HIGH);
        Assert.assertEquals(newWorkflowPage.getSelectedPriorityOption(), Priority.HIGH);

        newWorkflowPage.selectPriorityDropDown(Priority.LOW);
        Assert.assertEquals(newWorkflowPage.getSelectedPriorityOption(), Priority.LOW);
    }

}
