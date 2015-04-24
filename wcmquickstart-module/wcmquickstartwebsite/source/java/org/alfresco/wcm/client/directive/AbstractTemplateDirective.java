/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.directive;

import javax.servlet.http.HttpServletRequest;

import freemarker.core.Environment;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModelException;

/**
 * Base class for all Web Editor directive implementations.
 * 
 * @author gavinc
 * @author Chris Lack
 */
public abstract class AbstractTemplateDirective implements TemplateDirectiveModel
{
    public static final long serialVersionUID = 4251970922970982753L;

    protected String urlPrefix = null;

    protected HttpServletRequest getRequest(Environment env) throws TemplateModelException
    {
        return ((HttpRequestHashModel) env.getDataModel().get("Request")).getRequest();
    }

    /**
     * Determines whether editing is currently enabled
     * 
     * @return true if editing is enabled
     * @throws TemplateModelException
     */
    protected boolean isEditingEnabled(Environment env) throws TemplateModelException
    {
        boolean editingEnabled = false;
        HttpServletRequest request = getRequest(env);
        Object enabledKey = request.getAttribute(TemplateConstants.REQUEST_ATTR_KEY_WEF_ENABLED);
        if (enabledKey != null && (enabledKey instanceof Boolean))
        {
            editingEnabled = (Boolean) enabledKey;
        }
        return editingEnabled;
    }

    /**
     * Returns the URL prefix for the web editor application.
     * 
     * @return The WEF prefix URL
     * @throws TemplateModelException
     */
    protected String getWebEditorUrlPrefix(Environment env) throws TemplateModelException
    {
        if (this.urlPrefix == null)
        {
            HttpServletRequest request = getRequest(env);
            String prefix = (String) request.getAttribute(TemplateConstants.REQUEST_ATTR_KEY_URL_PREFIX);
            if (prefix != null && prefix.length() > 0)
            {
                this.urlPrefix = prefix;
            }
            else
            {
                // try to load from properties file
                this.urlPrefix = TemplateProps.getUrlPrefix();
                if (this.urlPrefix == null)
                {
                    // assume a default
                    this.urlPrefix = request.getContextPath();
                }
            }
        }

        return this.urlPrefix;
    }

    /**
     * Determines whether debug is enabled for the web editor application
     * <p>
     * This is the value of the <code>debug</code> init parameter of the Web
     * Editor filter definition in web.xml. If the init parameter is not present
     * false will be returned.
     * </p>
     * 
     * @return true if debug is enabled
     * @throws TemplateModelException
     */
    protected boolean isDebugEnabled(Environment env) throws TemplateModelException
    {
        boolean debugEnabled = false;
        HttpServletRequest request = getRequest(env);
        Object debug = request.getAttribute(TemplateConstants.REQUEST_ATTR_KEY_DEBUG_ENABLED);
        if ((debug != null) && (debug instanceof Boolean))
        {
            debugEnabled = (Boolean) debug;
        }
        else
        {
            // load from properties file
            Boolean prop = TemplateProps.isDebugEnabled();
            if (prop != null)
            {
                debugEnabled = prop;
            }
        }
        return debugEnabled;
    }

    /**
     * Determins the location of the toolbar
     * 
     * @return string location
     * @throws TemplateModelException
     */
    protected String getToolbarLocation(Environment env) throws TemplateModelException
    {
        HttpServletRequest request = getRequest(env);
        return (String) request.getAttribute(TemplateConstants.REQUEST_ATTR_KEY_TOOLBAR_LOCATION);
    }

    /**
     * Encodes the given string, so that it can be used within an HTML page.
     * 
     * @param string
     *            the String to convert
     */
    protected String encode(String string)
    {
        if (string == null)
        {
            return "";
        }

        StringBuilder sb = null; // create on demand
        String enc;
        char c;
        for (int i = 0; i < string.length(); i++)
        {
            enc = null;
            c = string.charAt(i);
            switch (c)
            {
            case '"':
                enc = "&quot;";
                break; // "
            case '&':
                enc = "&amp;";
                break; // &
            case '<':
                enc = "&lt;";
                break; // <
            case '>':
                enc = "&gt;";
                break; // >

            case '\u20AC':
                enc = "&euro;";
                break;
            case '\u00AB':
                enc = "&laquo;";
                break;
            case '\u00BB':
                enc = "&raquo;";
                break;
            case '\u00A0':
                enc = "&nbsp;";
                break;

            default:
                if (((int) c) >= 0x80)
                {
                    // encode all non basic latin characters
                    enc = "&#" + ((int) c) + ";";
                }
                break;
            }

            if (enc != null)
            {
                if (sb == null)
                {
                    String soFar = string.substring(0, i);
                    sb = new StringBuilder(i + 16);
                    sb.append(soFar);
                }
                sb.append(enc);
            }
            else
            {
                if (sb != null)
                {
                    sb.append(c);
                }
            }
        }

        if (sb == null)
        {
            return string;
        }
        else
        {
            return sb.toString();
        }
    }
}
