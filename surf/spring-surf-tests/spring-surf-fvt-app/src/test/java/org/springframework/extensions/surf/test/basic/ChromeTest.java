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
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_WEBSCRIPT_OUTPUT_1;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_WEBSCRIPT_OUTPUT_2;
import static org.springframework.extensions.surf.test.basic.Constants._BASIC_WEBSCRIPT_OUTPUT_3;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_GLOBAL_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_PAGE_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_PAGE_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_TEMPLATE_SCOPE_FREEMARKER_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._BEFORE_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT;
import static org.springframework.extensions.surf.test.basic.Constants._COMPONENT_FREEMARKER_CHROME_END;
import static org.springframework.extensions.surf.test.basic.Constants._COMPONENT_FREEMARKER_CHROME_START;
import static org.springframework.extensions.surf.test.basic.Constants._COMPONENT_JSP_CHROME_END;
import static org.springframework.extensions.surf.test.basic.Constants._COMPONENT_JSP_CHROME_START;
import static org.springframework.extensions.surf.test.basic.Constants._COMPONENT_WEBSCRIPT_CHROME_END;
import static org.springframework.extensions.surf.test.basic.Constants._COMPONENT_WEBSCRIPT_CHROME_START;
import static org.springframework.extensions.surf.test.basic.Constants._HTTP_GET_METHOD;

import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;



/**
 * <p>Tests all Chrome related function. The following aspects of Chrome are tested:</p>
 * <li>Freemarker based chrome around region</li>
 * <li>TODO: JSP based chrome around region</li>
 * <li>TODO: Custom based chrome around region - need to actually understand what this is!</li>
 * <li>Freemarker rendered chrome around Freemarker component (all scopes)</li>
 * <li>WebScript rendered chrome around Freemarker component (all scopes)</li>
 * <li>Freemarker rendered chrome around WebScript component (all scopes)</li>
 * <li>WebScript rendered chrome around WebScript component (all scopes)</li>
 * <li>JSP based chrome around component</li>
 * <li>TODO: Custom based chrome around component</li>
 *
 * @author David Draper
 *
 */
public class ChromeTest extends AbstractJettyTest
{
    /**
     * Constant for "BeforeFTLChromeRegion". This is the text at the beginning of the the test_template_for_chrome_freemarker
     * template and is expected to appear first in the response when requesting the page featuring chrome. It has been extracted
     * to a constant because it is used more than once.
     */
    private static final String _BEFORE_FTL_CHROME_REGION = "BeforeFTLChromeRegion";

    /**
     * Constant for "BeforeFTLChrome. This is the text at the beginning of the template used for the freemarker rendered chrome.
     */
    private static final String _BEFORE_FTL_CHROME = "FTLChromeStart";

    /**
     * Constant for "Test_WebScript_1". This is the response returned  by Test_WebScript_1.
     */
    private static final String _TEST_WEBSCRIPT_RESPONSE = "Test_WebScript_1";

    /**
     * Constant for "AfterFTLCrome". This is the text at the end of the template used for the freemarker rendered chrome.
     */
    private static final String _AFTER_FTL_CHROME = "FTLCromeEnd";

    /**
     * Constant for "AfterFTLChromeRegion". This is the text at the end of the template used for the page featuring
     * freemarker rendered chrome.
     */
    private static final String _AFTER_FTL_CHROME_REGION = "AfterFTLChromeRegion";

    /**
     *
     */
    @Test
    public void testFreeMarkerChrome()
    {
        checkResponseTextOrdering("basic_chrome_freemarker-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_FTL_CHROME_REGION,
                                   _BEFORE_FTL_CHROME,
                                   _TEST_WEBSCRIPT_RESPONSE,
                                   _AFTER_FTL_CHROME,
                                   _AFTER_FTL_CHROME_REGION);
    }



    /**
     * This tests loading a page referencing a global scope, Freemarker rendered component
     * wrapped in Freemarker rendered chrome.
     */
    @Test
    public void testGlobalScopeFreemarkerComponentWithFreemarkerChrome()
    {
        checkResponseTextOrdering("global_scope_freemarker_component_with_freemarker_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_GLOBAL_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_FREEMARKER_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_1,
                                   _COMPONENT_FREEMARKER_CHROME_END,
                                   _AFTER_GLOBAL_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a page scope, Freemarker rendered component
     * wrapped in Freemarker rendered chrome.
     */
    @Test
    public void testPageScopeFreemarkerComponentWithFreemarkerChrome()
    {
        checkResponseTextOrdering("page_scope_freemarker_component_with_freemarker_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_PAGE_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_FREEMARKER_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_2,
                                   _COMPONENT_FREEMARKER_CHROME_END,
                                   _AFTER_PAGE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a template scope, Freemarker rendered component
     * wrapped in Freemarker rendered chrome.
     */
    @Test
    public void testTemplateScopeFreemarkerComponentWithFreemarkerChrome()
    {
        checkResponseTextOrdering("template_scope_freemarker_component_with_freemarker_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_TEMPLATE_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_FREEMARKER_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_3,
                                   _COMPONENT_FREEMARKER_CHROME_END,
                                   _AFTER_TEMPLATE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a global scope, Freemarker rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testGlobalScopeFreemarkerComponentWithWebScriptChrome()
    {
        checkResponseTextOrdering("global_scope_freemarker_component_with_webscript_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_GLOBAL_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_WEBSCRIPT_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_1,
                                   _COMPONENT_WEBSCRIPT_CHROME_END,
                                   _AFTER_GLOBAL_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a page scope, Freemarker rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testPageScopeFreemarkerComponentWithWebScriptChrome()
    {
        checkResponseTextOrdering("page_scope_freemarker_component_with_webscript_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_PAGE_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_WEBSCRIPT_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_2,
                                   _COMPONENT_WEBSCRIPT_CHROME_END,
                                   _AFTER_PAGE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a template scope, Freemarker rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testTemplateScopeFreemarkerComponentWithWebScriptChrome()
    {
        checkResponseTextOrdering("template_scope_freemarker_component_with_webscript_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_TEMPLATE_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_WEBSCRIPT_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_3,
                                   _COMPONENT_WEBSCRIPT_CHROME_END,
                                   _AFTER_TEMPLATE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a global scope, Freemarker rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testGlobalScopeWebScriptComponentWithFreemarkerChrome()
    {
        checkResponseTextOrdering("global_scope_webscript_component_with_freemarker_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_FREEMARKER_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_1,
                                   _COMPONENT_FREEMARKER_CHROME_END,
                                   _AFTER_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a page scope, Freemarker rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testPageScopeWebScriptComponentWithFreemarkerChrome()
    {
        checkResponseTextOrdering("page_scope_webscript_component_with_freemarker_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_PAGE_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_FREEMARKER_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_2,
                                   _COMPONENT_FREEMARKER_CHROME_END,
                                   _AFTER_PAGE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a template scope, Freemarker rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testTemplateScopeWebScriptComponentWithFreemarkerChrome()
    {
        checkResponseTextOrdering("template_scope_webscript_component_with_freemarker_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_FREEMARKER_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_3,
                                   _COMPONENT_FREEMARKER_CHROME_END,
                                   _AFTER_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a global scope, WebScript rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testGlobalScopeWebScriptComponentWithWebScriptChrome()
    {
        checkResponseTextOrdering("global_scope_webscript_component_with_webscript_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_WEBSCRIPT_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_1,
                                   _COMPONENT_WEBSCRIPT_CHROME_END,
                                   _AFTER_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a page scope, WebScript rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testPageScopeWebScriptComponentWithWebScriptChrome()
    {
        checkResponseTextOrdering("page_scope_webscript_component_with_webscript_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_PAGE_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_WEBSCRIPT_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_2,
                                   _COMPONENT_WEBSCRIPT_CHROME_END,
                                   _AFTER_PAGE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a template scope, WebScript rendered component
     * wrapped in WebScript rendered chrome.
     */
    @Test
    public void testTemplateScopeWebScriptComponentWithWebScriptChrome()
    {
        checkResponseTextOrdering("template_scope_webscript_component_with_webscript_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_WEBSCRIPT_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_3,
                                   _COMPONENT_WEBSCRIPT_CHROME_END,
                                   _AFTER_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a global scope, Freemarker rendered component
     * wrapped in JSP rendered chrome.
     */
    @Test
    public void testGlobalScopeFreemarkerComponentWithJSPChrome()
    {
        checkResponseTextOrdering("global_scope_freemarker_component_with_jsp_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_GLOBAL_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_JSP_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_1,
                                   _COMPONENT_JSP_CHROME_END,
                                   _AFTER_GLOBAL_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a global scope, WebScript rendered component
     * wrapped in JSP rendered chrome.
     */
    @Test
    public void testGlobalScopeWebScriptComponentWithJSPChrome()
    {
        checkResponseTextOrdering("global_scope_webscript_component_with_jsp_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_JSP_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_1,
                                   _COMPONENT_JSP_CHROME_END,
                                   _AFTER_GLOBAL_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a page scope, Freemarker rendered component
     * wrapped in JSP rendered chrome.
     */
    @Test
    public void testPageScopeFreemarkerComponentWithJSPChrome()
    {
        checkResponseTextOrdering("page_scope_freemarker_component_with_jsp_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_PAGE_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_JSP_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_2,
                                   _COMPONENT_JSP_CHROME_END,
                                   _AFTER_PAGE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a page scope, WebScript rendered component
     * wrapped in JSP rendered chrome.
     */
    @Test
    public void testPageScopeWebScriptComponentWithJSPChrome()
    {
        checkResponseTextOrdering("page_scope_webscript_component_with_jsp_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_PAGE_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_JSP_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_2,
                                   _COMPONENT_JSP_CHROME_END,
                                   _AFTER_PAGE_SCOPE_WEBSCRIPT_COMPONENT);
    }

    /**
     * This tests loading a page referencing a template scope, Freemarker rendered component
     * wrapped in JSP rendered chrome.
     */
    @Test
    public void testTemplateScopeFreemarkerComponentWithJSPChrome()
    {
        checkResponseTextOrdering("template_scope_freemarker_component_with_jsp_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_TEMPLATE_SCOPE_FREEMARKER_COMPONENT,
                                   _COMPONENT_JSP_CHROME_START,
                                   _BASIC_FREEMARKER_OUTPUT_3,
                                   _COMPONENT_JSP_CHROME_END,
                                   _AFTER_TEMPLATE_SCOPE_FREEMARKER_COMPONENT);
    }

    /**
     * This tests loading a page referencing a template scope, WebScript rendered component
     * wrapped in JSP rendered chrome.
     */
    @Test
    public void testTemplateScopeWebScriptComponentWithJSPChrome()
    {
        checkResponseTextOrdering("template_scope_webscript_component_with_jsp_chrome-page",
                                   _HTTP_GET_METHOD,
                                   _BEFORE_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT,
                                   _COMPONENT_JSP_CHROME_START,
                                   _BASIC_WEBSCRIPT_OUTPUT_3,
                                   _COMPONENT_JSP_CHROME_END,
                                   _AFTER_TEMPLATE_SCOPE_WEBSCRIPT_COMPONENT);
    }
}
