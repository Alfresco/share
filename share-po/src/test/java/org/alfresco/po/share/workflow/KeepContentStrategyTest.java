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
