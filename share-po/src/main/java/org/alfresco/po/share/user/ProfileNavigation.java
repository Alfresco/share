/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.user;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ChangePasswordPage;
import org.alfresco.po.share.FactoryPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

/**
 * Represent elements found on the HTML page relating to the profile navigation
 * bar
 * 
 * @author Abhijeet Bharade
 * @since 1.7.1
 */
public class ProfileNavigation extends PageElement
{
    private static final By TRASHCAN_LINK = By.cssSelector("div>a[href='user-trashcan']");
    private static final By LANGUAGE_LINK = By.cssSelector("div>a[href='change-locale']");
    private static final By NOTIFICATIONS_LINK = By.cssSelector("div>a[href='user-notifications']");
    private static final By SITES_LINK = By.cssSelector("div>a[href='user-sites']");
    private static final By CONTENT_LINK = By.cssSelector("div>a[href='user-content']");
    private static final By FOLLOWING_LINK = By.cssSelector("div>a[href='following']");
    private static final By FOLLOWERS_LINK = By.cssSelector("div>a[href='followers']");
    private static final By CHANGE_PASSWORD_LINK = By.cssSelector("div>a[href='change-password']");
    /**
     * Constructor
     * 
     * @param driver WebDriver browser client
     */
    public ProfileNavigation(WebDriver driver, FactoryPage factoryPage)
    {
        this.driver = driver;
        this.factoryPage = factoryPage;
    }


    /**
     * Click on the trashcan link
     * 
     * @return - {@link TrashCanPage}
     * <br/><br/>author sprasanna
     */
    public TrashCanPage selectTrashCan()
    {
        driver.findElement(TRASHCAN_LINK).click();
        return getCurrentPage().render();
    }

    /**
     * Method to select Language link
     * 
     * @return <br/><br/>
     */
    public LanguageSettingsPage selectLanguage()
    {
        try
        {
            driver.findElement(LANGUAGE_LINK).click();
            return factoryPage.instantiatePage(driver, LanguageSettingsPage.class);
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Language link" + nse);
        }
    }

    /**
     * Click on the Notifications link
     * 
     * @return - {@link NotificationPage}
     * <br/><br/>author sprasanna
     */
    public NotificationPage selectNotification()
    {
        driver.findElement(NOTIFICATIONS_LINK).click();
        return factoryPage.instantiatePage(driver, NotificationPage.class);
    }

    /**
     * Click on the Sites link
     * 
     * @return - {@link UserSitesPage}
     * <br/><br/>author sprasanna
     */
    public UserSitesPage selectSites()
    {
        driver.findElement(SITES_LINK).click();
        return factoryPage.instantiatePage(driver, UserSitesPage.class);
    }
    
    /**
     * Click on the Content link
     * 
     * @return - {@link UserContentPage}
     * <br/><br/>author bogdan - 30.06.2014
     */
    public UserContentPage selectContent()
    {
        driver.findElement(CONTENT_LINK).click();
        return factoryPage.instantiatePage(driver,UserContentPage.class);
    }

    /**
     * Click on the I'm Following link
     *
     * @return - {@link FollowingPage}
     *
     */

    public FollowingPage selectFollowing()
    {
        driver.findElement(FOLLOWING_LINK).click();
        return factoryPage.instantiatePage(driver, FollowingPage.class);
    }

    /**
     * Click on the Following Me link
     *
     * @return - {@link FollowersPage}
     *
     */

    public FollowersPage selectFollowers()
    {
        driver.findElement(FOLLOWERS_LINK).click();
        return factoryPage.instantiatePage(driver, FollowersPage.class);
    }

    /**
     * Click on the Following Me link
     *
     * @return - {@link FollowersPage}
     *
     */

    public ChangePasswordPage selectChangePassword()
    {
        driver.findElement(CHANGE_PASSWORD_LINK).click();
        return factoryPage.instantiatePage(driver, ChangePasswordPage.class);
    }

}
