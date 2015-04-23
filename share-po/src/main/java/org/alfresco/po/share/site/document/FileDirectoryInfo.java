package org.alfresco.po.share.site.document;

import org.alfresco.po.share.UserProfilePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.workflow.DestinationAndAssigneePage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.webdrone.HtmlPage;

import java.util.List;

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

    boolean isFolderType();

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
     * Gets the Tag Information of the file or directory, if none then 'No Tags'
     * string is returned.
     * 
     * @return List<String> List of tags added to the content
     */
    List<String> getTags();

    /**
     * Cets the list of inline tags after clicking on tag info icon
     * string is returned.
     * 
     * @return List<String> List of inline tags
     */
    List<String> getInlineTagsList();

    /**
     * Get the {@link List} of added {@link Categories}.
     * 
     * @return {@link List} of {@link Categories}
     * @depricated Use {@link #getCategories()} instead.
     */
    @Deprecated
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
     * Check if tags are attached to the selected content.
     * 
     * @return boolean <tt>true</tt> if content has one or more Tags
     */
    boolean hasTags();

    /**
     * Adds the specified Tag to the file or directory.
     * 
     * @param tagName String tag to be added
     */
    void addTag(String tagName);

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
     * Mimics the action of hovering over a tag until edit tag icon appears.
     */
    void clickOnAddTag();

    /**
     * This method gets the status whether given tagname remove button
     * has found or not.
     * 
     * @return boolean if icon is displayed
     */
    boolean removeTagButtonIsDisplayed(String tagName);

    /**
     * This method clicks on given tag name remove button.
     * 
     * @param tagName String tag name
     */
    void clickOnTagRemoveButton(String tagName);

    /**
     * This method is used to click on save button when editing a tag.
     */
    void clickOnTagSaveButton();

    /**
     * This method is used to click on cancel button when editing a tag.
     */
    void clickOnTagCancelButton();

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
     * Returns whether the file / dir is cloud synced.
     * 
     * @return
     */
    boolean isCloudSynced();

    /**
     * Returns whether the file / dir is part of workflow.
     * 
     * @return
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
     * This method clicks on tag Name link.
     * 
     * @param tagName
     * @return {@link DocumentLibraryPage}
     */
    HtmlPage clickOnTagNameLink(String tagName);

    /**
     * Selects the "Sync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     * 
     * @return {@link DestinationAndAssigneePage} response
     */
    HtmlPage selectSyncToCloud();

    /**
     * Selects the edit in google docs link
     * @deprecated google docs has been discontinued.
     * @return {@link DestinationAndAssigneePage} response
     */
    HtmlPage selectEditInGoogleDocs();

    /**
     * Returns true if Sign In To Alfresco Cloud popup opens (User haven't set up CloudSync)
     * 
     * @return boolean
     */
    boolean isSignUpDialogVisible();

    /**
     * Selects the "unSync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     * 
     * @return {@link DestinationAndAssigneePage} response
     */
    DocumentLibraryPage selectUnSyncAndRemoveContentFromCloud(boolean doRemoveContentOnCloud);

    /**
     * Selects the "Sync to Cloud" link on the select data row on
     * DocumentLibrary Page.
     * 
     * @return {@link DestinationAndAssigneePage} response
     */
    void selectUnSyncFromCloud();

    /**
     * Selects the "Force UnSync" link on the selected data row on
     * DocumentLibrary Page. Only applicable for Cloud environment
     * 
     * @return DocumentLibraryPage
     */
    DocumentLibraryPage selectForceUnSyncInCloud();

    /**
     * This method verifies the viewCloudSyncInfo link is present or not.
     * 
     * @return boolean
     */
    boolean isViewCloudSyncInfoLinkPresent();

    /**
     * This method clicks on the viewCloudSyncInfo link.
     * 
     * @return SyncInfoPage
     */
    SyncInfoPage clickOnViewCloudSyncInfo();

    /**
     * This method verifies if cloud info button is displayed
     * 
     * @return SyncInfoPage
     */
    boolean isViewCloudSyncInfoDisplayed();

    /**
     * This method verifies if cloud info button is displayed with failed
     * 
     * @return SyncInfoPage
     */
    boolean isCloudSyncFailed();

    /**
     * Selects the "Inline Edit" link on the select data row on
     * DocumentLibrary Page.
     * 
     * @return {@link InlineEditPage} response
     */
    HtmlPage selectInlineEdit();

    /**
     * This method clicks on the viewCloudSyncInfo link.
     * 
     * @return SyncInfoPage
     */
    String getCloudSyncType();

    /**
     * Retrieve content info (This document is locked by you., This document is locked by you for offline editing., Last sync failed.)
     * 
     * @return
     */
    String getContentInfo();

    /**
     * Method to check if the content is locked or not
     * 
     * @return
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
     * This method verifies the editInGoogleDocs link is present or not.
     * @deprecated google docs has been discontinued.
     * @return boolean
     */
    boolean isEditInGoogleDocsPresent();

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
     * Check "UnSync to Cloud" link on the select data row.
     * DocumentLibrary Page.
     * 
     * @return {@link DestinationAndAssigneePage} response
     * @author nshah
     */
    boolean isUnSyncFromCloudLinkPresent();

    /**
     * Verify if the Sync failed icon is displayed or not
     * 
     * @param waitTime
     * @return
     */
    boolean isSyncFailedIconPresent(long waitTime);

    /**
     * Verify if the Rule icon is displayed or not
     * 
     * @param waitTime
     * @return
     */
    boolean isRuleIconPresent(long waitTime);

    /**
     * Select the link Request Sync from
     * the actions drop down.
     */
    DocumentLibraryPage selectRequestSync();

    /**
     * Request to sync is present or not.
     * the actions drop down.
     */
    boolean isRequestToSyncLinkPresent();

    /**
     * Check "Sync to Cloud" link on the select data row.
     * DocumentLibrary Page.
     * 
     * @return
     * @author rmanyam
     */
    boolean isSyncToCloudLinkPresent();

    /**
     * select Manage permission link from more option of document library.
     * 
     * @return
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

    HtmlPage delete();

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
     * @return
     */
    boolean isManagePermissionLinkPresent();

    /**
     * check Edit properties link from more option of document library.
     * 
     * @return
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
     * @return {@link DocumentLibraryPage}
     */
    void selectLocateFile();

    /**
     * Returns whether the file is being edited
     * 
     * @return
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
     * Checks if view in browser icon is visible.
     * 
     * @return boolean
     */
    boolean isViewInBrowserVisible();

    /**
     * Sends the keys that needs to be entered for new tag.
     * 
     * @param tagName
     */
    void enterTagString(final String tagName);

    /**
     * Sends the keys that needs to be entered in inlineTagEdit input
     * 
     * @param keysToSend
     */
    void sendKeysToTagInput(CharSequence... keysToSend);

    /**
     * Checks if tag is highlighted in inlineTagEdit input
     * 
     * @param tagName
     */
    boolean isTagHighlightedOnEdit(final String tagName);

    /**
     * Click on tag in inlineTagEdit input
     * 
     * @param tagName
     */
    void clickTagOnEdit(final String tagName);

    /**
     * Hovers over the edit icon and clicks the edit button.
     */
    void contentNameEnableEdit();

    /**
     * Enters the new content name in the box
     * 
     * @param newContentName
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
     * @return
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
     * @return
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
     * @param categoryName
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
     * @return
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
    DocumentDetailsPage selectViewOriginalDocument();

    /**
     * Return the URL of the thumbnail
     * 
     * @return
     */
    public String getThumbnailURL();

    public void declareRecord();

    public boolean isTypeRecord();

    /**
     * This method verifies the Geolocation Metadata icon is present or not.
     * 
     * @return boolean
     */
    public boolean isGeoLocationIconDisplayed();

    /**
     * This method verifies the EXIF icon is present or not.
     * 
     * @return boolean
     */
    public boolean isEXIFIconDisplayed();

    boolean isDownloadPresent();

    boolean isMoreMenuButtonPresent();

    boolean isTagsFieldPresent();

    List<String> getDescriptionList();

    String getDescriptionFromInfo();

    /***
     * This method clicks on Preview Web Asset for the selected document
     */
    public void selectPreviewWebAsset();

    public boolean isIndirectlySyncedIconPresent();

    public String getSyncInfoToolTip();

    /**
     * @deprecated google docs has been discontinued.
     * @return
     */
    public GoogleDocCheckInPage selectCheckInGoogleDoc();

    /**
     * @deprecated google docs has been discontinued.
     * @return
     */
    public DocumentLibraryPage selectCancelEditingInGoogleDocs();

    boolean isModelInfoPresent();

    boolean isModelActive();

    public String getModelName();

    public String getModelDesription();

    CopyOrMoveContentPage selectCopyToOnFolderCloud();

}