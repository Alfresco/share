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
public class PageAssociationTest
{
	@Test
    public void testCRUD() throws Exception
    {
    	int c = TestCaseSetup.getObjectService().findPageAssociations().size();
    	
    	Page childPage1 = TestCaseSetup.getObjectService().newPage("childPage1");
    	TestCaseSetup.getObjectService().saveObject(childPage1);
    	Page childPage2 = TestCaseSetup.getObjectService().newPage("childPage2");
    	TestCaseSetup.getObjectService().saveObject(childPage2);
    	Page childPage3 = TestCaseSetup.getObjectService().newPage("childPage3");
    	TestCaseSetup.getObjectService().saveObject(childPage3);
    	
    	TestCaseSetup.getObjectService().associatePage("page1", "childPage1");
    	TestCaseSetup.getObjectService().associatePage("page1", "childPage2", "dummyAssociationType");
    	TestCaseSetup.getObjectService().associatePage("page2", "childPage3");

    	Assert.assertEquals(c+3, TestCaseSetup.getObjectService().findPageAssociations().size());
    	Assert.assertEquals(2, TestCaseSetup.getObjectService().findPageAssociations("page1", null, null).size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findPageAssociations("page1", "childPage2", null).size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findPageAssociations("page1", null, "dummyAssociationType").size());
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findPageAssociations("page2", null, null).size());
    	
    	// remove association + verify
    	TestCaseSetup.getObjectService().unassociatePage("page2", "childPage3");
    	Assert.assertEquals(0, TestCaseSetup.getObjectService().findPageAssociations("page2", null, null).size());
    	
    	// remove + verify
    	TestCaseSetup.getObjectService().unassociatePage("page1", "childPage1");
    	Assert.assertEquals(1, TestCaseSetup.getObjectService().findPageAssociations("page1", null, null).size());
    }
}
