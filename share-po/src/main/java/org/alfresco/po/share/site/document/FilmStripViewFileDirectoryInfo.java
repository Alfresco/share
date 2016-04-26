package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Abhijeet Bharade
 */
public class FilmStripViewFileDirectoryInfo extends FilmStripOrGalleryView
{

    private static Log logger = LogFactory.getLog(FilmStripViewFileDirectoryInfo.class);

//    /**
//     * @param nodeRef
//     * @param webElement
//     * @param driver
//     */
//    public FilmStripViewFileDirectoryInfo(String nodeRef, WebElement webElement, WebDriver driver)
//    {
//        super(nodeRef, webElement, driver);
//
//        THUMBNAIL_TYPE = String.format(".//div[@class='alf-filmstrip-nav-item-thumbnail']//img[@id='%s']", nodeRef);
//        rowElementXPath = "../../../..";
//        FILE_DESC_IDENTIFIER = "div.detail:first-of-type span.item";
//        THUMBNAIL = THUMBNAIL_TYPE + "/../..";
//        DETAIL_WINDOW = By.xpath("//div[@class='alf-actions']/../../..");
//        resolveStaleness();
//        TAG_ICON = "//h3[@class='filename']/span/a[text()='%s']/../../../div/span[@class='insitu-edit']";
//    }

    /**
     * @return WebElement
     */
    @Override
    protected WebElement getInfoIcon()
    {
        try
        {
            if (findAndWait(By.xpath(THUMBNAIL)).isDisplayed())
            {
                selectThumbnail();
            }
            else
            {
                throw new PageException("Thumbnail not visible");
            }
            return findAndWait(By.cssSelector("a.alf-show-detail"));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the css.", e);
        }

        throw new PageException("File directory info with title was not found");
    }

    @Override
    public String getName()
    {
        return findAndWait(By.xpath(THUMBNAIL)).getText();
    }

    /**
     * Returns true if content in the selected data row on DocumentLibrary is folder Page.
     * 
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
    @Override
    public boolean isFolder()
    {
        try
        {
            WebElement thumbnailType = findAndWait(By.xpath(THUMBNAIL_TYPE));
            if (logger.isTraceEnabled())
            {
                logger.trace("thumbnailType - " + thumbnailType.getAttribute("src"));
            }
            return thumbnailType.getAttribute("src").contains("folder");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectThumbnail()
     */
    @Override
    public HtmlPage selectThumbnail()
    {
        findAndWait(By.xpath(THUMBNAIL)).click();
        domEventCompleted();
        return getCurrentPage();
    }

    /**
     * Gets the Title of the file or directory, if none then empty string is returned.
     * 
     * @return String Content description
     */
    @Override
    public String getTitle()
    {
        clickInfoIcon();
        return super.getTitle();
    }

    @Override
    public void selectCheckbox()
    {
        clickInfoIcon();
        super.selectCheckbox();
    }
}
