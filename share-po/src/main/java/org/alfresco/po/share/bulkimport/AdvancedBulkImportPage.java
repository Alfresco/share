package org.alfresco.po.share.bulkimport;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * @author Sergey Kardash
 */

public abstract class AdvancedBulkImportPage extends SharePage
{

    // Button
    protected static final By IDLE_CURRENT_STATUS = By.xpath("//td[contains(text(),'Current status')]/following-sibling::td/span[text()='Idle']");
    protected static final By IN_PROGRESS_CURRENT_STATUS = By.xpath("//td[contains(text(),'Current status')]/following-sibling::td/span[text()='In progress']");

    // CheckBoxes
    protected static final By CHECK_BOX_DISABLE_RULES = By.cssSelector("input[id='disableRules']");

    // Button
    protected static final By INITIATE_BULK_IMPORT_BUTTON = By.cssSelector("input[type='submit']");

    // Input fields
    protected static final By IMPORT_DIRECTORY = By.cssSelector("input[name$='sourceDirectory']");
    protected static final By TARGET_SPACE_PATH = By.cssSelector("input[name$='targetPath']");

    public AdvancedBulkImportPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedBulkImportPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedBulkImportPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedBulkImportPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to set disable rules check box
     */
    public void setDisableRulesCheckbox()
    {
        try
        {
            drone.find(CHECK_BOX_DISABLE_RULES).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find disable rules checkbox");
        }
    }

    /**
     * Method for clicking 'Initiate Bulk Import' button
     */
    public void clickImport()
    {
        WebElement importButton = drone.findAndWait(INITIATE_BULK_IMPORT_BUTTON);
        try
        {
            importButton.click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find 'Initiate Bulk Import' button");
        }
    }

    /**
     * Method to set String input in the field
     * 
     * @param input
     * @param value
     */
    public void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find the element");
        }
    }

    /**
     * Method to set import directory field
     * 
     * @param importDirectory
     */
    public void setImportDirectoryField(final String importDirectory)
    {
        setInput(drone.findAndWait(IMPORT_DIRECTORY), importDirectory);
    }

    /**
     * Method to set target space path field
     * 
     * @param path
     */
    public void setTargetPathField(final String path)
    {
        setInput(drone.findAndWait(TARGET_SPACE_PATH), path);
    }
}
