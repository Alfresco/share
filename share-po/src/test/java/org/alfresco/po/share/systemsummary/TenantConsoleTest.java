package org.alfresco.po.share.systemsummary;

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
 * @author Olga Antonik
 */
@Listeners(FailedTestListener.class)
public class TenantConsoleTest  extends AbstractTest
{

    private static Log logger = LogFactory.getLog(TenantConsoleTest.class);

    private TenantConsole tenantConsole;

    @Test(groups = "Enterprise-only")
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) ShareUtil.navigateToSystemSummary(drone, shareUrl, username, password);
        tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
        assertNotNull(tenantConsole);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnTenantPagePO()
    {
        tenantConsole = drone.getCurrentPage().render();
        assertNotNull(tenantConsole);
    }

    @Test(dependsOnMethods = "checkDroneReturnTenantPagePO")
    public void checkCreationTenant()
    {
        String tenant = "tenant" + System.currentTimeMillis();
        tenantConsole.createTenant(tenant, password);
        assertTrue(tenantConsole.findText().contains("created tenant: " + tenant));

    }



}
