
package org.alfresco.po.share.workflow;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhijeet Bharade
 */
public enum Priority
{
    HIGH("High", "1"),
    MEDIUM("Medium", "2"),
    LOW("Low", "3");

    private String priority;
    private String value;

    Priority(String priority, String value)
    {
        this.priority = priority;
        this.value = value;
    }

    public String getPriority()
    {
        return priority;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Returns {@link Priority} based on given value.
     * 
     * @param stringValue String
     * @return {@link Priority}
     */
    public static Priority getPriority(String stringValue)
    {
        if (StringUtils.isEmpty(stringValue))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (Priority p : Priority.values())
        {
            if (stringValue.contains(p.priority))
            {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid Priority Value : " + stringValue);
    }
}
