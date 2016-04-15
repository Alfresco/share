package org.alfresco.po.share.dashlet;

/**
 * Contains all the possible filters on Site Content Dashlet.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public enum SiteContentFilter
{
    I_AM_EDITING("I'm Editing"),
    I_HAVE_RECENTLY_MODIFIED("I've Recently Modified"),
    MY_FAVOURITES("My Favorites"),
    SYNCED_CONTENT("Synced content"),
    SYNCED_WITH_ERRORS("Synced with Errors");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private SiteContentFilter(String description)
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

    public static SiteContentFilter getFilter(String description)
    {
        for (SiteContentFilter filter : SiteContentFilter.values())
        {
            if (description.contains(filter.getDescription()))
            {
                return filter;
            }
        }
        return null;
    }

}