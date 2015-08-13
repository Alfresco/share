package org.alfresco.po.share.systemsummary;

import java.util.List;

import org.alfresco.po.AbstractTest;

import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class RepositoryServerClusteringPageTest extends AbstractTest
{

    @Test(groups = "Enterprise-only")
    public void isClusterEnabledTest()
    {
        SystemSummaryPage sysSummaryPage = (SystemSummaryPage) shareUtil.navigateToSystemSummary(driver, shareUrl, username, password);
        RepositoryServerClusteringPage clusteringPage = sysSummaryPage.openConsolePage(AdminConsoleLink.RepositoryServerClustering).render();
        if (!clusteringPage.isClusterEnabled())
        {
            throw new SkipException("Cluster isn't enabled");
        }
    }

    @Test(dependsOnMethods = "isClusterEnabledTest", groups = "Enterprise-only")
    public void getClusterMembersNumberTest()
    {
        RepositoryServerClusteringPage clusteringPage = resolvePage(driver).render();
        Assert.assertTrue(clusteringPage.getClusterMembersNumber() >= 1, "Number of cluster members isn't correctly");
    }

    @Test(dependsOnMethods = "getClusterMembersNumberTest", groups = "Enterprise-only")
    public void getClusterMembers()
    {
        RepositoryServerClusteringPage clusteringPage = resolvePage(driver).render();
        List<String> getClusterMembers = clusteringPage.getClusterMembers();
        Assert.assertTrue(!getClusterMembers.isEmpty(), "Server Details(IP) of cluster members isn't correctly");
    }

}
