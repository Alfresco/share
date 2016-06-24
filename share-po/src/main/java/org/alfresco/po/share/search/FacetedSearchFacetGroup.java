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

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
    public FacetedSearchFacetGroup(WebDriver driver, WebElement facetGroup)
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
                this.facets.add(new FacetedSearchFacet(driver, link, label, hits));
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
        private WebDriver driver;
        private WebElement link;
        private String label;
        private int hits;

        /**
         * Instantiates a new faceted search facet.
         *
         * @param driver the driver
         * @param link the link
         * @param label the label
         * @param hits the hits
         */
        public FacetedSearchFacet(WebDriver driver, WebElement link, String label, int hits)
        {
            this.driver = driver;
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
            return factoryPage.getPage(this.driver);
        }

        @Override
        public <T extends HtmlPage> T render(RenderTime timer)
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
