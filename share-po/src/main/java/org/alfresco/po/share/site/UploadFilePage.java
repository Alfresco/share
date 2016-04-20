package org.alfresco.po.share.site;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Upload file page object, holds all element of the HTML page relating to
 * share's upload file page in site.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class UploadFilePage extends ShareDialogue
{
    private Log logger = LogFactory.getLog(this.getClass());
    @FindBy(css="input.dnd-file-selection-button") WebElement uploadFileInput;
    @RenderWebElement @FindBy(css="img.title-folder") WebElement uploadFolderIcon; 

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public UploadFilePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    public boolean isUploadInputDisplayed()
    {
       return uploadFileInput.isDisplayed();
    }

    /**
     * Uploads a file by entering the file location into the input field and
     * submitting the form.
     * 
     * @param filePath String file location to upload
     * @return {@link SharePage} DocumentLibrary or a RepositoryPage response
     */
    public HtmlPage uploadFile(final String filePath)
    {
        DocumentLibraryPage lib = upload(filePath).render();
        lib.setShouldHaveFiles(true);
        return lib;
    }

    public HtmlPage upload(final String filePath)
    {
        uploadFileInput.sendKeys(filePath);
        if (logger.isTraceEnabled())
        {
            logger.trace("Upload button has been actioned");
        }
        return getCurrentPage();
    }

    /**
     * Clicks on the cancel link.
     */
    public void cancel()
    {
        List<WebElement> cancelButton = driver.findElements(By.cssSelector("button[id$='default-cancelOk-button-button']"));
        for (WebElement webElement : cancelButton)
        {
            if (webElement.isDisplayed() && webElement.isEnabled())
            {
                webElement.click();
            }
        }
    }
}
