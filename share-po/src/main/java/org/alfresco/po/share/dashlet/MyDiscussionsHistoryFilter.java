
package org.alfresco.po.share.dashlet;

/**
 * Contains topics history filters
 * 
 * @author jcule
 */
public enum MyDiscussionsHistoryFilter
{

    LAST_DAY_TOPICS("Topics updated in the last day"), 
    SEVEN_DAYS_TOPICS("Topics updated in the last 7 days"), 
    FOURTEEN_DAYS_TOPICS("Topics updated in the last 14 days"), 
    TWENTY_EIGHT_DAYS_TOPICS("Topics updated in the last 28 days");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private MyDiscussionsHistoryFilter(String description)
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

    public static MyDiscussionsHistoryFilter getFilter(String description)
    {
        for (MyDiscussionsHistoryFilter filter : MyDiscussionsHistoryFilter.values())
        {
            if (description.contains(filter.getDescription()))
                return filter;
        }
        return null;
    }

}
