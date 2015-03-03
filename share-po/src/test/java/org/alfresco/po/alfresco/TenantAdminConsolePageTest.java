/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.po.alfresco;


import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
/**
 * TenantAdminConsolePageTest page object test.
 * 
 * @author Michael Suzuki
 * @since 5.0
 */
public class TenantAdminConsolePageTest extends AbstractTest
{
    TenantAdminConsolePage page;
    @BeforeClass
    public void setup() throws Exception
    {
        loginAs(username, password).render();
        page = ShareUtil.navigateToTenantAdminConsole(drone, username, password).render();
    }
    @Test(groups = "Enterprise-only")
    public void create() throws Exception
    {
        TenantAdminConsolePage tacp = new TenantAdminConsolePage(drone);
        Assert.assertNotNull(tacp);
    }
    @Test(expectedExceptions = IllegalArgumentException.class, groups = "Enterprise-only")
    public void createWithNull() throws Exception
    {
        new TenantAdminConsolePage(null);
    }
    @Test(groups = "Enterprise-only")
    public void createTenant() throws Exception
    {
        String tenantName = "mike" + System.currentTimeMillis();
        page.createTenant(tenantName, "password").render();
        String expected = String.format("created tenant: %s", tenantName);
        String result = page.getResult();
        Assert.assertEquals(result,expected);
    }

}
