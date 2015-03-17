package org.alfresco.po.share.wqs;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The news page that is opened for a folder (Global Economy, Companies, Markets .... )
 * 
 * @author bogdan.bocancea
 */

public class WcmqsNewsPage extends WcmqsAbstractPage
{

    private final By DELETE_LINK = By.cssSelector("a[class=alfresco-content-delete]");
    private final By DELETE_CONFIRM_OK = By.xpath("//button[contains(text(),'Ok')]");
    private final By DELETE_CONFIRM_CANCEL = By.xpath("//button[contains(text(),'Cancel')]");
    private final By DELETE_CONFIRM_WINDOW = By.id("prompt_c");

    public static final String FTSE_1000 = "FTSE 100 rallies from seven-week low";
    public static final String GLOBAL_CAR_INDUSTRY = "Global car industry";
    public static final String FRESH_FLIGHT_TO_SWISS = "Fresh flight to Swiss franc as Europe's bond strains return";
    public static final String HOUSE_PRICES = "House prices face rollercoaster ride";
    public static final String EUROPE_DEPT_CONCERNS = "Europe debt concerns ease but bank fears remain";
    public static final String INVESTORS_FEAR = "Investors fear rising risk of US regional defaults";
    public static final String CREDIT_CARDS = "Credit card interest rates rise";
    public static final String EXPERTS_WEIGHT_STOCKS = "Experts Weigh Stocks, the Dollar, and the 'Fiscal Hangover'";

    public static final String NEWS = "news";
    public static final String GLOBAL = "global";
    public static final String COMPANIES = "companies";
    public static final String MARKETS = "markets";
    public static final String COLLECTIONS = "collections";
    public static final String SECTION_ARTICLES = "section.articles";
    public static final String RELATED_ARTICLES_SECTION = "services-box";

    public static final String ARTICLE_4 = "article4.html";
    public static final String ARTICLE_3 = "article3.html";
    public static final String ARTICLE_2 = "article2.html";
    public static final String ARTICLE_1 = "article1.html";
    public static final String ARTICLE_6 = "article6.html";
    public static final String ARTICLE_5 = "article5.html";

    protected static String TITLES_NEWS = "//div[@id='left']//div[@class='interior-content']//a//.././/./.././a";
    protected static By RIGHT_TITLES_NEWS = By.cssSelector("div[id='right'] ul");

    @RenderWebElement
    private final By NEWS_MENU = By.cssSelector("a[href$='news/']");

    private final By RSS_LINK = By.xpath("//a[text()='Subscribe to RSS']");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public WcmqsNewsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to get the headline titles from news Page
     * 
     * @return List<ShareLink>
     */
    public List<ShareLink> getHeadlineTitleNews()
    {
        List<ShareLink> titles = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = drone.findAll(By.xpath(TITLES_NEWS));
            for (WebElement div : links)
            {
                titles.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return titles;
    }

    /**
     * Method to get the date and time for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return String news Date and Time
     */
    public String getDateTimeNews(String newsName)
    {
        try
        {
            return drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]//.././/./.././span[@class='newslist-date']", newsName))).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news links. " + e.toString());
        }

    }

    /**
     * Method to get the date and time for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return String news Date and Time
     */
    public boolean isDateTimeNewsPresent(String newsName)
    {
        boolean present = false;

        try
        {
            present = drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]//.././/./.././span[@class='newslist-date']", newsName)))
                    .isDisplayed();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

        return present;

    }

    /**
     * Method to get the description for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return String news description
     */
    public String getNewsDescrition(String newsName)
    {
        try
        {
            return drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]//.././/./.././p", newsName))).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

    }

    /**
     * Method title the title for a news
     * 
     * @param newsName - the of the news declared in share!
     * @return
     */
    public String getNewsTitle(String newsName)
    {
        try
        {
            return drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]", newsName))).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the news title:" + e.toString());
        }

    }

    /**
     * Method to click a news title
     * 
     * @param newsName - the title of the news declared in share!
     * @return
     */
    public void clickNewsByName(String newsName)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(@href,'%s')]", newsName))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

    }

    /**
     * Method to navigate to news folders
     * 
     * @param folderName - the Name of the folder from SHARE
     * @return WcmqsNewsPage
     */
    public WcmqsNewsPage openNewsPageFolder(String folderName)
    {
        try
        {
            WebElement news = drone.findAndWait(NEWS_MENU);
            drone.mouseOver(news);

            drone.findAndWait(By.cssSelector(String.format("a[href$='/wcmqs/news/%s/']", folderName))).click();

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news links. " + e.toString());
        }

        return new WcmqsNewsPage(drone);
    }

    public boolean checkIfBlogIsDeleted(String title)
    {
        boolean check = false;
        try
        {
            drone.waitUntilElementDisappears(By.xpath(String.format("//a[contains(text(),'%s')]", title)),
                    SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            check = true;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

        return check;
    }

    public boolean checkIfNewsExists(String title)
    {
        boolean check = false;
        try
        {
            drone.waitForElement(By.xpath(String.format("//h2[contains(text(),\"%s\")]", title)), SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            check = true;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }

        return check;
    }

    /**
     * Presses the delete button while you are in blog editing
     */
    public void deleteArticle()
    {

        try
        {
            drone.findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
    }

    /**
     * Verifies if delete confirmation window is displayed
     * 
     * @return boolean
     */
    public boolean isDeleteConfirmationWindowDisplayed()
    {
        boolean check = false;
        try
        {

            drone.waitForElement(DELETE_CONFIRM_WINDOW, SECONDS.convert(drone.getDefaultWaitTime(), MILLISECONDS));
            check = true;
        }
        catch (NoSuchElementException nse)
        {
        }

        return check;
    }

    public void confirmArticleDelete()
    {
        try
        {
            drone.findAndWait(DELETE_CONFIRM_OK).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
    }

    public void cancelArticleDelete()
    {
        try
        {
            drone.findAndWait(DELETE_CONFIRM_CANCEL).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
    }

    /**
     * Method to get the headline titles from right side of news Page
     * 
     * @return List<ShareLink>
     */
    public List<ShareLink> getRightHeadlineTitleNews()
    {
        List<ShareLink> titles = new ArrayList<ShareLink>();
        try
        {
            List<WebElement> links = drone.findAll(RIGHT_TITLES_NEWS);
            for (WebElement div : links)
            {
                titles.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return titles;
    }

    /**
     * Method to verify if the Subscribe to RSS link is displayed
     * 
     * @param
     * @return is displayed
     */
    public boolean isRSSLinkDisplayed()
    {
        try
        {
            return drone.find(RSS_LINK).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }

    }

}
