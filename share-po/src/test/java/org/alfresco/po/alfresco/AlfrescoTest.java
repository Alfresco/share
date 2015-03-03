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

package org.alfresco.po.alfresco;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by ivan.kornilov on 23.04.2014.
 */
@Listeners(FailedTestListener.class)
public class AlfrescoTest extends AbstractTest
{

    @Test(groups = "Enterprise-only")
    public void checkWebScriptsPage() throws Exception
    {
        loginAs(username, password).render();
        WebScriptsPage webScriptsPage = ShareUtil.navigateToWebScriptsHome(drone, username, password).render();
        WebScriptsMaintenancePage webScriptsMaintenancePage = webScriptsPage.clickRefresh();
        assertTrue(webScriptsMaintenancePage.isOpened());
    }

}
