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
package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of an Simple/Detail/Table view of FileDirectoryInfo.
 * 
 * @author Chiran
 */
public abstract class SimpleDetailTableView extends FileDirectoryInfoImpl
{
    protected String CONTENT_ACTIONS = "td:nth-of-type(5)";

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoreAction()
     */
    private void selectMoreAction()
    {
        //reset focus
        //driver.findElement(By.tagName("body")).click();
        WebElement actions = selectAction();
        mouseOver(actions);
        WebElement more = actions.findElement(By.cssSelector(MORE_ACTIONS));
        more.click();
    }
    

    /**
     * Returns the WebElement for Actions in the selected row.
     * 
     * @return {Link WebElement} from where the set of Actions available for the
     *         selected content can be accessed
     */
    private WebElement selectAction()
    {
        return findElement(By.cssSelector(CONTENT_ACTIONS));
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectMoreLink()
     */
    @Override
    public void selectMoreLink()
    {
        selectMoreAction();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownloadFolderAsZip()
     */
    @Override
    public void selectDownloadFolderAsZip()
    {
        if (!isFolder())
        {
            throw new PageOperationException("Option Download Folder is not possible against a file, must be folder to workFileDirectoryInfoTest");
        }

        WebElement contentActions = selectAction();
        downloadFolderAsZip(contentActions);
        /*
         * Assumes driver capability settings to save file in a specific location when
         * <Download> option is selected via Browser
         */
    }

    /**
     * Clicks on the download folder as a zip button from the action menu
     * 
     * @param contentActions drop down menu web element
     * @param retry limits the number of tries
     */
    private void downloadFolderAsZip(WebElement contentActions, String... retry)
    {
        try
        {
            mouseOver(contentActions);
            super.downloadFolderAsZip();
        }
        catch (NoSuchElementException nse)
        {
            if (retry.length < 1)
            {
                downloadFolderAsZip(contentActions, "retry");
            }
            throw new PageException("Unable to click download folder as a zip", nse);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownload()
     */
    @Override
    public void selectDownload()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Option View Details is only available to Content of type Document");
        }

        WebElement contentActions = selectAction();
        mouseOver(contentActions);
        super.selectDownload();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewFolderDetails()
     */
    @Override
    public FolderDetailsPage selectViewFolderDetails()
    {
        WebElement contentActions = selectAction();
        mouseOver(contentActions);
        return super.selectViewFolderDetails();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        selectMoreAction();
        return super.selectInlineEdit();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        selectMoreAction();
        return super.isInlineEditLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        selectMoreAction();
        return super.isEditOfflineLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        selectMoreAction();
        return super.isDeletePresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        selectMoreAction();
        return super.selectManageRules();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManagePermission()
     */
    @Override
    public ManagePermissionsPage selectManagePermission()
    {
        selectMoreAction();
        return super.selectManagePermission();
    }

    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
        try
        {
            if (isLinkToFile() || isLinkToFolder()) 
            {
                mouseOver(selectContentActions());
            }
            else 
            {
                selectMoreAction();
            }
            return super.selectCopyTo();
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectCopyTo();
        }
        
        throw new PageOperationException("Unable to select the CopyTo...");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoveTo()
     */
    @Override
    public CopyOrMoveContentPage selectMoveTo()
    {
        try
        {
            if (isLinkToFile() || isLinkToFolder())
            {
                mouseOver(selectContentActions());
            }
            else 
            {
                selectMoreAction();
            }
            return super.selectMoveTo();
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectMoveTo();
        }
        
        throw new PageOperationException("Unable to select the MoveTo...");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectStartWorkFlow()
     */
    @Override
    public StartWorkFlowPage selectStartWorkFlow()
    {
        selectMoreAction();
        return super.selectStartWorkFlow();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUploadNewVersion()
     */

    @Override
    public UpdateFilePage selectUploadNewVersion()
    {
       try
        {
            WebElement actions = selectAction();
            mouseOver(actions);
            WebElement contentActions = selectAction();
            contentActions.findElement(By.cssSelector(MORE_ACTIONS)).click();
            return super.selectUploadNewVersion();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectUploadNewVersion();
        }

        throw new PageOperationException("Error in Select Delete.");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isManagePermissionLinkPresent()
     */
    @Override
    public boolean isManagePermissionLinkPresent()
    {
        selectMoreAction();
        return super.isManagePermissionLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditPropertiesLinkPresent()
     */
    @Override
    public boolean isEditPropertiesLinkPresent()
    {
        WebElement actions = selectAction();
        mouseOver(actions);
        resolveStaleness();
        if(!super.isEditPropertiesLinkPresent())
        {
            selectMoreAction();
        }
        return super.isEditPropertiesLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOffline()
    {
        selectMoreAction();
        return super.selectEditOffline();
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOfflineAndCloseFileWindow()
    {
        selectMoreAction();
        return super.selectEditOfflineAndCloseFileWindow();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCancelEditing()
     */
    @Override
    public DocumentLibraryPage selectCancelEditing()
    {
        selectMoreAction();
        return super.selectCancelEditing();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageAspects()
     */
    @Override
    public SelectAspectsPage selectManageAspects()
    {
        selectMoreAction();
        return super.selectManageAspects();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditProperties()
     */
    @Override
    public EditDocumentPropertiesPage selectEditProperties()
    {
        WebElement actions = selectAction();
        mouseOver(actions);
        if(!super.isEditPropertiesLinkPresent())
        {
            selectMoreAction();
        }
        return super.selectEditProperties();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewInBrowser()
     */
    @Override
    public void selectViewInBrowser()
    {
        WebElement actions = selectAction();
        mouseOver(actions);
        super.selectViewInBrowser();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDelete()
     */
    @Override
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            selectMoreAction();
            return super.selectDelete();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException st)
        {
            resolveStaleness();
            selectDelete();
        }

        throw new PageOperationException("Error in Select Delete.");
    }

    @Override
    public String getVersionInfo()
    {
        WebElement actions = selectAction();
        mouseOver(actions);
        return super.getVersionInfo();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewOriginalDocument()
     */
    @Override
    public HtmlPage selectViewOriginalDocument()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Option View Original Document is only available to Content of type Document");
        }

        selectMoreAction();
        return  super.selectViewOriginalDocument();
    }
    
    @Override
    public void contentNameEnableEdit()
    {
        // hover over tag area
        RenderTime timer = new RenderTime(maxPageLoadingTime * 2);
        while (true)
        {
            try
            {
                timer.start();
                super.contentNameEnableEdit();
                if (findElement(By.cssSelector(INPUT_CONTENT_NAME)).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException e)
            {
            }
            catch (ElementNotVisibleException e2)
            {
            }
            catch (StaleElementReferenceException stale)
            {
            }
            catch (TimeoutException te)
            {
            }
            finally
            {
                timer.end();
            }
        }
    }

    
    @Override
    public void declareRecord(){
        
        try
        {
            selectMoreAction();
            super.declareRecord();
        }
        catch (NoSuchElementException e)
        {
            
            throw(e);
        }
    }

    
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLocateFile()
     */
    @Override
    public void selectLocateFile()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Option View Details is only available to Content of type Document");
        }

        WebElement contentActions = selectAction();
        mouseOver(contentActions);
        super.selectLocateFile();
    }


    
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectPreviewWebAsset
     */
    @Override
    public void selectPreviewWebAsset()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Option Preview Web Asset is only available to Content of type Document");
        }

        WebElement contentActions = selectAction();
        mouseOver(contentActions);
        super.selectPreviewWebAsset();
    }

}
