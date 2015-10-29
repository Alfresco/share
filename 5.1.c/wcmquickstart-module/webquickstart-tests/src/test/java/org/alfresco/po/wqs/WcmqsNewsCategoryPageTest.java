package org.alfresco.po.wqs;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.AbstractTest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by P3700360 on 04.02.2015.
 */
public class WcmqsNewsCategoryPageTest extends AbstractTest
{
        private static final Logger logger = Logger.getLogger(WcmqsNewsCategoryPageTest.class);
        DashBoardPage dashBoard;
        private String wqsURL;
        private String siteName;
        private String ipAddress;

        @BeforeClass(alwaysRun = true)
        public void prepare() throws Exception {
                String testName = this.getClass().getSimpleName();
                siteName = testName;

                String hostName = (shareUrl).replaceAll(".*\\//|\\:.*", "");
                try {
                        ipAddress = InetAddress.getByName(hostName).toString().replaceAll(".*/", "");
                        logger.info("Ip address from Alfresco server was obtained");
                } catch (UnknownHostException | SecurityException e) {
                        logger.error("Ip address from Alfresco server could not be obtained");
                }

                wqsURL = siteName + ":8080/wcmqs/news/global/";
                logger.info(" wcmqs url : " + wqsURL);
                logger.info("Start Tests from: " + testName);

                // WCM Quick Start is installed; - is not required to be executed automatically
                int columnNumber = 2;
                String SITE_WEB_QUICK_START_DASHLET = "site-wqs";
                dashBoard = loginAs(username, password);
                //
                //                // Site is created in Alfresco Share;
                //                CreateSitePage createSitePage = dashBoard.getNav().selectCreateSite().render();
                //                SitePage site = createSitePage.createNewSite(siteName).render();
                //
                //                // WCM Quick Start Site Data is imported;
                //                CustomiseSiteDashboardPage customiseSiteDashboardPage = site.getSiteNav().selectCustomizeDashboard().render();
                //                SiteDashboardPage siteDashboardPage = customiseSiteDashboardPage.addDashlet(Dashlets.WEB_QUICK_START, columnNumber);
                //                SiteWebQuickStartDashlet wqsDashlet = siteDashboardPage.getDashlet(SITE_WEB_QUICK_START_DASHLET).render();
                //                wqsDashlet.selectWebsiteDataOption(WebQuickStartOptions.FINANCE);
                //                wqsDashlet.clickImportButtton();
                //                wqsDashlet.waitForImportMessage();
                //
                //                // Change property for quick start to sitename
                //                DocumentLibraryPage documentLibraryPage = site.getSiteNav().selectSiteDocumentLibrary().render();
                //                documentLibraryPage.selectFolder("Alfresco Quick Start");
                //                EditDocumentPropertiesPage documentPropertiesPage = documentLibraryPage.getFileDirectoryInfo("Quick Start Editorial").selectEditProperties()
                //                        .render();
                //                documentPropertiesPage.setSiteHostname(siteName);
                //                documentPropertiesPage.clickSave();
                //
                //                // Change property for quick start live to ip address
                //                documentLibraryPage.getFileDirectoryInfo("Quick Start Live").selectEditProperties().render();
                //                documentPropertiesPage.setSiteHostname(ipAddress);
                //                documentPropertiesPage.clickSave();
                //
                //                // setup new entry in hosts to be able to access the new wcmqs site
                //                String setHostAddress = "cmd.exe /c echo. >> %WINDIR%\\System32\\Drivers\\Etc\\hosts && echo " + ipAddress + " " + siteName
                //                        + " >> %WINDIR%\\System32\\Drivers\\Etc\\hosts";
                //                Runtime.getRuntime().exec(setHostAddress);


        }

        @AfterClass
        public void tearDown()
        {
                logout(drone);
        }

        @BeforeMethod
        public void setUp() {
                drone.navigateTo(wqsURL);
        }

        @Test
        public void testIsRSSLinkDisplayed()
        {
//                WcmqsNewsCategoryPage categoryPage = new WcmqsNewsCategoryPage(drone);
//                categoryPage.render();
//                Assert.assertTrue(categoryPage.isRSSLinkDisplayed());
        }
}
