/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UnknownSharePage;
import org.alfresco.po.share.search.FacetedSearchScopeMenu.ScopeMenuSelectedItemsMenu;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * The Class FacetedSearchPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class FacetedSearchPage extends SharePage implements SearchResultPage
{

    /** Constants */
    private static final By SEARCH_MENU_BAR = By.id("FCTSRCH_TOP_MENU_BAR");
    private static final By RESULT = By.cssSelector("tr.alfresco-search-AlfSearchResult");
    private static final By CONFIGURE_SEARCH = By.cssSelector("div[id=FCTSRCH_CONFIG_PAGE_LINK]");
    private static final Log logger = LogFactory.getLog(FacetedSearchPage.class);
    private static final String goToAdvancedSearch = "span#HEADER_ADVANCED_SEARCH_text";
    private static final By SEARCH_RESULTS_LIST = By.cssSelector("div[id='FCTSRCH_SEARCH_RESULTS_LIST']");

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render()
     */
    @Override
    public FacetedSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render(org.alfresco.po.RenderTime)
     */
    @Override
    public FacetedSearchPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException e)
                {
                }
            }
            try
            {

                if (driver.findElement(SEARCH_MENU_BAR).isDisplayed() && driver.findElement(SEARCH_RESULTS_LIST).isDisplayed())
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    /**
     * Gets the search form.
     * 
     * @return {@link FacetedSearchForm}
     */
    public FacetedSearchForm getSearchForm()
    {
        return new FacetedSearchForm(driver, factoryPage);
    }

    /**
     * Gets the sort.
     * 
     * @return {@link FacetedSearchSort}
     */
    public FacetedSearchSort getSort()
    {
        return new FacetedSearchSort(driver, factoryPage);
    }

    /**
     * Gets the results.
     * 
     * @return List
     */
    public List<SearchResult> getResults()
    {
        List<SearchResult> results = new ArrayList<SearchResult>();
        List<WebElement> response = driver.findElements(RESULT);
        for (WebElement result : response)
        {
            results.add(new FacetedSearchResult(driver, result, factoryPage));
        }
        return results;
    }

    /**
     * Gets a result by its title if it exists.
     *
     * @param title String
     * @return the result
     */
    public SearchResult getResultByTitle(String title)
    {
        try
        {
            for (SearchResult facetedSearchResult : getResults())
            {
                if (facetedSearchResult.getTitle().equals(title))
                {
                    return facetedSearchResult;
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the title : ", e);
        }

        throw new PageOperationException("Unable to get the title  : ");

    }

    /**
     * Gets the view.
     * ^M
     * 
     * @return {@link FacetedSearchView}
     */
    public FacetedSearchView getView()
    {
        return factoryPage.instantiatePage(driver, FacetedSearchView.class).render();
    }

    /**
     * Gets a result by its name if it exists.
     *
     * @param name String
     * @return the result
     */
    public SearchResult getResultByName(String name)
    {
        try
        {
            for (SearchResult facetedSearchResult : getResults())
            {
                if (facetedSearchResult.getName().equals(name))
                {
                    return facetedSearchResult;
                }
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the name : ", e);
        }

        throw new PageOperationException("Unable to get the name  : ");

    }

    /**
     * Scroll to page bottom.
     */
    public void scrollSome(int distance)
    {
        executeJavaScript("window.scrollTo(0," + distance + ");", "");
    }

    /**
     * Scroll to page bottom.
     */
    public void scrollToPageBottom()
    {
        executeJavaScript(
                "window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));", "");
    }

    /**
     * Gets the current url hash.
     *
     * @return the url hash
     */
    public String getUrlHash()
    {
        String url = this.driver.getCurrentUrl();

        // Empty url or no #
        if (StringUtils.isEmpty(url) || !StringUtils.contains(url, "#"))
        {
            return null;
        }

        return StringUtils.substringAfter(url, "#");
    }

    /**
     * Get the numeric value display on top of search results.
     * The number indicates the total count found for given search.
     * 
     * @return int
     */
    public int getResultCount()
    {
        String val = driver.findElement(By.cssSelector("#FCTSRCH_RESULTS_COUNT_LABEL")).getText();
        String count = val.split("-")[0].trim();
        if (StringUtils.isEmpty(count))
        {
            throw new RuntimeException("Unable to find result count info");
        }
        return Integer.valueOf(count).intValue();
    }

    /**
     * Select the facet that matches the title from the facet grouping.
     * 
     * @param title facet name, eg Microsoft Word
     * @return FacetedSearchPage with filtered results
     */
    public FacetedSearchPage selectFacet(final String title)
    {
        PageUtils.checkMandatoryParam("Facet title", title);
        WebElement facet = driver.findElement(By.xpath(String.format("//span[@class = 'filterLabel'][contains(., '%s')]", title)));
        facet.click();
        return this;
    }

    /**
     * Click on configure search Link. *
     * 
     * @return the FacetedSearchConfigpage
     */
    public FacetedSearchConfigPage clickConfigureSearchLink()
    {
        try
        {
            WebElement configure_search = driver.findElement(CONFIGURE_SEARCH);
            if (configure_search.isDisplayed())
            {
                configure_search.click();
            }
            return getCurrentPage().render();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the link : " + e.getMessage());
        }

        throw new PageException("Unable to find the link : ");
    }

    /**
     * verify configureSearchlink is displayed
     * 
     * @param driver WebDrone
     * @return Boolean
     */
    public Boolean isConfigureSearchDisplayed(WebDriver driver)
    {
        try
        {
            WebElement configure_search = driver.findElement(CONFIGURE_SEARCH);
            if (configure_search.isDisplayed())
            {
                return true;
            }

        }

        catch (NoSuchElementException te)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find configure search link");
            }
        }
        return false;
    }

    @Override
    public boolean hasResults()
    {
        return !getResults().isEmpty();
    }

    @Override
    public HtmlPage selectItem(String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Search row name is required");
        }
        try
        {
            String selector = String.format("//span[@class = 'value'][contains(., '%s')]", name);
            driver.findElement(By.xpath(selector)).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %s item not found", name), e);
        }

        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
    }

    @Override
    public HtmlPage selectItem(int number)
    {
        if (number < 0)
        {
            throw new IllegalArgumentException("Value can not be negative");
        }
        number += 1;
        try
        {
            String selector = String.format("tr.alfresco-search-AlfSearchResult:nth-of-type(%d) div.nameAndTitleCell a", number);
            WebElement row = driver.findElement(By.cssSelector(selector));
            row.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %d item not found", number), e);
        }

        return getCurrentPage();
    }

    public boolean isItemPresentInResultsList(SitePageType pageType, String itemName)
    {
        String thumbnailImg;
        boolean isPresent = false;
        List<SearchResult> searchResults = getResults();

        for (SearchResult result : searchResults)
        {
            if (result.getName().equals(itemName))
            {
                switch (pageType)
                {
                    case WIKI:
                        thumbnailImg = "wiki-page.png";
                        break;
                    case BLOG:
                        thumbnailImg = "topic-post.png";
                        break;
                    case CALENDER:
                        thumbnailImg = "calendar-event.png";
                        break;
                    case DATA_LISTS:
                        thumbnailImg = "datalist.png";
                        break;
                    case DISCUSSIONS:
                        thumbnailImg = "topic-post.png";
                        break;
                    case LINKS:
                        thumbnailImg = "link.png";
                        break;
                    default:
                        thumbnailImg = "doclib";
                        break;
                }

                isPresent = result.getThumbnail().contains(thumbnailImg);

                //Check for Folder
                if  (thumbnailImg == "doclib")
                {
                    isPresent = result.getThumbnail().contains(thumbnailImg) || result.getThumbnail().contains("folder");
                }
                if (isPresent)
                    break;
            }
        }
        return isPresent;
    }

    public boolean isPageCorrect()
    {
        boolean isCorrect;
        isCorrect = driver.findElement(getSearchForm().SEARCH_FIELD).isDisplayed() &&
                    driver.findElement(getSearchForm().SEARCH_BUTTON).isDisplayed() &&
                    driver.findElement(RESULT).isDisplayed() &&
                    getSort().isSortCorrect();
        return isCorrect;
    }

    public boolean isGoToAdvancedSearchPresent()
    {
        return driver.findElement(By.cssSelector(goToAdvancedSearch)).isDisplayed();
    }

    public List<FacetedSearchFacetGroup> getFacetGroups()
    {
        List<WebElement> facetGroups = driver.findElements(By.cssSelector("div.alfresco-documentlibrary-AlfDocumentFilters:not(.hidden)"));
        List<FacetedSearchFacetGroup> filters = new ArrayList<FacetedSearchFacetGroup>();
        for (WebElement facetGroup : facetGroups)
        {
            filters.add(new FacetedSearchFacetGroup(driver, facetGroup));
        }
        return filters;
    }

    public FacetedSearchScopeMenu getScopeMenu()
    {
        return new FacetedSearchScopeMenu(driver, factoryPage);
    }

    @FindBy(css = "input[name='searchTerm']") TextInput search;
    @FindBy(css = "span[role='button']") WebElement searchBtn;
    public HtmlPage search(String searchTerm)
    {
        search.clear();
        search.sendKeys(searchTerm);
        searchBtn.click();
        return this;
    }

    /**
     * Get FacetedSearchPage sub navigation.
     *
     * @return {@link FacetedSearchBulkActions} object.
     */
    // FacetedSearchBulkActions facetedSearchBulkActions;

    public FacetedSearchBulkActions getNavigation()
    {
        // return facetedSearchBulkActions;
        return new FacetedSearchBulkActions(driver, factoryPage);
    }

    /**
     * Method to select the Search Scope on FacetedSearchResultsPage
     * 
     * @param {@link ScopeMenuSelectedItemsMenu} scope
     * @return HtmlPage
     */
    public HtmlPage selectSearchScope(ScopeMenuSelectedItemsMenu scope)
    {
        FacetedSearchScopeMenu menu = getScopeMenu();
        return menu.selectScope(scope).render();
    }

}
