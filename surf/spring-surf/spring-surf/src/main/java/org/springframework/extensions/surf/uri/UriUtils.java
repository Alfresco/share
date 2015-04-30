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

package org.springframework.extensions.surf.uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.webscripts.ProcessorModelHelper;
import org.springframework.extensions.webscripts.URLHelper;

/**
 * Utility functions for dealing with URI and token replacement.
 * 
 * @author Kevin Roast
 */
public final class UriUtils
{
    private static Log logger = LogFactory.getLog(UriUtils.class);

    /**
     * Private constructor
     */
    private UriUtils()
    {
    }
    
    /**
     * <p>Builds a list of all the maps that can be used for performing token substitution. The list
     * will be populated as follows (assuming that each token map is available).
     * <ol>
     * <li>URI tokens</li>
     * <li>Request context parameters</p>
     * <li>Page URL arguments</li>
     * <li>Template URL arguments</li>
     * <li>Page properties</li>
     * <li>Template properties</li>
     * </ol></p>
     * 
     * @param context The current {@link RequestContext}.
     * @return
     */
    public static List<Map<String, String>> getTokenMaps(RequestContext context)
    {
        List<Map<String, String>> tokenMaps = new ArrayList<Map<String,String>>();
        if (context != null)
        {
            Map<String, String> uriTokens = context.getUriTokens();
            if (uriTokens != null)
            {
                tokenMaps.add(uriTokens);
            }
            Map<String, String> contextParams = context.getParameters();
            if (contextParams != null)
            {
                tokenMaps.add(contextParams);
            }
            
            Map<String, Object> model = context.getModel();
            if (model != null)
            {
                Object o = model.get(ProcessorModelHelper.MODEL_URL);
                if (o instanceof URLHelper)
                {
                    URLHelper urlHelper = (URLHelper) o;
                    Map<String, String> urlArgs = urlHelper.getArgs();
                    if (urlArgs != null)
                    {
                        tokenMaps.add(urlArgs);
                    }
                    
                    Map<String, String> templateArgs = urlHelper.getTemplateArgs();
                    if (templateArgs != null)
                    {
                        tokenMaps.add(templateArgs);
                    }
                }
            }
            
            Map<String, Serializable> contextAttributes = context.getAttributes();
            if (contextAttributes != null)
            {
                tokenMaps.add(convertModelObjectProps(contextAttributes));
            }
            
            Page page = context.getPage();
            if (page != null && page.getCustomProperties() != null)
            {
                tokenMaps.add(convertModelObjectProps(page.getCustomProperties()));
            }
            TemplateInstance template = context.getTemplate();
            if (template != null && template.getCustomProperties() != null)
            {
                tokenMaps.add(convertModelObjectProps(template.getCustomProperties()));
            }    
        }
        return tokenMaps;
    }
    
    /**
     * <p>Converts a {@link Map} of String to Serializable object to a {@link Map} of String to String.</p>
     * 
     * @param modelProps
     * @return
     */
    private static Map<String, String> convertModelObjectProps(Map<String, Serializable> modelProps)
    {
        Map<String, String> props = new HashMap<String, String>();
        for (Entry<String, Serializable> pageProp: modelProps.entrySet())
        {
            props.put(pageProp.getKey(), (pageProp.getValue() != null ? pageProp.getValue().toString() : null));
        }
        return props;
    }
    
    
    public static Pattern pattern = Pattern.compile("\\{([A-Za-z0-9_\\-]*)\\}");
    
    /**
     * <p>Replaces tokens in using all available properties built from the <code>getTokenMaps()</code> method.</p>
     * 
     * @param s The String to replace the tokens in.
     * @param context The current {@link RequestContext}
     * @param tokenPattern An alternative token pattern to process (if null, uses the default)
     * @param groupNumber The group number to use as the token key (if null, defaults to 1)
     * @param missingTokenString TODO
     * @return The String with all tokens replaced.
     */
    public static String replaceTokens(String s, RequestContext context, Pattern tokenPattern, Integer groupNumber, String missingTokenString)
    {
        StringBuffer result = new StringBuffer();
        if (s != null && context != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Replacing tokens in: '" + s + "'");
            }
            List<Map<String,String>> tokenMaps = getTokenMaps(context);
            if (tokenMaps.isEmpty())
            {
                // No token maps to search through...
                result.append(s);
            }
            else
            {
                Pattern p = (tokenPattern != null) ? tokenPattern : pattern;
                int gNum = (groupNumber != null) ? groupNumber.intValue() : 1;
                Matcher m = p.matcher(s);
                while (m.find())
                {
                    String key = m.group(gNum);
                    String token = null;
                    Iterator<Map<String, String>> i = tokenMaps.iterator();
                    while (token == null && i.hasNext())
                    {
                        token = i.next().get(key);
                    }
                    if (token == null)
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Could not find token for: '" + key + "'");
                        }
                        m.appendReplacement(result, missingTokenString);
                    }
                    else
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Replacing token: '" + key + "' with: '" + token + "'");
                        }
                        m.appendReplacement(result, Matcher.quoteReplacement(token));
                    }
                }
                m.appendTail(result);
            }
        }
        else
        {
            result.append(s);
        }
        
        return result.toString();
    }
    
    /**
     * Helper to replace tokens in a string with values from a map of token->value.
     * Token names in the string are delimited by '{' and '}' - the entire token name
     * plus the delimiters are replaced by the value found in the supplied replacement map.
     * If no replacement value is found for the token name, it is replaced by the empty string.
     * 
     * @param s       String to work on - cannot be null
     * @param tokens  Map of token name -> token value for replacements
     * @return the replaced string or the original if no tokens found or a failure occurs
     * @deprecated
     */
    public static String replaceUriTokens(String s, Map<String, String> tokens)
    {
        String result = s;
        int preIndex = 0;
        if (s != null)
        {
            int delimIndex = s.indexOf('{');
            if (delimIndex != -1)
            {
                StringBuilder buf = new StringBuilder(s.length() + 16);
                do
                {
                    // copy up to token delimiter start
                    buf.append(s.substring(preIndex, delimIndex));

                    // extract token and replace
                    if (s.length() < delimIndex + 2)
                    {
                        if (logger.isWarnEnabled())
                            logger.warn("Failed to replace context tokens - malformed input: " + s);
                        return s;
                    }
                    int endDelimIndex = s.indexOf('}', delimIndex + 2);
                    if (endDelimIndex == -1)
                    {
                        if (logger.isWarnEnabled())
                            logger.warn("Failed to replace context tokens - malformed input: " + s);
                        return s;
                    }
                    String token = s.substring(delimIndex + 1, endDelimIndex);
                    String replacement = tokens.get(token);
                    if (replacement == null)
                    {
                        replacement = "";
                    }
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Replacing token '" + token + "' with: '" + replacement + "'");
                    }
                    buf.append(replacement);

                    // locate next delimiter and mark end of previous delimiter
                    preIndex = endDelimIndex + 1; 
                    delimIndex = s.indexOf('{', preIndex);
                    if (delimIndex == -1 && s.length() > preIndex)
                    {
                        // append suffix of original string after the last delimiter found
                        buf.append(s.substring(preIndex));
                    }
                } while (delimIndex != -1);

                result = buf.toString();
            }
        }
        
        return result;
    }
    
    /**
     * Return the relative section of a URI from a complete URI
     * 
     * @param uri       URI to process
     * 
     * @return relative part of the URI
     */
    private static Pattern URI_PATTERN = Pattern.compile("^(?:(?![^:@]+:[^:@/]*@)([^:/?#.]+):)?(?://)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:/?#]*)(?::(\\d*))?)(((/(?:[^?#](?![^?#/]*\\.[^?#/.]+(?:[?#]|$)))*/?)?([^?#/]*))(?:\\?([^#]*))?(?:#(.*))?)");
    private static String[] URI_PARTS =
    {
        "source", "protocol", "authority", "userInfo", "user", "password", "host", "port", "relative", "path", "directory", "file", "query", "ref"
    };
    
    /**
     * Return the relative section of a URI from a complete URI
     * 
     * @param uri       URI to process
     * 
     * @return relative part of the URI
     */
    public static String relativeUri(String uri)
    {
        Map<String, String> tempUri = new HashMap<String, String>(16);
        
        /*
         * Original regex:
         *    parseUri 1.2.2
         *    http://stevenlevithan.com/demo/parseuri/js/assets/parseuri.js
         *    (c) Steven Levithan <stevenlevithan.com>
         *    MIT License
         */
        Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.find())
        {
            for (int i = 0; i < URI_PARTS.length; i++)
            {
                String match;
                try
                {
                    match = matcher.group(i);
                }
                catch (Exception ex)
                {
                    match = "*";
                }
                tempUri.put(URI_PARTS[i], match == null ? "*" : match);
            }
        }
        return tempUri.get("relative");
    }
}
