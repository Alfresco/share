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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;

public class AIMSConfig implements ApplicationContextAware
{
    private ApplicationContext context;
    private ConfigService configService;

    public void init()
    {
        this.configService = (ConfigService) this.context.getBean("web.config");
    }

    public boolean isAIMSEnabled()
    {
        Config AIMSConfigCondition = this.configService.getConfig(AIMSConfigElement.AIMS_CONFIG_CONDITION);
        AIMSConfigElement config = null;

        if (AIMSConfigCondition != null)
        {
            config = (AIMSConfigElement) AIMSConfigCondition.getConfigElement(AIMSConfigElement.AIMS_CONFIG_ELEMENT);
        }

        return config != null && config.getEnabled();
    }

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.context = applicationContext;
    }
}
