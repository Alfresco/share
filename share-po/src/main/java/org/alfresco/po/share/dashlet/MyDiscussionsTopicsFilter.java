
package org.alfresco.po.share.dashlet;

/**
 * Contains My Discussion topics filters
 * 
 * @author jcule
 */
public enum MyDiscussionsTopicsFilter
{

    MY_TOPICS("My Topics"), 
    ALL_TOPICS("All Topics");

    private final String description;

    /**
     * Set the description for the each filter.
     * 
     * @param description - The Filter Description on HTML Page.
     */
    private MyDiscussionsTopicsFilter(String description)
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

    public static MyDiscussionsTopicsFilter getFilter(String description)
    {
        for (MyDiscussionsTopicsFilter filter : MyDiscussionsTopicsFilter.values())
        {
            if (description.contains(filter.getDescription()))
                return filter;
        }
        return null;
    }

}
