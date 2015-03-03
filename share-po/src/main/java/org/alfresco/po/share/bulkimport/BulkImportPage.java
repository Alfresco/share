package org.alfresco.po.share.bulkimport;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

/**
 * @author Sergey Kardash on 5/16/14.
 */
@SuppressWarnings("unused")
public class BulkImportPage extends AdvancedBulkImportPage
{
    private Log logger = LogFactory.getLog(BulkImportPage.class);

    // Input fields
    private static final By TARGET_SPACE_PATH = By.cssSelector("input[name$='targetPath']");
    private static final By TARGET_SPACE_NODEREF = By.cssSelector("input[name$='targetNodeRef']");

    // CheckBoxes
    private static final By CHECK_BOX_DISABLE_RULES = By.cssSelector("input[id='disableRules']");
    private static final By CHECK_BOX_REPLACE_EXISTING = By.cssSelector("input[id='replaceExisting']");

    // Button
    private static final By INITIATE_BULK_IMPORT_BUTTON = By.cssSelector("input[type='submit']");

    public BulkImportPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BulkImportPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BulkImportPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BulkImportPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to set target space path field
     * 
     * @param nodeRef
     */
    public void setNodeRefField(final String nodeRef)
    {
        setInput(drone.findAndWait(TARGET_SPACE_NODEREF), nodeRef);
    }

    /**
     * Method to set replace existing files check box
     */
    public void setReplaceExistingFilesCheckbox()
    {
        try
        {
            drone.find(CHECK_BOX_REPLACE_EXISTING).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find replace existing files checkbox");
        }
    }

    /**
     * Method for bulk import creation
     * 
     * @param importDirectory
     * @param path
     * @param nodeRef
     * @param disableRules
     * @param replaceExistingFiles
     * @return StatusBulkImportPage
     */
    public StatusBulkImportPage createImport(String importDirectory, String path, String nodeRef, boolean disableRules, boolean replaceExistingFiles)
    {
        logger.info("Create import");
        try
        {

            if (importDirectory != null && !importDirectory.isEmpty())
            {
                setImportDirectoryField(importDirectory);
            }
            if (path != null && !path.isEmpty())
            {
                setTargetPathField(path);
            }
            if (nodeRef != null && !nodeRef.isEmpty())
            {
                setNodeRefField(nodeRef);
            }
            if (disableRules)
            {
                setDisableRulesCheckbox();
            }
            if (replaceExistingFiles)
            {
                setReplaceExistingFilesCheckbox();
            }
            clickImport();
        }
        catch (TimeoutException te)
        {
            logger.debug("The operation has timed out");
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("Unable to find the elements");
        }

        drone.waitUntilElementDeletedFromDom(INITIATE_BULK_IMPORT_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitUntilElementPresent(IDLE_CURRENT_STATUS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return drone.getCurrentPage().render();
    }
}
