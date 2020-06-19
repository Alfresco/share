/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.po.wqs;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public class WcmqsAllPublicationsPage extends WcmqsAbstractPage
{

    @RenderWebElement
    private final By PAGE_TITLE = By.cssSelector("div.interior-header > h2");
    private final By PUBLICATIONS_TITLES = By.cssSelector(".portfolio-wrapper>li>h3>a");
    private final By PUBLICATION_PREVIEWS = By.cssSelector(".img-border");
    private final By KEY_PUBLICATIONS_SECTION = By.cssSelector(".interior-content>h3");
    private final By PUBLICATION_ELEMENT_TITLE = By.cssSelector(".publications-list-detail>h3>a");
    private final By PUBLICATION_ELEMENT_DATE_AUTHOR = By.cssSelector(".newslist-date");
    private final By PUBLICATION_ELEMENT_DESCRIPTION = By.cssSelector(".publications-list-detail>p");
    private final By PUBLICATION_ELEMENT_TAGSECTION = By.cssSelector(".tag-list");


    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public WcmqsAllPublicationsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAllPublicationsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(PAGE_TITLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAllPublicationsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAllPublicationsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public boolean isPublicationTitleDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_ELEMENT_TITLE).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;


    }

    public boolean isPublicationPreviewDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_PREVIEWS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;


    }

    public boolean isPublicationDateAndAuthorDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_ELEMENT_DATE_AUTHOR).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;


    }

    public boolean isPublicationDescriptionDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_ELEMENT_DESCRIPTION).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;


    }

    public boolean isPublicationTagDisplay()
    {
        try
        {
            return drone.find(PUBLICATION_ELEMENT_TAGSECTION).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;


    }

    /**
     * Method to get all titles from Publication Page
     *
     * @return List<ShareLink>
     */
    public List<ShareLink> getAllPublictionsTitles()
    {
        List<ShareLink> folders = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = drone.findAll(PUBLICATIONS_TITLES);
            for (WebElement div : links)
            {
                folders.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return folders;
    }

    public WebElement getKeyPublicationsSection()
    {
        return drone.find(KEY_PUBLICATIONS_SECTION);
    }

    /**
     * Method to get all titles from Publication Page
     *
     * @return List<ShareLink>
     */
    public List<ShareLink> getAllPublictionsImages()
    {
        List<ShareLink> folders = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = drone.findAll(PUBLICATION_PREVIEWS);
            for (WebElement div : links)
            {
                folders.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return folders;
    }

    /**
     * Method to click on a document from any publication root page
     *
     * @param documentTitle
     */
    public void clickDocumentImage(String documentTitle)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(text(),'%s')]/../../..//img", documentTitle))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Document link. " + e.toString());
        }
    }


}
