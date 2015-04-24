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

import static org.springframework.extensions.surf.test.basic.Constants._BAR;
import static org.springframework.extensions.surf.test.basic.Constants._FOO;
import static org.springframework.extensions.surf.test.basic.Constants._HTTP_GET_METHOD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;

/**
 * <p>This class tests that the model is correctly configured and populated.</p>
 * @author David Draper
 */
public class ModelTest extends AbstractJettyTest
{
    /**
     * <p>This actually tests quite a few properties that can be added to the model. The URL should match a URI template
     * configured in surf.xml which defines the "pageid", "arg1" and "arg2" arguments. The template tokens should be added
     * to the model and when detected, a "pageid" token should be used as the id of the page to render. The page uses components
     * that provide properties which should be added to the model.</p>
     */
    @Test
    public void testTemplateArgsAndProperties()
    {
        checkResponseTextOrdering("pageid/pageargs_and_properties-page/arg1/foo/arg2/bar",
                                  _HTTP_GET_METHOD,
                                  _FOO,
                                  _BAR,
                                  "pageProp",
                                  "templateProp",
                                  "componentProp");
    }

    /**
     * <p>This tests that template arguments are passed into the model when rendering a page with an id that is a complete URI
     * template match (also proving that page id = uri template matching works!). The tokens "arg1" and "arg2" should be added
     * to the model.</p>
     */
    @Test
    public void testUriTemplateMatchArgs()
    {
        checkResponseTextOrdering("arg1/foo/arg2/bar",
                                  _HTTP_GET_METHOD,
                                  _FOO,
                                  _BAR);
    }
    
    /**
     * <p>The purpose of this test is to ensure that we can use the ${head} model property in the <{@code}head> HTML element
     * of a template. The contents of the ${head} property should include the output of the *.head.ftl file of all WebScript 
     * components included in the template. The idea is that these are generated on a first pass to set up output that
     * can ONLY be included in the <{@code}head> element (because by the time the component gets rendered the <{@code}head>
     * element will already have been closed.</p> 
     */
    @Test
    public void testTemplateHeaderRendering()
    {
        String response = requestResourceAssertingResponse("use_head_prop-page", Constants._HTTP_GET_METHOD);
        assertOccurenceAndOrder(response, 
                                "<head>", 
                                "_HEADER_DATA_", 
                                "</head>", 
                                "<body>", 
                                "_BODY_DATA_", 
                                "</body>");
    }
    
    /**
     * <p>The purpose of this test is to ensure that all the appropriate model properties have been set.
     * The test page renders a template that attempts to access the model as well as embedding both a 
     * page and template scoped component that accesses the properties.</p>
     * <p>The value of each property is rendered and if the property is not found the keyword "MISSING"
     * is output followed by the property id. If the test detects "MISSING" anywhere in the rendered
     * output then the test will fail.</p>
     */
    @Test
    public void testModelProperties()
    {
        String response = requestResourceAssertingResponse("model_test-page", Constants._HTTP_GET_METHOD);
        int index = response.indexOf("MISSING");
        if (index == -1)
        {
            // The string "MISSING" could not be found so the test has passed. No action required. 
        }
        else
        {            
            String remainder = response;
            List<String> missingProps = new ArrayList<String>();
            int breakIndex = -1;
            while (index != -1)
            {
                remainder = remainder.substring(index);                
                breakIndex = remainder.indexOf("<br>");
                if (breakIndex == -1)
                {
                    // The test page is broken. As we require the output of each property to be delimited
                    // by the <br> HTML element, because a) it makes it easier to read the output of the
                    // page when developing, and b) but more importantly we need to know where to start
                    // searching from for any more failures.
                    throw new Error("Invalid test page - all properties should be delimited with <br>");
                }
                else
                {
                    // Identify the missing property (ignore the word "MISSING: ") and add it to the list...
                    String missingProperty = remainder.substring(9, breakIndex);
                    missingProps.add(missingProperty);
                    
                    // Remove the property from the remainder of the response and search for any more...
                    remainder = remainder.substring(breakIndex);
                    index = remainder.indexOf("MISSING:");                    
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("The following properties could not found: ");
            for (String property: missingProps)
            {
                sb.append("\"");
                sb.append(property);
                sb.append("\", ");
            }
            throw new AssertionError(sb.toString()); 
        }
    }
    
    /**
     * <p>This tests the PresetsManager service and the ConstructPreset and PresetToken custom JSP tags. It does
     * this by loading a page that uses the custom tags that uses the PresetsManager to create new Spring Surf
     * ModelObject files. It then attempts to load the page created by those files.</p>
     */
    @Test
    public void testPresets()
    {
        // Delete any previously created preset files.
        File presets = new File("src/main/webapp/WEB-INF/presetConstructs");
        if (presets.exists())
        {
            for (File presetFile: presets.listFiles())
            {
                presetFile.delete();
            }            
        }
        
        // Call the presets test page that uses the ConstructPreset custom tag to create new preset files
        String response = requestResourceAssertingResponse("construct_preset_jsp-page", Constants._HTTP_GET_METHOD);
        assertOccurenceAndOrder(response, 
                                "StartTest", 
                                "BeforeDefaultRegionChrome", 
                                "BeforeDefaultComponentChrome", 
                                "AfterDefaultComponentChrome", 
                                "AfterDefaultRegionChrome");
        
        // Check that the files exist
        File page = new File("src/main/webapp/WEB-INF/presetConstructs/PresetPage.xml");
        File template = new File("src/main/webapp/WEB-INF/presetConstructs/PresetTemplate.xml");
        File component = new File("src/main/webapp/WEB-INF/presetConstructs/global.PresetRegion.xml");
        if (page.exists() && template.exists() && component.exists())
        {
            // All the files that should have been created have been, try to load the page...
            response = requestResourceAssertingResponse("PresetPage", Constants._HTTP_GET_METHOD);
            assertOccurenceAndOrder(response, 
                                    "BeforeDefaultRegionChrome", 
                                    "ComponentWebScriptChromeStart", 
                                    "WebScript1", 
                                    "ComponentWebScriptChromeEnd", 
                                    "AfterDefaultRegionChrome");
            
            // Delete the files...
            page.delete();
            template.delete();
            component.delete();
        }
        else
        {
            // The files that should have been created cannot be found. This is a test failure.
            throw new Error("The preset files have not all been created. Page (" + 
                            page.exists() + "), Template (" + template.exists() + 
                            "), Component (" + component.exists() + ")");
        }
    }
}
