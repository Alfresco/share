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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.keycloak.common.enums.SslRequired;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class AIMSConfig
{
    private static final Log logger = LogFactory.getLog(AIMSConfig.class);

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
        String value, methodName;
        this.adapterConfig = new AdapterConfig();

        for (Map.Entry<String, ConfigElement> configElement: config.getConfigElements().entrySet())
        {
            value = configElement.getValue().getValue();
            methodName = "set" + StringUtils.capitalize(configElement.getKey());

            // Skip null or empty values
            if (value == null)
            {
                continue;
            }

            // Loop through AdapterConfig setter methods and call the setter for each value available from properties
            for (Method method: this.adapterConfig.getClass().getMethods())
            {
                try
                {
                    if (method.getName().equals(methodName) && method.getParameterCount() == 1)
                    {
                        if (method.getParameterTypes()[0] == String.class)
                        {
                            try
                            {
                                // Special case of ssl-required; make sure we don't set an invalid value
                                if (methodName.equals("setSslRequired"))
                                {
                                    SslRequired.valueOf(value.toUpperCase());
                                }
                                method.invoke(this.adapterConfig, value);
                            }
                            catch (IllegalArgumentException e)
                            {
                                method.invoke(this.adapterConfig, SslRequired.EXTERNAL.toString().toLowerCase());
                            }
                        }
                        else if (method.getParameterTypes()[0] == boolean.class)
                        {
                            method.invoke(this.adapterConfig, Boolean.parseBoolean(value));
                        }
                    }
                }
                catch (InvocationTargetException | IllegalAccessException e)
                {
                    logger.debug(e.getMessage());
                }
            }
        }
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
