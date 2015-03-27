package org.alfresco.po.share.bulkimport;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;

import java.util.Calendar;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Sergey Kardash
 */
public class InPlaceBulkImportPage extends AdvancedBulkImportPage
{
    private org.apache.commons.logging.Log logger = LogFactory.getLog(InPlaceBulkImportPage.class);

    // Input fields
    private static final By CONTENT_STORE = By.cssSelector("input[name$='contentStore']");

    public InPlaceBulkImportPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public InPlaceBulkImportPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InPlaceBulkImportPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public InPlaceBulkImportPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to set import directory field
     * 
     * @param contentStore
     */
    public void setContentStoreField(final String contentStore)
    {
        setInput(drone.findAndWait(CONTENT_STORE), contentStore);
    }

    /**
     * Method for bulk import creation
     * 
     * @param importDirectory
     * @param contentStore
     * @param path
     * @param disableRules
     * @return
     */
    public StatusBulkImportPage createImportInPlace(String importDirectory, String contentStore, String path, boolean disableRules)
    {
        logger.info("Create import");
        try
        {

            if (importDirectory != null && !importDirectory.isEmpty())
            {
                setImportDirectoryField(importDirectory);
            }
            else
            {
                setImportDirectoryField(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            }
            if (contentStore != null && !contentStore.isEmpty())
            {
                setContentStoreField(contentStore);
            }
            else
            {
                setContentStoreField("default");
            }
            if (path != null && !path.isEmpty())
            {
                setTargetPathField(path);
            }
            if (disableRules)
            {
                setDisableRulesCheckbox();
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
        drone.waitUntilElementPresent(IN_PROGRESS_CURRENT_STATUS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitUntilElementPresent(IDLE_CURRENT_STATUS, (SECONDS.convert(maxPageLoadingTime, MILLISECONDS))*2);
        return drone.getCurrentPage().render();
    }
}