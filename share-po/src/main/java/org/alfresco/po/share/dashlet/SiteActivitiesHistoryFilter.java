package org.alfresco.po.share.dashlet;

/**
 * Contains the history filters on Site Activities Dashlet.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public enum SiteActivitiesHistoryFilter
{
    TODAY("today"),
    SEVEN_DAYS("in the last 7 days"),
    FOURTEEN_DAYS("in the last 14 days"),
    TWENTY_EIGHT_DAYS("in the last 28 days");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private SiteActivitiesHistoryFilter(String description)
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

    public static SiteActivitiesHistoryFilter getFilter(String description)
    {
        for (SiteActivitiesHistoryFilter filter : SiteActivitiesHistoryFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }
}