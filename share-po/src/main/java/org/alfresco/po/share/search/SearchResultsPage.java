/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.Pagination;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Search results page object, holds all element of the html page relating to
 * share's search results.
 * 
 * @author Michael Suzuki, Subashni Prasanna
 * @since 1.0
 */
public abstract class SearchResultsPage extends SharePage implements SearchResultPage
{
    private static final String SEARCH_INFO_DIV = "div.search-info";
    private static final String SEARCH_ON_SITE = "search.site.link";
    private static final String SEARCH_ON_ALL_SITES = "search.site.all.link";
    private static final String SEARCH_FIELD = "page_x002e_search_x002e_search_x0023_default-search-text";
    private static final String SEARCH_BUTTON = "page_x002e_search_x002e_search_x0023_default-search-button-button";
    private static final String SEARCH_RESULT_COUNT = "search.results.count";
    private static final String SEARCH_RESULT_PAGINATOR_ID = "search.results.paginator.id";
    private static final String SEARCH_RESULTS_DIV_ID = "search.results.div.id";
    private static final String CURRENT_POSITION_CSS = "span.yui-pg-current-page.yui-pg-page";
    private static final String PAGINATION_BUTTON_NEXT = "a.yui-pg-next";
    private static final String PAGINATION_BUTTON_PREVIOUS = "a.yui-pg-previous";
    private final String goToAdvancedSearch;
    private static final String SORT_BY_RELEVANCE = "button[id$='default-sort-menubutton-button']";
    private static final String SORT_LIST = "span[id$='-sort-menubutton'] + div>div.bd>ul>li>a";
    protected static final By BACK_TO_SITE_LINK = By.cssSelector("#HEADER_SEARCH_BACK_TO_SITE_DASHBOARD");

    /**
     * Constructor.
     */
    public SearchResultsPage(WebDrone drone)
    {
        super(drone);
        switch (alfrescoVersion)
        {
            case Enterprise41:
                goToAdvancedSearch = ".navigation-item.forwardLink>a";
                break;
            default:
                goToAdvancedSearch = "span#HEADER_ADVANCED_SEARCH_text";
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchResultsPage render(RenderTime timer)
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
                if (drone.find(By.cssSelector(SEARCH_INFO_DIV)).isDisplayed())
                {
                    if (completedSearch())
                    {
                        break;
                    }
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

    @SuppressWarnings("unchecked")
    @Override
    public SearchResultsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchResultsPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Resolves if searching message is displayed.
     * 
     * @return true if visible
     */
    protected boolean completedSearch()
    {
        try
        {
            String info = drone.find(By.cssSelector(SEARCH_INFO_DIV)).getText();
            return !info.contains("Searching");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Verify search yielded results.
     * 
     * @return true if results returned
     */
    public boolean hasResults()
    {
        return count() > 0;
    }

    /**
     * Verify pagination is displayed.
     * 
     * @return true if results returned
     */
    public boolean paginationDisplayed()
    {
        boolean exists = false;
        try
        {
            WebElement element = drone.findByKey(SEARCH_RESULT_PAGINATOR_ID);
            exists = element.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            exists = false;
        }
        return exists;
    }

    /**
     * Gets number of results displayed on the page.
     * 
     * @return Integer number of results
     */
    public int count()
    {
        try
        {
            WebElement info = drone.find(By.cssSelector(SEARCH_INFO_DIV));
            String value = info.getText();
            if (value != null)
            {
                Pattern intsOnly = Pattern.compile("\\d*");
                Matcher makeMatch = intsOnly.matcher(value);
                makeMatch.find();
                String count = makeMatch.group();
                if (count != null && !count.isEmpty())
                {
                    return Integer.valueOf(count);
                }
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return 0;
    }

    /**
     * Selects the first result from page.
     */
    public HtmlPage selectFirstResult()
    {
        List<WebElement> searchResults = drone.findAll(By.cssSelector("h3 a"));
        WebElement result = searchResults.get(0);
        result.click();
        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Selects search within a specific site and performs the search,
     * 
     * @param text String to search
     * @return HtmlPage results page object
     */
    public HtmlPage selectOnlyOnSite(final String text)
    {

        WebElement searchType = drone.findByKey(SEARCH_ON_SITE);
        searchType.click();
        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Selects All sites option and searches against all sites in alfresco
     * share.
     * 
     * @param text String search text
     * @return HtmlPage results page object
     */
    public HtmlPage selectOnAllSites(final String text)
    {
        WebElement searchType = drone.findByKey(SEARCH_ON_ALL_SITES);
        searchType.click();
        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Select repository option search and performs search against repository.
     * 
     * @param text String search text
     * @return HtmlPage results page object
     */
    public HtmlPage selectRepository()
    {
        WebElement searchType = drone.find(By.cssSelector("a[id$='repo-link']"));
        searchType.click();
        // Check till the results render
        try
        {
            WebElement element = drone.findByKey(SEARCH_RESULT_COUNT);
            String result = element.getText();
            if (result != null && !result.isEmpty())
            {
                result.contains("repository");
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Enters the search text and submits the from on basic search page form.
     * 
     * @param text String search text
     * @return HtmlPage results page object
     */
    public HtmlPage doSearch(final String text)
    {
        WebElement searchField = drone.find(By.id(SEARCH_FIELD));
        searchField.clear();
        searchField.sendKeys(text);
        WebElement button = drone.find(By.id(SEARCH_BUTTON));
        button.click();

        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Gets the search type description on the search that has been performed.
     * 
     * @return String search type
     */
    public String searchType()
    {
        WebElement searchType = drone.find(By.cssSelector("a.bold"));
        return searchType.getText();
    }

    /**
     * Displays the position the page is in
     * the pagination, if there is only one page
     * then it will return the value 1
     * 
     * @return int current page position
     */
    public int getPaginationPosition()
    {
        WebElement paginator = drone.findByKey(SEARCH_RESULT_PAGINATOR_ID);
        WebElement element = paginator.findElement(By.cssSelector(CURRENT_POSITION_CSS));
        String position = element.getText();

        return Integer.valueOf(position).intValue();
    }

    /**
     * Checks for next page button on the
     * pagination bar.
     * 
     * @return if visible and clickable
     */
    public boolean hasNextPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Checks for previous page button on the
     * pagination bar.
     * 
     * @return if visible and clickable
     */
    public boolean hasPrevioudPage()
    {
        return Pagination.hasPaginationButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }

    /**
     * Action of selecting next page on pagination bar.
     * 
     * @return results of the next page
     */
    public HtmlPage selectNextPage()
    {
        return Pagination.selectPaginationButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Action of selecting previous page on pagination bar.
     * 
     * @return results of the next page
     */
    public HtmlPage selectPreviousPage()
    {
        return Pagination.selectPaginationButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }

    /**
     * The total number of pages in pagination bar.
     * 
     * @return int total pages of pagiantion
     */
    public int paginationCount()
    {
        List<WebElement> results = null;
        try
        {
            WebElement element = drone.findByKey(SEARCH_RESULT_PAGINATOR_ID);
            HtmlElement paginator = new HtmlElement(element, drone);
            WebElement span = paginator.findAndWait(By.cssSelector("span.yui-pg-pages"));
            results = span.findElements(By.tagName("a"));
        }
        catch (NoSuchElementException nse)
        {
        }
        return results != null ? results.size() + 1 : 0;
    }

    /**
     * Selects and opens the paginated page based on the
     * required position.
     * 
     * @param int pagination page position
     * @return the select paginated page
     */
    public HtmlPage paginationSelect(int position)
    {
        try
        {
            WebElement element = drone.findByKey(SEARCH_RESULT_PAGINATOR_ID);
            HtmlElement paginator = new HtmlElement(element, drone);
            WebElement button = paginator.findAndWait(By.linkText(String.valueOf(position)));
            button.click();
        }
        catch (NoSuchElementException nse)
        {
        }
        catch (TimeoutException te)
        {
        }
        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Gets the search results as a collection of SearResultItems.
     * 
     * @return Collections of search result
     */
    public List<SearchResult> getResults()
    {
        List<SearchResult> results = new ArrayList<SearchResult>();
        if (hasResults())
        {
            try
            {
                List<WebElement> elements = drone.findAll(By.cssSelector("tbody.yui-dt-data tr"));
                for (WebElement element : elements)
                {
                    results.add(new SearchResultItem(element, drone));
                }
            }
            catch (NoSuchElementException nse)
            {
            }
        }
        return results;
    }

    /**
     * Select a particular search result item link
     * based on the match of the title.
     * 
     * @param title String Search result item title
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectItem(String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        drone.find(By.xpath(String.format("//h3/a[text()='%s']", title))).click();
        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Select a particular search result item link
     * based on the count, the accepted range is 1 onwards.
     * 
     * @param number int Search result item index
     * @return {@link HtmlPage} page response
     */
    public HtmlPage selectItem(int number)
    {
        if (number < 0)
        {
            throw new IllegalArgumentException("Value can not be negative");
        }
        number += 1;
        String selector = AlfrescoVersion.Enterprise43.equals(alfrescoVersion) || alfrescoVersion.isCloud() ?
                "tr.alfresco-search-AlfSearchResult:nth-of-type(%d) td.thumbnailCell a" :
                    "tbody.yui-dt-data tr:nth-of-type(%d) > td div span a ";
        try
        {
            drone.find(By.cssSelector(String.format(selector, number))).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %d item not found", number), e);
        }

        return FactorySharePage.getUnknownPage(drone);
    }

    /**
     * Performs the search by entering the term into search field
     * and submitting the search form on the result page.
     * 
     * @param term String term to search
     */
    protected void searchFor(final String term)
    {
        if (term == null || term.isEmpty())
        {
            throw new UnsupportedOperationException("Search term is required to perform a search");
        }
        WebElement searchInput = drone.find(By.cssSelector("input[id$='default-search-text']"));
        searchInput.clear();
        searchInput.sendKeys(term + "\n");
    }

    /**
     * Click on go to Advanced Search link.
     * 
     * @returns {@link SharePage}
     */
    public HtmlPage goBackToAdvanceSearch()
    {
        drone.find(By.cssSelector(goToAdvancedSearch)).click();
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * This function will give a list of share link which stores the reference of
     * different sort type present in the search results page.
     * 
     * @throws NoSuchElementException
     * @returns - list of Sort type elements.
     */
    private List<ShareLink> sortFilterList() throws NoSuchElementException
    {
        try
        {
            WebElement sortDropDown = drone.find(By.cssSelector(SORT_BY_RELEVANCE));
            sortDropDown.click();
            List<WebElement> sortLinkWebElement = drone.findAndWaitForElements(By.cssSelector(SORT_LIST));
            List<ShareLink> sortLinks = new ArrayList<ShareLink>();
            for (WebElement sortListElement : sortLinkWebElement)
            {
                sortLinks.add(new ShareLink(sortListElement, drone));
            }
            return sortLinks;
        }
        catch (TimeoutException nse)
        {
            throw new NoSuchElementException("Sort link is not found", nse);
        }
    }

    /**
     * Function to get the description of All the sort items
     * 
     * @return List<String>
     */
    public List<String> sortListItemsDescription()
    {
        List<String> sortName = new ArrayList<String>();
        List<ShareLink> sortLinks = sortFilterList();
        for (ShareLink sortElement : sortLinks)
        {
            sortName.add(sortElement.getDescription());
        }
        return sortName;
    }

    /**
     * Sort the content and returning searchResults based on sort type passed.
     * 
     * @param SortType {@link SortType}
     * @return SharePage page response
     */
    public HtmlPage sortPage(SortType sortType)
    {
        if (sortType == null)
        {
            throw new IllegalArgumentException("SortType can't be null.");
        }
        try
        {
            List<ShareLink> sortLinks = sortFilterList();
            for (ShareLink sortElement : sortLinks)
            {
                if (sortElement.getDescription().equalsIgnoreCase(sortType.getSortName()))
                {
                    sortElement.click();
                    return FactorySharePage.resolvePage(drone);
                }
            }
        }
        catch (NoSuchElementException e)
        {
        }
        throw new PageException("cannot find the sort type passed");
    }

    /**
     * Method to verify all controls are displayed on Search page (Note: does not include pagination verification)
     *
     * @return true if page is correct
     */
    public boolean isPageCorrect()
    {
        boolean isCorrect;
        isCorrect = drone.isElementDisplayed(By.id(SEARCH_FIELD)) && drone.isElementDisplayed(By.id(SEARCH_BUTTON))
            && drone.findByKey(SEARCH_RESULTS_DIV_ID).isDisplayed() && isSortCorrect() && drone.isElementDisplayed(By.cssSelector(goToAdvancedSearch));
        return isCorrect;
    }

    private boolean isSortCorrect()
    {
        try
        {
            List <WebElement> sortList = drone.findAll(By.cssSelector(SORT_LIST));
            SortType sortTypesValues [] = SortType.values();
            if (sortList.size() == sortTypesValues.length)
            {
                for(int i = 0; i < sortList.size(); i++)
                {
                    if(!sortList.get(i).getAttribute("text").equals(sortTypesValues[i].getSortName()))
                        return false;
                }
                return true;
            }
            else
            {
                throw new ShareException("Sort by list is of incorrect size");
            }
        }
        catch (NoSuchElementException nse)
        {
           throw new ShareException("Unable to find " + SORT_LIST);
        }
    }

    /**
     * Click next page button on the
     * pagination bar.
     *
     * @return if visible and clickable
     */
    public HtmlPage clickNextPage()
    {
        return Pagination.selectPaginationButton(drone, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Click prev page button on the
     * pagination bar.
     *
     * @return if visible and clickable
     */
    public HtmlPage clickPrevPage()
    {
        return Pagination.selectPaginationButton(drone, PAGINATION_BUTTON_PREVIOUS);
    }
}
