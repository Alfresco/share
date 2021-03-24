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
package org.alfresco.web.scripts;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Java controller to determine if the RSS dashlet should be enabled (it is disabled by default since MNT-22156).
 * Used by the following webscripts:
 *     org.alfresco.components.dashlets.rssfeed.get
 *     org.alfresco.components.dashlets.async-rssfeed.get
 *     org.alfresco.components.dashlets.addons.get
 *     org.alfresco.components.dashboard.customise-dashlets.get
 */
public class RssDashletStatusWebScript extends DeclarativeWebScript implements ApplicationContextAware
{
    private static final String RSSDASHLET_ENABLED = "rssdashlet.enabled";
    private static final String SHOW_DASHLET = "showDashlet";

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.context = applicationContext;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();

        boolean rssDashletStatus = Boolean.parseBoolean(System.getProperty(RSSDASHLET_ENABLED));
        model.put(SHOW_DASHLET, rssDashletStatus);

        return model;
    }
}
