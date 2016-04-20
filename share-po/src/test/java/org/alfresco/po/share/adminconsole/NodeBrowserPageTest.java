package org.alfresco.po.share.adminconsole;

import static org.alfresco.po.share.adminconsole.NodeBrowserPage.QueryType.LUCENE;
import static org.alfresco.po.share.adminconsole.NodeBrowserPage.QueryType.STORE_ROOT;
import static org.alfresco.po.share.adminconsole.NodeBrowserPage.Store.WORKSPACE_SPACE_STORE;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.test.FailedTestListener;
import org.alfresco.po.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class NodeBrowserPageTest extends AbstractTest
{

    NodeBrowserPage nodeBrowserPage;

    @Test(groups = "Enterprise-only", timeOut = 400000)
    public void checkThatFactoryReturnNodeBrowserPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getNodeBrowserPage().render();
        resolvePage(driver).render();
    }

    @Test(dependsOnMethods = "checkThatFactoryReturnNodeBrowserPage", groups = "Enterprise-only", timeOut = 400000)
    public void executeCustomNodeSearch() throws Exception
    {
        nodeBrowserPage = resolvePage(driver).render();
        nodeBrowserPage.selectStore(WORKSPACE_SPACE_STORE);
        nodeBrowserPage.selectQueryType(STORE_ROOT);
        nodeBrowserPage.clickSearchButton();
        assertTrue(nodeBrowserPage.isSearchResults());
    }

    @Test(dependsOnMethods = "executeCustomNodeSearch", groups = "Enterprise-only")
    public void getSearchResults() throws Exception
    {
        nodeBrowserPage = nodeBrowserPage.getNav().getNodeBrowserPage().render();
        nodeBrowserPage.fillQueryField("TYPE:\"cm:category\"");
        nodeBrowserPage.clickSearchButton();
        List<NodeBrowserSearchResult> nodeBrowserSearchResults = nodeBrowserPage.getSearchResults();
        Assert.assertTrue(nodeBrowserSearchResults.size() > 0);

        NodeBrowserSearchResult language = nodeBrowserPage.getSearchResults("cm:Languages");
        Assert.assertNotNull(language);
    }

    @Test(dependsOnMethods = "getSearchResults", groups = "Enterprise-only")
    public void getSearchResultsNoResults() throws Exception
    {
        nodeBrowserPage = nodeBrowserPage.getNav().getNodeBrowserPage().render();
        nodeBrowserPage.selectQueryType(LUCENE);
        nodeBrowserPage.fillQueryField(String.valueOf(System.currentTimeMillis()));
        nodeBrowserPage.clickSearchButton();
        List<NodeBrowserSearchResult> nodeBrowserSearchResults = nodeBrowserPage.getSearchResults();
        Assert.assertTrue(nodeBrowserSearchResults.size() == 0);
    }

    @Test(dependsOnMethods = "getSearchResultsNoResults", groups = "Enterprise-only", expectedExceptions = PageOperationException.class)
    public void getSearchResultsWithException() throws Exception
    {
        nodeBrowserPage.getSearchResults("sdfsadfsf");
    }

    @Test(dependsOnMethods = "getSearchResultsWithException", groups = "Enterprise-only", expectedExceptions = IllegalArgumentException.class)
    public void getSearchResultsWithIllegalArgException() throws Exception
    {
        nodeBrowserPage.getSearchResults("");
    }
}
