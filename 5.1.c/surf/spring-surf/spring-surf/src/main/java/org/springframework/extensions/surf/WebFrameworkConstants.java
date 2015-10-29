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

package org.springframework.extensions.surf;

/**
 * Web Framework constants
 *
 * @author muzquiano
 */
public class WebFrameworkConstants
{
    // Labels
    public static final String FRAMEWORK_TITLE = "Spring Surf";
    public static final String FRAMEWORK_VERSION = "1.0.0";
    public static final String FRAMEWORK_INITIALIZATION_SUCCESS_MESSAGE = "Successfully Initialized Spring Surf";

    // System Pages
    public static final String SYSTEM_PAGE_GETTING_STARTED = "page-getting-started";
    public static final String SYSTEM_PAGE_UNCONFIGURED = "page-unconfigured";
    public static final String SYSTEM_PAGE_CONTENT_NOT_LOADED = "page-content-not-loaded";
    public static final String SYSTEM_PAGE_CONTENT_ASSOCIATION_MISSING = "page-content-association-missing";

    // Dispatcher Handlers
    public static final String DISPATCHER_HANDLER_PAGE_ERROR = "page-error";
    public static final String DISPATCHER_HANDLER_COMPONENT_ERROR = "component-error";
    public static final String DISPATCHER_HANDLER_TEMPLATE_ERROR = "template-error";
    public static final String DISPATCHER_HANDLER_REGION_ERROR = "region-error";
    public static final String DISPATCHER_HANDLER_REGION_NO_COMPONENT = "region-nocomponent";
    public static final String DISPATCHER_HANDLER_GENERAL_ERROR = "general-error";

    // Region Scopes
    public static final String REGION_SCOPE_GLOBAL   = "global";
    public static final String REGION_SCOPE_TEMPLATE = "template";
    public static final String REGION_SCOPE_PAGE     = "page";
    public static final String REGION_SCOPE_URI      = "uri";
    public static final String REGION_SCOPE_THEME    = "theme";

    // Chromes
    public static final String DEFAULT_REGION_CHROME_ID = "default-region-chrome";
    public static final String CHROMELESS_REGION_CHROME_ID = "chromeless-region-chrome";
    public static final String DEFAULT_COMPONENT_CHROME_ID = "default-component-chrome";

    // Misc
    public static final String DEFAULT_ALFRESCO_ENDPOINT_ID = "alfresco";

    // Renderer Context Parameters (Page)
    public static final String RENDER_DATA_PAGE_ID = "page-id";
    public static final String RENDER_DATA_PAGE_TYPE_ID = "page-type-id";
    public static final String RENDER_DATA_TEMPLATE_ID = "template-id";
    public static final String RENDER_DATA_TEMPLATE_TYPE_ID = "template-type-id";
    public static final String RENDER_DATA_REGION_ID = "region-id";
    public static final String RENDER_DATA_REGION_SCOPE_ID = "region-scope-id";
    public static final String RENDER_DATA_REGION_SOURCE_ID = "region-source-id";
    public static final String RENDER_DATA_REGION_CHROME_ID = "region-chrome-id";
    public static final String RENDER_DATA_COMPONENT_ID = "component-id";
    public static final String RENDER_DATA_COMPONENT_TYPE_ID = "component-type-id";
    public static final String RENDER_DATA_COMPONENT_REGION_ID = "component-region-id";
    public static final String RENDER_DATA_COMPONENT_SOURCE_ID = "component-source-id";
    public static final String RENDER_DATA_COMPONENT_SCOPE_ID = "component-scope-id";
    public static final String RENDER_DATA_COMPONENT_CHROME_ID = "component-chrome-id";
    public static final String RENDER_DATA_COMPONENT_CHROME = "component-chrome";
    public static final String RENDER_DATA_COMPONENT = "component";
    public static final String RENDER_DATA_SUB_COMPONENT = "sub-component";
    public static final String RENDER_DATA_CHROMELESS = "chromeless";
    public static final String RENDER_DATA_SURFBUG_ENABLED = "surfBugEnabled";
    

    public static final String RENDER_DATA_HTMLID = "htmlid";
    public static final String RENDER_DATA_REQUEST_CONTEXT_STACK_KEY = "configuration-stack";

    // Model Persistence Store ID key name
    public static final String WEBAPP_ID_REQUEST_PARAM_NAME = "alfWebappId";
    public static final String WEBAPP_ID_SESSION_ATTRIBUTE_NAME = "alfWebappId";

    public static final String STORE_ID_REQUEST_PARAM_NAME = "alfStoreId";
    public static final String STORE_ID_SESSION_ATTRIBUTE_NAME = "alfStoreId";

    // Request Context Environment
    public static final String STORE_ID_REQUEST_CONTEXT_NAME = "alfStoreId";
    public static final String WEBAPP_ID_REQUEST_CONTEXT_NAME = "alfWebappId";

    // Theme
    public static final String DEFAULT_THEME_ID = "default";

    // Page Types
    public static final String GENERIC_PAGE_TYPE_DEFAULT_PAGE_ID = "generic";

    // Processor Types
    public static final String PROCESSOR_JSP = "jsp";
    public static final String PROCESSOR_FREEMARKER = "freemarker";
    public static final String PROCESSOR_WEBSCRIPT = "webscript";

    // Render context values
    public static final String STYLESHEET_RENDER_CONTEXT_NAME = "alfStylesheet";

    // Default user factory
    public static final String DEFAULT_USER_FACTORY_ID = "webframework.factory.user.default";

    // View resolver constants
    public static final String FOCUS = "focus";
    public static final String MODE = "mode";
    public static final String SCOPE_ID = "scopeId";
    public static final String REGION_ID = "regionId";
    public static final String SOURCE_ID = "sourceId";
    public static final String PAGE_ID = "pageId";
    public static final String TEMPLATE_ID = "templateId";

    // General, commonly re-used Strings...
    public static final String URI = "uri";
    public static final String URL = "url";

    // Constants for the benefit of the TagLib...
    public static final String CURRENT_RENDERER = "currentRenderer";
    public static final String RENDER_TYPE = "tagLibRenderType";
    public static final String RENDER_COMPONENT = "tagLibComponentRendering";
    public static final String RENDER_REGION = "tagLibRegionRendering";
    public static final String RENDER_SUB_COMPONENT = "tagLibSubComponentRendering";
}