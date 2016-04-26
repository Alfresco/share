
package org.alfresco.po.wqs;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Created by Lucian Tuca on 11/18/2014.
 * Modified by Sergiu Vidrascu on 01/09/2015
 */
public class WcmqsBlogPage extends WcmqsAbstractPage
{
    public static final String ETHICAL_FUNDS = "Ethical funds";
    public static final String COMPANY_ORGANISES_WORKSHOP = "Company organises workshop";
    public static final String ANALYSTS_LATEST_THOUGHTS = "latest thoughts";
    public static final String BLOG = "blog";
    public static final String BLOG_1 = "blog1.html";
    public static final String BLOG_2 = "blog2.html";
    public static final String BLOG_3 = "blog3.html";
    @RenderWebElement
    private final By BLOG_HEADER = By.cssSelector(".interior-header>h2");
    private final By BLOG_ENTRIES = By.cssSelector("div[class='blog-entry']");
    private final By BLOG_POST_DATE = By.cssSelector("div[class='blog-entry']>div[class='blog-list-misc']>span:nth-of-type(1)");
    private final By BLOG_POST_CREATOR = By.cssSelector("div[class='blog-entry']>div[class='blog-list-misc']>span:nth-of-type(2)");
    private final By BlOG_POST_COMMENTS_LINK = By.cssSelector("div[class='blog-entry']>div[class='blog-list-misc']>span:nth-of-type(3)>a");

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public WcmqsBlogPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsBlogPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(BLOG_HEADER));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsBlogPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsBlogPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public boolean isBlogDeleted(String title)
    {
        WebDroneUtil.checkMandotaryParam("title", title);
        try
        {
            drone.waitUntilElementDisappears(By.xpath(String.format("//a[contains(text(),'%s')]", title)),
                    SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            return true;
        }
        catch (TimeoutException e)
        {
        }

        return false;
    }

    public boolean isBlogDisplayed(String title)
    {
        WebDroneUtil.checkMandotaryParam("title", title);
        try
        {
            drone.waitForElement(By.xpath(String.format("//a[contains(text(),'%s')]", title)), SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            return true;
        }
        catch (TimeoutException e)
        {
        }

        return false;
    }

    public int getBlogPosts()
    {
        try
        {
            List<WebElement> posts = drone.findAndWaitForElements(BLOG_ENTRIES);
            return posts.size();
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find the blog posts. " + te.toString());
        }
    }

    public boolean isBlogPostDateDisplayed()
    {
        try
        {
            drone.findAndWait(BLOG_POST_DATE);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }

    public boolean isBlogPostCreatorDisplayed()
    {
        try
        {
            drone.findAndWait(BLOG_POST_CREATOR);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }

    public boolean isBlogPostCommentsLinkDisplayed()
    {
        try
        {
            drone.findAndWait(BlOG_POST_COMMENTS_LINK);
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }

    /**
     * Method to click a blog read more button give the blog name
     *
     * @param blogName of the blog post declared in share!
     */
    public void clickReadMoreByBlog(String blogName)
    {
        try
        {
            drone.find(By.xpath(String.format("//a[contains(text(),\"%s\")]/../..//a[contains(text(),\"read more\")]", blogName))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }
    }

    /**
     * Method to click on a tag in the tag list
     *
     * @param tagName
     */
    public void clickTag(String tagName)
    {
        WebDroneUtil.checkMandotaryParam("tagName", tagName);
        try
        {
            drone.findAndWait(By.xpath(String.format("//ul[@class=\"tag-list\"]//a[contains(text(),'%s')]", tagName))).click();
        }
        catch (TimeoutException e)
        {
//                render(new RenderTime(time));
        }
    }

    /**
     * Method to navigate to a blog post found by its title
     *
     * @param title - Blog post title
     */
    public HtmlPage openBlogPost(String title)
    {
        WebDroneUtil.checkMandotaryParam("title", title);
        try
        {
            drone.find(By.xpath(String.format("//a[contains(text(),'%s')]", title))).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException("Exceeded time to find blog post. " + e.toString());
        }
        return FactoryWqsPage.resolveWqsPage(drone).render();
    }

    /**
     * Method to click a blog post name from share
     *
     * @param blogNameFromShare of the blog post declared in share!
     */
    public HtmlPage clickBlogNameFromShare(String blogNameFromShare)
    {
        WebDroneUtil.checkMandotaryParam("blogNameFromShare", blogNameFromShare);
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]", blogNameFromShare))).click();
            return FactoryWqsPage.resolveWqsPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }
    }
}
