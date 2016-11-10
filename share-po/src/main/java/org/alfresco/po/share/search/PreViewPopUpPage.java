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
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.ShareDialogueAikau;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * PreView pop up page object 
 * 
 * @author Charu
 */
@SuppressWarnings("unchecked")
public class PreViewPopUpPage extends ShareDialogueAikau
{
    private static final By PREVIEW_POPUP = By.cssSelector(".alfresco-dialog-AlfDialog.dijitDialog");
    private static final By IMG_ON_POPUP = By.cssSelector(".previewer");
    private static final By PREVIEW_TEXT = By.cssSelector(".textLayer>div");
    @RenderWebElement
    private static final By PREVIEW_TITLE = By.cssSelector("span.dijitDialogTitle");
    private static final By PREVIEW_CLOSE_BUTTON = By.cssSelector("span.dijitDialogCloseIcon");
    private WebElement closeButton;
    private WebElement previewTitle;
    private WebElement imgOnPopUp;
    private WebElement previewText;

    @Override
    public PreViewPopUpPage render()
    {
        render(new RenderTime(maxPageLoadingTime));
        return this;
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
            return findFirstDisplayedElement(PREVIEW_POPUP).isDisplayed();
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
     * Verify is PreviewPageTitle displayed,
     */
    public boolean isTitlePresent(String name)
    {
        try
        {
            previewTitle = findFirstDisplayedElement(PREVIEW_TITLE);
            if(previewTitle.isDisplayed())
            {
                if(previewTitle.getText().equals(name));
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
     * Verify is PreviewImage displayed,
     */
    public boolean isPreViewDisplayed()
    {
        try
        {
            imgOnPopUp = findFirstDisplayedElement(IMG_ON_POPUP);
            if(imgOnPopUp.isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException | TimeoutException e)
        {
        }
        return false;
    }
    
    /**
     * Verify is PreviewText displayed,
     */
    public boolean isPreViewTextDisplayed()
    {
        try
        {
            previewText = findAndWait(PREVIEW_TEXT);
            if(previewText.isDisplayed())
            {
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
        closeButton = driver.findElement(PREVIEW_CLOSE_BUTTON);
        Actions a = new Actions(driver);
        a.moveToElement(closeButton);
        a.click();
        a.perform();
        driver.findElements(PREVIEW_CLOSE_BUTTON);
        return factoryPage.instantiatePage(driver, FacetedSearchPage.class);
    }
}
