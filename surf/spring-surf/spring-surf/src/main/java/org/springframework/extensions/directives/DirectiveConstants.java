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
package org.springframework.extensions.directives;

/**
 * <p>A Class for storing commonly used constants relating to the custom FreeMarker directives together.</p> 
 * @author David Draper
 */
public class DirectiveConstants
{
    public static final String SRC_PARAM = "src";
    public static final String GROUP_PARAM = "group";
    public static final String HREF_MEDIA = "href";
    public static final String EQUALS = "=";
    public static final String QUESTION_MARK = "?";
    public static final String SRC = "src";
    public static final String CHECKSUM_PARM = "parameter";
    public static final String EMPTY_STRING = "";
    
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    
    // String constants used to output the JavaScript used to instantiate the widget...
    public static final String VAR = "var ";
    public static final String NEW = "new ";
    public static final String OPEN_BRACKET = "(";
    public static final String COMMA = ", ";
    public static final String QUOTE = "\"";
    public static final String CLOSE_BRACKET = ")";
    public static final String SET_OPTIONS = ".setOptions(";
    public static final String SET_MESSAGES = ".setMessages(";
    public static final String CLOSE_LINE = ";\n";
    
    // Widget instantiation attributes...
    public final static String WEBSCRIPT_WIDGETS = "widgets";
    public final static String NAME = "name";
    public final static String PROVIDE_OPTIONS = "useOptions";
    public final static String PROVIDE_MESSAGES = "useMessages";
    public final static String OPTIONS = "options";
    public final static String ASSIGNMENT_VARIABLE_NAME = "assignTo";
    public final static String INSTANTIATION_ARGS = "initArgs";
    
    public static final String ARGS = "args";
    public static final String MESSAGES = "messages";
    public static final String MEDIA_PARAM = "media";
    public static final String DEFAULT_MEDIA = "screen";
    public static final String HREF = "href";
    public static final String TEMPLATE_INSTANCE_CSS_LINKS = "templateStylesheets";
    public static final String TYPE = "type";
    public static final String DEFAULT_TYPE = "text/javascript";
    public static final String BEGINNING = "   <script type=\"";
    public static final String MIDDLE = "\" src=\"";
    public static final String END = "\"></script>\n";
    
    // Constants used for adding CSS imports blocks...
    public static final String OPEN_CSS_1 = "   <style type=\"text/css\" media=\"";
    public static final String OPEN_CSS_2 = "\">\n";
    public static final String CSS_IMPORT = "      @import url(\"";
    public static final String DELIMIT_CSS_IMPORT = "\");\n";
    public static final String CLOSE_CSS = "   </style>\n\n";
    
    // Constants used for opening a JavaScript block...
    public static final String OPEN_SCRIPT = "   <script type=\"text/javascript\">//<![CDATA[\n";
    public static final String CLOSE_SCRIPT = "//]]></script>\n";
    
    // Constants used for adding JavaScript import blocks...
    public static final String OPEN_DEP_SCRIPT_TAG = "   <script type=\"text/javascript\" src=\"";
    public static final String CLOSE_DEP_SCRIPT_TAG = "\"></script>";
    public static final String OPEN_GROUP_COMMENT = " <!-- Group Name: \"";
    public static final String CLOSE_GROUP_COMMENT = "\" -->";
    public static final String NEW_LINE = "\n";
    
    // Constants used for adding CSS link blocks...
    public static final String OPEN_LINK_TAG = "   <link rel=\"stylesheet\" type=\"text/css\" href=\"";
    public static final String SET_MEDIA = "\" media=\"";
    public static final String CLOSE_LINK_TAG = "\">\n";
    public static final String DEPENDENCIES_COMMENT = "\n\n   <!-- Extension Module Dependencies -->\n\n";
    public static final String COULD_NOT_FIND_JS_COMMENT_OPEN = "   <!-- Could not find JavaScript dependency: \"";
    public static final String COULD_NOT_FIND_JS_COMMENT_CLOSE = "\" -->\n";
    public static final String COULD_NOT_FIND_CSS_COMMENT_OPEN = "      /* Could not find CSS dependency: \"";
    public static final String COULD_NOT_FIND_CSS_COMMENT_CLOSE = "\" */\n";
}
