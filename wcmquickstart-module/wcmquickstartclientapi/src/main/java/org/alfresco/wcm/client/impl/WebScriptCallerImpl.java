/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.wcm.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WebScriptCallerImpl implements WebScriptCaller
{
    static Log log = LogFactory.getLog(WebScriptCallerImpl.class);

    private static ThreadLocal<byte[]> localBuffer = new ThreadLocal<byte[]>()
    {
        @Override
        protected byte[] initialValue()
        {
            return new byte[1024];
        }
    };

    private String baseUrl;
    HttpClient httpClient;
    private AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
    
    private String username = null;
    private String password = null;;

    public WebScriptCallerImpl()
    {
        httpClient = new HttpClient();
        httpClient.setHttpConnectionManager(new MultiThreadedHttpConnectionManager());
    }

    /**
     * Method that allows the default HttpClient instance to be replaced with one that is configured differently
     * @param httpClient
     */
    public void setHttpClient(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public void setBaseUrl(String serviceLocation) throws URISyntaxException
    {
        this.baseUrl = serviceLocation;
        if (!baseUrl.endsWith("/"))
        {
            baseUrl += "/";
        }
    }

    public void setAuthScope(AuthScope authScope)
    {
        this.authScope = authScope;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
     }

    public void init()
    {
    }

    public String getTicket(String user, String password)
    {
        TicketResponseHandler responseHandler = new TicketResponseHandler();
        List<WebscriptParam> paramList = new ArrayList<WebscriptParam>();
        paramList.add(new WebscriptParam("u", user));
        paramList.add(new WebscriptParam("pw", password));
        get("login", responseHandler, paramList, true);
        Credentials credentials = new UsernamePasswordCredentials(user, password); 
        if (responseHandler.ticket != null)
        {
            credentials = new UsernamePasswordCredentials("", responseHandler.ticket);
        }
        httpClient.getState().setCredentials(authScope, credentials);
        httpClient.getParams().setAuthenticationPreemptive(true);
        return responseHandler.ticket;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.wcm.client.impl.WebScriptCaller#getJsonObject(java.lang.String, java.util.List)
     */
    public JSONObject getJsonObject(String servicePath, List<WebscriptParam> params)
    {
        GetMethod getMethod = getGETMethod(servicePath, params);
        JsonResponseHandler handler = new JsonResponseHandler();
        executeRequest(handler, getMethod);
        return handler.jsonObject;
    }
    
    public void get(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params)
    {
        get(servicePath, handler, params, false);
    }
    
    private void get(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params, boolean ignoreUnauthorized)
    {
        GetMethod getMethod = getGETMethod(servicePath, params);
        executeRequest(handler, getMethod, ignoreUnauthorized);
    }
    
    public void post(String servicePath, WebscriptResponseHandler handler, List<WebscriptParam> params)
    {
        PostMethod postMethod = getPOSTMethod(servicePath, params);
        executeRequest(handler, postMethod);
    }

    private void executeRequest(WebscriptResponseHandler handler, HttpMethod httpMethod)
    {
        executeRequest(handler, httpMethod, false);
    }

    private void executeRequest(WebscriptResponseHandler handler, HttpMethod httpMethod, boolean ignoreUnauthorized)
    {
        long startTime = 0L;
        if (log.isDebugEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        try
        {
            httpClient.executeMethod(httpMethod);
            
            if ((httpMethod.getStatusCode() == 401 || httpMethod.getStatusCode() == 403) && !ignoreUnauthorized)
            {
                discardResponse(httpMethod);
                
                this.getTicket(username, password);
                httpClient.executeMethod(httpMethod);
            }
            
            if (httpMethod.getStatusCode() == 200)
            {
                handler.handleResponse(httpMethod.getResponseBodyAsStream());
            }
            else
            {
                // Must read the response, even though we don't use it
                discardResponse(httpMethod);
            }
        }
        catch (RuntimeException ex)
        {
            log.error("Rethrowing runtime exception.", ex);
            throw ex;
        }
        catch (Exception ex)
        {
            log.error("Failed to make request to Alfresco web script", ex);
        }
        finally
        {
            if (log.isDebugEnabled())
            {
                log.debug(httpMethod.getName() + " request to " + httpMethod.getPath() + "?" + 
                        httpMethod.getQueryString() + " completed in " + 
                        (System.currentTimeMillis() - startTime) + "ms");
            }
            httpMethod.releaseConnection();
        }
    }

    void discardResponse(HttpMethod httpMethod) throws IOException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Received non-OK response when invoking method on path " + httpMethod.getPath() + 
                    ". Response was:\n" + httpMethod.getResponseBodyAsString());
        }
        else
        {
            byte[] buf = localBuffer.get();
            InputStream responseStream = httpMethod.getResponseBodyAsStream();
            while (responseStream.read(buf) != -1);
        }
    }

    GetMethod getGETMethod(String servicePath, List<WebscriptParam> params)
    {
        GetMethod getMethod = new GetMethod(this.baseUrl + servicePath);

        if (params != null)
        {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            for (WebscriptParam param : params)
            {
                args.add(new NameValuePair(param.getName(), param.getValue()));
            }
            getMethod.setQueryString(args.toArray(new NameValuePair[args.size()]));
        }
        return getMethod;
    }

    PostMethod getPOSTMethod(String servicePath, List<WebscriptParam> params)
    {
        PostMethod postMethod = new PostMethod(this.baseUrl + servicePath);

        if (params != null)
        {
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            for (WebscriptParam param : params)
            {
                args.add(new NameValuePair(param.getName(), param.getValue()));
            }
            postMethod.addParameters(args.toArray(new NameValuePair[args.size()]));
        }
        return postMethod;
    }

    @Override
    public JSONObject getJsonObject(String servicePath, WebscriptParam... params)
    {
        return getJsonObject(servicePath, Arrays.asList(params));
    }

    @Override
    public void get(String servicePath, WebscriptResponseHandler handler, WebscriptParam... params)
    {
        get(servicePath, handler, Arrays.asList(params));
    }

    private static class JsonResponseHandler implements WebscriptResponseHandler
    {
        public JSONObject jsonObject;
        
        @Override
        public void handleResponse(InputStream in)
        {
            try
            {
                jsonObject = new JSONObject(new JSONTokener(
                        new InputStreamReader(in, "UTF-8")));
            }
            catch (JSONException ex)
            {
                log.error("Failed to parse response from Alfresco", ex);
            }
            catch (UnsupportedEncodingException e)
            {
                //UTF-8 is always supported
            }
        }
        
    }

    private static class TicketResponseHandler extends DefaultHandler implements WebscriptResponseHandler
    {
        private String ticket = null;
        private StringBuilder ticketChars;

        @Override
        public void handleResponse(InputStream in)
        {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser parser;
            try
            {
                parser = saxParserFactory.newSAXParser();
                parser.parse(in, this);
            }
            catch (Exception e)
            {
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (ticketChars != null)
            {
                ticketChars.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if ("ticket".equals(qName) && ticketChars != null)
            {
                ticket = ticketChars.toString();
                ticketChars = null;
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if ("ticket".equals(qName))
            {
                ticketChars = new StringBuilder();
            }
        }
    }
}
