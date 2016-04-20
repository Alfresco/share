/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.Pagination;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.UnknownSharePage;
import org.alfresco.po.share.exception.ShareException;
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
    private static final String SEARCH_RESULT_PAGINATOR_ID = "_default-paginator-top";
    private static final String SEARCH_RESULTS_DIV_ID = "search.results.div.id";
    private static final String CURRENT_POSITION_CSS = "span.yui-pg-current-page.yui-pg-page";
    private static final String PAGINATION_BUTTON_NEXT = "a.yui-pg-next";
    private static final String PAGINATION_BUTTON_PREVIOUS = "a.yui-pg-previous";
    private final String goToAdvancedSearch = "span#HEADER_ADVANCED_SEARCH_text";
    private static final String SORT_BY_RELEVANCE = "button[id$='default-sort-menubutton-button']";
    private static final String SORT_LIST = "span[id$='-sort-menubutton'] + div>div.bd>ul>li>a";
    protected static final By BACK_TO_SITE_LINK = By.cssSelector("#HEADER_SEARCH_BACK_TO_SITE_DASHBOARD");

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
                if (driver.findElement(By.cssSelector(SEARCH_INFO_DIV)).isDisplayed())
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

    /**
     * Resolves if searching message is displayed.
     * 
     * @return true if visible
     */
    protected boolean completedSearch()
    {
        try
        {
            String info = driver.findElement(By.cssSelector(SEARCH_INFO_DIV)).getText();
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
            WebElement element = driver.findElement(By.cssSelector("[id$='_default-paginator-top']"));
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
            WebElement info = driver.findElement(By.cssSelector(SEARCH_INFO_DIV));
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
        List<WebElement> searchResults = driver.findElements(By.cssSelector("h3 a"));
        WebElement result = searchResults.get(0);
        result.click();
        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
    }

    /**
     * Selects search within a specific site and performs the search,
     * 
     * @param text String to search
     * @return HtmlPage results page object
     */
    public HtmlPage selectOnlyOnSite(final String text)
    {

        WebElement searchType = findByKey(SEARCH_ON_SITE);
        searchType.click();
        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
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
        WebElement searchType = findByKey(SEARCH_ON_ALL_SITES);
        searchType.click();
        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
    }

    /**
     * Select repository option search and performs search against repository.
     * @return HtmlPage results page object
     */
    public HtmlPage selectRepository()
    {
        WebElement searchType = driver.findElement(By.cssSelector("a[id$='repo-link']"));
        searchType.click();
        // Check till the results render
        try
        {
            WebElement element = findByKey(SEARCH_RESULT_COUNT);
            String result = element.getText();
            if (result != null && !result.isEmpty())
            {
                result.contains("repository");
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
    }

    /**
     * Enters the search text and submits the from on basic search page form.
     * 
     * @param text String search text
     * @return HtmlPage results page object
     */
    public HtmlPage doSearch(final String text)
    {
        WebElement searchField = driver.findElement(By.id(SEARCH_FIELD));
        searchField.clear();
        searchField.sendKeys(text);
        WebElement button = driver.findElement(By.id(SEARCH_BUTTON));
        button.click();

        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
    }

    /**
     * Gets the search type description on the search that has been performed.
     * 
     * @return String search type
     */
    public String searchType()
    {
        WebElement searchType = driver.findElement(By.cssSelector("a.bold"));
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
        WebElement paginator = findByKey(SEARCH_RESULT_PAGINATOR_ID);
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
        return Pagination.hasPaginationButton(driver, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Checks for previous page button on the
     * pagination bar.
     * 
     * @return if visible and clickable
     */
    public boolean hasPrevioudPage()
    {
        return Pagination.hasPaginationButton(driver, PAGINATION_BUTTON_PREVIOUS);
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
                List<WebElement> elements = driver.findElements(By.cssSelector("tbody.yui-dt-data tr"));
                for (WebElement element : elements)
                {
                    results.add(new SearchResultItem(element, driver));
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
        driver.findElement(By.xpath(String.format("//h3/a[text()='%s']", title))).click();
        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
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
        String selector = "tr.alfresco-search-AlfSearchResult:nth-of-type(%d) td.thumbnailCell a";
        try
        {
            driver.findElement(By.cssSelector(String.format(selector, number))).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("Search result %d item not found", number), e);
        }

        return factoryPage.instantiatePage(driver, UnknownSharePage.class);
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
        WebElement searchInput = driver.findElement(By.cssSelector("input[id$='default-search-text']"));
        searchInput.clear();
        searchInput.sendKeys(term + "\n");
    }

    /**
     * Click on go to Advanced Search link.
     * 
     * @return {@link SharePage}
     */
    public HtmlPage goBackToAdvanceSearch()
    {
        driver.findElement(By.cssSelector(goToAdvancedSearch)).click();
        return getCurrentPage();
    }

    /**
     * This function will give a list of share link which stores the reference of
     * different sort type present in the search results page.
     * 
     * @throws NoSuchElementException
     * @return - list of Sort type elements.
     */
    private List<ShareLink> sortFilterList() throws NoSuchElementException
    {
        try
        {
            WebElement sortDropDown = driver.findElement(By.cssSelector(SORT_BY_RELEVANCE));
            sortDropDown.click();
            List<WebElement> sortLinkWebElement = findAndWaitForElements(By.cssSelector(SORT_LIST));
            List<ShareLink> sortLinks = new ArrayList<ShareLink>();
            for (WebElement sortListElement : sortLinkWebElement)
            {
                sortLinks.add(new ShareLink(sortListElement, driver, factoryPage));
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
     * @param sortType SortType {@link SortType}
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
                    return getCurrentPage();
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
        isCorrect = isElementDisplayed(By.id(SEARCH_FIELD)) && isElementDisplayed(By.id(SEARCH_BUTTON))
            && findByKey(SEARCH_RESULTS_DIV_ID).isDisplayed() && isSortCorrect() && isElementDisplayed(By.cssSelector(goToAdvancedSearch));
        return isCorrect;
    }

    private boolean isSortCorrect()
    {
        try
        {
            List <WebElement> sortList = driver.findElements(By.cssSelector(SORT_LIST));
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

}
