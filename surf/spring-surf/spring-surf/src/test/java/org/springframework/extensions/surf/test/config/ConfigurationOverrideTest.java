/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.surf.test.config;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.test.TestCaseSetup;
import org.testng.Assert;

/**
 * Tests the Surf API directly using mock objects
 * 
 * @author muzquiano
 */
public class ConfigurationOverrideTest
{
	public void testConfig()
	{
		// assert default settings
	    WebFrameworkConfigElement config = TestCaseSetup.getServiceRegistry().getWebFrameworkConfiguration();
		Assert.assertEquals("testcomponentchrome1", config.getDefaultComponentChrome());
		Assert.assertEquals("testregionchrome1", config.getDefaultRegionChrome());
		Assert.assertEquals("testsiteconfig1", config.getDefaultSiteConfigurationId());
		Assert.assertEquals("testtheme1", config.getDefaultThemeId());
		Assert.assertEquals("testformat1", config.getDefaultFormatId());
		
		Assert.assertEquals("webframework.factory.user.test1", config.getDefaultUserFactoryId());
		
		UserFactory userFactory = TestCaseSetup.getServiceRegistry().getUserFactory();
		Assert.assertEquals("org.springframework.extensions.surf.config.TestUserFactory", userFactory.getClass().getName());
		
		// check that our formats exist
		Assert.assertNotNull(config.getFormatDescriptor("testformat1"));
		Assert.assertNotNull(config.getFormatDescriptor("testformat2"));
	}
}
