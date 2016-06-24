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

package org.alfresco.po.alfresco;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.openqa.selenium.By;

/**
 * Created by ivan.kornilov on 22.04.2014.
 */
public class LoginAlfrescoPage extends SharePage
{
    private final By userName = By.xpath("//input[@id='loginForm:user-name']");
    private final By password = By.xpath("//input[@id='loginForm:user-password']");
    private final By loginButton = By.xpath("//input[@id='loginForm:submit']");

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

    /**
     * Method to return Alfresco Explorer URL
     *
     * @param shareUrl String
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
     * @param userName String
     */
    public void inputUserName(String userName)
    {
        findAndWait(this.userName).clear();
        findAndWait(this.userName, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(userName);
    }

    /**
     * Method for input password
     *
     * @param password String
     */
    public void inputPassword(String password)
    {
        findAndWait(this.password).clear();
        findAndWait(this.password, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(password);
    }

    /**
     * Method for click Login Button
     */

    public void clickLoginButton()
    {
        findAndWait(loginButton, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).click();
    }

    /**
     * Method for login to Alfresco Explorer
     *
     * @param userName String
     * @param password String
     * @return My Alfresco Page
     */
    public HtmlPage login(String userName, String password)
    {
        try
        {
            inputUserName(userName);
            inputPassword(password);
            clickLoginButton();
            return factoryPage.instantiatePage(driver, MyAlfrescoPage.class);

        }
        catch (UnsupportedOperationException uso)
        {
            throw new UnsupportedOperationException("Can not navigate to MyAlfresco Page");
        }
    }

    /**
     * Method to verify Login Dashboard is opened
     *
     * @return boolean
     */
    public boolean isOpened()
    {
        return findAndWait(By.xpath("//span[@class='mainSubTitle' and text()='Enter Login details:']")).isDisplayed();
    }

    @Override
    public String toString()
    {
        return "Login Dashboard";
    }

}
