
package org.alfresco.po.share.search;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Advance Folder Search page object, holds all element of the folder search
 * page. The user can search with the following elements (keyword, Name,
 * title, Description).
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */

public class AdvanceSearchFolderPage extends AdvanceSearchPage
{
    protected static final By FOLDER_SEARCH_FORM = By.cssSelector("button[id$='selected-form-button-button']");


    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchFolderPage render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            synchronized (this)
            {
                try
                {
                    this.wait(100L);
                }
                catch (InterruptedException ite)
                {
                }
            }
            try
            {
                if (isFolderSearchPageDisplayed())
                {
                    if (isSearchButtonDisplayed())
                    {
                        break;
                    }
                }
            }
            catch (NoSuchElementException nse)
            {
                // Keep waiting for it
            }
            timer.end();
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchFolderPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Validate whether folder Search is displayed by validating the look for
     * field of search Page.
     * 
     * @return true Folder is present.
     */
    protected boolean isFolderSearchPageDisplayed()
    {
        Boolean displayed = false;
        try
        {
            WebElement folderSearchForm = driver.findElement(FOLDER_SEARCH_FORM);
            if (folderSearchForm != null && folderSearchForm.getText().contains("Folders"))
            {
                displayed = true;
            }
        }
        catch (NoSuchElementException nse)
        {
        }
        return displayed;
    }
}
