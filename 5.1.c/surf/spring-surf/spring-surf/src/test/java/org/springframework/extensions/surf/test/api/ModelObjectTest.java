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
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.test.TestCaseSetup;

/**
 * Tests the Surf API directly using mock objects
 * 
 * @author muzquiano
 */
public class ModelObjectTest
{
	@Test
    public void testProperties() throws Exception
    {
    	ModelObject o = TestCaseSetup.getObjectService().newObject("page");
    	TestCaseSetup.getObjectService().saveObject(o);
    	    	
    	o.setDescription("desc1");
    	Assert.assertEquals("desc1", o.getDescription());
    	
    	o.setDescriptionId("descid2");
    	Assert.assertEquals("descid2", o.getDescriptionId());
    	
    	o.setTitle("title1");
    	Assert.assertEquals("title1", o.getTitle());
    	
    	o.setTitleId("titleId1");
    	Assert.assertEquals("titleId1", o.getTitleId());
    	
    	o.setCustomProperty("customProperty1", "customPropertyValue1");
    	Assert.assertEquals("customPropertyValue1", o.getProperty("customProperty1"));
    	
    	o.setProperty("customProperty2", "customPropertyValue2");
    	Assert.assertEquals("customPropertyValue2", o.getProperty("customProperty2"));
    }
}
