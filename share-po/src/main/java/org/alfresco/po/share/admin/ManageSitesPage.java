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
package org.alfresco.po.share.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.DocListPaginator;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * The Class ManageSitesPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class ManageSitesPage extends SharePage
{

    /** Constants */
    private static String SITE_ROWS = "tr.alfresco-lists-views-layouts-Row";
    private static String SITE_ROW_NAME = "td.alfresco-lists-views-layouts-Cell.siteName";
    private static String SITE_ROW_DESCRIPTION = "td.alfresco-lists-views-layouts-Cell.siteDescription";
    private static String SITE_ROW_VISIBILITY = "td.alfresco-lists-views-layouts-Cell.visibility";
    private static String SITE_ROW_ACTIONS = "td.alfresco-lists-views-layouts-Cell.actions";

    private List<ManagedSiteRow> managedSiteRows;
    @FindBy(css="div[id=DOCLIB_PAGINATION_MENU]") private DocListPaginator docListPaginator;

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render()
     */
    @Override
    public ManageSitesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.Render#render(org.alfresco.po.RenderTime)
     */
    @Override
    public ManageSitesPage render(RenderTime maxPageLoadingTime)
    {
        basicRender(maxPageLoadingTime);
        loadElements();
        return this;
    }

    /**
     * Method to get the List of ManageSiteRow.
     * 
     * @return List
     */
    public List<ManagedSiteRow> getManagedSiteRows()
    {
        return this.managedSiteRows;
    }

    /**
     * Gets the document list paginator.
     * 
     * @return the paginator
     */
    public DocListPaginator getPaginator()
    {
        return this.docListPaginator;
    }

    /**
     * Find a managed site row by name from paginated results.
     * 
     * @param siteName the required site name
     * @return the managed site row
     */
    public ManagedSiteRow findManagedSiteRowByNameFromPaginatedResults(String siteName)
    {
        // Initialise a simple row instance for comparison
        ManagedSiteRow testManagedSiteRow = new ManagedSiteRow(siteName);

        // Iterate through paginations until the last pagination or row found
        boolean first = true;
        do
        {
            if (first)
            {
                // Navigate to the first page of results
                this.docListPaginator.gotoFirstResultsPage();
                first = false;
            }
            else
            {
                // Navigate to next pagination
                this.docListPaginator.clickNextButton();

            }

            // Refresh the page elements
            this.loadElements();

            // Iterate through rows and return if we find a matching site
            for (ManagedSiteRow row : this.getManagedSiteRows())
            {
                if (row.equals(testManagedSiteRow))
                {
                    return row;
                }
            }
        }
        while (this.docListPaginator.hasNextPage());

        return null;
    }

    /**
     * Initialises the elements that make up a ManageSitesPage.
     */
    public void loadElements()
    {
// TODO Cloud
//        if (alfrescoVersion.isCloud())
//        { 
//            SITE_ROWS = SITE_ROWS.replace("lists", "documentlibrary");
//            SITE_ROW_NAME = SITE_ROW_NAME.replace("lists", "documentlibrary");
//            SITE_ROW_DESCRIPTION = SITE_ROW_DESCRIPTION.replace("lists", "documentlibrary");
//            SITE_ROW_VISIBILITY = SITE_ROW_VISIBILITY.replace("lists", "documentlibrary");
//            SITE_ROW_ACTIONS = SITE_ROW_ACTIONS.replace("lists", "documentlibrary");
//        }

        // Initialise the available site rows
        List<WebElement> siteRows = findAndWaitForElements(By.cssSelector(SITE_ROWS));
        this.managedSiteRows = new ArrayList<ManagedSiteRow>();
        for (WebElement siteRow : siteRows)
        {
            ManagedSiteRow manageSiteRow = new ManagedSiteRow();
            manageSiteRow.setSiteName(siteRow.findElement(By.cssSelector(SITE_ROW_NAME)).getText());
            manageSiteRow.setSiteDescription(siteRow.findElement(By.cssSelector(SITE_ROW_DESCRIPTION)).getText());
            manageSiteRow.setVisibility(new VisibilityDropDown(driver, siteRow.findElement(By.cssSelector(SITE_ROW_VISIBILITY))));
            manageSiteRow.setActions(new ActionsSet(driver, siteRow.findElement(By.cssSelector(SITE_ROW_ACTIONS)), factoryPage));
            this.managedSiteRows.add(manageSiteRow);
        }

    }
}
