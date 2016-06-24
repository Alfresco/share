/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
