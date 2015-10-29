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

import org.junit.Assert;
import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;

import static org.springframework.extensions.surf.test.basic.Constants.*;

/**
 * <p>This test suite aims to ensure that all the Surf custom JSP Tab Library is fully functional. At
 * the time of writing the Tab Library is not only used in JSP but is also used by the custom FreeMarker
 * directives. Ultimate the aim should be for both the Tag Library and FreeMarker Directives using common
 * code rather than one building on the other - this test suite should help ensure that nothing gets
 * broken during the transition.</p>
 *
 * @author David Draper
 */
public class TagLibTest extends AbstractJettyTest
{
    /**
     * <p>The <{@code}region> tag will render a region with an with id matching the <code>name</code> attribute
     * bound to the scope defined by the <code>scope</code> attribute. This tag originally allowed you to optionally
     * provide <code>chrome</code>, <code>chromeless</code> and <code>protect</code>. However, the use of <code>chrome</code>
     * and <code>chromeless</code> are opposing (so <code>chromeless</code> should be removed) and there are no
     * accessor methods for the <code>protect</code> attribute so this will never have worked so will also be removed.</code>
     */
    @Test
    public void testRegionTag()
    {
        // Get the response...
        String response = requestResourceAssertingResponse("taglib_region_test-page", _HTTP_GET_METHOD);

        // Check the response for the various combinations of scope and default/custom chrome, storing the String match of each...
        String globalComponentDefaultChrome = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_1, response);
        globalComponentDefaultChrome = findWrappedInDefaultRegionChrome(globalComponentDefaultChrome, response);

        String pageComponentDefaultChrome = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_2, response);
        pageComponentDefaultChrome = findWrappedInDefaultRegionChrome(pageComponentDefaultChrome, response);

        String templateComponentDefaultChrome = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_3, response);
        templateComponentDefaultChrome = findWrappedInDefaultRegionChrome(templateComponentDefaultChrome, response);

        String globalComponentCustomChrome = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_1, response, _REGION_JSP_CHROME_START, _REGION_JSP_CHROME_END);
        String pageComponentCustomChrome = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_2, response, _REGION_JSP_CHROME_START, _REGION_JSP_CHROME_END);
        String templateComponentCustomChrome = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_3, response, _REGION_JSP_CHROME_START, _REGION_JSP_CHROME_END);

        // Check the order of all the constituent parts of the expected response...
        assertOccurenceAndOrder(response,
                                _BEFORE_TEMPLATE_REGION_DIRECTIVE,
                                globalComponentDefaultChrome,
                                pageComponentDefaultChrome,
                                templateComponentDefaultChrome,
                                globalComponentCustomChrome,
                                pageComponentCustomChrome,
                                templateComponentCustomChrome,
                                _BASIC_JSP_OUTPUT_1,
                                _BASIC_JSP_OUTPUT_2,
                                _BASIC_JSP_OUTPUT_3,
                                _AFTER_TEMPLATE_REGION_DIRECTIVE);
    }

    /**
     * <p>The <{@code}component> tag will render any component with an id matching the <code>component</code> attribute (a component
     * id contains the scope, regionId and sourceId). The test page that is rendered attempts to render components that are bound to
     * each scope and then attempts to render them again but sets chrome.</p>
     */
    @Test
    public void testComponentTag()
    {
        // Get the response...
        String response = requestResourceAssertingResponse("taglib_component_test-page", _HTTP_GET_METHOD);

        // Check the response for the various combinations of scope and default/custom chrome, storing the String match of each...
        String globalComponentDefaultChrome = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_1, response);
        String pageComponentDefaultChrome = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_2, response);
        String templateComponentDefaultChrome = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_3, response);
        String globalComponentCustomChrome = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_1, response, _COMPONENT_JSP_CHROME_START, _COMPONENT_JSP_CHROME_END);
        String pageComponentCustomChrome = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_2, response, _COMPONENT_JSP_CHROME_START, _COMPONENT_JSP_CHROME_END);
        String templateComponentCustomChrome = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_3, response, _COMPONENT_JSP_CHROME_START, _COMPONENT_JSP_CHROME_END);

        // Check the order of all the constituent parts of the expected response...
        assertOccurenceAndOrder(response,
                                _BEFORE_TEMPLATE_REGION_DIRECTIVE,
                                globalComponentDefaultChrome,
                                pageComponentDefaultChrome,
                                templateComponentDefaultChrome,
                                globalComponentCustomChrome,
                                pageComponentCustomChrome,
                                templateComponentCustomChrome,
                                _BASIC_JSP_OUTPUT_1,
                                _BASIC_JSP_OUTPUT_2,
                                _BASIC_JSP_OUTPUT_3,
                                _AFTER_TEMPLATE_REGION_DIRECTIVE);
    }

    /**
     * <p>This test is designed to check that errors DO NOT occur when a region requests to use component chrome (by using
     * the <{@code}componentInclude> rather than the <{@code}regionInclude> tag). Previously this would cause SpringSurf to
     * go into an infinite loop, however it should now allow the page to render. The interesting side effect of this is that
     * the component is rendered in 2 layers of component chrome (because that is effectively what has been requested) rather
     * than region chrome wrapping component chrome.</p>
     */
    @Test
    public void testRegionTagFail()
    {
        String response = requestResourceAssertingResponse("taglib_region_fail_test-page", _HTTP_GET_METHOD);
        String match = findWrappedInDefaultComponentChrome(_BASIC_JSP_OUTPUT_1, response);
        match = findBetweenPrefixAndSuffix(match, response, _COMPONENT_JSP_CHROME_START, _COMPONENT_JSP_CHROME_END);
        match = findWrappedInDefaultComponentChrome(match, response);
        match = findWrappedInDefaultRegionChrome(match, response);
        findBetweenPrefixAndSuffix(match, response, _BEFORE_TEMPLATE_REGION_DIRECTIVE, _AFTER_TEMPLATE_REGION_DIRECTIVE);
    }

    /**
     * <p>This test is designed to check that errors DO NOT occur when a component requests to use region chrome (by using
     * the <{@code}regionInclude> rather than the <{@code}componentInclude>tag. Previously this would cause SpringSurf to
     * generate an <code>OutOfMemoryException</code>, however it should now allow the page to render. The interesting side
     * effect of this is that the component is now just wrapped in region chrome (as requested) rather than region chrome
     * wrapping component chrome.</p>
     */
    @Test
    public void testComponentTagFail()
    {
        String response = requestResourceAssertingResponse("taglib_component_fail_test-page", _HTTP_GET_METHOD);
        String match = findBetweenPrefixAndSuffix(_BASIC_JSP_OUTPUT_1, response, _REGION_JSP_CHROME_START, _REGION_JSP_CHROME_END);
        match = findWrappedInDefaultComponentChrome(match, response);
        match = findWrappedInDefaultRegionChrome(match, response);
        findBetweenPrefixAndSuffix(match, response, _BEFORE_TEMPLATE_REGION_DIRECTIVE, _AFTER_TEMPLATE_REGION_DIRECTIVE);
    }

    /**
     * <p>This test checks that the <{@code}head> tag renders correctly. The tag itself seems to have little or no use and the
     * info originally provided for it in the TLD could not possibly be true (it suggests that it will "import the required dependencies into
     * the HTML HEAD" which will of course be impossible depending upon where the tag is referenced. The code has been fixed
     * up to ensure that a <code>ClassCastException</code> is no longer thrown when using it.</p>
     */
    @Test
    public void testHeadTag()
    {
        String response = requestResourceAssertingResponse("taglib_head_test-page", _HTTP_GET_METHOD);
        Assert.assertTrue(response.startsWith("Head!"));
    }

    /**
     * <p>The pageTitle tag should render the contents of the <{@code}title> tag from the page definition. Interestingly,
     * if you request the component directly (using the "c" or "component" prefix) then page title is rendered as
     * "Default Page".</p>
     * TODO: Test to see if "Default Page" is a localised value?
     */
    @Test
    public void testPageTitleTag()
    {
        checkResponseForContent("taglib_pagetitle_test-page", _HTTP_GET_METHOD, "Example Page Title");
        checkResponseForContent("c/scope/page/regionId/taglib_pagetitle_test-region/sourceId/taglib_pagetitle_test-page", _HTTP_GET_METHOD, "Default Page");
    }

//    /**
//     * <p>This test checks that the <{@code}link> tag renders correctly. This was previously only available as a FreeMarker directive
//     * but has not been added to the TLD so that it can be used in JSPs. The purpose of the tag is to use as a workaround for an
//     * IE bug. It should successfully render all CSS imports into a single <{@code}style> tag.</p>
//     */
//    @Test
//    public void testLinkTag()
//    {
//        checkResponseTextOrdering("taglib_link_test-page",
//                                  _HTTP_GET_METHOD,
//                                  _BEFORE_TEMPLATE_REGION_DIRECTIVE,
//                                  _DEFAULT_REGION_CHROME_PREFIX,
//                                  _DEFAULT_COMPONENT_CHROME_PREFIX,
//                                  "<style type=\"text/css\" media=\"screen\">",
//                                  "@import \"/css/test_css_1.css\";",
//                                  "@import \"/css/test_css_2.css\";",
//                                  "</style>",
//                                  _DEFAULT_COMPONENT_CHROME_SUFFIX,
//                                  _DEFAULT_REGION_CHROME_SUFFIX,
//                                  _AFTER_TEMPLATE_REGION_DIRECTIVE);
//    }

    /**
     * <p>This test checks that the <{@code}pagelink> tag renders correctly. The tag should only render the a path and not a complete
     * anchor (that's what the <{@code}anchor> tag does). 8 paths will be rendered in all:
     * <ul>
     * <li>page</li>
     * <li>page type</li>
     * <li>page with format request parameter</li>
     * <li>page type with format request parameter</li>
     * <li>page with object request parameter</li>
     * <li>page type with object request parameter</li>
     * <li>page with format and object request parameters</li>
     * <li>page type with format and object request parameters</li>
     * </ul>
     * </p>
     */
    @Test
    public void testPageLinkTag()
    {
        checkResponseTextOrdering("taglib_pagelink_test-page",
                                  _HTTP_GET_METHOD,
                                  _BEFORE_TEMPLATE_REGION_DIRECTIVE,
                                  _DEFAULT_REGION_CHROME_PREFIX,
                                  _DEFAULT_COMPONENT_CHROME_PREFIX,
                                  "/basic_template-page",
                                  "/pt/basic-pageType",
                                  "/basic_template-page?f=dummyFormat",
                                  "/pt/basic-pageType?f=dummyFormat",
                                  "/basic_template-page?o=dummyObject",
                                  "/pt/basic-pageType?o=dummyObject",
                                  "/basic_template-page?f=dummyFormat&o=dummyObject",
                                  "/pt/basic-pageType?f=dummyFormat&o=dummyObject",
                                  _DEFAULT_COMPONENT_CHROME_SUFFIX,
                                  _DEFAULT_REGION_CHROME_SUFFIX,
                                  _AFTER_TEMPLATE_REGION_DIRECTIVE);
    }

    /**
     * <p> This test checks that the <{@code}anchor> tag renders correctly. The tag should render an HTML anchor tag to provide the link
     * defined by the arguments provided. It relies on the tag being given a body to actually render something visible. All permutations of
     * the various arguments are used to construct a total of 16 different links.</p>
     */
    @Test
    public void testAnchorTag()
    {
        checkResponseTextOrdering("taglib_anchor_test-page",
                                  _HTTP_GET_METHOD,
                                  _BEFORE_TEMPLATE_REGION_DIRECTIVE,
                                  _DEFAULT_REGION_CHROME_PREFIX,
                                  _DEFAULT_COMPONENT_CHROME_PREFIX,
                                  "<A href=\"/basic_template-page\">PageLink</A>",
                                  "<A href=\"/basic_template-page\" target=\"dummyTarget\">PageLinkWithTarget</A>",
                                  "<A href=\"/basic_template-page?o=dummyObject\">PageLinkWithObject</A>",
                                  "<A href=\"/basic_template-page?f=dummyFormat\">PageLinkWithFormat</A>",
                                  "<A href=\"/basic_template-page?o=dummyObject\" target=\"dummyTarget\">PageLinkWithTargetAndObject</A>",
                                  "<A href=\"/basic_template-page?f=dummyFormat\" target=\"dummyTarget\">PageLinkWithTargetAndFormat</A>",
                                  "<A href=\"/basic_template-page?f=dummyFormat&o=dummyObject\">PageLinkWithObjectAndFormat</A>",
                                  "<A href=\"/basic_template-page?f=dummyFormat&o=dummyObject\" target=\"dummyTarget\">PageLinkWithTargetAndObjectAndFormat</A>",
                                  "<A href=\"/basic-pageType\">PageTypeLink</A>",
                                  "<A href=\"/basic-pageType\" target=\"dummyTarget\">PageTypeLinkWithTarget</A>",
                                  "<A href=\"/basic-pageType?o=dummyObject\">PageTypeLinkWithObject</A>",
                                  "<A href=\"/basic-pageType?f=dummyFormat\">PageTypeLinkWithFormat</A>",
                                  "<A href=\"/basic-pageType?o=dummyObject\" target=\"dummyTarget\">PageTypeLinkWithTargetAndObject</A>",
                                  "<A href=\"/basic-pageType?f=dummyFormat\" target=\"dummyTarget\">PageTypeLinkWithTargetAndFormat</A>",
                                  "<A href=\"/basic-pageType?f=dummyFormat&o=dummyObject\">PageTypeLinkWithObjectAndFormat</A>",
                                  "<A href=\"/basic-pageType?f=dummyFormat&o=dummyObject\" target=\"dummyTarget\">PageTypeLinkWithTargetAndObjectAndFormat</A>",
                                  _DEFAULT_COMPONENT_CHROME_SUFFIX,
                                  _DEFAULT_REGION_CHROME_SUFFIX,
                                  _AFTER_TEMPLATE_REGION_DIRECTIVE);
    }

    /**
     *
     */
    @Test
    public void testResourceTag()
    {
        // TODO: Test this tag
    }
}
