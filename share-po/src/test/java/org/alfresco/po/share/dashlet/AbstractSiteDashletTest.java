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
package org.alfresco.po.share.dashlet;

import java.io.File;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

/**
 * Abstract class with common method used in dashlet
 * based test cases.
 * @author Michael Suzuki
 *
 */
public class AbstractSiteDashletTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(AbstractSiteDashletTest.class);
    protected String siteName;
    protected String fileName;
    protected SiteDashboardPage siteDashBoard;
    
    /**
     * Creates a site and uploads a document to the site.
     * @throws Exception if error
     */
    protected void uploadDocument()throws Exception
    {
        try
        {
             File file = siteUtil.prepareFile();
             fileName = file.getName();
             loginAs(username, password);
             siteUtil.createSite(driver, username, password,siteName, 
                         "description",
                         "Public");
             SitePage site = resolvePage(driver).render();
             DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
             //DocumentLibraryPage docPage = getDocumentLibraryPage(siteName).render();
             UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
             docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
             DocumentDetailsPage dd = docPage.selectFile(fileName).render();
             dd.selectLike();
        }
        catch (Throwable pe)
        {
            saveScreenShot("uploadDodDashlet");
            logger.error("Problem deleting site", pe);
        }
    }
   
    protected HtmlPage navigateToSiteDashboard()
    {
        if(logger.isTraceEnabled())
        {
            logger.trace("navigate to " + shareUrl);
        }
        driver.navigate().to(shareUrl);
        DashBoardPage boardPage = resolvePage(driver).render();
        boardPage.waitUntilElementDisappears(By.cssSelector("div.bd"),1000);
        SiteFinderPage finderPage = boardPage.getNav().selectSearchForSites().render();
        finderPage = finderPage.searchForSite(siteName).render();
        finderPage = siteUtil.siteSearchRetry(driver, finderPage, siteName);
        siteDashBoard = finderPage.selectSite(siteName).render();
        return siteDashBoard;
    }
    
    /**
    @AfterClass(alwaysRun=true)
    public void deleteSite()
    {
        try
        {
            if(driver != null && !StringUtils.isEmpty(siteName))
            {
                siteUtil.deleteSite(username, password, siteName);
                closeWebDriver();
            }
        }
        catch (Exception e)
        {
            logger.error("Problem deleting site", e);
        }
    }
    **/
}
