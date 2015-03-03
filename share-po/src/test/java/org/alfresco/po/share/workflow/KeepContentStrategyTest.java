/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.workflow;


import static org.alfresco.po.share.workflow.KeepContentStrategy.getKeepContentStrategy;
import static org.alfresco.po.share.workflow.KeepContentStrategy.DELETECONTENT;
import static org.alfresco.po.share.workflow.KeepContentStrategy.KEEPCONTENT;
import static org.alfresco.po.share.workflow.KeepContentStrategy.KEEPCONTENTREMOVESYNC;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class KeepContentStrategyTest
{
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getKeepContentStrategyWithNull()
    {
        getKeepContentStrategy(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getKeepContentStrategyWithEmpty()
    {
        getKeepContentStrategy("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid Keep Content Strategy Value : Alfresco")
    public void getCurrentTaskTypeWithAlfresco()
    {
        getKeepContentStrategy("Alfresco");
    }
    
    @Test
    public void getKeepContentStrategyTest()
    {
        assertEquals(getKeepContentStrategy("Keep content synced on cloud"), KEEPCONTENT);
        assertEquals(getKeepContentStrategy("Keep content on cloud and remove sync"), KEEPCONTENTREMOVESYNC);
        assertEquals(getKeepContentStrategy("Delete content on cloud and remove sync"), DELETECONTENT);
    }
    
    @Test
    public void getStrategy()
    {
        assertEquals(KEEPCONTENT.getStrategy(), "Keep content synced on cloud");
        assertEquals(KEEPCONTENTREMOVESYNC.getStrategy(), "Keep content on cloud and remove sync");
        assertEquals(DELETECONTENT.getStrategy(), "Delete content on cloud and remove sync");
    }

}
