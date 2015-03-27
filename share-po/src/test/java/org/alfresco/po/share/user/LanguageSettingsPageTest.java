/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * LanguageSettingsPage Test
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
@Listeners(FailedTestListener.class)
public class LanguageSettingsPageTest extends AbstractTest
{
    DashBoardPage dashboardPage;
    MyProfilePage myprofile;
    LanguageSettingsPage languageSettingsPage;

   
    /**
     * Pre test to create a content with properties set.
     * 
     * @throws Exception
     */
    @BeforeClass(groups = { "Cloud" })
    public void prepare() throws Exception
    {
        dashboardPage = loginAs(cloudUserName, cloudUserPassword);
        myprofile = dashboardPage.getNav().selectMyProfile().render();
        languageSettingsPage = myprofile.getProfileNav().selectLanguage().render();
    }

//
//    private Map<BrowserPreference, Object> getCustomPreferences()
//    {
//        Map<BrowserPreference, Object> customProfile = new HashMap<BrowserPreference, Object>();
//        customProfile.put(BrowserPreference.Language, Language.JAPANESE);
//        return customProfile;
//    }

    @AfterClass(groups = { "Cloud" })
    public void tearDown()
    {

    }


    @Test(groups = { "Cloud" })
    public void testSetLanguage()
    {
        languageSettingsPage.changeLanguage(Language.FRENCH);
        Assert.assertTrue(languageSettingsPage.isLanguageSelected(Language.FRENCH));

        languageSettingsPage.changeLanguage(Language.DEUTSCHE);
        Assert.assertEquals(languageSettingsPage.getSelectedLanguage(), Language.DEUTSCHE);

        languageSettingsPage.changeLanguage(Language.ENGLISH_US);

    }


}
