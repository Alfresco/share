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
 * <p>Tests the following capabilities of direct component loading.</p>
 * <ul>
 * <li>Using "c" prefix</li>
 * <li>Using "component" prefix</li>
 * <li>Providing just a "regionId"</li>
 * <li>Providing "scope", "regionId" and "sourceId"</li>
 * <li>Providing "mode", "scope", "regionId" and "sourceId"</li>
 * <li>Providing "focus", "scope", "regionId" and "sourceId"</li>
 * <li>Providing "mode", "focus", "scope", "regionId" and "sourceId"</li>
 * </ul>
 * <p>TODO: We also need to write tests that check error messages for missing data, etc. - once we've provided error messages!</p>
 * @author David Draper
 */
public class ComponentTest extends AbstractJettyTest
{
    /**
     * <p>Convenience method for checking the response of requesting a component directly. Each request is made twice to ensure that no caching
     * errors occur.</p>
     *
     * @param resource The resource to request.
     * @param target The target String to find between the default chrome prefix and suffix.
     */
    private void testStandardComponentRequestPattern(String resource, String target)
    {
        for (int i=0; i<2; i++)
        {
            String response = requestResourceAssertingResponse(resource, _HTTP_GET_METHOD);
            findWrappedInDefaultComponentChrome(target, response);
        }
    }

    /**
     * <p>Convenience method for checking the response of requesting a component to be rendered with both HEADER and BODY focus.
     * Each request is made twice to ensure that no cachingerrors occur.</p>
     *
     * @param resource The resource to request.
     * @param headerTarget The content that should appear in the header.
     * @param target The target String that should occur in the body between the default chrome prefix and suffix.
     */
    private void testAllFocusComponentRequestPattern(String resource, String headerTarget, String bodyTarget)
    {
        for (int i=0; i<2; i++)
        {
            String response = requestResourceAssertingResponse(resource, _HTTP_GET_METHOD);
            String bodyMatch = findWrappedInDefaultComponentChrome(bodyTarget, response);
            assertOccurenceAndOrder(response, headerTarget, bodyMatch);
        }
    }

    /**
     * <p>Tests that a component can be loaded using the "c" prefix. All other tests use the "component" prefix.</p>
     */
    @Test
    public void testPrefixC()
    {
        testStandardComponentRequestPattern("c/regionId/global_scope_webscript_component-region", _BASIC_WEBSCRIPT_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, WebScript component (no chrome) can be loaded directly by specifying just the "regionId" in the request.</p>
     */
    @Test
    public void testGlobalScopeWebScriptComponentSpecifyingJustRegion()
    {
        testStandardComponentRequestPattern("component/regionId/global_scope_webscript_component-region",_BASIC_WEBSCRIPT_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, WebScript component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testGlobalScopeWebScriptComponent()
    {
        testStandardComponentRequestPattern("component/scope/global/regionId/global_scope_webscript_component-region/sourceId/global", _BASIC_WEBSCRIPT_OUTPUT_1);
    }

    /**
     * <p>Tests that a page scope, WebScript component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testPageScopeWebScriptComponent()
    {
        testStandardComponentRequestPattern("component/scope/page/regionId/page_scope_webscript_component-region/sourceId/page_scope_webscript_component-page", _BASIC_WEBSCRIPT_OUTPUT_2);
    }

    /**
     * <p>Tests that a template scope, WebScript component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testTemplateScopeWebScriptComponent()
    {
        testStandardComponentRequestPattern("component/scope/template/regionId/template_scope_webscript_component-region/sourceId/template_scope_webscript_component-template",_BASIC_WEBSCRIPT_OUTPUT_3);
    }

    /**
     * <p>Tests that a global scope, Freemarker component (no chrome) can be loaded directly by specifying just the "regionId" in the request.</p>
     */
    @Test
    public void testGlobalScopeFreemarkerComponentSpecifyingJustRegion()
    {
        testStandardComponentRequestPattern("component/regionId/global_scope_freemarker_component-region", _BASIC_FREEMARKER_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, Freemarker component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testGlobalScopeFreemarkerComponent()
    {
        testStandardComponentRequestPattern("component/scope/global/regionId/global_scope_freemarker_component-region/sourceId/global", _BASIC_FREEMARKER_OUTPUT_1);
    }

    /**
     * <p>Tests that a page scope, Freemarker component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testPageScopeFreemarkerComponent()
    {
        testStandardComponentRequestPattern("component/scope/page/regionId/page_scope_freemarker_component-region/sourceId/page_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_2);
    }

    /**
     * <p>Tests that a template scope, Freemarker component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testTemplateScopeFreemarkerComponent()
    {
        testStandardComponentRequestPattern("component/scope/template/regionId/template_scope_freemarker_component-region/sourceId/template_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_3);
    }

    /**
     * <p>Tests that a global scope, JSP component (no chrome) can be loaded directly by specifying just the "regionId" in the request.</p>
     */
    @Test
    public void testGlobalScopeJSPComponentSpecifyingJustRegion()
    {
        testStandardComponentRequestPattern("component/regionId/global_scope_jsp_component-region", _BASIC_JSP_OUTPUT_1);
    }

    /**
     * <p>Tests that a global scope, JSP component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testGlobalScopeJSPComponent()
    {
        testStandardComponentRequestPattern("component/scope/global/regionId/global_scope_jsp_component-region/sourceId/global", _BASIC_JSP_OUTPUT_1);
    }

    /**
     * <p>Tests that a page scope, JSP component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testPageScopeJSPComponent()
    {
        testStandardComponentRequestPattern("component/scope/page/regionId/page_scope_jsp_component-region/sourceId/page_scope_jsp_component-page", _BASIC_JSP_OUTPUT_2);
    }

    /**
     * <p>Tests that a template scope, JSP component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request.</p>
     */
    @Test
    public void testTemplateScopeJSPComponent()
    {
        testStandardComponentRequestPattern("component/scope/template/regionId/template_scope_jsp_component-region/sourceId/template_scope_jsp_component-template", _BASIC_JSP_OUTPUT_3);
    }

    /**
     * <p>Tests that a global scope, Freemarker component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testGlobalScopeFreemarkerComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/global/regionId/global_scope_freemarker_component-region/sourceId/global", _BASIC_FREEMARKER_OUTPUT_4);
    }

    /**
     * <p>Tests that a page scope, Freemarker component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testPageScopeFreemarkerComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/page/regionId/page_scope_freemarker_component-region/sourceId/page_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_5);
    }

    /**
     * <p>Tests that a template scope, Freemarker component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testTemplateScopeFreemarkerComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/template/regionId/template_scope_freemarker_component-region/sourceId/template_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_6);
    }

    /**
     * <p>Tests that a global scope, JSP component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testGlobalScopeJSPComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/global/regionId/global_scope_jsp_component-region/sourceId/global", _BASIC_JSP_OUTPUT_4);
    }

    /**
     * <p>Tests that a page scope, JSP component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testPageScopeJSPComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/page/regionId/page_scope_jsp_component-region/sourceId/page_scope_jsp_component-page", _BASIC_JSP_OUTPUT_5);
    }

    /**
     * <p>Tests that a template scope, JSP component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testTemplateScopeJSPComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/template/regionId/template_scope_jsp_component-region/sourceId/template_scope_jsp_component-template",  _BASIC_JSP_OUTPUT_6);
    }

    /**
     * <p>Tests that a global scope, WebScript component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testGlobalScopeWebScriptComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/global/regionId/global_scope_webscript_component-region/sourceId/global", _BASIC_WEBSCRIPT_OUTPUT_4);
    }

    /**
     * <p>Tests that a page scope, WebScript component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testPageScopeWebScriptComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/page/regionId/page_scope_webscript_component-region/sourceId/page_scope_webscript_component-page", _BASIC_WEBSCRIPT_OUTPUT_5);
    }

    /**
     * <p>Tests that a template scope, WebScript component (no chrome) can be loaded directly by specifying the "scope", "regionId" and "sourceId" in the
     * request and additionally providing a "mode" of "edit" (but not providing any particular focus)</p>
     */
    @Test
    public void testTemplateScopeWebScriptComponentEditModeNoFocus()
    {
        testStandardComponentRequestPattern("component/mode/edit/scope/template/regionId/template_scope_webscript_component-region/sourceId/template_scope_webscript_component-template",  _BASIC_WEBSCRIPT_OUTPUT_6);
    }

    /**
     * <p>Tests that a global scope, WebScript component that overrides the component type specified URL can be loaded.</p>
     */
    @Test
    public void testGlobalScopeWebScriptComponentWithComponentTypeURLOverride()
    {
        testStandardComponentRequestPattern("component/regionId/global_scope_webscript_component_ct_override-region",  _BASIC_WEBSCRIPT_OUTPUT_2);
    }

    /**
     * <p>Tests that a global scope, Freemarker component that overrides the component type specified URL can be loaded.</p>
     */
    @Test
    public void testGlobalScopeFreemarkerComponentWithComponentTypeURLOverride()
    {
        testStandardComponentRequestPattern("component/regionId/global_scope_freemarker_component_ct_override-region", _BASIC_FREEMARKER_OUTPUT_2);
    }

    /**
     * <p>Tests that a global scope, JSP component that overrides the component type specified URL can be loaded.</p>
     */
    @Test
    public void testGlobalScopeJSPComponentWithComponentTypeURLOverride()
    {
        testStandardComponentRequestPattern("component/regionId/global_scope_jsp_component_ct_override-region", _BASIC_JSP_OUTPUT_2);
    }

    /**
     * <p>Tests that its possible to request the header focus for a FreeMarker component.</p>
     */
    @Test
    public void testPageScopeFreemarkerHeaderFocusNoMode()
    {
        checkResponse("component/focus/header/scope/page/regionId/freemarker_multifocus-region/sourceId/freemarker_multifocus-page", _HTTP_GET_METHOD, _FREEMARKER_HEAD);
    }

    /**
     * <p>Tests that its possible to request the body focus for a FreeMarker component.</p>
     */
    @Test
    public void testPageScopeFreemarkerBodyFocusNoMode()
    {
        testStandardComponentRequestPattern("component/focus/body/scope/page/regionId/freemarker_multifocus-region/sourceId/freemarker_multifocus-page",_FREEMARKER_BODY);
    }

    /**
     * <p>Tests that its possible to request the "all" (to render both "header" and "body") focus for a FreeMarker component.</p>
     */
    @Test
    public void testPageScopeFreemarkerAllFocusNoMode()
    {
        testAllFocusComponentRequestPattern("component/focus/all/scope/page/regionId/freemarker_multifocus-region/sourceId/freemarker_multifocus-page",_FREEMARKER_HEAD,_FREEMARKER_BODY);
    }

    /**
     * <p>Tests that its possible to request the header focus for a FreeMarker component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeFreemarkerHeaderFocusViewMode()
    {
        checkResponse("component/mode/view/focus/header/scope/page/regionId/freemarker_multifocus-region/sourceId/freemarker_multifocus-page", _HTTP_GET_METHOD, _FREEMARKER_HEAD);
    }

    /**
     * <p>Tests that its possible to request the body focus for a FreeMarker component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeFreemarkerBodyFocusViewMode()
    {
        testStandardComponentRequestPattern("component/mode/view/focus/body/scope/page/regionId/freemarker_multifocus-region/sourceId/freemarker_multifocus-page",_FREEMARKER_BODY);
    }

    /**
     * <p>Tests that its possible to request the "all" (to render both "header" and "body") focus for a FreeMarker component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeFreemarkerAllFocusViewMode()
    {
        testAllFocusComponentRequestPattern("component/mode/view/focus/all/scope/page/regionId/freemarker_multifocus-region/sourceId/freemarker_multifocus-page",_FREEMARKER_HEAD,_FREEMARKER_BODY);
    }

    /**
     * <p>Tests that its possible to request the header focus for a JSP component.</p>
     */
    @Test
    public void testPageScopeJSPHeaderFocusNoMode()
    {
        checkResponse("component/focus/header/scope/page/regionId/jsp_multifocus-region/sourceId/jsp_multifocus-page", _HTTP_GET_METHOD, _JSP_HEAD);
    }

    /**
     * <p>Tests that its possible to request the body focus for a JSP component.</p>
     */
    @Test
    public void testPageScopeJSPBodyFocusNoMode()
    {
        testStandardComponentRequestPattern("component/focus/body/scope/page/regionId/jsp_multifocus-region/sourceId/jsp_multifocus-page",_JSP_BODY);
    }

    /**
     * <p>Tests that its possible to request the "all" (to render both "header" and "body") focus for a JSP component.</p>
     */
    @Test
    public void testPageScopeJSPAllFocusNoMode()
    {
        testAllFocusComponentRequestPattern("component/focus/all/scope/page/regionId/jsp_multifocus-region/sourceId/jsp_multifocus-page",_JSP_HEAD,_JSP_BODY);
    }

    /**
     * <p>Tests that its possible to request the header focus for a JSP component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeJSPHeaderFocusViewMode()
    {
        checkResponse("component/mode/view/focus/header/scope/page/regionId/jsp_multifocus-region/sourceId/jsp_multifocus-page", _HTTP_GET_METHOD, _JSP_HEAD);
    }

    /**
     * <p>Tests that its possible to request the body focus for a JSP component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeJSPBodyFocusViewMode()
    {
        testStandardComponentRequestPattern("component/mode/view/focus/body/scope/page/regionId/jsp_multifocus-region/sourceId/jsp_multifocus-page",_JSP_BODY);
    }

    /**
     * <p>Tests that its possible to request the "all" (to render both "header" and "body") focus for a JSP component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeJSPAllFocusViewMode()
    {
        testAllFocusComponentRequestPattern("component/mode/view/focus/all/scope/page/regionId/jsp_multifocus-region/sourceId/jsp_multifocus-page",_JSP_HEAD,_JSP_BODY);
    }

    /**
     * <p>Tests that its possible to request the header focus for a WebScript component.</p>
     */
    @Test
    public void testPageScopeWebScriptHeaderFocusNoMode()
    {
        checkResponse("component/focus/header/scope/page/regionId/webscript_multifocus-region/sourceId/webscript_multifocus-page", _HTTP_GET_METHOD, _WEBSCRIPT_HEAD);
    }

    /**
     * <p>Tests that its possible to request the body focus for a WebScript component.</p>
     */
    @Test
    public void testPageScopeWebScriptBodyFocusNoMode()
    {
        testStandardComponentRequestPattern("component/focus/body/scope/page/regionId/webscript_multifocus-region/sourceId/webscript_multifocus-page",_WEBSCRIPT_BODY);
    }

    /**
     * <p>Tests that its possible to request the "all" (to render both "header" and "body") focus for a WebScript component.</p>
     */
    @Test
    public void testPageScopeWebScriptAllFocusNoMode()
    {
        testAllFocusComponentRequestPattern("component/focus/all/scope/page/regionId/webscript_multifocus-region/sourceId/webscript_multifocus-page",_WEBSCRIPT_HEAD,_WEBSCRIPT_BODY);
    }

    /**
     * <p>Tests that its possible to request the header focus for a WebScript component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeWebScriptHeaderFocusViewMode()
    {
        checkResponse("component/mode/view/focus/header/scope/page/regionId/webscript_multifocus-region/sourceId/webscript_multifocus-page", _HTTP_GET_METHOD, _WEBSCRIPT_HEAD);
    }

    /**
     * <p>Tests that its possible to request the body focus for a WebScript component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeWebScriptBodyFocusViewMode()
    {
        testStandardComponentRequestPattern("component/mode/view/focus/body/scope/page/regionId/webscript_multifocus-region/sourceId/webscript_multifocus-page",_WEBSCRIPT_BODY);
    }

    /**
     * <p>Tests that its possible to request the "all" (to render both "header" and "body") focus for a WebScript component when also specifying a mode.</p>
     */
    @Test
    public void testPageScopeWebScriptAllFocusViewMode()
    {
        testAllFocusComponentRequestPattern("component/mode/view/focus/all/scope/page/regionId/webscript_multifocus-region/sourceId/webscript_multifocus-page",_WEBSCRIPT_HEAD,_WEBSCRIPT_BODY);
    }
}
