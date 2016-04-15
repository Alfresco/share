
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
public class TransformationServicesPageTest extends AbstractTest
{
    private TransformationServicesPage transformationServicesPage;

    @Test
    public void checkOpenPage()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) shareUtil.navigateToSystemSummary(driver, shareUrl, username, password);
        transformationServicesPage = sysSummaryPage.openConsolePage(AdminConsoleLink.Transformations).render();
        assertNotNull(transformationServicesPage);
    }

    @Test(dependsOnMethods = "checkOpenPage")
    public void checkDroneReturnTransformationServicesPagePO()
    {
        transformationServicesPage = resolvePage(driver).render();
        assertNotNull(transformationServicesPage);
    }

    @Test(dependsOnMethods = "checkDroneReturnTransformationServicesPagePO")
    public void canSelectJODConverterEnabledCheckbox()
    {
        transformationServicesPage = resolvePage(driver).render();
        for (int i = 1; i <= 2; i++)
        {
            transformationServicesPage.selectJODConverterEnabledCheckbox();

            boolean isSelected = transformationServicesPage.isJODConverterEnabledSelected();
            if (!isSelected)
            {
                assertFalse(isSelected);
            }
            else
            {
                assertTrue(isSelected);
            }
        }
    }
}
