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

package org.springframework.extensions.surf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * Implementation of HttpServletRequest that mimicks a servlet originated object and provides identity implementations
 * of the methods.
 * 
 * @author muzquiano
 */
public class FakeHttpServletRequest extends HttpServletRequestWrapper
{
    /**
     * @param request
     */
    public FakeHttpServletRequest(HttpServletRequest request)
    {
        super(request);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    @Override
    public String getAuthType()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getCookies()
     */
    @Override
    public Cookie[] getCookies()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
     */
    @Override
    public long getDateHeader(String name)
    {
        return -1;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String name)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
     */
    @Override
    public Enumeration getHeaders(String name)
    {
        return new Vector().elements();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     */
    @Override
    public Enumeration getHeaderNames()
    {
        return new Vector().elements();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
     */
    @Override
    public int getIntHeader(String name)
    {
        return -1;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    @Override
    public String getMethod()
    {
        return "GET";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     */
    @Override
    public String getPathInfo()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     */
    @Override
    public String getPathTranslated()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     */
    @Override
    public String getContextPath()
    {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    @Override
    public String getQueryString()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
     */
    @Override
    public String getRemoteUser()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
     */
    @Override
    public boolean isUserInRole(String role)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
     */
    @Override
    public String getRequestedSessionId()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    @Override
    public String getRequestURI()
    {
        return "/";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     */
    @Override
    public StringBuffer getRequestURL()
    {
        return new StringBuffer("http://localhost/");
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     */
    @Override
    public String getServletPath()
    {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    @Override
    public HttpSession getSession(boolean create)
    {
        if (!create)
        {
            return null;
        }
        return new FakeHttpSession();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getSession()
     */
    @Override
    public HttpSession getSession()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
     */
    @Override
    public boolean isRequestedSessionIdValid()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
     */
    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
     */
    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
     */
    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name)
    {
        return this.attributes.get(name);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getAttributeNames()
     */
    @Override
    public Enumeration getAttributeNames()
    {
        return Collections.enumeration(this.attributes.keySet());
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding()
    {
        return this.characterEncoding;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException
    {
        this.characterEncoding = characterEncoding;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    @Override
    public int getContentLength()
    {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getContentType()
     */
    @Override
    public String getContentType()
    {
        return "text/plain";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        return new ServletInputStream()
        {
            @Override
            public int read() throws IOException
            {
                return -1;
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    @Override
    public String getLocalAddr()
    {
        return "127.0.0.1";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    @Override
    public String getLocalName()
    {
        return "localhost";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    @Override
    public int getLocalPort()
    {
        return 80;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @Override
    public Enumeration getParameterNames()
    {
        return new Vector().elements();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    @Override
    public String[] getParameterValues(String name)
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    @Override
    public Map getParameterMap()
    {
        return Collections.EMPTY_MAP;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    @Override
    public String getProtocol()
    {
        return "HTTP/1.1";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getScheme()
     */
    @Override
    public String getScheme()
    {
        return "http";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getServerName()
     */
    @Override
    public String getServerName()
    {
        return "localhost";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getServerPort()
     */
    @Override
    public int getServerPort()
    {
        return 80;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getReader()
     */
    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new StringReader(""));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr()
    {
        return "localhost";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    @Override
    public String getRemoteHost()
    {
        return "localhost";
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    @Override
    public int getRemotePort()
    {
        return 80;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object o)
    {
        this.attributes.put(name, o);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name)
    {
        this.attributes.remove(name);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return Locale.getDefault();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocales()
     */
    @Override
    public Enumeration getLocales()
    {
        return Collections.enumeration(Arrays.asList(new Locale[]
        {
            Locale.getDefault()
        }));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#isSecure()
     */
    @Override
    public boolean isSecure()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path)
    {
        return new RequestDispatcher()
        {
            public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException
            {
            }

            public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException
            {
            }
        };
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
     */
    @Override
    public String getRealPath(String path)
    {
        return null;
    }

    /** The character encoding. */
    private String characterEncoding = null;

    /** The attributes. */
    private Map attributes = new HashMap(24, 1.0f);
}
