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
package org.springframework.extensions.surf.test.basic;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;

/**
 * 
 * @author David Draper
 */
public class FormTest extends AbstractJettyTest
{
    /**
     * <p>This test checks whether or not multipart form submission works.</p>
     * @throws IOException 
     */
    @Test
    public void testMultipartPost() throws IOException
    {
        HashMap<String, String> stringKeyToValueMap = new HashMap<String, String>();
        stringKeyToValueMap.put("name", "testName");
        stringKeyToValueMap.put("title", "testTitle");
        
        HashMap<String, String> fileKeyToLocationMap = new HashMap<String, String>();
        fileKeyToLocationMap.put("file", "src/main/webapp/images/surf32.jpg");
        
        String response = getFilePostResponse("service/testformdatamultipartprocessing2", 
                                              stringKeyToValueMap,
                                              fileKeyToLocationMap);
        
        Assert.assertEquals("SuccessisMultiPart = truearg.name = testNamename = testNametitle = testTitlefilename = surf32.jpgnumber of form fields = 3", response);
    }
}
