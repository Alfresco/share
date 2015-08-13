package org.alfresco.po.share.steps;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DashBoardActionsTest extends AbstractTest
{
    @Autowired DashBoardActions dashBoaradActions;

    @BeforeClass(groups = "Enterprise-only")
    public void setup() throws Exception
    {
        loginAs("admin", "admin");
    }
    
    @Test(groups = "Enterprise-only", priority=1)
    public void testopenUserDashBoard() throws Exception
    {
            DashBoardPage dashBoard = dashBoaradActions.openUserDashboard(driver);
            Assert.assertNotNull(dashBoard);
    }
}
