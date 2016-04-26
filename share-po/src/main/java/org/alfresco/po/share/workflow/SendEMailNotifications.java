package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;

/**
 * This enum hold the Type field in Task Details (My Tasks page)
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum SendEMailNotifications
{

    YES("Yes"),
    NO("No");

    private String value;

    SendEMailNotifications(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Returns {@link SendEMailNotifications} based on given value.
     * 
     * @param value String
     * @return {@link SendEMailNotifications}
     */
    public static SendEMailNotifications getValue(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (SendEMailNotifications notification : SendEMailNotifications.values())
        {
            if (value.equals(notification.value))
            {
                return notification;
            }
        }
        throw new IllegalArgumentException("Invalid SendEMailNotifications Value : " + value);
    }
}
