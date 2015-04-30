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

package org.springframework.extensions.surf.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.types.Component;

/**
 * Static utility methods utilized during the rendering process.  These
 * are grouped here for convenience by developers who wish to build
 * custom renderer implementations. 
 * 
 * @author muzquiano
 * @author kevinr
 */
public final class RenderUtil
{    
    private static Log logger = LogFactory.getLog(RenderUtil.class);
    
    /**
     * Renders the fully formed URL string for a link to a given page
     *
     * @param context
     * @param request
     * @param response
     * @param objectId
     * @param formatId
     */
    public static void page(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, String pageId, String formatId, String objectId)
    {
        String url = context.getLinkBuilder().page(context, pageId, formatId, objectId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }
    }

    /**
    /**
     * Renders the fully formed URL string for a link to a given content object
     *
     * @param context
     * @param request
     * @param response
     * @param objectId
     * @param formatId
     */
    public static void object(RequestContext context,
            HttpServletRequest request, HttpServletResponse response,
            String objectId, String formatId)
    {
        String url = context.getLinkBuilder().object(context, objectId, formatId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }
    }

    /**
     * Renders the fully formed URL string for a link to a given page type
     *
     * @param context
     * @param request
     * @param response
     * @param objectId
     * @param formatId
     */
    public static void pageType(RequestContext context, HttpServletRequest request,
            HttpServletResponse response, String pageTypeId, String formatId, String objectId)
    {
        String url = context.getLinkBuilder().pageType(context, pageTypeId, formatId, objectId);
        if (url != null)
        {
            try
            {
                response.getWriter().write(url);
            }
            catch (Exception ex)
            {
                logger.error(ex);
            }
        }
    }

    /**
     * Renders an HTML <script/> tag to the output stream
     * 
     * @param context
     * @param uri
     * 
     * @return script tag to the resource
     */
    public static String renderScriptImport(RequestContext context, String uri)
    {
        String src = context.getLinkBuilder().resource(context, uri);
        
        // TODO: copy in query string?
        // TODO: this will be refactored once WEF integration begins (post 1.0.0.M3)
        /*
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 4)
            src = src + "?" + queryString;         
         */

        return "<script type=\"text/javascript\" src=\"" + src + "\"></script>";
    }

    /**
     * Renders an HTML stylesheet <link/> tag.
     * 
     * @param context
     * @param uri
     * 
     * @return script tag to the resource
     */
    public static String renderLinkImport(RequestContext context, String uri)
    {
        return renderLinkImport(context, uri, null);
    }

    /**
     * Renders an HTML stylesheet <link/> tag and optionally appends
     * the incoming query string to the URL.
     * 
     * @param context
     * @param uri
     * @param id
     * 
     * @return link tag to the resource
     */
    public static String renderLinkImport(RequestContext context, String uri, String id)
    {
        String href = context.getLinkBuilder().resource(context, uri);
        
        // TODO: include query string?
        // TODO: this will be refactored once WEF integration begins (post 1.0.0.M3)
        /*
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 4)
            href = href + "?" + queryString;
        */

        String value = "<link ";
        if (id != null)
        {
            value += "id=\"" + id + "\" ";
        }
        value += "rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\"></link>";

        return value;
    }    
    
    /** Mask for hex encoding. */
    private static final int MASK = (1 << 4) - 1;

    /** Digits used string encoding. */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    
    /**
     * Helper to ensure only valid and acceptable characters are output as HTML element IDs.
     * 
     * @param id the id
     * 
     * @return the string
     */
    public static String validHtmlId(String id)
    {
        final int len = id.length();
        final StringBuilder buf = new StringBuilder(len + (len>>1) + 8);
        for (int i = 0; i<len; i++)
        {
            final char c = id.charAt(i);
            final int ci = (int)c;
            if (i == 0)
            {
                if ((ci >= 97 && ci <= 122) ||   // a-z
                    (ci >= 65 && ci <= 90))      // A-Z
                {
                    buf.append(c);
                }
                else
                {
                    encodef(c, buf);
                }
            }
            else
            {
                if ((ci >= 97 && ci <= 122) ||  // a-z
                    (ci >= 65 && ci <= 90) ||   // A-Z
                    (ci >= 48 && ci <= 57) ||   // 0-9
                    ci == 45 || ci == 95)       // - and _
                {
                    buf.append(c);
                }
                else
                {
                    encode(c, buf);
                }
            }
        }
        return buf.toString();
    }
    
    /**
     * Encode.
     * 
     * @param c the c
     * @param builder the builder
     */
    private static void encode(char c, StringBuilder builder)
    {
        char[] buf = new char[] { '_', 'x', '0', '0', '0', '0', '_' };
        int charPos = 6;
        do
        {
            buf[--charPos] = DIGITS[c & MASK];
            c >>>= 4;
        }
        while (c != 0);
        builder.append(buf);
    }
    
    /**
     * Encodef.
     * 
     * @param c the c
     * @param builder the builder
     */
    private static void encodef(char c, StringBuilder builder)
    {
        char[] buf = new char[] { 'x', '0', '0', '0', '0', '_' };
        int charPos = 5;
        do
        {
            buf[--charPos] = DIGITS[c & MASK];
            c >>>= 4;
        }
        while (c != 0);
        builder.append(buf);
    }
    
    
    /**
     * Attempts to retrieve the render context instance bound to the given
     * http servlet request
     * 
     * @param request
     * @return
     */
    public static RequestContext getContext(HttpServletRequest request)
    {
        return (RequestContext) request.getAttribute(RenderContextRequest.ATTRIB_RENDER_CONTEXT);        
    }
    
    /**
     * Return the "source" ID for the given scope ID for the supplied context.
     * 
     * For 'global' scope this will simply return 'global',
     * for 'template' it will return the current template ID,
     * for 'page' it will return the current page ID,
     * for 'uri' it will return the current page URI,
     * for 'theme' it will return the current theme ID.
     * 
     * @param context   Current RequestContext
     * @param scopeId   {@link WebFrameworkConstants}
     * 
     * @return the source ID
     */
    public static String getSourceId(RequestContext context, String scopeId)
    {
        String sourceId = null;
        
        if (WebFrameworkConstants.REGION_SCOPE_GLOBAL.equals(scopeId))
        {
            sourceId = WebFrameworkConstants.REGION_SCOPE_GLOBAL;
        }
        else if (WebFrameworkConstants.REGION_SCOPE_TEMPLATE.equals(scopeId))
        {
            sourceId = context.getTemplateId();
        }
        else if (WebFrameworkConstants.REGION_SCOPE_PAGE.equals(scopeId))
        {
            sourceId = context.getPageId();
        }
        else if (WebFrameworkConstants.REGION_SCOPE_URI.equals(scopeId))
        {
            sourceId = (context.getViewName() != null ? context.getViewName() : context.getUri());
        }
        else if (WebFrameworkConstants.REGION_SCOPE_THEME.equals(scopeId))
        {
            sourceId = context.getThemeId();
        }
        
        return sourceId;
    }
    
    /**
     * Returns the object to which this component is bound
     * This is the same as calling component.getSourceObject()
     * 
     * @param context
     * @param component
     * @return
     */
    public static Object getComponentBindingSourceObject(RequestContext context, Component component)
    {
        Object obj = null;
        
        String scopeId = component.getScope();
        String sourceId = component.getSourceId();
        
        if (WebFrameworkConstants.REGION_SCOPE_GLOBAL.equals(scopeId))
        {
            obj = WebFrameworkConstants.REGION_SCOPE_GLOBAL;
        }
        else if (WebFrameworkConstants.REGION_SCOPE_TEMPLATE.equals(scopeId))
        {
            obj = context.getObjectService().getTemplate(sourceId);
        }
        else if (WebFrameworkConstants.REGION_SCOPE_PAGE.equals(scopeId))
        {
            obj = context.getObjectService().getPage(sourceId);
        }
        else if (WebFrameworkConstants.REGION_SCOPE_URI.equals(scopeId))
        {
            obj = (context.getViewName() != null ? context.getViewName() : context.getUri());
        }
        else if (WebFrameworkConstants.REGION_SCOPE_THEME.equals(scopeId))
        {
            obj = context.getObjectService().getTheme(sourceId);
        }
        
        return obj;
    }
    
    /**
     * Generates the deterministic component id from its key properties
     * 
     * @param scopeId
     * @param regionId
     * @param sourceId
     * 
     * @return deterministic component id based on supplied values
     */
    public static String generateComponentId(final String scopeId, final String regionId, final String sourceId)
    {
        String generatedId = null;
        
        if (scopeId != null && regionId != null)
        {
            StringBuilder id = new StringBuilder(64);
            id.append(scopeId).append('.').append(regionId);
            if (sourceId != null && !WebFrameworkConstants.REGION_SCOPE_GLOBAL.equals(scopeId))
            {
                id.append('.').append(sourceId.replace('/', '~'));
            }
            generatedId = id.toString();
        }
        
        return generatedId;
    }
}            