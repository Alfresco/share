package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.MySitesDashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
/**
 * Abstract test holds all common methods and functionality to test against
 * Document based tests.
 * 
 * @author Michael Suzuki
 */
public abstract class AbstractDocumentTest extends AbstractTest
{
    /**
     * Helper method to navigate to document library page of a site that we
     * create for the test.
     * @param siteName String site identifier
     * @return HtmlPage document library page.
     * @throws PageException if error
     */
    protected DocumentLibraryPage getDocumentLibraryPage(final String siteName) throws PageException
    {
        SiteDashboardPage site = getSiteDashboard(siteName);
        return site.getSiteNav().selectDocumentLibrary().render();
    }
    
    /**
     * Prepare test by getting the driver to the correct page.
     * 
     * @param file file
     * @return {@link DocumentDetailsPage} page object
     * @throws PageException if error
     */
    protected HtmlPage selectDocument(File file) throws PageException
    {
        DocumentLibraryPage docsPage = resolvePage(driver).render();
        return docsPage.selectFile(file.getName()).render();
    }
    /**
     * Helper method to get site dashboard page.
     * @param siteName String name of the site to enter
     * @return {@link SiteDashboardPage} page
     */
    protected SiteDashboardPage getSiteDashboard(final String siteName)
    {
        DashBoardPage dashBoard = factoryPage.getPage(driver).render();
        MySitesDashlet dashlet = dashBoard.getDashlet("my-sites").render();
        return dashlet.selectSite(siteName).click().render();
    }
}
