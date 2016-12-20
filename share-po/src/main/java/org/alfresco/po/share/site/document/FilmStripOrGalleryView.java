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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Abstract of an Gallery/FilmStrip view of FileDirectoryInfo.
 * 
 * @author cbairaajoni
 */
public abstract class FilmStripOrGalleryView extends FileDirectoryInfoImpl
{
    private static Log logger = LogFactory.getLog(FilmStripOrGalleryView.class);
    protected String TAG_INFO = "//div[@class='detail']/span[@class='insitu-edit']/../span[@class='item']";
    protected String TAG_ICON = "//h3/span/a[text()='%s']/../../../../../../../div/div[starts-with(@class,'alf-detail')]/div/div/div/span[@title='Tag']";
    protected static final String MODIFIED_F = "//div/span[contains(text(),'Modified')]";
    protected static final By DOC_FOOTER = By.cssSelector("div[id$='default-doclistBarBottom']");

//    public FilmStripOrGalleryView(String nodeRef, WebElement webElement, WebDriver driver)
//    {
//        super(nodeRef, webElement, driver);
//    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getDescription()
     */
    @Override
    public String getDescription()
    {
        clickInfoIcon(false);
        String desc = super.getDescription();
        focusOnDocLibFooter();
        return desc;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentEditInfo()
     */
    @Override
    public String getContentEditInfo()
    {
        clickInfoIcon(false);
        String editInfo = super.getContentEditInfo();
        focusOnDocLibFooter();
        return editInfo;
    }


    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getCategories()
     */
    @Override
    public List<Categories> getCategories()
    {
        clickInfoIcon(false);
        List<Categories> cats = super.getCategories();
        focusOnDocLibFooter();
        return cats;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDelete()
     */
    @Override
    public ConfirmDeletePage selectDelete()
    {
        clickInfoIcon(true);
        return super.selectDelete();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditProperties()
     */
    @Override
    public EditDocumentPropertiesPage selectEditProperties()
    {
        clickInfoIcon(false);
        return super.selectEditProperties();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewInBrowser()
     */
    @Override
    public void selectViewInBrowser()
    {
        clickInfoIcon(false);
        super.selectViewInBrowser();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectFavourite()
     */
    @Override
    public void selectFavourite()
    {
        clickInfoIcon(false);
        super.selectFavourite();
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectLike()
     */
    @Override
    public void selectLike()
    {
        clickInfoIcon(false);
        super.selectLike();
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLiked()
     */
    @Override
    public boolean isLiked()
    {
        clickInfoIcon(false);
        boolean like = super.isLiked();
        focusOnDocLibFooter();
        return like;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isFavourite()
     */
    @Override
    public boolean isFavourite()
    {
        clickInfoIcon(false);
        boolean favourite = super.isFavourite();
        focusOnDocLibFooter();
        return favourite;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getLikeCount()
     */
    @Override
    public String getLikeCount()
    {
        clickInfoIcon(false);
        String likeCount = super.getLikeCount();
        focusOnDocLibFooter();
        return likeCount;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#getContentNodeRef()
     */
    @Override
    public String getContentNodeRef()
    {
        clickInfoIcon(false);
        String nodeRef = super.getContentNodeRef();
        focusOnDocLibFooter();
        return nodeRef;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isPartOfWorkflow()
     */
    @Override
    public boolean isPartOfWorkflow()
    {
        clickInfoIcon(false);
        boolean cloudSync = super.isPartOfWorkflow();
        focusOnDocLibFooter();
        return cloudSync;
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
            throw new UnsupportedOperationException("Download folder as zip is available for folders only.");
        }
        clickInfoIcon(false);
        super.downloadFolderAsZip();
        focusOnDocLibFooter();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectDownload()
     */
    @Override
    public void selectDownload()
    {
        clickInfoIcon(false);
        super.selectDownload();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectViewFolderDetails()
     */
    @Override
    public FolderDetailsPage selectViewFolderDetails()
    {
        clickInfoIcon(false);
        return super.selectViewFolderDetails();
    }


    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectInlineEdit()
     */
    @Override
    public HtmlPage selectInlineEdit()
    {
        clickInfoIcon(true);
        return super.selectInlineEdit();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isLocked()
     */
    @Override
    public boolean isLocked()
    {
        clickInfoIcon(false);
        boolean lock = super.isLocked();
        focusOnDocLibFooter();
        return lock;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isInlineEditLinkPresent()
     */
    @Override
    public boolean isInlineEditLinkPresent()
    {
        clickInfoIcon(true);
        return super.isInlineEditLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditOfflineLinkPresent()
     */
    @Override
    public boolean isEditOfflineLinkPresent()
    {
        clickInfoIcon(true);
        return super.isEditOfflineLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isDeletePresent()
    {
        clickInfoIcon(true);
        boolean deletePresent = super.isDeletePresent();
        focusOnDocLibFooter();
        return deletePresent;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isDeletePresent()
     */
    @Override
    public boolean isViewInBrowserVisible()
    {
        // openGalleryInfo(true);
        return super.isViewInBrowserVisible();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageRules()
     */
    @Override
    public HtmlPage selectManageRules()
    {
        clickInfoIcon(true);
        return super.selectManageRules();
    }
    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManagePermission()
     */
    @Override
    public ManagePermissionsPage selectManagePermission()
    {
        clickInfoIcon(true);
        return super.selectManagePermission();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCopyTo()
     */
    @Override
    public CopyOrMoveContentPage selectCopyTo()
    {
        clickInfoIcon(true);
        return super.selectCopyTo();
    }
    
    @Override
    public CopyOrMoveContentPage selectCopyToOnFolderCloud()
    {
        clickInfoIcon(false);
        return super.selectCopyToOnFolderCloud();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectMoveTo()
     */
    @Override
    public CopyOrMoveContentPage selectMoveTo()
    {
        clickInfoIcon(true);
        return super.selectMoveTo();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectStartWorkFlow()
     */
    @Override
    public StartWorkFlowPage selectStartWorkFlow()
    {
        clickInfoIcon(true);
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
            clickInfoIcon(true);
            return super.selectUploadNewVersion();
        }
        catch (ElementNotVisibleException s)
        {
            resolveStaleness();
            selectUploadNewVersion();
        }
        throw new PageException("Element not visible");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isManagePermissionLinkPresent()
     */
    @Override
    public boolean isManagePermissionLinkPresent()
    {
        clickInfoIcon(true);
        return super.isManagePermissionLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#isEditPropertiesLinkPresent()
     */
    @Override
    public boolean isEditPropertiesLinkPresent()
    {
        clickInfoIcon(true);
        return super.isEditPropertiesLinkPresent();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectEditOffline()
     */
    @Override
    public DocumentLibraryPage selectEditOffline()
    {
        clickInfoIcon(true);
        return super.selectEditOffline();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectCancelEditing()
     */
    @Override
    public DocumentLibraryPage selectCancelEditing()
    {
        clickInfoIcon(true);
        return super.selectCancelEditing();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoInterface#selectManageAspects()
     */
    @Override
    public SelectAspectsPage selectManageAspects()
    {
        clickInfoIcon(true);
        return super.selectManageAspects();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoPopUpDisplayed()
     */
    @Override
    public boolean isInfoPopUpDisplayed()
    {
        try
        {
            return findAndWait(By.cssSelector("div.yui-panel-container")).isDisplayed();
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickInfoIcon()
     */
    @Override
    public void clickInfoIcon()
    {
        clickInfoIcon(false);
    }

    /**
     * @param hasMoreLink
     */
    private void clickInfoIcon(boolean hasMoreLink)
    {
        WebElement infoIcon = getInfoIcon();
        infoIcon.click();

        if (hasMoreLink)
        {
            selectMoreLink();
        }
        resolveStaleness();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectMoreLink()
     */
    @Override
    public void selectMoreLink()
    {
        try
        {
            findElement(By.cssSelector("a.show-more")).click();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Exceeded time to find the css.", e);
            throw new PageException("Unable to find and click the more link");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isInfoIconVisible()
     */
    @Override
    public boolean isInfoIconVisible()
    {
        try
        {
            WebElement infoIcon = getInfoIcon();
            return infoIcon.isDisplayed();
        }
        catch (PageException e)
        {
        }
        return false;
    }

    /**
     * @return WebElement
     */
    protected WebElement getInfoIcon()
    {
        try
        {
            // mouseOver(findAndWait(By.linkText(getName())));
            mouseOver(findAndWait(By.xpath(String.format(".//div[@class='alf-label']/a[text()='%s']", getName()))));
            return findAndWait(By.cssSelector("a.alf-show-detail"));
        }
        catch (TimeoutException e)
        {
            logger.error("Exceeded time to find the css.", e);
        }

        throw new PageException("File directory info with title was not found");
    }

    private void focusOnDocLibFooter()
    {
        mouseOver(findAndWait(By.xpath("//div[contains(@id,'default-doclistBarBottom')]")));
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
                clickInfoIcon(false);
                String temp = FILENAME_IDENTIFIER;
                FILENAME_IDENTIFIER = "h3.filename a";
                super.contentNameEnableEdit();
                FILENAME_IDENTIFIER = temp;
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
    public void contentNameEnter(String newContentName)
    {
        // openGalleryInfo(false);
        super.contentNameEnter(newContentName);
    }

    @Override
    public void contentNameClickSave()
    {
        // openGalleryInfo(false);
        super.contentNameClickSave();
    }

    @Override
    public void contentNameClickCancel()
    {
        // openGalleryInfo(false);
        super.contentNameClickCancel();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#isShareLinkVisible()
     */
    @Override
    public boolean isShareLinkVisible()
    {
        clickInfoIcon(false);
        boolean shareLinkVisible = super.isShareLinkVisible();//findFirstDisplayedElement
        WebElement element = driver.findElement(By.xpath("//div[@class='alf-detail-thumbnail']/../../.."));
        String id = element.getAttribute("id");
        mouseOver(findAndWait(By.cssSelector("button[id$='default-fileSelect-button-button']")));
        waitUntilElementDisappears(By.id(id), 30);
        return shareLinkVisible; 
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickShareLink()
     */
    @Override
    public HtmlPage clickShareLink()
    {
        clickInfoIcon(false);
        return super.clickShareLink();
    }

    @Override
    public String getContentInfo()
    {
        clickInfoIcon(false);
        return super.getContentInfo();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickOnCategoryNameLink(java.lang.String)
     */
    @Override
    public HtmlPage clickOnCategoryNameLink(String categoryName)
    {
        clickInfoIcon(true);
        return super.clickOnCategoryNameLink(categoryName);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#clickCommentsLink()
     */
    @Override
    public HtmlPage clickCommentsLink()
    {
        clickInfoIcon(false);
        return super.clickCommentsLink();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsToolTip()
     */
    @Override
    public String getCommentsToolTip()
    {
        clickInfoIcon(false);
        return super.getCommentsToolTip();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#getCommentsCount()
     */
    @Override
    public int getCommentsCount()
    {
        clickInfoIcon(false);
        return super.getCommentsCount();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isCommentLinkPresent()
     */
    @Override
    public boolean isCommentLinkPresent()
    {
        clickInfoIcon(false);
        return super.isCommentLinkPresent();
    }

    @Override
    public String getVersionInfo()
    {
        clickInfoIcon(false);
        return super.getVersionInfo();
    }

    @Override
    public String getContentNameFromInfoMenu()
    {
        clickInfoIcon(false);
        String title = "";
        try
        {
            title = findAndWait(By.cssSelector("h3.filename a")).getText();
        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            getName();
        }
        return title;
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfoImpl#clickContentNameFromInfoMenu()
     */
    @Override
    public HtmlPage clickContentNameFromInfoMenu()
    {
        clickInfoIcon(false);
        try
        {
            findAndWait(By.cssSelector("h3.filename a")).click();
            return getCurrentPage();
        }
        catch (TimeoutException te)
        {
            logger.error("Timeout Reached", te);
        }
        catch (StaleElementReferenceException stale)
        {
            resolveStaleness();
            clickContentNameFromInfoMenu();
        }
        throw new PageException("Unable to find and click the file name link");
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#selectModifier()
     */
    @Override
    public HtmlPage selectModifier()
    {
        clickInfoIcon(false);
        return super.selectModifier();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.po.share.site.document.FileDirectoryInfo#isFileShared()
     */
    @Override
    public boolean isFileShared()
    {
        clickInfoIcon(false);
        boolean result = super.isFileShared();
        focusOnDocLibFooter();
        return result;
    }

    /**
     * Selects the 'Actions' menu link on the select data row on DocumentLibrary Page.
     *
     * @return List of {@link WebElement} available for the selected Content
     */
    @Override
    public List<String> getContentActions() 
    {
        List<String> actions = new ArrayList<>();
        try
        {
            clickInfoIcon();
            List<WebElement> actionElements = findElements(By.cssSelector(ACTIONS_LIST));
            for (WebElement webElement : actionElements)
            {
                actions.add(webElement.getText());
            }
        }
        catch (NoSuchElementException e)
        {
            throw new NoSuchElementException("Not able to find actions", e);
        }
        return actions;
    }

    @Override
    public boolean isModelActive()
    {
        throw new UnsupportedOperationException("Model info not available in Table view.");
    }
    
    @Override
    public String getModelName()
    {
        throw new UnsupportedOperationException("Model info not available in Table view.");
    }
    
    @Override
    public String getModelDesription()
    {
        throw new UnsupportedOperationException("Model info not available in Table view.");
    }  

}
