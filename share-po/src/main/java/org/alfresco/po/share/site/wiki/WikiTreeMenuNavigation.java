package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
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

    public WikiTreeMenuNavigation(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public WikiTreeMenuNavigation render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Retuns true if the given menu tree is visible.
     *
     * @return
     */
    public boolean isMenuTreeVisible(TreeMenu treeMenu)
    {
        if (treeMenu == null)
        {
            throw new UnsupportedOperationException("TreeMenu is required.");
        }

        try
        {
            return drone.findAndWait(By.xpath(treeMenu.getXpath())).isDisplayed();
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
     * @param treeMenu
     * @return
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
            String elClass = drone.find(menuLocator).getAttribute("class");
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
     * @param treeMenu
     */
    private void expandMenu(TreeMenu treeMenu)
    {
        if (!isMenuExpanded(treeMenu))
        {
            By menuLocator = By.xpath(treeMenu.getXpath());
            try
            {
                drone.find(menuLocator).click();
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
     * @param pagesMenu
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
            drone.find(nodeLocator).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find element " + nodeLocator, e);
        }

        return FactorySharePage.resolvePage(drone).render();
    }

    /**
     * Expands the Tags menu and selects the given link.
     *
     * @param tagName
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
            drone.find(nodeLocator).click();
            drone.waitForPageLoad(WAIT_TIME_3000);

        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Unable to find element " + nodeLocator, e);
        }

        return FactorySharePage.resolvePage(drone);
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
            drone.find(nodeLocator).click();
            drone.waitForPageLoad(WAIT_TIME_3000);
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException (SHOW_ALL_ITEMS + "isn't available", e);
        }
        return FactorySharePage.resolvePage(drone);
    }
}
