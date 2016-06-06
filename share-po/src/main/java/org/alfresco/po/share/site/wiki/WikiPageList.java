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

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.WebDriverAwareDecorator;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * Holds html elements of Wiki List page
 *
 * @author Marina.Nenadovets
 */
@SuppressWarnings("unused")
public class WikiPageList extends WikiPage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private static final By BUTTON_CREATE = By.cssSelector("button[id$='default-create-button-button']");
    private static final By DETAILS_WIKI = By.cssSelector("a[href*='action=details']");
    private static final By DELETE_WIKI = By.cssSelector("button[id$='default-delete-button-button']");
    private static final By EDIT_WIKI = By.cssSelector("a[href*='action=edit']");
    private static final By WIKI_CONTAINER = By.cssSelector("div[id*='default-pagelist']>.wikipage:not(.wikiPageDeselect)");
    private final static String WIKI_PAGE_TITLE = "//a[contains(@class,'pageTitle') and text()='%s']";
    private final static String WIKI_PAGE_TEXT = "//a[contains(@class,'pageTitle') and text()='%s']/parent::div/following-sibling::div[contains(@class,'pageCopy')]";
    private final static String wikiTag = "//a[@class='taglibrary-action']/span[text()='%s']";
    private final static String tagNone = "//a[contains(text(),'%s')]/../following-sibling::div[@class='pageTags']";
    private final static String tagName = "//a[contains(text(),'%s')]/../following-sibling::div[@class='pageTags']/a[contains(text(),'%s')]";
    private static final By NO_WIKI_PAGES = By.cssSelector("div[class='noWikiPages']");


    @SuppressWarnings("unchecked")
    @Override
    public WikiPageList render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(BUTTON_CREATE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPageList render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public WikiPageDirectoryInfo getWikiPageDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement row = null;

        try
        {
            row = findAndWait(By.xpath(String.format("//a[text()='%s']/../..", title)), getDefaultWaitTime());
            mouseOver(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        WikiPageDirectoryInfo wiki = new WikiPageDirectoryInfo();
        PageFactory.initElements(new WebDriverAwareDecorator(driver),wiki);
        return wiki;
    }
    WikiTreeMenuNavigation wikiTreeMenuNavigation;
    /**
     * Get document library left hand navigation menu trees.
     *
     * @return {@link WikiTreeMenuNavigation} object.
     */
    public WikiTreeMenuNavigation getLeftMenus()
    {
        return wikiTreeMenuNavigation;
    }

    /**
     * Method to edit a wiki page based on title provided
     * @param title String
     * @param txtLines String
     * @return WikiPage
     */
    public WikiPage editWikiPage (String title, String txtLines)
    {
        getWikiPageDirectoryInfo(title).clickEdit();
        WikiPage wikiPage = getCurrentPage().render();
        wikiPage.editWikiText(txtLines);
        logger.info("Edited Wiki page");
        return wikiPage.clickSaveButton();
    }

    /**
     * Method to rename a wiki page
     *
     * @param wikiOldTitle String
     * @param wikiNewTitle String
     * @return WikiPage object
     */
    public WikiPage renameWikiPage (String wikiOldTitle, String wikiNewTitle)
    {
        getWikiPageDirectoryInfo(wikiOldTitle).clickDetails();
        WikiPage wikiPage = getCurrentPage().render(); 
        return wikiPage.renameWikiPage(wikiNewTitle);
    }

    /**
     * Method to retrieve wiki count
     *
     * @return number of pages
     */
    public int getWikiCount()
    {
        try
        {
            if (!isElementDisplayed(WIKI_CONTAINER))
            {
                return 0;
            }
            return driver.findElements(WIKI_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to get wiki count");
        }
    }

    /**
     * Method to delete a wiki with confirmation
     * @param title String
     * @return WikiPageList
     */
    public WikiPageList deleteWikiWithConfirm(String title)
    {
        try
        {
            getWikiPageDirectoryInfo(title).clickDelete();
            if(!isElementDisplayed(PROMPT_PANEL_ID))
            {
                throw new ShareException("Prompt isn't popped up");
            }
            findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            logger.info("Deleted Wiki page");
            return getCurrentPage().render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete wiki");
        }
    }

    /**
     * Method to verify whether wiki page is available in the list (if title contains "_" it will be replaced for space)
     *
     * @param title String
     * @return boolean
     */
    public boolean isWikiPagePresent(String title)
    {
        boolean isDisplayed;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            title = title.replace("_", " ");
            WebElement theItem = driver.findElement(By.xpath(String.format(WIKI_PAGE_TITLE, title)));
            isDisplayed = theItem.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            isDisplayed = false;
        }
        return isDisplayed;
    }

    /**
     * Method to verify whether item is available (if title contains "_" it will be replaced for space)
     *
     * @param title String
     * @return String
     */
    public String getWikiPageTextFromPageList(String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        WebElement theItem;
        try
        {
            title = title.replace("_", " ");
            theItem = findAndWait(By.xpath(String.format(WIKI_PAGE_TEXT, title)), getDefaultWaitTime());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Unable to locate wiki page title '" + title + "'"), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("Unable to locate wiki page title '" + title + "'"), e);
        }

        return theItem.getText();
    }

    /**
     * Method click on wiki page title (if title contains "_" it will be replaced for space)
     * 
     * @param title String
     * @return WikiPage
     */
    public HtmlPage clickWikiPage(String title)
    {

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            title = title.replace("_", " ");
            WebElement theItem = findAndWait(By.xpath(String.format(WIKI_PAGE_TITLE, title)), getDefaultWaitTime());
            theItem.click();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to locate wiki page title '" + title + "' button",nse);
            }
            throw new PageOperationException("Unable to locate expected element.");
        }
        return getCurrentPage();
    }

    /**
     * Method click on wiki page details action (if title contains "_" it will be replaced for space)
     *
     * @param title String
     * @return WikiPage
     */
    public HtmlPage clickWikiPageDetails(String title)
    {

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            title = title.replace("_", " ");
            WikiPageList wikiPageList = getCurrentPage().render();
            wikiPageList.getWikiPageDirectoryInfo(title.replace("_", " ")).clickDetails();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to locate wiki page '" + title + "' action details",nse);
            }
            throw new PageOperationException("Unable to locate expected element.");
        }
        return getCurrentPage();
    }

    /**
     * Method to remove tags for wiki page
     * @param title String
     * @param tags String[]
     * @return WikiPage
     */
    public HtmlPage removeTag (String title, final String[] tags)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        if (tags == null)
        {
            throw new IllegalArgumentException("Tag is required");
        }
        getWikiPageDirectoryInfo(title).clickEdit();
        WikiPage wikiPage = getCurrentPage().render(); 
        WebElement element;
        for (String tag : tags)
        {
            String tagXpath = String.format(wikiTag, tag);
            try
            {
                element = findAndWait(By.xpath(tagXpath));
                element.click();
                waitUntilElementDisappears(By.xpath(tagXpath), 3000);
            }
            catch (NoSuchElementException e)
            {
                throw new PageException("Unable to find tag " + tag + "");
            }
        }

        wikiPage.clickSaveButton();
        logger.info("Removed tags for Wiki page");
        return getCurrentPage();
    }

    /**
     * Method to check tags for wiki page
     * if param tag is null will be return true if 'Tags: (None)'
     * 
     * @param title String
     * @param tag String
     * @return boolean
     */
    public boolean checkTags(String title, String tag)
    {
        boolean isDisplayed;
        WebElement element;
        String tagXpath;

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        if (tag == null)
        {

            tagXpath = String.format(tagNone, title);
            try
            {
                element = findAndWait(By.xpath(tagXpath));
                isDisplayed = element.getText().contains("None");
            }
            catch (NoSuchElementException ex)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate wiki page or 'Tags: (None)'",ex);
                }
                throw new PageOperationException("Unable to locate wiki page or 'Tags: (None)'");
            }

        }
        else
        {

            tagXpath = String.format(tagName, title, tag);
            try
            {
                element = findAndWait(By.xpath(tagXpath));
                isDisplayed = element.isDisplayed();
            }
            catch (NoSuchElementException te)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to locate expected tag or wiki page", te);
                }
                throw new PageOperationException("Unable to locate expected tag or wiki page");
            }
        }
        return isDisplayed;
    }

    /**
     * Method check that no wiki pages displayed
     *
     * @return true if no wiki pages displayed
     */
    public boolean isNoWikiPagesDisplayed()
    {
        boolean isDisplayed;

        try
        {
            WebElement theItem = findAndWait(NO_WIKI_PAGES);
            isDisplayed = theItem.isDisplayed();
        }
        catch (TimeoutException te)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Unable to locate expected element.'There are currently no pages to display'", te);
            }
            throw new PageOperationException("Unable to locate expected element.'There are currently no pages to display'");
        }
        return isDisplayed;
    }
}
