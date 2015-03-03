/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.site.document;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.enums.ZoomStyle;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.user.CloudSignInPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Represent elements found on the HTML page relating to the document library
 * sub navigation bar that appears on the document library site page.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
public class DocumentLibraryNavigation extends SharePage
{
    public static final String FILE_UPLOAD_BUTTON = "button[id$='fileUpload-button-button']";
    public static final String CREATE_NEW_FOLDER_BUTTON = "button[id$='newFolder-button-button']";
    private static final By CREATE_CONTENT_BUTTON = By.cssSelector("button[id$='createContent-button-button']");
    private static final By SELECTED_ITEMS = By.cssSelector("button[id$='selectedItems-button-button']");
    private static final By SELECTED_ITEMS_MENU = By.cssSelector("div[id$='selectedItems-menu']");
    private static final By DOWNLOAD_AS_ZIP = By.cssSelector(".onActionDownload");
    private static final By SELECT_DROPDOWN = By.cssSelector("button[id$='default-fileSelect-button-button']");
    private static final By SELECT_DROPDOWN_MENU = By.cssSelector("div[id$='default-fileSelect-menu']");
    private static final By SELECT_ALL = By.cssSelector(".selectAll");
    private static final By SELECT_NONE = By.cssSelector(".selectNone");
    private static final By SYNC_TO_CLOUD = By.cssSelector(".onActionCloudSync");
    private static final By REQUEST_SYNC = By.cssSelector(".onActionCloudSyncRequest");
    private static final String FILE_UPLOAD_ERROR_MESSAGE = "Unable to create file upload page";
    private static final String CREATE_FOLDER_ERROR_MESSAGE = "Unable to create new folder page";
    private static final By DELETE = By.cssSelector(".onActionDelete");
    private static final By COPY_TO = By.cssSelector(".onActionCopyTo");
    private static final By MOVE_TO = By.cssSelector(".onActionMoveTo");
    private static final By DESELECT_ALL = By.cssSelector(".onActionDeselectAll");
    private static final By START_WORKFLOW = By.cssSelector(".onActionAssignWorkflow");
    private static final By SET_DEFAULT_VIEW = By.cssSelector(".setDefaultView");
    private static final By REMOVE_DEFAULT_VIEW = By.cssSelector(".removeDefaultView");
    private static final By FOLDER_UP_BUTTON = By.cssSelector("button[id$='folderUp-button-button']");
    private static final By CRUMB_TRAIL = By.cssSelector("div[id$='default-breadcrumb']");
    private static final By CREATE_NEW_FOLDER = By.cssSelector(".folder-file");
    private final By ZOOM_CONTROL_BAR_THUMBNAIL_CSS = By.cssSelector(".alf-gallery-slider-thumb");
    private static final By SORT_DROPDOWN = By.cssSelector("button[id$='default-sortField-button-button']");
    private static final By SORT_DIRECTION_BUTTON = By.cssSelector("button[id$='default-sortAscending-button-button']");
    private static final By SORT_DIRECTION = By.cssSelector("span[id$='default-sortAscending-button']");
    private static final By CURRENT_SORT_FIELD = By.cssSelector("div[class$='sort-field'] button");
    private static final By SORT_FIELD = By.cssSelector("div[class$='sort-field'] > div");
    private static final By SELECT_FOLDERS = By.cssSelector(".selectFolders");
    private static final By SELECT_DOCUMENTS = By.cssSelector(".selectDocuments");
    private static final By SELECT_INVERT_SELECTION = By.cssSelector(".selectInvert");
    private static final String CREATE_A_FOLDER_LINK = (".docListInstructionsWithDND div[id$='new-folder-template'] a");
    private static final String CREATE_FOLDER_FROM_TEMPLATE = "//span[text()='Create folder from template']/parent::a";
    private static final String CREATE_DOCUMENT_FROM_TEMPLATE = "//span[text()='Create document from template']/parent::a";
    private static final By BREAD_CRUMBS_PARENT = By.cssSelector("div[id$='default-breadcrumb'] a[class='folder']:first-child");
    private static final By BREAD_CRUMBS_PARENT_SPAN = By.cssSelector("span[class='label']>a");
    private static final By SYNC_TO_CLOUD_BUTTON = By.cssSelector("button[id$=default-syncToCloud-button-button]");

    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * Constructor.
     */
    public DocumentLibraryNavigation(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryNavigation render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryNavigation render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryNavigation render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * @return <tt>true</tt> if the <b>Upload</b> link is available
     * @since 1.5.1
     */
    public boolean hasFileUploadLink()
    {
        try
        {
            By criteria = By.cssSelector(FILE_UPLOAD_BUTTON);
            return drone.findAndWait(criteria, 100).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Mimics the action of selecting the file upload button.
     * 
     * @return HtmlPage response page object
     */
    public HtmlPage selectFileUpload()
    {
        if (!alfrescoVersion.isFileUploadHtml5())
        {
            setSingleMode();
        }
        WebElement button = drone.findAndWait(By.cssSelector(FILE_UPLOAD_BUTTON));
        button.click();
        return getFileUpload(drone);
    }

    /**
     * Get file upload page pop up object.
     * 
     * @param drone WebDrone browser client
     * @return SharePage page object response
     */
    public HtmlPage getFileUpload(WebDrone drone)
    {

        // Verify if it is really file upload page, and then create the page.
        try
        {
            WebElement element;
            switch (alfrescoVersion)
            {
                case Enterprise41:
                    element = drone.findAndWait(By.cssSelector("form[id$='_default-htmlupload-form']"));
                    break;
                default:
                    // Find by unique folder icon that appears in the dialog
                    element = drone.findAndWait(By.cssSelector("img.title-folder"));
                    break;
            }

            if (element.isDisplayed())
            {
                return new UploadFilePage(drone);
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException(FILE_UPLOAD_ERROR_MESSAGE, te);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(FILE_UPLOAD_ERROR_MESSAGE, e);
        }
        throw new PageException(FILE_UPLOAD_ERROR_MESSAGE);
    }

    /**
     * @return <tt>true</tt> if the <b>New Folder</b> link is available
     * @since 1.5.1
     */
    public boolean hasNewFolderLink()
    {
        try
        {
            By criteria = By.cssSelector(CREATE_NEW_FOLDER_BUTTON);
            return drone.findAndWait(criteria, 250).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    /**
     * Introduced in Enterprise 4.2 dropdown with actions
     * to create different content such as folders and files.
     * 
     * @return Current Page of {@link HtmlPage}
     */
    public HtmlPage selectCreateContentDropdown()
    {
        try
        {
            WebElement createContentElement = drone.findAndWait(CREATE_CONTENT_BUTTON);

            if (createContentElement.isEnabled())
            {
                createContentElement.click();
                return FactorySharePage.resolvePage(drone);
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Create Content not available : " + CREATE_CONTENT_BUTTON.toString(), e);
        }
        throw new PageException("Not able to click the Create Content Button.");
    }

    /**
     * Mimics the action of selecting the create new folder button.
     * 
     * @return {@link NewFolderPage} page response
     */
    public NewFolderPage selectCreateNewFolder()
    {
        WebElement button;
        switch (alfrescoVersion)
        {
            case Enterprise41:
                button = drone.findAndWait(By.cssSelector(CREATE_NEW_FOLDER_BUTTON));
                break;
            default:
                selectCreateContentDropdown();
                button = drone.findAndWait(By.cssSelector("span.folder-file"));
                break;
        }
        button.click();
        return getNewFolderPage(drone);
    }

    /**
     * Action of selecting Create document from template.
     * 
     * @return {@link DocumentLibraryPage}
     * @throws TimeoutException
     */
    public DocumentLibraryPage selectCreateContentFromTemplate()
    {

        WebElement button;
        try
        {
            selectCreateContentDropdown();
            button = drone.findAndWait(By.xpath("//span[text()='Create document from template']/parent::a"));
            button.click();

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create document from template.", exception);
        }
        return new DocumentLibraryPage(drone);
    }

    /**
     * Action of selecting Create folder from template.
     * 
     * @return {@link NewFolderPage}
     * @throws TimeoutException
     */
    public NewFolderPage selectCreateFolderFromTemplate()
    {

        WebElement button;

        try
        {
            selectCreateContentDropdown();
            button = drone.findAndWait(By.xpath("//span[text()='Create folder from template']/parent::a"));
            button.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create folder from template.", exception);
        }

        return new NewFolderPage(drone);
    }

    /**
     * Action of selecting Create document from template.
     * 
     * @return {@link DocumentLibraryPage}
     * @throws TimeoutException
     */
    public HtmlPage selectCreateContentFromTemplateHover()
    {

        WebElement button;
        try
        {
            selectCreateContentDropdown();

            button = drone.findAndWait(By.xpath("//span[text()='Create document from template']/parent::a"));
            drone.mouseOver(button);
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create document from template.", exception);
        }

    }

    /**
     * Action of selecting Create folder from template.
     * 
     * @return {@link DocumentLibraryPage}
     * @throws TimeoutException
     */
    public HtmlPage selectCreateFolderFromTemplateHover()
    {

        WebElement button;
        try
        {
            selectCreateContentDropdown();

            button = drone.findAndWait(By.xpath("//span[text()='Create folder from template']/parent::a"));
            drone.mouseOver(button);
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create folder from template.", exception);
        }
    }

    /**
     * Mimics the action of selecting Create Plain Text.
     * 
     * @return {@link SharePage}
     * @throws Exception
     */
    public HtmlPage selectCreateContent(ContentType content)
    {
        if (alfrescoVersion.isCloud())
        {
            switch (content)
            {
                case GOOGLEDOCS:
                case GOOGLEPRESENTATION:
                case GOOGLESPREADSHEET:
                    break;
                case PLAINTEXT:
                case HTML:
                case XML:
                default:
                    throw new UnsupportedOperationException("Create Plain Text not Available for Cloud");
            }
        }
        try
        {
            drone.findAndWait(CREATE_CONTENT_BUTTON).click();
            drone.findAndWait(content.getContentLocator()).click();

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create Plain Text Link.", exception);
        }
        return content.getContentCreationPage(drone);
    }

    /**
     * Mimics the action of Selected Items.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage clickSelectedItems()
    {
        try
        {
            WebElement selectedItemsElement = drone.findAndWait(SELECTED_ITEMS);

            if (selectedItemsElement.isEnabled())
            {
                selectedItemsElement.click();
                return FactorySharePage.resolvePage(drone);
            }
            throw new PageException("Selected Items Button found, but is not enabled please select one or more item");
        }
        catch (TimeoutException e)
        {
            logger.error("Selected Item not available : " + SELECTED_ITEMS.toString());
            throw new PageException("Not able to find the Selected Items Button.", e);
        }
    }

    /**
     * @return true is Selected Item Menu Visible, else false.
     */
    public boolean isSelectedItemMenuVisible()
    {
        try
        {
            return drone.findAndWait(SELECTED_ITEMS_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * @return true is Selected Item Menu Visible and all expected actions are available for selected Folder, else return false.
     */
    public boolean isSelectedItemMenuCorrectForFolder()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                if (AlfrescoVersion.Enterprise42.equals(alfrescoVersion) || AlfrescoVersion.Enterprise43.equals(alfrescoVersion))
                {
                    drone.findAndWait(DOWNLOAD_AS_ZIP).isDisplayed();
                }
                drone.findAndWait(COPY_TO).isDisplayed();
                drone.findAndWait(MOVE_TO).isDisplayed();
                drone.findAndWait(DELETE).isDisplayed();
                drone.findAndWait(DESELECT_ALL).isDisplayed();
                return true;
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to check the Selected Items menu";
            logger.error(exceptionMessage + e.getMessage());
        }
        return false;
    }

    /**
     * @return true is Selected Item Menu Visible and all expected actions are available for selected Document, else return false.
     */
    public boolean isSelectedItemMenuCorrectForDocument()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                if (AlfrescoVersion.Enterprise42.equals(alfrescoVersion) || AlfrescoVersion.Enterprise43.equals(alfrescoVersion))
                {
                    drone.findAndWait(DOWNLOAD_AS_ZIP).isDisplayed();
                }
                drone.findAndWait(COPY_TO).isDisplayed();
                drone.findAndWait(MOVE_TO).isDisplayed();
                drone.findAndWait(DELETE).isDisplayed();
                drone.findAndWait(DESELECT_ALL).isDisplayed();
                drone.findAndWait(START_WORKFLOW).isDisplayed();
                return true;
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to check the Selected Items menu";
            logger.error(exceptionMessage + e.getMessage());
        }
        return false;
    }

    /**
     * Mimics the action select download as Zip from selected Item.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectDownloadAsZip()
    {
        if (AlfrescoVersion.Enterprise41.equals(alfrescoVersion) || alfrescoVersion.isCloud())
        {
            throw new UnsupportedOperationException("Download as Zip option is not available on this version " + alfrescoVersion.toString());
        }
        clickSelectedItems();
        try
        {
            if (isSelectedItemMenuVisible())
            {
                drone.findAndWait(DOWNLOAD_AS_ZIP).click();
                return new DocumentLibraryPage(drone);
            }
            else
            {
                throw new PageException("Selected Items menu not visible please click selected items before download as zip");
            }
        }
        catch (TimeoutException e)
        {
            String expectionMessage = "Not able to find the download as zip Link";
            logger.error(expectionMessage, e);
            throw new PageException(expectionMessage);
        }
    }

    /**
         *
         */
    private void clickSelectDropDown()
    {
        try
        {
            drone.findAndWait(SELECT_DROPDOWN).click();
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the Select Dropdown";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * @return true is Select Menu Visible, else false.
     */
    public boolean isSelectMenuVisible()
    {
        try
        {
            return drone.findAndWait(SELECT_DROPDOWN_MENU).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Mimics the action select All select dropdown.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectAll()
    {
        try
        {
            clickSelectDropDown();
            if (isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_ALL).click();
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * Mimics the action select None select dropdown.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectNone()
    {
        try
        {
            clickSelectDropDown();
            if (isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_NONE).click();
                return FactorySharePage.resolvePage(drone);
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Select None not available : " + SELECT_NONE.toString(), e);
        }
        throw new PageException("Select dropdown menu not visible");
    }

    /**
     * Mimics the action select "Sync to Cloud" from selected Item.
     * Assumes Cloud sync is already set-up
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectSyncToCloud()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(SYNC_TO_CLOUD);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                if (isSignUpDialogVisible())
                {
                    return new CloudSignInPage(getDrone());
                }
                else
                {
                    return new DestinationAndAssigneePage(getDrone());
                }
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Sync to Cloud\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage);
        }
    }

    /**
     * Mimics the  select "Sync to Cloud" button from Navigation bar.
     * It displays when have boths files and folders selected
     * Assumes Cloud sync is already set-up
     *
     * @return {@link DestinationAndAssigneePage}
     */
    public HtmlPage selectSyncToCloudFromNav()
    {

        try
        {
            {
                WebElement element = drone.findAndWait(SYNC_TO_CLOUD_BUTTON);
                String id = element.getAttribute("id");
                element.click();
                if (isSignUpDialogVisible())
                {
                    return new CloudSignInPage(getDrone());
                }
                else
                {
                    return new DestinationAndAssigneePage(getDrone());
                }
            }

        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Sync to Cloud\" button";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage);
        }
    }

    /**
     * Mimics the action select All select dropdown.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectFolders()
    {
        try
        {
            clickSelectDropDown();
            if (isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_FOLDERS).click();
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * Mimics the action select All select dropdown.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectDocuments()
    {
        try
        {
            clickSelectDropDown();
            if (isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_DOCUMENTS).click();
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * Mimics the action select All select dropdown.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectInvert()
    {
        try
        {
            clickSelectDropDown();
            if (isSelectMenuVisible())
            {
                drone.findAndWait(SELECT_INVERT_SELECTION).click();
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find select All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * Returns true if Sign In To Alfresco Cloud popup opens (User haven't set up CloudSync)
     * 
     * @return boolean
     */
    public boolean isSignUpDialogVisible()
    {
        RenderTime time = new RenderTime(maxPageLoadingTime);
        time.start();
        try
        {
            while (true)
            {
                try
                {
                    return drone.find(By.cssSelector("form.cloud-auth-form")).isDisplayed();
                }
                catch (NoSuchElementException e)
                {
                    try
                    {
                        return !drone.find(By.cssSelector("div[id$='default-cloud-folder-title']")).isDisplayed();
                    }
                    catch (NoSuchElementException nse)
                    {
                        time.end();
                        continue;
                    }
                }
            }
        }
        catch (PageRenderTimeException prte)
        {

        }
        return false;
    }

    /**
     * New folder page pop up object.
     * 
     * @param drone WebDrone browser client
     * @param repositoryBrowsing <tt>true</tt> if we are creating a folder in the Repository browser
     * @return NewFolderPage page object response
     */
    public NewFolderPage getNewFolderPage(WebDrone drone)
    {
        // Verify if it is right page, and then create the page.
        try
        {
            WebElement element = drone.findAndWait(By.cssSelector("div[id$='default-createFolder-dialog']"));
            if (element.isDisplayed())
            {
                return new NewFolderPage(drone);
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException(CREATE_FOLDER_ERROR_MESSAGE, te);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(FILE_UPLOAD_ERROR_MESSAGE, e);
        }
        throw new PageException(CREATE_FOLDER_ERROR_MESSAGE);
    }

    /**
     * Mimics the action select "Request Sync" from selected Item.
     * Assumes Cloud sync is already set-up
     * 
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectRequestSync()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(REQUEST_SYNC);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new DocumentLibraryPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Request Sync\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on delete item of selected items drop down.
     * 
     * @return
     */
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(DELETE);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new ConfirmDeletePage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Delete\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    public void clickSelectedItemsButton(){
        clickSelectedItems();
    }

    public Boolean isDeleteActionForIncompleteWorkflowDocumentPresent()
    {
        try
        {
            boolean isDeleteAction = drone.find(DELETE).isDisplayed();
            return isDeleteAction;
        }
        catch(NoSuchElementException ex)
        {
            return false;
        }
    }

    /**
     * Click on copy to item of selected items drop down.
     * 
     * @return
     */
    public CopyOrMoveContentPage selectCopyTo()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(COPY_TO);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new CopyOrMoveContentPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Copy To\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on Move to item of selected items drop down.
     * 
     * @return
     */
    public CopyOrMoveContentPage selectMoveTo()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(MOVE_TO);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new CopyOrMoveContentPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Move To\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Click on DeselectAll item of selected items drop down.
     * 
     * @return
     */
    public HtmlPage selectDesellectAll()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(DESELECT_ALL);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"Deselect All\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    /**
     * Mimics the action select All select dropdown.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage deselectAll()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                drone.findAndWait(DESELECT_ALL).click();
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageException("Select dropdown menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find deselect All option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage);
        }
    }

    /**
     * Click on Start Work Flow item of selected items drop down.
     * 
     * @return StartWorkFlowPage
     */
    public StartWorkFlowPage selectStartWorkFlow()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = drone.findAndWait(START_WORKFLOW);
                String id = element.getAttribute("id");
                element.click();
                drone.waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return new StartWorkFlowPage(drone);
            }
            else
            {
                throw new PageOperationException("Selected Items menu not visible");
            }
        }
        catch (TimeoutException e)
        {
            String exceptionMessage = "Not able to find the \"StartWorkFlow\" Link";
            logger.error(exceptionMessage, e);
            throw new PageOperationException(exceptionMessage, e);
        }
    }

    private void clickOptionDropDown()
    {
        By optionButton = By.cssSelector("button[id$='default-options-button-button']");
        WebElement btn = drone.findAndWait(optionButton);
        drone.waitUntilElementClickable(optionButton, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        btn.click();
    }

    /**
     * Select the option drop down, introduced in
     * Alfresco enterprise 4.2 and clicks on the button in
     * the dropdown.
     * 
     * @param By selector location of button in dropdown to select
     */
    private void selectItemInOptionsDropDown(By button)
    {
        clickOptionDropDown();
        drone.waitForElement(By.cssSelector("div[id$='default-options-menu']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.waitUntilElementClickable(button, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        drone.executeJavaScript("arguments[0].click();", drone.findAndWait(button));
    }

    /**
     * Selects the Detailed View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectDetailedView()
    {
        try
        {
            switch (alfrescoVersion)
            {
                case Enterprise41:
                    drone.findAndWait(By.cssSelector("button[title='Detailed View']")).click();
                    break;

                case Cloud:
                    drone.findAndWait(By.cssSelector("button[id$='default-detailedView-button']")).click();
                    break;

                default:
                    selectItemInOptionsDropDown(By.cssSelector("span.view.detailed"));
                    break;
            }
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css.", e);
        }
    }

    /**
     * Selects the Filmstrip View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectFilmstripView()
    {
        String viewType = "span.view.filmstrip";
        return selectViewType(viewType);
    }

    /**
     * Selects the Filmstrip View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectAudioView()
    {
        String viewType = "span.view.audio";
        return selectViewType(viewType);
    }

    /**
     * Selects the Filmstrip View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectMediaView()
    {
        return selectViewType("span.view.media_table");
    }

    /**
     * @param viewType
     * @return
     * @throws PageOperationException
     */
    private HtmlPage selectViewType(String viewType) throws PageOperationException
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(viewType));
            return drone.getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to select film strip view");
    }

    /**
     * Selects the Simple View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectSimpleView()
    {
        try
        {
            switch (alfrescoVersion)
            {
                case Enterprise41:
                    drone.findAndWait(By.cssSelector("button[title='Simple View']")).click();
                    break;
                case Cloud:
                    drone.findAndWait(By.cssSelector("button[id$='default-simpleView-button']")).click();
                    break;
                default:
                    selectItemInOptionsDropDown(By.cssSelector("span.view.simple"));
                    break;
            }
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css.", e);
        }
    }

    /**
     * Selects the Table View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectTableView()
    {
        try
        {
            switch (alfrescoVersion)
            {
                case Enterprise41:
                    drone.findAndWait(By.cssSelector("button[title='Table View']")).click();
                    break;
                case Cloud:
                    drone.findAndWait(By.cssSelector("button[id$='default-tableView-button']")).click();
                    break;
                default:
                    selectItemInOptionsDropDown(By.cssSelector("span.view.table"));
                    break;
            }
            return drone.getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
            throw new PageException("Exceeded the time to find css.", e);
        }
    }

    /**
     * Mimics the action of selecting the Hide Folders in Option Menu.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectHideFolders()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(".hideFolders"));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find css.", te);
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Hide Folder Option");
    }

    /**
     * Mimics the action of selecting the Show Folders in Option Menu.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectShowFolders()
    {
        try
        {
            if (AlfrescoVersion.Enterprise41.equals(alfrescoVersion))
            {
                drone.find(By.cssSelector("button[id$='howFolders-button-button']")).click();
            }
            else
            {
                selectItemInOptionsDropDown(By.cssSelector(".showFolders"));
            }
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find css.", te);
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Show Folder Option");
    }

    /**
     * Closes the Option Menu if it is opened.
     */
    private void closeOptionMenu()
    {
        if (isOptionMenuVisible())
        {
            try
            {
                WebElement btn = drone.find(By.cssSelector("button[id$='default-options-button-button']"));
                HtmlElement dropdownButton = new HtmlElement(btn, drone);
                dropdownButton.click();
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Not able to find the Option menu");
            }
        }
    }

    /**
     * Opens the Option Menu if it is opened.
     */
    private void openOptionMenu()
    {
        if (!isOptionMenuVisible())
        {
            try
            {
                WebElement btn = drone.find(By.cssSelector("button[id$='default-options-button-button']"));
                HtmlElement dropdownButton = new HtmlElement(btn, drone);
                dropdownButton.click();
            }
            catch (NoSuchElementException e)
            {
                throw new PageOperationException("Not able to find the Option menu");
            }
        }
    }

    private boolean isOptionMenuVisible()
    {
        try
        {
            return drone.find(By.cssSelector("div[id$='_default-options-menu']")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Mimics the action of selecting the Hide Breadcrumb in Option Menu.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectHideBreadcrump()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(".hidePath"));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find css.", te);
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Hide Breadcrump Option");
    }

    /**
     * Mimics the action of selecting the Show Folders in Option Menu.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectShowBreadcrump()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(".showPath"));
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find css.", te);
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Show Breadcrumb Option");
    }

    /**
     * Method to check the visibility of navigation bar.
     * 
     * @return true if navigation bar visible else false.
     */
    public boolean isNavigationBarVisible()
    {
        try
        {
            return drone.find(By.cssSelector("div[id$='_default-navBar']")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Mimcis the action of click the folder up button in Navigation bar.
     * 
     * @return {@link HtmlPage}
     */
    public HtmlPage clickFolderUp()
    {
        WebElement folderUpElement = null;
        if (isNavigationBarVisible())
        {
            try
            {
                folderUpElement = drone.findAndWait(By.cssSelector("button[id$='folderUp-button-button']"));
            }
            catch (TimeoutException ex)
            {
                logger.error("Exceeded time to find folderUpElement", ex);
                throw new PageOperationException("FolderUp element didn't found on page.");
            }
            if (folderUpElement.isEnabled())
            {
                folderUpElement.click();
                return FactorySharePage.resolvePage(drone);
            }
            else
            {
                throw new PageOperationException("You may be in the root folder, please check path and use folder up whenever required.");
            }
        }
        else
        {
            throw new PageOperationException("Navigation might be hidden, please click show breadcrumb from option menu.");
        }
    }

    /**
     * Returns the {@link List} for Folders as {@link ShareLink}.
     * 
     * @return {@link List} of {@link ShareLink} Folders.
     */
    public List<ShareLink> getFoldersInNavBar()
    {
        if (isNavigationBarVisible())
        {
            List<ShareLink> folderLinks = new ArrayList<ShareLink>();
            try
            {
                List<WebElement> folders = drone.findAll(By.cssSelector("a.folder"));
                folders.add(drone.find(By.cssSelector(".label>a")));
                for (WebElement folder : folders)
                {
                    folderLinks.add(new ShareLink(folder, drone));
                }
            }
            catch (NoSuchElementException e)
            {
            }
            return folderLinks;
        }
        else
        {
            throw new PageOperationException("Navigation might be hidden, please click show breadcrumb from option menu.");
        }
    }

    /**
     * Mimics the action selecting the folder in the navigation bar.
     * 
     * @param folderName - Folder Name to be selected in navigation bar.
     * @return {@link HtmlPage}
     */
    public HtmlPage selectFolderInNavBar(String folderName)
    {
        if (isNavigationBarVisible())
        {
            List<ShareLink> folderLinks = getFoldersInNavBar();
            for (ShareLink shareLink : folderLinks)
            {
                if (shareLink.getDescription().trim().equals(folderName))
                {
                    return shareLink.click();
                }
            }
            throw new PageOperationException("Not able to find the folder named: " + folderName);
        }
        else
        {
            throw new PageOperationException("Navigation might be hidden, please click show breadcrump from option menu.");
        }
    }

    /**
     * Selects the Gallery View of the Document Library.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectGalleryView()
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector("span.view.gallery"));
            return drone.getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find css.", nse);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageException("Unable to select the Gallery view.");
    }

    /**
     * This method is used to find the view type.
     * 
     * @param By selector location of button in dropdown to select
     */
    public ViewType getViewType()
    {
        // Note: This is temporary fix to find out the view type.
        String text = (String) drone.executeJavaScript("return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').options.viewRendererName;");
        ViewType type = ViewType.getViewType(text);
        return type;
    }

    private boolean isDefaultViewVisible(By view)
    {
        boolean visible = false;
        try
        {
            clickOptionDropDown();
            if (drone.findAndWait(By.cssSelector("div[id$='default-options-menu']")).isDisplayed())
            {
                visible = drone.find(view).isDisplayed();
            }
        }
        catch (TimeoutException e)
        {
        }
        catch (NoSuchElementException e)
        {
        }

        closeOptionMenu();

        return visible;
    }

    /**
     * Returns true if the Set current view to default is visible
     * 
     * @return
     */
    public boolean isSetDefaultViewVisible()
    {
        return isDefaultViewVisible(SET_DEFAULT_VIEW);
    }

    /**
     * Returns true if the Remove current default view is present
     * 
     * @return
     */
    public boolean isRemoveDefaultViewVisible()
    {
        return isDefaultViewVisible(REMOVE_DEFAULT_VIEW);
    }

    /**
     * Clicks on the 'Set "<current view>" as default for this folder' button in the options menu.
     * 
     * @return
     */
    public HtmlPage selectSetCurrentViewToDefault()
    {
        try
        {
            selectItemInOptionsDropDown(SET_DEFAULT_VIEW);
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find css.", te);
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Set Default View Option");
    }

    /**
     * Clicks on the 'Remove "<current view>" as default for this folder' button in the options menu.
     * 
     * @return
     */
    public HtmlPage selectRemoveCurrentViewFromDefault()
    {
        try
        {
            selectItemInOptionsDropDown(REMOVE_DEFAULT_VIEW);
            return FactorySharePage.resolvePage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded the time to find css.", te);
        }
        closeOptionMenu();
        throw new PageOperationException("Not able to find the Remove Default View Option");
    }

    /**
     * Click on the Sort Field drop down to expand or collapse the menu.
     */
    private void clickSortDropDown()
    {
        try
        {
            drone.find(SORT_DROPDOWN).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find css.", nse);
        }
    }

    /**
     * Select the sort drop down and clicks on the button in
     * the dropdown.
     * 
     * @param sortField enum value of the button in dropdown to select
     * @return DocumentLibraryPage
     */
    public HtmlPage selectSortFieldFromDropDown(SortField sortField)
    {
        try
        {
            clickSortDropDown();

            WebElement dropdown = drone.findAndWait(SORT_FIELD);
            if (dropdown.isDisplayed())
            {
                drone.find(sortField.getSortLocator()).click();
                return FactorySharePage.resolvePage(drone);
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageException("Unable to select the sort field " + sortField);
    }

    /**
     * Get the current sort field.
     * 
     * @return The SortField enum.
     */
    public SortField getCurrentSortField()
    {
        try
        {
            WebElement button = drone.findAndWait(CURRENT_SORT_FIELD);

            return SortField.getEnum(button.getText().split(" ")[0]);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageException("Unable to find the current sort field ");
    }

    /**
     * Determines the sort direction.
     * 
     * @return <code>true</code> if the page is sorted ascending, otherwise <code>false</code>
     */
    public boolean isSortAscending()
    {
        try
        {
            String classValue = drone.find(SORT_DIRECTION).getAttribute("class");

            if (classValue == null)
            {
                return true;
            }

            // if class sort-descending exists then ascending is true.
            return !classValue.contains("sort-descending");
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Unable to find css.", nse);
        }
        throw new PageException("Unable to find the sort button.");
    }

    /**
     * Click the Sort Direction button.
     */
    private void clickSortOrder()
    {
        try
        {
            drone.find(SORT_DIRECTION_BUTTON).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Unable to find the sort button.", nse);
        }
    }

    /**
     * Set the sort direction to ascending.
     * 
     * @return The refreshed HtmlPage.
     */
    public HtmlPage sortAscending(boolean isAscending)
    {
        if (isSortAscending() != isAscending)
        {
            clickSortOrder();
        }

        return FactorySharePage.resolvePage(drone);
    }

    /**
     * Finds the ZoomStyle of the doclib.
     * 
     * @param zoomThumbnail
     * @return {@link ZoomStyle}
     */
    public ZoomStyle getZoomStyle()
    {
        int size;
        try
        {
            WebElement zoomThumbnail = findZoomControl();
            String style = zoomThumbnail.getAttribute("style");
            size = Integer.valueOf(style.substring(style.indexOf(" ") + 1, style.indexOf("p")));
        }
        catch (NumberFormatException e)
        {
            throw new PageOperationException("Unable to convert String into int:", e);
        }

        try
        {
            return ZoomStyle.getZoomStyle(size);
        }
        catch (IllegalArgumentException e)
        {
            throw new PageOperationException("Unable to find the ZoomStyle for the zoomSize:" + size, e);
        }
    }

    /**
     * This methods does the zoom in and zoom out on Gallery view.
     * 
     * @param zoomStyle
     * @return HtmlPage
     */
    public HtmlPage selectZoom(ZoomStyle zoomStyle)
    {
        if (!getViewType().equals(ViewType.GALLERY_VIEW))
        {
            throw new UnsupportedOperationException("Zoom Control is available in GalleryView only.");
        }

        if (zoomStyle == null)
        {
            throw new IllegalArgumentException("ZoomStyle value is required");
        }

        WebElement zoomThumbnail = findZoomControl();
        ZoomStyle actualZoomStyle = getZoomStyle();

        if (zoomStyle.equals(actualZoomStyle))
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("The selected zoom style is already in place.");
            }
        }
        else
        {
            drone.dragAndDrop(zoomThumbnail, (zoomStyle.getSize() - actualZoomStyle.getSize()), 0);
        }

        return FactorySharePage.resolvePage(drone);
    }

    /**
     * @return WebElement
     */
    private WebElement findZoomControl()
    {
        try
        {
            return drone.findAndWait(ZOOM_CONTROL_BAR_THUMBNAIL_CSS);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the Zoom Control bar css", e);
        }

        throw new PageOperationException("Error in Selecting zoom.");
    }

    /**
     * Returns true if the Zoom Control is visible
     * 
     * @return boolean
     */
    public boolean isZoomControlVisible()
    {
        try
        {
            return drone.find(ZOOM_CONTROL_BAR_THUMBNAIL_CSS).isDisplayed();
        }
        catch (NoSuchElementException ne)
        {
        }

        return false;
    }

    /**
     * Checks if the file upload button is enabled
     * 
     * @return
     */
    public boolean isFileUploadEnabled()
    {
        try
        {
            return drone.find(By.cssSelector(FILE_UPLOAD_BUTTON)).isEnabled();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the file upload button is visible
     * 
     * @return
     */
    public boolean isFileUploadVisible()
    {
        try
        {
            return drone.find(By.cssSelector(FILE_UPLOAD_BUTTON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the create content dropdown is enabled
     * 
     * @return
     */
    public boolean isCreateContentEnabled()
    {
        try
        {
            return drone.find(CREATE_CONTENT_BUTTON).isEnabled();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the folder up button is visible
     * 
     * @return
     */
    public boolean isFolderUpVisible()
    {
        try
        {
            return drone.find(FOLDER_UP_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the create content dropdown is visible
     * 
     * @return
     */
    public boolean isCreateContentVisible()
    {
        try
        {
            return drone.find(CREATE_CONTENT_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * @return true if Select button Visible, else false.
     */
    public boolean isSelectVisible()
    {
        try
        {
            return drone.find(SELECT_DROPDOWN).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * @return true if Selected Item button Visible, else false.
     */
    public boolean isSelectedItemVisible()
    {
        try
        {
            return drone.find(SELECTED_ITEMS).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * @return true if Selected Item button Enabled, else false.
     */
    public boolean isSelectedItemEnabled()
    {
        try
        {
            return drone.find(SELECTED_ITEMS).isEnabled();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * @return true if the crumb trail is Visible, else false.
     */
    public boolean isCrumbTrailVisible()
    {
        try
        {
            return drone.find(CRUMB_TRAIL).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if Options menu item is displayed.
     * 
     * @return true if visible
     */
    public boolean isOptionPresent(LibraryOption option)
    {
        try
        {
            openOptionMenu();
            return drone.find(By.cssSelector(option.getOption())).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Option is not present Css value is :", nse);
            }
        }
        finally
        {
            closeOptionMenu();
        }
        return false;
    }

    /**
     * @return true if the New Folder button is Visible, else false.
     */
    public boolean isNewFolderVisible()
    {
        try
        {
            return drone.find(CREATE_NEW_FOLDER).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * The method to select RSS Feed option from Options drop-down
     * 
     * @return RssFeedPage
     */
    public RssFeedPage selectRssFeed(String username, String password, String siteName)
    {
        try
        {
            String currentDocLibUrl = drone.getCurrentUrl();
            String protocolVar = PageUtils.getProtocol(currentDocLibUrl);
            String shareUrlVar = PageUtils.getShareUrl(currentDocLibUrl);
            String rssUrl = String.format("%s%s:%s@%s/feedservice/components/documentlibrary/feed/all/site/%s/documentLibrary/?filter=path&format=rss",
                    protocolVar, username, password, shareUrlVar, siteName);
            drone.navigateTo(rssUrl);
            return new RssFeedPage(drone);
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Exceeded the time to find css.", nse);
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded the time to find css.", e);
        }
        throw new PageOperationException("Not able to select RSS Feed option");
    }

    /**
     * Selecting the create a folder link when the parent folder is empty
     * 
     * @return {@link NewFolderPage} page response
     */
    public NewFolderPage selectCreateAFolder()
    {
        WebElement button = drone.findAndWait(By.cssSelector(CREATE_A_FOLDER_LINK));
        button.click();
        return getNewFolderPage(drone);
    }

    /**
     * Selects the <View Details> link on the select data row on DocumentLibrary
     * Page. Only available for content type = Folder.
     * 
     * @return {@link DocumentLibraryPage} response
     */
    public String getBreadCrumbsPath()
    {
        try
        {
            return drone.findAndWait(CRUMB_TRAIL).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find " + CRUMB_TRAIL, e);
        }
        throw new PageException("Unable to find " + CRUMB_TRAIL);
    }

    /**
     * Method checks if content type is present at the Options drop-down
     * 
     * @param content Content type to check
     * @return
     */
    public boolean isCreateContentPresent(ContentType content)
    {

        try
        {
            return drone.findAndWait(content.getContentLocator()).isDisplayed();

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the element");
            return false;
        }
    }

    /**
     * Method checks if (Create) Folder is present at the Options drop-down
     * 
     * @return true if visible
     */
    public boolean isCreateNewFolderPresent()
    {
        try
        {

            if (AlfrescoVersion.Enterprise41.equals(drone.getProperties().getVersion()))
            {
                return drone.find(By.cssSelector(CREATE_NEW_FOLDER_BUTTON)).isDisplayed();

            }
            else
            {
                return drone.find(By.cssSelector("span.folder-file")).isDisplayed();
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("The element isn't present");
            return false;
        }
    }

    /**
     * Method checks if Create Folder/Document from Template are present at the Options drop-down
     * 
     * @param folder if true, checks for Folder template; false - Document template
     * @return
     */
    public boolean isCreateFromTemplatePresent(boolean folder)
    {
        try
        {

            if (folder)
            {
                return drone.findAndWait(By.xpath(CREATE_FOLDER_FROM_TEMPLATE)).isDisplayed();

            }
            else
            {
                return drone.findAndWait(By.xpath(CREATE_DOCUMENT_FROM_TEMPLATE)).isDisplayed();
            }

        }
        catch (NoSuchElementException nse)
        {
            logger.error("The element isn't present at a drop-down");
            return false;
        }
    }

    /**
     * Checks if Options menu item is displayed.
     * 
     * @return true if visible
     */
    public boolean isSelectedItemsOptionPresent(SelectedItemsOptions option)
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {

                return drone.find(By.cssSelector(option.getOption())).isDisplayed();
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Option is not present. ", nse);
            }
        }
        return false;
    }

    /**
     * Method gets String name of an item link in a breadcrumbs path
     * 
     * @return String name
     */
    public String getCrumbsElementDetailsLinkName()
    {
        try
        {
            return drone.find(BREAD_CRUMBS_PARENT_SPAN).getText();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find " + BREAD_CRUMBS_PARENT_SPAN, e);
        }
        throw new PageException("Unable to find " + BREAD_CRUMBS_PARENT_SPAN);
    }

    /**
     * Click on the parent item link of the breadcrumbs path
     * 
     * @return
     */
    public HtmlPage clickCrumbsParentLinkName()
    {
        try
        {
            drone.findAndWait(BREAD_CRUMBS_PARENT).click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException e)
        {
            logger.error(BREAD_CRUMBS_PARENT + " isn't present");
            throw new PageException("Not able to find " + BREAD_CRUMBS_PARENT, e);
        }
    }

    /**
     * Click on the last item link of the breadcrumbs path
     * 
     * @return {@link DetailsPage page of the item}
     */
    public HtmlPage clickCrumbsElementDetailsLinkName()
    {
        try
        {
            drone.findAndWait(BREAD_CRUMBS_PARENT_SPAN).click();
            return FactorySharePage.resolvePage(drone);

        }
        catch (TimeoutException e)
        {
            logger.error(BREAD_CRUMBS_PARENT_SPAN + " isn't present");
            throw new PageException("Not able to find " + BREAD_CRUMBS_PARENT_SPAN, e);
        }
    }


}