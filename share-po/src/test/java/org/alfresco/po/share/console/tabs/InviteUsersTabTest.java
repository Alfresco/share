package org.alfresco.po.share.console.tabs;

import java.io.File;
import java.util.Map;

import org.alfresco.po.share.console.AbstractCloudConsoleTest;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test to validate the Cloud Console Pages.
 *
 * @author Dmitry Yukhnovets
 * @since 1.7
 */
@Listeners(FailedTestListener.class)
public class InviteUsersTabTest extends AbstractCloudConsoleTest {
    private InviteUsersTab inviteUsersTab;

    @BeforeMethod(groups = {"Cloud2"})
    public void beforeTest() throws Exception {
        cloudConsolePage.openCloudConsole(consoleUrl);
        if (cloudConsolePage.isLoggedToCloudConsole()) {
            cloudConsolePage = cloudConsolePage.logOutFromCloudConsole().render();
        }
        inviteUsersTab = cloudConsolePage.openCloudConsole(consoleUrl).render()
                .loginAs(USERNAME, PASSWORD).render()
                .openDashboardPage().render()
                .openInviteUsersTab().render();
    }

    @Test(groups = {"Cloud2"})
    public void InviteBulkUsersTest() {
        String user1 = System.currentTimeMillis() + "@test.drtest";
        String user2 = System.currentTimeMillis() + "@test2.drtest";
        String[] usersForInvitation = {user1, user2};
        Map<String, Boolean> results = inviteUsersTab.executeCorrectBulkImport(usersForInvitation);

        Assert.assertTrue(results.containsKey(user1) && results.get(user1));
        Assert.assertTrue(results.containsKey(user2) && results.get(user2));
    }

    @Test(groups = {"Cloud2"})
    public void InviteUsersViaFileTest() {
        String user1 = System.currentTimeMillis() + "@t1.drtest";
        String user2 = System.currentTimeMillis() + "@t2.drtest";
        String usersForInvitation = user1 + "\r\n" + user2;
        File fileForBulkImport = SiteUtil.prepareFile(testName, usersForInvitation);
        Map<String, Boolean> results = inviteUsersTab.executeCorrectBulkImport(fileForBulkImport);

        Assert.assertTrue(results.containsKey(user1) && results.get(user1));
        Assert.assertTrue(results.containsKey(user2) && results.get(user2));
    }
}
