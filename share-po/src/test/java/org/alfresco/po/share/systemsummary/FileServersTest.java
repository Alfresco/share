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

package org.alfresco.po.share.systemsummary;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by olga.lokhach
 */

@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class FileServersTest extends AbstractTest
{

    private FileServersPage fileServersPage;
    static final String port = "2121";

    @Test
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) shareUtil.navigateToSystemSummary(driver, shareUrl, username, password);
        fileServersPage = sysSummaryPage.openConsolePage(AdminConsoleLink.FileServers).render();
        assertNotNull(fileServersPage);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnFileServersPagePO()
    {
        fileServersPage = resolvePage(driver).render();
        assertNotNull(fileServersPage);
    }

    @Test(dependsOnMethods = "checkDroneReturnFileServersPagePO")
    public void checkEditFtpPort()
    {
        fileServersPage = resolvePage(driver).render();
        fileServersPage.configFtpPort(port);
        assertTrue(fileServersPage.getPort().equals(port));
    }

    @Test(dependsOnMethods = "checkEditFtpPort")
    public void canSelectFtpEnabledCheckbox()
    {
        fileServersPage = resolvePage(driver).render();
        fileServersPage.selectFtpEnabledCheckbox();
        assertFalse(fileServersPage.isFtpEnabledSelected());
        fileServersPage.selectFtpEnabledCheckbox();
        assertTrue(fileServersPage.isFtpEnabledSelected());
    }
}
