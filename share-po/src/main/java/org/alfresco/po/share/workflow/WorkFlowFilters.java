/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Aliaksei Boole
 */
public class WorkFlowFilters extends HtmlElement
{
    private static final Logger logger = Logger.getLogger(WorkFlowFilters.class);
    private final static String PRIORITY_XPATH_TEMPLATE = "//a[@rel='%s']";
    private final static String WORKFLOW_TYPE_TEMPLATE = "//a[text()='%s']";

    public WorkFlowFilters(WebDrone drone)
    {
        super(drone);
    }

    public HtmlPage select(StartedFilter startedFilter)
    {
        checkNotNull(startedFilter);
        drone.findAndWait(startedFilter.by).click();
        waitUntilAlert();
        return drone.getCurrentPage().render();
    }

    public HtmlPage select(DueFilters dueFilters)
    {
        checkNotNull(dueFilters);
        drone.findAndWait(dueFilters.by).click();
        waitUntilAlert();
        return drone.getCurrentPage().render();
    }

    public HtmlPage select(Priority priority)
    {
        checkNotNull(priority);
        By xpath = By.xpath(String.format(PRIORITY_XPATH_TEMPLATE, priority.getValue()));
        drone.findAndWait(xpath).click();
        waitUntilAlert();
        return drone.getCurrentPage().render();
    }

    public HtmlPage select(WorkFlowType workFlowType)
    {
        checkNotNull(workFlowType);
        By xpath = By.xpath(String.format(WORKFLOW_TYPE_TEMPLATE, workFlowType.getTitle()));
        drone.findAndWait(xpath).click();
        waitUntilAlert();
        return drone.getCurrentPage().render();
    }

    protected void waitUntilAlert()
    {
        final long WAIT_ALERT_PRESENT = 1; //hardcoded - possible temporary excess in most cases.
        try
        {
            By AlertMessage = By.xpath(".//*[@id='message']/div/span");
            drone.waitUntilElementPresent(AlertMessage, WAIT_ALERT_PRESENT);
            drone.waitUntilElementDeletedFromDom(AlertMessage, 3);
        }
        catch (TimeoutException ex)
        {
            if (logger.isDebugEnabled())
            {
                logger.error("Alert message hide quickly", ex);
            }
        }
    }
}
