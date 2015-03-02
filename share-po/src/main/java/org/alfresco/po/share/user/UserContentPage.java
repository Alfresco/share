package org.alfresco.po.share.user;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * User Content Page - with added and modified content
 * 
 * @author Bocancea Bogdan
 */

public class UserContentPage extends SharePage
{

    private static final By NO_ADDED_CONTENT = By.xpath(".//*[contains(@id,'default-body')]/div/p[1]");
    private static final By NO_MODIFIED_CONTENT = By.xpath(".//*[contains(@id,'default-body')]/div/p[2]");
    private static final By CONTENT_ADDED_LIST = By.xpath(".//div[@class='profile']//ul[1]");
    private static final By CONTENT_MODIFIED_LIST = By.xpath(".//div[@class='profile']//ul[2]");
    @RenderWebElement
    private static final By HEADER_BAR = By.cssSelector(".header-bar");

    private final Log logger = LogFactory.getLog(UserContentPage.class);

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public UserContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserContentPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public UserContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public UserContentPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Get the navigation bar.
     * 
     * @return {@link ProfileNavigation}
     */
    public ProfileNavigation getProfileNav()
    {
        return new ProfileNavigation(drone);
    }

    /**
     * Return <code>true</code> if the No content add message is displayed on screen.
     * 
     * @return boolean present
     */
    public boolean isNoContentAddMessagePresent()
    {
        boolean present = false;
        try
        {
            present = drone.findAndWait(NO_ADDED_CONTENT).getText().equals(drone.getValue("user.profile.content.noaddedcontent"));
            return present;
        }
        catch (NoSuchElementException e)
        {
        }

        return present;
    }

    /**
     * Return <code>true</code> if the No content modified message is displayed on screen.
     * 
     * @return boolean present
     */
    public boolean isNoContentModifiedMessagePresent()
    {
        boolean present = false;
        try
        {
            present = drone.findAndWait(NO_MODIFIED_CONTENT).getText().equals(drone.getValue("user.profile.content.nomodifiedcontent"));
            return present;
        }
        catch (NoSuchElementException e)
        {
        }

        return present;
    }

    /**
     * Get a list of added contents.
     * 
     * @return A {@link List} of {@link UserContentItems}.
     */
    public List<UserContentItems> getContentAdded()
    {
        return getContentModifiedBySelector(CONTENT_ADDED_LIST);
    }

    /**
     * Get a list of modified contents.
     * 
     * @return A {@link List} of {@link UserContentItems}.
     */
    public List<UserContentItems> getContentModified()
    {
        return getContentModifiedBySelector(CONTENT_MODIFIED_LIST);
    }
    
    /**
     * Get a list of modified contents.
     * @param by - Locator for content
     * 
     * @return A {@link List} of {@link UserContentItems}.
     */
    private List<UserContentItems> getContentModifiedBySelector(By by)
    {
        List<UserContentItems> contentsModified = new ArrayList<>();
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(by);

            for (WebElement el : elements)
            {
                UserContentItems contentModified = new UserContentItems(el, drone);
                contentsModified.add(contentModified);
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find any sites in " + by, e);
        }
        return contentsModified;
    }

    /**
     * Get a {@link UserContentItems} for the named site
     * 
     * @param contentName
     * @return contentAdded
     */
    public List<UserContentItems> getContentAdded(String contentName)
    {
        List<UserContentItems> contents = getContentAdded();
        List<UserContentItems> contentByName = new ArrayList<UserContentItems>();

        for (UserContentItems contendAdded : contents)
        {
            if (contendAdded.getContentName().equals(contentName))
            {
                contentByName.add(contendAdded);
            }
        }

        if(contentByName.isEmpty())
        {
            throw new PageOperationException("Unable to find content: " + contentName);
        }
        return contentByName;
    }

    /**
     * Get a {@link UserContentItems} for the named content
     * 
     * @param contentName
     * @return contentModified
     */
    public  List<UserContentItems> getContentModified(String contentName)
    {
        List<UserContentItems> contents = getContentModified();
        List<UserContentItems> contentByName = new ArrayList<UserContentItems>();

        for (UserContentItems contentModified : contents)
        {
            if (contentModified.getContentName().equals(contentName))
            {
                contentByName.add(contentModified);
            }
        }

        if(contentByName.isEmpty())
        {
            throw new PageOperationException("Unable to find content: " + contentName);
        }
        return contentByName;
    }

}
