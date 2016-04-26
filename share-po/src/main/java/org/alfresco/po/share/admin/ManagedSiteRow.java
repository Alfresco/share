package org.alfresco.po.share.admin;

/**
 * Representation of a Site in Admin > Manage Sites.
 * 
 * @author Richard Smith
 */
public class ManagedSiteRow
{

    private String siteName;
    private String siteDescription;
    private VisibilityDropDown visibility;
    private ActionsSet actions;

    /**
     * Instantiates a new manage site row.
     */
    public ManagedSiteRow()
    {
    }

    /**
     * Instantiates a new manage site row.
     * 
     * @param siteName the site name
     */
    public ManagedSiteRow(String siteName)
    {
        this.siteName = siteName;
    }

    /**
     * Instantiates a new manage site row.
     * 
     * @param siteName the site name
     * @param siteDescription the site description
     * @param visibility the visibility drop down
     * @param actions the actions set
     */
    public ManagedSiteRow(String siteName, String siteDescription, VisibilityDropDown visibility, ActionsSet actions)
    {
        this.siteName = siteName;
        this.siteDescription = siteDescription;
        this.visibility = visibility;
        this.actions = actions;
    }

    /**
     * Gets the site name.
     * 
     * @return the site name
     */
    public String getSiteName()
    {
        return siteName;
    }

    /**
     * Sets the site name.
     * 
     * @param siteName the new site name
     */
    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    /**
     * Gets the site description.
     * 
     * @return the site description
     */
    public String getSiteDescription()
    {
        return siteDescription;
    }

    /**
     * Sets the site description.
     * 
     * @param siteDescription the new site description
     */
    public void setSiteDescription(String siteDescription)
    {
        this.siteDescription = siteDescription;
    }

    /**
     * Gets the visibility.
     * 
     * @return the visibility
     */
    public VisibilityDropDown getVisibility()
    {
        return visibility;
    }

    /**
     * Sets the visibility.
     * 
     * @param visibility the new visibility
     */
    public void setVisibility(VisibilityDropDown visibility)
    {
        this.visibility = visibility;
    }

    /**
     * Gets the actions.
     * 
     * @return the actions
     */
    public ActionsSet getActions()
    {
        return actions;
    }

    /**
     * Sets the actions.
     * 
     * @param actions the new actions
     */
    public void setActions(ActionsSet actions)
    {
        this.actions = actions;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ManagedSiteRow other = (ManagedSiteRow) obj;
        if (siteName == null)
        {
            if (other.siteName != null)
                return false;
        }
        else if (!siteName.equals(other.siteName))
            return false;
        return true;
    }
}