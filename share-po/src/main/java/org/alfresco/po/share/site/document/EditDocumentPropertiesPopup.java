package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * Edit document properties pop up page object, holds all element of the HTML page
 * relating to share's edit document properties pop up page.
 * 
 * @author Michael Suzuki
 * @since 1.3.1
 */
@Deprecated
public class EditDocumentPropertiesPopup extends AbstractEditProperties
{

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPopup render(RenderTime timer)
    {
        while (true)
        {
            timer.start();
            try
            {
                if (isEditPropertiesPopupVisible())
                {
                    break;
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                timer.end();
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditDocumentPropertiesPopup render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if edit properties element,
     * that contains the form is visible.
     * 
     * @return true if displayed
     */
    public boolean isEditPropertiesPopupVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector("form.bd")).isDisplayed();
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
     * Clicks on save button from the popup view on document
     * library page.
     * 
     * @return {@link DocumentLibraryPage} page response
     */
    public HtmlPage selectSave()
    {
        clickSave();
        canResume();
        return getCurrentPage();
    }

    /**
     * Select cancel button.
     * 
     * @return {@link DocumentLibraryPage} page response
     */
    public HtmlPage selectCancel()
    {
        clickOnCancel();
        return getCurrentPage();
    }
}
