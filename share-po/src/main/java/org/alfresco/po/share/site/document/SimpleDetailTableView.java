/*
 * Copyright (C) 2005-2012 Alfresco Software Limited. This file is part of
 * Alfresco Alfresco is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. Alfresco is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Alfresco. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.exception.AlfrescoVersionException;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneImpl;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static Log logger = LogFactory.getLog(SimpleDetailTableView.class);
    protected String CONTENT_ACTIONS = "td:nth-of-type(5)";

    public SimpleDetailTableView(String nodeRef, WebElement webElement, WebDrone drone)
    {
        super(nodeRef, webElement, drone);
        MORE_ACTIONS = drone.getElement("more.actions");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoreAction()
     */
    private WebElement selectMoreAction()
    {
        WebElement actions = selectAction();
        getDrone().mouseOverOnElement(actions);
        WebElement contentActions = selectAction();
        return contentActions.findElement(By.cssSelector(MORE_ACTIONS));
    }
    
    private void clickMoreAction()
    {
        try
        {
            selectMoreAction().click();
        }
        catch(NoSuchElementException e)
        {
            logger.error("select more action failed. " + e);
        }
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
        selectMoreAction().click();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownloadFolderAsZip()
     */
    @Override
    public void selectDownloadFolderAsZip()
    {
        AlfrescoVersion version = getDrone().getProperties().getVersion();
        if (!isFolder())
        {
            throw new PageOperationException("Option Download Folder is not possible against a file, must be folder to workFileDirectoryInfoTest");
        }
        if (AlfrescoVersion.Enterprise41.equals(version) || version.isCloud())
        {
            throw new AlfrescoVersionException("Option Download Folder as Zip is not available for this version of Alfresco");
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
            getDrone().mouseOver(contentActions);
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
        getDrone().mouseOverOnElement(contentActions);
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
        getDrone().mouseOver(contentActions);
        return super.selectViewFolderDetails();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectSyncToCloud()
     */
    @Override
    public HtmlPage selectSyncToCloud()
    {
        selectMoreAction().click();
        return super.selectSyncToCloud();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditInGoogleDocs()
     */
    @Override
    public HtmlPage selectEditInGoogleDocs()
    {
        selectMoreAction().click();
        return super.selectEditInGoogleDocs();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectUnSyncFromCloud()
     */
    @Override
    public void selectUnSyncFromCloud()
    {
        selectMoreAction().click();
        super.selectUnSyncFromCloud();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        selectMoreAction().click();
        return super.selectInlineEdit();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        clickMoreAction();
        return super.isInlineEditLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        clickMoreAction();
        return super.isEditOfflineLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditInGoogleDocsPresent()
     */
    @Override
    public boolean isEditInGoogleDocsPresent()
    {
        clickMoreAction();
        boolean isEditInGoogleDocsLink = super.isEditInGoogleDocsPresent();
        focusOnFileOrFolder();
        return isEditInGoogleDocsLink;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        clickMoreAction();
        return super.isDeletePresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        selectMoreAction().click();
        return super.selectManageRules();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isUnSyncFromCloudLinkPresent()
     */
    @Override
    public boolean isUnSyncFromCloudLinkPresent()
    {
        clickMoreAction();
        return super.isUnSyncFromCloudLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectRequestSync()
     */
    @Override
    public DocumentLibraryPage selectRequestSync()
    {
        selectMoreAction().click();
        return super.selectRequestSync();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isRequestToSyncLinkPresent()
     */
    @Override
    public boolean isRequestToSyncLinkPresent()
    {
        clickMoreAction();
        return super.isRequestToSyncLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isSyncToCloudLinkPresent()
     */
    @Override
    public boolean isSyncToCloudLinkPresent()
    {
        clickMoreAction();
        return super.isSyncToCloudLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManagePermission()
     */
    @Override
    public ManagePermissionsPage selectManagePermission()
    {
        selectMoreAction().click();
        return super.selectManagePermission();
    }

    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
        try
        {
            selectMoreAction().click();
            return super.selectCopyTo();
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
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoveTo()
     */
    @Override
    public CopyOrMoveContentPage selectMoveTo()
    {
        try
        {
            selectMoreAction().click();
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
        selectMoreAction().click();
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
            drone.mouseOver(actions);
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
        clickMoreAction();
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
        getDrone().mouseOverOnElement(actions);
        resolveStaleness();
        return super.isEditPropertiesLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOffline()
    {
        selectMoreAction().click();
        return super.selectEditOffline();
    }
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOfflineAndCloseFileWindow()
    {
        selectMoreAction().click();
        return super.selectEditOfflineAndCloseFileWindow();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCancelEditing()
     */
    @Override
    public DocumentLibraryPage selectCancelEditing()
    {
        selectMoreAction().click();
        return super.selectCancelEditing();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageAspects()
     */
    @Override
    public SelectAspectsPage selectManageAspects()
    {
        selectMoreAction().click();
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
        getDrone().mouseOverOnElement(actions);
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
        getDrone().mouseOverOnElement(actions);
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
            selectMoreAction().click();
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

    private void focusOnFileOrFolder()
    {
        drone.mouseOverOnElement(drone.findAndWait(By.xpath("//h3[@class='filename']/span/a[text()='" + getName() + "']")));
    }

    @Override
    public String getVersionInfo()
    {
        WebElement actions = selectAction();
        getDrone().mouseOverOnElement(actions);
        return super.getVersionInfo();
    }

    /* (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#selectForceUnSyncInCloud()
     */
    @Override
    public DocumentLibraryPage selectForceUnSyncInCloud()
    {
        selectMoreAction().click();
        return super.selectForceUnSyncInCloud();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewOriginalDocument()
     */
    @Override
    public DocumentDetailsPage selectViewOriginalDocument()
    {
        if (isFolder())
        {
            throw new UnsupportedOperationException("Option View Original Document is only available to Content of type Document");
        }

        clickMoreAction();
        return  super.selectViewOriginalDocument();
    }
    
    @Override
    public void contentNameEnableEdit()
    {
        // hover over tag area
        RenderTime timer = new RenderTime(((WebDroneImpl) getDrone()).getMaxPageRenderWaitTime() * 2);
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
            selectMoreAction().click();
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
        getDrone().mouseOverOnElement(contentActions);
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
        getDrone().mouseOverOnElement(contentActions);
        super.selectPreviewWebAsset();
    }

    /**
     * Method to select Check In Google Doc
     *
     * @return GoogleDocCheckInPage
     */
    @Override
    public GoogleDocCheckInPage selectCheckInGoogleDoc()
    {
        selectMoreAction().click();
        return super.selectCheckInGoogleDoc();
    }

    /**
     * Method to select Cancel Editing in Google Docs
     *
     * @return DocumentLibraryPage
     */
    @Override
    public DocumentLibraryPage selectCancelEditingInGoogleDocs()
    {
        selectMoreAction().click();
        return super.selectCancelEditingInGoogleDocs();
    }
}