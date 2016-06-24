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
package org.alfresco.po.share.site.wiki;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Represent elements found on the HTML page relating to the wiki page
 * left hand menus that appear on the wiki site page.
 *
 * @author Marina.Nenadovets
 */
public class WikiTreeMenuNavigation extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Use this to refer to a specific left hand menu tree in WikiTreeMenuNavigation
     *
     * @author Marina.Nenadovets
     */
    public enum TreeMenu
    {
        PAGES("//div[contains(@class,'wiki-filter')]"),
        TAGS("//div[contains(@id,'tags')]/div[contains(@class,'filter')]");

        private final String xpath;

        private TreeMenu(String xpath)
        {
            this.xpath = xpath;
        }

        public String getXpath()
        {
            return xpath;
        }
    }

    /**
     * Use this to refer to items in the Pages menu.
     *
     */
    public enum PagesMenu
    {
        RECENTLY_MODIFIED("recentlyModified"),
        ALL("all"),
        RECENTLY_ADDED("recentlyAdded"),
        MY_PAGES("myPages");

        private final String elClass;

        private PagesMenu(String elClass)
        {
            this.elClass = elClass;
        }

        private String getXpath()
        {
            return String.format("//span[@class='%s']/a", elClass);
        }
    }

    private static final String TAG_NODE = "//a[text()='%s']";
    private static final String SHOW_ALL_ITEMS = "//a[@rel='-all-']";

    @SuppressWarnings("unchecked")
    @Override
    public WikiTreeMenuNavigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiTreeMenuNavigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Retuns true if the given menu tree is visible.
     *
     * @return boolean
     */
    public boolean isMenuTreeVisible(TreeMenu treeMenu)
    {
        if (treeMenu == null)
        {
            throw new UnsupportedOperationException("TreeMenu is required.");
        }

        try
        {
            return findAndWait(By.xpath(treeMenu.getXpath())).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.trace("Exceeded time to find the " + treeMenu + " tree." + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the menu is open
     *
     * @param treeMenu TreeMenu
     * @return boolean
     */
    private boolean isMenuExpanded(TreeMenu treeMenu)
    {
        if (!isMenuTreeVisible(treeMenu))
        {
            throw new IllegalArgumentException(treeMenu + " is not visible on the page.");
        }

        By menuLocator = By.xpath(treeMenu.getXpath() + "/h2");
        try
        {
            String elClass = driver.findElement(menuLocator).getAttribute("class");
            if (elClass.contains("alfresco-twister-open"))
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find element " + menuLocator, e);
        }
        return false;
    }

    /**
     * Opens the menu tree
     *
     * @param treeMenu TreeMenu
     */
    private void expandMenu(TreeMenu treeMenu)
    {
        if (!isMenuExpanded(treeMenu))
        {
            By menuLocator = By.xpath(treeMenu.getXpath());
            try
            {
                driver.findElement(menuLocator).click();
            }
            catch (NoSuchElementException e)
            {
                throw new PageException("Unable to find element " + menuLocator, e);
            }
        }
    }

    /**
     * Expands the Pages menu and selects the given link.
     *
     * @param pagesMenu PagesMenu
     * @return The page loaded when the node is selected.
     */
    public HtmlPage selectPageNode(PagesMenu pagesMenu)
    {
        if (pagesMenu == null)
        {
            throw new IllegalArgumentException("DocumentsMenu is required.");
        }

        logger.info("Selecting Wiki Page filter " + pagesMenu);
        String xpath = TreeMenu.PAGES.getXpath() + pagesMenu.getXpath();
        By nodeLocator = By.xpath(xpath);
        try
        {
            expandMenu(TreeMenu.PAGES);
            driver.findElement(nodeLocator).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find element " + nodeLocator, e);
        }

        return getCurrentPage().render();
    }

    /**
     * Expands the Tags menu and selects the given link.
     *
     * @param tagName String
     * @return The page loaded when the node is selected.
     */
    public HtmlPage selectTagNode(String tagName)
    {
        if (tagName == null)
        {
            throw new IllegalArgumentException("Tag name is required.");
        }

        logger.info("Selecting wiki tags filter for " + tagName);
        //Tag names are lower case so change tagNAme to lower case to allow matching.
        tagName = tagName.toLowerCase();

        String xpath = TreeMenu.TAGS.getXpath() + String.format(TAG_NODE, tagName);

        By nodeLocator = By.xpath(xpath);
        try
        {
            expandMenu(TreeMenu.TAGS);
            driver.findElement(nodeLocator).click();
            waitForPageLoad(getDefaultWaitTime());

        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Unable to find element " + nodeLocator, e);
        }

        return getCurrentPage();
    }

    /**
     * Method to select Show All Items for tags part
     *
     * @return HtmlPage
     */
    public HtmlPage selectShowAllItems()
    {
        logger.info("Selecting Show All Items filter");
        String xpath = TreeMenu.TAGS.getXpath() + SHOW_ALL_ITEMS;

        By nodeLocator = By.xpath(xpath);
        try
        {
            expandMenu(TreeMenu.TAGS);
            driver.findElement(nodeLocator).click();
            waitForPageLoad(getDefaultWaitTime());
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException (SHOW_ALL_ITEMS + "isn't available", e);
        }
        return getCurrentPage();
    }
}
