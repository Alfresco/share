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
package org.alfresco.web.site.servlet.config;

import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;


public class CookieHeadersConfig
{
    private boolean enableSecure;
    private String sameSite;
    private ConfigService configService;

    public void init()
    {
        Config config = this.configService.getConfig("COOKIES");
        this.setEnableSecure(Boolean.parseBoolean(config.getConfigElementValue("secure")));
        this.setSameSite(config.getConfigElementValue("sameSite"));
    }

    /**
     * @param configService ConfigService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     * @return boolean
     */
    public boolean isEnableSecure()
    {
        return this.enableSecure;
    }

    /**
     * @param enableSecure boolean
     */
    private void setEnableSecure(boolean enableSecure)
    {
        this.enableSecure = enableSecure;
    }

    public String getSameSite()
    {
        return sameSite;
    }

    public void setSameSite(String sameSite)
    {
        this.sameSite = sameSite;
    }

}
