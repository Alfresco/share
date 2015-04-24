package org.alfresco.po.wqs;

import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public class WcmqsNewsArticleDetails extends WcmqsAbstractArticlePage
{
    private final By ARTICLE_BODY = By.cssSelector("div.article-body");
    private final By TITLE_LINK = By.cssSelector("div.interior-content>h2");
    private final By DETAILS_LINK = By.cssSelector("div.interior-content span.ih-date");
    private final By ARTICLE_IMG = By.cssSelector("div.article-body img");
    private final By TAGS_SECTION = By.cssSelector("h3.tag-list");
    private final By TAG_LIST = By.cssSelector("ul.tag-list");

    private final By DELETE_LINK = By.cssSelector("a[class=alfresco-content-delete]");
    private final By DELETE_CONFIRM_OK = By.xpath("//button[contains(text(),'Ok')]");
    private final By DELETE_CONFIRM_CANCEL = By.xpath("//button[contains(text(),'Cancel')]");
    private final By DELETE_CONFIRM_WINDOW = By.id("prompt_c");

    public WcmqsNewsArticleDetails(WebDrone drone)
    {
        super(drone);
    }

    public static WcmqsNewsArticleDetails getCurrentNewsArticlePage(WebDrone drone)
    {
        WcmqsNewsArticleDetails currentPage = new WcmqsNewsArticleDetails(drone);
        currentPage.render();
        return currentPage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsArticleDetails render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(ARTICLE_BODY));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsArticleDetails render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsNewsArticleDetails render(final long time)
    {
        return render(new RenderTime(time));
    }

    public String getTitleOfNewsArticle()
    {
        try
        {
            return drone.findAndWait(TITLE_LINK).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find title of the article. " + e.toString());
        }

    }

    public String getBodyOfNewsArticle()
    {
        try
        {
            return drone.findAndWait(ARTICLE_BODY).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find body of the article. " + e.toString());
        }

    }

    public String getDetailsOfNewsArticle()
    {
        try
        {
            return drone.findAndWait(DETAILS_LINK).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find deatils of the article. " + e.toString());
        }

    }

    /**
     * Method to get the link name from "Form" section
     *
     * @return the text of the FromLink
     */
    public String getFromLinkName()
    {
        try
        {
            WebElement detailLink = drone.findAndWait(DETAILS_LINK);
            return detailLink.findElement(By.cssSelector("a")).getText();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find deatils of the article. ", e);
        }
    }

    public boolean isNewsArticleImageDisplayed()
    {
        try
        {
            return drone.find(ARTICLE_IMG).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }

    }

    public boolean isTagsSectionDisplayed()
    {
        try
        {
            return drone.find(TAGS_SECTION).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }

    }

    /**
     * Method to get all tags in section tag list as text
     *
     * @return List<String> Contains "None" in case of empty list
     */
    public List<String> getTagList()
    {

        ArrayList<String> taglist = new ArrayList<String>();
        WebElement sectionTags = null;
        try
        {
            sectionTags = drone.find(TAG_LIST);
        }
        catch (NoSuchElementException e)
        {
            taglist.add("None");
        }

        List<WebElement> tags = sectionTags.findElements(By.cssSelector("a"));
        for (WebElement tag : tags)
        {
            taglist.add(tag.getText());
        }
        return taglist;

    }

    public WcmqsNewsPage clickComponentLinkFromSection(String componentLink)
    {
        try
        {
            WebElement detailLink = drone.findAndWait(DETAILS_LINK);
            detailLink.findElement(By.cssSelector(String.format("a[href*=\"%s\"]", componentLink))).click();
            return new WcmqsNewsPage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find deatils of the article. ", e);
        }
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

    public HtmlPage confirmArticleDelete() throws InterruptedException
    {
        try
        {
            drone.findAndWait(DELETE_CONFIRM_OK).click();
            Thread.sleep(1000);
            drone.waitUntilElementDisappears(DELETE_CONFIRM_WINDOW, maxPageLoadingTime);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find delete button. " + e.toString());
        }
        
        return FactoryWqsPage.resolveWqsPage(drone);
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

}
