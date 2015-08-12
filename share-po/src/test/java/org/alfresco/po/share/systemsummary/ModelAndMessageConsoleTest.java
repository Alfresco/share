package org.alfresco.po.share.systemsummary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by olga.lokhach
 */
@Listeners(FailedTestListener.class)
public class ModelAndMessageConsoleTest extends AbstractTest
{
       private ModelAndMessagesConsole modelAndMessagesConsole;

        @Test(groups = "Enterprise-only")
        public void checkOpenPage()
        {
            SystemSummaryPage sysSummaryPage = (SystemSummaryPage) shareUtil.navigateToSystemSummary(driver, shareUrl, username, password);
            modelAndMessagesConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.RepoConsole).render();
            assertNotNull(modelAndMessagesConsole);
        }

        @Test(dependsOnMethods = "checkOpenPage")
        public void checkDroneReturnModelAndMessagePagePO()
        {
            modelAndMessagesConsole = resolvePage(driver).render();
            assertNotNull(modelAndMessagesConsole);
        }

        @Test(dependsOnMethods = "checkDroneReturnModelAndMessagePagePO")
        public void checkCanSendCommand()
        {
            modelAndMessagesConsole.sendCommand("help");
            assertEquals(modelAndMessagesConsole.findText().contains("List this help"), true);
        }


    }
