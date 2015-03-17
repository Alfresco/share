package org.alfresco.po.share.search;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchForm.
 */
public class FacetedSearchForm
{
    /** Constants */
    private static final By FACETED_SEARCH_FORM = By.cssSelector("div#FCTSRCH_SEARCH_FORM");
    public final By SEARCH_FIELD = By.cssSelector("INPUT[name=searchTerm]");
    public final By SEARCH_BUTTON = By.cssSelector("span.dijitButtonNode");

    private WebDrone drone;
    private WebElement input;
    private WebElement button;

    /**
     * Instantiates a new faceted search form.
     */
    public FacetedSearchForm(WebDrone drone)
    {
        this.drone = drone;
        WebElement facetedSearchForm = drone.find(FACETED_SEARCH_FORM);
        this.input = facetedSearchForm.findElement(SEARCH_FIELD);
        this.button = facetedSearchForm.findElement(SEARCH_BUTTON);
    }

    /**
     * Gets the search term.
     *
     * @return the search term
     */
    public String getSearchTerm()
    {
        return this.input.getAttribute("value");
    }

    /**
     * Sets the search term.
     *
     * @param searchTerm the new search term
     */
    public void setSearchTerm(String searchTerm)
    {
        this.input.sendKeys(searchTerm);
    }

    /**
     * Clear search term.
     */
    public void clearSearchTerm()
    {
        this.input.clear();
    }

    /**
     * Performs a search for the provided searchTerm.
     *
     * @param searchTerm the term upon which to search
     * @return the html page
     */
    public HtmlPage search(String searchTerm)
    {
        clearSearchTerm();
        setSearchTerm(searchTerm);
        this.button.click();
        
        return FactorySharePage.resolvePage(this.drone);
    }

}