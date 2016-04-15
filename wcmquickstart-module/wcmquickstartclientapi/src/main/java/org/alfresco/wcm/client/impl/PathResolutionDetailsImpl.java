package org.alfresco.wcm.client.impl;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.PathResolutionDetails;
import org.alfresco.wcm.client.Section;

public class PathResolutionDetailsImpl implements PathResolutionDetails
{
    private Asset asset = null;
    private Section section = null;
    private boolean redirect = false;
    private String redirectLocation = null;

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.PathResolutionDetails#getAsset()
     */
    public Asset getAsset()
    {
        return asset;
    }

    public void setAsset(Asset asset)
    {
        this.asset = asset;
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.PathResolutionDetails#getSection()
     */
    public Section getSection()
    {
        return section;
    }

    public void setSection(Section section)
    {
        this.section = section;
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.PathResolutionDetails#isRedirect()
     */
    public boolean isRedirect()
    {
        return redirect;
    }

    public void setRedirect(boolean redirect)
    {
        this.redirect = redirect;
    }

    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.PathResolutionDetails#getRedirectLocation()
     */
    public String getRedirectLocation()
    {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation)
    {
        this.redirectLocation = redirectLocation;
    }

}
