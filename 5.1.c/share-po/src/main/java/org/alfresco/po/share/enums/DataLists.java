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
package org.alfresco.po.share.enums;

/**
 * Enum to include all Data Lists types
 *
 * @author Marina.Nenadovets
 */
public enum DataLists
{
    CONTACT_LIST("Contact List"),
    EVENT_AGENDA("Event Agenda"),
    EVENT_LIST("Event List"),
    ISSUE_LIST("Issue List"),
    LOCATION_LIST("Location List"),
    MEETING_AGENDA("Meeting Agenda"),
    TASK_LIST_ADV("Task List (Advanced)"),
    TASK_LIST_SPL("Task List (Simple)"),
    TO_DO_LIST("To Do List");

    public final String listName;

    DataLists(String listName)
    {
        this.listName = listName;
    }

    public String getListName()
    {
        return listName;
    }
}
