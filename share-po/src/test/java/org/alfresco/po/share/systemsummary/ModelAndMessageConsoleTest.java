package org.alfresco.po.share.systemsummary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by olga.lokhach
 */
@Listeners(FailedTestListener.class)
public class ModelAndMessageConsoleTest extends AbstractTest
{
       private static Log logger = LogFactory.getLog(ModelAndMessagesConsole.class);
       private ModelAndMessagesConsole modelAndMessagesConsole;

        @Test(groups = "Enterprise-only")
        public void checkOpenPage()
        {
            SystemSummaryPage sysSummaryPage = (SystemSummaryPage) ShareUtil.navigateToSystemSummary(drone, shareUrl, username, password);
            modelAndMessagesConsole = sysSummaryPage.openConsolePage(AdminConsoleLink.RepoConsole).render();
            assertNotNull(modelAndMessagesConsole);
        }

        @Test(dependsOnMethods = "checkOpenPage")
        public void checkDroneReturnModelAndMessagePagePO()
        {
            modelAndMessagesConsole = drone.getCurrentPage().render();
            assertNotNull(modelAndMessagesConsole);
        }

        @Test(dependsOnMethods = "checkDroneReturnModelAndMessagePagePO")
        public void checkCanSendCommand()
        {
            modelAndMessagesConsole.sendCommand("help");
            assertEquals(modelAndMessagesConsole.findText().contains("List this help"), true);
        }


    }
