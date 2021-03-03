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
package org.alfresco.po;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;

import org.alfresco.dataprep.ContentService;
import org.alfresco.dataprep.DataListsService;
import org.alfresco.dataprep.SitePagesService;
import org.alfresco.dataprep.UserService;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.po.share.cmm.steps.CmmActions;
import org.alfresco.po.share.dashlet.FactoryShareDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.steps.AdminActions;
import org.alfresco.po.share.steps.SiteActions;
import org.alfresco.po.share.steps.UserProfileActions;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.selenium.FetchUtil;
import org.alfresco.test.AlfrescoTests;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration("classpath*:share-po-test-context.xml")
@PropertySources({
    @PropertySource("classpath:test.properties"),
    @PropertySource("classpath:module.properties"),
    @PropertySource("classpath:cmm.properties")
})
/**
 * Abstract test holds all common methods and information required
 * to run share page object tests.
 *
 * @author Michael Suzuki
 */
public abstract class AbstractTest extends AbstractTestNGSpringContextTests implements AlfrescoTests
{
    private static Log logger = LogFactory.getLog(AbstractTest.class);
    @Autowired private ApplicationContext ctx;
    @Value("${share.url}")protected String shareUrl;
    @Value("${share.license}")protected String licenseShare;
    @Value("${download.directory}")protected String downloadDirectory;
    @Value("${test.password}") protected String password;
    @Value("${test.username}") protected String username;
    @Value("${test.network}") protected String testNetwork;
    @Value("${blog.url}") protected String blogUrl;
    @Value("${blog.username}") protected String blogUsername;
    @Value("${blog.password}") protected String blogPassword;
    @Value("${render.error.popup.time}") protected long popupRendertime;
    @Value("${share.version}") protected String alfrescoVersion;
    @Value("${render.page.wait.time}") protected long maxPageWaitTime;
    @Value("${alfresco.server}") protected String alfrescoSever;
    @Value("${alfresco.port}") protected String alfrescoPort;
    @Autowired protected UserProfile anotherUser;
    @Autowired protected FactoryPage factoryPage;
    @Autowired protected FactoryShareDashlet dashletFactory;
    @Autowired protected ShareUtil shareUtil;
    @Autowired protected SiteUtil siteUtil;
    @Autowired protected SitePagesService sitePagesService;
    @Autowired protected DataListsService dataListPagesService;
    @Autowired protected ContentService contentService;
    @Autowired protected UserService userService;
    @Autowired protected CmmActions cmmActions;
    @Autowired protected SiteActions siteActions;
    @Autowired protected AdminActions adminActions;
    @Autowired protected UserProfileActions userActions;
    
    public static Integer retrySearchCount = 3;
    protected long solrWaitTime = 20000;
    protected WebDriver driver;
    protected static final String UNAME_PASSWORD = "password";

    public static long count = 0;

    @BeforeClass(alwaysRun = true)
    public void getWebDriver() throws Exception
    {
        driver = (WebDriver) ctx.getBean("webDriver");
        driver.manage().window().maximize();

        String className = getClass().getSimpleName();
        System.out.println("====== STARTING SUITE : " + className + " =====");

        count = 0;
    }

    @AfterClass(alwaysRun = true)
    public void closeWebDriver()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Closing web driver");
        }
        // Close the browser
        if (driver != null)
        {
            driver.quit();
            driver = null;
        }

        String className = getClass().getSimpleName();
        System.out.println("====== END SUITE : " + className + " =====\n");
    }

    @BeforeMethod(alwaysRun = true)
    protected void startSession(Method method) throws Exception
    {
        count++;
        String testName = method.getName();
        System.out.print(String.format("\t %d. %s", count, testName));
    }

    @AfterMethod(alwaysRun = true)
    protected void endTest(ITestResult result) throws Exception
    {
        System.out.println(String.format("\t : %s", result.isSuccess() ? "PASSED" : "FAILED"));
    }

    /**
     * Helper to log admin user into dashboard.
     *
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public DashBoardPage loginAs(final String... userInfo) throws Exception
    {
        return shareUtil.loginAs(driver, shareUrl, userInfo).render();
    }

    /**
     * Helper to log admin user into dashboard.
     *
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public DashBoardPage loginAs(WebDriver driver, String shareUrl, final String... userInfo) throws Exception
    {
        return shareUtil.loginAs(driver, shareUrl, userInfo).render();
    }

    public void saveOsScreenShot(String methodName) throws IOException, AWTException
    {
        Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenShot, "png", new File("target/webdriver-" + methodName+ "_OS" +".png"));
    }
    /**
     * Grabs a screen shot of what the {@link WebDriver} is currently viewing. This is only possible on WebDriver that are UI based browser.
     * 
     * @return {@link File} screen image of the page
     */
    public final File getScreenShot()
    {
        if(driver instanceof TakesScreenshot) 
        {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        }
        WebDriver augmentedDriver = new Augmenter().augment(driver);
        return ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
    }
    public void saveScreenShot(String methodName) throws IOException
    {
        if(StringUtils.isEmpty(methodName))
        {
            throw new IllegalArgumentException("Method Name can't be empty or null.");
        }
        File file = getScreenShot();
        File tmp = new File("target/webdriver-" + methodName + ".png");
        FileUtils.copyFile(file, tmp);
    }

    public void savePageSource(String methodName) throws IOException
    {
        FetchUtil.save(driver, methodName + ".html");
    }

    /**
     * User Log out using logout URL Assumes User is logged in.
     *
     * @param driver WebDriver Instance
     */
    public void logout(WebDriver driver)
    {
        if(driver != null)
        {
            try
            {
                if (driver.getCurrentUrl().contains(shareUrl.trim()))
                {
                    shareUtil.logout(driver);
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("Logout");
                    }
                }
            }
            catch (Exception e)
            {
                // Already logged out.
            }
        }
    }

    /**
     * Function to create user on Enterprise using API
     *
     * @param uname - This should always be unique. So the user of this method needs to verify it is unique.
     *                eg. - "testUser" + System.currentTimeMillis();
     * @return
     * @throws Exception
     */
    public void createEnterpriseUser(String uname) throws Exception
    {
        userService.create(username, password, uname, "password", getUserEmail(uname), uname, uname);
    }
    
    public String getUserEmail(String username)
    {
    	if (username.contains("@"))
    	{
    		// Use as it
    		return username;
    	}
    	else if (testNetwork == null || testNetwork.isEmpty() || testNetwork.contains("$"))
    	{
    		testNetwork = "test.com";
    	}
    	return username + "@" + testNetwork;
    			 
    }


    /**
     * Utility method to open site document library from search
     * @param driver
     * @param siteName
     * @return
     */
    protected DocumentLibraryPage openSiteDocumentLibraryFromSearch(WebDriver driver, String siteName)
    {
        SharePage sharePage = factoryPage.getPage(driver).render();
        SiteFinderPage siteFinderPage = sharePage.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(siteName).render();
        siteFinderPage = siteUtil.siteSearchRetry(driver, siteFinderPage, siteName);
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(siteName).render();
        DocumentLibraryPage documentLibPage = siteDashboardPage.getSiteNav().selectDocumentLibrary().render();
        return documentLibPage;
    }

    /**
     * Method to Cancel a WorkFlow or Delete a WorkFlow (To use in TearDown method)
     * @param workFlow
     */
    protected void cancelWorkFlow(String workFlow)
    {
        SharePage sharePage = factoryPage.getPage(driver).render();
        MyWorkFlowsPage myWorkFlowsPage = sharePage.getNav().selectWorkFlowsIHaveStarted().render();
        myWorkFlowsPage.render();
        if(myWorkFlowsPage.isWorkFlowPresent(workFlow))
        {
            myWorkFlowsPage.cancelWorkFlow(workFlow);
        }
        myWorkFlowsPage = myWorkFlowsPage.selectCompletedWorkFlows().render();
        if(myWorkFlowsPage.isWorkFlowPresent(workFlow))
        {
            myWorkFlowsPage.deleteWorkFlow(workFlow);
        }
    }

    /**
     * Method to upload a file from given path. Method assumes that user is already in Document Library Page
     * @param driver
     * @param filePath
     * @return
     */
    public DocumentLibraryPage uploadContent(WebDriver driver, String filePath)
    {
        DocumentLibraryPage documentLibraryPage = factoryPage.getPage(driver).render();
        UploadFilePage uploadForm = documentLibraryPage.getNavigation().selectFileUpload().render();
        return uploadForm.uploadFile(filePath).render();
    }

    protected HtmlPage resolvePage(WebDriver driver)
    {
        return factoryPage.getPage(driver);
    }
    
    
    /**
     * Executes delete request
     * 
     * @param url
     * @param username
     * @param password
     * @return
     * @throws HttpException
     * @throws IOException
     */
    protected int executeDeleteRequest(String url, String username, String password) throws HttpException, IOException
    {
        HttpClient client = new HttpClient();
        Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
        client.getState().setCredentials(AuthScope.ANY, defaultcreds);
        DeleteMethod method = new DeleteMethod(url);
        return client.executeMethod(method); 
    }
    
}
