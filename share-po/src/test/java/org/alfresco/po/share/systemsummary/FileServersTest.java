/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.share.systemsummary;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by olga.lokhach
 */

@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class FileServersTest extends AbstractTest
{

    private static Log logger = LogFactory.getLog(FileServersPage.class);
    private FileServersPage fileServersPage;
    static final String port = "2121";

    @Test
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) ShareUtil.navigateToSystemSummary(drone, shareUrl, username, password);
        fileServersPage = sysSummaryPage.openConsolePage(AdminConsoleLink.FileServers).render();
        assertNotNull(fileServersPage);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnFileServersPagePO()
    {
        fileServersPage = drone.getCurrentPage().render();
        assertNotNull(fileServersPage);
    }

    @Test(dependsOnMethods = "checkDroneReturnFileServersPagePO")
    public void checkEditFtpPort()
    {
        fileServersPage = drone.getCurrentPage().render();
        fileServersPage.configFtpPort(port);
        assertTrue(fileServersPage.getPort().equals(port));
    }

    @Test(dependsOnMethods = "checkEditFtpPort")
    public void canSelectFtpEnabledCheckbox()
    {
        fileServersPage = drone.getCurrentPage().render();
        fileServersPage.selectFtpEnabledCheckbox();
        assertFalse(fileServersPage.isFtpEnabledSelected());
        fileServersPage.selectFtpEnabledCheckbox();
        assertTrue(fileServersPage.isFtpEnabledSelected());
    }
}
