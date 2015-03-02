package org.alfresco.po.share.admin;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DocListPaginator;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * The Class ManageSitesPage.
 * 
 * @author Richard Smith
 */
@SuppressWarnings("unchecked")
public class ManageSitesPage extends SharePage
{

    /** Constants */
    private static final By SITE_ROWS = By.cssSelector("tr.alfresco-lists-views-layouts-Row");
    private static final By SITE_ROW_NAME = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteName");
    private static final By SITE_ROW_DESCRIPTION = By.cssSelector("td.alfresco-lists-views-layouts-Cell.siteDescription");
    private static final By SITE_ROW_VISIBILITY = By.cssSelector("td.alfresco-lists-views-layouts-Cell.visibility");
    private static final By SITE_ROW_ACTIONS = By.cssSelector("td.alfresco-lists-views-layouts-Cell.actions");

    private List<ManagedSiteRow> managedSiteRows;
    private DocListPaginator docListPaginator;

    /**
     * Instantiates a new manage sites page.
     * 
     * @param drone WebDriver browser client
     */
    public ManageSitesPage(WebDrone drone)
    {
        super(drone);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render()
     */
    @Override
    public ManageSitesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(long)
     */
    @Override
    public ManageSitesPage render(long maxPageLoadingTime)
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.webdrone.Render#render(org.alfresco.webdrone.RenderTime)
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
     * @return List<{@link ManagedSiteRow}>
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
        // Initialise the available site rows
        List<WebElement> siteRows = drone.findAndWaitForElements(SITE_ROWS);
        this.managedSiteRows = new ArrayList<ManagedSiteRow>();
        for (WebElement siteRow : siteRows)
        {
            ManagedSiteRow manageSiteRow = new ManagedSiteRow();
            manageSiteRow.setSiteName(siteRow.findElement(SITE_ROW_NAME).getText());
            manageSiteRow.setSiteDescription(siteRow.findElement(SITE_ROW_DESCRIPTION).getText());
            manageSiteRow.setVisibility(new VisibilityDropDown(drone, siteRow.findElement(SITE_ROW_VISIBILITY)));
            manageSiteRow.setActions(new ActionsSet(drone, siteRow.findElement(SITE_ROW_ACTIONS)));
            this.managedSiteRows.add(manageSiteRow);
        }

        // Initialise the doc list paginator
        this.docListPaginator = new DocListPaginator(drone);
    }
}