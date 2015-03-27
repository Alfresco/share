/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.thirdparty.firefox;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.Page;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Native page in FireFox for RSS
 *
 * @author Aliaksei Boole
 */
public class RssFeedPage extends Page
{
    private final static By CONTENT = By.cssSelector(".entry>h3>a>span");
    private final static By SUBSCRIBE_BUTTONS_PANEL = By.xpath(".//*[@id='feedSubscribeLine']");
    private final static By CONTENTS_BODY = By.xpath(".//*[@id='feedBody']");

    public RssFeedPage(WebDrone drone)
    {
        super(drone);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RssFeedPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(SUBSCRIBE_BUTTONS_PANEL),
                getVisibleRenderElement(CONTENTS_BODY));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RssFeedPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @Override
    @SuppressWarnings("unchecked")
    public RssFeedPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    //TODO Temporary method. Before elementRender from SharePage didn't moved out to Page.
    private void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        for (RenderElement element : elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(drone, waitSeconds);
            }
            catch (TimeoutException e)
            {
                throw new PageRenderTimeException("element not rendered in time.");
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }

    /**
     * Click on link in feed if 'linkTextContains'
     *
     * @param linkTextContains
     * @return
     */
    public SharePage clickOnFeedContent(String linkTextContains)
    {
        try
        {
            getFeedLinkWith(linkTextContains).click();
            return drone.getCurrentPage().render();
        }
        catch (StaleElementReferenceException e)
        {
            return clickOnFeedContent(linkTextContains);
        }
    }

    /**
     * return true if in feedContent has information about link with 'linkTextContains'
     *
     * @param linkTextContains
     * @return
     */
    public boolean isDisplayedInFeed(String linkTextContains)
    {
        try
        {
            return getFeedLinkWith(linkTextContains).isDisplayed();
        }
        catch (PageOperationException e)
        {
            return false;
        }
    }

    /**
     * return count elements in feed
     *
     * @return
     */
    public int getFeedContentsCount()
    {
        return getFeedContentLinks().size();
    }

    public boolean isSubscribePanelDisplay()
    {
        try
        {
            return drone.find(SUBSCRIBE_BUTTONS_PANEL).isDisplayed();
        }
        catch (ElementNotFoundException e)
        {
            return false;
        }
    }

    private List<WebElement> getFeedContentLinks()
    {
        try
        {
            return drone.findAndWaitForElements(CONTENT, 5000);
        }
        catch (TimeoutException e)
        {
            return Collections.emptyList();
        }
    }

    private WebElement getFeedLinkWith(String linkTextContains)
    {
        List<WebElement> feedContentLinks = getFeedContentLinks();
        for (WebElement feedContentLink : feedContentLinks)
        {
            String linkText = feedContentLink.getText();
            if (linkText.contains(linkTextContains))
            {
                return feedContentLink;
            }
        }
        throw new PageOperationException(String.format("Links with text '%s' don't found in feed.", linkTextContains));
    }

}
