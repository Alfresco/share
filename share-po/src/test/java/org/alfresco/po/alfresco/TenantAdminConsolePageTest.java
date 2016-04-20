
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
public class TenantAdminConsolePageTest extends AbstractTest
{
    TenantAdminConsolePage page;
    @BeforeClass
    public void setup() throws Exception
    {
        loginAs(username, password).render();
        page = shareUtil.navigateToTenantAdminConsole(driver, username, password).render();
    }
    @Test(groups = "Enterprise-only")
    public void create() throws Exception
    {
        TenantAdminConsolePage tacp = new TenantAdminConsolePage();
        Assert.assertNotNull(tacp);
    }
    @Test(groups = "Enterprise-only")
    public void createTenant() throws Exception
    {
        String tenantName = "mike" + System.currentTimeMillis();
        page.createTenant(tenantName, "password").render();
        String expected = String.format("created tenant: %s", tenantName);
        String result = page.getResult();
        Assert.assertEquals(result,expected);
    }

}
