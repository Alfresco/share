/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.user;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareUtil;
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
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }

        DashBoardPage dashboardPage = FactorySharePage.resolvePage(drone).render();
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
