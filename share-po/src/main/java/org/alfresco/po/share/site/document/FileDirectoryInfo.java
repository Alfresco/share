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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;

public interface FileDirectoryInfo
{

    /**
     * Gets the name of the file or directory, if none then empty string is
     * returned.
     *
     * @return String title
     */
    String getName();

    /**
     * Click on content title.
     * Opens DocumentLibraryPage if content is folder. Opens DocumentDetailsPage for file.
     *
     * @return HtmlPage
     */
    HtmlPage clickOnTitle();

    /**
     * Checks if the FileDirectory is of a folder type.
     *
     * @return true if folder
     */
    boolean isTypeFolder();

    /**
     * Gets the description of the file or directory, if none then empty string
     * is returned.
     *
     * @return String Content description
     */
    String getDescription();

    /**
     * Gets the Create / Edit Information of the file or directory, if none then
     * empty string is returned.
     *
     * @return String Content Edit Information
     */
    String getContentEditInfo();


    /**
     * Get the {@link List} of added {@link Categories}.
     * <br>depricated Use {@link #getCategories()} instead.
     *
     * @return {@link List} of {@link Categories}
     */
    List<Categories> getCategories();

    /**
     * Get the {@link List} of added categories.
     *
     * @return {@link List} of categories
     */
    List<String> getCategoryList();

    /**
     * Select the delete button on the item.
     *
     * @return boolean <tt>true</tt> if delete option is available and clicked
     */
    ConfirmDeletePage selectDelete();

    /**
     * Selects the edit properties link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link EditDocumentPropertiesPage} response
     */
    EditDocumentPropertiesPage selectEditProperties();

    /**
     * Selects the view in browser link on the select data row on
     * DocumentLibrary Page.
     */
    void selectViewInBrowser();

    /**
     * Selects or de-selects the favorite option on the select data row on DocumentLibrary Page.
     */
    void selectFavourite();

    /**
     * Selects or de selects the Like option on the select data row on
     * DocumentLibrary Page.
     */
    void selectLike();

    /**
     * Checks if the Like option is selected on the selected data row on
     * DocumentLibrary Page
     *
     * @return {boolean} true if the content is liked
     */
    boolean isLiked();

    /**
     * Checks if the Favourite option is selected on the selected data row on
     * DocumentLibrary Page
     *
     * @return {Boolean} true if the content is marked as Favourite
     */
    boolean isFavourite();

    /**
     * Gets the like count for the selected data row on DocumentLibrary Page
     *
     * @return {String} Like Count
     */
    String getLikeCount();


    /**
     * Get NodeRef for the content on the selected data row on DocumentLibrary
     * Page.
     *
     * @return {String} Node Ref / GUID
     */
    String getContentNodeRef();

    /**
     * Gets the Title of the file or directory, if none then empty string
     * is returned.
     *
     * @return String Content description
     */
    String getTitle();

    /**
     * Selects checkbox next to the contentRow.
     */
    void selectCheckbox();

    /**
     * Verify if checkbox next to the contentRow is selected.
     *
     * @return true if selected
     */
    boolean isCheckboxSelected();

    /**
     * Clicks on the thumbnail next to the contentRow.
     *
     * @return {Link SitePage} Instance of SitePage page object
     */
    HtmlPage selectThumbnail();

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * folder Page.
     *
     * @return {boolean} <tt>true</tt> if the content is of type folder.
     */
    boolean isFolder();

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * a link to a folder.
     *
     * @return {boolean} <tt>true</tt> if the content is of type link to folder.
     */
    boolean isLinkToFolder();

    /**
     * Returns true if content in the selected data row on DocumentLibrary is
     * a link to a file.
     *
     * @return {boolean} <tt>true</tt> if the content is of type link to file.
     */
    boolean isLinkToFile();

    /**
     * Returns true if Locate Linked Item is present
     *
     * @return {boolean} <tt>true</tt> if the Locate Linked Item action is present.
     */
    boolean isLocateLinkedItemDisplayed();

    /**
     * Returns true if Delete Link is present
     *
     * @return {boolean} <tt>true</tt> if the Delete Link action is present.
     */
    boolean isDeleteLinkDisplayed();

    /**
     * Returns whether the file / dir is part of workflow.
     *
     * @return boolean
     */
    boolean isPartOfWorkflow();

    /**
     * Selects the <Download as zip> link on the select data row on DocumentLibrary
     * Page. Only available for content type = Folder.
     */
    void selectDownloadFolderAsZip();

    /**
     * Selects the <Download> link on the select data row on DocumentLibrary Page.
     */
    void selectDownload();

    /**
     * Gets the node ref id of the content.
     *
     * @return String node identifier
     */
    String getNodeRef();

    /**
     * Selects the <View Details> link on the select data row on DocumentLibrary Page.
     * Only available for content type = Folder.
     *
     * @return {@link DocumentLibraryPage} response
     */
    FolderDetailsPage selectViewFolderDetails();


    /**
     * Returns true if Sign In To Alfresco Cloud popup opens (User haven't set up CloudSync)
     *
     * @return boolean
     */
    boolean isSignUpDialogVisible();

    /**
     * Selects the "Inline Edit" link on the select data row on
     * DocumentLibrary Page.
     *
     * @return {@link InlineEditPage} response
     */
    HtmlPage selectInlineEdit();

    /**
     * Retrieve content info (This document is locked by you., This document is locked by you for offline editing., Last sync failed.)
     *
     * @return String
     */
    String getContentInfo();

    /**
     * Method to check if the content is locked or not
     *
     * @return boolean
     */
    boolean isLocked();

    /**
     * Method to check if Inline Edit Link is displayed or not
     *
     * @return true if visible on the page
     */
    boolean isInlineEditLinkPresent();

    /**
     * Method to check if Edit Offline Link is displayed or not
     *
     * @return true if visible on the page
     */
    boolean isEditOfflineLinkPresent();

    /**
     * This method verifies the delete link is present or not.
     *
     * @return boolean
     */
    boolean isDeletePresent();

    /**
     * Select the link manage rules from
     * the actions drop down.
     */
    HtmlPage selectManageRules();
    
    /**
     * Verify if the Rule icon is displayed or not
     *
     * @param waitTime long
     * @return boolean
     */
    boolean isRuleIconPresent(long waitTime);

    /**
     * select Manage permission link from more option of document library.
     *
     * @return ManagePermissionsPage
     */
    ManagePermissionsPage selectManagePermission();

    /**
     * select Copy to... link from more option of document library.
     *
     * @return CopyOrMoveContentPage
     */
    CopyOrMoveContentPage selectCopyTo();

    /**
     * select Move to... link from more option of document library.
     *
     * @return CopyOrMoveContentPage
     */
    CopyOrMoveContentPage selectMoveTo();

    /**
     * Click on Locate Linked Item link from actions on links on document library.
     *
     * @return CopyOrMoveContentPage
     */
    HtmlPage selectLocateLinkedItem();

    /**
     * Click on Delete link from actions on links on document library.
     *
     * @return CopyOrMoveContentPage
     */
    HtmlPage selectDeleteLink();

    HtmlPage delete();

    HtmlPage deleteLink();

    /**
     * select StartWorkFlow... link from more option of document library.
     *
     * @return StartWorkFlowPage
     */
    StartWorkFlowPage selectStartWorkFlow();

    /**
     * Select UploadNewVersion - link fro more option of document library
     *
     * @return - UpdateFilePage
     */

    UpdateFilePage selectUploadNewVersion();

    /**
     * check Manage permission link from more option of document library.
     *
     * @return boolean
     */
    boolean isManagePermissionLinkPresent();

    /**
     * check Edit properties link from more option of document library.
     *
     * @return boolean
     */
    boolean isEditPropertiesLinkPresent();

    /**
     * Method to select Edit Offline link
     *
     * @return {@link DocumentLibraryPage}
     */
    DocumentLibraryPage selectEditOffline();
    
    /**
     * Method to select Edit Offline link and close File Browse window
     *
     * @return {@link DocumentLibraryPage}
     */
    DocumentLibraryPage selectEditOfflineAndCloseFileWindow();
    
    /**
     * Method to select Cancel Editing link
     *
     * @return {@link DocumentLibraryPage}
     */
    DocumentLibraryPage selectCancelEditing();
    
    
    /**
     * Method to select Locate File link
     *
     */
    void selectLocateFile();

    /**
     * Returns whether the file is being edited
     *
     * @return boolean
     */
    boolean isEdited();

    /**
     * Mimics the action of select the manage aspects.
     *
     * @return {@link SelectAspectsPage}
     */
    SelectAspectsPage selectManageAspects();

    /**
     * Check if comment link is present.
     *
     * @return boolean
     */
    boolean isCommentLinkPresent();

    /**
     * Performs the find with an added resolveStaleness.
     * If we encounter the staleness exception we refresh the web
     * element we are working with and re-do the search.
     *
     * @return {@link String}
     */
    String getLikeOrUnlikeTip();

    /**
     * Checks if quick share link present
     *
     * @return boolean
     */
    boolean isShareLinkVisible();

    /**
     * Checks if the Like option is visible
     *
     * @return {boolean} true if Like option is visible
     */
    boolean isLikeVisible();

    /**
     * Checks if the Favorite option is visible
     *
     * @return {boolean} true if Favorite option is visible
     */
    boolean isFavoriteVisible();

    /**
     * Checks if view in browser icon is visible.
     *
     * @return boolean
     */
    boolean isViewInBrowserVisible();



    /**
     * Hovers over the edit icon and clicks the edit button.
     */
    void contentNameEnableEdit();

    /**
     * Enters the new content name in the box
     *
     * @param newContentName String
     */
    void contentNameEnter(String newContentName);

    /**
     * Clicks the save link for content name editing.
     */
    void contentNameClickSave();

    /**
     * Clicks the cancel link for content name editing.
     */
    void contentNameClickCancel();

    /**
     * Hovers over the edit icon and clicks the edit button.
     * Enters the new content name in the box.
     * Clicks the save link for content name editing.
     */
    void renameContent(String newContentName);

    /**
     * Returns the modified date string as displayed on-screen.
     *
     * @return String
     */
    String getModified();

    /**
     * Clicks on the modifier's name
     *
     * @return If the current user is the modifier then {@link MyProfilePage} otherwise {@link UserProfilePage}
     * @throws UnsupportedOperationException if this operation is not supported in the current view.
     */
    HtmlPage selectModifier();

    /**
     * Gets the modifier's name as displayed on-screen.
     *
     * @return The modifier's name
     * @throws UnsupportedOperationException if this operation is not supported in the current view.
     */
    String getModifier();

    /**
     * Returns the created date as displayed on-screen.
     *
     * @return String
     * @throws UnsupportedOperationException if this operation is not supported in the current view.
     */
    String getCreated();

    /**
     * Clicks on the creator's name.
     *
     * @return If the current user is the creator then {@link MyProfilePage} otherwise {@link UserProfilePage}
     * @throws UnsupportedOperationException if this operation is not supported in the current view.
     */
    HtmlPage selectCreator();

    /**
     * Gets the creator's name as displayed on screen.
     *
     * @return The creator's name
     * @throws UnsupportedOperationException if this operation is not supported in the current view.
     */
    String getCreator();

    /**
     * This method is used to get the File or Foder height in document library page.
     *
     * @return double
     */
    double getFileOrFolderHeight();

    /**
     * Click on quick share link.
     *
     * @return HtmlPage
     */
    HtmlPage clickShareLink();

    /**
     * This method verifies that info popup is displayed or not.
     */
    boolean isInfoPopUpDisplayed();

    /**
     * This method used to click the info icon present on file or folder.
     */
    void clickInfoIcon();

    /**
     * Checks to see if file is visible on the page.
     *
     * @return true if file exists on the page
     */
    boolean isInfoIconVisible();

    /**
     * This method is used to click on more link, on file directory info.
     */
    void selectMoreLink();

    /**
     * This method does the clicking event and opens the coments section from doclib page.
     *
     * @return HtmlPage
     */
    HtmlPage clickCommentsLink();

    /**
     * This method gets the comments tooltip from doclib page.
     *
     * @return String
     */
    String getCommentsToolTip();

    /**
     * This method gets the comments count from doclib page.
     *
     * @return int
     */
    int getCommentsCount();

    /**
     * This method clicks on category Name link.
     *
     * @param categoryName String
     * @return {@link DocumentLibraryPage}
     */
    HtmlPage clickOnCategoryNameLink(String categoryName);

    /**
     * This method retrieves the version of content.
     */
    public String getVersionInfo();

    /**
     * Checks to see if version is visible on the page.
     *
     * @return boolean
     */
    public boolean isVersionVisible();

    /**
     * Checks to see if checkbox is visible on the page.
     *
     * @return boolean
     */
    public boolean isCheckBoxVisible();

    /**
     * Gets the name of content from info menu.
     *
     * @return boolean
     */
    public String getContentNameFromInfoMenu();

    /**
     * Clicks on the name link present on Info menu of Gallery or FilmStrip view.
     *
     * @return HtmlPage
     */
    HtmlPage clickContentNameFromInfoMenu();

    /**
     * This method return url for PreviewImage.
     *
     * @return Url
     */
    String getPreViewUrl();

    /**
     * Gets the Like option tool tip on the select data row on DocumentLibrary Page.
     */
    public String getFavouriteOrUnFavouriteTip();

    /**
     * Check if the file is shared.
     *
     * @return boolean
     */
    boolean isFileShared();    

    /**
     * Check if the save link is visible.
     */
    public boolean isSaveLinkVisible();

    /**
     * Check if the Cancel link is visible.
     */
    public boolean isCancelLinkVisible();

    /**
     * Method to check if View Original Document Link is displayed or not
     *
     * @return true if visible on the page
     */
    boolean isViewOriginalLinkPresent();

    /**
     * Method to select View Original Document link
     *
     * @return {@link DocumentLibraryPage}
     */
    HtmlPage selectViewOriginalDocument();
      
    /**
     * Return the URL of the thumbnail
     * @return String
     */
    public String getThumbnailURL();
    public void declareRecord();
    public boolean isTypeRecord();
    boolean isDownloadPresent();
    boolean isMoreMenuButtonPresent();
    boolean isTagsFieldPresent();
    List<String> getDescriptionList();
    String getDescriptionFromInfo();

    /***
     * 
     * This method clicks on Preview Web Asset for the selected document
     */
    public void selectPreviewWebAsset();
    boolean isModelInfoPresent();
    boolean isModelActive();
    public String getModelName();
    public String getModelDesription();
//    public CopyOrMoveContentPage selectCopyToOnFolderCloud();
}
