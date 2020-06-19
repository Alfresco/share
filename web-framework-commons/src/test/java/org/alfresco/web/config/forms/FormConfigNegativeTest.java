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
package org.alfresco.web.config.forms;

import org.alfresco.util.BaseTest;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.XMLConfigService;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service.
 * 
 * @author Neil McErlean
 */
public class FormConfigNegativeTest extends BaseTest
{
    private static final String TEST_CONFIG_FORMS_NEGATIVE_XML = "test-config-forms-negative.xml";
	private XMLConfigService configService;
    private Config globalConfig;
    private ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormConfigElement formConfigElement;
    protected DefaultControlsConfigElement defltCtrlsConfElement;
    
    @Override
	public String getResourcesDir() {
		return "classpath:";
	}
    
    public void testInvalidConfigXmlShouldProduceNullConfigElements()
    {
        configService = initXMLConfigService(TEST_CONFIG_FORMS_NEGATIVE_XML);
        assertNotNull("configService was null.", configService);

        Config contentConfig = configService.getConfig("content");
        assertNotNull("contentConfig was null.", contentConfig);

        ConfigElement confElement = contentConfig.getConfigElement("form");
        assertNull("confElement should be null.", confElement);

        globalConfig = configService.getGlobalConfig();

        globalDefaultControls = globalConfig
                .getConfigElement("default-controls");
        assertNull("global default-controls element should be null",
                globalDefaultControls);

        globalConstraintHandlers = globalConfig
                .getConfigElement("constraint-handlers");
        assertNull("global constraint-handlers element should be null",
                globalConstraintHandlers);
     }
}