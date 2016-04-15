
package org.alfresco.po.share.workflow;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Abhijeet Bharade
 */
public enum KeepContentStrategy
{

    KEEPCONTENT("Keep content synced on cloud", "documentsSynced"),
    KEEPCONTENTREMOVESYNC("Keep content on cloud and remove sync", "documentsUnSynced"),
    DELETECONTENT("Delete content on cloud and remove sync", "documentsDelete");

    private String strategy;
    private String value;

    KeepContentStrategy(String strategy, String value)
    {
        this.strategy = strategy;
        this.value = value;
    }

    public String getStrategy()
    {
        return strategy;
    }

    public String getValue()
    {
        return value;
    }

    /**
     * Returns {@link KeepContentStrategy} based on given value.
     * 
     * @param stringValue String
     * @return {@link KeepContentStrategy}
     */
    public static KeepContentStrategy getKeepContentStrategy(String stringValue)
    {
        if (StringUtils.isEmpty(stringValue))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (KeepContentStrategy strategy : KeepContentStrategy.values())
        {
            if (stringValue.equalsIgnoreCase(strategy.strategy))
            {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Invalid Keep Content Strategy Value : " + stringValue);
    }

}
