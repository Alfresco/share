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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.enums.ZoomStyle;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page relating to the document library
 * sub navigation bar that appears on the document library site page.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
public class DocumentLibraryNavigation extends PageElement
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
    private static final By REQUEST_SYNC = By.cssSelector(".onActionCloudSyncRequest");
    private static final String FILE_UPLOAD_ERROR_MESSAGE = "Unable to create file upload page";
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

    private Log logger = LogFactory.getLog(this.getClass());

    /**
     * @return <tt>true</tt> if the <b>Upload</b> link is available
     * @since 1.5.1
     */
    public boolean hasFileUploadLink()
    {
        try
        {
            By criteria = By.cssSelector(FILE_UPLOAD_BUTTON);
            return driver.findElement(criteria).isEnabled();
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
        WebElement button = driver.findElement(By.cssSelector(FILE_UPLOAD_BUTTON));
        button.click();
        return getFileUpload(driver);
    }

    /**
     * Get file upload page pop up object.
     * 
     * @param driver WebDriver browser client
     * @return SharePage page object response
     */
    public HtmlPage getFileUpload(WebDriver driver)
    {

        // Verify if it is really file upload page, and then create the page.
        try
        {
            WebElement element = driver.findElement(By.cssSelector("img.title-folder"));
            if (element.isDisplayed())
            {
                return factoryPage.instantiatePage(driver, UploadFilePage.class);
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
            return driver.findElement(criteria).isEnabled();
        }
        catch (NoSuchElementException e)
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
            WebElement createContentElement = driver.findElement(CREATE_CONTENT_BUTTON);
            if (createContentElement.isEnabled())
            {
                createContentElement.click();
                return getCurrentPage();
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
        selectCreateContentDropdown();
        WebElement button = driver.findElement(By.cssSelector("span.folder-file"));
        button.click();
        return getNewFolderPage(driver);
    }

    /**
     * Action of selecting Create document from template.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage selectCreateContentFromTemplate()
    {

        WebElement button;
        try
        {
            selectCreateContentDropdown();
            button = driver.findElement(By.xpath("//span[text()='Create document from template']/parent::a"));
            button.click();

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create document from template.", exception);
        }
        return factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
    }

    /**
     * Action of selecting Create folder from template.
     * 
     * @return {@link NewFolderPage}
     */
    public NewFolderPage selectCreateFolderFromTemplate()
    {

        WebElement button;

        try
        {
            selectCreateContentDropdown();
            button = driver.findElement(By.xpath("//span[text()='Create folder from template']/parent::a"));
            button.click();
        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create folder from template.", exception);
        }

        return factoryPage.instantiatePage(driver, NewFolderPage.class);
    }

    /**
     * Action of selecting Create document from template.
     * 
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectCreateContentFromTemplateHover()
    {

        WebElement button;
        try
        {
            selectCreateContentDropdown();

            button = driver.findElement(By.xpath("//span[text()='Create document from template']/parent::a"));
            mouseOver(button);
            return getCurrentPage();

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
     */
    public HtmlPage selectCreateFolderFromTemplateHover()
    {

        WebElement button;
        try
        {
            selectCreateContentDropdown();

            button = driver.findElement(By.xpath("//span[text()='Create folder from template']/parent::a"));
            mouseOver(button);
            return getCurrentPage();

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
     * @return {@link HtmlPage}
     */
    public HtmlPage selectCreateContent(ContentType content)
    {
//        if (alfrescoVersion.isCloud())
//        {
//            switch (content)
//            {
//                case GOOGLEDOCS:
//                case GOOGLEPRESENTATION:
//                case GOOGLESPREADSHEET:
//                    break;
//                case PLAINTEXT:
//                case HTML:
//                case XML:
//                default:
//                    throw new UnsupportedOperationException("Create Plain Text not Available for Cloud");
//            }
//        }
        try
        {
            driver.findElement(CREATE_CONTENT_BUTTON).click();
            driver.findElement(content.getContentLocator()).click();

        }
        catch (TimeoutException exception)
        {
            logger.error("Not able to find the web element");
            throw new PageException("Unable to fine the Create Plain Text Link.", exception);
        }
        Class<?> proxy = content.getContentCreationPage(driver);
        return (HtmlPage) factoryPage.instantiatePage(driver, proxy);
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
            WebElement selectedItemsElement = driver.findElement(SELECTED_ITEMS);

            if (selectedItemsElement.isEnabled())
            {
                selectedItemsElement.click();
                return getCurrentPage();
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
            return driver.findElement(SELECTED_ITEMS_MENU).isDisplayed();
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
                driver.findElement(DOWNLOAD_AS_ZIP).isDisplayed();
                driver.findElement(COPY_TO).isDisplayed();
                driver.findElement(MOVE_TO).isDisplayed();
                driver.findElement(DELETE).isDisplayed();
                driver.findElement(DESELECT_ALL).isDisplayed();
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
                driver.findElement(DOWNLOAD_AS_ZIP).isDisplayed();
                driver.findElement(COPY_TO).isDisplayed();
                driver.findElement(MOVE_TO).isDisplayed();
                driver.findElement(DELETE).isDisplayed();
                driver.findElement(DESELECT_ALL).isDisplayed();
                driver.findElement(START_WORKFLOW).isDisplayed();
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
        clickSelectedItems();
        try
        {
            if (isSelectedItemMenuVisible())
            {
                driver.findElement(DOWNLOAD_AS_ZIP).click();
                return factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
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
            driver.findElement(SELECT_DROPDOWN).click();
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
            return driver.findElement(SELECT_DROPDOWN_MENU).isDisplayed();
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
                driver.findElement(SELECT_ALL).click();
                return getCurrentPage();
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
                driver.findElement(SELECT_NONE).click();
                return getCurrentPage();
            }
        }
        catch (TimeoutException e)
        {
            logger.error("Select None not available : " + SELECT_NONE.toString(), e);
        }
        throw new PageException("Select dropdown menu not visible");
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
                driver.findElement(SELECT_FOLDERS).click();
                return getCurrentPage();
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
                driver.findElement(SELECT_DOCUMENTS).click();
                return getCurrentPage();
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
                driver.findElement(SELECT_INVERT_SELECTION).click();
                return getCurrentPage();
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
                    return driver.findElement(By.cssSelector("form.cloud-auth-form")).isDisplayed();
                }
                catch (NoSuchElementException e)
                {
                    try
                    {
                        return !driver.findElement(By.cssSelector("div[id$='default-cloud-folder-title']")).isDisplayed();
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
     * @param driver WebDriver browser client
     * @return NewFolderPage page object response
     */
    public NewFolderPage getNewFolderPage(WebDriver driver)
    {
        return factoryPage.instantiatePage(driver, NewFolderPage.class).render();
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
                WebElement element = driver.findElement(REQUEST_SYNC);
                String id = element.getAttribute("id");
                element.click();
                waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return factoryPage.instantiatePage(driver, DocumentLibraryPage.class);
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
     * @return ConfirmDeletePage
     */
    public ConfirmDeletePage selectDelete()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = driver.findElement(DELETE);
                String id = element.getAttribute("id");
                element.click();
                waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return factoryPage.instantiatePage(driver, ConfirmDeletePage.class);
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

    public void clickSelectedItemsButton()
    {
        clickSelectedItems();
    }

    public Boolean isDeleteActionForIncompleteWorkflowDocumentPresent()
    {
        try
        {
            boolean isDeleteAction = driver.findElement(DELETE).isDisplayed();
            return isDeleteAction;
        }
        catch (NoSuchElementException ex)
        {
            return false;
        }
    }

    /**
     * Click on copy to item of selected items drop down.
     * 
     * @return ConfirmDeletePage
     */
    public HtmlPage selectCopyTo()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = driver.findElement(COPY_TO);
                String id = element.getAttribute("id");
                element.click();
                waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return getCurrentPage();
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
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectMoveTo()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = driver.findElement(MOVE_TO);
                String id = element.getAttribute("id");
                element.click();
                waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return getCurrentPage().render();
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
     * @return HtmlPage
     */
    public HtmlPage selectDesellectAll()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = driver.findElement(DESELECT_ALL);
                String id = element.getAttribute("id");
                element.click();
                waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return getCurrentPage();
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
                driver.findElement(DESELECT_ALL).click();
                return getCurrentPage();
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
    public HtmlPage selectStartWorkFlow()
    {
        try
        {
            clickSelectedItems();
            if (isSelectedItemMenuVisible())
            {
                WebElement element = driver.findElement(START_WORKFLOW);
                String id = element.getAttribute("id");
                element.click();
                waitUntilElementDeletedFromDom(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                return getCurrentPage();
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
        WebElement btn = driver.findElement(optionButton);
        waitUntilElementClickable(optionButton, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        btn.click();
    }

    /**
     * Select the option drop down, introduced in
     * Alfresco enterprise 4.2 and clicks on the button in
     * the dropdown.
     * 
     * @param button By selector location of button in dropdown to select
     */
    private void selectItemInOptionsDropDown(By button)
    {
        clickOptionDropDown();
        waitForElement(By.cssSelector("div[id$='default-options-menu']"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        waitUntilElementClickable(button, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        executeJavaScript("arguments[0].click();", driver.findElement(button));
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
            selectItemInOptionsDropDown(By.cssSelector("li.detailed > a"));
            return getCurrentPage();
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
     * Selects the Gallery View of the Document Library.
     *
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage selectGalleryView()
    {
        return selectViewType("span.view.gallery");
    }

    /**
     * @param viewType String
     * @return HtmlPage
     * @throws PageOperationException
     */
    private HtmlPage selectViewType(String viewType) throws PageOperationException
    {
        try
        {
            selectItemInOptionsDropDown(By.cssSelector(viewType));
            return getCurrentPage();
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
            selectItemInOptionsDropDown(By.cssSelector("span.view.simple"));
            return getCurrentPage();
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
            selectItemInOptionsDropDown(By.cssSelector("span.view.table"));
            return getCurrentPage();
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
            return getCurrentPage();
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
            selectItemInOptionsDropDown(By.cssSelector(".showFolders"));
            return getCurrentPage();
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
                WebElement dropdownButton = driver.findElement(By.cssSelector("button[id$='default-options-button-button']"));
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
                WebElement dropdownButton = driver.findElement(By.cssSelector("button[id$='default-options-button-button']"));
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
            return driver.findElement(By.cssSelector("div[id$='_default-options-menu']")).isDisplayed();
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
            return getCurrentPage();
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
            return getCurrentPage();
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
            return driver.findElement(By.cssSelector("div[id$='_default-navBar']")).isDisplayed();
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
                folderUpElement = driver.findElement(By.cssSelector("button[id$='folderUp-button-button']"));
            }
            catch (TimeoutException ex)
            {
                logger.error("Exceeded time to find folderUpElement", ex);
                throw new PageOperationException("FolderUp element didn't found on page.");
            }
            if (folderUpElement.isEnabled())
            {
                folderUpElement.click();
                return getCurrentPage();
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
                List<WebElement> folders = driver.findElements(By.cssSelector("a.folder"));
                folders.add(driver.findElement(By.cssSelector(".label>a")));
                for (WebElement folder : folders)
                {
                    folderLinks.add(new ShareLink(folder, driver, factoryPage));
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
     * This method is used to find the view type.
     */
    public ViewType getViewType()
    {
        // Note: This is temporary fix to find out the view type.
        String text = (String) executeJavaScript("return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').options.viewRendererName;");
        ViewType type = ViewType.getViewType(text);
        return type;
    }

    private boolean isDefaultViewVisible(By view)
    {
        boolean visible = false;
        try
        {
            clickOptionDropDown();
            if (driver.findElement(By.cssSelector("div[id$='default-options-menu']")).isDisplayed())
            {
                visible = driver.findElement(view).isDisplayed();
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
     * @return boolean
     */
    public boolean isSetDefaultViewVisible()
    {
        return isDefaultViewVisible(SET_DEFAULT_VIEW);
    }

    /**
     * Returns true if the Remove current default view is present
     * 
     * @return boolean
     */
    public boolean isRemoveDefaultViewVisible()
    {
        return isDefaultViewVisible(REMOVE_DEFAULT_VIEW);
    }

    /**
     * Clicks on the 'Set "<current view>" as default for this folder' button in the options menu.
     * 
     * @return HtmlPage
     */
    public HtmlPage selectSetCurrentViewToDefault()
    {
        try
        {
            selectItemInOptionsDropDown(SET_DEFAULT_VIEW);
            return getCurrentPage();
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
     * @return HtmlPage
     */
    public HtmlPage selectRemoveCurrentViewFromDefault()
    {
        try
        {
            selectItemInOptionsDropDown(REMOVE_DEFAULT_VIEW);
            return getCurrentPage();
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
            driver.findElement(SORT_DROPDOWN).click();
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

            WebElement dropdown = driver.findElement(SORT_FIELD);
            if (dropdown.isDisplayed())
            {
                driver.findElement(sortField.getSortLocator()).click();
                return getCurrentPage();
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
            WebElement button = driver.findElement(CURRENT_SORT_FIELD);

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
            String classValue = driver.findElement(SORT_DIRECTION).getAttribute("class");

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
            driver.findElement(SORT_DIRECTION_BUTTON).click();
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

        return getCurrentPage();
    }

    /**
     * Finds the ZoomStyle of the doclib.
     * 
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
     * @return WebElement
     */
    private WebElement findZoomControl()
    {
        try
        {
            return driver.findElement(ZOOM_CONTROL_BAR_THUMBNAIL_CSS);
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
            return driver.findElement(ZOOM_CONTROL_BAR_THUMBNAIL_CSS).isDisplayed();
        }
        catch (NoSuchElementException ne)
        {
        }

        return false;
    }

    /**
     * Checks if the file upload button is enabled
     * 
     * @return boolean
     */
    public boolean isFileUploadEnabled()
    {
        try
        {
            return driver.findElement(By.cssSelector(FILE_UPLOAD_BUTTON)).isEnabled();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the file upload button is visible
     * 
     * @return boolean
     */
    public boolean isFileUploadVisible()
    {
        try
        {
            return driver.findElement(By.cssSelector(FILE_UPLOAD_BUTTON)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the create content dropdown is enabled
     * 
     * @return boolean
     */
    public boolean isCreateContentEnabled()
    {
        try
        {
            return driver.findElement(CREATE_CONTENT_BUTTON).isEnabled();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the folder up button is visible
     * 
     * @return boolean
     */
    public boolean isFolderUpVisible()
    {
        try
        {
            return driver.findElement(FOLDER_UP_BUTTON).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks if the create content dropdown is visible
     * 
     * @return boolean
     */
    public boolean isCreateContentVisible()
    {
        try
        {
            return driver.findElement(CREATE_CONTENT_BUTTON).isDisplayed();
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
            return driver.findElement(SELECT_DROPDOWN).isDisplayed();
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
            return driver.findElement(SELECTED_ITEMS).isDisplayed();
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
            return driver.findElement(SELECTED_ITEMS).isEnabled();
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
            return driver.findElement(CRUMB_TRAIL).isDisplayed();
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
            return driver.findElement(By.cssSelector(option.getOption())).isDisplayed();
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
            return driver.findElement(CREATE_NEW_FOLDER).isDisplayed();
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
            String currentDocLibUrl = driver.getCurrentUrl();
            String protocolVar = PageUtils.getProtocol(currentDocLibUrl);
            String shareUrlVar = PageUtils.getShareUrl(currentDocLibUrl);
            String rssUrl = String.format("%s%s:%s@%s/feedservice/components/documentlibrary/feed/all/site/%s/documentLibrary/?filter=path&format=rss",
                    protocolVar, username, password, shareUrlVar, siteName);
            driver.navigate().to(rssUrl);
            return factoryPage.instantiatePage(driver, RssFeedPage.class);
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
        WebElement button = driver.findElement(By.cssSelector(CREATE_A_FOLDER_LINK));
        button.click();
        return getNewFolderPage(driver);
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
            return driver.findElement(CRUMB_TRAIL).getText();
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
     * @return boolean
     */
    public boolean isCreateContentPresent(ContentType content)
    {

        try
        {
            return driver.findElement(content.getContentLocator()).isDisplayed();

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
            return driver.findElement(By.cssSelector("span.folder-file")).isDisplayed();
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
     * @return boolean
     */
    public boolean isCreateFromTemplatePresent(boolean folder)
    {
        try
        {

            if (folder)
            {
                return driver.findElement(By.xpath(CREATE_FOLDER_FROM_TEMPLATE)).isDisplayed();

            }
            else
            {
                return driver.findElement(By.xpath(CREATE_DOCUMENT_FROM_TEMPLATE)).isDisplayed();
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

                return driver.findElement(By.cssSelector(option.getOption())).isDisplayed();
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
            return driver.findElement(BREAD_CRUMBS_PARENT_SPAN).getText();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Unable to find BreadCrumb element: " + BREAD_CRUMBS_PARENT_SPAN, e);
        }
        throw new PageException("Unable to find BreadCrumb element: " + BREAD_CRUMBS_PARENT_SPAN);
    }

    /**
     * Click on the parent item link of the breadcrumbs path
     * 
     * @return HtmlPage
     */
    public HtmlPage clickCrumbsParentLinkName()
    {
        try
        {
            driver.findElement(BREAD_CRUMBS_PARENT).click();
            return getCurrentPage();

        }
        catch (NoSuchElementException | StaleElementReferenceException e)
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
            driver.findElement(BREAD_CRUMBS_PARENT_SPAN).click();
            return getCurrentPage();
        }
        catch (NoSuchElementException | StaleElementReferenceException e)
        {
            logger.error(BREAD_CRUMBS_PARENT_SPAN + " isn't present");
            throw new PageException("Not able to find " + BREAD_CRUMBS_PARENT_SPAN, e);
        }
    }

}
