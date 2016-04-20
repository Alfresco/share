package org.alfresco.po.share.enums;

/**
 * This enums used to describe the Dashlets.
 * 
 * @author cbairaajoni
 * @since v1.0
 */
public enum Dashlets
{
    ALFRESCO_ADDONS_RSS_FEED("Alfresco Add-ons RSS Feed"),
    DATA_LISTS("Data Lists"),
    IMAGE_PREVIEW("Image Preview"),
    MY_ACTIVITIES("My Activities"),
    MY_DOCUMENTS("My Documents"),
    MY_DISCUSSIONS("My Discussions"),
    MY_SITES("My Sites"),
    MY_TASKS("My Tasks"),
    MY_CALENDAR("My Calendar"),
    MY_PROFILE("My Profile"),
    RSS_FEED("RSS Feed"),
    SAVED_SEARCH("Saved Search"),
    SITE_ACTIVITIES("Site Activities"),
    SITE_CALENDAR("Site Calendar"),
    SITE_CONTENT("Site Content"),
    SITE_DATA_LISTS("Site Data Lists"),
    SITE_LINKS("Site Links"),
    SITE_NOTICE("Site Notice"),
    SITE_MEMBERS("Site Members"),
    SITE_PROFILE("Site Profile"),
    SITE_SEARCH("Site Search"),
    SITE_CONTENT_REPORT("Site File Type Breakdown"),
    TOP_SITE_CONTRIBUTOR_REPORT("Site Contributor Breakdo..."),
    WEB_VIEW("Web View"),
    WELCOME_SITE("Welcome Site"),
    WIKI("Wiki"),
    MY_MEETING_WORKSPACES("My Meeting Workspaces"),
    CUSTOM_SITE_REPORTS("Custom Site Reports"),
    USER_ACTIVITY_REPORT("User Activity Report"),
    HOT_CONTENT_REPORT("Hot Content Report"),
    WEB_QUICK_START("Web Quick Start"),
    CONTENT_I_AM_EDITING ("Content I\'m Editing");
    

    private String dashletName;

    private Dashlets(String dashlet)
    {
        dashletName = dashlet;
    }

    public String getDashletName()
    {
        return dashletName;
    }
}