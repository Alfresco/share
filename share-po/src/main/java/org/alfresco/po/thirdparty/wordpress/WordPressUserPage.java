package org.alfresco.po.thirdparty.wordpress;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
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

    @SuppressWarnings("unchecked")
    @Override
    public WordPressUserPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(WP_WRAP), getVisibleRenderElement(SITE_TITLE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressUserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private WordPressUserPage search(String text)
    {
        try
        {
            WebElement inputElement = findAndWait(SEARCH_INPUT);
            inputElement.clear();
            inputElement.sendKeys(text);
            findAndWait(SEARCH_SUBMIT).click();

        }
        catch (TimeoutException e)
        {
            logger.error("Not able to search ", e);
        }
        return  factoryPage.instantiatePage(driver, WordPressUserPage.class).render();
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
            if (driver.findElement(thePost).isDisplayed())
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
        isPresent = driver.findElement(thePost).isDisplayed();
        if (driver.findElement(thePost).isDisplayed())
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
                driver.navigate().refresh();
                isPresent = driver.findElement(thePost).isDisplayed();
                if (!isPresent)
                {
                    break;
                }
            }
        return !isPresent;
    }
}
