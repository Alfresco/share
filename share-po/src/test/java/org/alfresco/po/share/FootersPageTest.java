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
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Footer page test covers the information about the license who, till when and what product
 * 
 * @author nshah
 */
@Listeners(FailedTestListener.class)
public class FootersPageTest extends AbstractTest
{

    public FootersPageTest()
    {
        // TODO Auto-generated constructor stub
    }

    DashBoardPage dashBoard;

    /**
     * Test process of accessing dashboard page.
     *
     * @throws Exception
     */
    @Test(groups = "EnterpriseOnly")
    public void loadDashBoard() throws Exception
    {
        dashBoard = loginAs(username, password);
        Assert.assertTrue(dashBoard.isLogoPresent());
        String copyright = dashBoard.getCopyRight();
        Assert.assertTrue(copyright.contains("Alfresco Software"));
    }

    @Test(dependsOnMethods = "loadDashBoard", groups = "EnterpriseOnly")
    public void getFooterPageTest()
    {
        String copyRightDetails = dashBoard.getCopyRightDetails();
        Assert.assertTrue(copyRightDetails.contains("2005"), "License beginig year is not correct");
        Assert.assertTrue(copyRightDetails.contains("2014"), "License ending year is not correct");
        Assert.assertTrue(dashBoard.getLicenseHolder().contains(licenseShare), "Please provide correct Licensed to");

        FootersPage footer = dashBoard.getFooter().render();
        Assert.assertTrue(footer instanceof FootersPage);
        String version = footer.getAlfrescoVersion();       
        Assert.assertTrue(version.contains("Alfresco Enterprise "), "Product is not correct.");
    }
}
