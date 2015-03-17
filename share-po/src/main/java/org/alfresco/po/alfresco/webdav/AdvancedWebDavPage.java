package org.alfresco.po.alfresco.webdav;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

/**
 * @author Sergey Kardash
 */
public class AdvancedWebDavPage extends SharePage
{

    private static Log logger = LogFactory.getLog(AdvancedWebDavPage.class);
    private final By DIRECTORY_LISTING = By.cssSelector("td[class='textLocation']");
    private final String DIRECTORY_LINK = "//td[@class='textData']/a[text()='%s']";
    private final By UP_TO_LEVEL = By.xpath("//td[@class='textData']/a[text()='[Up a level]']");

    public AdvancedWebDavPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedWebDavPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedWebDavPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedWebDavPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to verify webdav is opened
     *
     * @return boolean
     */
    public boolean isOpened()
    {
        try
        {
            return drone.find(DIRECTORY_LISTING).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;

    }

    /**
     * Method to verify webdav is opened
     *
     * @return String
     */
    public String getDirectoryText()
    {
        return drone.findAndWait(DIRECTORY_LISTING).getText();
    }

    /**
     * Method to click directory for webdav page
     */
    public void clickDirectory(String directoryName)
    {
        drone.findAndWait(By.xpath(String.format(DIRECTORY_LINK, directoryName))).click();
    }

    /**
     * Method to check directory is displayed for webdav page
     *
     * @return boolean
     */
    public boolean checkDirectoryDisplayed(String directoryName)
    {
        try
        {
            return drone.find(By.xpath(String.format(DIRECTORY_LINK, directoryName))).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Method to click directory for webdav page
     */
    public void clickUpToLevel()
    {
        drone.findAndWait(UP_TO_LEVEL).click();
    }

    /**
     * Method to check directory is displayed for webdav page
     *
     * @return boolean
     */
    public boolean checkUpToLevelDisplayed()
    {
        try
        {
            return drone.find(UP_TO_LEVEL).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

}
