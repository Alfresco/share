package org.alfresco.po.alfresco.webdav;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * @author Sergey Kardash
 */
public class AdvancedWebDavPage extends SharePage
{

    private final By DIRECTORY_LISTING = By.cssSelector("td[class='textLocation']");
    private final String DIRECTORY_LINK = "//td[@class='textData']/a[text()='%s']";
    private final By UP_TO_LEVEL = By.xpath("//td[@class='textData']/a[text()='[Up a level]']");

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

    /**
     * Method to verify webdav is opened
     *
     * @return boolean
     */
    public boolean isOpened()
    {
        try
        {
            return driver.findElement(DIRECTORY_LISTING).isDisplayed();
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
        return findAndWait(DIRECTORY_LISTING).getText();
    }

    /**
     * Method to click directory for webdav page
     */
    public void clickDirectory(String directoryName)
    {
        findAndWait(By.xpath(String.format(DIRECTORY_LINK, directoryName))).click();
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
            return driver.findElement(By.xpath(String.format(DIRECTORY_LINK, directoryName))).isDisplayed();
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
        findAndWait(UP_TO_LEVEL).click();
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
            return driver.findElement(UP_TO_LEVEL).isDisplayed();
        }
        catch (Exception e)
        {
        }
        return false;
    }

}
