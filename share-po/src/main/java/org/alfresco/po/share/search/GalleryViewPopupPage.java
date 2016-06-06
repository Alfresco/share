package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * GalleryViewPopupPage object 
 * 
 * @author Charu
 */
public class GalleryViewPopupPage extends SharePage
{
    private static final By GALLERY_POPUP_TITLE = By.cssSelector("div[class='dijitDialogTitleBar']>span[class='dijitDialogTitle']");
    private static final By GALLERY_VIEW_CLOSE_BUTTON = By.cssSelector("span.dijitDialogCloseIcon");
    private WebElement galleryPopupTitle;
    private WebElement closeButton;

    
    @SuppressWarnings("unchecked")
    public GalleryViewPopupPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    
	/**
     * Verify is GalleryViewPopupPageTitle displayed,
     */
    public boolean isTitlePresent(String name)
    {
        try
        {
            galleryPopupTitle = findAndWait(GALLERY_POPUP_TITLE);
            if(galleryPopupTitle.isDisplayed())
            {
                if(galleryPopupTitle.getText().contains(name));
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
        catch (StaleElementReferenceException ste)
        {
            return false;
        }
        return false;
        
    }
    
    /**
     * Select close button.
     * 
     * @return {@link FacetedSearchPage} page response
     */
    public HtmlPage selectClose()
    {
        closeButton = driver.findElement(GALLERY_VIEW_CLOSE_BUTTON);
        Actions a = new Actions(driver);
        a.moveToElement(closeButton);
        a.click();
        a.perform();
        return factoryPage.instantiatePage(driver, FacetedSearchPage.class);
    }
}
