package org.alfresco.po.share.systemsummary;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Olga Antonik
 */
@Listeners(FailedTestListener.class)
public class TenantConsoleTest  extends AbstractTest
{


    private TenantConsole tenantConsole;

    @Test(groups = "Enterprise-only")
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) shareUtil.navigateToSystemSummary(driver, shareUrl, username, password);
        tenantConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.TenantAdminConsole).render();
        assertNotNull(tenantConsole);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnTenantPagePO()
    {
        tenantConsole = resolvePage(driver).render();
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
