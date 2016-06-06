/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share;

import org.alfresco.po.Version;

/**
 * The Alfresco site type and version enum.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public enum AlfrescoVersion implements Version
{
    // The current
    Share(null, 5.0, true,true),
    Enterprise(null),
    Enterprise41(Enterprise, 4.1, false, false),
    Enterprise42(Enterprise, 4.2, true, false),
    Enterprise43(Enterprise, 4.3, true, false),
    Enterprise5(Enterprise, 5.0, true, true),
    Enterprise51(Enterprise, 5.1, true, true),
    Cloud(null, 2.0, true, true),
    CloudNonFacetSearch(Cloud, 2.0, true,false),
    MyAlfresco(Cloud, 2.1, true, true);

    private static final String ALFRESCO_VERSION_DOES_NOT_MATCH_ERROR = "version %s does not match to an existing AlfrescoVersion:"
            + " Enterprise41, Enterprise42, Enterprise43, Cloud1 or Cloud2 or MyAlfresco";
    private AlfrescoVersion parent;
    private Double version;
    private boolean dojoSuport;
    private boolean facetedSearch;

    private AlfrescoVersion(AlfrescoVersion parent)
    {
        this.parent = parent;
        this.version = 0.0;
        this.dojoSuport = false;
        this.facetedSearch = false;
    }

    /**
     * Constructor.
     * 
     * @param parent {@link AlfrescoVersion}
     * @param version Alfresco version identifier
     * @param dojoSupport Alfresco that have dojo in ui.
     */
    private AlfrescoVersion(AlfrescoVersion parent, Double version, boolean dojoSupport, boolean facetedSearch)
    {
        this.parent = parent;
        this.version = version;
        this.dojoSuport = dojoSupport;
        this.facetedSearch = facetedSearch;
    }

    /**
     * Checks if its a cloud base enum.
     * 
     * @return true if its cloud base enum
     */
    public boolean isCloud()
    {
        if (AlfrescoVersion.Cloud == this)
        {
            return true;
        }
        else if (parent != null && parent == Cloud)
        {
            return true;
        }
        return false;
    }

    /**
     * Flag to indicates if file upload is supported
     * by HTML5
     * 
     * @return true if supported which at present only cloud does
     */
    public boolean isFileUploadHtml5()
    {
        return !AlfrescoVersion.Enterprise41.equals(this);
    }

    /**
     * Create {@link AlfrescoVersion} from string.
     * 
     * @param value String alfresco version
     * @return {@link AlfrescoVersion} version
     */
    public static AlfrescoVersion fromString(String value)
    {
        if (value == null || value.trim().isEmpty())
        {
            return AlfrescoVersion.Share;
        }
        String version = value;
        if (value.startsWith("Enterprise") && value.contains("-"))
        {
            version = version.replace("-", "");
        }
        for (AlfrescoVersion alfrescoVersion : AlfrescoVersion.values())
        {
            if (version.equalsIgnoreCase(alfrescoVersion.name()))
            {
                return alfrescoVersion;
            }
        }
        throw new IllegalArgumentException(String.format(ALFRESCO_VERSION_DOES_NOT_MATCH_ERROR, value));
    }

    public Double getVersion()
    {
        return version;
    }

    public boolean isDojoSupported()
    {
        return dojoSuport;
    }

    public boolean isFacetedSearch()
    {
        return facetedSearch;
    }

    public void setFacetedSearch(boolean facetedSearch)
    {
        this.facetedSearch = facetedSearch;
    }
    

}
