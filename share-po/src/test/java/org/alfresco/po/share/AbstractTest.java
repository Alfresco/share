/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.po.share;

import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.SyncInfoPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.ShareTestProperty;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.test.AlfrescoTests;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.*;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
/**
 * Abstract test holds all common methods and functionality to test against
 * Benchmark Grid tests.
 *
 * @author Michael Suzuki
 */
public abstract class AbstractTest implements AlfrescoTests
{
    private static Log logger = LogFactory.getLog(AbstractTest.class);
    private static ApplicationContext ctx;
    protected static String shareUrl;
    protected static String pentahoUserConsoleUrl;
    protected static String hybridShareUrl;
    protected static String password;
    protected static String username;
    protected static String googleusername;
    protected static String googlepassword;
    protected static String cloudUserName;
    protected static String cloudUserPassword;
    protected static UserProfile anotherUser;
    protected static AlfrescoVersion alfrescoVersion;
    public static Integer retrySearchCount = 3;
    public static String downloadDirectory;
    public boolean hybridEnabled;
    protected WebDrone drone;
    protected WebDrone hybridDrone;
    protected WebDrone customDrone;
    protected WebDrone customHybridDrone;
    protected String testName;
    protected String hybridUserName;
    protected String hybridUserPassword;
    protected static final String UNAME_PASSWORD = "password";
    protected long popupRendertime;
    public static long maxWaitTime_CloudSync = 50000;
    public static String licenseShare;
    protected static String blogUrl;
    protected static String blogUsername;
    protected static String blogPassword;

    public WebDrone getDrone()
    {
        return drone;
    }

    @BeforeSuite(alwaysRun = true)
    @Parameters({"contextFileName"})
    public void setupContext(@Optional("share-po-test-context.xml")String contextFileName) throws Exception
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("Starting test context" + contextFileName );
        }

        List<String> contextXMLList = new ArrayList<String>();
        contextXMLList.add(contextFileName);
        contextXMLList.add("webdrone-context.xml");
        ctx = new ClassPathXmlApplicationContext(contextXMLList.toArray(new String[contextXMLList.size()]));

        ShareTestProperty t = (ShareTestProperty) ctx.getBean("shareTestProperties");
        shareUrl = t.getShareUrl();
        pentahoUserConsoleUrl = t.getPentahoUserConsoleUrl();
        username = t.getUsername();
        password = t.getPassword();
        googleusername = t.getGoogleUserName();
        googlepassword = t.getGooglePassword();
        alfrescoVersion = t.getAlfrescoVersion();
        downloadDirectory = t.getDownloadDirectory();
        hybridEnabled = t.isHybridEnabled();
        cloudUserName = t.getCloudUserName();
        cloudUserPassword = t.getCloudUserPassword();
        popupRendertime = t.getPopupRendertime();
        licenseShare = t.getLicenseShare();
        blogUrl = t.getBlogUrl();
        blogUsername = t.getBlogUsername();
        blogPassword = t.getBlogPassword();

        if(hybridEnabled)
        {
            ShareTestProperty testProperty = (ShareTestProperty) ctx.getBean("shareHybridTestProperties");
            hybridShareUrl = testProperty.getShareUrl();
            hybridUserName = testProperty.getUsername();
            hybridUserPassword = testProperty.getPassword();
        }

        if(logger.isTraceEnabled())
        {
            logger.trace("Alfresco version is" + alfrescoVersion);
            logger.trace("Alfresco shareUrl is" + shareUrl);
            logger.trace("Pentaho user console is" + pentahoUserConsoleUrl);
        }
        anotherUser = (UserProfile) ctx.getBean("anotherUser");
        if(logger.isTraceEnabled())
        {
            logger.trace("Loaded another user for test purposes - " + anotherUser);
        }
    }

    @BeforeClass(alwaysRun = true)
    public void getWebDrone() throws Exception
    {
        if(hybridEnabled)
        {
            hybridDrone = (WebDrone) ctx.getBean("hybridDrone");
            hybridDrone.maximize();
        }
        drone = (WebDrone) ctx.getBean("webDrone");
        drone.maximize();
    }

    @AfterClass(alwaysRun = true)
    public void closeWebDrone()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("Closing web drone");
        }
        // Close the browser
        if (drone != null)
        {
            drone.quit();
            drone = null;
        }
        // Close the browser
        if (hybridDrone != null)
        {
            hybridDrone.quit();
            hybridDrone = null;
        }
        // Close the browser
        if (customDrone != null)
        {
            customDrone.quit();
            customDrone = null;
        }
    }

    /**
     * Helper to log admin user into dashboard.
     *
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public DashBoardPage loginAs(final String... userInfo) throws Exception
    {
        if(shareUrl == null)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("null shareUrl");
            }
        }
        return ShareUtil.loginAs(drone, shareUrl, userInfo).render();
    }

    /**
     * Helper to log admin user into dashboard.
     *
     * @return DashBoardPage page object.
     * @throws Exception if error
     */
    public DashBoardPage loginAs(WebDrone drone, String shareUrl, final String... userInfo) throws Exception
    {
        if(shareUrl == null)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("null shareUrl");
            }
        }
        return ShareUtil.loginAs(drone, shareUrl, userInfo).render();
    }

    public void saveOsScreenShot(String methodName) throws IOException, AWTException
    {
        Robot robot = new Robot();
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIO.write(screenShot, "png", new File("target/webdrone-" + methodName+ "_OS" +".png"));
    }

    public void saveScreenShot(String methodName) throws IOException
    {
        if(StringUtils.isEmpty(methodName))
        {
            throw new IllegalArgumentException("Method Name can't be empty or null.");
        }
        File file = drone.getScreenShot();
        File tmp = new File("target/webdrone-" + methodName + ".png");
        FileUtils.copyFile(file, tmp);
    }

    public void savePageSource(String methodName) throws IOException
    {
        String htmlSource = ((WebDroneImpl) drone).getDriver().getPageSource();
        File file = new File("target/webdrone-" + methodName + ".html");
        FileUtils.writeStringToFile(file, htmlSource);
    }

    @BeforeMethod(alwaysRun = true)
    protected void startSession(Method method) throws Exception
    {
        testName = method.getName();
        if(logger.isTraceEnabled())
        {
            logger.trace(String.format("Test run:%s.%s",
                                        method.getDeclaringClass().getCanonicalName(),
                                        testName));
        }
    }
    /**
     * Helper method to get site dashboard page.
     * @param siteName String name of the site to enter
     * @return {@link SiteDashboardPage} page
     */
    protected SiteDashboardPage getSiteDashboard(final String siteName)
    {
        DashBoardPage dashBoard = drone.getCurrentPage().render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        return dashlet.selectSite(siteName).click().render();
    }

    /**
     * User Log out using logout URL Assumes User is logged in.
     *
     * @param drone WebDrone Instance
     */
    public static void logout(WebDrone drone)
    {
        if(drone != null)
        {
            try
            {
                if (drone.getCurrentUrl().contains(shareUrl.trim()))
                {
                    ShareUtil.logout(drone);
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
     * Function to create user on Enterprise using UI
     *
     * @param uname - This should always be unique. So the user of this method needs to verify it is unique.
     *                eg. - "testUser" + System.currentTimeMillis();
     * @return
     * @throws Exception
     */
    public boolean createEnterpriseUser(String uname) throws Exception
    {
        if (alfrescoVersion.isCloud() || StringUtils.isEmpty(uname))
        {
            throw new UnsupportedOperationException("This method is not applicable for cloud");
        }
        try
        {
            DashBoardPage dashBoard = loginAs(username, password);
            UserSearchPage page = dashBoard.getNav().getUsersPage().render();
            NewUserPage newPage = page.selectNewUser().render();
            String userinfo = uname + "@test.com";
            newPage.inputFirstName(userinfo);
            newPage.inputLastName(userinfo);
            newPage.inputEmail(userinfo);
            newPage.inputUsername(uname);
            newPage.inputPassword("password");
            newPage.inputVerifyPassword("password");
            UserSearchPage userCreated = newPage.selectCreateUser().render();
            userCreated.searchFor(userinfo).render();
            return userCreated.hasResults();
        }
        catch (Throwable t)
        {
            saveScreenShot("createUser");
            throw new Exception(t);
        }
        finally
        {
            logout(drone);
        }
    }

    public boolean isHybridEnabled()
    {
        return hybridEnabled;
    }

    /**
     * Method to setup cloud sync from My Profile page.
     * @param drone
     * @param cloudUserName
     * @param password
     */
    protected void signInToCloud(WebDrone drone, String cloudUserName, String password)
    {
        // go to profile
        MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();

        // Click cloud sync
        CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
        // Sign in

        if (!cloudSyncPage.isDisconnectButtonDisplayed())
        {
            CloudSignInPage cloudSignPage = cloudSyncPage.selectCloudSign().render();
            cloudSyncPage = cloudSignPage.loginAs(cloudUserName, password).render();
        }
        logger.info("is signed in: " + cloudSyncPage.isDisconnectButtonDisplayed());
        cloudSyncPage.render();
    }

    /**
     * Utility method to open site document library from search
     * @param drone
     * @param siteName
     * @return
     */
    protected DocumentLibraryPage openSiteDocumentLibraryFromSearch(WebDrone drone, String siteName)
    {
        SharePage sharePage = drone.getCurrentPage().render();
        SiteFinderPage siteFinderPage = sharePage.getNav().selectSearchForSites().render();
        siteFinderPage.searchForSite(siteName).render();
        siteFinderPage = SiteUtil.siteSearchRetry(drone, siteFinderPage, siteName);
        SiteDashboardPage siteDashboardPage = siteFinderPage.selectSite(siteName).render();
        DocumentLibraryPage documentLibPage = siteDashboardPage.getSiteNav().selectSiteDocumentLibrary().render();
        return documentLibPage;
    }

    /**
     * Utility method to disconnect Cloud Sync.
     * @param drone
     */
    protected void disconnectCloudSync(WebDrone drone)
    {
        if(hybridEnabled)
        {
            MyProfilePage myProfilePage = ((SharePage) drone.getCurrentPage()).getNav().selectMyProfile().render();
            CloudSyncPage cloudSyncPage = myProfilePage.getProfileNav().selectCloudSyncPage().render();
            if(cloudSyncPage.isDisconnectButtonDisplayed())
            {
                cloudSyncPage.disconnectCloudAccount().render();
            }
        }
    }

    /**
     * Method to Cancel a WorkFlow or Delete a WorkFlow (To use in TearDown method)
     * @param workFlow
     */
    protected void cancelWorkFlow(String workFlow)
    {
        SharePage sharePage = drone.getCurrentPage().render();
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
     * This method is used to get if the task is present or not.
     * If the task is not displayed, retry for defined time(maxWaitTime_CloudSync)
     * @param driver
     * @param taskName
     * @return boolean
     */
    public static boolean checkIfTaskIsPresent(WebDrone driver, String taskName)
    {
        MyTasksPage myTasksPage = (MyTasksPage) driver.getCurrentPage();
        myTasksPage.render();

        RenderTime t = new RenderTime(maxWaitTime_CloudSync);
        try
        {
            while (true)
            {
                t.start();
                try
                {
                    if(myTasksPage.isTaskPresent(taskName))
                    {
                        return true;
                    }
                    else
                    {
                        try
                        {
                            driver.waitForElement(By.id("AlfrescoWebdronez1"), SECONDS.convert(1000, MILLISECONDS));
                        }
                        catch (TimeoutException e) {}
                        driver.refresh();
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageRenderTimeException p)
        {
        }
        return false;
    }

    /**
     * This method is used to get sync status (with retry) for a content from
     * document library page and returns true if the content synced otherwise
     * false. Since cloud sync is not instantaneous, the method keeps retrying
     * until maxWaitTime_CloudSync is reached This method could be invoked after
     * syncToCloud is initiated from document library page.
     *
     * @param driver
     * @param fileName
     * @return boolean
     */
    public boolean checkIfContentIsSynced(WebDrone driver, String fileName)
    {
        DocumentLibraryPage docLibPage = driver.getCurrentPage().render();
        docLibPage = docLibPage.renderItem(maxWaitTime_CloudSync, fileName);

        String status = "";
        SyncInfoPage syncInfoPage;

        try
        {
            RenderTime t = new RenderTime(maxWaitTime_CloudSync);
            while (true)
            {
                t.start();
                try
                {
                    syncInfoPage = docLibPage.getFileDirectoryInfo(fileName).clickOnViewCloudSyncInfo().render();
                    status = syncInfoPage.getCloudSyncStatus();
                    syncInfoPage.clickOnCloseButton();

                    if (status.contains("Pending"))
                    {
                    	synchronized (this)
                        {
                            try
                            {
                                this.wait(1000L);
                            }
                            catch (InterruptedException e)
                            {
                            }
                        }
                        driver.refresh();
                        docLibPage = driver.getCurrentPage().render();
                        docLibPage = docLibPage.renderItem(maxWaitTime_CloudSync, fileName).render();
                    }
                    else
                    {
                        return status.contains("Synced");
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageException e)
        {
        }
        catch (PageRenderTimeException exception)
        {
        }

        return false;
    }
    /**
     * This method is used to get sync status (with retry) for a content from
     * document details page and returns true if the content synced otherwise
     * false. Since cloud sync is not instantaneous, the method keeps retrying
     * until maxWaitTime_CloudSync is reached This method could be invoked after
     * syncToCloud is initiated from document library page.
     *
     * @param driver
     * @return boolean
     */
    public boolean checkIfContentIsSynced(WebDrone driver)
    {
        DocumentDetailsPage detailsPage = driver.getCurrentPage().render();

        String status = "";
        try
        {
            RenderTime t = new RenderTime(maxWaitTime_CloudSync);
            while (true)
            {
                t.start();
                try
                {
                    status = detailsPage.getSyncStatus();
                    if (status.contains("Pending") || status.isEmpty())
                    {
                    	synchronized (this)
                        {
                            try
                            {
                                this.wait(1000L);
                            }
                            catch (InterruptedException e)
                            {
                            }
                        }
                        driver.refresh();
                        detailsPage = driver.getCurrentPage().render();
                    }
                    else
                    {
                        return status.contains("Synced");
                    }
                }
                finally
                {
                    t.end();
                }
            }
        }
        catch (PageException e)
        {
        }
        catch (PageRenderTimeException exception)
        {
        }

        return false;
    }

    /**
     * Method to upload a file from given path. Method assumes that user is already in Document Library Page
     * @param drone
     * @param filePath
     * @return
     */
    public DocumentLibraryPage uploadContent(WebDrone drone, String filePath)
    {
        DocumentLibraryPage documentLibraryPage = drone.getCurrentPage().render();
        UploadFilePage uploadForm = documentLibraryPage.getNavigation().selectFileUpload().render();
        return uploadForm.uploadFile(filePath).render();
    }
    
    /**
     * Helper to create a new file, empty or with specified contents if one does not exist. 
     * Logs if File already exists
     * 
     * @param filename String Complete path of the file to be created
     * @param contents String Contents for text file
     * @return File
     */
    public static File newFile(String filename, String contents)
    {
        File file = new File(filename);

        try
        {
            if (!file.exists())
            {

                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                }
                else
                {
                    file.createNewFile();
                }
            }
            else
            {
                logger.debug("Filename already exists: " + filename);
            }
        }
        catch (IOException ex)
        {
            logger.error("Unable to create sample file", ex);
        }
        return file;
    }

}
