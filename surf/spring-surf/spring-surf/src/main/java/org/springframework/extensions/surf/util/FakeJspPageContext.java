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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

import org.springframework.extensions.surf.site.RequestUtil;

/**
 * Fake Jsp PageContext implementation which wraps predescribed HTTP objects.
 * 
 * @author muzquiano
 */
public class FakeJspPageContext
    extends PageContext
{
    
    /** The exception. */
    protected Exception exception;
    
    /** The values. */
    protected Map<String, Object> values;
    
    /** The context. */
    protected ServletContext context;
    
    /** The request. */
    protected HttpServletRequest request;
    
    /** The response. */
    protected HttpServletResponse response;
    
    /** The out. */
    protected JspWriter out;
    
    /**
     * Instantiates a new fake jsp page context
     * 
     * @param context The ServletContext to wrap
     * @param request The HttpServletRequest instance to wrap
     * @param response The HttpServletResponse instance to wrap
     * @param out The JspWriter to wrap
     */
    public FakeJspPageContext(ServletContext context, HttpServletRequest request, HttpServletResponse response, JspWriter out)
    {
        this.context = context;
        this.request = request;
        this.response = response;
        this.out = out;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getRequest()
     */
    public ServletRequest getRequest()
    {
        return this.request;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getResponse()
     */
    public ServletResponse getResponse()
    {
        return this.response;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getServletContext()
     */
    public ServletContext getServletContext()
    {
        return this.context;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getServletConfig()
     */
    public ServletConfig getServletConfig()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getOut()
     */
    public JspWriter getOut()
    {
        return this.out;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getSession()
     */
    public HttpSession getSession()
    {
        return this.request.getSession();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#findAttribute(java.lang.String)
     */
    public Object findAttribute(String name)
    {
        Object ret = getAttribute(name, PAGE_SCOPE);
        if (ret != null)
            return ret;
        ret = getAttribute(name, REQUEST_SCOPE);
        if (ret != null)
            return ret;
        ret = getAttribute(name, SESSION_SCOPE);
        if (ret != null)
            return ret;
        ret = getAttribute(name, APPLICATION_SCOPE);
        if (ret != null)
            return ret;
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        return findAttribute(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getAttribute(java.lang.String, int)
     */
    public Object getAttribute(String name, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                return getServletContext().getAttribute(name);
            case REQUEST_SCOPE:
                Object ret = getRequest().getAttribute(name);
                if (ret == null)
                    ret = getRequest().getParameter(name);
                return ret;
            case SESSION_SCOPE:
                if (getSession() != null)
                    return getSession().getAttribute(name);
                else
                    return null;
            case PAGE_SCOPE:
                return getValue(name);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object obj)
    {
        setValue(name, obj);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String, java.lang.Object, int)
     */
    public void setAttribute(String name, Object obj, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                getServletContext().setAttribute(name, obj);
                break;
            case REQUEST_SCOPE:
                getRequest().setAttribute(name, obj);
                break;
            case SESSION_SCOPE:
                if (getSession() != null)
                    getSession().setAttribute(name, obj);
                break;
            case PAGE_SCOPE:
                setValue(name, obj);
                break;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        removeValue(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String, int)
     */
    public void removeAttribute(String name, int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                getServletContext().removeAttribute(name);
                break;
            case REQUEST_SCOPE:
                getRequest().removeAttribute(name);
                break;
            case SESSION_SCOPE:
                if (getSession() != null)
                    getSession().removeAttribute(name);
                break;
            case PAGE_SCOPE:
                removeValue(name);
                break;
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getAttributeNamesInScope(int)
     */
    public Enumeration getAttributeNamesInScope(int scope)
    {
        switch (scope)
        {
            case APPLICATION_SCOPE:
                return getServletContext().getAttributeNames();
            case REQUEST_SCOPE:
                return getRequest().getAttributeNames();
            case SESSION_SCOPE:
                return getSession().getAttributeNames();
            case PAGE_SCOPE:
                return getValueNames();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getAttributesScope(java.lang.String)
     */
    public int getAttributesScope(String name)
    {
        if (getValue(name) != null)
            return PAGE_SCOPE;

        // allow request attributes to override request parameters
        if (getRequest().getAttribute(name) != null)
            return REQUEST_SCOPE;
        if (getRequest().getParameter(name) != null)
            return REQUEST_SCOPE;
        if (getSession().getAttribute(name) != null)
            return SESSION_SCOPE;
        if (getServletContext().getAttribute(name) != null)
            return APPLICATION_SCOPE;

        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#forward(java.lang.String)
     */
    public void forward(String url) throws ServletException, IOException
    {
        RequestUtil.forward(getServletContext(), getRequest(), getResponse(), url);        
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#include(java.lang.String)
     */
    public void include(String url) throws ServletException, IOException
    {
        include(url, true);
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#include(java.lang.String, boolean)
     */
    public void include(String url, boolean b) throws ServletException,
            IOException
    {
        RequestUtil.include(getServletContext(), getRequest(), getResponse(), url);
        
        // make sure everything is flushed before doing an include -- important for included JSP files
        flushOut();
    }

    /**
     * Flush out.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public void flushOut() throws java.io.IOException
    {
        out.flush();
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#release()
     */
    public void release()
    {
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getExpressionEvaluator()
     */
    public ExpressionEvaluator getExpressionEvaluator()
    {
        return null;

    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.JspContext#getVariableResolver()
     */
    public VariableResolver getVariableResolver()
    {
        return null;

    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Throwable)
     */
    public void handlePageException(Throwable t)
    {
        // TODO?
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Exception)
     */
    public void handlePageException(Exception e)
    {
        exception = e;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getException()
     */
    public Exception getException()
    {
        return exception;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getPage()
     */
    public Object getPage()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#getELContext()
     */
    @Override
    public ELContext getELContext()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.jsp.PageContext#initialize(javax.servlet.Servlet, javax.servlet.ServletRequest, javax.servlet.ServletResponse, java.lang.String, boolean, int, boolean)
     */
    public void initialize(Servlet srv, ServletRequest req,
            ServletResponse res, String s1, boolean b1, int i1, boolean b2)
    {
    }
    
    
    // local page context helper methods
    
    /**
     * Gets the value.
     * 
     * @param key
     *            the key
     * 
     * @return the value
     */
    protected Object getValue(String key)
    {
        if (values == null)
            values = new HashMap<String, Object>();
        return values.get(key);
    }
    
    /**
     * Sets the value.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     */
    protected void setValue(String key, Object value)
    {
        if (values == null)
            values = new HashMap<String, Object>();
        values.put(key, value);
    }
    
    /**
     * Removes the value.
     * 
     * @param key
     *            the key
     */
    protected void removeValue(String key)
    {
        if (values == null)
            values = new HashMap<String, Object>();
        values.remove(key);
    }
    
    /**
     * Gets the value names.
     * 
     * @return the value names
     */
    protected Enumeration getValueNames()
    {
        ArrayList<Object> array = new ArrayList<Object>();

        Iterator it = values.keySet().iterator();
        while (it.hasNext())
        {
            array.add(it.next());
        }

        return java.util.Collections.enumeration(array);
    }


}
