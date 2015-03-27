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
package org.alfresco.po.share.search;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to validate the advance Search for all CRM attachements.
 * 
 * @author Subashni Prasanna
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
public class AdvanceSearchCRMTest extends AbstractTest
{
    DashBoardPage dashBoard;
    SiteDashboardPage site;
    AdvanceSearchCRMPage crmSearchPage;

    /**
     * Pre test setup for CRM Search.
     * 
     * @throws Exception
     */
    @BeforeClass(groups={"Cloud2"})
    public void prepare() throws Exception
    {
        dashBoard = loginAs(username, password);
    }

    /**
     * This Test case is to Test CRM search with All fields entered
     * 
     * @throws Exception
     */
    @Test(groups={"Cloud2"})
    public void crmSearchTest() throws Exception
    {
        AdvanceSearchContentPage contentSearchPage = dashBoard.getNav().selectAdvanceSearch().render();
        crmSearchPage = contentSearchPage.searchLink("CRM Attachments").render();
        crmSearchPage.inputCrmAccountId("001b0000009aPo1AAE");
        crmSearchPage.inputCrmAccountName("helloaccount");
        crmSearchPage.inputCrmOpporName("helloaccountoppor");
        crmSearchPage.inputCrmContractNumber("00000104");
        crmSearchPage.inputCrmContractName("00000104");
        crmSearchPage.inputCrmCaseNumber("00001006");
        crmSearchPage.inputCrmCaseName("00001006");
        SearchResultsPage searchResults = crmSearchPage.clickSearch().render();
        Assert.assertTrue(searchResults.count()==0);
    } 
    
    /**
     * This Test is to check when I pass Null value to the CrmAccountIdentifier field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmAccountIdNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmAccountId(null);
    }

    /**
     * This Test is to check when I pass empty value to the CrmAccountIdentifier field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmAccountIdEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmAccountId("");
    }
    
    /**
     * This Test is to check when I pass Null value to the CrmAccountName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmAccountNameNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmAccountName(null);
    }
    
    /**
     * This Test is to check when I pass Empty value to the CrmAccountName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmAccountNameEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmAccountName("");
    }
    
    /**
     * This Test is to check when I pass Null value to the CrmOpporName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmOpporNameNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmOpporName(null);
    }
    /**
     * This Test is to check when I pass Empty value to the CrmContractNumber field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmOpporNameEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmOpporName("");
    }
    /**
     * This Test is to check when I pass Empty value to the CrmCaseNumber field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmCaseNumberEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmCaseNumber("");
    }
    
    /**
     * This Test is to check when I pass Null value to the CrmCaseNumber field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmCaseNumberNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmCaseNumber(null);
    }
    /**
     * This Test is to check when I pass Empty value to the CrmCaseName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmCaseNameEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmCaseName("");
    }
    
    /**
     * This Test is to check when I pass Null value to the CrmCaseName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmCaseNameNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmCaseName(null);
    } 
    
    /**
     * This Test is to check when I pass Empty value to the CrmContractNumber field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmContractNumberEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmContractNumber("");
    }
    
    /**
     * This Test is to check when I pass Null value to the CrmContractNumber field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmContractNumberNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmContractNumber(null);
    }
    /**
     * This Test is to check when I pass Empty value to the CrmContractName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmContractNameEmptyCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmContractName("");
    }
    
    /**
     * This Test is to check when I pass Null value to the CrmContractName field and
     * exception is thrown as excepted.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class, groups="Cloud2")
    public void crmContractNameNullCheckTest()
    {
        AdvanceSearchCRMPage searchpage = new AdvanceSearchCRMPage(drone);
        searchpage.inputCrmContractName(null);
    }  
    
}
