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
package org.alfresco.po.share.site;

import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


/**
 * Customize Site Page Object have to add pages, get current pages and get available pages.
 *
 * @author Shan Nagarajan
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public class CustomizeSitePage extends SitePage
{

    private static final By CURRENT_PAGES = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-currentPages-ul");
    private static final By AVAILABLE_PAGES = By.cssSelector("ul[id$='_default-availablePages-ul']");
    private static final By SAVE_BUTTON = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-save-button-button");
    private static final By CANCEL_BUTTON = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-save-button-button");
    private static final By THEME_MENU = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-theme-menu");
    private static final By DOCUMENT_LIB = By.cssSelector("li[id$='_default-page-documentlibrary']");
    private static final By CURRENT_PAGES_CONTAINER = By.xpath("//ul[contains(@id, '_default-currentPages-ul')]/..");
    private static final By CURRENT_PAGES_CONTAINER_ITEM = By.cssSelector("ul[id$='_default-currentPages-ul']>li");
    private static final String PARENT_AVAILABLE_PAGES_XPATH = "/parent::ul[contains(@id,'_default-currentPages-ul')]";

    public CustomizeSitePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomizeSitePage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(AVAILABLE_PAGES), RenderElement.getVisibleRenderElement(CURRENT_PAGES),
                RenderElement.getVisibleRenderElement(SAVE_BUTTON), RenderElement.getVisibleRenderElement(CANCEL_BUTTON),
                RenderElement.getVisibleRenderElement(THEME_MENU));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomizeSitePage render(long time)
    {
        render(new RenderTime(time));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomizeSitePage render()
    {
        render(new RenderTime(maxPageLoadingTime));
        return this;
    }

    /**
     * Returns All Current {@link SitePageType}.
     *
     * @return List<SitePageType>
     */
    public List<SitePageType> getCurrentPages()
    {
        return getPages(CURRENT_PAGES);
    }

    /**
     * Returns All Available {@link SitePageType}.
     *
     * @return List<SitePageType>
     */
    public List<SitePageType> getAvailablePages()
    {
        return getPages(AVAILABLE_PAGES);
    }

    private List<SitePageType> getPages(By locator)
    {
        WebElement currentPageElement = drone.find(locator);
        List<SitePageType> currentPageTypes = new ArrayList<SitePageType>();
        SitePageType[] pageTypes = SitePageType.values();
        for (SitePageType pageType : pageTypes)
        {
            if (currentPageElement.getText().contains(pageType.getDisplayText()))
            {
                currentPageTypes.add(pageType);
            }
        }
        return currentPageTypes;
    }

    /**
     * Add the Site Pages to Site.
     *
     * @return {@link SiteDashboardPage}
     */
//    public SiteDashboardPage addPages(List<SitePageType> pageTypes)
//    {
//       WebElement target = drone.findAndWait(CURRENT_PAGES_CONTAINER_ITEM);
//
//        if (getAvailablePages().containsAll(pageTypes))
//        {
//            for (SitePageType sitePageType : pageTypes)
//            {
//                try
//                {
//                    target.click();
//                    waitUntilAlert();
//                    drone.dragAndDrop(drone.findAndWait(sitePageType.getLocator()),target);
//                    waitUntilAlert();
//                }
//                catch (TimeoutException e)
//                {
//                    throw new PageException("Not able to Site Page in the Page : " + sitePageType, e);
//                }
//            }
//        }
//        else
//        {
//            throw new PageException("Some of SIte Page(s) already not available to add, may be already added. " + pageTypes.toString());
//        }
//        if(!getAddedPages().containsAll(pageTypes))
//        {
//            throw new PageException("Not all pages were added!");
//        }
//        drone.findAndWait(SAVE_BUTTON).click();
//        waitUntilAlert();
//        return drone.getCurrentPage().render();
//    }

    /**
     * Method used to add pages using coordinates for dropping
     *
     * @param pageTypes List<SitePageType>
     * @return SiteDashboardPage
     */
    public SiteDashboardPage addPages (List<SitePageType> pageTypes)
    {
        WebElement target = drone.findAndWait(CURRENT_PAGES_CONTAINER);

        if (getAvailablePages().containsAll(pageTypes))
        {
            for (SitePageType theTypes : pageTypes)
            {
                try
                {
//                    WebDriver webDriver = ((WebDroneImpl) drone).getDriver();
//                    Actions builder = new Actions(webDriver);
                    WebElement elem = drone.findAndWait(theTypes.getLocator());
                    drone.executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                    drone.dragAndDrop(elem, target);
                }
                catch (TimeoutException e)
                {
                    throw new PageException("Not able to Site Page in the Page : " + theTypes, e);
                }
            }
        }
        else
        {
            throw new PageException("Some of Site Page(s) are not available to add, may be already added. " + pageTypes.toString());
        }
        if(!getAddedPages().containsAll(pageTypes))
        {
            throw new PageException("Not all pages were added!");
        }
        drone.findAndWait(SAVE_BUTTON).click();
        waitUntilAlert();
        return drone.getCurrentPage().render();
    }

    /**
     * Method to add all available pages
     *
     * @return SiteDashboardPage
     */
    public SiteDashboardPage addAllPages ()
    {
        List<SitePageType> allThePages = getAvailablePages();
        addPages(allThePages);
        return drone.getCurrentPage().render();
    }

    /**
     * Method to get added pages
     *
     * @return List<SitePageType>
     */
    private List<SitePageType> getAddedPages ()
    {
        return getPages(CURRENT_PAGES_CONTAINER);
    }
}