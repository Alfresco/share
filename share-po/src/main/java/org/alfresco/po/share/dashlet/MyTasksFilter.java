
package org.alfresco.po.share.dashlet;

/**
 * Contains all the possible filters on My Tasks Dashlet.
 * Created by olga lokhach
 */

public enum MyTasksFilter
{
    ACTIVE_TASKS("Active Tasks"),
    COMPLETED_TASKS("Completed Tasks"),
    HIGH_PRIORITY_TASKS("High Priority Tasks"),
    TASKS_DUE_TODAY("Tasks Due Today"),
    TASKS_ASSIGNED_TO_ME("Tasks Assigned to Me"),
    UNASSIGNED("Unassigned (Pooled Tasks)"),
    OVERDUE_TASKS("Overdue Tasks");

    private final String description;

    /**
     * Set the description for the each filter.
     *
     * @param description - The Filter Description on HTML Page.
     */
    private MyTasksFilter(String description)
    {
        this.description = description;
    }

    /**
     * Gets description.
     *
     * @return String description
     */
    public String getDescription()
    {
        return this.description;
    }

    public static MyTasksFilter getFilter(String description)
    {
        for (MyTasksFilter filter : MyTasksFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

}
