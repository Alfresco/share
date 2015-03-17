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
