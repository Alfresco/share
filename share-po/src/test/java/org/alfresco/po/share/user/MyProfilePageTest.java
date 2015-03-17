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

import org.alfresco.po.share.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/**
 * @author Aliaksei Boole
 */
public class MyProfilePageTest extends AbstractTest
{
    private MyProfilePage myProfilePage;
    private EditProfilePage editProfilePage;
    private String userName;

    @BeforeClass(groups = { "alfresco-one" }, alwaysRun = true)
    public void prepare() throws Exception
    {
        userName = "User_" + System.currentTimeMillis();
        AlfrescoVersion version = drone.getProperties().getVersion();
        if (version.isCloud())
        {
            ShareUtil.loginAs(drone, shareUrl, username, password).render();
        }
        else
        {
            createEnterpriseUser(userName);
            ShareUtil.loginAs(drone, shareUrl, userName, UNAME_PASSWORD).render();
        }

        DashBoardPage dashboardPage = FactorySharePage.resolvePage(drone).render();
        myProfilePage = dashboardPage.getNav().selectMyProfile().render();
    }

    public File createTemporaryImg()
    {
        File jpgFile = null;
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.drawString("TEST", 20, 30);
        try
        {
            jpgFile = File.createTempFile("test", ".jpg");
            ImageIO.write(image, "jpg", jpgFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return jpgFile;
    }

    @Test(groups = { "alfresco-one" })
    public void openEditProfilePage()
    {
        editProfilePage = myProfilePage.openEditProfilePage();
        editProfilePage.render();
    }

    @Test(groups = { "alfresco-one" }, dependsOnMethods = "openEditProfilePage")
    public void uploadNewAvatar()
    {
        File file = createTemporaryImg();
        editProfilePage.uploadAvatar(file);
        file.delete();
    }

    @Test(groups = { "alfresco-one" }, dependsOnMethods = "uploadNewAvatar")
    public void closeEditProfilePage()
    {
        editProfilePage = myProfilePage.openEditProfilePage();
        myProfilePage = editProfilePage.clickCancel();
        myProfilePage.render();
    }

    @Test(groups = { "alfresco-one" }, dependsOnMethods = "closeEditProfilePage")
    public void editLastName()
    {
        editProfilePage = myProfilePage.openEditProfilePage();
        myProfilePage = editProfilePage.editLastName("edited");
        assertTrue(myProfilePage.getUserName().endsWith("edited"), "New last name isn't displayed");
    }


}
