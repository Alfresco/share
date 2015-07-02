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

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Google authorisation login prompt page object.
 *
 * @author Subashni Prasanna
 * @author Michael Suzuki
 * @since 1.5
 */
public class GoogleDocsAuthorisation extends SharePage
{
    private static final By BUTTON_TAG_NAME = By.tagName("button");
    private boolean isGoogleCreate;
    private String documentVersion;

    /**
     * Constructor.
     *
     * @param drone           {@link WebDrone}
     * @param documentVersion String original document version.
     * @param isGoogleCreate Boolean
     */
    protected GoogleDocsAuthorisation(WebDrone drone, String documentVersion, Boolean isGoogleCreate)
    {
        super(drone);
        this.isGoogleCreate = isGoogleCreate;
        this.documentVersion = documentVersion;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsAuthorisation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsAuthorisation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsAuthorisation render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Verify if authorisation prompt is displayed.
     *
     * @return true if dialog is displayed.
     */
    public boolean isAuthorisationDisplayed()
    {
        boolean displayed = false;
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            displayed = prompt.isDisplayed();
        }
        catch (TimeoutException e)
        {
            displayed = false;
        }

        return displayed;
    }

    /**
     * Clicks on the submit button to transfer to google docs page.
     *
     * @return EditInGoogleDocsPage
     */
    public EditInGoogleDocsPage submitNoAuth()
    {
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("OK", elements);
            okButton.click();
            return new EditInGoogleDocsPage(drone, isGoogleCreate);
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("authorisation prompt was not found", te);
        }
    }

    /**
     * Clicks on the submit button to transfer the control to Sign up page.
     *
     * @return GoogleSignUpPage
     */
    public GoogleSignUpPage submitAuth()
    {
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("OK", elements);
            drone.executeJavaScript("var arg = arguments[0]; setTimeout(function() {arg.click()}, 0);", okButton);
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("authorisation prompt was not found", te);
        }
        catch (NoSuchElementException te)
        {
            throw new PageOperationException("authorisation prompt was not found", te);
        }
        GoogleSignUpPage googleSignUpPage = new GoogleSignUpPage(drone, documentVersion, isGoogleCreate);
        return googleSignUpPage;
    }

    /**
     * Clicks on the cancel button to transfer the control to document details.
     *
     * @return DocumentDetailsPage
     */
    public HtmlPage cancel()
    {
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement cancelButton = findButton("Cancel", elements);
            cancelButton.click();
        }
        catch (TimeoutException nse)
        {
            throw new TimeoutException("authorisation prompt was not found", nse);
        }
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }
}
