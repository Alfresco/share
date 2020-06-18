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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.web.config.util.BaseTest;
import org.junit.Test;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.XMLConfigService;

public class AIMSConfigTest extends BaseTest
{
    // public static final String PROPERTIES_RESOURCES = "classpath*:alfresco/module/*/share-config.properties";
    /*private static final String TEST_CONFIG_AIMS_BASIC_XML = "test-config-aims-basic.xml";
    protected XMLConfigService configService;

    protected List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add(TEST_CONFIG_AIMS_BASIC_XML);
        return result;
    }

    @Override
    public String getResourcesDir()
    {
        return "classpath:";
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

    }

    @Test
    public void testValidateIsEnableConfigProperties()
    {
        AIMSConfigElement aims = initConfigAIMS();
        assertFalse(aims.isEnabled());

    }

    @Test
    public void testValidateIsEnableSystemProperties()
    {
        System.setProperty("aims.config.enabled", "true");
        AIMSConfigElement aims = initConfigAIMS();
        assertTrue(aims.isEnabled());

    }

    @Test
    public void testValidateKeycloakProperties()
    {
        AIMSConfigElement aims = initConfigAIMS();
        AdapterConfig keycloakConfigElem = aims.getKeycloakConfigElem();
        assertNotNull(keycloakConfigElem);
        assertTrue("real should be alfresco.", keycloakConfigElem.getRealm().equals("alfresco"));
        assertTrue("resource should be alfresco.", keycloakConfigElem.getResource().equals("alfresco"));
        assertTrue("authServerUrl should be null.", (keycloakConfigElem.getAuthServerUrl() == null));
        assertTrue("sslRequired should be none.", keycloakConfigElem.getSslRequired().equals("none"));
        assertTrue("publicClient should be true.", keycloakConfigElem.isPublicClient());
        assertTrue("autodetectBearerOnly should be true.", keycloakConfigElem.isAutodetectBearerOnly());
        assertTrue("alwaysRefreshToken should be true.", keycloakConfigElem.isAlwaysRefreshToken());
        assertTrue("principalAttribute should be email.", keycloakConfigElem.getPrincipalAttribute().equals("email"));
        assertTrue("enableBasicAuth should be true.", keycloakConfigElem.isEnableBasicAuth());
    }

    @Test
    public void testValidateKeycloakConfigProperties()
    {
        System.setProperty("aims.config.realm", "test");
        System.setProperty("aims.config.resource", "test");
        System.setProperty("aims.config.authServerUrl", "test");
        System.setProperty("aims.config.sslRequired", "none");
        System.setProperty("aims.config.publicClient", "true");
        System.setProperty("aims.config.autodetectBearerOnly", "true");
        System.setProperty("aims.config.alwaysRefreshToken", "false");
        System.setProperty("aims.config.principalAttribute", "test");
        System.setProperty("aims.config.enableBasicAuth", "true");

        AIMSConfigElement aims = initConfigAIMS();
        AdapterConfig keycloakConfigElem = aims.getKeycloakConfigElem();
        assertNotNull(keycloakConfigElem);
        assertTrue("real should be test.", keycloakConfigElem.getRealm().equals("test"));
        assertTrue("resource should be test.", keycloakConfigElem.getResource().equals("test"));
        assertTrue("authServerUrl should be empty.", keycloakConfigElem.getAuthServerUrl().equals("test"));
        assertTrue("sslRequired should be none.", keycloakConfigElem.getSslRequired().equals("none"));
        assertTrue("publicClient should be true.", keycloakConfigElem.isPublicClient());
        assertTrue("autodetectBearerOnly should be true.", keycloakConfigElem.isAutodetectBearerOnly());
        assertFalse("alwaysRefreshToken should be false.", keycloakConfigElem.isAlwaysRefreshToken());
        assertTrue("principalAttribute should be test.", keycloakConfigElem.getPrincipalAttribute().equals("test"));
        assertTrue("enableBasicAuth should be true.", keycloakConfigElem.isEnableBasicAuth());

    }

    private AIMSConfigElement initConfigAIMS()
    {
        Resource[] resources;
        AIMSConfigElement aimsConfigElement = null;

        try
        {
            // define properties from a property file
            resources = new PathMatchingResourcePatternResolver().getResources(PROPERTIES_RESOURCES);

            configService = initXMLConfigServiceWithProperties(getConfigFiles(), resources);
            assertNotNull("configService was null.", configService);

            Config aimsConditionObj = configService.getConfig("AIMS");
            assertNotNull(aimsConditionObj);

            ConfigElement aimsConfigObj = aimsConditionObj.getConfigElement("aims");
            assertNotNull(aimsConfigObj);
            assertTrue("aimsConfigObj should be instanceof AIMSConfigElement.", aimsConfigObj instanceof AIMSConfigElement);

            aimsConfigElement = (AIMSConfigElement) aimsConfigObj;

        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

        return aimsConfigElement;

    }*/
}
