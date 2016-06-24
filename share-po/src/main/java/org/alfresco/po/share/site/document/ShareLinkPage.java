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
package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The class represents the Share Link page and holds the links to Public Link and share links with Facebook, Twitter, Gmail, g+.
 * 
 * @author cbairaajoni
 */
public class ShareLinkPage extends SharePage
{
    private static Log logger = LogFactory.getLog(ShareLinkPage.class);
    @RenderWebElement
    private static final By shareLinkDivLocator = By.cssSelector("div.visible div.bd");
    @RenderWebElement
    private static final By viewLinkLocator = By.cssSelector("div.visible span>a.quickshare-action-view");
    @RenderWebElement
    private static final By unShareLinkLocator = By.cssSelector("div.visible span>a.quickshare-action-unshare");
    @RenderWebElement
    private static final By emailLinkLocator = By.cssSelector("div.visible a.linkshare-action-email");
    @RenderWebElement
    private static final By facebookLinkLocator = By.cssSelector("div.visible a.linkshare-action-facebook");
    @RenderWebElement
    private static final By twitterLinkLocator = By.cssSelector("div.visible a.linkshare-action-twitter");
    @RenderWebElement
    private static final By googlePlusLinkLocator = By.cssSelector("div.visible a.linkshare-action-google-plus");

    @SuppressWarnings("unchecked")
    @Override
    public ShareLinkPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ShareLinkPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify that ViewLink is displayed.
     * 
     * @return boolean
     */
    public boolean isViewLinkPresent()
    {
        try
        {
            return driver.findElement(viewLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }

    /**
     * Click View Link present on Share Link page.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickViewButton()
    {
        try
        {
            findAndWait(viewLinkLocator).click();

            return factoryPage.instantiatePage(driver, ViewPublicLinkPage.class);
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the view link element", ex);
        }

        throw new PageException("Unable to find View link");
    }

    /**
     * Verify that UnshareLink is displayed.
     * 
     * @return boolean
     */
    public boolean isUnShareLinkPresent()
    {
        try
        {
            return driver.findElement(unShareLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }

    /**
     * Click View Link present on Share Link page.
     * 
     * @return HtmlPage
     */
    public HtmlPage clickOnUnShareButton()
    {
        try
        {
            findAndWait(unShareLinkLocator).click();
            return getCurrentPage();
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the unshare linkelement", ex);
        }

        throw new PageException("Unable to find Unshare link");
    }

    /**
     * Verify that Email link is displayed.
     * 
     * @return boolean
     */
    public boolean isEmailLinkPresent()
    {
        try
        {
            return driver.findElement(emailLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }
    
    /**
     * Click Email Link present on Share Link page.
     */
    public void clickEmailLink()
    {
        try
        {
            driver.findElement(emailLinkLocator).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Exceeded time to find the email link element", ex);
            throw new PageException("Unable to find email link");
        }
    }

    /**
     * Verify that Facebook link is displayed.
     * 
     * @return boolean
     */
    public boolean isFaceBookLinkPresent()
    {
        try
        {
            return driver.findElement(facebookLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
        }

        return false;
    }

    /**
     * Click Facebook Link present on Share Link page.
     */
    public void clickFaceBookLink()
    {
        try
        {
            driver.findElement(facebookLinkLocator).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Exceeded time to find the Facebook link element", ex);
            throw new PageException("Unable to find Facebook link");
        }
    }

    /**
     * Verify that Twitter link is displayed.
     * 
     * @return boolean
     */
    public boolean isTwitterLinkPresent()
    {
        try
        {
            return driver.findElement(twitterLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Twitter link is not present.");
        }

        return false;
    }

    /**
     * Click Twitter Link present on Share Link page.
     */
    public void clickTwitterLink()
    {
        try
        {
            driver.findElement(twitterLinkLocator).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Exceeded time to find the Twitter link element", ex);
            throw new PageException("Unable to find Twitter link");
        }
    }

    /**
     * Verify that Google+ link is displayed.
     * 
     * @return boolean
     */
    public boolean isGooglePlusLinkPresent()
    {
        try
        {
            return driver.findElement(googlePlusLinkLocator).isDisplayed();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Google+ link is not present.", ex);
        }

        return false;
    }

    /**
     * Click Google+ Link present on Share Link page.
     */
    public void clickGooglePlusLink()
    {
        try
        {
            driver.findElement(googlePlusLinkLocator).click();
        }
        catch (NoSuchElementException ex)
        {
            logger.error("Exceeded time to find the Google+ link element", ex);
            throw new PageException("Unable to find Google+ link");
        }
    }

    /**
     * Get the shared url for the document.
     * 
     * @return String
     */
    public String getShareURL()
    {
        try
        {
            WebElement element = findAndWait(viewLinkLocator);
            return element.getAttribute("href");
        }
        catch (TimeoutException ex)
        {
            logger.error("Exceeded time to find the shared URL", ex);
        }

        throw new PageException("Unable to find the shared URL");
    }
}
