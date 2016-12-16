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
import java.util.Collections;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.Pagination;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Site document library page object, holds all element of the HTML page relating to share's site document library page.
 *
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
public class DocumentLibraryPage extends SitePage
{
    protected static final String JS_DOCUMENT_VIEW_TYPE = "return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').options.viewRendererName;";
    private static final String JS_SCRIPT_CHECK_DOCLIST = "return Alfresco.util.ComponentManager.findFirst('Alfresco.DocumentList').widgets.dataTable._getViewRecords();";
    private static Log logger = LogFactory.getLog(DocumentLibraryPage.class);
    private static final String PAGINATION_BUTTON_NEXT = "a.yui-pg-next";
    private static final String PAGINATION_BUTTON_PREVIOUS = "a.yui-pg-previous";
    public static final String FILES_AND_DOCUMENTS_TABLE_CSS = "table#yuievtautoid-0 > tbody.yui-dt-data > tr";
    private static final String ALL_TAGS_PRESENT_ON_DOC_LIB = "div.filter>ul>li>span.tag>a.tag-link";
    private static final String FILE_UPLOAD_INSTRUCTION = "div.docListInstructionsWithDND";
    private static final String BOTTOM_PAGINATOR_LOCATION = "div[id$='_default-paginatorBottom']";

    private static final By THUMBNAIL_IMAGE = By.cssSelector("td[class$='yui-dt-col-thumbnail'] img");
    private static final By DOCUMENTS_TREE_CSS = By.cssSelector("div.filter.doclib-filter h2");
    private static final By CATEGORIES_TREE_CSS = By.cssSelector("div[class='categoryview filter'] > h2");
    private static final By MY_FAVOURITES = By.cssSelector("span.favourites > a");
    private static final By RECENTLY_MODIFIED = By.cssSelector("span.recentlyModified > a");
    private static final By RECENTLY_ADDED = By.cssSelector("span.recentlyAdded > a");
    private static final By ALL_DOCUMENTS = By.cssSelector("span.all > a");
    private static final By CATEGORIES_IN_TREE = By.cssSelector("div[class^='categoryview'] span[id^='ygtvlabelel']");
    private final String subfolderName = null;
    private boolean shouldHaveFiles;
    private String contentName = null;
    private ViewType viewType = ViewType.DETAILED_VIEW;
    private static final String TEMPLATE_LIST = "//div[contains(@class, 'menu visible')]//div[@class='bd']//ul//ul//li";
    private final By submitButton = By.cssSelector("button[id$='default-createFolder-form-submit-button']");
    private static final By ALL_CATEGORIES_PRESENT_ON_DOC_LIB = By.xpath("//span[text()='Category Root']/ancestor-or-self::table/parent::div/child::div/div");
    private static String CATEGORY_ROOT_SPACER = "//span[text()='Category Root']/ancestor-or-self::table[contains(@class, 'depth0')]";
    private static By CATEGORY_ROOT_SPACER_LINK = By.xpath(CATEGORY_ROOT_SPACER + "//a");
    private static final String CHECK_BOX = "input[id^='checkbox-yui']";
    private static final By SYNC_MESSAGE = By.xpath(".//span[contains(text(),'Sync was created')]");

    public enum Optype
    {
        REQ_TO_SYNC, SYNC, UNSYNC;
    }

    /**
     * Getter method
     */
    public String getContentName()
    {
        return contentName;
    }

    public void setContentName(String contentName)
    {
        this.contentName = contentName;
    }

    /**
     * Check if javascript message is displayed. The message details are loading document library message.
     *
     * @return if message displayed
     */
    private boolean isDocumentLibLoading()
    {
        // Check url to see if the document lib page is present.
        return (isJSMessageDisplayed() || pageLoadingMessageDisplayed());
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized DocumentLibraryPage render(RenderTime timer)
    {
        while (true)
        {
            try
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(100L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }

                // Case 1: When we have no files we check upload instruction appears and we are not expecting files
                if (isFileUploadInstructionDisplayed() && !shouldHaveFiles)
                {
                    if (paginatorRendered())
                    {
                        boolean hasNoFiles = !hasFiles();
                        if (hasNoFiles)
                        {
                            if (logger.isTraceEnabled())
                            {
                                logger.trace("upload message appears and has no files is: " + hasNoFiles);
                            }
                            break;
                        }
                    }
                }
                // Case 2: When we have some files or expecting to see a file.
                // Check if loading document library message is gone
                if (!isDocumentLibLoading())
                {
                    // Check if were in correct folder
                    if (subfolderName != null)
                    {
                        if (isSubFolderDocLib(subfolderName))
                        {
                            // Give time to reload the document library page
                            synchronized (this)
                            {
                                try
                                {
                                    this.wait(100L);
                                }
                                catch (InterruptedException e)
                                {
                                }
                            }
                            if (dataRendered())
                            {
                                break;
                            }
                        }
                    }
                    else
                    {
                        if (dataRendered())
                        {
                            break;
                        }
                    }
                }
            }
            catch (StaleElementReferenceException ste)
            {
                // DOM has changed therefore page should render once change is completed
            }
            finally
            {
                timer.end();
            }
        }

        viewType = getNavigation().getViewType();
        return this;
    }

    /**
     * Checks to see if document list bottom paginator is displayed
     *
     * @return true if displayed
     */
    public boolean paginatorRendered()
    {
        try
        {
            return driver.findElement(By.cssSelector(BOTTOM_PAGINATOR_LOCATION)).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    private boolean pageLoadingMessageDisplayed()
    {
        try
        {
            WebElement loadingElement = driver.findElement(By.cssSelector("table>tbody>tr>td.yui-dt-empty>div"));
            return (loadingElement.isDisplayed() && loadingElement.getText().contains("Loading"));

        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Render logic to check if files or folders are present on the document library page or if empty file folder help message is displayed.
     *
     * @return true if page has rendered
     */
    private boolean dataRendered()
    {
        // First check bottom pagination is rendered
        if (paginatorRendered())
        {
            // If result is expected
            if (shouldHaveFiles)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("dataRendered check with shouldHaveFiles ");
                }
                return hasData();
            }
            // otherwise let it be true
            return true;
        }
        return false;
    }

    /**
     * Checks if file upload message appears, this indicates there are no files or folders in the document library page
     *
     * @return true if message is displayed
     */
    private boolean hasNoData()
    {
        try
        {
            WebElement td = driver.findElement(By.cssSelector("tbody.yui-dt-message tr td"));
            boolean visible = td.isDisplayed();
            return visible;
        }
        catch (NoSuchElementException e)
        {
        }
        return false;

    }

    /**
     * Check to see if document library contains files or folders with tags.
     *
     * @return true if files or folders are displayed
     */
    private boolean hasData()
    {
        // Look for results
        try
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("starting hasData check");
            }
            if (hasNoData())
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("hasNoData returned true so returning false");
                }
                return false;
            }
            if (logger.isTraceEnabled())
            {
                logger.trace("checking has content rows");
            }
            boolean hasContentRows = hasFiles();
            if (logger.isTraceEnabled())
            {
                logger.trace("checking has content row done hasContentRows: " + hasContentRows);
            }
            return hasContentRows;
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }


    /**
     * Checks to verify if we are in the correct sub folder document library page.
     *
     * @return true if bread crumb match location of the sub folder name.
     */
    private boolean isSubFolderDocLib(final String name)
    {
        // If we are expected to be in sub folder assert by checking the bread crumb
        try
        {
            List<WebElement> list = driver.findElements(By.cssSelector("div[id$='default-breadcrumb']>div.crumb>span.label>a"));
            WebElement element = list.get(list.size() - 1);
            String folderName = element.getText();
            if (name.equalsIgnoreCase(folderName))
            {
                return true;
            }
        }
        catch (Exception e)
        {
            logger.debug("Unable to determine if in sub folder of: " + subfolderName);
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentLibraryPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public DocumentLibraryPage renderItem(final long time, String itemName)
    {
        elementRender(new RenderTime(time), RenderElement.getVisibleRenderElement(By.xpath(String.format("//h3/span/a[text()='%s']/../../../../..", itemName))));
        return this;
    }

    /**
     * Verify if the page viewed is the document library page.
     */
    public boolean isDocumentLibrary()
    {
        try
        {
            return driver.findElement(By.cssSelector("#alfresco-documentlibrary")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    DocumentLibraryNavigation documentLibraryNavigation;
    /**
     * Get document library sub navigation.
     *
     * @return {@link DocumentLibraryNavigation} object.
     */
    public DocumentLibraryNavigation getNavigation()
    {
        return documentLibraryNavigation;
    }

    /**
     * Get filmstrip view actions
     *
     * @return {@link FilmstripActions} object.
     */
    public FilmstripActions getFilmstripActions()
    {
        return factoryPage.instantiatePage(driver,FilmstripActions.class).render();
    }
    TreeMenuNavigation treeMenuNavigation;
    /**
     * Get document library left hand navigation menu trees.
     *
     * @return {@link TreeMenuNavigation} object.
     */
    public TreeMenuNavigation getLeftMenus()
    {
        return treeMenuNavigation;
    }

    /**
     * Check is instruction message is displayed. If it is displayed then it is a good indication that there are no files or folder on the page.
     *
     * @return if displayed
     */
    public boolean isFileUploadInstructionDisplayed()
    {
        try
        {
            boolean displayed = driver.findElement(By.cssSelector(FILE_UPLOAD_INSTRUCTION)).isDisplayed();
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("** File upload instruction is displayed: %s", displayed));
            }
            return displayed;
        }
        catch (NoSuchElementException e)
        {
        }
        return false;
    }

    /**
     * Checks document list is populated by injecting a javascript in to an alfresco component that renders the document list.
     *
     * @return true if collection of documents exists
     */
    public boolean hasFiles()
    {
        try
        {
            ArrayList<?> objs = (ArrayList<?>) executeJavaScript(JS_SCRIPT_CHECK_DOCLIST);
            if (!objs.isEmpty())
            {
                return true;
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Extracts the results from result table that matches the file name.
     *
     * @return Collection of {@link FileDirectoryInfo} relating to result
     */
    public List<FileDirectoryInfo> getFiles()
    {
        try
        {
            boolean noFiles = !hasFiles();
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("Document list has no files: %s", noFiles));
            }

            if (noFiles)
            {
                return Collections.emptyList();
            }

            List<WebElement> results = driver.findElements(By.cssSelector("tr.yui-dt-rec "));
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("getFiles list is empty: %s file size %d", results.isEmpty(), results.size()));
            }

            if (!results.isEmpty())
            {
                List<FileDirectoryInfo> fileDirectoryList = new ArrayList<FileDirectoryInfo>();
                for (WebElement result : results)
                {
                    WebElement element = result.findElement(By.cssSelector("input[type='checkbox']"));
                    
                    String nodeRef = element.getAttribute("value");
                    
                    FileDirectoryInfo file = getFileDirectoryInfo(nodeRef, result);
                    logger.debug("adding file" + file.getName());
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("adding file" + file.getName());
                    }
                    fileDirectoryList.add(file);
                }
                return fileDirectoryList;
            }
            // Try again as we are expecting results.
            return getFiles();
        }
        catch (NoSuchElementException e)
        {
        }
        catch (StaleElementReferenceException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.debug("found stale element retrying get files");
            }
        }
        // Try again as we should have results else upload instructions view would be piked up.
        return getFiles();
    }

    /**
     * Selects the title of the document link.
     *
     * @param title String file title
     * @return DocumentDetailsPage page response object
     */
    public HtmlPage selectFile(final String title)
    {
        selectEntry(title).click();
        waitUntilAlert();
        return getCurrentPage();
    }

    
    /**
     * Selects the title of the document link edited offline.
     *
     * @param title String file title
     * @return DocumentDetailsPage page response object
     */
    public DocumentEditOfflinePage selectFileEditedOffline(final String title)
    {
        selectEntry(title).click();
        waitUntilAlert();
        return factoryPage.instantiatePage(driver, DocumentEditOfflinePage.class);
    }
    
    
    /**
     * Selects the title of the folder link.
     *
     * @param title String folder title
     * @return HtmlPage page response object
     */

    public HtmlPage selectFolder(final String title)
    {
        try
        {
            selectEntry(title).click();
            waitUntilAlert();
        }
        catch(TimeoutException e)
        {
            throw new PageOperationException("Timeout locating folder: " + title, e);
        }
        return getCurrentPage();
    }

    /**
     * Selects an entry regardless of type (file or folder).
     * The entry selected is based on the name displayed and not the actual
     * file name hence we are using the function normalize-space().
     * An example:
     * An entry may display Hello world.txt but in the html it would be
     * Hello        world.txt
     * 
     *
     * @return WebElement
     */
    protected WebElement selectEntry(final String title)
    {
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Title is required");
        String xpath = "//h3/span/a[normalize-space(text())='%s']";

        switch (viewType)
        {
            case TABLE_VIEW:
                xpath = "//td[contains(@class,'yui-dt-col-name')]/div/span/a[normalize-space(text())='%s']";
                break;
            case DETAILED_VIEW:    
                xpath = "//a[text()='%s']";
                break;
            default:
                break;
        }

        if(title == null || title.isEmpty()) {throw new IllegalArgumentException("Title is required");}
        String search = String.format(xpath, title);
        try
        {
            return findAndWait(By.xpath(search), getDefaultWaitTime());
        }
        catch(Exception e)
        {
            throw new NoSuchElementException("Unabel to find element, query: " + search);
        }
    }

    /**
     * Selects file or folder from page.
     *
     * @param name identifier
     * @return true if selected
     */
    private synchronized FileDirectoryInfo findFileOrFolder(final String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new IllegalArgumentException("Name is required");
        }
        if (logger.isTraceEnabled())
        {
            logger.trace("Look in collection for: " + name);
        }
        List<FileDirectoryInfo> files = getFiles();
        for (FileDirectoryInfo file : files)
        {
            String fileName = file.getName();
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("matching %s against %s", name, fileName));
            }
            if (name.equalsIgnoreCase(fileName))
            {
                return file;
            }
        }
        if (logger.isTraceEnabled())
        {
            logger.trace(String.format("content: %s not found", name));
        }
        throw new PageOperationException("Unable to locate fileName: " + name);
    }

    /**
     * Checks to see if file is visible on the page.
     *
     * @param fileName String title
     * @return true if file exists on the page
     */
    public synchronized boolean isFileVisible(final String fileName)
    {
        if (fileName == null || fileName.isEmpty())
        {
            throw new UnsupportedOperationException("File name required");
        }
        try
        {
            for (FileDirectoryInfo file : getFiles())
            {
                if (fileName.equalsIgnoreCase(file.getName()))
                {
                    return true;
                }
            }
        }
        catch (PageOperationException e)
        {
        }
        return false;
    }

    /**
     * Checks to see if files is on the page.
     *
     * @return true if file exists on the page
     */
    public boolean isFilesVisible()
    {
        try
        {
            WebElement content = driver.findElement(By.cssSelector("div[id$='default-documents']"));
            return content.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * The number of comments value displayed on the span comment count.
     *
     * @return {@link Integer} total number of comments
     */
    public Integer getCommentCount()
    {
        try
        {
            WebElement span = driver.findElement(By.cssSelector("span.comment-count"));
            return Integer.valueOf(span.getText());
        }
        catch (NoSuchElementException nse)
        {
            return 0;
        }
    }

    /**
     * Checks if pagination next button is active.
     *
     * @return true if next page exists
     */
    public boolean hasNextPage()
    {
        return Pagination.hasPaginationButton(driver, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Checks if pagination previous button is active.
     *
     * @return true if next page exists
     */
    public boolean hasPreviousPage()
    {
        return Pagination.hasPaginationButton(driver, PAGINATION_BUTTON_PREVIOUS);
    }

    /**
     * Selects the button next on the pagination bar.
     */
    public HtmlPage selectNextPage()
    {
        Pagination pagination = (Pagination)factoryPage.instantiatePageElement(driver,Pagination.class);
        return pagination.selectPaginationButton(driver, PAGINATION_BUTTON_NEXT);
    }

    /**
     * Selects the button previous on the pagination bar.
     */
    public HtmlPage selectPreviousPage()
    {
        Pagination pagination = (Pagination)factoryPage.instantiatePageElement(driver,Pagination.class);
        return pagination.selectPaginationButton(driver, PAGINATION_BUTTON_PREVIOUS);
    }

    /**
     * Locates the file or folder and deletes it.
     *
     * @param name String identifier
     * @return page response
     */
    public HtmlPage deleteItem(final String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("require name value");
        }
        FileDirectoryInfo item = getFileDirectoryInfo(name);
        item.selectDelete();
        confirmDelete();
        return getCurrentPage();
    }

    /**
     * Locates the file or folder and deletes it.
     *
     * @param number int identifier
     * @return page response
     */
    public HtmlPage deleteItem(final int number)
    {
        FileDirectoryInfo item = getFileDirectoryInfo(number);
        item.selectDelete();
        confirmDelete();
        return getCurrentPage();
    }

    /**
     * Action of selecting ok on confirm delete pop up dialog.
     */
    private void confirmDelete()
    {
        WebElement confirmDelete = driver.findElement(By.cssSelector("div#prompt div.ft span span button"));
        confirmDelete.click();
        if (logger.isTraceEnabled())
        {
            logger.trace("deleting");
        }
    }

    /**
     * Returns the ShareContentRow for the selected contentName.
     *
     * @param name String content name identifier
     * @return {@link FileDirectoryInfo}
     * @deprecated use getFileDirectoryInfo
     */
    public synchronized FileDirectoryInfo getContentRow(final String name)
    {
        if (name == null || name.isEmpty())
        {
            throw new UnsupportedOperationException("Name input value is required");
        }
        return findFileOrFolder(name);
    }

    /**
     * This method does the clicking on given tag name, which presents under the Tags Tree menu on Document Library page.
     * <br>depricated Use {@link TreeMenuNavigation#selectTagNode(String)} instead.
     *
     * @param tagName String
     * @return {@link DocumentLibraryPage}
     *
     */
    @Deprecated
    public HtmlPage clickOnTagNameUnderTagsTreeMenuOnDocumentLibrary(String tagName)
    {
        if (tagName == null)
        {
            throw new UnsupportedOperationException("TagName is required.");
        }

        String text = null;
        List<WebElement> tags = getAllTags();

        for (WebElement tag : tags)
        {
            text = tag.getText();

            if (text != null && text.equalsIgnoreCase(tagName))
            {
                tag.click();
                return getCurrentPage();
            }
        }

        throw new PageException("Not able to find the given tag : " + tagName);

    }

    /**
     * This method gets the all tag elements present on document library Tags tree menu.
     *
     * @return List<WebElement>
     */
    private List<WebElement> getAllTags()
    {
        try
        {
            return findAndWaitForElements(By.cssSelector(ALL_TAGS_PRESENT_ON_DOC_LIB));
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find the All Tags css.", e);
            }
            return Collections.emptyList();
        }
    }

    /**
     * This method gets the list of tag names present on document library Tags tree menu.
     *
     * @return List<String>
     */
    public List<String> getAllTagNames()
    {
        List<String> tagNames = new ArrayList<String>();
        String text = null;

        List<WebElement> tags = getAllTags();

        for (WebElement tag : tags)
        {
            text = tag.getText();

            if (text != null)
            {
                tagNames.add(text);
            }
        }
        return tagNames;
    }


    /**
     * This method gets the list of tag names present on document library Categories tree menu.
     *
     * @return List<String>
     */
    public List<String> getAllCategoriesNames()
    {
        List<String> categoryNames = new ArrayList<>();
        String text;
        openCategoriesTree();
        WebElement spacer = findAndWait(By.xpath(CATEGORY_ROOT_SPACER), 5000);
        if(spacer.getAttribute("class").contains("collapsed"))
        {
            findAndWait(CATEGORY_ROOT_SPACER_LINK).click();
            waitUntilElementPresent(CATEGORY_ROOT_SPACER_LINK, 5);
        }

        List<WebElement> categories = getAllCategories();

        for (WebElement category : categories)
        {
            text = category.getText();

            if (text != null)
            {
                categoryNames.add(text);
            }
        }
        return categoryNames;
    }

    /**
     * This method gets the all categories elements present on document library Categories tree menu.
     *
     * @return List<WebElement>
     */
    private List<WebElement> getAllCategories()
    {
        try
        {
            return findAndWaitForElements(ALL_CATEGORIES_PRESENT_ON_DOC_LIB);
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded the time to find the All Categories css.", e);
            }
            return Collections.emptyList();
        }
    }

    public boolean isItemVisble(String contentName)
    {
        PageUtils.checkMandatoryParam("contentName", contentName);
        try
        {
            return null != findFileOrFolder(contentName);
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Select a particular file directory info row based on the count, the accepted range is 1-50.
     *
     * @param number Integer item row
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final Integer number)
    {
        if (number == null || !((number > 0) && (number < 50)))
        {
            throw new IllegalArgumentException("A valid number range of 1 to 50 is required");
        }
        try
        {
            WebElement row = driver.findElement(By.cssSelector(String.format("tbody.yui-dt-data tr:nth-of-type(%d)", number)));
            String nodeRef = row.findElement(THUMBNAIL_IMAGE).getAttribute("id");
            return getFileDirectoryInfo(nodeRef, row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException(String.format("File directory info row %d was not found", number), e);
        }
    }

    /**
     * Select a particular file directory info row based on the title.
     *
     * @param title String item title
     * @return {@link FileDirectoryInfo} page response
     */
    public FileDirectoryInfo getFileDirectoryInfo(final String title)
    {
        if (title == null || title.isEmpty())
        {
            throw new IllegalArgumentException("Title is required");
        }
        WebElement row;
        String nodeRef;
        try
        {
            String query = String.format("//h3/span/a[text()='%s']/../../../../..", title);
            row = driver.findElement(By.xpath(query));
            nodeRef = row.findElement(THUMBNAIL_IMAGE).getAttribute("id");
            return getFileDirectoryInfo(nodeRef, row);
        }
        catch (NoSuchElementException e)
        {
            throw new PageOperationException(String.format("File directory info with title %s was not found", title), e);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException(String.format("File directory info with title %s was not found", title), e);
        }
    }

    public boolean isShouldHaveFiles()
    {
        return shouldHaveFiles;
    }

    public void setShouldHaveFiles(boolean shouldHaveFiles)
    {
        this.shouldHaveFiles = shouldHaveFiles;
    }

    /**
     * @param optype Optype
     * @return boolean
     */
    public boolean isMessagePresent(Optype optype)
    {

        try
        {
            String message = driver.findElement(By.cssSelector(".message")).getText();
            switch (optype)
            {
                case SYNC:
                    return "Sync was created".equals(message) ? true : false;
                case UNSYNC:
                    return "Sync has been removed".equals(message) ? true : false;
                case REQ_TO_SYNC:
                    return "Successfully requested Sync".equalsIgnoreCase(message) ? true : false;
                default:
                    throw new PageOperationException(message);
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("Message element not found!!", toe);
        }
        throw new PageOperationException("Message element not found!!");
    }
    
    /**
     * Returns true if Sync message is present
     *
     * @return boolean
     */
    
    public boolean isSyncMessagePresent()
    {
        try
        {
            waitForElement(SYNC_MESSAGE, SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
            WebElement syncMessage = driver.findElement(SYNC_MESSAGE);
            if (syncMessage != null)
            {
                return true;
            }
        }
        catch(TimeoutException toe)
        {
            logger.error("Message element not found!!", toe);
            return false;
        }
        return false;
    }
    

    /**
     * Returns true if Cloud Sync sign up dialog is visible
     *
     * @return boolean
     */
    public boolean isSignUpDialogVisible()
    {
        try
        {
            return findAndWait(By.cssSelector("form.cloud-auth-form")).isDisplayed();
        }
        catch (TimeoutException te)
        {
            return false;
        }
    }

    /**
     * This method returns the count for the given tag string.
     * <br>depricated Use {@link TreeMenuNavigation#getTagCount(String)}
     *
     * @param tagName String
     * @return int
     *
     */
    @Deprecated
    public int getTagsCountUnderTagsTreeMenu(String tagName)
    {
        if (tagName == null)
        {
            throw new UnsupportedOperationException("TagName is required.");
        }

        try
        {
            String count = findAndWait(By.xpath(String.format(".//ul[@class='filterLink']//a[@rel='%s']/..", tagName.toLowerCase()))).getText();

            return Integer.parseInt(count.substring(count.indexOf("(") + 1, count.indexOf(")")));
        }
        catch (TimeoutException te)
        {
            logger.error("Exceeded time to find out the " + tagName + " count: ", te);
        }
        catch (NumberFormatException ne)
        {
            logger.error("Unable to convert tags count string value into int : ", ne);
        }

        throw new PageException("Unable to find the given tag count : " + tagName);
    }

    /**
     * Check the documents tree is expanded or not , on DocumentLibraryPage or Repository Page.
     *
     * @return - boolean
     */
    public boolean isDocumentsTreeExpanded()
    {
        try
        {
            WebElement documents = driver.findElement(DOCUMENTS_TREE_CSS);
            if (documents.getAttribute("class").contains("open"))
            {
                return true;
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Exceeded time to find the documents tree.", e);
        }

        return false;
    }

    /**
     * This method is used to click on the documents tree, on DocumentLibraryPage or Repository Page.
     *
     * @return - DocumentLibraryPage
     */
    public HtmlPage clickDocumentsTreeExpanded()
    {
        try
        {
            findAndWait(DOCUMENTS_TREE_CSS).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the documents tree.", e);
        }
        throw new PageException("Unable to find the Documents Tree link.");
    }

    /**
     * This method is used to open on the categories tree, on DocumentLibraryPage or Repository Page.
     *
     * @return - DocumentLibraryPage
     */
    public HtmlPage openCategoriesTree()
    {
        try
        {
            WebElement cat = findAndWait(CATEGORIES_IN_TREE);
            if(cat.getAttribute("class").contains("closed"))
                findAndWait(CATEGORIES_TREE_CSS).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the categories tree.", e);
        }
        throw new PageException("Unable to find the Categories Tree link.");
    }


    private FileDirectoryInfo getFileDirectoryInfo(String nodeRef, WebElement webElement)
    {
        if (viewType == null)
        {
            throw new UnsupportedOperationException("Document Library page render is needed.");
        }

        return FactoryShareFileDirectoryInfo.getPage(nodeRef, webElement, driver, viewType, factoryPage);
    }

    protected void setViewType(ViewType viewType)
    {
        this.viewType = viewType;
    }

    /**
     * Returns the current view type if it set already or by calling render it sets the view type.
     *
     * @return {@link ViewType}
     */
    public ViewType getViewType()
    {
        return viewType;
    }

    /**
     * Click on "My Favourites" it will take you to document library/Repository page.
     *
     * @return HtmlPage
     */
    public HtmlPage clickOnMyFavourites()
    {
        try
        {
            WebElement element = driver.findElement(MY_FAVOURITES);
            if (element.isDisplayed())
            {
                element.click();
                waitUntilAlert();
                return getCurrentPage();
            }

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("My Favourites is not loaded", nse);
            }
        }
        throw new PageOperationException("My Favourites not loaded - My Favourites tape may not be displayed.");
    }

    /**
     * Click on "Recently Modified" it will take you to document library/Repository page.
     *
     * @return DocumentLibraryPage
     */
    public HtmlPage clickOnRecentlyModified()
    {
        try
        {
            WebElement element = driver.findElement(RECENTLY_MODIFIED);
            if (element.isDisplayed())
            {
                element.click();
                waitUntilAlert();
                return getCurrentPage();
            }

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Recently Modified is not loaded", nse);
            }
        }
        throw new PageOperationException("Recently Modified not loaded - Recently Modified tape may not be displayed.");
    }

    /**
     * Click on "Recently Added" it will take you to document library/Repository page.
     *
     * @return DocumentLibraryPage
     */
    public HtmlPage clickOnRecentlyAdded()
    {
        try
        {
            WebElement element = driver.findElement(RECENTLY_ADDED);
            if (element.isDisplayed())
            {
                element.click();
                waitUntilAlert();
                return getCurrentPage();
            }

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Recently Added is not loaded", nse);
            }
        }
        throw new PageOperationException("Recently Added not loaded - Recently Added tape may not be displayed.");
    }

    public HtmlPage clickOnAllDocuments()
    {
        try
        {
            WebElement element = driver.findElement(ALL_DOCUMENTS);
            if (element.isDisplayed())
            {
                element.click();
                waitUntilAlert();
                return getCurrentPage();
            }

        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("All Documents is not loaded", nse);
            }
        }
        throw new PageOperationException("All Documents - All Documents tape may not be displayed.");
    }

    /**
     * Returned Object mimic all action with Pagination.
     *
     * @return PaginationForm
     */
    public PaginationForm getBottomPaginationForm()
    {
        PaginationForm bottomPagination = new PaginationForm(driver, By.xpath("//div[contains(@id,'default-paginatorBottom')]"));
        if (bottomPagination.isDisplay())
        {
            return bottomPagination;
        }
        logger.trace("Can't find Bottom pagination on Page");
        throw new PageOperationException("Can't find Bottom pagination on Page");
    }


    /**
     * Click on Category link in left tree menu.
     *
     * @param categoryName Categories
     * @return PaginationForm
     */
    public HtmlPage clickOnCategory(Categories categoryName)
    {
        List<WebElement> categories = findAndWaitForElements(CATEGORIES_IN_TREE);
        for (WebElement category : categories)
        {
            if (category.getText().equals(categoryName.getValue()))
            {
                category.click();
                waitUntilAlert();
                return getCurrentPage().render();
            }
        }
        throw new PageOperationException(String.format("Category didn't found [%s]", categoryName));
    }

    /**
     * Create content from template
     *
     * @param templateName String
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage createContentFromTemplate(String templateName)
    {
        try
        {
            if(!templateName.isEmpty())
            {
                getNavigation().selectCreateContentFromTemplate();
                WebElement template = getTemplate(templateName);
                template.click();

                return  getCurrentPage();
        }
        }
        catch (StaleElementReferenceException ste)
        {
            // DOM has changed therefore page should render once change is completed
        }

        throw new PageOperationException(String.format("Template didn't found [%s]", templateName));

    }

    /**
     * Create folder from template
     *
     * @param templateName String
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage createFolderFromTemplate(String templateName)
    {
        try{
            if(!templateName.isEmpty()){
                getNavigation().selectCreateFolderFromTemplate();
                WebElement template = getTemplate(templateName);
                template.click();
                WebElement okButton = findAndWait(submitButton);
                okButton.click();
                waitUntilMessageAppearAndDisappear("Folder", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                DocumentLibraryPage page = getCurrentPage().render();
                page.setShouldHaveFiles(true);
                return getCurrentPage();
            }
        }
        catch (StaleElementReferenceException ste)
        {
            // DOM has changed therefore page should render once change is completed
        }

        throw new PageOperationException(String.format("Template didn't found [%s]", templateName));

    }

    /**
     * find all existing templates.
     * 'Create content from template' or 'Create folder from template' menu must be chosen
     *
     * @return List<WebElement> of all existing templates
     */
    public List<WebElement> getTemplateList(){
        getCurrentPage().render();
        waitUntilNotVisibleWithParitalText(By.xpath(TEMPLATE_LIST), "Loading...", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        findAndWaitForElements(By.xpath(TEMPLATE_LIST));
        return findDisplayedElements(By.xpath(TEMPLATE_LIST));
    }

    /**
     * find needed template from lists of templates
     *
     * @param templateName String
     * @return WebElement of needed template
     */
    public WebElement getTemplate(String templateName){
        List<WebElement> list = getTemplateList();
        if(list.get(0).getText().contains("Loading")){
            while (list.get(0).getText().contains("Loading")){
                list = getTemplateList();
                getCurrentPage().render();
            }

        }
        for(WebElement template : list){
            if(template.getText().equals(templateName)){
                return  template;
            }
        }
        throw new PageOperationException(String.format("Template [%s] didn't found on page.", templateName));
    }

    /**
     * Create content from template
     *
     * @param templateName String
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage createContentFromTemplateHover(String templateName)
    {
        try{
            if(!templateName.isEmpty()){
                getNavigation().selectCreateContentFromTemplateHover().render();
                WebElement template = getTemplate(templateName);
                template.click();

                return  getCurrentPage();
            }
        }
        catch (StaleElementReferenceException ste)
        {
        }

        throw new PageOperationException(String.format("Template didn't found [%s]", templateName));

    }

    /**
     * Create folder from template
     *
     * @param templateName String
     * @return {@link DocumentLibraryPage}
     */
    public DocumentLibraryPage createFolderFromTemplateHover(String templateName)
    {
        try{
            if(!templateName.isEmpty()){
                getNavigation().selectCreateFolderFromTemplateHover().render();
//                WebElement template = getTemplate(templateName);
//                template.click();
                findAndWait(By.xpath("//div[@class='bd']//span[contains(text(), '" + templateName + "')]")).click();
//                getCurrentPage().render();
                WebElement okButton = findAndWait(submitButton);
                okButton.click();
                waitUntilMessageAppearAndDisappear("Folder", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                DocumentLibraryPage page = getCurrentPage().render();
                page.setShouldHaveFiles(true);
                return page;
            }
        }
        catch (StaleElementReferenceException ste)
        {
        }

        throw new PageOperationException(String.format("Template didn't found [%s]", templateName));

    }

    /**
     * Method checks if check-box present at a page
     *
     * @return true if present
     */
    public boolean isCheckBoxPresent()
    {
        try
        {
            return findAndWait(By.cssSelector(CHECK_BOX)).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error(CHECK_BOX + " isn't present at a page");
            return false;
        }
    }


    /**
     * Create folder from template
     *
     * @param templateName String
     * @return {@link DocumentLibraryPage}
     */
    public HtmlPage openFolderFromTemplateHover(String templateName)
    {
        try{
            if(!templateName.isEmpty()){
                getNavigation().selectCreateFolderFromTemplateHover().render();
                findAndWait(By.xpath("//div[@class='bd']//span[contains(text(), '" + templateName + "')]")).click();
                findAndWait(submitButton);
                
                return getCurrentPage();
            }
        }
        catch (StaleElementReferenceException ste)
        {
        }
        throw new PageOperationException(String.format("Template didn't found [%s]", templateName));

    }

    /**
     * The method helps to navigate to a folder or a file from document library.
     * @param title String
     * @return HtmlPage
     */
    public HtmlPage browseToEntry(String title) throws Exception
    {
        DocumentLibraryPage documentLibraryPage = getCurrentPage().render();
        FileDirectoryInfo fileInfo = documentLibraryPage.getFileDirectoryInfo(title);

        if (fileInfo.isFolder())
        {
            String url = selectEntry(title).getAttribute("href");
            String param = selectEntry(title).getAttribute("rel");
            param = param.substring(1, param.length());

            URIBuilder b = new URIBuilder(url);
            b.addParameter("filter", param);
            url = b.build().toString();
            driver.navigate().to(url);
        }
        else
        {
            String url = selectEntry(title).getAttribute("href");
            driver.navigate().to(url);
        }

        return getCurrentPage();
    }
}
