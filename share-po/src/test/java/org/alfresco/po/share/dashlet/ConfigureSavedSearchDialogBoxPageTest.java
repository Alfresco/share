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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.CustomiseUserDashboardPage;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test Saved search dashlet page elements.
 * 
 * @author Ranjith Manyam
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2" })
public class ConfigureSavedSearchDialogBoxPageTest extends AbstractSiteDashletTest
{
    private static final String SAVED_SEARCH = "saved-search";

    private SavedSearchDashlet savedSearchDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    ConfigureSavedSearchDialogBoxPage configureSavedSearchDialogBoxPage = null;
    private static final String savedSearchTitle = "Test Saved Search";
    private DashBoardPage dashBoardPage;

    @BeforeTest
    public void prepare() throws Exception
    {
        siteName = "configsavedsearchdashlet" + System.currentTimeMillis();
    }

    @BeforeClass
    public void loadFile() throws Exception
    {
        uploadDocument();
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SAVED_SEARCH, 1).render();
        savedSearchDashlet = siteDashBoard.getDashlet(SAVED_SEARCH).render();
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void clickOnEditButton()
    {
        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        Assert.assertNotNull(configureSavedSearchDialogBoxPage);
    }

    @Test(dependsOnMethods = "clickOnEditButton")
    public void clickOnCancelButton()
    {
        siteDashBoard = configureSavedSearchDialogBoxPage.clickOnCancelButton().render();
        Assert.assertFalse(siteDashBoard.isConfigureSavedSearchDialogDisplayed());
    }

    @Test(dependsOnMethods = "clickOnCancelButton")
    public void clickOnCloseButton()
    {
        savedSearchDashlet = siteDashBoard.getDashlet(SAVED_SEARCH).render();
        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        siteDashBoard = configureSavedSearchDialogBoxPage.clickOnCloseButton().render();
        Assert.assertFalse(siteDashBoard.isConfigureSavedSearchDialogDisplayed());
    }

    @Test(dependsOnMethods = "clickOnCloseButton")
    public void clickOnOKButton()
    {
        savedSearchDashlet = siteDashBoard.getDashlet(SAVED_SEARCH).render();
        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        configureSavedSearchDialogBoxPage.setSearchTerm(fileName);
        configureSavedSearchDialogBoxPage.setTitle(savedSearchTitle);
        configureSavedSearchDialogBoxPage.setSearchLimit(SearchLimit.FIFTY);
        siteDashBoard = configureSavedSearchDialogBoxPage.clickOnOKButton().render();
        Assert.assertFalse(siteDashBoard.isConfigureSavedSearchDialogDisplayed());
    }

    @Test(dependsOnMethods = "clickOnOKButton")
    public void verifySearchLimitValues()
    {
        savedSearchDashlet = siteDashBoard.getDashlet(SAVED_SEARCH).render();
        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        List<Integer> searchLimitValues = configureSavedSearchDialogBoxPage.getAvailableListOfSearchLimitValues();
        Assert.assertEquals(searchLimitValues.size(), SearchLimit.values().length);
        List<Integer> expectedLimits = new ArrayList<Integer>();
        for(SearchLimit val: SearchLimit.values())
        {
            expectedLimits.add(val.getValue());
        }
        Assert.assertTrue(searchLimitValues.containsAll(expectedLimits));
    }

    @Test(dependsOnMethods = "verifySearchLimitValues")
    public void isHelpBalloonDisplayed()
    {
        configureSavedSearchDialogBoxPage.setSearchTerm("");
        configureSavedSearchDialogBoxPage = configureSavedSearchDialogBoxPage.clickOnOKButton().render();
        Assert.assertTrue(configureSavedSearchDialogBoxPage.isHelpBalloonDisplayed());
        Assert.assertEquals(configureSavedSearchDialogBoxPage.getHelpBalloonMessage(), "The value cannot be empty.");
    }

    @Test(dependsOnMethods = "isHelpBalloonDisplayed")
    public void dashBoardPageClickCancelButton()
    {
        siteDashBoard = configureSavedSearchDialogBoxPage.clickOnCancelButton().render();
        dashBoardPage = siteDashBoard.getNav().selectMyDashBoard().render();
        CustomiseUserDashboardPage customiseUserDashBoard = dashBoardPage.getNav().selectCustomizeUserDashboard();
        customiseUserDashBoard.render();
        dashBoardPage = customiseUserDashBoard.addDashlet(Dashlets.SAVED_SEARCH, 1).render();
        savedSearchDashlet = dashBoardPage.getDashlet("saved-search").render();

        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        dashBoardPage = configureSavedSearchDialogBoxPage.clickOnCancelButton().render();
        Assert.assertTrue(dashBoardPage != null);
    }

    @Test(dependsOnMethods = "isHelpBalloonDisplayed")
    public void dashBoardPageClickCloseButton()
    {
        savedSearchDashlet = dashBoardPage.getDashlet("saved-search").render();

        configureSavedSearchDialogBoxPage = savedSearchDashlet.clickOnEditButton().render();
        dashBoardPage = configureSavedSearchDialogBoxPage.clickOnCloseButton().render();
        Assert.assertTrue(dashBoardPage != null);
    }
}
