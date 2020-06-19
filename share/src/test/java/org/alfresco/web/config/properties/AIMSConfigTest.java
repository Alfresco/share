/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
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
package org.alfresco.web.config.properties;

import org.alfresco.web.config.util.BaseTest;
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.xml.XMLConfigService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AIMSConfigTest extends BaseTest
{
    public static final String CLASSPATH_SHARE_CONFIG_PROPERTIES = "classpath*:alfresco/module/*/share-config.properties";
    private static final String TEST_CONFIG_AIMS_BASIC_XML = "test-config-aims-basic.xml";

    @Before public void setUp()
    {
    }

    private List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add(TEST_CONFIG_AIMS_BASIC_XML);
        return result;
    }

    public String getResourcesDir()
    {
        return "classpath:";
    }

    /**
     *
     * @return
     */
    private AIMSConfig initAIMSConfig()
    {
        Resource[] resources;
        AIMSConfig aimsConfig = new AIMSConfig();

        try
        {
            // Define properties from a property file
            resources = new PathMatchingResourcePatternResolver().getResources(CLASSPATH_SHARE_CONFIG_PROPERTIES);
            XMLConfigService configService = initXMLConfigServiceWithProperties(this.getConfigFiles(), resources);
            Assert.assertNotNull(configService);

            Config config = configService.getConfig("AIMS");
            Assert.assertNotNull(config);

            aimsConfig.setConfigService(configService);
            aimsConfig.init();
        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

        return aimsConfig;
    }

    public void testEnabledPropertyIsFalseByDefault()
    {
        AIMSConfig aimsConfig = this.initAIMSConfig();
        Assert.assertFalse(aimsConfig.isEnabled());
    }

    public void testFromSystemEnvironmentAreSetCorrectly()
    {
        System.setProperty("aims.enabled", "false");
        System.setProperty("aims.realm", "alfresco");
        System.setProperty("aims.resource", "alfresco");
        System.setProperty("aims.authServerUrl", "http://localhost:8080/auth");
        System.setProperty("aims.sslRequired", "none");
        System.setProperty("aims.publicClient", "true");
        System.setProperty("aims.autodetectBearerOnly", "true");
        System.setProperty("aims.alwaysRefreshToken", "true");
        System.setProperty("aims.principalAttribute", "email");
        System.setProperty("aims.enableBasicAuth", "true");

        AIMSConfig aimsConfig = initAIMSConfig();

        Assert.assertEquals(Boolean.parseBoolean(System.getProperty("aims.enabled")), aimsConfig.isEnabled());
        Assert.assertEquals(System.getProperty("aims.realm"), aimsConfig.getAdapterConfig().getRealm());
        Assert.assertEquals(System.getProperty("aims.resource"), aimsConfig.getAdapterConfig().getResource());
        Assert.assertEquals(System.getProperty("aims.authServerUrl"), aimsConfig.getAdapterConfig().getAuthServerUrl());
        Assert.assertEquals(System.getProperty("aims.sslRequired"), aimsConfig.getAdapterConfig().getSslRequired());

        Assert.assertEquals(Boolean.parseBoolean(System.getProperty("aims.publicClient")),
            aimsConfig.getAdapterConfig().isPublicClient());

        Assert.assertEquals(Boolean.parseBoolean(System.getProperty("aims.autodetectBearerOnly")),
            aimsConfig.getAdapterConfig().isAutodetectBearerOnly());

        Assert.assertEquals(Boolean.parseBoolean(System.getProperty("aims.alwaysRefreshToken")),
            aimsConfig.getAdapterConfig().isAlwaysRefreshToken());

        Assert.assertEquals(System.getProperty("aims.principalAttribute"), aimsConfig.getAdapterConfig().getPrincipalAttribute());

        Assert.assertEquals(Boolean.parseBoolean(System.getProperty("aims.enableBasicAuth")),
            aimsConfig.getAdapterConfig().isEnableBasicAuth());
    }
}
