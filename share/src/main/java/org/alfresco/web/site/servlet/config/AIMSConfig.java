/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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
package org.alfresco.web.site.servlet.config;

import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;

public class AIMSConfig implements ApplicationContextAware
{
    private ApplicationContext context;
    private boolean enabled;
    private AdapterConfig adapterConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.context = applicationContext;
    }

    public void init()
    {
        ConfigService configService = (ConfigService) this.context.getBean("web.config");
        Config config = configService.getConfig("AIMS");

        String enabled = config.getConfigElementValue("enabled");
        if (enabled != null && enabled.length() > 0)
        {
            this.enabled = Boolean.parseBoolean(enabled);
        }

        this.adapterConfig = new AdapterConfig();

        String realm = config.getConfigElementValue("realm");
        if (realm != null && realm.length() > 0)
        {
            this.adapterConfig.setRealm(realm);
        }

        String resource = config.getConfigElementValue("resource");
        if (resource != null && resource.length() > 0)
        {
            this.adapterConfig.setResource(resource);
        }

        String authServerUrl = config.getConfigElementValue("authServerUrl");
        if (authServerUrl != null && authServerUrl.length() > 0)
        {
            this.adapterConfig.setAuthServerUrl(authServerUrl);
        }

        String sslRequired = config.getConfigElementValue("sslRequired");
        if (sslRequired != null && sslRequired.length() > 0)
        {
            this.adapterConfig.setSslRequired(sslRequired);
        }

        String publicClient = config.getConfigElementValue("publicClient");
        if (publicClient != null && publicClient.length() > 0)
        {
            this.adapterConfig.setPublicClient(Boolean.parseBoolean(publicClient));
        }

        String autodetectBearerOnly = config.getConfigElementValue("autodetectBearerOnly");
        if (autodetectBearerOnly != null && autodetectBearerOnly.length() > 0)
        {
            this.adapterConfig.setAutodetectBearerOnly(Boolean.parseBoolean(autodetectBearerOnly));
        }

        String alwaysRefreshToken = config.getConfigElementValue("alwaysRefreshToken");
        if (alwaysRefreshToken != null && alwaysRefreshToken.length() > 0)
        {
            this.adapterConfig.setAlwaysRefreshToken(Boolean.parseBoolean(alwaysRefreshToken));
        }

        String principalAttribute = config.getConfigElementValue("principalAttribute");
        if (principalAttribute != null && principalAttribute.length() > 0)
        {
            this.adapterConfig.setPrincipalAttribute(principalAttribute);
        }

        String enableBasicAuth = config.getConfigElementValue("enableBasicAuth");
        if (enableBasicAuth != null && enableBasicAuth.length() > 0)
        {
            this.adapterConfig.setEnableBasicAuth(Boolean.parseBoolean(enableBasicAuth));
        }
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public AdapterConfig getAdapterConfig()
    {
        return this.adapterConfig;
    }
}
