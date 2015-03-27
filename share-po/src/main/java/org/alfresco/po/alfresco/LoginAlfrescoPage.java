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

package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by ivan.kornilov on 22.04.2014.
 */
public class LoginAlfrescoPage extends SharePage
{
    private final By userName = By.xpath("//input[@id='loginForm:user-name']");
    private final By password = By.xpath("//input[@id='loginForm:user-password']");
    private final By loginButton = By.xpath("//input[@id='loginForm:submit']");

    public LoginAlfrescoPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginAlfrescoPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(userName),
            getVisibleRenderElement(password),
            getVisibleRenderElement(loginButton));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginAlfrescoPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginAlfrescoPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to return Alfresco Explorer URL
     *
     * @param shareUrl
     * @return String
     */
    public static String getAlfrescoURL(String shareUrl)
    {
        String alfrescoUrl = PageUtils.getProtocol(shareUrl) + PageUtils.getAddress(shareUrl) + "/alfresco/faces/jsp/login.jsp";
        return alfrescoUrl;
    }

    /**
     * Method for input username
     *
     * @param userName
     * @return
     */
    public void inputUserName(String userName)
    {
        drone.findAndWait(this.userName).clear();
        drone.findAndWait(this.userName, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(userName);
    }

    /**
     * Method for input password
     *
     * @param password
     * @return
     */
    public void inputPassword(String password)
    {
        drone.findAndWait(this.password).clear();
        drone.findAndWait(this.password, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(password);
    }

    /**
     * Method for click Login Button
     *
     * @param
     * @return
     */

    public void clickLoginButton()
    {
        drone.findAndWait(loginButton, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).click();
    }

    /**
     * Method for login to Alfresco Explorer
     *
     * @param userName
     * @param password
     * @return My Alfresco Page
     */
    public MyAlfrescoPage login(String userName, String password)
    {
        try
        {
            inputUserName(userName);
            inputPassword(password);
            clickLoginButton();
            return new MyAlfrescoPage(drone);

        }
        catch (UnsupportedOperationException uso)
        {
            throw new UnsupportedOperationException("Can not navigate to MyAlfresco Page");
        }
    }

    /**
     * Method to verify Login Dashboard is opened
     *
     * @param
     * @return boolean
     */
    public boolean isOpened()
    {
        return drone.findAndWait(By.xpath("//span[@class='mainSubTitle' and text()='Enter Login details:']")).isDisplayed();
    }

    @Override
    public String toString()
    {
        return "Login Dashboard";
    }

}
