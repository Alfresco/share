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

import static org.springframework.extensions.surf.test.basic.Constants.*;

import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;



/**
 * <p>This suite of tests ensures that Spring Surf applications that were written according to earlier Milestone releases
 * are still compatible with the improved and more consistent style of configuration. The following backwards compatibility
 * tests are carried out:</p>
 * <ul>
 * <li>Load JSP component using "jsp-path" element in "processor" element</li>
 * <li>Load WebScript component with no "uri" specified in the component type</li>
 * </ul>
 * @author David Draper
 *
 */
public class BackwardsCompatibilityTest extends AbstractJettyTest
{
    /**
     * Test that it is still possible to render JSP components where the component type uses the "jsp-path" element rather than
     * the more consistent "uri" element.
     */
    @Test
    public void testJSPComponentWithJSPPathElement()
    {
        String response = requestResourceAssertingResponse("component/regionId/global_scope_legacy_jsp_component-region", _HTTP_GET_METHOD);
        findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_1, response);
    }

    /**
     * Test that it is still possible to render WebScript components where the "url" MUST be in the component and there is no information
     * contained in the component type at all.
     */
    @Test
    public void testWebScriptComponentNoComponentTypeURI()
    {
        String response = requestResourceAssertingResponse("component/regionId/global_scope_legacy_webscript_component-region", _HTTP_GET_METHOD);
        findWrappedInDefaultComponentChrome(_BASIC_WEBSCRIPT_OUTPUT_1, response);
    }

    @Test
    public void testJSPComponentWithJSPPathElement_CACHE_TEST()
    {
        this.testJSPComponentWithJSPPathElement();
    }

    @Test
    public void testWebScriptComponentNoComponentTypeURI_CACHE_TEST()
    {
        this.testWebScriptComponentNoComponentTypeURI();
    }
}
