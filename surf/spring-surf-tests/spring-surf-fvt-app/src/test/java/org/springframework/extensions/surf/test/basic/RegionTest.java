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
 * <p>Tests the following capabilities of direct region loading.</p>
 * <ul>
 * <li>Using "r" prefix</li>
 * <li>Using "region" prefix</li>
 * <li>Providing just a "regionId"</li>
 * <li>Providing "scope", "regionId" and "sourceId"</li>
 * </ul>
 * <p><b>Please note: Currently this test suite is virtually identical to the Component test suite
 * because I have not been able to determine the different between direct Component rendering and
 * direct Region rendering!</b></p>
 * @author David Draper
 */
public class RegionTest extends AbstractJettyTest
{
    /**
     * <p>Convenience method for checking the response of requesting a region directly. Each region should be wrapped in both the default
     * component and region chrome. At some point in the future it may be the case that regions or component are collapsed into each other,
     * it should not be necessary to change this method if that happens, but hopefully to just change the constants used for the the
     * prefixes and suffixes of the default chrome tested for. Each request is made twice to ensure that no caching
     * errors occur.</p>
     *
     * @param resource The resource to request.
     * @param target The target String to find between the default chrome prefix and suffix.
     */
    private void testStandardRegionRequestPattern(String resource, String target)
    {
        for (int i=0; i<2; i++)
        {
            String response = requestResourceAssertingResponse(resource, _HTTP_GET_METHOD);
            String lastMatch = findWrappedInDefaultComponentChrome(target, response);
            findWrappedInDefaultRegionChrome(lastMatch, response);
        }
    }

    /**
     * <p>Tests that a region can be loaded using the "r" prefix. All other tests use the "region" prefix.</p>
     */
    @Test
    public void testPrefixC()
    {
        testStandardRegionRequestPattern("r/regionId/global_scope_webscript_component-region",  _BASIC_WEBSCRIPT_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, WebScript region (no chrome) can be loaded directly by specifying just the "regionId" in the request.</p>
     */
    @Test
    public void testGlobalScopeWebScriptRegionSpecifyingJustRegion()
    {
        testStandardRegionRequestPattern("region/regionId/global_scope_webscript_component-region",  _BASIC_WEBSCRIPT_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, WebScript region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testGlobalScopeWebScriptRegion()
    {
        testStandardRegionRequestPattern("region/scope/global/regionId/global_scope_webscript_component-region/sourceId/global", _BASIC_WEBSCRIPT_OUTPUT_1);
    }

    /**
     * <p>Tests that a page scope, WebScript region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testPageScopeWebScriptRegion()
    {
        testStandardRegionRequestPattern("region/scope/page/regionId/page_scope_webscript_component-region/sourceId/page_scope_webscript_component-page", _BASIC_WEBSCRIPT_OUTPUT_2);
    }

    /**
     * <p>Tests that a template scope, WebScript region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testTemplateScopeWebScriptRegion()
    {
        testStandardRegionRequestPattern("region/scope/template/regionId/template_scope_webscript_component-region/sourceId/template_scope_webscript_component-template", _BASIC_WEBSCRIPT_OUTPUT_3);
    }

    /**
     * <p>Tests that a global scope, Freemarker region (no chrome) can be loaded directly by specifying just the "regionId" in the request.</p>
     */
    @Test
    public void testGlobalScopeFreemarkerRegionSpecifyingJustRegion()
    {
        testStandardRegionRequestPattern("region/regionId/global_scope_freemarker_component-region", _BASIC_FREEMARKER_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, Freemarker region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testGlobalScopeFreemarkerRegion()
    {
        testStandardRegionRequestPattern("region/scope/global/regionId/global_scope_freemarker_component-region/sourceId/global", _BASIC_FREEMARKER_OUTPUT_1);
    }

    /**
     * <p>Tests that a page scope, Freemarker region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testPageScopeFreemarkerRegion()
    {
        testStandardRegionRequestPattern("region/scope/page/regionId/page_scope_freemarker_component-region/sourceId/page_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_2);
    }

    /**
     * <p>Tests that a template scope, Freemarker region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testTemplateScopeFreemarkerRegion()
    {
        testStandardRegionRequestPattern("region/scope/template/regionId/template_scope_freemarker_component-region/sourceId/template_scope_freemarker_component-template", _BASIC_FREEMARKER_OUTPUT_3);
    }

    /**
     * <p>Tests that a global scope, JSP region (no chrome) can be loaded directly by specifying just the "regionId" in the request.</p>
     */
    @Test
    public void testGlobalScopeJSPRegionSpecifyingJustRegion()
    {
        testStandardRegionRequestPattern("region/regionId/global_scope_jsp_component-region", _BASIC_JSP_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, JSP region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testGlobalScopeJSPRegion()
    {
        testStandardRegionRequestPattern("region/scope/global/regionId/global_scope_jsp_component-region/sourceId/global", _BASIC_JSP_OUTPUT_1);
    }

    /**
     * <p>Tests that a page scope, JSP region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testPageScopeJSPRegion()
    {
        testStandardRegionRequestPattern("region/scope/page/regionId/page_scope_jsp_component-region/sourceId/page_scope_jsp_component-page", _BASIC_JSP_OUTPUT_2);
    }

    /**
     * <p>Tests that a template scope, JSP region (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testTemplateScopeJSPRegion()
    {
        testStandardRegionRequestPattern("region/scope/template/regionId/template_scope_jsp_component-region/sourceId/template_scope_jsp_component-template",  _BASIC_JSP_OUTPUT_3);
    }
}
