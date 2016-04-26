package org.alfresco.po.share.site.document;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;


/**
 * @author cbairaajoni
 */
public class DetailedTableViewFileDirectoryInfo extends SimpleDetailTableView
{
    @Override
    public String getName()
    {
        String title = "";
        try
        {
            title = findAndWait(By.cssSelector("td[class$='name']")).getText();
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            getName();
        }
        return title;
    }
}
