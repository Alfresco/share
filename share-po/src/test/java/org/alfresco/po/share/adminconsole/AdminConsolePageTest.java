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
package org.alfresco.po.share.adminconsole;


import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.LoginPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

//import java.io.File;
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;

//import static org.testng.Assert.assertEquals;

import static org.testng.Assert.assertTrue;

/**
 * @author Antonik Olga
 */
@Listeners(FailedTestListener.class)
public class AdminConsolePageTest extends AbstractTest
{
    AdminConsolePage adminConsolePage;
    String userName;

  
    
    @Test(groups = "Enterprise-only")
    public void checkThatFactoryReturnApplicationPage() throws Exception
    {
        userName = "AdminConsolePage" + System.currentTimeMillis();
        createEnterpriseUser(userName);
        SharePage page = loginAs("admin", "admin");
        page.getNav().getAdminConsolePage().render();
        resolvePage(driver).render();

    }
    
    
    @Test(dependsOnMethods = "checkThatFactoryReturnApplicationPage", groups = "Enterprise-only")
    public void adminConsoleAccessTest() throws Exception
    {
        adminConsolePage = resolvePage(driver).render();
        shareUtil.logout(driver);
        
        //log in as normal user and check that error page is displayed
        driver.navigate().to(shareUrl + "/page/console/admin-console/application");
        LoginPage loginPage = factoryPage.getPage(driver).render();
        loginPage.loginAs(userName, "password");
        assertTrue(resolvePage(driver).getTitle().contains("System Error"));
        
        driver.navigate().to(shareUrl + "/page/user/" + userName + "/dashboard");
        shareUtil.logout(driver);
        
        //login as admin user and check that admin console is displayed
        driver.navigate().to(shareUrl + "/page/console/admin-console/application");
        loginPage = factoryPage.getPage(driver).render();
        loginPage.loginAs("admin", "admin");
        adminConsolePage = resolvePage(driver).render();
        assertTrue(adminConsolePage.getPageTitle().equals("Admin Tools"));
    }  
    /**
    @Test(dependsOnMethods = "adminConsoleAccessTest", groups = "Enterprise-only", alwaysRun = true)
    public void selectThemeTest() throws Exception
    {
        adminConsolePage = resolvePage(driver).render();
        adminConsolePage.selectTheme(AdminConsolePage.ThemeType.googleDocs).render();
        assertTrue(adminConsolePage.isThemeSelected(AdminConsolePage.ThemeType.googleDocs));
        adminConsolePage.selectTheme(AdminConsolePage.ThemeType.light).render();
        assertTrue(adminConsolePage.isThemeSelected(AdminConsolePage.ThemeType.light));


    }
    
    @Test(dependsOnMethods = "selectThemeTest", groups = "Enterprise-only", alwaysRun = true)
    public void uploadPictureTest() throws IOException, TimeoutException, InterruptedException
    {
        adminConsolePage = resolvePage(driver).render();

        // negative test, that it is impossible to upload not picture file (for example, txt)
        File pic = siteUtil.prepareFile("ff.txt");
        String srcBeforeUpload = driver.findElement(AdminConsolePage.LOGO_PICTURE).getAttribute("src");
        adminConsolePage.uploadPicture(pic.getCanonicalPath());
        String srcAfterUpload = driver.findElement(AdminConsolePage.LOGO_PICTURE).getAttribute("src");

        assertEquals(srcBeforeUpload, srcAfterUpload);
    }
    **/
}
