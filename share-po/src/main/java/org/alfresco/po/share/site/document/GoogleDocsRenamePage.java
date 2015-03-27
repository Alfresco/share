package org.alfresco.po.share.site.document;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * The user can rename the title of the document inside googledocs page.
 * 
 * @author Subashni Prasanna
 * @since 1.5
 */

public class GoogleDocsRenamePage extends SharePage
{
    private static final By RENAME_DIALOG = By.cssSelector("div[class$='modal-dialog-content']");
    private static final By RENAME_DOCUMENT_NAME = By.cssSelector("input[class$='modal-dialog-userInput jfk-textinput']");
    private static final By OK_BUTTON = By.cssSelector("button[name$='ok']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[name$='cancel']");
    private boolean isGoogleCreate;

    /**
     * Constructor.
     */
    protected GoogleDocsRenamePage(WebDrone drone, boolean isGoogleCreate)
    {
        super(drone);
        this.isGoogleCreate = isGoogleCreate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsRenamePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsRenamePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public GoogleDocsRenamePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * function to check rename document is displayed.
     * 
     * @return true - if displayed.
     */
    public boolean isRenameDisplayed()
    {
        try
        {
            return (drone.find(RENAME_DIALOG).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Function to click on OK Button on the rename dialog
     */
    public EditInGoogleDocsPage updateDocumentName(String newName)
    {
        try
        {
            WebElement rename = drone.findAndWait(RENAME_DOCUMENT_NAME);
            rename.clear();
            rename.sendKeys(newName);
            drone.find(OK_BUTTON).click();
            drone.waitUntilElementDeletedFromDom(RENAME_DIALOG, 5);
            if (drone.isElementDisplayed(By.cssSelector("iframe")))
            {
                drone.switchToDefaultContent();
            }
            return new EditInGoogleDocsPage(drone, isGoogleCreate);
        }
        catch (TimeoutException te)
        {
            throw new PageOperationException("cannot rename the doucment - Time out", te);
        }
    }

    /**
     * Function to Cancel rename dialog
     */
    public EditInGoogleDocsPage cancelDocumentRename()
    {
        drone.find(CANCEL_BUTTON).click();
        drone.switchToDefaultContent();
        return new EditInGoogleDocsPage(drone, isGoogleCreate);
    }
}
