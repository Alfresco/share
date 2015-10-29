package org.alfresco.po.share.steps;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DashBoardActionsTest extends AbstractTest
{

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs("admin", "admin");
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testopenUserDashBoard() throws Exception
    {
            DashBoardPage dashBoard = cmmActions.openUserDashboard(driver);
            Assert.assertNotNull(dashBoard);
    }
}
