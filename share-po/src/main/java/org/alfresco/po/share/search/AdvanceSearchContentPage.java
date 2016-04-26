
package org.alfresco.po.share.search;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Advance search Content Search page object, holds all element of the content
 * Search page.
 * 
 * @author Subashni Prasanna
 * @since 1.6
 */
public class AdvanceSearchContentPage extends AdvanceSearchPage
{

    @SuppressWarnings("unchecked")
    @Override
    public AdvanceSearchContentPage render(RenderTime timer)
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
                if (isContentSearchDisplayed())
                {
                    if (isSearchButtonDisplayed() && isDateModifierFromDisplayed())
                    {
                        // It's there and visible
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
    public AdvanceSearchContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Check whether content search form is loaded correctly
     * 
     * @return true if page is displayed correctly
     */
    protected boolean isContentSearchDisplayed()
    {
        Boolean displayed = false;
        try
        {
            WebElement contentSearchForm = driver.findElement(CONTENT_SEARCH_FORM_DROPDOWN);
            if (contentSearchForm != null && contentSearchForm.getText().contains("Content"))
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
