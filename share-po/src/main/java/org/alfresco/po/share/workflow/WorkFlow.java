package org.alfresco.po.share.workflow;

/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import org.alfresco.po.HtmlPage;

/**
 * A WorkFlow interface.
 *
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public interface WorkFlow extends HtmlPage
{
    /**
     * Gets workflow subpage in the WorkFlow view.
     *
     * @param formDetails WorkFlowFormDetails
     * @return HtmlPage page object
     * @throws InterruptedException
     */
    HtmlPage startWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException;

    /**
     * Enters message in the message box.
     *
     * @param messageString String
     */
    void enterMessageText(String messageString);

    /**
     * Clicks on Select button for selecting reviewers
     */
    AssignmentPage selectReviewer();

    /**
     * Enters due date in the date box.
     *
     * @param date String
     */
    void enterDueDateText(String date);

    /**
     * Cancels creation of the workflow.
     *
     * @param formDetails WorkFlowFormDetails
     * @return HtmlPage page object
     * @throws InterruptedException
     */
    HtmlPage cancelCreateWorkflow(WorkFlowFormDetails formDetails) throws InterruptedException;
}
