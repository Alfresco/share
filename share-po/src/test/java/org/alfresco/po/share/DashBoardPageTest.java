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

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.site.document.SharedFilesPage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.Keys;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to verify dashboard page elements are in place.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
@Listeners(FailedTestListener.class)
public class DashBoardPageTest extends AbstractTest
{
    /**
     * Test process of accessing dashboard page.
     *
     * @throws Exception
     */
    
    DashBoardPage dashBoard;

    @Test(groups = "alfresco-one")
    public void loadDashBoard() throws Exception
    {
        dashBoard = loginAs(username, password);

        Assert.assertTrue(dashBoard.isLogoPresent());
        String copyright = dashBoard.getCopyRight();
        Assert.assertTrue(copyright.contains("Alfresco Software"));
    }

    @Test(dependsOnMethods = "loadDashBoard", groups = "alfresco-one")
    public void refreshPage() throws Exception
    {
        //Were already logged in from the previous test.
        driver.navigate().refresh();
        DashBoardPage dashBoard = resolvePage(driver).render();
        Assert.assertNotNull(dashBoard);
    }

    @Test(dependsOnMethods = "refreshPage", groups = "alfresco-one")
    public void checkTopLogoUrl()
    {
        dashBoard = resolvePage(driver).render();
        Assert.assertNotNull(dashBoard.getTopLogoUrl());
    }

    @Test(dependsOnMethods = "refreshPage", groups = "alfresco-one")
    public void checkFooterLogoUrl()
    {
        DashBoardPage dashBoardPage = resolvePage(driver).render();
        Assert.assertNotNull(dashBoardPage.getFooterLogoUrl());
    }

    @Test(dependsOnMethods = "refreshPage", enabled = false, groups = "nonGrid")
    public void testKeysForHeaderBar() throws Exception
    {
        driver.navigate().refresh();
        dashBoard.inputFromKeyborad(Keys.TAB);
        dashBoard.inputFromKeyborad(Keys.ARROW_RIGHT);
        dashBoard.inputFromKeyborad(Keys.ARROW_RIGHT);
        dashBoard.inputFromKeyborad(Keys.RETURN);
        Assert.assertTrue(resolvePage(driver).render() instanceof SharedFilesPage);
    }
    
    /**
     * Verifies that Get Started Panel can be removed from user dashboard page
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "checkTopLogoUrl", groups = "Enterprise-only")
    public void testHideGetStartedPanelFromUserDashboard() throws Exception
    {
        HideGetStartedPanel  hideGetStartedPanel = dashBoard.clickOnHideGetStartedPanelButton().render();
        dashBoard = hideGetStartedPanel.clickOnHideGetStartedPanelOkButton().render();
        Assert.assertFalse(dashBoard.panelExists(dashBoard.getGetStartedPanelTitle()));
                
    }
    
    /**
     * Verifies that Get Started Panel on user dashboard page can be restored from Customise User dashboard page
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "testHideGetStartedPanelFromUserDashboard", groups = "Enterprise-only")
    public void testShowGetStartedPanelFromCustomiseUserDashboard() throws Exception
    {
        //go to Customise User Dashboard page and click on Show radio button
        CustomiseUserDashboardPage customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard().render();
        
        //click on show get started panel radio button
        customiseUserDashboardPage = customiseUserDashboardPage.clickOnShowOnDashboardRadioButton().render();
        
        //click on OK button
        dashBoard = customiseUserDashboardPage.selectOk().render();
        
        //check that get Started Panel is restored on the user dashboard
        Assert.assertTrue(dashBoard.panelExists(dashBoard.getGetStartedPanelTitle()));
                
    }
    
    @Test(dependsOnMethods = "testShowGetStartedPanelFromCustomiseUserDashboard", groups = "Enterprise-only")
    public void testHideGetStartedPanelFromCustomiseUserDashboard() throws Exception
    {
        //go to Customise User Dashboard page and click on Hide radio button 
        CustomiseUserDashboardPage customiseUserDashboardPage = dashBoard.getNav().selectCustomizeUserDashboard().render();
        
        //click on hide get started panel radio button
        customiseUserDashboardPage = customiseUserDashboardPage.clickOnHideOnDashboardRadioButton().render();
        
        //click on OK button
        dashBoard = customiseUserDashboardPage.selectOk().render();
        
        //check that get Started Panel is not present on the user dashboard
        Assert.assertFalse(dashBoard.panelExists(dashBoard.getGetStartedPanelTitle()));
                
    }
}

