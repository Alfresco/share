package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

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

    /**
     * Constructor
     */
    public WikiPageList(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public WikiPageList render(long time)
    {
        return render(new RenderTime(time));
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
            row = drone.findAndWait(By.xpath(String.format("//a[text()='%s']/../..", title)), WAIT_TIME_3000);
            drone.mouseOver(row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageException(String.format("File directory info with title %s was not found", title), e);
        }
        return new WikiPageDirectoryInfo (drone, row);
    }

    /**
     * Get document library left hand navigation menu trees.
     *
     * @return {@link WikiTreeMenuNavigation} object.
     */
    public WikiTreeMenuNavigation getLeftMenus()
    {
        return new WikiTreeMenuNavigation(drone);
    }

    /**
     * Method to edit a wiki page based on title provided
     * @param title
     * @param txtLines
     * @return
     */
    public WikiPage editWikiPage (String title, String txtLines)
    {
        WikiPage wikiPage = getWikiPageDirectoryInfo(title).clickEdit();
        wikiPage.editWikiText(txtLines);
        logger.info("Edited Wiki page");
        return wikiPage.clickSaveButton();
    }

    /**
     * Method to rename a wiki page
     *
     * @param wikiOldTitle
     * @param wikiNewTitle
     * @return WikiPage object
     */
    public WikiPage renameWikiPage (String wikiOldTitle, String wikiNewTitle)
    {
        WikiPage wikiPage = getWikiPageDirectoryInfo(wikiOldTitle).clickDetails();
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
            if (!drone.isElementDisplayed(WIKI_CONTAINER))
            {
                return 0;
            }
            return drone.findAll(WIKI_CONTAINER).size();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to get wiki count");
        }
    }

    /**
     * Method to delete a wiki with confirmation
     * @param title
     * @return
     */
    public WikiPageList deleteWikiWithConfirm(String title)
    {
        try
        {
            getWikiPageDirectoryInfo(title).clickDelete();
            if(!drone.isElementDisplayed(PROMPT_PANEL_ID))
            {
                throw new ShareException("Prompt isn't popped up");
            }
            drone.findAndWait(CONFIRM_DELETE).click();
            waitUntilAlert();
            logger.info("Deleted Wiki page");
            return new WikiPageList(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to delete wiki");
        }
    }

    /**
     * Method to verify whether wiki page is available in the list (if title contains "_" it will be replaced for space)
     *
     * @param title
     * @return
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
            WebElement theItem = drone.find(By.xpath(String.format(WIKI_PAGE_TITLE, title)));
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
     * @param title
     * @return
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
            theItem = drone.findAndWait(By.xpath(String.format(WIKI_PAGE_TEXT, title)), WAIT_TIME_3000);
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
     * @param title
     * @return
     */
    public WikiPage clickWikiPage(String title)
    {

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            title = title.replace("_", " ");
            WebElement theItem = drone.findAndWait(By.xpath(String.format(WIKI_PAGE_TITLE, title)), WAIT_TIME_3000);
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
        return drone.getCurrentPage().render();
    }

    /**
     * Method click on wiki page details action (if title contains "_" it will be replaced for space)
     *
     * @param title
     * @return
     */
    public WikiPage clickWikiPageDetails(String title)
    {

        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }

        try
        {
            title = title.replace("_", " ");
            WikiPageList wikiPageList;
            wikiPageList = drone.getCurrentPage().render();
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
        return drone.getCurrentPage().render();
    }

    /**
     * Method to remove tags for wiki page
     * @param title
     * @param tags
     * @return
     */
    public WikiPage removeTag (String title, final String[] tags)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        if (tags == null)
        {
            throw new IllegalArgumentException("Tag is required");
        }
        WikiPage wikiPage = getWikiPageDirectoryInfo(title).clickEdit();
        WebElement element;
        for (String tag : tags)
        {
            String tagXpath = String.format(wikiTag, tag);
            try
            {
                element = drone.findAndWait(By.xpath(tagXpath));
                element.click();
                drone.waitUntilElementDisappears(By.xpath(tagXpath), 3000);
            }
            catch (NoSuchElementException e)
            {
                throw new PageException("Unable to find tag " + tag + "");
            }
        }

        wikiPage.clickSaveButton();
        logger.info("Removed tags for Wiki page");
        return drone.getCurrentPage().render();
    }

    /**
     * Method to check tags for wiki page
     * if param tag is null will be return true if 'Tags: (None)'
     * 
     * @param title
     * @param tag
     * @return
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
                element = drone.findAndWait(By.xpath(tagXpath));
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
                element = drone.findAndWait(By.xpath(tagXpath));
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
            WebElement theItem = drone.findAndWait(NO_WIKI_PAGES);
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
