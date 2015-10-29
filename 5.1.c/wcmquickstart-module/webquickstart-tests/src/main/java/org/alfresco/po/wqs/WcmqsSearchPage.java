package org.alfresco.po.wqs;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucian Tuca on 11/18/2014.
 */
public class WcmqsSearchPage extends WcmqsAbstractPage
{
    private static final String PAGINATION_BUTTON_NEXT = "//div[@class='body-rm']/a";
    private static final String PAGINATION_BUTTON_PREVIOUS = "//div[@class='reverse-arrow']/a";
    @RenderWebElement
    private final By SEARCH_RESULT_HEADER = By.xpath("//div[@class='interior-header']/*[text()='Search Results']");
    private final By TAG_SEARCH_RESULT_TITLES = By.cssSelector(".newslist-wrapper>li>h4>a");
    private final By NO_OF_SEARCH_RESULTS = By.cssSelector("p.intheader-paragraph");
    private final By LATEST_BLOG_ARTICLES = By.cssSelector("div[id='right']>div[class='latest-news']");
    private final By PAGINATION = By.cssSelector("div[class='pagination']>span[class='page-number']");

    /**
     * Constructor.
     *
     * @param drone WebDriver to access page
     */
    public WcmqsSearchPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsSearchPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsSearchPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to get tag search result titles
     */
    public ArrayList<String> getTagSearchResults()
    {
        ArrayList<String> results = new ArrayList<String>();
        try
            {
                List<WebElement> links = drone.findAll(TAG_SEARCH_RESULT_TITLES);
                for (WebElement div : links)
                {
                    results.add(div.getText());
                }
                if (isNextButtonDisplayed())
                {
                    WcmqsSearchPage wcmqsSearchPage = clickNextPage().render();
                    results.addAll(wcmqsSearchPage.getTagSearchResults());
                    clickPrevPage().render();
                }
              }
            catch (TimeoutException te)
            {
                // no exception is thrown because the list of results can be empty
            }
        return results;
    }

    /**
     * Method verifies the number of search results
     */
    public boolean verifyNumberOfSearchResultsHeader(int showOfResults, int noOfResults, String searchedText)
    {
        String resultsText = String.format("Showing %d of %d results for \"%s\" within the website...", showOfResults, noOfResults, searchedText);
        return resultsText.equals(drone.findAndWait(NO_OF_SEARCH_RESULTS).getText());
    }

    /**
     * Method returns if the Latest Blog Articles block is displayed
    */
    public boolean isLatestBlogArticlesDisplayed()
    {
        try
        {
            drone.findAndWait(LATEST_BLOG_ARTICLES);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Latest Blog Articles block. " + e.toString());
        }
    }

    /**
     * Method returns the pagination text
     *
     * @return
     */
    public String getWcmqsSearchPagePagination()
    {
        try
        {
            return drone.findAndWait(PAGINATION).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Pagination. " + e.toString());
        }
    }

    /**
     * Method to get latest blog articles titles
     */
    public ArrayList<String> getLatestBlogArticles()

    {
        ArrayList<String> blogArticles = new ArrayList<String>();
        try
        {
            WebElement latestNewsBlock = drone.find(LATEST_BLOG_ARTICLES);
            List<WebElement> links = latestNewsBlock.findElements(By.cssSelector("a"));
            for (WebElement div : links)
            {
                blogArticles.add(div.getText());
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find Latest Blog Articles", nse);
        }

        return blogArticles;
    }

    /**
     * Method to click a news title
     *
     * @param blogArticleTitle - the title of the blog article in wcmqs site
     */
    public void clickLatestBlogArticle(String blogArticleTitle)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[text()=\"%s\"]", blogArticleTitle))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }
    }

    /**
     * Selects the next or previous button on the pagination
     * bar based on the action required.
     *
     * @param drone {@link WebDrone}
     * @param xpath that identifies which button to select
     * @return WcmqsSearchPage Search results page
     */
    private WcmqsSearchPage selectPaginationButton(WebDrone drone, final String xpath)
    {
        try
        {
            WebElement pagination = drone.find(PAGINATION);
            WebElement button = pagination.findElement(By.xpath(xpath));
            button.click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Pagination link element was not found. " + nse.toString());
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find pagination links. " + te.toString());
        }

        return this.render();
    }

    /**
     * Click next page button on the
     * pagination bar.
     *
     * @return WcmqsSearchPage Search results page
     */
    public WcmqsSearchPage clickNextPage()
    {
        return selectPaginationButton(drone, PAGINATION_BUTTON_NEXT);

    }

    /**
     * Click prev page button on the
     * pagination bar.
     *
     * @return WcmqsSearchPage Search results page
     */
    public WcmqsSearchPage clickPrevPage()
    {
        return selectPaginationButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }

    public boolean isNextButtonDisplayed()
    {
        try
        {
            return drone.find(By.xpath(PAGINATION_BUTTON_NEXT)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    public boolean isPreviousButtonDisplayed()
    {
        try
        {
            return drone.find(By.xpath(PAGINATION_BUTTON_PREVIOUS)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }
}

