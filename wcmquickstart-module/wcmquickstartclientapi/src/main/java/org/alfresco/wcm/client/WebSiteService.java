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

import java.util.Collection;

/**
 * Web Site Service Interface
 * 
 * @author Roy Wetherall
 * @author Brian Remmington
 */
public abstract class WebSiteService
{
    private static ThreadLocal<WebSite> currentWebsite = new ThreadLocal<WebSite>();

    /**
     * Gets all the web sites hosted on the repository
     * 
     * @return Collection<WebSite> web sites
     */
    public abstract Collection<WebSite> getWebSites();

    /**
     * Gets the web site that relates to the host name and port. Assumes the root context.
     * 
     * @param hostName
     *            host name
     * @param hostPort
     *            port number
     * @return WebSite web site, null if non found
     */
    public abstract WebSite getWebSite(String hostName, int hostPort);

    /**
     * Gets the website that relates to the specified host, port, and context path. For example, if your webapp
     * is deployed at "http://my.web.site/wqs" then the host would be "my.web.site", the port would be 80 (the default HTTP port)
     * and the context path would be "wqs".
     * @param hostName
     * @param hostPort
     * @param contextPath
     * @return The matching website or null if none found
     */
    public abstract WebSite getWebSite(String hostName, int hostPort, String contextPath);

    /**
     * Set the supplied website in a thread-local container to make it available
     * for all activity that subsequently takes place on the current thread
     * 
     * @param website
     */
    public static void setThreadWebSite(WebSite website)
    {
        currentWebsite.set(website);
    }

    /**
     * Retrieve the WebSite object that has most recently been set on the
     * current thread via a call to {@link #setThreadWebSite(WebSite)}.
     * 
     * @return The WebSite object most recently set on this thread or null if no
     *         object has been set.
     */
    public static WebSite getThreadWebSite()
    {
        return currentWebsite.get();
    }
}