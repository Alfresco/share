/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.wqs;

import org.alfresco.po.share.SharePage;
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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

public abstract class WcmqsAbstractPage extends SharePage
{
    public final static By PAGE_MENU = By.cssSelector("div[id='myslidemenu']");
    public final static By CONTACT_MENU = By.cssSelector("div.link-menu");
    public final static By CONTACT_MENU_URL = By.cssSelector("div[class='link-menu']>ul>li>a");
    public final static By ALFRESCO_LOGO = By.cssSelector("div[id='logo']");
    public final static By ALFRESCO_LOGO_URL = By.cssSelector("div[id='logo']>a");
    public final static By SEARCH_FIELD = By.cssSelector("input[id='search-phrase']");
    public final static By SEARCH_BUTTON = By.cssSelector("input.input-arrow");
    public final static By ALFRSCO_BOTTOM_URL = By.cssSelector("div[id='footer']>div[class='copyright']>a");
    public final static By NEWS_MENU = By.cssSelector("a[href$='news/']");
    public final static By HOME_MENU = By.xpath("//div[@id='myslidemenu']//a[text()='Home']");
    public final static By PUBLICATIONS_MENU = By.cssSelector("a[href$='publications/']");
    public final static By BLOG_MENU = By.cssSelector("a[href$='blog/']");
    public final static String NEWS_MENU_STR = "news";
    public final static String BLOG_MENU_STR = "blog";
    public static final String PUBLICATIONS_MENU_STR = "publications";
    public static final String HOME_MENU_STR = "home";
    private static Log logger = LogFactory.getLog(WcmqsAbstractPage.class);
    private final By TAG_LIST = By.cssSelector("div.blog-categ");

    public WcmqsAbstractPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractPage render(RenderTime renderTime)
    {
        elementRender(renderTime, getVisibleRenderElement(PAGE_MENU), getVisibleRenderElement(CONTACT_MENU));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WcmqsAbstractPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to verify the contact link exists
     *
     * @return
     */
    public boolean isContactLinkDisplay()
    {
        try
        {
            return drone.findAndWait(CONTACT_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the page menu exists
     *
     * @return
     */
    public boolean isPageMenuDisplay()
    {
        try
        {
            return drone.findAndWait(PAGE_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the Alfresco logo exists
     *
     * @return
     */
    public boolean isAlfrescoLogoDisplay()
    {
        try
        {
            return drone.findAndWait(ALFRESCO_LOGO).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Method to verify the search field with search button exists
     *
     * @return
     */
    public boolean isSearchFieldWithButtonDisplay()
    {
        try
        {
            return drone.findAndWait(SEARCH_FIELD).isDisplayed() && drone.findAndWait(SEARCH_BUTTON).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    public boolean isBottomUrlDisplayed()
    {
        try
        {
            drone.findAndWait(ALFRSCO_BOTTOM_URL);
            return true;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find bottom URL. " + e.toString());
        }
    }

    public HtmlPage selectMenu(String menuOption)
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
            default:
            {
                webElement = drone.findAndWait(By.cssSelector(String.format("a[href$='%s/']", menuOption)));
                break;
            }

        }
        try
        {
            webElement.click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click " + menuOption + " menu. " + e.toString());
        }
        return FactoryWqsPage.resolveWqsPage(drone);
    }

    public WcmqsHomePage clickWebQuickStartLogo()
    {
        try
        {
            drone.findAndWait(ALFRESCO_LOGO_URL).click();
            return new WcmqsHomePage(drone);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click the Alfresco Web Quick Start Logo");
        }
    }

    public HtmlPage clickContactLink()
    {
        try
        {
            drone.findAndWait(CONTACT_MENU_URL).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click the contact link.");
        }
        return FactoryWqsPage.resolveWqsPage(drone);
    }

    public void clickAlfrescoLink()
    {
        try
        {
            drone.findAndWait(ALFRSCO_BOTTOM_URL).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find and click the bottom Alfresco link.");
        }
    }

    /**
     * Method to input test is the search field
     *
     * @return
     */
    public void inputTextInSearchField(String searchedText)
    {
        drone.findAndWait(SEARCH_FIELD).clear();
        drone.findAndWait(SEARCH_FIELD, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).sendKeys(searchedText);
    }

    /**
     * Method to click search button
     *
     * @return
     */
    public void clickSearchButton()
    {
        drone.findAndWait(SEARCH_BUTTON).click();
    }

    /**
     * Method to enter searched text and click search button
     *
     * @return
     */
    public HtmlPage searchText(String searchedText)
    {
        try
        {
            logger.info("Search text " + searchedText);
            inputTextInSearchField(searchedText);
            clickSearchButton();
            return FactoryWqsPage.resolveWqsPage(drone);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("Exceeded time to find search button. " + te.toString());
        }
    }

    /**
     * Method to click a news title
     *
     * @param newsTitle - the title of the news in wcmqs site
     * @return
     */
    public HtmlPage clickLinkByTitle(String newsTitle)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(text(),\"%s\")]", newsTitle))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

        return FactoryWqsPage.resolveWqsPage(drone);

    }

    /**
     * Method to click a news title
     *
     * @param newsTitle - the title of the news in wcmqs site
     * @param section   - the class of the section where you want to search
     * @return
     */
    public void clickLinkByTitle(String newsTitle, String section)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//*[@class=\"" + section + "\"]//a[contains(text(),\"%s\")]", newsTitle))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news link. " + e.toString());
        }

    }

    /**
     * Method to click on a document from any publication root page
     *
     * @param documentTitle
     */
    public void clickImageLink(String documentTitle)
    {
        try
        {
            drone.findAndWait(By.xpath(String.format("//a[contains(text(),\"%s\")]/../..//img", documentTitle))).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find Document link. " + e.toString());
        }
    }

    /**
     * Method to get text from the search field
     *
     * @return search field content
     */
    public String getTextFromSearchField()
    {
        return drone.findAndWait(SEARCH_FIELD).getAttribute("value");
    }

    /**
     * Method to check if image link for a title is displayed
     *
     * @param documentTitle
     */
    public boolean isImageLinkForTitleDisplayed(String documentTitle)
    {
        try
        {
            return drone.find(By.xpath(String.format("//a[contains(text(),\"%s\")]/../..//img", documentTitle))).isDisplayed();
        }
        catch (NoSuchElementException nse)
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
        try
        {
            ArrayList<String> taglist = new ArrayList<String>();
            WebElement sectionTags = drone.findAndWait(TAG_LIST);
            List<WebElement> tags = sectionTags.findElements(By.cssSelector("a"));
            if (tags.size() == 0)
            {
                taglist.add("None");
            }
            else
            {
                for (WebElement tag : tags)
                {
                    taglist.add(tag.getText());
                }
            }
            return taglist;
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find the tags ", e);
        }
    }

    /**
     * Method to get the headline titles from right side of news Page
     *
     * @return List<ShareLink>
     */
    public List<WebElement> getTagLinks()
    {
        try
        {
            WebElement sectionTags = drone.findAndWait(TAG_LIST);
            List<WebElement> links = sectionTags.findElements(By.cssSelector("a"));
            return links;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to access news site data", nse);
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
            return FactoryWqsPage.resolveWqsPage(drone).render();

        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Exceeded time to find news links. ", e);
        }
    }

}
