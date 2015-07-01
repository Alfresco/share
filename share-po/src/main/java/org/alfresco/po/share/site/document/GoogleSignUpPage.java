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
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Create site page object, to do a sign up to google Account Google.
 *
 * @author Subashni Prasanna
 * @since 1.5
 */
public class GoogleSignUpPage extends SharePage
{
    private static final By GOOGLE_USERNAME = By.xpath("//input[@name='Email']");
    private static final By GOOGLE_PASSWORD = By.xpath("//input[@id='Passwd']");
    private static final By NEXT_BUTTON = By.xpath("//input[@id='next']");
    private static final By SIGNUP_BUTTON = By.xpath("//input[@id='signIn']");
    private static final String googleAccountTitle = "Google Accounts";
    private static final By MSG_SELECTOR = By.xpath("//div[contains(@class, 'bd')]/span[text()='We hit a problem opening the file in Google Docs™. Please try " +
        "again. If this happens again then please contact your Alfresco Administrator.' or text()='There was an error opening the document in Google Docs™. " +
        "If the errors occurs again please contact your System Administrator.']");


    private boolean isGoogleCreate;
    private String documentVersion;

    private static Log logger = LogFactory.getLog(GoogleSignUpPage.class);

    /**
     * Constructor and switch to the sign up window
     */
    protected GoogleSignUpPage(WebDrone drone, String documentVersion, Boolean isGoogleCreate)
    {
        super(drone);
        this.isGoogleCreate = isGoogleCreate;
        this.documentVersion = documentVersion;
    }

    /**
     * Public constructor and switch to the sign up window
     */
    public GoogleSignUpPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleSignUpPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Ensures that the 'checked out' message is visible.
     *
     * @param timer Max time to wait
     * @return {@link GoogleSignUpPage}
     */
    @SuppressWarnings("unchecked")
    @Override
    public GoogleSignUpPage render(RenderTime timer)
    {
        switchToGoogleSignIn();
        elementRender(timer, getVisibleRenderElement(GOOGLE_USERNAME), getVisibleRenderElement(NEXT_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleSignUpPage render() throws PageRenderTimeException
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if googleSignup Dialog is displayed.
     *
     * @return true if dialog is displayed.
     */
    public boolean isSignupWindowDisplayed()
    {
        try
        {
            return drone.find(GOOGLE_USERNAME).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Enter the Username , password and Click on Sign up button.
     *
     * @return EditInGoogleDocsPage
     */
    public EditInGoogleDocsPage signUp(String username, String password)
    {
        try
        {
            logger.info("Signing in to GoogleDocs");
            WebElement usernameInput = drone.findAndWait(GOOGLE_USERNAME);
            usernameInput.clear();
            usernameInput.sendKeys(username);
            
            WebElement submitButton = drone.findAndWait(NEXT_BUTTON);
            submitButton.click();

            drone.waitUntilElementPresent(GOOGLE_PASSWORD, WAIT_TIME_3000);
            WebElement passwordInput = drone.find(GOOGLE_PASSWORD);
            passwordInput.clear();
            passwordInput.sendKeys(password);           

            submitButton = drone.find(SIGNUP_BUTTON);
            submitButton.click();
            switchToShare();
            waitUntilAlert(10);
            try
            {
                WebElement theError = drone.findAndWait(MSG_SELECTOR, 10000);
                if (theError.isDisplayed())
                {
                    throw new PageOperationException("Unable to open Google Doc for Editing");
                }
            }
            catch (TimeoutException e)
            {
                return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
            }
        }
        catch (TimeoutException | NoSuchElementException te)
        {
            throw new TimeoutException("Google Sign up page timeout", te);
        }
        return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
    }

    /**
     * Enter the Username , password and Click on Sign up button.
     * use for old format doc., xls., ppt.
     *
     * @param username
     * @param password
     * @param confirmUpgrade
     * @return HtmlPage
     */
    public HtmlPage signUpOldFormat(String username, String password, boolean confirmUpgrade)
    {
        SharePopup upgrade;
        try
        {
            WebElement usernameInput = drone.findAndWait(GOOGLE_USERNAME);
            usernameInput.clear();
            usernameInput.sendKeys(username);

            WebElement passwordInput = drone.findAndWait(GOOGLE_PASSWORD);
            passwordInput.clear();
            passwordInput.sendKeys(password);

            WebElement submitButton = drone.find(SIGNUP_BUTTON);
            submitButton.click();
            switchToShare();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google Sign up page timeout", te);
        }
        waitUntilAlert();
        upgrade = drone.getCurrentPage().render();
        if (confirmUpgrade)
        {

            upgrade.clickYes();
            String message = "Editing in Google Docs";
            if (isGoogleCreate)
            {
                message = "Creating Google Docs";
            }
            drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), message, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), message, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            String errorMessage = "";
            try
            {
                errorMessage = drone.find(By.cssSelector("div.bd>span.message")).getText();
            }
            catch (NoSuchElementException e)
            {
                return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
            }
            throw new PageException(errorMessage);
        }
        else
        {
            upgrade.cancelNo();
            return drone.getCurrentPage().render();
        }
    }

    /**
     * Choose edit google doc from "Document Library" without authentication
     * use if authentication was made previously
     * use method from Document Library
     *
     * @param filename
     * @param confirmUpgrade
     * @return HtmlPage
     */
    public HtmlPage signUpOldFormatWithoutAuth(String filename, boolean confirmUpgrade)
    {
        SharePopup upgrade;
        DocumentLibraryPage docLibPage = drone.getCurrentPage().render();
        docLibPage.getFileDirectoryInfo(filename).selectEditInGoogleDocs().render();
        waitUntilAlert(3);

        upgrade = drone.getCurrentPage().render();
        if (confirmUpgrade)
        {

            upgrade.clickYes();
            String message = "Editing in Google Docs";
            if (isGoogleCreate)
            {
                message = "Creating Google Docs";
            }
            drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), message, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), message, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            String errorMessage = "";
            try
            {
                errorMessage = drone.find(By.cssSelector("div.bd>span.message")).getText();
            }
            catch (NoSuchElementException e)
            {
                return new EditInGoogleDocsPage(drone, documentVersion, isGoogleCreate);
            }
            throw new PageException(errorMessage);
        }
        else
        {
            upgrade.cancelNo().render();
            return drone.getCurrentPage().render();
        }
    }

    /**
     * Enter the Username , password and Click on Sign up button.
     */
    public void channelSignUp(String username, String password)
    {
        try
        {
            WebElement usernameInput = drone.findAndWait(GOOGLE_USERNAME);
            usernameInput.clear();
            usernameInput.sendKeys(username);

            WebElement passwordInput = drone.findAndWait(GOOGLE_PASSWORD);
            passwordInput.clear();
            passwordInput.sendKeys(password);

            WebElement submitButton = drone.find(SIGNUP_BUTTON);
            submitButton.click();
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google Sign up page timeout", te);
        }
    }

    /**
     * Switch to google Doc Signup Window based on title.
     */
    private void switchToGoogleSignIn()
    {
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle().contains(googleAccountTitle))
            {
                break;
            }
        }
    }

    /**
     * Switch to Share Window.
     */
    private void switchToShare()
    {
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle() != null)
            {
                if ((drone.getTitle().contains("Document Details")))
                {
                    break;
                }
                else if ((drone.getTitle().contains("Document Library")))
                {
                    break;
                }
            }
        }
    }

    /**
     * Switch to google docs edit.
     */
    private void switchToGoogleDocsEdit()
    {
        Set<String> windowHandles = drone.getWindowHandles();
        for (String windowHandle : windowHandles)
        {
            drone.switchToWindow(windowHandle);
            if (drone.getTitle() != null)
            {
                if ((drone.getTitle().endsWith("Google Docs Editor")))
                {
                    break;
                }
            }
        }
    }

}
