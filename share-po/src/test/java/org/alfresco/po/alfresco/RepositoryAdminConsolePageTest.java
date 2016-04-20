
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
