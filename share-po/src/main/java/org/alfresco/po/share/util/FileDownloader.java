/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Set;

import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.openqa.selenium.Cookie;

/**
 * Downloads file from given url using {@link WebDrone}'s session details
 * extracted from a cookie.
 * 
 * @author Michael Suzuki
 * @since 1.2
 */
public class FileDownloader
{

    private static Log logger = LogFactory.getLog(FileDownloader.class);
    private WebDrone drone;
    private String localDownloadPath = System.getProperty("java.io.tmpdir");

    public FileDownloader(WebDrone drone)
    {
        this.drone = drone;
    }

    public String getLocalDownloadPath()
    {
        return localDownloadPath;
    }

    public void setLocalDownloadPath(String localDownloadPath)
    {
        this.localDownloadPath = localDownloadPath;
    }

    /**
     * Loads the cookies from WebDrone to mimic the browser cookie state
     * 
     * @param seleniumCookieSet
     * @return {@link BasicCookieStore} current state
     */
    private BasicCookieStore getCookies()
    {

        BasicCookieStore mimicWebDriverCookie = new BasicCookieStore();
        Set<Cookie> cookies = ((WebDroneImpl) drone).getCookies();

        for (Cookie seleniumCookie : cookies)
        {
            BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
            duplicateCookie.setDomain(seleniumCookie.getDomain());
            duplicateCookie.setSecure(seleniumCookie.isSecure());
            duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
            duplicateCookie.setPath(seleniumCookie.getPath());
            mimicWebDriverCookie.addCookie(duplicateCookie);
        }
        return mimicWebDriverCookie;
    }

    /**
     * Main method that performs the download operation
     * using HttpClient with WebDrone's cookies.
     * 
     * @param path url path to file
     * @throws Exception if error
     */
    public void download(final String path, File file) throws Exception
    {
        URL fileToDownload;
        // Cookie setup
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(redirectStrategy).build();
        HttpEntity entity = null;
        try
        {
            String myUrl = URLDecoder.decode(path, "UTF-8");
            String fileUrl = myUrl.replace("\\/", "/").replace(" ", "%20");

            fileToDownload = new URL(fileUrl);
            BasicHttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(HttpClientContext.COOKIE_STORE, getCookies());
            // Prepare an http get call
            HttpGet httpget = new HttpGet(fileToDownload.toURI());
            if (logger.isDebugEnabled())
            {
                logger.debug("Sending GET request for: " + httpget.getURI());
            }
            // Get the response from http get call
            HttpResponse response = client.execute(httpget, localContext);
            int httpStatusCode = response.getStatusLine().getStatusCode();
            if (logger.isDebugEnabled())
            {
                logger.debug("HTTP GET request status: " + httpStatusCode);
            }
            // Extract content and stream to file
            entity = response.getEntity();
            InputStream input = entity.getContent();
            if (input != null)
            {
                FileUtils.copyInputStreamToFile(input, file);
            }
        }
        catch (MalformedURLException murle)
        {
            throw new Exception("Unable to reach document", murle);
        }
        catch (IllegalStateException ise)
        {
            throw new Exception("State problem", ise);
        }
        catch (IOException ioe)
        {
            throw new Exception("Unable to read write file", ioe);
        }
        catch (URISyntaxException urise)
        {
            throw new Exception("A uri syntax problem", urise);
        }
    }

}