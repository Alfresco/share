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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * An implementation of HttpSession which provides a local notion of a session.
 * All session data is stored locally within the class instance.
 * 
 * @author muzquiano
 */
public class FakeHttpSession implements HttpSession
{
    
    /**
     * Instantiates a new fake http session.
     */
    public FakeHttpSession()
    {
        super();
        creationTime = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getCreationTime()
     */
    public long getCreationTime()
    {
        return creationTime;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getId()
     */
    public String getId()
    {
        return "alfresco";
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getLastAccessedTime()
     */
    public long getLastAccessedTime()
    {
        return creationTime;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getServletContext()
     */
    public ServletContext getServletContext()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    public void setMaxInactiveInterval(int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    public javax.servlet.http.HttpSessionContext getSessionContext()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    public Object getValue(String name)
    {
        return attributes.get(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        return Collections.enumeration(attributes.keySet());
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    public String[] getValueNames()
    {
        return (String[]) attributes.keySet().toArray(
                new String[attributes.keySet().size()]);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    public void putValue(String name, Object value)
    {
        attributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    public void removeValue(String name)
    {
        attributes.remove(name);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#invalidate()
     */
    public void invalidate()
    {
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSession#isNew()
     */
    public boolean isNew()
    {
        return true;
    }

    /** The attributes. */
    private Map attributes = new HashMap(24, 1.0f);

    /** The creation time. */
    private long creationTime;

    /** The max inactive interval. */
    private int maxInactiveInterval = 30 * 60 * 1000;
}
