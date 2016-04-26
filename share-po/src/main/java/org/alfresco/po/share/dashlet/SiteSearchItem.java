package org.alfresco.po.share.dashlet;

import org.alfresco.po.PageElement;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.ShareLink;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * The Object the represent Site search result item on Site Search Dashlet.
 * 
 * @author snagarajan
 */
public class SiteSearchItem extends PageElement
{
    private ShareLink thumbnail;
    private ShareLink itemName;
    private ShareLink path;

    public SiteSearchItem(WebElement searchItem, WebDriver driver, FactoryPage factoryPage)
    {
        this.thumbnail = new ShareLink(searchItem.findElement(By.cssSelector("td[class*='col-site'] a")), driver, factoryPage);
        this.itemName = new ShareLink(searchItem.findElement(By.cssSelector("td[class*='col-path'] div>h3>a")), driver, factoryPage);
        try
        {
            this.path = new ShareLink(searchItem.findElement(By.cssSelector("td[class*='col-path'] .details>a")), driver, factoryPage);
        }
        catch (NoSuchElementException nse)
        {
        }
    }

    public ShareLink getThumbnail()
    {
        return thumbnail;
    }

    public ShareLink getItemName()
    {
        return itemName;
    }

    public ShareLink getPath()
    {
        return path;
    }

}
