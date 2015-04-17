package org.alfresco.po.share.steps;

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
