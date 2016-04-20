/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
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
import org.openqa.selenium.WebDriver;

/**
 * Downloads file from given url using {@link WebDriver}'s session details
 * extracted from a cookie.
 * 
 * @author Michael Suzuki
 * @since 1.2
 */
public class FileDownloader
{

    private static Log logger = LogFactory.getLog(FileDownloader.class);
    private WebDriver driver;
    private String localDownloadPath = System.getProperty("java.io.tmpdir");

    public FileDownloader(WebDriver driver)
    {
        this.driver = driver;
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
     * Loads the cookies from WebDriver to mimic the browser cookie state
     * @return {@link BasicCookieStore} current state
     */
    private BasicCookieStore getCookies()
    {

        BasicCookieStore mimicWebDriverCookie = new BasicCookieStore();
        Set<Cookie> cookies = driver.manage().getCookies();

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
     * using HttpClient with WebDriver's cookies.
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
