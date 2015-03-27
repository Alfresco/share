package org.alfresco.po.share.search;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author  Olga Antonik
 */
@SuppressWarnings("unchecked")
public class PreViewPopUpImagePage  extends SharePage
{


    @RenderWebElement
    private static final By PREVIEW_IMAGE = By.xpath("//a/img[@id='lightboxImage']");
    private static final By PREVIEW_CLOSE_BUTTON = By.xpath("//a/img[@id='closeButton']");
    private static final Log logger = LogFactory.getLog(PreViewPopUpPage.class);

    public PreViewPopUpImagePage(WebDrone drone)
    {
        super(drone);
    }

    public PreViewPopUpImagePage render(RenderTime timer)
    {
        webElementRender(timer);
        List<WebElement> elements = drone.findAll(PREVIEW_IMAGE);
        for (WebElement webElement : elements)
        {
            System.out.println(webElement.isDisplayed());
        }
        return this;
    }


    @Override
    public PreViewPopUpImagePage render(long time)
    {
        return render(new RenderTime(time));
    }


    @Override
    public PreViewPopUpImagePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if preview popup page is visible,
     *
     * @return true if displayed
     */
    public boolean isPreViewPopupPageVisible()
    {
        try
        {
            return drone.findFirstDisplayedElement(PREVIEW_IMAGE).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
    }

    /**
     * Select close button.
     *
     * @return {@link FacetedSearchPage} page response
     */
    public FacetedSearchPage selectClose()
    {
        try
        {
            WebElement closeButton = drone.findFirstDisplayedElement(PREVIEW_CLOSE_BUTTON);
            if(closeButton.isDisplayed())
                closeButton.click();

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find close button");
            }
        }
        catch (StaleElementReferenceException e )
        {
            e.printStackTrace();
        }

        return new FacetedSearchPage(drone);
    }
}
