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


import org.alfresco.po.AbstractTest;

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
public class RepositoryAdminConsolePageTest extends AbstractTest
{
    RepositoryAdminConsolePage page;
    @BeforeClass
    public void setup() throws Exception
    {
        loginAs(username, password).render();
        page = shareUtil.navigateToRepositoryAdminConsole(driver, username, password).render();
    }
    @Test(groups = "Enterprise-only")
    public void create() throws Exception
    {
        RepositoryAdminConsolePage p = new RepositoryAdminConsolePage();
        Assert.assertNotNull(p);
    }

}
