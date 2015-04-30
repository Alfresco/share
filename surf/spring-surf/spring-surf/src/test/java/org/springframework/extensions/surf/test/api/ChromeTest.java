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

package org.springframework.extensions.surf.test.api;

import org.junit.Assert;
import org.springframework.extensions.surf.test.TestCaseSetup;
import org.springframework.extensions.surf.types.Chrome;
import org.testng.annotations.Test;

/**
 * Tests the Surf API directly using mock objects
 * 
 * @author muzquiano
 */
public class ChromeTest
{
	@Test
	public void testCRUD() throws Exception
    {
    	int c = TestCaseSetup.getObjectService().findChrome().size();
    	
    	// instantiate
    	Chrome chrome1 = TestCaseSetup.getObjectService().newChrome();
    	TestCaseSetup.getObjectService().saveObject(chrome1);
    	Chrome chrome2 = TestCaseSetup.getObjectService().newChrome("chrome2");
    	chrome2.setChromeType("chromeType2");
    	TestCaseSetup.getObjectService().saveObject(chrome2);
    	
    	// verify
    	Assert.assertEquals(c+2, TestCaseSetup.getObjectService().findChrome().size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findChrome("chromeType2").size());
    	
    	// changes + verify
    	chrome2.setChromeType("tempChromeType2");
    	TestCaseSetup.getObjectService().saveObject(chrome2);
    	Assert.assertEquals(0, TestCaseSetup.getObjectService().findChrome("chromeType2").size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findChrome("tempChromeType2").size());
    	
    	// deletion
    	TestCaseSetup.getObjectService().removeObject(chrome2);
    	Assert.assertEquals(c+1, TestCaseSetup.getObjectService().findChrome().size());
    	Assert.assertEquals(0, TestCaseSetup.getObjectService().findChrome("tempChromeType2").size());
    	
    	// deletion
    	TestCaseSetup.getObjectService().removeObject(chrome1);
    	Assert.assertEquals(c, TestCaseSetup.getObjectService().findChrome().size());
    } 
}
