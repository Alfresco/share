package org.alfresco.po.wqs;

import org.alfresco.po.share.ShareLink;
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
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public class WcmqsHomePage extends WcmqsAbstractPage
{
    private static Log logger = LogFactory.getLog(WcmqsHomePage.class);
    private final By HOME_MENU = By.xpath("//div[@id='myslidemenu']//a[text()='Home']");
    private final By NEWS_MENU = By.cssSelector("a[href$='news/']");
    private final By BLOG_MENU = By.cssSelector("a[href$='blog/']");
    private final By FIRST_ARTICLE = By.cssSelector("div[id='left'] div.interior-content ul>li:nth-child(1)>h4>a");
    private final By BANNER = By.cssSelector("ul[id='slideshow']");
    private final By PUBLICATIONS_MENU = By.cssSelector("a[href$='publications/']");
    private final By RESEARCH_REPORTS = By.cssSelector("a[href$='research-reports/']");
    private final By WHITE_PAPERS = By.cssSelector("a[href$='white-papers/']");
    private final By SLIDE_READ_MORE = By.cssSelector("div[class='slideshow-rm']>a");
    private final By NEWS_AND_ANALYSIS_PANEL = By.cssSelector("div[id='left']");
    private final By FEATURED_SECTION = By.cssSelector("div[class='h-box-1']>div[class='services-box']>h3");
    private final By EXAMPLE_FEATURE_SECTION = By.cssSelector("div[class='h-box-2']>div[class='address-box']>div");
    private final By LATEST_BLOG_ARTICLES = By.cssSelector("div[id='right']>div[class='latest-news']");
    public static final String SECTION_NEWSLIST = "newslist-wrapper";
    public static final String SECTION_SERVICES = "services-box";
    public static final String SECTION_ADDRESSBOX = "address-box";
    public static final String SECTION_LATESTNEWS = "latest-news";

    // private final By BLOG_ARTICLE=By.cssSelector("div[id='left'] div.interior-content div.blog-entry:nth-child(2)>h2>a");
    // private final By RIGHT_PANEL = By.cssSelector("div[id='right']");

    public WcmqsHomePage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(BANNER));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsHomePage render(final long time)
    {
        return render(new RenderTime(time));
    }


    /**
     * Method to open the articles from WQS Home Page
     *
     * @return WcmqsNewsArticleDetails ( if the article is opened first time --  WcmqsLoginPage)
     */


    public WcmqsNewsArticleDetails selectFirstArticleFromLeftPanel()
    {
        logger.info("Selecting first article from left panel.");
        List<WebElement> allArticles = drone.findAll(FIRST_ARTICLE);
        try
        {
            allArticles.get(0).click();
        }
        catch (IndexOutOfBoundsException e)
        {
            logger.error("The first article was not found ", e);
        }

        return new WcmqsNewsArticleDetails(drone);
    }

    /**
     * Method to get all the folders for a selected Primary folder (eg: News, Publications, Blog)
     * 
     * @return List<ShareLink>
     */
    public List<ShareLink> getAllFoldersFromMenu(String folderName)
    {
        List<ShareLink> folders = new ArrayList<ShareLink>();
        try
        {
            WebElement folder = drone.findAndWait(BLOG_MENU);
            drone.mouseOver(folder);

            List<WebElement> firstFolders = drone.findAll(By.xpath(String.format(".//*[@id='myslidemenu']//a[contains(@href,'%s')]", folderName)));

            for (WebElement div : firstFolders)
            {
                folders.add(new ShareLink(div, drone));
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
        }

        return folders;
    }

    /**
     * Method to wait for a slide title in banner and then click it
     * 
     * @param slideName - the title of the slide declared in share!
     */
    public void waitForAndClickSlideInBanner(String slideName)
    {
        By banner = By.xpath(String.format("//div/a[contains(@href,'%s')]", slideName));
        drone.waitUntilElementPresent(banner, 5);
        drone.find(banner).click();
    }

    public boolean isResearchReportsDisplayed()
    {
        return drone.isElementDisplayed(RESEARCH_REPORTS);
    }

    public boolean isWhitePapersDisplayed()
    {
        return drone.isElementDisplayed(WHITE_PAPERS);
    }

    public void mouseOverMenu(String menuOption)
    {
        WebElement webElement = null;
        switch (menuOption.toLowerCase())
        {

            case "home":
            {
                webElement = drone.findAndWait(HOME_MENU);
                break;
            }
            case "news":
            {
                webElement = drone.findAndWait(NEWS_MENU);
                break;
            }
            case "publications":
            {
                webElement = drone.findAndWait(PUBLICATIONS_MENU);
                break;
            }
            case "blog":
            {
                webElement = drone.findAndWait(BLOG_MENU);
                break;
            }

        }
        try
        {
            drone.mouseOver(webElement);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click " + menuOption + " menu. " + e.toString());
        }
    }

    public HtmlPage openPublicationsPageFolder(String folderName)
    {
        try
        {

            WebElement menu = drone.findAndWait(PUBLICATIONS_MENU);

            drone.mouseOver(menu);
            switch (folderName.toLowerCase())
            {
                case "white papers":
                {

                    drone.findAndWait(WHITE_PAPERS).click();
                    break;
                }

                case "research reports":
                {
                    drone.findAndWait(RESEARCH_REPORTS).click();
                    break;
                }

            }
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news links. " + e.toString());
        }

        return FactoryWqsPage.resolveWqsPage(drone);
    }

    public boolean isSlideReadMoreButtonDisplayed()
    {
        try
        {
            drone.findAndWait(SLIDE_READ_MORE);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Read More. " + e.toString());
        }
    }

    public boolean isNewsAndAnalysisSectionDisplayed()
    {
        try
        {
            drone.findAndWait(NEWS_AND_ANALYSIS_PANEL);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find News and Analysis. " + e.toString());
        }
    }

    public boolean isFeaturedSectionDisplayed()
    {
        try
        {
            drone.findAndWait(FEATURED_SECTION);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Featured Section. " + e.toString());
        }
    }

    public boolean isExampleFeatureSectionDisplayed()
    {
        try
        {
            drone.findAndWait(EXAMPLE_FEATURE_SECTION);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Example Feature Section. " + e.toString());
        }
    }

    public boolean isLatestBlogArticlesDisplayed()
    {
        try
        {
            drone.findAndWait(LATEST_BLOG_ARTICLES);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Latest Blog Articles. " + e.toString());
        }
    }

    /*
     * Clicks on a slideshow readme button
     * @param slideNr 1 2 or 3
     */
    public void clickOnSlideShowReadme(Integer slideNr)
    {
        long timer = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < timer)
        {

            try
            {
                drone.executeJavaScript("slideSwitch()");
                drone.find(By.xpath("//ul[@id='slideshow']//a[contains(@href,\"slide" + slideNr + "\")]")).click();
                return;
            }
            catch (TimeoutException e)
            {

            }
        }

        throw new PageOperationException("Exceeded time to find slide " + slideNr + " readme button");

    }

}
