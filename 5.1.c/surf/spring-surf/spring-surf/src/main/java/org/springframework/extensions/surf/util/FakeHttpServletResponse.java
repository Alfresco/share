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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Implementation of a Fake HttpServletResponse object which can be used to trap output from
 * dispatched objects into a buffer and then deal with results at a later time. Useful for page
 * caching or nested components where the output stream must be committed to real response at a
 * undetermined point in the future.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class FakeHttpServletResponse extends HttpServletResponseWrapper
{
    /** The Constant CHARSET_PREFIX. */
    private static final String CHARSET_PREFIX = "charset=";

    /** The character encoding. */
    private String characterEncoding = "UTF-8";

    /** The content. */
    private ByteArrayOutputStream content;

    /** The output stream. */
    private DelegatingServletOutputStream outputStream;

    /** The writer. */
    private PrintWriter writer;

    /** The content length. */
    private int contentLength = 0;

    /** The content type. */
    private String contentType;

    /** The buffer size. */
    private int bufferSize = 1024;

    /** The committed. */
    private boolean committed;

    /** The locale. */
    private Locale locale = Locale.getDefault();

    /** The cookies. */
    private List<Cookie> cookies = null;

    /** The headers. */
    private Map<String, Object> headers = null;

    /** The status. */
    private int status = HttpServletResponse.SC_OK;

    /** The error message. */
    private String errorMessage;

    /** The redirected url. */
    private String redirectedUrl;

    /** The forwarded url. */
    private String forwardedUrl;

    /** The included url. */
    private String includedUrl;
    
    /** True if buffers initialised, false otherwise */
    private boolean initialised = false;
    
    
    /**
     * Construction
     * 
     * @param wrapped   HttpServletResponse to be wrapped
     */
    public FakeHttpServletResponse(HttpServletResponse wrapped)
    {
        super(wrapped);
    }

    private void init()
    {
        if (!this.initialised)
        {
            this.content = new ByteArrayOutputStream(1024);
            this.outputStream = new DelegatingServletOutputStream(this.content);
            this.initialised = true;
        }
    }
    
    /**
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
     */
    @Override
    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding()
    {
        return this.characterEncoding;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream()
    {
        init();
        return this.outputStream;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getWriter()
     */
    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException
    {
        if (this.writer == null)
        {
            // create a proxy writer that only instantiates buffers on the first write
            // as many instances of this class never actually write any result
            Writer targetWriter = new Writer()
            {
                private Writer proxy = null;
                
                private Writer getWriter()
                    throws UnsupportedEncodingException
                {
                    if (proxy == null)
                    {
                        // ensure outer class buffers have been allocated
                        init();
                        
                        // create a writer wrapping the underlying content output stream
                        proxy = characterEncoding != null ? new OutputStreamWriter(
                                    content, characterEncoding) : new OutputStreamWriter(content);
                    }
                    return proxy;
                }
                
                @Override
                public void write(char[] cbuf, int off, int len) throws IOException
                {
                    getWriter().write(cbuf, off, len);
                }

                @Override
                public void flush() throws IOException
                {
                    if (proxy != null)
                    {
                        getWriter().flush();
                    }
                }

                @Override
                public void close() throws IOException
                {
                    if (proxy != null)
                    {
                        getWriter().close();
                    }
                }
            };
            
            this.writer = new PrintWriter(targetWriter, true);
        }
        
        return this.writer;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    @Override
    public void flushBuffer()
    {
        if (this.writer != null)
        {
            this.writer.flush();
        }

        if (this.outputStream != null)
        {
            try
            {
                this.outputStream.flush();
            }
            catch (IOException ex)
            {
                throw new IllegalStateException("Could not flush OutputStream: " + ex.getMessage());
            }
        }

        this.committed = true;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    @Override
    public void sendError(int newStatus, String newErrorMessage) throws IOException
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }

        this.status = newStatus;
        this.errorMessage = newErrorMessage;
        this.committed = true;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    @Override
    public void sendError(int newStatus) throws IOException
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }

        this.status = newStatus;
        this.committed = true;
    }

    /**
     * Gets the error message.
     * 
     * @return the error message
     */
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
     */
    @Override
    public void sendRedirect(String url) throws IOException
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }

        this.redirectedUrl = url;
        this.committed = true;
    }

    /**
     * Gets the redirected url.
     * 
     * @return the redirected url
     */
    public String getRedirectedUrl()
    {
        return this.redirectedUrl;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    @Override
    public void setStatus(int status)
    {
        this.status = status;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
     */
    @Override
    public void setStatus(int status, String errorMessage)
    {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    public int getStatus()
    {
        return this.status;
    }

    /**
     * Gets the content as byte array.
     * 
     * @return the content as byte array
     */
    public byte[] getContentAsByteArray()
    {
        flushBuffer();
        return this.content != null ? this.content.toByteArray() : new byte[0];
    }

    /**
     * Gets the content as string.
     * 
     * @return the content as string
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    public String getContentAsString() throws UnsupportedEncodingException
    {
        String contentToReturn = "";
        flushBuffer();
        if (this.content != null)
        {
            contentToReturn = this.characterEncoding != null ? this.content.toString(this.characterEncoding) : this.content.toString();
        }
        else
        {
            // No action required. Content already initialised to empty string.
        }
        return contentToReturn;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    @Override
    public void setContentLength(int contentLength)
    {
        this.contentLength = contentLength;
    }

    /**
     * Gets the content length.
     * 
     * @return the content length
     */
    public int getContentLength()
    {
        return this.contentLength;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType)
    {
        this.contentType = contentType;

        if (contentType != null)
        {
            int charsetIndex = contentType.toLowerCase().indexOf(FakeHttpServletResponse.CHARSET_PREFIX);
            if (charsetIndex != -1)
            {
                String encoding = contentType.substring(charsetIndex + FakeHttpServletResponse.CHARSET_PREFIX.length());
                setCharacterEncoding(encoding);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getContentType()
     */
    @Override
    public String getContentType()
    {
        return this.contentType;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    @Override
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    @Override
    public int getBufferSize()
    {
        return this.bufferSize;
    }

    /**
     * Sets the committed.
     * 
     * @param committed
     *            the new committed
     */
    public void setCommitted(boolean committed)
    {
        this.committed = committed;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    @Override
    public boolean isCommitted()
    {
        return this.committed;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    @Override
    public void resetBuffer()
    {
        if (this.committed)
        {
            throw new IllegalStateException("Cannot reset buffer - response is already committed");
        }
        if (this.content != null)
        {
            this.content.reset();
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#reset()
     */
    @Override
    public void reset()
    {
        resetBuffer();
        this.characterEncoding = null;
        this.contentLength = 0;
        this.contentType = null;
        this.locale = null;
        this.cookies = null;
        this.headers = null;
        this.status = HttpServletResponse.SC_OK;
        this.errorMessage = null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletResponse#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return this.locale;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
     */
    @Override
    public void addCookie(Cookie cookie)
    {
        if (this.cookies == null)
        {
            this.cookies = new ArrayList<Cookie>(4);
        }
        this.cookies.add(cookie);
    }

    /**
     * Gets the cookies.
     * 
     * @return the cookies
     */
    public Cookie[] getCookies()
    {
        return (Cookie[]) (this.cookies == null ? new Cookie[0] : this.cookies.toArray(new Cookie[this.cookies.size()]));
    }

    /**
     * Gets the cookie.
     * 
     * @param name
     *            the name
     * @return the cookie
     */
    public Cookie getCookie(String name)
    {
        if (this.cookies == null)
        {
            this.cookies = new ArrayList<Cookie>(4);
        }
        for (Iterator it = this.cookies.iterator(); it.hasNext();)
        {
            Cookie cookie = (Cookie) it.next();
            if (name.equals(cookie.getName()))
            {
                return cookie;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
     */
    @Override
    public String encodeUrl(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    @Override
    public String encodeURL(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
     */
    @Override
    public String encodeRedirectUrl(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    @Override
    public String encodeRedirectURL(String url)
    {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(String name, String value)
    {
        doAddHeader(name, value);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(String name, String value)
    {
        if (this.headers == null)
        {
            this.headers = new HashMap<String, Object>(8);
        }
        this.headers.put(name, value);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    @Override
    public void addDateHeader(String name, long value)
    {
        doAddHeader(name, new Long(value));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    @Override
    public void setDateHeader(String name, long value)
    {
        if (this.headers == null)
        {
            this.headers = new HashMap<String, Object>(8);
        }
        this.headers.put(name, new Long(value));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    @Override
    public void addIntHeader(String name, int value)
    {
        doAddHeader(name, new Integer(value));
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    @Override
    public void setIntHeader(String name, int value)
    {
        if (this.headers == null)
        {
            this.headers = new HashMap<String, Object>(8);
        }
        this.headers.put(name, new Integer(value));
    }

    /**
     * Do add header.
     * 
     * @param name
     *            the name
     * @param value
     *            the value
     */
    private void doAddHeader(String name, Object value)
    {
        if (this.headers == null)
        {
            this.headers = new HashMap<String, Object>(8);
        }
        Object oldValue = this.headers.get(name);

        if (oldValue instanceof List)
        {
            List list = (List) oldValue;
            list.add(value);
        }
        else if (oldValue != null)
        {
            List list = new LinkedList();
            list.add(oldValue);
            list.add(value);
            this.headers.put(name, list);
        }
        else
        {
            this.headers.put(name, value);
        }
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    @Override
    public boolean containsHeader(String name)
    {
        return this.headers != null ? this.headers.containsKey(name) : false;
    }

    /**
     * Gets the header names.
     * 
     * @return the header names
     */
    public Set getHeaderNames()
    {
        if (this.headers == null)
        {
            this.headers = new HashMap<String, Object>(8);
        }
        return this.headers.keySet();
    }

    /**
     * Gets the header.
     * 
     * @param name
     *            the name
     * @return the header
     */
    public Object getHeader(String name)
    {
        return this.headers != null ? this.headers.get(name) : null;
    }

    /**
     * Gets the headers.
     * 
     * @param name
     *            the name
     * @return the headers
     */
    public List getHeaders(String name)
    {
        if (this.headers == null)
        {
            this.headers = new HashMap<String, Object>(8);
        }
        Object value = this.headers.get(name);

        if (value instanceof List)
        {
            return (List) value;
        }
        else if (value != null)
        {
            return Collections.singletonList(value);
        }
        else
        {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Sets the forwarded url.
     * 
     * @param forwardedUrl
     *            the new forwarded url
     */
    public void setForwardedUrl(String forwardedUrl)
    {
        this.forwardedUrl = forwardedUrl;
    }

    /**
     * Gets the forwarded url.
     * 
     * @return the forwarded url
     */
    public String getForwardedUrl()
    {
        return this.forwardedUrl;
    }

    /**
     * Sets the included url.
     * 
     * @param includedUrl
     *            the new included url
     */
    public void setIncludedUrl(String includedUrl)
    {
        this.includedUrl = includedUrl;
    }

    /**
     * Gets the included url.
     * 
     * @return the included url
     */
    public String getIncludedUrl()
    {
        return this.includedUrl;
    }
    
    
    /**
     * The Class DelegatingServletOutputStream.
     */
    public class DelegatingServletOutputStream extends ServletOutputStream
    {
        /**
         * Instantiates a new delegating servlet output stream.
         * 
         * @param targetStream
         *            the target stream
         */
        public DelegatingServletOutputStream(OutputStream targetStream)
        {
            super();
            this.proxy = targetStream;
        }

        /**
         * Gets the target stream.
         * 
         * @return the target stream
         */
        public OutputStream getTargetStream()
        {
            return this.proxy;
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(int b) throws IOException
        {
            this.proxy.write(b);
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#flush()
         */
        @Override
        public void flush() throws IOException
        {
            super.flush();
            this.proxy.flush();
        }

        /*
         * (non-Javadoc)
         * @see java.io.OutputStream#close()
         */
        @Override
        public void close() throws IOException
        {
            super.close();
            this.proxy.close();
        }

        /** The proxy. */
        private final OutputStream proxy;
    }

}
