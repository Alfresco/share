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

import static org.springframework.extensions.surf.test.basic.Constants._AFTER_GLOBAL_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._AFTER_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._AFTER_PAGE_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._AFTER_PAGE_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._AFTER_TEMPLATE_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._AFTER_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_FREEMARKER_OUTPUT_1;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_FREEMARKER_OUTPUT_2;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_FREEMARKER_OUTPUT_3;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_JSP_OUTPUT_1;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_JSP_OUTPUT_2;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_JSP_OUTPUT_3;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_WEBSCRIPT_OUTPUT_1;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_WEBSCRIPT_OUTPUT_2;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_WEBSCRIPT_OUTPUT_3;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_GLOBAL_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_PAGE_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_PAGE_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_TEMPLATE_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._HTTP_GET_METHOD;

import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;




public class BasicTest extends AbstractJettyTest
{
    /**
     * <p>A WebScript is the finest grained component that can be used by Spring Surf. In later
     * tests we will attempt to embed WebScripts as regions in pages, but to ensure that there are
     * no issues with the WebScript capability we should test it in isolation. This test attempts
     * to load a test WebScript directly. This is possible by specifying the registered URL directly</p>
     */
    @Test
    public void testBasicWebScript()
    {
    	checkResponse("/tests/test1", _HTTP_GET_METHOD, "Test_WebScript_1");
    }

    /**
     * <p>This is the most basic test of the Spring Surf capability. Attempt to load a page
     * referencing a template that contains nothing but the word "success". This essentially
     * proves that the PageViewResolver is able to locate a page.</p>
     *
     * <p>It should be possible to load pages either via incorporating the page id into the
     * request URL</p>
     */
    @Test
    public void testBasicPageLoad()
    {
    	checkResponse("basic_template-page", _HTTP_GET_METHOD, "Success");
    }

    /**
     * <p>This checks that the site configured root page will be loaded when no resource
     * information is provided.</p>
     */
    @Test
    public void testRootPageLoad()
    {
        checkResponse("", _HTTP_GET_METHOD, "RootPage");
    }

    /**
     * <p>This test checks that a WebScript that lives outside of a <code>WEB-INF/webscripts</code> can
     * still be rendered. This test has been added to simply prevent regressions as there is a temptation
     * to modify the default <code>webframework.webscripts.store.webinf</code> Spring Bean to use a path
     * of <code>/WEB-INF/webscripts</code> (as this makes the binding of Spring Beans to WebScripts much
     * more obvious). However, doing this will result in regressions (particularly to Alfresco). See
     * <a href=http://forum.springsource.org/showthread.php?t=93411">this</a> forum thread for the details.
     * </p> 
     */
    @Test
    public void testWebScriptOutside_webscripts_folder()
    {
        checkResponse("/outer/webscript", _HTTP_GET_METHOD, "WebScript Outside webscripts folder");
    }
    
    /**
     * <p>The template used by a page should be able to embed regions rendered by a WebScript. These
     * tests ensure that a region can be correctly embedded in a page.</p>
     */
    @Test
    public void testBasicRegionLoad()
    {
        logTestMessage("Loading page with embedded region");
        checkResponseTextOrdering("basic_webscript_region-page",
                                   _HTTP_GET_METHOD,
                                   "Before Region",
                                   "Test_WebScript_1",
                                   "After Region");
    }

    // Constants used for page type tests...

    /**
     * <p>test_page_type_1 is the id of a page type that is mapped in theme configurations</p>
     * <ul><li>theme_1-theme (the default theme)</li>
     * <li>theme_2-theme</li>
     * <li>theme_3-theme</li></ul>
     * <p>Each of these themes maps the page type to a different page so the output of the page type
     * will differ depending upon the theme requested.</p>
     */
    private static final String _BASIC_PAGETYPE = "basic-pageType";

    /**
     * The prefix "pt/" can be used in URLs to specify that a page type is being requested. This is the
     * prefix that is documented in "Professional Alfresco" and has been added post M3 to ensure that
     * the book is correct.
     */
    private static final String _PT_PREFIX = "pt/";

    /**
     * The prefix "type/" can be used in URLs to specify that a page type is being requested. This prefix
     * was coded in M1, M2 and M3 so has been preserved for backwards compatibility but should ideally not
     * be documented.
     */
    private static final String _TYPE_PREFIX = "type/";

    /**
     * The request parameter "theme" can be used to in URLs to specify the theme to be used when loading a
     * page type. This is not documented in "Professional Alfresco" but has been captured in blogs on the
     * internet so has been preserved for backwards compatibility. Support for this request parameter was
     * introduced post M3.
     */
    private static final String _THEME_REQ_PARM = "theme";

    /**
     * <p>A PageType can be defined that when requested will be mapped to the Page for the current or
     * requested theme. In order for this to work, the current or requested theme must contain a mapping
     * from the PageTypeId to the Page. The following is checked:</p>
     * <ul>
     * <li>Page types can be loaded based on the page mapping in the default theme</li>
     * <li>Page types can be loaded using the "type/" prefix</li>
     * <li>Page types can be loaded using the "pt/" prefix</li>
     * <li>Page types can be loaded using the "pt" request parameter</li>
     * <li>The page loaded can be altered using the "theme" request parameter</li>
     * </ul>
     */
    @Test
    public void testPageTypeLoad()
    {
        // Test that loads page type from default theme...
        // The default theme is defined in "default.site.configuration.xml" and is set to "test_theme_1"
        // "test_theme_1" uses the element "page-instance-id" to map the page type to page...
    	checkResponse(_PT_PREFIX + _BASIC_PAGETYPE, _HTTP_GET_METHOD, "Theme1");

        // Test that loads page type using "type/" prefix
        // Test that loads page type from requested theme (using "page-id")
        checkResponse(_TYPE_PREFIX + _BASIC_PAGETYPE + "?" + _THEME_REQ_PARM + "=theme_2-theme", _HTTP_GET_METHOD, "Theme2");
    }

    /**
     * <p>Checks that it is possible to switch themes through the use of the "theme" request parameter. This was not originally possible
     * because the key used to cache the view was purely based on the view name, but updates to the code now include the theme as well.
     * If this test breaks it will be because someone has undone that fix.</code>
     */
    @Test
    public void switchThemeTest()
    {
        checkResponse(_TYPE_PREFIX + _BASIC_PAGETYPE + "?" + _THEME_REQ_PARM + "=theme_1-theme", _HTTP_GET_METHOD, "Theme1");
        checkResponse(_TYPE_PREFIX + _BASIC_PAGETYPE + "?" + _THEME_REQ_PARM + "=theme_2-theme", _HTTP_GET_METHOD, "Theme2");
        checkResponse(_TYPE_PREFIX + _BASIC_PAGETYPE + "?" + _THEME_REQ_PARM + "=theme_3-theme", _HTTP_GET_METHOD, "Theme3");
    }

    /**
     * <p>This is a convenience method that checks for the contents of a component rendered by requesting a page containing a template
     * that contains rendered a single component using the default chrome. Each request is carried out twice to ensure that there
     * are no caching issues.</p>
     *
     * @param resource The resource to request using the HTTP GET method.
     * @param target The target to find that will be rendered by the component.
     * @param templatePrefix The prefix inserted by the template.
     * @param templateSuffix The suffix inserted by the template.
     */
    private void testStandardPageTemplateComponentPattern(String resource, String target, String templatePrefix, String templateSuffix)
    {
        for (int i=0; i<2; i++)
        {
            String response = requestResourceAssertingResponse(resource, _HTTP_GET_METHOD);
            String currentMatch = findWrappedInDefaultComponentChrome(target, response);
            currentMatch = findWrappedInDefaultRegionChrome(currentMatch, response);
            currentMatch = findBetweenPrefixAndSuffix(currentMatch, response, templatePrefix, templateSuffix);
        }
    }

    /**
     * Tests that a page embedding a global scope, JSP rendered component can be successfully loaded.
     */
    @Test
    public void testGlobalScopeJSPComponentPage()
    {
        testStandardPageTemplateComponentPattern("global_scope_jsp_component-page", _BASIC_JSP_OUTPUT_1, "BeforeGlobalScopeJSPComponent", "AfterGlobalScopeJSPComponent");
    }

    /**
     * Tests that a page embedding a page scope, JSP rendered component can be successfully loaded.
     */
    @Test
    public void testPageScopeJSPComponentPage()
    {
        testStandardPageTemplateComponentPattern("page_scope_jsp_component-page", _BASIC_JSP_OUTPUT_2, "BeforePageScopeJSPComponent", "AfterPageScopeJSPComponent");
    }

    /**
     * Tests that a page embedding a template scope, WebScript rendered component can be successfully loaded.
     */
    @Test
    public void testTemplateScopeJSPComponentPage()
    {
        testStandardPageTemplateComponentPattern("template_scope_jsp_component-page", _BASIC_JSP_OUTPUT_3, "BeforeTemplateScopeJSPComponent", "AfterTemplateScopeJSPComponent");
    }

    /**
     * Tests that a page embedding a global scope, WebScript rendered component can be successfully loaded.
     */
    @Test
    public void testGlobalScopeWebScriptComponentPage()
    {
        testStandardPageTemplateComponentPattern("global_scope_webscript_component-page", _BASIC_WEBSCRIPT_OUTPUT_1, _BEFORE_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT, _AFTER_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * Tests that a page embedding a page scope, WebScript rendered component can be successfully loaded.
     */
    @Test
    public void testPageScopeWebScriptComponentPage()
    {
        testStandardPageTemplateComponentPattern("page_scope_webscript_component-page", _BASIC_WEBSCRIPT_OUTPUT_2, _BEFORE_PAGE_SCOPE_WEBSCRIPT_COMPONENT, _AFTER_PAGE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * Tests that a page embedding a template scope, WebScript rendered component can be successfully loaded.
     */
    @Test
    public void testTemplateScopeWebScriptComponentPage()
    {
        testStandardPageTemplateComponentPattern("template_scope_webscript_component-page", _BASIC_WEBSCRIPT_OUTPUT_3, _BEFORE_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT, _AFTER_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * Tests that a page embedding a global scope, Freemarker rendered component can be successfully loaded.
     */
    @Test
    public void testGlobalScopeFreemarkerComponentPage()
    {
        testStandardPageTemplateComponentPattern("global_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_1, _BEFORE_GLOBAL_SCOPE_FREEMARKER_COMPONENT, _AFTER_GLOBAL_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * Tests that a page embedding a page scope, Freemarker rendered component can be successfully loaded.
     */
    @Test
    public void testPageScopeFreemarkerComponentPage()
    {
        testStandardPageTemplateComponentPattern("page_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_2, _BEFORE_PAGE_SCOPE_FREEMARKER_COMPONENT, _AFTER_PAGE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * Tests that a page embedding a template scope, Freemarker rendered component can be successfully loaded.
     */
    @Test
    public void testTemplateScopeFreemarkerComponentPage()
    {
        testStandardPageTemplateComponentPattern("template_scope_freemarker_component-page", _BASIC_FREEMARKER_OUTPUT_3, _BEFORE_TEMPLATE_SCOPE_FREEMARKER_COMPONENT, _AFTER_TEMPLATE_SCOPE_FREEMARKER_COMPONENT);
    }
}
