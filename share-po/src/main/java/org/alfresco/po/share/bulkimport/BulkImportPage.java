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
package org.alfresco.po.share.bulkimport;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactorySharePage;
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


    /**
     * Method to set target space path field
     * 
     * @param nodeRef String
     */
    public void setNodeRefField(final String nodeRef)
    {
        setInput(findAndWait(TARGET_SPACE_NODEREF), nodeRef);
    }

    /**
     * Method to set replace existing files check box
     */
    public void setReplaceExistingFilesCheckbox()
    {
        try
        {
            driver.findElement(CHECK_BOX_REPLACE_EXISTING).click();
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find replace existing files checkbox");
        }
    }

    /**
     * Method for bulk import creation
     * 
     * @param importDirectory String
     * @param path String
     * @param nodeRef String
     * @param disableRules boolean
     * @param replaceExistingFiles boolean
     * @return StatusBulkImportPage
     */
    public HtmlPage createImport(String importDirectory, String path, String nodeRef, boolean disableRules, boolean replaceExistingFiles)
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

        waitUntilElementDeletedFromDom(INITIATE_BULK_IMPORT_BUTTON, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        waitUntilElementPresent(IDLE_CURRENT_STATUS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        return getCurrentPage();
    }
}
