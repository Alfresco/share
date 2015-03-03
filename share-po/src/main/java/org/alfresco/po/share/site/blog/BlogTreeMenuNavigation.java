package org.alfresco.po.share.site.blog;

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
 * Represent elements found on the HTML page relating to the blog list page
 * left hand menus that appear on the blog site page.
 * 
 * @author Sergey Kardash
 */
public class BlogTreeMenuNavigation extends SharePage
{
    private Log logger = LogFactory.getLog(this.getClass());

    private static final String POSTS_FILTER = "//span[@class='%s']/a";
    private static final String BLOG_FILTER = "//div[contains(@id,'filter')]/div[contains(@class,'blog-filter')]";

    /**
     * Use this to refer to items via Posts filter.
     */
    public enum PostsMenu
    {
        ALL("all"), LATEST("new"), MY_DRAFTS("mydrafts"), MY_PUBLISHED("mypublished"), PUBLISHED_EXTERNALLY("publishedext");

        private final String filter;

        private PostsMenu(String filter)
        {
            this.filter = filter;
        }

        private String getXpath()
        {
            return String.format(POSTS_FILTER, filter);
        }
    }

    public BlogTreeMenuNavigation(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogTreeMenuNavigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogTreeMenuNavigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlogTreeMenuNavigation render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Returns true if the given menu tree is visible.
     * BlogPageTest.BlogTreeMenuNavigation
     * 
     * @return
     */
    public boolean isMenuTreeVisible()
    {

        try
        {
            return drone.findAndWait(By.xpath(BLOG_FILTER)).isDisplayed();
        }
        catch (TimeoutException e)
        {
            logger.trace("Exceeded time to find the " + BLOG_FILTER + " tree." + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the menu is open
     * BlogPageTest.BlogTreeMenuNavigation
     * 
     * @return
     */
    public boolean isMenuExpanded()
    {

        By menuLocator = By.xpath(BLOG_FILTER + "/h2");
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
            By menuLocator = By.xpath(BLOG_FILTER);
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
     * BlogPageTest.BlogTreeMenuNavigation
     * 
     * @param postsMenu
     * @return The page loaded when the node is selected.
     */
    public HtmlPage selectListNode(PostsMenu postsMenu)
    {
        if (postsMenu == null)
        {
            throw new IllegalArgumentException("PostsMenu is required.");
        }
        logger.info("Selecting Posts Menu " + postsMenu);

        String selector = BLOG_FILTER + postsMenu.getXpath();
        By nodeLocator = By.xpath(selector);
        try
        {
            expandMenu();
            drone.find(nodeLocator).click();
            waitUntilAlert();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find element " + nodeLocator, e);
        }

        return FactorySharePage.resolvePage(drone).render();
    }
}
