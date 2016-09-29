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
package org.alfresco.po.share;

import java.io.IOException;

import org.alfresco.dataprep.UserService;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
@Component
@PropertySource("classpath:sharepo.properties")
/**
 * Share page object util
 * 
 * @author Michael Suzuki
 */
public class ShareUtil
{
    private  Log logger = LogFactory.getLog(ShareUtil.class);

    private  final String ADMIN_SYSTEMSUMMARY_PAGE = "alfresco/service/enterprise/admin";
    private  final String BULK_IMPORT_PAGE = "alfresco/service/bulkfsimport";
    private  final String BULK_IMPORT_IN_PLACE_PAGE = "alfresco/service/bulkfsimport/inplace";
    private  final String WEB_SCRIPTS_PAGE = "alfresco/service/index";
    private  final String TENANT_ADMIN_CONSOLE_PAGE = "alfresco/s/enterprise/admin/admin-tenantconsole";
    private  final String REPO_ADMIN_CONSOLE_PAGE = "alfresco/s/enterprise/admin/admin-repoconsole";
    private  final String WEBDAV_PAGE = "alfresco/webdav";
    @Autowired FactoryPage factoryPage;
    @Autowired UserService userService;
    @Value("${share.url}")protected String shareUrl;

    /**
     * A simple Enum to request the required Alfresco version.
     *
     * @author Jamal Kaabi-Mofrad
     */
    public  enum RequiredAlfrescoVersion
    {
        CLOUD_ONLY, ENTERPRISE_ONLY;
    }

    /**
     * Use Logout on header bar and mimics action of logout on share.
     */
    public void logout(final WebDriver driver)
    {
        SharePage page = factoryPage.getPage(driver).render();
        page.getNav().logout();
    }

    /**
     * Logs user into share.
     *
     * @param driver {@link WebDriver}
     * @param url Share url
     * @param userInfo username and password
     * @return {@link HtmlPage} page response
     * @throws Exception 
     */
    public HtmlPage loginAs(final WebDriver driver, final String url, final String... userInfo) throws Exception
    {
        PageUtils.checkMandatoryParam("webdriver", driver);
        if(null == url||!url.startsWith("http://"))
        {
            throw new IllegalArgumentException("A valid shareUrl is required and can not be: " + url);
        }
        return loginWithPost(driver, url, userInfo[0], userInfo[1]);
    }

    /**
     * Logs user into share from the current page.
     * 
     * @param driver
     * @param userInfo
     * @return
     */
    public HtmlPage logInAs(final WebDriver driver, final String... userInfo) throws Exception
    {
        return loginWithPost(driver, shareUrl, userInfo[0], userInfo[1]);
    }
    
    public HtmlPage loginWithPost(WebDriver driver, String shareUrl, String userName, String password)
    {
        HttpClient client = new HttpClient();

        //login
        PostMethod post = new PostMethod((new StringBuilder()).append(shareUrl).append("/page/dologin").toString());
        NameValuePair[] formParams = (new NameValuePair[]{
                new org.apache.commons.httpclient.NameValuePair("username", userName),
                new org.apache.commons.httpclient.NameValuePair("password", password),
                new org.apache.commons.httpclient.NameValuePair("success", "/share/page/site-index"),
                new org.apache.commons.httpclient.NameValuePair("failure", "/share/page/type/login?error=true")
        });
        post.setRequestBody(formParams);
        post.addRequestHeader("Accept-Language", "en-us,en;q=0.5");
        try
        {
            client.executeMethod(post);
            HttpState state = client.getState();
            //Navigate to login page to obtain a session cookie.
            driver.navigate().to(shareUrl);
            //add authenticated token to cookie and navigate to user dashboard
            String url = shareUrl + "/page/user/" + userName + "/dashboard";
            driver.manage().addCookie(new Cookie(state.getCookies()[0].getName(),state.getCookies()[0].getValue()));
            driver.navigate().to(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            logger.error("Login error ", e);
        }
        finally
        {
            post.releaseConnection();
        }

        return factoryPage.getPage(driver);

    } 
    
    /**
     * A helper method to check the current running Alfresco version against the
     * required version.
     *
     * @param alfrescoVersion the currently running Alfresco version
     * @param requiredVersion the required version (CLOUD_ONLY |
     *            ENTERPRISE_ONLY)
     * @throws UnsupportedOperationException if the {@code requiredVersion} differs from the {@code alfrescoVersion}
     * @throws IllegalArgumentException if {@code requiredVersion} is invalid
     */
    public  void validateAlfrescoVersion(AlfrescoVersion alfrescoVersion, RequiredAlfrescoVersion requiredVersion) throws UnsupportedOperationException,
            IllegalArgumentException
    {
        boolean isCloud = alfrescoVersion.isCloud();
        switch (requiredVersion)
        {
            case CLOUD_ONLY:
                if (!isCloud)
                {
                    throw new UnsupportedOperationException("This operation is Cloud only, not available for Enterprise.");
                }
                break;
            case ENTERPRISE_ONLY:
                if (isCloud)
                {
                    throw new UnsupportedOperationException("This operation is Enterprise only, not available for Cloud.");
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognised Alfresco version: " + requiredVersion);
        }
    }

    /**
     * @param driver
     * @param userInfo
     * @return
     */
    public HtmlPage navigateToSystemSummary(final WebDriver driver, String url, final String... userInfo)
    {
        String protocolVar = PageUtils.getProtocol(url);
        String consoleUrlVar = PageUtils.getAddress(url);
        String systemUrl = String.format("%s%s:%s@%s/" + ADMIN_SYSTEMSUMMARY_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
        try {
            driver.navigate().to(systemUrl);
        } catch (Exception e) {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e + ". Param systemUrl was " + systemUrl);
            }
        }
        return factoryPage.getPage(driver).render();
    }

    /**
     * Methods for navigation bulk import page
     *
     * @param driver
     * @param inPlace
     * @param userInfo
     * @return
     */
    public HtmlPage navigateToBulkImport(final WebDriver driver, boolean inPlace, final String... userInfo)
    {
        String currentUrl = driver.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentUrl);
        String consoleUrlVar = PageUtils.getAddress(currentUrl);
        if (inPlace)
        {
            currentUrl = String.format("%s%s:%s@%s/" + BULK_IMPORT_IN_PLACE_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
            logger.info("Property 'currentUrl' is: " + currentUrl);
        }
        else
        {
            currentUrl = String.format("%s%s:%s@%s/" + BULK_IMPORT_PAGE, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
            logger.info("Property 'currentUrl' is: " + currentUrl);
        }

        try
        {
            logger.info("Navigate to 'currentUrl': " + currentUrl);
            driver.navigate().to(currentUrl);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e + ". Param systemUrl was " + currentUrl);
            }
        }
        return factoryPage.getPage(driver).render();
    }

    /**
     * Helper method to extract alfresco webscript url and direct webdriver to location. 
     * @param driver
     * @param userInfo
     * @return
     * @throws Exception
     */
    public HtmlPage navigateToWebScriptsHome(final WebDriver driver,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(driver, WEB_SCRIPTS_PAGE, userInfo);
    }
    /**
     * Helper method to extract alfresco tenant admin console url and direct webdriver to location. 
     * @param driver
     * @param userInfo
     * @return
     * @throws Exception
     */
    public HtmlPage navigateToTenantAdminConsole(final WebDriver driver,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(driver, TENANT_ADMIN_CONSOLE_PAGE, userInfo);
    }
    /**
     * Helper method to extract alfresco repository admin console url and direct webdriver to location. 
     * @param driver
     * @param userInfo
     * @return
     * @throws Exception
     */
    public HtmlPage navigateToRepositoryAdminConsole(final WebDriver driver,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(driver, REPO_ADMIN_CONSOLE_PAGE, userInfo);
    }

    /**
     * Helper method to extract alfresco repository admin console url and direct webdriver to location.
     * @param driver
     * @param userInfo
     * @return
     * @throws Exception
     */
    public HtmlPage navigateToWebDav(final WebDriver driver,final String... userInfo) throws Exception
    {
        return navigateToAlfresco(driver, WEBDAV_PAGE, userInfo);
    }

    /**
     * Base helper method that extracts the url to required alfresco admin location.
     * Once extracted it formats it with the username and password to allow access to the page.
     * @param driver
     * @param path
     * @param userInfo
     * @return
     * @throws Exception
     */
    public HtmlPage navigateToAlfresco(final WebDriver driver, final String path,final String... userInfo) throws Exception
    {
        PageUtils.checkMandatoryParam("WebDriver", driver);
        PageUtils.checkMandatoryParam("Path", path);
        PageUtils.checkMandatoryParam("Username and password", userInfo);
        String currentUrl = driver.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentUrl);
        String consoleUrlVar = PageUtils.getAddress(currentUrl);
        currentUrl = String.format("%s%s:%s@%s/" + path, protocolVar, userInfo[0], userInfo[1], consoleUrlVar);
        driver.navigate().to(currentUrl);
        return factoryPage.getPage(driver).render();
    }
}
