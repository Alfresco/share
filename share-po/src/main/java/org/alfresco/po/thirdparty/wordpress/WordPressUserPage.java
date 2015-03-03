package org.alfresco.po.thirdparty.wordpress;

import static com.google.common.base.Preconditions.checkArgument;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.concurrent.TimeUnit;

import org.alfresco.webdrone.Page;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */
public class WordPressUserPage extends Page
{
    private static final Logger logger = Logger.getLogger(WordPressUserPage.class);
    private static final By WP_WRAP = By.cssSelector("#wrap");
    private static final By SITE_TITLE = By.cssSelector("#site-title");
    private final static By SEARCH_INPUT = By.cssSelector(".search-input");
    private final static By SEARCH_SUBMIT = By.cssSelector(".searchsubmit");
    private static final int retrySearchCount = 3;

    public WordPressUserPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressUserPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(WP_WRAP), getVisibleRenderElement(SITE_TITLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressUserPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressUserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    //TODO Temporary method. Before elementRender from SharePage didn't moved out to Page.
    private void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        for (RenderElement element : elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(drone, waitSeconds);
            }
            catch (TimeoutException e)
            {
                throw new PageRenderTimeException("element not rendered in time.");
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }

    private WordPressUserPage search(String text)
    {
        try
        {
            WebElement inputElement = drone.findAndWait(SEARCH_INPUT);
            inputElement.clear();
            inputElement.sendKeys(text);
            drone.findAndWait(SEARCH_SUBMIT).click();

        }
        catch (TimeoutException e)
        {
            logger.error("Not able to search ", e);
        }
        return new WordPressUserPage(drone).render();
    }

    public boolean isPostPresent(String postTitle)
    {
        boolean isPresent = false;
        By thePost = By.xpath(String.format("//article//a[text()='%s']", postTitle));
        logger.info("Start search with retry on WordPress User Page");
        int counter = 0;
        int waitInMilliSeconds = 4000;
        while (counter < retrySearchCount)
        {
            search(postTitle);
            isPresent = drone.isElementDisplayed(thePost);
            if (isPresent)
            {
                break;
            }
            counter++;
            synchronized (this)
            {
                try
                {
                    this.wait(waitInMilliSeconds);
                }
                catch (InterruptedException e)
                {
                    throw new PageException("Blog User page failed to retrieve results");
                }
            }
        }
        return isPresent;
    }

    public boolean isPostRemoved(String postTitle)
    {
        boolean isPresent;
        int waitInMilliSeconds = 2000;
        By thePost = By.xpath(String.format("//article//a[text()='%s']", postTitle));
        isPresent = drone.isElementDisplayed(thePost);
        if (isPresent)
            for (int retryCount = 1; retryCount < 3; retryCount++)
            {
                logger.info("Waiting for " + 3000 / 1000 + " seconds");
                synchronized (this)
                {
                    try
                    {
                        this.wait(waitInMilliSeconds);
                    }
                    catch (InterruptedException e)
                    {
                        throw new PageException("Failed waiting for posts");
                    }
                }
                drone.refresh();
                isPresent = drone.isElementDisplayed(thePost);
                if (!isPresent)
                    break;
            }
        return !isPresent;
    }
}
