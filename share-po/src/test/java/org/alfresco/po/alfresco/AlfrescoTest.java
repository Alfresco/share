
package org.alfresco.po.alfresco;

import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Created by ivan.kornilov on 23.04.2014.
 */
@Listeners(FailedTestListener.class)
public class AlfrescoTest extends AbstractTest
{

    @Test(groups = "Enterprise-only")
    public void checkWebScriptsPage() throws Exception
    {
        loginAs(username, password).render();
        WebScriptsPage webScriptsPage = shareUtil.navigateToWebScriptsHome(driver, username, password).render();
        WebScriptsMaintenancePage webScriptsMaintenancePage = webScriptsPage.clickRefresh().render();
        assertTrue(webScriptsMaintenancePage.isOpened());
    }

}
