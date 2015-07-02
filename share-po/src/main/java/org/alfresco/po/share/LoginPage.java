/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import java.io.IOException;

import org.alfresco.po.share.user.CloudForgotPasswordPage;
import org.alfresco.po.share.user.Language;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Login Page object that holds all information and methods that can be found on
 * the login page.
 *
 * @author Michael Suzuki
 * @since 1.0
 */
public class LoginPage extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    @RenderWebElement
    private static final By PASSWORD_INPUT = By.cssSelector("input[id$='password']");
    @RenderWebElement
    private static final By USERNAME_INPUT = By.cssSelector("input[id$='username']");
    @RenderWebElement
    private static final By SUBMIT_BTN = By.cssSelector("button[id$='submit-button']");
    
    private static final By LOGO = By.cssSelector(".theme-company-logo");
    private static final By LANGUAGE_SELECT = By.cssSelector("select[id$='default-language']");
    private static final By SIGN_UP_LINK = By.cssSelector("a.theme-color-1:first-of-type");
    private static final By FORGOT_PASSWORD_LINK = By.cssSelector("a[href$='forgot-password']");
    

    /**
     * Constructor.
     */
    public LoginPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage render(RenderTime timer)
    {
        basicRender(timer);
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if login html element div is displayed.
     *
     * @return true if panel is displayed
     */
    public boolean exists()
    {
        return panelExists("div.login-logo");
    }

    /**
     * Logs user into the site by first finding the login panel,populating the
     * fields and submitting the form.
     *
     * @param username String username value
     * @param password String password value
     */
    public void loginAs(final String username, final String password)
    {
        if (username == null || password == null)
        {
            throw new IllegalArgumentException("Input param can not be null");
        }
        WebElement usernameInput = drone.find(USERNAME_INPUT);
        usernameInput.click();
        usernameInput.clear();
        usernameInput.sendKeys(username);
        
        WebElement button = drone.find(By.cssSelector("button[id$='submit-button']"));
        
        WebElement passwordInput = drone.find(PASSWORD_INPUT);
        passwordInput.click();
        passwordInput.clear();
        passwordInput.sendKeys(password);

        String usernameEntered = usernameInput.getAttribute("value");
        String passwordEntered = passwordInput.getAttribute("value");
        logger.info("Values entered: User[" + usernameEntered + "], Password[" + passwordEntered + "]");
        if (!username.equals(usernameEntered))
        {
            drone.clearAndType(USERNAME_INPUT, username);
        }
        if (!password.equals(passwordEntered))
        {
            drone.clearAndType(PASSWORD_INPUT, password);
        }
        button.submit();
        logger.info("User[" + usernameEntered + "] loggedIn.");
    }

    /**
     * Verify if error message is displayed.
     *
     * @return true if div.bd is displayed
     */
    public boolean hasErrorMessage()
    {
        try
        {
            if (alfrescoVersion.isCloud() || isDojoSupport())
            {
                return drone.find(By.cssSelector("div.error")).isDisplayed();
            }
            return drone.find(By.cssSelector("div.bd")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public String getErrorMessage()
    {
        if (alfrescoVersion.isCloud() || isDojoSupport())
        {
            return drone.find(By.cssSelector("div.error")).getText();
        }
        return drone.find(By.cssSelector("div.bd")).getText();
    }

    /**
     * Return url for Logo Image.
     *
     * @return String
     */
    public String getLogoUrl()
    {
        return drone.findAndWait(LOGO).getCssValue("background-image").replace("url(\"", "").replace("\")", "");
    }

    public void loginAs(final String username, final String password, final Language language)
    {       
        if (username == null || password == null)
        {
            throw new IllegalArgumentException("Input param can not be null");
        }

        boolean languageSelect = drone.findAndWait(LANGUAGE_SELECT).isDisplayed();
        if (languageSelect)
        {
            changeLanguage(language);
        }
        loginAs(username, password);
    }

    public LoginPage changeLanguage(Language language)
    {
        Select languageDropDown = new Select(drone.findAndWait(LANGUAGE_SELECT));
        languageDropDown.selectByValue(language.getLanguageValue());
        return render();
    }

    /**
     * Performs the action of clicking the sign up link.
     */
    public void selectSignUpLink()
    {
        drone.findAndWait(SIGN_UP_LINK).click();
    }

    /**
     * Performs the action of clicking the Forgot password link.
     */
    public CloudForgotPasswordPage selectFogotPassordLink()
    {
        //CloudForgotPasswordPage forgotPassPage=null;
        
        drone.findAndWait(FORGOT_PASSWORD_LINK).click();
             
        return new CloudForgotPasswordPage(drone);
    }

    /**
     * Method to get Forgot password link URL
     * 
     * @return String
     */
    public String getForgotPasswordURL()
    {

        return drone.findAndWait(FORGOT_PASSWORD_LINK).getAttribute("href");

    }

    public HtmlPage loginWithPost(String shareUrl, String userName, String password)
    {
        HttpClient client = new HttpClient();

        //login
        PostMethod post = new PostMethod((new StringBuilder()).append(shareUrl).append("/page/dologin").toString());
        NameValuePair[] formParams = (new NameValuePair[]{
                new org.apache.commons.httpclient.NameValuePair("username", userName),
                new org.apache.commons.httpclient.NameValuePair("password", password),
                new org.apache.commons.httpclient.NameValuePair("success", "/share/page/site-index"),
                new org.apache.commons.httpclient.NameValuePair("failure", "/share/page/type/login?error=true")
        });
        post.setRequestBody(formParams);
        post.addRequestHeader("Accept-Language", "en-us,en;q=0.5");
        try
        {
            client.executeMethod(post);

            HttpState state = client.getState();

            //add cookies to browser and navigate to user dashboard
            WebDriver driver = ((WebDroneImpl) drone).getDriver();
            drone.navigateTo(shareUrl + "/page/user/" + userName + "/dashboard");
            driver.manage().addCookie(new Cookie(state.getCookies()[0].getName(),state.getCookies()[0].getValue()));
            drone.refresh();

        }
        catch (IOException e)
        {
            logger.error("Login error ", e);
        }
        finally
        {
            post.releaseConnection();
        }

        return FactorySharePage.resolvePage(drone);

    }

}
