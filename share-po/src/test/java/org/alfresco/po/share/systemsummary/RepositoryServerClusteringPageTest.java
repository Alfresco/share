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
