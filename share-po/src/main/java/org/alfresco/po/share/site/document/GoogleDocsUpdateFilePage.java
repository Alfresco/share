/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * When the user clicks on Save to Alfresco they will be provided with update version page.
 * This function extends UpdateFilePage and use the functionality of the page.
 * 
 * @author Subashni Prasanna
 * @since 1.5
 */

@SuppressWarnings("unchecked")
public class GoogleDocsUpdateFilePage extends UpdateFilePage
{
    private static final By BUTTON_TAG_NAME = By.tagName("button");

    /**
     * Constructor.
     */
    public GoogleDocsUpdateFilePage(WebDrone drone, String documentVersion, boolean editOffline)
    {
        super(drone, documentVersion, editOffline);
        setMinorVersionRadioButton("input[id$='default-configDialog-minorVersion-radioButton']");
        setMajorVersionRadioButton("input[id$='default-configDialog-majorVersion-radioButton']");
        setSubmitButton("button[id$='configDialog-ok-button']");
        setCancelButton("button[id$='configDialog-cancel-button']");
        setTextAreaCssLocation("textarea[id$='configDialog-description-textarea']");
    }

    /**
     * Render method Overridden as we have a different set of parameters to
     * check.
     */
    @Override
    public synchronized GoogleDocsUpdateFilePage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            // Look for comment box
            try
            {
                if (!drone.find(By.cssSelector(getMinorVersionRadioButton())).isDisplayed())
                {
                    continue;
                }
                if (!drone.find(By.cssSelector(getMajorVersionRadioButton())).isDisplayed())
                {
                    continue;
                }
            }
            catch (NoSuchElementException e)
            {
                // It's not there
                continue;
            }
            // Everything was found and is visible
            break;
        }
        return this;
    }

    @Override
    public GoogleDocsUpdateFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @Override
    public GoogleDocsUpdateFilePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Clicks on the submit upload button.
     */
    @Override
    public HtmlPage submit()
    {
        WebElement submitButtonElement = drone.findAndWait(By.cssSelector(getSubmitButton()));
        submitButtonElement.click();

        String text = "Saving Google Doc";
        drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        RenderTime time = new RenderTime(maxPageLoadingTime+maxPageLoadingTime);
        time.start();
        while (true)
        {
            try
            {
                drone.waitUntilNotVisibleWithParitalText(By.cssSelector("div.bd>span.message"), text, 10);
            }
            catch (TimeoutException e)
            {
                SharePopup errorPopup = new SharePopup(drone);
                try
                {
                    errorPopup.render(new RenderTime(popupRendertime));
                    return errorPopup;
                }
                catch (PageRenderTimeException exception)
                {
                    continue;
                }
            }
            finally
            {
                time.end();
            }
            break;
        }

        HtmlPage page = drone.getCurrentPage();

        if (page instanceof DocumentLibraryPage)
        {
            return new DocumentLibraryPage(drone);
        }

        return new DocumentDetailsPage(drone, getDocumentVersion());
    }

    /**
     * Clicks on the submit button. Methods used for edition by concurrent user's
     */
    public HtmlPage submitWithConcurrentEditors()
    {
        WebElement submitButtonElement = drone.findAndWait(By.cssSelector(getSubmitButton()));
        submitButtonElement.click();

        String text = "Saving Google Doc";
        drone.waitUntilVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

        HtmlPage page = drone.getCurrentPage();
        clickOkButton();

        drone.waitUntilNotVisible(By.cssSelector("div.bd>span.message"), text, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

        drone.getCurrentPage().render();

        if (page instanceof DocumentLibraryPage)
        {
            return new DocumentLibraryPage(drone);
        }

        return new DocumentDetailsPage(drone, getDocumentVersion());
    }

    /**
     * Click OK Button on the confirmation dialog.
     * 
     * @return - HtmlPage
     */
    public HtmlPage clickOkButton()
    {
        try
        {
            WebElement prompt = drone.findAndWait(PROMPT_PANEL_ID);
            List<WebElement> elements = prompt.findElements(BUTTON_TAG_NAME);
            WebElement okButton = findButton("OK", elements);
            okButton.click();
            drone.waitUntilElementDeletedFromDom(By.cssSelector("span[class='message']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (TimeoutException te)
        {
            throw new TimeoutException("Google discard Page ok button not visible", te);
        }
        drone.waitForPageLoad(SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return FactorySharePage.resolvePage(drone);
    }
}