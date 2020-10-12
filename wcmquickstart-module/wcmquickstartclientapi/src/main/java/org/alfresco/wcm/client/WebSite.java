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
package org.alfresco.wcm.client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Web Site Interface
 * 
 * @author Roy Wetherall
 */
public interface WebSite extends Serializable
{
    /** Property constants */
    static final String PROP_HOSTNAME = "ws:hostName";
    static final String PROP_HOSTPORT = "ws:hostPort";
    static final String PROP_CONTEXT = "ws:webAppContext";
    static final String PROP_SITE_CONFIG = "ws:siteConfig";

    static final String CONFIG_IS_EDITORIAL = "isEditorial";

    /**
     * Gets the host name
     * 
     * @return String host name
     */
    String getHostName();

    /**
     * Gets the host port
     * 
     * @return int host port
     */
    int getHostPort();

    /**
     * Gets the web sites root section
     * 
     * @return Section root section
     */
    Section getRootSection();

    PathResolutionDetails resolvePath(String path);
    
    /**
     * 
     * @param path String
     * @return Asset
     */
    Asset getAssetByPath(String path);

    /**
     * Gets the child sections.
     * 
     * @return List<Section> child sections
     */
    List<Section> getSections();

    /**
     * Obtain the identifier of this website
     * 
     * @return String
     */
    String getId();

    /**
     * Get the web site's title
     * 
     * @return String
     */
    String getTitle();

    /**
     * Get the web site's title
     * 
     * @return String
     */
    String getDescription();

    /**
     * Get the asset which is the site logo
     * 
     * @return Asset
     */
    Asset getLogo();

    /**
     * Get the UGC service
     * 
     * @return UgcService
     */
    UgcService getUgcService();

    /**
     * Get a section by path
     * 
     * @param path String
     * @return Section
     */
    Section getSectionByPath(String path);

    /**
     * Get context of the webapp used to deliver this site. The context,
     * together with the host name and host port, is used to generate absolute
     * URLs for assets in this site when necessary
     * 
     * @return String
     */
    String getContext();

    /**
     * Obtain the map of configuration properties set on this website.
     * 
     * @return Map
     */
    Map<String, String> getConfig();

    /**
     * Returns true if this website has been marked as an editorial site. May be
     * used by templates to enable editorial features on the website.
     * 
     * @return boolean
     */
    boolean isEditorialSite();
}
