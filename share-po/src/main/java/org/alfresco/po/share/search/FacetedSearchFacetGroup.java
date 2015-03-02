package org.alfresco.po.share.search;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class FacetedSearchFacetGroup.
 */
public class FacetedSearchFacetGroup
{

    /**  Constants. */
    private static final By GROUP_LABEL = By.cssSelector("div.label");
    private static final By FACET_FILTER = By.cssSelector("li.alfresco-search-FacetFilter:not(.hidden):not(.showMore):not(.showLess)");
    private static final By FACET_FILTER_LABEL = By.cssSelector("span.filterLabel");
    private static final By FACET_FILTER_HITS = By.cssSelector("span.hits");

    private String groupLabel;
    private List<FacetedSearchFacet> facets;

    /**
     * Instantiates a new faceted search facet group.
     *
     * @param facetGroup the facet group
     */
    public FacetedSearchFacetGroup(WebDrone drone, WebElement facetGroup)
    {
        this.groupLabel = facetGroup.findElement(GROUP_LABEL).getText();
        List<WebElement> facetFilters = facetGroup.findElements(FACET_FILTER);
        this.facets = new ArrayList<FacetedSearchFacet>();
        for (WebElement facetFilter : facetFilters)
        {
            WebElement link = facetFilter.findElement(FACET_FILTER_LABEL);
            String label = link.getText();
            int hits = Integer.parseInt(StringUtils.trim(facetFilter.findElement(FACET_FILTER_HITS).getText()));
            if(StringUtils.isNotEmpty(label))
            {
                this.facets.add(new FacetedSearchFacet(drone, link, label, hits));
            }
        }
    }

    /**
     * Gets the group label.
     *
     * @return the group label
     */
    public String getGroupLabel()
    {
        return groupLabel;
    }

    /**
     * Gets the facets.
     * 
     * @return the facets
     */
    public List<FacetedSearchFacet> getFacets()
    {
        return facets;
    }

    /**
     * The Class FacetedSearchFacet.
     */
    public class FacetedSearchFacet extends SharePage
    {

        protected FacetedSearchFacet(WebDrone drone)
        {
            super(drone);
            // TODO Auto-generated constructor stub
        }

        private WebDrone drone;
        private WebElement link;
        private String label;
        private int hits;

        /**
         * Instantiates a new faceted search facet.
         *
         * @param drone the drone
         * @param link the link
         * @param label the label
         * @param hits the hits
         */
        public FacetedSearchFacet(WebDrone drone, WebElement link, String label, int hits)
        {
            super(drone);
            this.drone = drone;
            this.link = link;
            this.label = label;
            this.hits = hits;
        }

        /**
         * Gets the link.
         *
         * @return the link
         */
        public WebElement getLink()
        {
            return link;
        }

        /**
         * Gets the label.
         *
         * @return the label
         */
        public String getLabel()
        {
            return label;
        }

        /**
         * Gets the hits.
         *
         * @return the hits
         */
        public int getHits()
        {
            return hits;
        }

        /**
         * Click the link.
         *
         * @return the html page
         */
        public HtmlPage clickLink()
        {
            this.link.click();
            return FactorySharePage.resolvePage(this.drone);
        }

        @Override
        public <T extends HtmlPage> T render(RenderTime timer)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends HtmlPage> T render(long time)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T extends HtmlPage> T render()
        {
            // TODO Auto-generated method stub
            return null;
        }
    }
}