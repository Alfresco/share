package org.alfresco.web.site.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Request wrapper used an Accept-Langauge header was not present in original
 * request.
 * 
 * @author Eugene Zheleznyakov
 */
public class SlingshotServletRequestWrapper extends HttpServletRequestWrapper
{
    private Map<String, String> headerMap;

    public SlingshotServletRequestWrapper(HttpServletRequest request)
    {
        super(request);
        headerMap = new HashMap<String, String>(8);
    }

    /**
     * Adds a header to the request
     * 
     * @param name The name of header
     * @param value The value of header
     */
    public void addHeader(String name, String value)
    {
        headerMap.put(name, value);
    }

    /**
     * @see javax.servlet.http.HttpServletRequestWrapper#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String name)
    {
        String value = headerMap.get(name);
        if (value != null)
        {
            return value;
        }
        else
        {
            return ((HttpServletRequest) getRequest()).getHeader(name);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletRequestWrapper#getHeaders(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Enumeration getHeaders(String name)
    {
        String value = headerMap.get(name);
        if (value != null)
        {
            List<String> values = new ArrayList<String>(8);
            values.add(value);
            return Collections.enumeration(values);
        }
        else
        {
            return super.getHeaders(name);
        }
    }

    /**
     * @see javax.servlet.http.HttpServletRequestWrapper#getHeaderNames()
     */
    @Override
    public Enumeration<String> getHeaderNames()
    {
        HttpServletRequest request = (HttpServletRequest) getRequest();

        List<String> list = new ArrayList<String>(16);

        Enumeration<?> e = (Enumeration<?>) request.getHeaderNames();
        while (e.hasMoreElements())
        {
            list.add(e.nextElement().toString());
        }

        for (String key : headerMap.keySet())
        {
            list.add(key);
        }

        return Collections.enumeration(list);
    }
}
