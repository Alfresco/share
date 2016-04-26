package org.alfresco.po.share.dashlet;

/**
 * Contains the owner filters on Site Activities Dashlet.
 * 
 * @author Jamie Allison
 * @since 4.3
 */
public enum SiteActivitiesUserFilter
{
    MY_ACTIVITIES("My activities"),
    OTHERS_ACTIVITIES("Everyone else's activities"),
    EVERYONES_ACTIVITIES("Everyone's activities"),
    IM_FOLLOWING("I'm following");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private SiteActivitiesUserFilter(String description)
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

    public static SiteActivitiesUserFilter getFilter(String description)
    {
        for (SiteActivitiesUserFilter filter : SiteActivitiesUserFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

}