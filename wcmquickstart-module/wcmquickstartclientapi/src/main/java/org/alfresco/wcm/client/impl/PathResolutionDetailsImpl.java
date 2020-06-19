/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
