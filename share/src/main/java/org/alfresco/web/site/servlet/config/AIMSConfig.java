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

import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;

public class AIMSConfig
{
    private boolean enabled;
    private ConfigService configService;
    private AdapterConfig adapterConfig;

    /**
     *
     */
    public void init()
    {
        Config config = this.configService.getConfig("AIMS");
        this.setEnabled(Boolean.parseBoolean(config.getConfigElementValue("enabled")));
        this.initAdapterConfig(config);
    }

    /**
     *
     * @param configService ConfigService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    /**
     *
     * @param enabled boolean
     */
    private void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     *
     * @return boolean
     */
    public boolean isEnabled()
    {
        return this.enabled;
    }

    /**
     *
     * @param config Config
     */
    private void initAdapterConfig(Config config)
    {
        this.adapterConfig = new AdapterConfig();

        this.adapterConfig.setRealm(config.getConfigElementValue("realm"));
        this.adapterConfig.setResource(config.getConfigElementValue("resource"));
        this.adapterConfig.setAuthServerUrl(config.getConfigElementValue("authServerUrl"));
        this.adapterConfig.setSslRequired(config.getConfigElementValue("sslRequired"));
        this.adapterConfig.setPublicClient(Boolean.parseBoolean(config.getConfigElementValue("publicClient")));
        this.adapterConfig.setAutodetectBearerOnly(Boolean.parseBoolean(config.getConfigElementValue("autodetectBearerOnly")));
        this.adapterConfig.setAlwaysRefreshToken(Boolean.parseBoolean(config.getConfigElementValue("alwaysRefreshToken")));
        this.adapterConfig.setPrincipalAttribute(config.getConfigElementValue("principalAttribute"));
        this.adapterConfig.setEnableBasicAuth(Boolean.parseBoolean(config.getConfigElementValue("enableBasicAuth")));
    }

    /**
     *
     * @return AdapterConfig
     */
    public AdapterConfig getAdapterConfig()
    {
        return this.adapterConfig;
    }
}
