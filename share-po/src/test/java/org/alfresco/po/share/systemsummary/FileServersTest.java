
package org.alfresco.po.share.systemsummary;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by olga.lokhach
 */

@Listeners(FailedTestListener.class)
@Test(groups = "Enterprise-only")
public class FileServersTest extends AbstractTest
{

    private FileServersPage fileServersPage;
    static final String port = "2121";

    @Test
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) shareUtil.navigateToSystemSummary(driver, shareUrl, username, password);
        fileServersPage = sysSummaryPage.openConsolePage(AdminConsoleLink.FileServers).render();
        assertNotNull(fileServersPage);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnFileServersPagePO()
    {
        fileServersPage = resolvePage(driver).render();
        assertNotNull(fileServersPage);
    }

    @Test(dependsOnMethods = "checkDroneReturnFileServersPagePO")
    public void checkEditFtpPort()
    {
        fileServersPage = resolvePage(driver).render();
        fileServersPage.configFtpPort(port);
        assertTrue(fileServersPage.getPort().equals(port));
    }

    @Test(dependsOnMethods = "checkEditFtpPort")
    public void canSelectFtpEnabledCheckbox()
    {
        fileServersPage = resolvePage(driver).render();
        fileServersPage.selectFtpEnabledCheckbox();
        assertFalse(fileServersPage.isFtpEnabledSelected());
        fileServersPage.selectFtpEnabledCheckbox();
        assertTrue(fileServersPage.isFtpEnabledSelected());
    }
}
