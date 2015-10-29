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

package org.springframework.extensions.surf.test.resource;

import java.util.Map;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.resource.ModelObjectResourceProvider;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.test.TestCaseSetup;
import org.testng.Assert;

/**
 * Tests the Surf API directly using mock objects
 * 
 * @author muzquiano
 */
public class ResourceProviderTest
{
    public void testResources() throws Exception
    {
    	ModelObject obj = TestCaseSetup.getObjectService().newObject("page", "page1");
    	TestCaseSetup.getObjectService().saveObject(obj);
    	
    	ModelObjectResourceProvider provider = new ModelObjectResourceProvider(obj);
    	
    	Resource resource1 = provider.addResource("resource1", "http://cmis.alfresco.com/images/logo/AlfrescoLogo200.png");
    	Assert.assertEquals(1, provider.getResources().length);
    	
    	Resource resource2 = provider.addResource("resource2", "http", "cmis.alfresco.com", "/images/logo/AlfrescoLogo200.png");
    	Assert.assertEquals(2, provider.getResources().length);
    	
    	Map<String, Resource> map = provider.getResourcesMap();
    	Assert.assertEquals(resource1, map.get("resource1"));
    	Assert.assertEquals(resource2, map.get("resource2"));
    	
    	Assert.assertEquals(resource1, provider.getResource("resource1"));
    	Assert.assertEquals(resource2, provider.getResource("resource2"));
    	
    	Resource resource3 = TestCaseSetup.getServiceRegistry().getResourceService().getResource("http://www.alfresco.com/about/people/images/dave-caruana_small.jpg");
    	Assert.assertNotNull(resource3);
    	
    	provider.updateResource("resource2", resource3);
    	
    	Assert.assertEquals(resource3, provider.getResource("resource3"));
    }
}
