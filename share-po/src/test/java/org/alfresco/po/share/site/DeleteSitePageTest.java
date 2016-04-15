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
package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.SharePage;


import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Delete site from Site Finder
 * 
 * @author Bocancea Bogdan
 */

@Listeners(FailedTestListener.class)
@Test(groups = "alfresco-one")
public class DeleteSitePageTest extends AbstractTest
{
    private String siteName1;
    private String userName;
    
    private static SiteFinderPage siteFinder;
    private static DeleteSitePage firstPopUp;
    private static DeleteSiteConfirmPage secondPopUp;

    @BeforeClass
    public void prepare() throws Exception
    {
        siteName1 = "SiteForDelete" + System.currentTimeMillis();
        userName = "User_" + System.currentTimeMillis();
        createEnterpriseUser(userName);
        shareUtil.loginAs(driver, shareUrl, userName, UNAME_PASSWORD).render();
    }

    @Test
    public void deleteSite()
    {
        String message_delete_site, message_confirm;
        String comparedMsg, compare_confirm_msg;
        
        siteUtil.createSite(driver, userName, UNAME_PASSWORD, siteName1, "description", "Public");
        SharePage page = resolvePage(driver).render();
        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = siteUtil.siteSearchRetry(driver, siteFinder, siteName1);

        List<String> sitesFound = siteFinder.getSiteList();

        // Click "Delete" button for Site1 -> Cancel Button
        firstPopUp = siteFinder.clickDelete(siteName1).render();
        message_delete_site = firstPopUp.getMessage(); 
        comparedMsg = "Are you sure you want to remove the site " + "''" +  siteName1 + "''" + "?";
        Assert.assertEquals(message_delete_site, comparedMsg);
        
        siteFinder = firstPopUp.clickCancel().render();
        Assert.assertTrue(sitesFound.contains(siteName1));

        // click delete -> delete -> No
        firstPopUp = siteFinder.clickDelete(siteName1).render();
        secondPopUp = firstPopUp.clickDelete().render();
        
        message_confirm = secondPopUp.getMessage();  
        compare_confirm_msg = "All content inside the site will be deleted.\nAre you sure you want to delete the site?";
        Assert.assertEquals(message_confirm, compare_confirm_msg);
        
        siteFinder = secondPopUp.clickNo().render();
        Assert.assertTrue(sitesFound.contains(siteName1));

        // click delete-> delete -> Yes
        firstPopUp = siteFinder.clickDelete(siteName1).render();
        secondPopUp = firstPopUp.clickDelete().render();
        siteFinder = secondPopUp.clickYes().render();

        // search for the site
        page = resolvePage(driver).render();
        siteFinder = page.getNav().selectSearchForSites().render();
        siteFinder = siteFinder.searchForSite(siteName1).render();
        List<String> sitesFound2 = siteFinder.getSiteList();
        
        Assert.assertFalse(sitesFound2.contains(siteName1));

    }

}
