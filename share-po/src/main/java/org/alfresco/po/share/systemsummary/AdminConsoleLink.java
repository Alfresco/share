package org.alfresco.po.share.systemsummary;

import org.openqa.selenium.By;

/**
 * @author sergey.kardash on 4/12/14.
 */
public enum AdminConsoleLink
{

    SystemSummary(By.cssSelector("a[href$='admin-systemsummary']")),
    ActivitiesFeed(By.cssSelector("a[href$='admin-activitiesfeed']")),
    RepositoryServerClustering(By.cssSelector("a[href$='admin-clustering']")),
    DirectoryManagement(By.cssSelector("a[href$='admin-directorymanagement']")),
    TenantAdminConsole(By.cssSelector("a[href$='admin-tenantconsole']")),
    FileServers(By.cssSelector("a[href$='admin-fileservers']")),
    RepoConsole(By.cssSelector("a[href$='admin-repoconsole']")),
    Transformations(By.cssSelector("a[href$='admin-transformations']"));

    public final By contentLocator;

    AdminConsoleLink(By adminConsoleLink)
    {
        this.contentLocator = adminConsoleLink;

    }

}
