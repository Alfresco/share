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
package org.alfresco.po.share.site;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactorySharePage;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.logging.Logger;
import java.util.logging.Logger;

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
    private static final By AVAILABLE_PAGES_CONTAINER_ITEM = By.cssSelector("ul[id$='_default-availablePages-ul']>li");
    private static final By SAVE_BUTTON = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-save-button-button");
    private static final By CANCEL_BUTTON = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-save-button-button");
    private static final By THEME_MENU = By.cssSelector("#template_x002e_customise-pages_x002e_customise-site_x0023_default-theme-menu");
    private static final By DOCUMENT_LIB = By.cssSelector("li[id$='_default-page-documentlibrary']");
    private static final By CURRENT_PAGES_CONTAINER = By.cssSelector(".current-pages");  //By.xpath("//ul[contains(@id, '_default-currentPages-ul')]/..");
    //private static final By LAST_AVAILABLE_PAGE = By.cssSelector("ul[id$='_default-availablePages-ul']:last-child li[class$='dnd-draggable']:last-child");
    private static final By LAST_AVAILABLE_PAGE = By.xpath("//ul[contains(@id, '_default-availablePages-ul')]/..//li[@class='customise-pages-page-list-item dnd-draggable'][last()]");
    private static final By LAST_CURRENT_PAGE = By.xpath("//ul[contains(@id, '_default-currentPages-ul')]/..//li[@class='customise-pages-page-list-item dnd-draggable'][last()]");
    private static final By CURRENT_PAGES_CONTAINER_ITEM = By.cssSelector("li[class$='_default-currentPages-ul']>li");
    private static final String PARENT_AVAILABLE_PAGES_XPATH = "/parent::ul[contains(@id,'_default-currentPages-ul')]";

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
        WebElement currentPageElement = driver.findElement(locator);
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

    private int getLastAvailablePageX()
    {

        try
        {
            getCurrentPage();
            return  driver.findElement(LAST_AVAILABLE_PAGE).getLocation().getX();
        }
        catch (NoSuchElementException n)
        {
        }
        return 0;
    }

    private int getLastAvailablePageY()
    {

        try
        {
            getCurrentPage();
            return  driver.findElement(LAST_AVAILABLE_PAGE).getLocation().getY();
        }
        catch (NoSuchElementException n)
        {
        }
        return 0;
    }

    private int getLastCurrentPageX()
    {

        try
        {
            getCurrentPage();
            return driver.findElement(LAST_CURRENT_PAGE).getLocation().getX();
        }
        catch (NoSuchElementException n)
        {
        }
        return 0;
    }

    private int getLastCurrentPageY()
    {

        try
        {
            getCurrentPage();
            return  driver.findElement(LAST_CURRENT_PAGE).getLocation().getY();
        }
        catch (NoSuchElementException n)
        {
        }
        return 0;
    }

    /**
     * Add the Site Pages to Site.
     *
     * @return {@link SiteDashboardPage}
     */
//    public SiteDashboardPage addPages(List<SitePageType> pageTypes)
//    {
//       WebElement target = driver.findElement(CURRENT_PAGES_CONTAINER_ITEM);
//
//        if (getAvailablePages().containsAll(pageTypes))
//        {
//            for (SitePageType sitePageType : pageTypes)
//            {
//                try
//                {
//                    target.click();
//                    waitUntilAlert();
//                    dragAndDrop(driver.findElement(sitePageType.getLocator()),target);
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
//        driver.findElement(SAVE_BUTTON).click();
//        waitUntilAlert();
//        return getCurrentPage();
//    }
    
    /**
     * Move pages from Current Site Pages to Available Site Pages 
     * 
     * @param pageType
     * @return
     */
    public HtmlPage addToAvailablePages (SitePageType pageType)
    {
        WebElement target = driver.findElement(AVAILABLE_PAGES);
        try
        {
            WebElement elem = driver.findElement(pageType.getLocator());
            executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
            dragAndDrop(elem, target);
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Not able to find Site Page: " + pageType, toe);
        }
        return getCurrentPage();
    }
   
    /**
     * Move pages from Available Site Pages to Current Site Pages
     * 
     * @param pageType
     * @return
     */
    public HtmlPage addToCurrentPages (SitePageType pageType)
    {
        WebElement target = driver.findElement(CURRENT_PAGES_CONTAINER);
        try
        {
            WebElement elem = driver.findElement(pageType.getLocator());
            executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
            dragAndDrop(elem, target);
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Not able to find Site Page: " + pageType, toe);
        }
        return getCurrentPage();
    }

    /**
     * Method used to add pages using coordinates for dropping
     *
     * @param pageTypes List<SitePageType>
     * @return SiteDashboardPage
     */
    public HtmlPage addPages (List<SitePageType> pageTypes)
    {

        if (getAvailablePages().containsAll(pageTypes))
        {
            for (SitePageType theTypes : pageTypes)
            {
                try
                {
                    WebElement elem = driver.findElement(theTypes.getLocator());
                    WebElement target = driver.findElement(CURRENT_PAGES_CONTAINER);
                    
                    executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                    dragAndDrop(elem, target);
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
        driver.findElement(SAVE_BUTTON).click();
        waitUntilAlert();
        return getCurrentPage();
    }
 
    /**
     * Method used to add current pages to Available Pages Section using coordinates for dropping and click OK button
     * to customise site page
     *
     * @param pageTypes List<SitePageType>
     * @return SiteDashboardPage
     */
    public HtmlPage addToAvailablePagesAndClickOK (List<SitePageType> pageTypes)
    {
        WebElement target = driver.findElement(AVAILABLE_PAGES);

        if (getCurrentPages().containsAll(pageTypes))
        {
            for (SitePageType theTypes : pageTypes)
            {
                try
                {
                    WebElement elem = driver.findElement(theTypes.getLocator());
                    executeJavaScript(String.format("window.scrollTo(0, '%s')", target.getLocation().getY()));
                    dragAndDrop(elem, target);
                }
                catch (TimeoutException e)
                {
                    throw new PageException("Not able to Site Page in the Page : " + theTypes, e);
                }
            }
        }
        else
        {
            throw new PageException("Some of Site Page(s) are not available to add to Available Pages Site, may be already added. " + pageTypes.toString());
        }        
        driver.findElement(SAVE_BUTTON).click();
        waitUntilAlert();
        return getCurrentPage();
    }
    
    
    /**
     * Method to add all available pages
     *
     * @return SiteDashboardPage
     */
    public HtmlPage addAllPages ()
    {
        List<SitePageType> allThePages = getAvailablePages();
        addPages(allThePages);
        return getCurrentPage();
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
