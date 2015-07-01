package org.alfresco.po.share.site.datalist;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * Represent elements found on the HTML page relating to the data list page
 * left hand menus that appear on the data list site page.
 *
 * @author Marina.Nenadovets
 */
public class DataListTreeMenuNavigation extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Use this to refer to items in the Pages menu.
     */
    public enum ListsMenu
    {
        RECENTLY_MODIFIED("recentlyModified"),
        ALL("all"),
        RECENTLY_ADDED("recentlyAdded"),
        CREATED_BY_ME("createdByMe");

        private final String elClass;

        private ListsMenu(String elClass)
        {
            this.elClass = elClass;
        }

        private String getXpath()
        {
            return String.format("//span[@class='%s']/a", elClass);
        }
    }

    private static final String LISTS_FILTER = "//div[contains(@class,'datalist-filter')]";

    public DataListTreeMenuNavigation(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListTreeMenuNavigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListTreeMenuNavigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataListTreeMenuNavigation render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Retuns true if the given menu tree is visible.
     *
     * @return boolean
     */
    public boolean isMenuTreeVisible()
    {

        try
        {
            return drone.findAndWait(By.xpath(LISTS_FILTER)).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.trace("Exceeded time to find the " + LISTS_FILTER + " tree." + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the menu is open
     *
     * @return boolean
     */
    private boolean isMenuExpanded()
    {

        By menuLocator = By.xpath(LISTS_FILTER + "/h2");
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
     */
    private void expandMenu()
    {
        if (!isMenuExpanded())
        {
            By menuLocator = By.xpath(LISTS_FILTER);
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
     * @param listsMenu ListsMenu
     * @return The page loaded when the node is selected.
     */
    public HtmlPage selectListNode(ListsMenu listsMenu)
    {
        if (listsMenu == null)
        {
            throw new IllegalArgumentException("DocumentsMenu is required.");
        }
        logger.info("Selecting Lists Menu " + listsMenu);

        String selector = LISTS_FILTER + listsMenu.getXpath();
        By nodeLocator = By.xpath(selector);
        try
        {
            expandMenu();
            drone.find(nodeLocator).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find element " + nodeLocator, e);
        }

        return FactorySharePage.resolvePage(drone).render();
    }
}
