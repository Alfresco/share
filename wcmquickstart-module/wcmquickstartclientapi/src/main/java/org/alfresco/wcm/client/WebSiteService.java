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