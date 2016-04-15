
package org.alfresco.po.share.user;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * NotificationPage Test
 * 
 * @author Jamie Allison
 * @since 4.3
 */
@Listeners(FailedTestListener.class)
public class NotificationPageTest extends AbstractTest
{
    private MyProfilePage myprofile;
    private NotificationPage notificationPage;

    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "alfresco-one" })
    public void prepare() throws Exception
    {
        shareUtil.loginAs(driver, shareUrl, username, password).render();
        DashBoardPage dashboardPage = factoryPage.getPage(driver).render();
        myprofile = dashboardPage.getNav().selectMyProfile().render();
        notificationPage = myprofile.getProfileNav().selectNotification().render();
    }

    @AfterClass(groups = { "alfresco-one" })
    public void tearDown()
    {

    }

    @Test(groups = { "alfresco-one" })
    public void isOkButtonDisplayed()
    {
        assertTrue(notificationPage.isOkButtonDisplayed());
    }

    @Test(dependsOnMethods = "isOkButtonDisplayed", groups = { "alfresco-one" })
    public void isCancelButtonDisplayed()
    {
        assertTrue(notificationPage.isCancelButtonDisplayed());
    }

    @Test(dependsOnMethods = "isCancelButtonDisplayed", groups = { "alfresco-one" })
    public void toggleNotificationFeed()
    {
        notificationPage.toggleNotificationFeed(true);
        assertTrue(notificationPage.isNotificationFeedChecked());

        notificationPage.toggleNotificationFeed(false);
        assertFalse(notificationPage.isNotificationFeedChecked());
    }

    @Test(dependsOnMethods = "toggleNotificationFeed", groups = { "alfresco-one" })
    public void selectOk()
    {
        notificationPage.toggleNotificationFeed(false);
        myprofile = notificationPage.selectOk().render();

        notificationPage = myprofile.getProfileNav().selectNotification().render();
        assertFalse(notificationPage.isNotificationFeedChecked());

        notificationPage.toggleNotificationFeed(true);
        myprofile = notificationPage.selectOk().render();

        notificationPage = myprofile.getProfileNav().selectNotification().render();
        assertTrue(notificationPage.isNotificationFeedChecked());
    }

    @Test(dependsOnMethods = "selectOk", groups = { "alfresco-one" })
    public void selectCancel()
    {
        notificationPage.toggleNotificationFeed(false);
        myprofile = notificationPage.selectCancel().render();

        notificationPage = myprofile.getProfileNav().selectNotification().render();
        assertTrue(notificationPage.isNotificationFeedChecked());
    }
}
