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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to hold tests for Pending Request page Test 
 * Enterprise 5.1 onwards
 * @author Charu
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class PendingRequestPageTest extends AbstractTest
{      
	private static PendingInvitesPage pendingRequestPage;  
    private static EditTaskPage editTaskPage;
    String siteName;
    String siteName1;
    String siteName2;
    String modSiteName;
    List<WebElement> pendingRequestList;
    String userName;    
    private String userName1;
    private String userName2;
    
    public static long refreshDuration = 15000;

    @BeforeClass(groups = "Enterprise-only")
    public void instantiatePendingRequest() throws Exception
    {
        userName = "user" + System.currentTimeMillis();       
        userName1 = "user1" + System.currentTimeMillis();
        userName2 = "user2" + System.currentTimeMillis();
        siteName = "PendingInvitesTest" + System.currentTimeMillis();
        siteName1 = "pendReqTest" + System.currentTimeMillis();
        modSiteName = "modSN" + System.currentTimeMillis();              
               
             
        loginAs(username, password);
      
        siteUtil.createSite(driver, username, password, modSiteName,"","Moderated");
       
        createEnterpriseUser(userName1);
        createEnterpriseUser(userName2);
        logout(driver);
        
        loginAs(userName1, "password");
               
        siteActions.requestToJoinModSite(driver, modSiteName);
        
        loginAs(userName2, "password"); 
                
        siteActions.requestToJoinModSite(driver, modSiteName);        
        
        loginAs(username, password); 
        
    }

    @Test(groups = "Enterprise-only", priority = 1, enabled = true)
    public void navigateToPendingInvitesPage() throws Exception
    {    	
    	pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName).render();
    	assertEquals(pendingRequestPage.getRequests().size(), 2);
    	Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName1));
    	Assert.assertTrue(pendingRequestPage.isUserNameDisplayedInList(userName2));
    }
    
    @Test(groups = "Enterprise-only", priority = 2, enabled = true)
    public void selectViewButtonBeforeSearch() throws Exception
    {    	
    	pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName).render();  	
        editTaskPage = pendingRequestPage.viewRequest(userName1).render();
    	pendingRequestPage = editTaskPage.selectSaveButton().render(); 	
        siteActions.navigateToPendingRequestPage(driver, modSiteName);
    	assertEquals(pendingRequestPage.getRequests().size(), 2);
    	editTaskPage = pendingRequestPage.viewRequest(userName2).render();        
    	pendingRequestPage = editTaskPage.selectSaveButton().render(); 	
    	
    }
    
    @Test(groups = "Enterprise-only", priority = 3, enabled = false)
    public void selectAcceptButtonBeforeSearch() throws Exception
    {    	
    	pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName).render();  	
    	pendingRequestPage.approveRequest(userName1);    	
    	assertEquals(pendingRequestPage.getRequests().size(), 1);
    	
    }   
                      
    @Test(groups = "Enterprise-only", priority = 4, enabled = true)
    public void clickViewButtonaftersearch() throws Exception
    {
    	pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName).render();  	
    	pendingRequestPage.searchRequest(userName2);
        assertEquals(pendingRequestPage.getRequests().size(), 1);
    	editTaskPage = pendingRequestPage.viewRequest(userName2).render();        
    	pendingRequestPage = editTaskPage.selectSaveButton().render(); 	
        
    }
    
    @Test(groups = "Enterprise-only", priority = 5, enabled = true)
    public void clickApproveButton()
    {    
    	pendingRequestPage = siteActions.navigateToPendingRequestPage(driver, modSiteName).render();  	
    	pendingRequestPage.searchRequest(userName2);       
        pendingRequestPage = pendingRequestPage.approveRequest(userName2).render();        
    	assertEquals(pendingRequestPage.getRequests().size(), 0);
    } 
    
    @AfterClass
    public void tearDown()
    {
        siteUtil.deleteSite(username, password, modSiteName);
      
    }
    
    
}
