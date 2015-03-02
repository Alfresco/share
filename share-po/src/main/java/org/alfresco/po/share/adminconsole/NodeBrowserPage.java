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
package org.alfresco.po.share.adminconsole;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Class associated with page in admin console 'Node Browser'
 *
 * @author Aliaksei Boole
 */
public class NodeBrowserPage extends AdminConsolePage
{
    private Log logger = LogFactory.getLog(this.getClass());
    private final static By TITLE_LABEL = By.cssSelector(".title>label");
    private final static By SEARCH_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-button-button");
    private final static By SEARCH_TEXT_FIELD = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-text");
    private final static By SEARCH_BAR = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-search-bar");

    private final static String SEARCH_RESULT_BASE_CSS = "tr[class~='yui-dt-rec']";
    private final static By SEARCH_RESULT = By.cssSelector(SEARCH_RESULT_BASE_CSS);
    private final static By RESULT_REFERENCE = By.cssSelector(SEARCH_RESULT_BASE_CSS + "> td[class~='yui-dt-col-nodeRef'] > div > a[href='#']");

    private final static By SEARCH_QUERY_TYPE_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-lang-menu-button-button");
    private final static By STORE_TYPE_BUTTON = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-store-menu-button-button");
    private final static By VISIBLE_DROPDOWN_SELECT = By.cssSelector("div[class~='visible']> div > ul >li[class~='yuimenuitem'] > a[class~='yuimenuitemlabel']");

    private final static By NODE_BROWSER_RESULT_NAME = By.cssSelector("td[class*='-col-name'] a");
    private final static By NODE_BROWSER_RESULT_PARENT = By.cssSelector("td[class*='-col-qnamePath'] div");
    private final static By NODE_BROWSER_RESULT_NODEREF = By.cssSelector("td[class*='-col-nodeRef'] a");

    private final static String REGEXP_PATTERN = ".*%s.*";

    private final static By RESULT_NAMES = By.cssSelector("td[class*='sortable'][headers*='-name'] > div > a[href='#']");

    private final static By CONTENT_URL = By.xpath("//a[contains(text(),'contentUrl')]");


    public enum Store
    {
        ALFRESCO_USER("user://alfrescoUserStore"),
        SYSTEM("system://system"),
        LIGHT_WEIGHT_VERSION("workspace://lightWeightVersionStore"),
        VERSION_2("workspace://version2Store"),
        ARCHIVE_SPACE_STORE("archive://SpacesStore"),
        WORKSPACE_SPACE_STORE("workspace://SpacesStore");

        public final String text;

        Store(String text)
        {
            this.text = text;
        }
    }

    public enum QueryType
    {
        STORE_ROOT("storeroot"),
        NODE_REF("noderef"),
        XPATH("xpath"),
        LUCENE("lucene"),
        FTS_ALFRECO("fts-alfresco"),
        CMIS_STRICT("cmis-strict"),
        CMIS_ALFRESCO("cmis-alfresco"),
        DB_AFTS("db-afts"),
        DB_CMIS("db-cmis");

        public final String text;

        QueryType(String text)
        {
            this.text = text;
        }
    }

    /**
     * Basic constructor.
     *
     * @param drone
     */
    public NodeBrowserPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
                getVisibleRenderElement(SEARCH_BUTTON),
                getVisibleRenderElement(SEARCH_TEXT_FIELD),
                getVisibleRenderElement(SEARCH_QUERY_TYPE_BUTTON),
                getVisibleRenderElement(STORE_TYPE_BUTTON),
                getVisibleRenderElement(TITLE_LABEL));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage render(long l)
    {
        checkArgument(l > 0);
        return render(new RenderTime(l));
    }

    @SuppressWarnings("unchecked")
    @Override
    public NodeBrowserPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Fill query field.
     *
     * @param query
     */
    public void fillQueryField(String query)
    {
        fillField(SEARCH_TEXT_FIELD, query);
    }

    /**
     * Mimic click by Search Button.
     */
    public HtmlPage clickSearchButton()
    {
        click(SEARCH_BUTTON);
        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Select store from drop-down menu on page by Name.
     *
     * @param store
     */
    public void selectStore(Store store)
    {
        select(STORE_TYPE_BUTTON, store.text);
    }

    /**
     * Select QueryType from drop-down menu on page by Name
     *
     * @param queryType
     */
    public void selectQueryType(QueryType queryType)
    {
        select(SEARCH_QUERY_TYPE_BUTTON, queryType.text);
    }

    /**
     * Check that after search has some results.
     *
     * @return true - if has results
     */
    public boolean isSearchResults()
    {
        List<WebElement> results;
        try
        {
            results = drone.findAndWaitForElements(SEARCH_RESULT, 5000);
        }
        catch (StaleElementReferenceException e)
        {
            return isSearchResults();
        }
        catch (TimeoutException e)
        {
            results = Collections.emptyList();
        }
        return results.size() > 0;
    }

    /**
     * Check how match results has.
     *
     * @return count returned elements
     */
    public int getSearchResultsCount()
    {
        return isSearchResults() ? drone.findAndWaitForElements(SEARCH_RESULT, 5000).size() : 0;
    }

    /**
     * Check test on SearchBar.
     *
     * @param regExp - what we expect on SearchBar
     * @return true if actual text on Bar matches with expect
     */
    public boolean isOnSearchBar(String regExp)
    {
        checkNotNull(regExp);
        WebElement searchBar = drone.findAndWait(SEARCH_BAR);
        String text = searchBar.getText();
        return text.matches(formatRegExp(regExp));
    }

    /**
     * Check results by node ref.
     * 
     * @param regExp - expect result node ref(or regExp)
     * @return true if results has matches with expect
     */
    public boolean isInResultsByNodeRef(String regExp)
    {
        return isInResults(regExp, RESULT_REFERENCE);
    }

    /**
     * Check results by name.
     *
     * @param regExp - expect resultName(or regExp)
     * @return true if results has matches with expect
     */
    public boolean isInResultsByName(String regExp)
    {
        return isInResults(regExp, RESULT_NAMES);
    }

    /**
     * Check results by name or node ref.
     * 
     * @param regExp - expect resultName(or regExp)
     * @return true if results has matches with expect
     */
    private boolean isInResults(String regExp, By locator)
    {
        checkNotNull(regExp);
        String fRegExp = formatRegExp(regExp);
        List<WebElement> resultsName = drone.findAndWaitForElements(locator, 5000);
        for (WebElement resultName : resultsName)
        {
            String name = resultName.getText();
            if (name.matches(fRegExp))
            {
                return true;
            }
        }
        return false;
    }

    private String formatRegExp(String regExp)
    {
        return String.format(REGEXP_PATTERN, regExp);
    }

    private void select(By buttonLocator, String text)
    {
        checkNotNull(text);
        click(buttonLocator);
        List<WebElement> options = drone.findAndWaitForElements(VISIBLE_DROPDOWN_SELECT);
        for (WebElement option : options)
        {
            if (text.equals(option.getText()))
            {
                option.click();
                return;
            }
        }
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }

    /**
     * Method to get Node Browser Search Results
     *
     * @return {@link List<NodeBrowserSearchResult>}
     */
    public List<NodeBrowserSearchResult> getSearchResults()
    {
        List<WebElement> results;
        List<NodeBrowserSearchResult> searchResultsList = new ArrayList<>();
        try
        {
            results = drone.findAndWaitForElements(SEARCH_RESULT, WAIT_TIME_3000);

            for (WebElement result : results)
            {
                NodeBrowserSearchResult nodeBrowserSearchResult = new NodeBrowserSearchResult();
                nodeBrowserSearchResult.setName(new ShareLink(result.findElement(NODE_BROWSER_RESULT_NAME), drone));
                nodeBrowserSearchResult.setParent(result.findElement(NODE_BROWSER_RESULT_PARENT).getText());
                nodeBrowserSearchResult.setReference(new ShareLink(result.findElement(NODE_BROWSER_RESULT_NODEREF), drone));

                searchResultsList.add(nodeBrowserSearchResult);
            }
            return searchResultsList;
        }
        catch (StaleElementReferenceException e)
        {
            return getSearchResults();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No results found");
            }
        }
        catch(NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace(nse.getMessage());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Method to get NodeBrowserSearchResults object for a given Name
     * @param name
     * @return
     */
    public NodeBrowserSearchResult getSearchResults(String name)
    {
        if(StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        try
        {
            NodeBrowserSearchResult searchResult = new NodeBrowserSearchResult();
            WebElement resultRow = drone.findAndWait(By.xpath("//a[text() = '" + name + "']/../../.."));

            searchResult.setName(new ShareLink(resultRow.findElement(NODE_BROWSER_RESULT_NAME), drone));
            searchResult.setParent(resultRow.findElement(NODE_BROWSER_RESULT_PARENT).getText());
            searchResult.setReference(new ShareLink(resultRow.findElement(NODE_BROWSER_RESULT_NODEREF), drone));

            return  searchResult;
        }
        catch (TimeoutException e)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace("Unable to find " + name, e);
            }
        }
        catch(NoSuchElementException nse)
        {
            if(logger.isTraceEnabled())
            {
                logger.trace(nse.getMessage(), nse);
            }
        }
        throw new PageOperationException("Unable to find " + name);
    }

    /**
     * Method to open details form for the founded item
     * @param itemName
     */
    public void getItemDetails (String itemName)
    {
        ShareLink link = getSearchResults("cm:" + itemName).getName();
        link.click();
    }

    /**
     * Method to find a link to the location of the item
     * @return String value of the link
     */
    public String getContentUrl ()
    {
        WebElement contentUrl = drone.findAndWait(CONTENT_URL);
        return contentUrl.getText();
    }
}
