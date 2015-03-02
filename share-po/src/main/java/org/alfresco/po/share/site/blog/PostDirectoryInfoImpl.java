package org.alfresco.po.share.site.blog;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Sergey Kardash
 */
public class PostDirectoryInfoImpl extends HtmlElement implements PostDirectoryInfo {

    private Log logger = LogFactory.getLog(this.getClass());

    private static final By EDIT_POST = By.cssSelector(".onEditBlogPost>a");
    private static final By DELETE_POST = By.cssSelector(".onDeleteBlogPost>a");

    /**
     * Constructor
     */
    protected PostDirectoryInfoImpl(WebDrone drone, WebElement webElement)
    {
        super(webElement, drone);
    }

    @Override
    public EditPostForm editPost() {
        try
        {
            findAndWait(EDIT_POST).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find Edit button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new EditPostForm(drone);
    }

    @Override
    public BlogPage deletePost() {
        try
        {
            findAndWait(DELETE_POST).click();
            drone.findAndWait(By.xpath("//span[@class='button-group']/span[1]/span/button")).click();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find Delete button");
        }
        catch (TimeoutException te)
        {
            logger.error("The operation has timed out");
        }
        return new BlogPage(drone).waitUntilAlert().render();
    }

    @Override
    public boolean isEditPostDisplayed() {
        try
        {
            WebElement editLink = findElement(EDIT_POST);
            return editLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    @Override
    public boolean isDeletePostDisplayed() {
        try
        {
            WebElement deleteLink = findElement(DELETE_POST);
            return deleteLink.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
}
