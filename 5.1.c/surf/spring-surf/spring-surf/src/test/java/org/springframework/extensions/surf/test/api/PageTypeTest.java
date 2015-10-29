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
import org.testng.annotations.Test;
import org.springframework.extensions.surf.test.TestCaseSetup;
import org.springframework.extensions.surf.types.Page;

/**
 * Tests the Surf API directly using mock objects
 * 
 * @author muzquiano
 */
public class PageTypeTest
{
	@Test
    public void testCRUD() throws Exception
    {
    	int c = TestCaseSetup.getObjectService().findPages().size();
    	
    	Page page1 = TestCaseSetup.getObjectService().newPage();
    	TestCaseSetup.getObjectService().saveObject(page1);
    	Page page2 = TestCaseSetup.getObjectService().newPage("page2");
    	page2.setPageTypeId("pageType2");
    	TestCaseSetup.getObjectService().saveObject(page2);

    	// verify
    	Assert.assertEquals(c+2, TestCaseSetup.getObjectService().findPages().size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findPages(null, "pageType2").size());
    	
    	// change page types + verify
    	page2.setPageTypeId("tempPageType2");
    	TestCaseSetup.getObjectService().saveObject(page2);
    	Assert.assertEquals(0, TestCaseSetup.getObjectService().findPages(null, "pageType2").size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findPages(null, "tempPageType2").size());
    	
    	// delete
    	TestCaseSetup.getObjectService().removeObject(page2);
    	Assert.assertEquals(c+1, TestCaseSetup.getObjectService().findPages().size());
    }
}
