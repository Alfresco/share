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
import org.alfresco.web.site.servlet.config.AIMSConfig.AIMSConfigElement;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.XMLConfigService;

public class AIMSConfigTest extends BaseTest
{

    public static final String PROPERTIES_RESOURCES = "classpath*:alfresco/module/*/share-config.properties";
    private static final String TEST_CONFIG_AIMS_BASIC_XML = "test-config-aims-basic.xml";
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
    public void testValidateConfigProperties()
    {
        AIMSConfigElement aims = initConfigAIMS();
        assertFalse(aims.getEnabled());

    }

    @Test
    public void testValidateSystemProperties()
    {
       /** System.setProperty("aims.config.enabled", "true");
        AIMSConfigElement aims = initConfigAIMS();
        assertTrue(aims.getEnabled());*/
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

            aimsConfigElement = AIMSConfigElement.buildObject(aimsConfigObj);
            assertNotNull(aimsConfigObj);

        }
        catch (IOException e)
        {
            throw new ExceptionInInitializerError(e);
        }

        return aimsConfigElement;

    }

}
