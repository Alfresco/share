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

package org.springframework.extensions.surf.test.groovy;

import org.springframework.extensions.surf.test.TestCaseSetup;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;

/**
 * Tests to ensure that script processor extensions work in Surf
 * 
 * @author muzquiano
 */
public class GroovyTest
{
	public void testGroovy() throws Exception
	{
		MockHttpServletRequest req = new MockHttpServletRequest(TestCaseSetup.getServletContext(), "GET", "/test/groovy1");
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		TestCaseSetup.getDispatcherServlet().service(req, res);
		
		String result = res.getContentAsString();
		Assert.assertEquals("VALUE: SUCCESS", result);
	}
}
