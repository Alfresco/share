package org.alfresco.po.share.wqs;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class WcmqsNewsArticleDetails extends WcmqsAbstractArticlePage
{
    private final By ARTICLE_BODY = By.cssSelector("div.article-body");
    private final By TITLE_LINK = By.cssSelector("div.interior-content>h2");
    private final By DETAILS_LINK = By.cssSelector("div.interior-content span.ih-date");
    private final By ARTICLE_IMG = By.cssSelector("div.article-body img");
    private final By TAGS_SECTION = By.cssSelector("h3.tag-list");
    private final By TAG_LIST = By.cssSelector("ul.tag-list");

    public WcmqsNewsArticleDetails(WebDrone drone)
    {
        super(drone);
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

    public static WcmqsNewsArticleDetails getCurrentNewsArticlePage(WebDrone drone)
    {
        WcmqsNewsArticleDetails currentPage = new WcmqsNewsArticleDetails(drone);
        currentPage.render();
        return currentPage;
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
}
