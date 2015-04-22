/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.steps;

/**
 * Test Class to test CommonActions > utils
 * 
 * @author mbhave
 */

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CommonActionsTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(CommonActionsTest.class);
    
    private CommonActions actions = new CommonActions();

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs("admin", "admin");
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testCheckIfDriverNull() throws Exception
    {
        try
        {
            actions.checkIfDriverIsNull(null);
        }
        catch(UnsupportedOperationException e)
        {
            Assert.assertTrue(e.getMessage().contains("WebDrone is required"));
        }
    }
    
    @Test(groups = "Enterprise-only", priority=2)
    public void testCheckIfDriverNotNull() throws Exception
    {
        actions.checkIfDriverIsNull(drone);
    }

    @Test(groups = "Enterprise-only", priority=3)
    public void testRefreshSharePage() throws Exception
    {
            SharePage page = drone.getCurrentPage().render();
            SharePage pageRefreshed = actions.refreshSharePage(drone).render();
            Assert.assertTrue(page.getClass() == pageRefreshed.getClass());
            Assert.assertTrue(page != pageRefreshed);
    }
    
    @Test(groups = "Enterprise-only", priority=4)
    public void testsWebDriverWait() throws Exception
    {
        long startTime = System.currentTimeMillis();
        long waitDuration = 7000;
        
        logger.info("Start Time: " + startTime);
        
        actions.webDriverWait(drone, waitDuration);
        
        long endTime = System.currentTimeMillis();
        Assert.assertTrue(endTime >= startTime + waitDuration);
        
        logger.info("End Time: " + endTime);
    }
}
