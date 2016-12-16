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

package org.alfresco.po.share.steps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteContentDashlet;
import org.alfresco.po.share.dashlet.MyActivitiesDashlet.LinkType;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.site.AddGroupsPage;
import org.alfresco.po.share.site.AddUsersToSitePage;
import org.alfresco.po.share.site.ConfirmRequestToJoinPopUp;
import org.alfresco.po.share.site.CreateSitePage;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.po.share.site.UploadFilePage;
import org.alfresco.po.share.site.document.ChangeTypePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ConfirmDeletePage.Action;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.ACTION;
import org.alfresco.po.share.site.document.CopyOrMoveContentPage.DESTINATION;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DetailsPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.FileDirectoryInfo;
import org.alfresco.po.share.site.document.SelectAspectsPage;
import org.alfresco.po.share.site.document.ShareLinkPage;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.enums.ActivityType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;


/**
 * Share actions - All the common steps of site action
 * 
 * @author sprasanna
 * @author mbhave
 * @author charu
 */
@Component
public class SiteActions extends CommonActions
{
    private static Log logger = LogFactory.getLog(SiteActions.class);
    public static long refreshDuration = 25000;
    final static String SITE_VISIBILITY_PUBLIC = "public";
    protected static final String SITE_VISIBILITY_PRIVATE = "private";
    protected static final String SITE_VISIBILITY_MODERATED = "moderated";
    public final static String DOCLIB = "DocumentLibrary";
    protected static final String UNIQUE_TESTDATA_STRING = "sync";
    private static final String SITE_DASH_LOCATION_SUFFIX = "/page/site/";
    /**
     * Create site
     */
    public boolean createSite(WebDriver driver, final String siteName, String desc, String siteVisibility)
    {
        if (siteName == null || siteName.isEmpty())
        {
            throw new IllegalArgumentException("site name is required");
        }
        boolean siteCreated = false;
        DashBoardPage dashBoard;
        SiteDashboardPage site = null;
        try
        {
            SharePage page = factoryPage.getPage(driver).render();
            dashBoard = page.getNav().selectMyDashBoard().render();
            CreateSitePage createSite = dashBoard.getNav().selectCreateSite().render();
            if (siteVisibility == null)
            {
                siteVisibility = SITE_VISIBILITY_PUBLIC;
            }
            if (siteVisibility.equalsIgnoreCase(SITE_VISIBILITY_MODERATED))
            {
                site = createSite.createModerateSite(siteName, desc).render();
            }
            else if (siteVisibility.equalsIgnoreCase(SITE_VISIBILITY_PRIVATE))
            {
                site = createSite.createPrivateSite(siteName, desc).render();
            }
            // Will create public site
            else
            {
                site = createSite.createNewSite(siteName, desc).render();
            }

            site = site.render();

            if (siteName.equalsIgnoreCase(site.getPageTitle()))
            {
                siteCreated = true;
            }
            return siteCreated;
        }
        catch (UnsupportedOperationException une)
        {
            String msg = String.format("Failed to create a new site %n Site Name: %s", siteName);
            throw new RuntimeException(msg, une);
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Document Library Page.
     * Assumes User is logged in and a specific Site is open.
     * 
     * @param driver WebDriver Instance
     * @param folderName String Name of the folder to be created
     * @param folderTitle String Title of the folder to be created
     * @param folderDesc String Description of the folder to be created
     * @return DocumentLibraryPage
     */
    public DocumentLibraryPage createFolder(WebDriver driver, String folderName, String folderTitle, String folderDesc)
    {
        DocumentLibraryPage docPage = null;

        // Open Document Library
        SharePage thisPage = getSharePage(driver);

        if (!(thisPage instanceof DocumentLibraryPage))
        {
            throw new PageOperationException("the current page is not documentlibrary page");
        }
        else
        {
            docPage = (DocumentLibraryPage) thisPage;
        }

        NewFolderPage newFolderPage = docPage.getNavigation().selectCreateNewFolder().render();
        docPage = newFolderPage.createNewFolder(folderName, folderTitle, folderDesc).render();

        logger.info("Folder Created" + folderName);
        return docPage;
    }

    /**
     * Open document Library: Top Level Assumes User is logged in and a Specific
     * Site is open.
     *
     * @param driver WebDriver Instance
     * @return DocumentLibraryPage
     */
    public DocumentLibraryPage openDocumentLibrary(WebDriver driver)
    {
        // Assumes User is logged in
        /*
         * SharePage page = getSharePage(driver); if (page instanceof
         * DocumentLibraryPage) { return (DocumentLibraryPage) page; }
         */

        // Open DocumentLibrary Page from Site Page
        SitePage site = factoryPage.getPage(driver).render();

        DocumentLibraryPage docPage = site.getSiteNav().selectDocumentLibrary().render();
        logger.info("Opened Document Library");
        return docPage;
    }

    /**
     * Assumes a specific Site is open Opens the Document Library Page and navigates to the Path specified.
     * 
     * @param driver WebDriver Instance
     * @param folderPath  String folder path relative to DocumentLibrary e.g. DOCLIB + file.seperator + folderName1
     * @throws ShareException if error in this API
     */
    public DocumentLibraryPage navigateToFolder(WebDriver driver, String folderPath) throws ShareException
    {
        DocumentLibraryPage docPage;

        try
        {
            if (folderPath == null)
            {
                throw new UnsupportedOperationException("Incorrect FolderPath: Null");
            }

            // check whether we are in the document libary page
            SharePage thisPage = getSharePage(driver);

            if (!(thisPage instanceof DocumentLibraryPage))
            {
                throw new PageOperationException("the current page is not documentlibrary page");
            }
            else
            {
                docPage = (DocumentLibraryPage) thisPage;
            }

            // Resolve folderPath, considering diff treatment for non-windows OS
            logger.info(folderPath);
            String[] path = folderPath.split(Pattern.quote(File.separator));

            // Navigate to the parent Folder where the file needs to be uploaded
            for (int i = 0; i < path.length; i++)
            {
                if (path[i].isEmpty())
                {
                    // Ignore, Continue to the next;
                    logger.debug("Empty Folder Path specified: " + path.toString());
                }
                else
                {
                    if ((i == 0) && (path[i].equalsIgnoreCase(DOCLIB)))
                    {
                        // Repo or Doclib is already open
                        logger.info("Base Folder: " + path[i]);
                    }
                    else
                    {
                        logger.info("Navigating to Folder: " + path[i]);
                        docPage = selectContent(driver, path[i]).render();
                    }
                }
            }
            logger.info("Selected Folder:" + folderPath);
        }
        catch (Exception e)
        {
            throw new ShareException("Skip test. Error in navigateToFolder: " + e.getMessage());
        }

        return docPage;
    }

    /**
     * Util traverses through all the pages of the doclib to find the content within the folder and clicks on the contentTile
     * 
     * @param driver
     * @param contentName
     * @return
     */
    public HtmlPage selectContent(WebDriver driver, String contentName)
    {
        return getFileDirectoryInfo(driver, contentName).clickOnTitle().render();
    }
    
    /**
     * Util returns the DetailsPage for the selected content
     * 
     * @param driver
     * @param contentName
     * @return DetailsPage
     */
    public FileDirectoryInfo getFileDirectoryInfo(WebDriver driver, String contentName)
    {
        Boolean moreResultPages = true;
        FileDirectoryInfo contentRow = null;
        DocumentLibraryPage docLibPage = getSharePage(driver).render();

        // Start from first page
        while (docLibPage.hasPreviousPage())
        {
            docLibPage = docLibPage.selectPreviousPage().render();
        }

        while (moreResultPages)
        {
            // Get Search Results
            try
            {
                contentRow = docLibPage.getFileDirectoryInfo(contentName);
                break;
            }
            catch (PageException pe)
            {
                // Check next Page if available
                moreResultPages = docLibPage.hasNextPage();

                if (moreResultPages)
                {
                    docLibPage = docLibPage.selectNextPage().render();
                }
            }
        }

        // Now return the content found else throw PageException
        if (contentRow == null)
        {
            throw new PageException(String.format("File directory info with title %s was not found in the selected folder", contentName));
        }

        return contentRow;
    }

    /**
     * Creates a new folder at the Path specified, Starting from the Document
     * Library Page. Assumes User is logged in and a specific Site is open.
     *
     * @param driver WebDriver Instance
     * @param folderName String Name of the folder to be created
     * @param folderDesc String Description of the folder to be created
     * @param parentFolderPath String Path for the folder to be created, under
     *            DocumentLibrary : such as constDoclib + file.seperator +
     *            parentFolderName1 + file.seperator + parentFolderName2
     * @throws Exception
     */
    public DocumentLibraryPage createFolderInFolder(WebDriver driver, String folderName, String folderDesc, String folderTitle, String parentFolderPath)
            throws Exception
    {
        try
        {
            // Data setup Options: Use UI, Use API, Copy, Data preloaded?

            // Using Share UI
            // Navigate to the parent Folder where the file needs to be uploaded
            navigateToFolder(driver, parentFolderPath);

            // Create Folder
            return createFolder(driver, folderName, folderTitle, folderDesc);
        }
        catch (Exception ex)
        {
            throw new ShareException("Skip test. Error in Create Folder: " + ex.getMessage());
        }
    }

    /**
     * Assumes User is logged in and a specific Site's Doclib is open, Parent Folder is pre-selected.
     * 
     * @param file File Object for the file in reference
     * @return DocumentLibraryPage
     */
    public HtmlPage uploadFile(WebDriver driver, File file)
    {
        DocumentLibraryPage docPage;
        try
        {
            checkIfDriverIsNull(driver);
            docPage = factoryPage.getPage(driver).render();
            // Upload File
            UploadFilePage upLoadPage = docPage.getNavigation().selectFileUpload().render();
            docPage = upLoadPage.uploadFile(file.getCanonicalPath()).render();
            docPage.setContentName(file.getName());
            logger.info("File Uploaded:" + file.getCanonicalPath());
        }
        catch (Exception e)
        {
            throw new ShareException("Skip test. Error in UploadFile: " + e);
        }

        return docPage.render();
    }

    /**
     * This method is used to create content with name, title and description.
     * User should be logged in and present on site page.
     *
     * @param driver
     * @param contentDetails
     * @param contentType
     * @return {@link DocumentLibraryPage}
     * @throws Exception
     */
    public DocumentLibraryPage createContent(WebDriver driver, ContentDetails contentDetails, ContentType contentType) throws Exception
    {
        // Open Document Library
        DocumentLibraryPage documentLibPage = factoryPage.getPage(driver).render();
        DocumentDetailsPage detailsPage = null;

        try
        {
            CreatePlainTextContentPage contentPage = documentLibPage.getNavigation().selectCreateContent(contentType).render();
            detailsPage = contentPage.create(contentDetails).render();
            documentLibPage = (DocumentLibraryPage) detailsPage.getSiteNav().selectDocumentLibrary();
            documentLibPage.render();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new ShareException("Error in creating content." + e);
        }

        return documentLibPage;
    }

    /**
     * isFileVisible is to check whether file or folder visible..
     * 
     * @param driver
     * @param contentName
     * @return
     */
    public boolean isFileVisible(WebDriver driver, String contentName)
    {
        try
        {
            getFileDirectoryInfo(driver, contentName);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Open Site and then Open Document Library Assumes User is logged in and a
     * Specific Site Dashboard is open.
     *
     * @param driver WebDriver Instance
     * @param siteName String Name of the Site
     * @return DocumentLibraryPage
     */
    public DocumentLibraryPage openSitesDocumentLibrary(WebDriver driver, String siteName)
    {
        // Assumes User is logged in

        // Checking for site doc lib to be open.
        HtmlPage page = getSharePage(driver).render();
        if (page instanceof DocumentLibraryPage)
        {
        	DocumentLibraryPage doclibPage = page.render();
            if (doclibPage.isSite(siteName) && doclibPage.isDocumentLibrary())
            {
                logger.info("Site doc lib page open ");
                return doclibPage;
            }
        }

        // Open Site
        openSiteDashboard(driver, siteName);

        // Open DocumentLibrary Page from SiteDashBoard
        DocumentLibraryPage docPage = openDocumentLibrary(driver);

        // Return DocLib Page
        return docPage;
    }

    /**
     * From the User DashBoard, navigate to the Site DashBoard and waits for the
     * page render to complete. Assumes User is logged in.
     *
     * @param driver WebDriver Instance
     * @param siteName String Name of the site to be opened
     * @return SiteDashboardPage
     * @throws PageException
     */
    public SiteDashboardPage openSiteDashboard(WebDriver driver, String siteName) throws PageException
    {
        // Assumes User is logged in
        HtmlPage page = getSharePage(driver).render();

        // Check if site dashboard is already open. Return
        if (page instanceof SiteDashboardPage)
        {
            if (((SiteDashboardPage) page).isSite(siteName))
            {
                logger.info("Site dashboad page already open for site - " + siteName);
                return page.render();
            }
        }

        // Open User DashBoard: Using SiteURL
        SiteDashboardPage siteDashPage = openSiteURL(driver, getSiteShortname(siteName));

        // Open User DashBoard: Using SiteFinder
        // SiteDashboardPage siteDashPage = SiteUtil.openSiteFromSearch(driver, siteName);

        // logger.info("Opened Site Dashboard using SiteURL: " + siteName);

        return siteDashPage;
    }

    /**
     * Method to navigate to site dashboard url, based on siteshorturl, rather than sitename
     * This is to be used to navigate only as a util, not to test getting to the site dashboard
     * 
     * @param driver
     * @param siteShortURL
     * @return {@link org.alfresco.po.share.site.SiteDashboardPage}
     */
    public SiteDashboardPage openSiteURL(WebDriver driver, String siteShortURL)
    {
        String url = driver.getCurrentUrl();
        String target = url.substring(0, url.indexOf("/page/")) + SITE_DASH_LOCATION_SUFFIX + getSiteShortname(siteShortURL) + "/dashboard";
        driver.navigate().to(target);
        SiteDashboardPage siteDashboardPage = getSharePage(driver).render();

        return siteDashboardPage.render();
    }

    /**
     * Helper to consistently get the Site Short Name.
     *
     * @param siteName String Name of the test for uniquely identifying / mapping
     *            test data with the test
     * @return String site short name
     */
    public String getSiteShortname(String siteName)
    {
        String siteShortname = "";
        String[] unallowedCharacters = { "_", "!" };

        for (String removeChar : unallowedCharacters)
        {
            siteShortname = siteName.replace(removeChar, "");
        }

        return siteShortname;
    }

    /**
     * Helper to create a new file, empty or with specified contents if one does
     * not exist. Logs if File already exists
     *
     * @param filename String Complete path of the file to be created
     * @param contents String Contents for text file
     * @return File
     */
    public File newFile(String filename, String contents)
    {
        File file = new File(filename);

        try
        {
            if (!file.exists())
            {

                if (!contents.isEmpty())
                {
                    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
                    writer.write(contents);
                    writer.close();
                }
                else
                {
                    file.createNewFile();
                }
            }
            else
            {
                logger.debug("Filename already exists: " + filename);
            }
        }
        catch (IOException ex)
        {
            logger.error("Unable to create sample file", ex);
        }
        return file;
    }

    /**
     * Util to download a file in a particular path
     */

    public void shareDownloadFileFromDocLib(WebDriver driver, String FileName, String path)
    {
        FileDirectoryInfo fileInfo = getFileDirectoryInfo(driver, FileName);
        fileInfo.selectDownload();
        DocumentLibraryPage docLib = factoryPage.getPage(driver).render();
        docLib.waitForFile(path);
    }

    /**
     * Delete content in share
     */
    public void deleteContentInDocLib(WebDriver driver, String contentName)
    {
        selectContentCheckBox(driver, contentName);
        DocumentLibraryPage doclib = deleteDocLibContents(driver);
        doclib.render();

    }

    /**
     * Checks the checkbox for a content if not selected on the document library
     * page.
     * 
     * @param driver
     * @param contentName
     * @return DocumentLibraryPage
     */
    private DocumentLibraryPage selectContentCheckBox(WebDriver driver, String contentName)
    {
        DocumentLibraryPage docLibPage = factoryPage.getPage(driver).render();
        if (!docLibPage.getFileDirectoryInfo(contentName).isCheckboxSelected())
        {
            docLibPage.getFileDirectoryInfo(contentName).selectCheckbox();
        }
        return docLibPage.render();
    }

    /**
     * Delete doc lib contents.
     * 
     * @param driver
     * @return
     */
    private DocumentLibraryPage deleteDocLibContents(WebDriver driver)
    {
        ConfirmDeletePage deletePage = ((DocumentLibraryPage) getSharePage(driver)).getNavigation().selectDelete();
        return deletePage.selectAction(Action.Delete).render();
    }

    /**
     * This method uploads the new version for the document with the given file
     * from data folder. User should be on Document details page.
     * 
     * @param fileName
     * @param driver
     * @return DocumentDetailsPage
     * @throws IOException
     */
    public void uploadNewVersionOfDocument(WebDriver driver, String title, String fileName, String comments) throws IOException
    {
        String fileContents = "New File being created via newFile:" + fileName;
        File newFileName = newFile(fileName, fileContents);
        DocumentLibraryPage doclib = (DocumentLibraryPage) factoryPage.getPage(driver);
        DocumentDetailsPage detailsPage = doclib.selectFile(title).render();
        UpdateFilePage updatePage = detailsPage.selectUploadNewVersion().render();
        updatePage.selectMajorVersionChange();
        updatePage.uploadFile(newFileName.getCanonicalPath());
        updatePage.setComment(comments);
        detailsPage = updatePage.submitUpload().render();
        detailsPage.selectDownload(null);
    }

    /**
     * Just get version number of the file
     */
    public String getVersionNumber(WebDriver driver, String title)
    {
        DocumentLibraryPage doclib = (DocumentLibraryPage) factoryPage.getPage(driver).render();
        DocumentDetailsPage detailsPage = doclib.selectFile(title).render();
        return detailsPage.getDocumentVersion();
    }

    /**
     * Get Version info from Document Library
     */
    public String getDocLibVersionInfo(WebDriver driver, String contentName)
    {
        FileDirectoryInfo fileInfo = getFileDirectoryInfo(driver, contentName);
        return fileInfo.getVersionInfo();
    }

    /**
     * Navigate to Document library
     */
    public HtmlPage navigateToDocumentLibrary(WebDriver driver, String siteName)
    {
        openSiteURL(driver, siteName); 
        return openDocumentLibrary(driver).render();
    }
    
    /**
     * Copy or Move to File or folder from document library.
     * 
     * @param driver WebDriver
     * @param destination String (options: Recent Sites, Favorite Sites, All Sites, Repository, Shared Files, My File)
     * @param siteName String - the siteName that exists in <destination>
     * @param siteDescription String - the siteDescription - IF THIS VALUE IS SET, THEN WE WILL SELECT THE SITE BY DESCRIPTION NOT BY <siteName>
     * @param fileName String
     * @return HtmlPage
     * @author pbrodner
     */
    public HtmlPage copyOrMoveArtifact(WebDriver driver, DESTINATION destination, String siteName, String siteDescription, String fileName, CopyOrMoveContentPage.ACTION action, String... moveFolderName)
    {
        DocumentLibraryPage docPage = getSharePage(driver).render();
        
        CopyOrMoveContentPage copyOrMoveToPage;

        // Select Copy or Move To Action
        if (action == ACTION.COPY || action == ACTION.CREATE_LINK) 
        {
            copyOrMoveToPage = docPage.getFileDirectoryInfo(fileName).selectCopyTo().render();
        }
        else
        {
            copyOrMoveToPage = docPage.getFileDirectoryInfo(fileName).selectMoveTo().render();
        }

        // Select <destination> if not already selected
        String active = copyOrMoveToPage.getSelectedDestination();
        if(!active.equals(destination.getValue())) 
        {
            copyOrMoveToPage.selectDestination(destination.getValue());	 
        }
        
        // Select Site
        if(destination.hasSites())
        {
            if (siteDescription!=null && !siteDescription.isEmpty())
            {
                copyOrMoveToPage.selectSiteByDescription(siteName, siteDescription).render();
    	    } 
    	    else
            {
                copyOrMoveToPage.selectSite(siteName).render();
    	    }        	
        }
        
        // Select Destination Path
        if (moveFolderName != null && moveFolderName.length > 0)
        {
            copyOrMoveToPage.selectPath(moveFolderName).render();
        }
        
        // Select Create Link or Default Option
        if (action == ACTION.CREATE_LINK)
        {
            copyOrMoveToPage.selectCreateLinkButton().render();
        }
        else
        {
            copyOrMoveToPage.selectOkButton().render();
        }
        
        return getSharePage(driver);
    }

    /**
     * Uses the in-line rename function to rename content
     * Assumes User is logged in and a DocumentLibraryPage of the selected site is open
     * 
     * @param driver
     * @param contentName
     * @param newName
     * @param saveChanges <code>true</code> saves the changes, <code>false</code> cancels without saving.
     * @return DocumentLibraryPage
     */
    public DocumentLibraryPage editContentNameInline(WebDriver driver, String contentName, String newName, boolean saveChanges)
    {
        FileDirectoryInfo fileDirInfo = getFileDirectoryInfo(driver, contentName);

        fileDirInfo.contentNameEnableEdit();
        fileDirInfo.contentNameEnter(newName);
        if (saveChanges)
        {
            fileDirInfo.contentNameClickSave();
        }
        else
        {
            fileDirInfo.contentNameClickCancel();
        }

        return getSharePage(driver).render();
    }
    
    /**
     * In the document library page select edit properties to set a new title , description or name for the content
     * Assume the user is logged in and a documentLibraryPage of the selected site is open
     * 
     * @author sprasanna
     * @param - Webdriver
     * @param - String contentName
     * @param - String newContentName
     * @param - String title
     * @param - String descirption
     */
    public DocumentLibraryPage editProperties(WebDriver driver, String contentName, String newContentName, String title, String description)
    {
        DocumentLibraryPage documentLibraryPage = factoryPage.getPage(driver).render();
        EditDocumentPropertiesPage editProp = documentLibraryPage.getFileDirectoryInfo(contentName).selectEditProperties().render();
        // Check the newContent is present
        if (newContentName != null)
        {
            editProp.setName(newContentName);
        }
        // Check the newContent is present
        if (title != null)
        {
            editProp.setDocumentTitle(title);
        }
        // Check the newContent is present
        if (description != null)
        {
            editProp.setDescription(description);
        }

        return editProp.selectSave().render();
    }
    
    /**
     * Util to Navigate to Edit Properties Page from DocLib or Details Page
     * 
     * @param driver
     * @param contentName
     * @return HtmlPage
     */
    public HtmlPage getEditPropertiesPage(WebDriver driver, String contentName)
    {
        PageUtils.checkMandatoryParam("Expected ContentName", contentName);

        try
        {
            SharePage sharePage = getSharePage(driver).render();

            // Get DetailsPage
            if (sharePage instanceof DocumentLibraryPage)
            {
                sharePage = selectContent(driver, contentName).render();
            }
            
            // Select EditPropertiesPage
            if (sharePage instanceof DetailsPage)
            {
                return ((DetailsPage) sharePage).selectEditProperties().render();
            }
            else if(sharePage instanceof EditDocumentPropertiesPage)
            {
                return sharePage;
            }
            else
            {
                throw new UnexpectedSharePageException("Expected Doclib or Details Page, if not already on EditDocumentPropertiesPage");
            }
        }
        catch (Exception e)
        {
            throw new PageException("Error getting EditDocumentPropertiesPage", e);
        }
    }
    
    /**
     * Util to change the type of the selected folder / content to the specified type 
     * Expects Document / Folder Details Page is already open
     */
    public DetailsPage changeType(WebDriver driver, String typeToBeSelected)
    {
        try
        {
            DetailsPage detailsPage = getSharePage(driver).render();
            return detailsPage.changeType(typeToBeSelected).render();       
        }
        catch(Exception e)
        {
            throw new PageException("Error During Change Type: " + typeToBeSelected, e);
        }
    }
    
    /**
     * Util to check if the type is available for selection in the <Change Type> drop down 
     * Expects Document / Folder Details Page is already open
     */
    public boolean isTypeAvailable(WebDriver driver, String typeToBeSelected)
    {
        boolean isType = false;
        try
        {
            DetailsPage detailsPage = getSharePage(driver).render();
        
            isType = detailsPage.isTypeAvailable(typeToBeSelected);
            ChangeTypePage typePopup = getSharePage(driver).render();
            typePopup.clickClose().render();            
        }
        catch (ClassCastException ce)
        {
            
        }
        return isType;
    }
    
    /**
     * Util to check if the specified Aspect is added to the selected node 
     * Expects Document / Folder Details Page is already open
     */
    public boolean isAspectAdded(WebDriver driver, String aspectName)
    {
    
        boolean aspectAdded = false;
        
        try
        {
            SelectAspectsPage aspectsPage = getAspectsPage(driver).render();
            aspectAdded = aspectsPage.isAspectAdded(aspectName);
            aspectsPage.clickCancel().render();
        }
        catch (ClassCastException | PageException e)
        {

        }

        return aspectAdded;
}
    
    /**
     * Util to add the list of aspects to the selected document / folder
     * @param driver
     * @param aspectsToBeAdded
     * @return DetailsPage
     */
    public DetailsPage addAspects(WebDriver driver, List<String> aspectsToBeAdded)
    {
        try
        {           
            SelectAspectsPage aspectsPage = getAspectsPage(driver);
            aspectsPage = aspectsPage.addDynamicAspects(aspectsToBeAdded).render(); 
            return aspectsPage.clickApplyChanges().render();
        }
        catch(PageException e)
        {
            throw new PageException("Error During Adding Aspect", e);
        }
    }
    
    /**
     * Util to remove the list of aspects from the selected document / folder
     * @param driver
     * @param aspectsToBeRemoved
     * @return DetailsPage
     */
    public DetailsPage removeAspects(WebDriver driver, List<String> aspectsToBeRemoved)
    {
        try
        {           
            SelectAspectsPage aspectsPage = getAspectsPage(driver);
            aspectsPage = aspectsPage.removeDynamicAspects(aspectsToBeRemoved).render(); 
            return aspectsPage.clickApplyChanges().render();
        }
        catch(PageException e)
        {
            throw new PageException("Error During Removing Aspect", e);
        }
    }
    
    /**
     * Util to return ManageAspectsPopup from DetailsPage
     * @param driver
     * @return SelectAspectsPage
     */
    public SelectAspectsPage getAspectsPage(WebDriver driver)
    {
        try
        {
            DetailsPage detailsPage = getSharePage(driver).render();
            return detailsPage.selectManageAspects().render();
        }
        catch(ClassCastException ce)
        {
            throw new UnexpectedSharePageException(DetailsPage.class, ce);
        }
        catch(PageException pe)
        {
            throw new PageException("Unable to select Manage Aspects", pe);
        }
    }
    
    /**
     * Util to Save or Cancel the Node Properties from Details Page.
     * 
     * @param driver
     * @param actionSaveOrCancel
     * @param contentName
     * @return HtmlPage
     */
    public HtmlPage editNodeProperties(WebDriver driver, boolean saveProperties, Map<String, Object> properties)
    {
        PageUtils.checkMandatoryParam("Expected Properties Map", properties);

        try
        {
            EditDocumentPropertiesPage editPropPage = null;
            
            SharePage sharePage = getSharePage(driver).render();

            if (sharePage instanceof SharePopup)
            {
                editPropPage = acknowledgeShareError(driver).render();
            }
            else
            {
                editPropPage = sharePage.render();
            }

            // Edit Properties
            editPropPage.setProperties(properties);

            // Save or Cancel
            if (saveProperties)
            {
                return editPropPage.selectSave().render();
            }
            else
            {
                return editPropPage.selectCancel().render();
            }
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException("Expected EditDocumentPropertiesPage Page", ce);
        }
    }
    
    /**
     * Util to Save the Node Properties from Details Page but error could be expected during save.
     * 
     * @param driver
     * @param properties Map<String, Object>
     * @return HtmlPage
     */
    public HtmlPage editNodePropertiesExpectError(WebDriver driver, Map<String, Object> properties)
    {
        PageUtils.checkMandatoryParam("Expected Properties Map", properties);
        try
        {
            EditDocumentPropertiesPage editPropPage = null;
            
            SharePage sharePage = getSharePage(driver).render();
            if (sharePage instanceof SharePopup)
            {
                editPropPage = acknowledgeShareError(driver).render();
            }
            else
            {
                editPropPage = sharePage.render();
            }

            // Edit Properties
            editPropPage.setProperties(properties);

            // Save
            return editPropPage.selectSaveExpectError();
        }
        catch (ClassCastException ce)
        {
            throw new UnexpectedSharePageException("Expected EditDocumentPropertiesPage Page", ce);
        }
    }


    public HtmlPage viewDetails(WebDriver driver, String name)
    {
        DocumentLibraryPage doclib = factoryPage.getPage(driver).render();
        FileDirectoryInfo row = doclib.getFileDirectoryInfo(name);
        if(row.isFolder())
        {
        	return row.selectViewFolderDetails();
        }
        return doclib.selectFile(name).render();
    }
    
    
    /**
     * Click ok / Default button if error is displayed
     * @param driver
     * @return HtmlPage
     */

    public HtmlPage acknowledgeShareError(WebDriver driver)
    {
        SharePage sharePage = getSharePage(driver).render();
        
        if (sharePage instanceof SharePopup)
        {
            return ((SharePopup) sharePage).clickOK();
        }
        return sharePage;
    }
    
    /**
     * Helper to search for an Activity Entry on the Site Dashboard Page, with configurable retry search option.
     * 
     * @param driver <WebDriver> instance
     * @param dashlet <String> Name of the Dashlet such as: activities,content,myDocuments etc
     * @param entry <String> Entry to look for within the Dashlet
     * @param entryPresent <String> Parameter to indicate should the entry be visible within the dashlet
     * @param siteName <String> Parameter to indicate the site name to open the site dashboard.
     * @param activityType <Enum> paramerter to indicate the activity type.
     * @return <Boolean>
     */
    public Boolean searchSiteDashBoardWithRetry(WebDriver driver, Dashlets dashlet, String entry, Boolean entryPresent, String siteName, ActivityType activityType)
    {
        Boolean found = false;
        Boolean resultAsExpected = false;

        List<ShareLink> shareLinkEntries = null;

        // Open Site DashBoard: Assumes User is logged in
        SiteDashboardPage siteDashBoard = openSiteDashboard(driver, siteName);

        // Repeat search until the element is found or Timeout is hit
        for (int searchCount = 1; searchCount <= retrySearchCount; searchCount++)
        {
            if (searchCount > 1)
            {
                // This below code is needed to wait for the solr indexing.
                webDriverWait(driver, refreshDuration);

                siteDashBoard = refreshSiteDashboard(driver);
            }

            if (dashlet.equals(Dashlets.SITE_ACTIVITIES) && ActivityType.DESCRIPTION.equals(activityType))
            {
                SiteActivitiesDashlet siteActivitiesDashlet = siteDashBoard.getDashlet(Dashlets.SITE_ACTIVITIES.getDashletName()).render();
                found = siteActivitiesDashlet.getSiteActivityDescriptions().contains(entry);
            }
            else
            {
                shareLinkEntries = getSiteDashletEntries(driver, dashlet, activityType);

                if (shareLinkEntries != null)
                {
                    found = findInList(shareLinkEntries, entry);
                }
            }

            // Loop again if result is not as expected: To cater for solr lag: eventual consistency
            resultAsExpected = (entryPresent.equals(found));
            if (resultAsExpected)
            {
                break;
            }
        }

        return resultAsExpected;
    }
    
    /**
     * Helper to search for an Element in the list of <ShareLinks>.
     * 
     * @param driver WebDriver Instance
     * @param dashlet String Name of the dashlet
     * @return List<ShareLink>: List of Share Links available in the dashlet
     */
    protected List<ShareLink> getSiteDashletEntries(WebDriver driver, Dashlets dashlet, ActivityType activityType)
    {
        List<ShareLink> entries = null;

        SiteDashboardPage siteDashBoard = getSharePage(driver).render();
        if (dashlet == null)
        {
            dashlet = Dashlets.SITE_CONTENT;
        }

        if (dashlet.equals(Dashlets.SITE_CONTENT))
        {
            SiteContentDashlet siteContentDashlet = siteDashBoard.getDashlet(dashlet.getDashletName()).render();
            entries = siteContentDashlet.getSiteContents();
        }
        else if (dashlet.equals(Dashlets.SITE_ACTIVITIES))
        {
            SiteActivitiesDashlet siteActivitiesDashlet = null;
            if (ActivityType.USER.equals(activityType))
            {
                siteActivitiesDashlet = siteDashBoard.getDashlet(dashlet.getDashletName()).render();
                entries = siteActivitiesDashlet.getSiteActivities(LinkType.User);
            }
            else if (ActivityType.DOCUMENT.equals(activityType))
            {
                siteActivitiesDashlet = siteDashBoard.getDashlet(dashlet.getDashletName()).render();
                entries = siteActivitiesDashlet.getSiteActivities(LinkType.Document);
            }           
        }

        return entries;
    }
    
    /**
     * Navigate to User DashBoard page and waits for the page render to
     * complete. Assumes User is logged in
     * 
     * @param driver WebDriver Instance
     * @return DashBoardPage
     */
    public SiteDashboardPage refreshSiteDashboard(WebDriver driver)
    {
        // Open DocumentLibrary Page from Site Page
        SitePage site = getSharePage(driver).render();

        logger.info("Opening Site Dashboard");
        return site.getSiteNav().selectSiteDashBoard().render();
    }
    
    /**
     * Utility to click on the share link for the specified content in the Site Doclib
     * @param WebDriver driver
     * @param String filename
     * @return {@link ShareLinkPage}
     */
    public HtmlPage shareFile(WebDriver driver, String filename)
    {
        FileDirectoryInfo thisRow = getFileDirectoryInfo(driver, filename);
        return thisRow.clickShareLink().render();
    }
    
    /**
     * Utility to navigate to specified link
     * @param {@link WebDriver} driver
     * @param {String} link
     * @return HtmlPage
     */
    public HtmlPage viewSharedLink(WebDriver driver, String link)
    {
        driver.navigate().to(link);

        return factoryPage.getPage(driver).render();

    }
    
    /**
     * Utility for requesting to join moderated site when user already logged in
     * @param  siteName 
     */
    public  HtmlPage requestToJoinModSite(WebDriver driver, String modSiteName)
    {
    	SiteDashboardPage siteDashboardPage = openSiteDashboard(driver, modSiteName).render(); 
    	SharePage sharePage = siteDashboardPage.requestToJoinSite().render();        
    	if (sharePage instanceof ConfirmRequestToJoinPopUp) 
    	{
    		 return ((ConfirmRequestToJoinPopUp) sharePage).selectOk();	
    	}
        return factoryPage.getPage(driver).render();
    }
    
    /**
     * Utility for cancel requesting to join a site when user already logged in
     * @param  siteName 
     */
    public  HtmlPage cancelRequestToJoinSite(WebDriver driver, String siteName)
    {
    	SiteDashboardPage siteDashboardPage = openSiteDashboard(driver, siteName).render(); 
    	siteDashboardPage.cancelRequestToJoinSite().render();
        return factoryPage.getPage(driver).render();
    }  
        
    /**
     * Utility for navigating to PendingRequset Page when user already logged in
     * @param  siteName     
     */
    public HtmlPage navigateToPendingRequestPage(WebDriver driver, String modSiteName)
    {
    	SiteDashboardPage siteDashboardPage = openSiteDashboard(driver, modSiteName).render();
    	SiteMembersPage siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage().render();
    	return siteMembersPage.navigateToPendingInvites().render();
    	
    } 
    
    /**
     * Utility for verify user role is as specified
     * @param {@link WebDriver} driver
     * @param userName
     * @param siteName
     * @return {@link Boolean} expectedRole    
     */
    public Boolean checkUserRole(WebDriver driver, String userName, String siteName, UserRole userRole, Boolean expectedRole )
    {
       // Open site dashboard
       SiteDashboardPage siteDashboardPage = openSiteDashboard(driver, siteName).render();

       // Verify user is a member of site with specified role
       SiteMembersPage siteMembersPage = siteDashboardPage.getSiteNav().selectMembersPage().render();
       return siteMembersPage.checkUserRole(userName, userRole);
    }
    
    /**
     * Utility to add group to site with any role, when user is on site dashboard
     * @param siteName
     * @param groupName
     * @param {@link WebDriver} driver
     * @return {@link Boolean} expectedRole    
     */
    
    public HtmlPage addGroupToSite(WebDriver driver, String siteName, String groupName, UserRole userRole)
    {
    	SiteDashboardPage siteDashboardPage = openSiteDashboard(driver, siteName).render();

        // Navigate to Add Users page
    	AddUsersToSitePage addUsersToSitePage = siteDashboardPage.getSiteNav().selectAddUser().render();
    	SiteGroupsPage siteGroupsPage = addUsersToSitePage.navigateToSiteGroupsPage().render();

        // Add groupName to site with any role
    	AddGroupsPage addGroupsPage = siteGroupsPage.navigateToAddGroupsPage().render();
        return addGroupsPage.addGroup(groupName, userRole).render();
        
    }
}
