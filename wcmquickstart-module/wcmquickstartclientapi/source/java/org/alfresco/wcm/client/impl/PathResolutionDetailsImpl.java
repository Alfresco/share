/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
