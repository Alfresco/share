package org.alfresco.po.share.dashlet;

import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Tests for Data Lists dashlet web elements
 * 
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise4.2", "Enterprise-only" })
public class SiteDataListsDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_DATA_LISTS_DASHLET = "data-lists";
    private SiteDataListsDashlet siteDataListsDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private NewListForm newListForm = null;

    private static final String EXP_HELP_BALLOON_MSG = "This dashlet shows lists relevant to the site. Clicking a list opens it.";
    private static final String LIST_NAME = "WhatIHateLIST";

    @BeforeClass
    public void setUp() throws Exception
    {
        siteName = "siteDataListsDashletTest" + System.currentTimeMillis();
        loginAs("admin", "admin");
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.SITE_DATA_LISTS, 1).render();
        siteDataListsDashlet = siteDashBoard.getDashlet(SITE_DATA_LISTS_DASHLET).render();
        assertNotNull(siteDataListsDashlet);
    }

    @Test(dependsOnMethods = "instantiateDashlet")
    public void verifyHelpIcon()
    {
        siteDataListsDashlet.clickOnHelpIcon();
        assertTrue(siteDataListsDashlet.isBalloonDisplayed());
        String actualHelpBalloonMsg = siteDataListsDashlet.getHelpBalloonMessage();
        assertEquals(actualHelpBalloonMsg, EXP_HELP_BALLOON_MSG);
        siteDataListsDashlet.closeHelpBallon();
        assertFalse(siteDataListsDashlet.isBalloonDisplayed());
    }

    @Test(dependsOnMethods = "verifyHelpIcon", groups = "TestBug")
    public void verifyCreateDataList()
    {
        assertTrue(siteDataListsDashlet.isCreateDataListDisplayed());
        newListForm = siteDataListsDashlet.clickCreateDataList();
        assertNotNull(newListForm);
        newListForm.inputTitleField(LIST_NAME);
        newListForm.inputDescriptionField(LIST_NAME);
        newListForm.selectListType(CONTACT_LIST);
        newListForm.clickSave();
        assertTrue(drone.getCurrentPage().render() instanceof DataListPage);
    }

    @Test(dependsOnMethods = "verifyCreateDataList", groups = "TestBug")
    public void verifyListCountInDashlet()
    {
        navigateToSiteDashboard();
        assertEquals(siteDataListsDashlet.getListsCount(), 1);
    }

    @Test(dependsOnMethods = "verifyListCountInDashlet", groups = "TestBug")
    public void verifyIsDataListDisplayed()
    {
        assertTrue(siteDataListsDashlet.isDataListDisplayed(LIST_NAME));
    }

}
