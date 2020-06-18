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
package org.alfresco.web.site;

import com.google.common.base.Strings;

import java.io.Serializable;

/**
 * A simple structure that supplies the path to Alfresco Share and Alfresco Repository from JVM system properties.
 *
 * @author Alexandru Epure
 */
public class UrlUtil implements Serializable
{
    public static String alfrescoContext;
    public static String alfrescoPort;
    public static String alfrescoProtocol;
    public static String alfrescoHost;
    public static String alfrescoProxy;
    public static String shareContext;
    public static String sharePort;
    public static String shareProtocol;
    public static String shareHost;
    public static String shareProxy;

    private final String repoURL;
    private final String shareURL;

    public UrlUtil()
    {
        alfrescoContext = System.getProperty("alfresco.context");
        alfrescoPort = System.getProperty("alfresco.port");
        alfrescoProtocol = System.getProperty("alfresco.protocol");
        alfrescoHost = System.getProperty("alfresco.host");
        alfrescoProxy = System.getProperty("alfresco.proxy");
        shareContext = System.getProperty("share.context");
        sharePort = System.getProperty("share.port");
        shareProtocol = System.getProperty("share.protocol");
        shareHost = System.getProperty("share.host");
        shareProxy = System.getProperty("share.proxy");
        this.repoURL = getRepoURL();
        this.shareURL = getShareURL();
    }

    public String getRepoURL()
    {
        if (!Strings.isNullOrEmpty(alfrescoProxy))
        {
            return alfrescoProxy;
        }
        if (!Strings.isNullOrEmpty(alfrescoProtocol) && !Strings.isNullOrEmpty(alfrescoHost) && !Strings.isNullOrEmpty(alfrescoPort))
        {
            return alfrescoProtocol + "://" + alfrescoHost + ":" + alfrescoPort;
        }
        return "";
    }

    public String getShareURL()
    {
        if (!Strings.isNullOrEmpty(shareProxy))
        {
            return shareProxy;
        }
        if (!Strings.isNullOrEmpty(shareProtocol) && !Strings.isNullOrEmpty(shareHost) && !Strings.isNullOrEmpty(sharePort))
        {
            return shareProtocol + "://" + shareHost + ":" + sharePort;
        }
        return "";
    }
}
