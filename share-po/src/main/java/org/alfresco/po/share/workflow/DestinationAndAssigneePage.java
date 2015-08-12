package org.alfresco.po.share.workflow;

/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page relating to the verify Destination And Assignee page load.
 * 
 * @author Siva Kaliyappan, Ranjith Manyam
 * @since 1.6.2
 */
public class DestinationAndAssigneePage extends SharePage
{

    private static Log logger = LogFactory.getLog(DestinationAndAssigneePage.class);

    private static final By SELECT_SITE = By.cssSelector("div>a>h4");
    private static final By SITE_ELEMENTS = By.cssSelector("div[id$='cloud-folder-sitePicker']>div");
    private static final By NETWORK = By.cssSelector("button[id*='-cloud-folder-network-']");
    private static final By SUBMIT_SYNC_BUTTON = By.cssSelector("button[id$='cloud-folder-ok-button']");
    private static final By FOLDER_LABELS = By.cssSelector("div[id$='-cloud-folder-treeview'] [id^='ygtvlabel']");
    private static final By INCLUDE_SUB_FOLDER = By.cssSelector("input[id$='includeSubFolders']");
    private static final By LOCK_ON_PREM = By.cssSelector("input[id $='lockSourceCopy']");
    private static final By BUTTON_CANCEL = By.cssSelector("button[id$='-cloud-folder-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector("div[id$='-cloud-folder-dialog']>a.container-close");
    private static final By CREATE_NEW_FOLDER_ICON = By.cssSelector("div.cloud-path-add-folder");

    private final RenderElement networkRenderElement = getVisibleRenderElement(NETWORK);
    private final RenderElement siteRenderElement = getVisibleRenderElement(SITE_ELEMENTS);
    private final RenderElement folderPathElement = getVisibleRenderElement(By.cssSelector("div[id$='-cloud-folder-treeview']>div[class^='ygtvitem']"));
    private final RenderElement submitSyncButtonElement = getVisibleRenderElement(SUBMIT_SYNC_BUTTON);
    private final RenderElement cancelButtonElement = getVisibleRenderElement(BUTTON_CANCEL);
    private final RenderElement createNewFolderIconElement = getVisibleRenderElement(CREATE_NEW_FOLDER_ICON);
    private final RenderElement headerElement = getVisibleRenderElement(By.cssSelector(".last>a>h4"));
    private final RenderElement closeButtonElement = getVisibleRenderElement(CLOSE_BUTTON);

    private static final long FOLDER_LOAD_TIME = 2000;


    @SuppressWarnings("unchecked")
    @Override
    public DestinationAndAssigneePage render(RenderTime timer)
    {
        elementRender(timer, headerElement, networkRenderElement, siteRenderElement, folderPathElement, submitSyncButtonElement, cancelButtonElement,
                createNewFolderIconElement, closeButtonElement);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DestinationAndAssigneePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to select the siteName
     * 
     * @param siteName String
     */
    public void selectSite(String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site Name can't be null or empty.");
        }
        try
        {
            List<WebElement> availableElements = findAndWaitForElements(SITE_ELEMENTS);
            if (!CollectionUtils.isEmpty(availableElements))
            {
                for (WebElement webElement : availableElements)
                {
                    WebElement siteLink = webElement.findElement(SELECT_SITE);
                    if (siteLink.getText().equals(siteName))
                    {
                        if (!siteLink.isDisplayed())
                        {
                            siteLink.click();
                        }
                        siteLink.click();
                        if (logger.isTraceEnabled())
                        {
                            logger.trace("Site " + siteName + " selected");
                        }
                        // waitUntilElementDeletedFromDom(By.cssSelector("div[id$='default-cloud-folder-treeview'] td[class='ygtvcell ygtvloading']"),
                        // SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        // waitForElement(FOLDER_LABELS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        try
                        {
                            waitForElement(By.id("AlfrescoWebdriverz1"), SECONDS.convert(FOLDER_LOAD_TIME, MILLISECONDS));
                        }
                        catch (TimeoutException e)
                        {
                        }
                        // driver.waitFor(FOLDER_LOAD_TIME);
                        return;
                    }
                }
            }
        }
        catch (TimeoutException exception)
        {
            logger.error("Time out while finding elements!!", exception);
        }
        throw new PageException("could not found Site name:" + siteName);
    }

    /**
     * Returns the title
     */
    public String getSyncToCloudTitle()
    {
        return driver.findElement(By.cssSelector("div[id$='-cloud-folder-title']")).getText();
    }

    /**
     * Method to check the given folder's permissions
     * 
     * @param folderName String
     * @return true if the given folder class is set to ".no-permission"
     */
    public boolean isSyncPermitted(String folderName)
    {
        if (StringUtils.isEmpty(folderName))
        {
            throw new IllegalArgumentException("Folder Name can't be null or empty.");
        }

        try
        {
            List<WebElement> folderList = getFoldersList();

            for (WebElement element : folderList)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("FolderName: " + element.getText());
                }
                if (element.getText().equals(folderName))
                {
                    return !element.getAttribute("class").equals("no-permission");
                }
            }

        }
        catch (TimeoutException e)
        {
        }

        if (logger.isTraceEnabled())
        {
            logger.trace("Folder \"" + folderName + "\" is NOT displayed");
        }

        throw new PageOperationException("Folder " + folderName + " not found");
    }

    /**
     * Helper method to get all folder web elements
     * 
     * @return List<WebElement>
     */
    private List<WebElement> getFoldersList()
    {
        List<WebElement> webElements = findAndWaitForElements(FOLDER_LABELS);
        try
        {
            webElements.addAll(driver.findElements(By.cssSelector("div[id$='default-cloud-folder-treeview'] .no-permission")));
        }
        catch (NoSuchElementException nse)
        {
        }
        return webElements;
    }

    /**
     * Method to check if given site exists or not.
     * 
     * @param siteName String
     * @return True if site displayed
     */
    public boolean isSiteDisplayed(String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site Name Empty or Null.");
        }
        try
        {
            List<WebElement> availableSites = findAndWaitForElements(SITE_ELEMENTS);
            if (availableSites != null && !availableSites.isEmpty())
            {
                for (WebElement site : availableSites)
                {
                    if (siteName.equalsIgnoreCase(site.getText()))
                    {
                        return true;
                    }
                }
            }
        }
        catch (TimeoutException exception)
        {
        }

        return false;
    }

    /**
     * Method to check if given Network exists or not.
     * 
     * @param networkName String
     * @return True if Network displayed
     */
    public boolean isNetworkDisplayed(String networkName)
    {
        if (StringUtils.isEmpty(networkName))
        {
            throw new IllegalArgumentException("NetWork Name Empty or Null.");
        }
        try
        {
            List<WebElement> availableNetworks = findAndWaitForElements(NETWORK);
            if (availableNetworks != null && !availableNetworks.isEmpty())
            {
                for (WebElement network : availableNetworks)
                {
                    if (networkName.equalsIgnoreCase(network.getText()))
                    {
                        if (logger.isTraceEnabled())
                        {
                            logger.trace("Network is: " + network);
                        }
                        return true;
                    }
                }
            }
        }
        catch (TimeoutException exception)
        {
        }
        return false;
    }

    /**
     * Method to check if given folder exists or not
     * 
     * @param folderName String
     * @return True if a specified folder displayed
     */
    public boolean isFolderDisplayed(String folderName)
    {
        if (StringUtils.isEmpty(folderName))
        {
            throw new IllegalArgumentException("Folder Name can't be null or empty.");
        }
        try
        {
            List<WebElement> folderNames = getFoldersList();
            for (WebElement webElement : folderNames)
            {
                if (folderName.equals(webElement.getText()))
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("Folder \"" + folderName + "\" is displayed");
                    }
                    return true;
                }
            }
        }
        catch (TimeoutException e)
        {
        }
        if (logger.isTraceEnabled())
        {
            logger.trace("Folder \"" + folderName + "\" is NOT displayed");
        }
        return false;
    }

    /**
     * Method to select the given Network
     * 
     * @param network - The Network Name to be selected.
     */
    public void selectNetwork(String network)
    {
        if (StringUtils.isEmpty(network))
        {
            throw new IllegalArgumentException("Network can't null or empty.");
        }

        List<WebElement> availableNetworks = null;
        try
        {
            availableNetworks = findAndWaitForElements(NETWORK);
        }
        catch (TimeoutException exception)
        {
        }
        if (availableNetworks != null && !availableNetworks.isEmpty())
        {
            for (WebElement webElement : availableNetworks)
            {
                if (network.equals(webElement.getText()))
                {
                    webElement.click();
                    waitUntilElementClickable(SITE_ELEMENTS, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    return;
                }
            }
        }
        throw new PageOperationException("could not found Network:" + network);
    }

    /**
     * Un-select Include sub folder check box.
     */
    public void unSelectIncludeSubFolders()
    {
        try
        {
            findAndWait(INCLUDE_SUB_FOLDER).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out loading :" + INCLUDE_SUB_FOLDER, toe);
        }
    }

    /**
     * @return boolean
     */
    public boolean isIncludeSubFoldersSelected()
    {
        try
        {
            return findAndWait(INCLUDE_SUB_FOLDER).isSelected();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out loading :" + INCLUDE_SUB_FOLDER, toe);
            }
        }
        return false;
    }

    /**
     * Select "Lock (on-premise) copy".
     */
    public void selectLockOnPremCopy()
    {
        try
        {
            findAndWait(LOCK_ON_PREM).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out loading :" + LOCK_ON_PREM);
        }
    }

    /**
     * Check Lock (On-Premise) copy selected.
     * 
     * @return boolean
     */
    public boolean isLockOnPremCopy()
    {
        try
        {
            return findAndWait(LOCK_ON_PREM).isSelected();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out loading :" + INCLUDE_SUB_FOLDER, toe);
            }
        }
        return false;
    }

    /**
     * This method accepts string array of
     * folders.
     * <br>
     * ForMultiLevelFolder folder, subFolder, superSubFolder...
     * <br>
     * FotSingleLevelFolder folder.
     * @param folderPath String...
     */
    public void selectFolder(String... folderPath)
    {
        if (folderPath == null || folderPath.length < 1)
        {
            throw new IllegalArgumentException("Invalid Folder path!!");
        }
        for (String folder : folderPath)
        {
            List<WebElement> folderNames = getFoldersList();
            for (WebElement syncFolder : folderNames)
            {
                if (syncFolder.getText().equals(folder))
                {
                    if (!syncFolder.isEnabled())
                    {
                        throw new PageOperationException("Sync Folder is disabled");
                    }

                    syncFolder.click();

                    if (logger.isTraceEnabled())
                    {
                        logger.trace("Folder \"" + folder + "\" selected");
                    }

                    if (folderPath.length > 1)
                    {
                        // waitUntilElementDeletedFromDom(By.cssSelector("div[id$='default-cloud-folder-treeview'] td[class='ygtvcell ygtvloading']"),
                        // SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        // waitForElement(By.cssSelector("div[id$='default-cloud-folder-treeview'] td[class^='ygtvcell']>a.ygtvspacer"),
                        // SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        try
                        {
                            waitForElement(By.id("AlfrescoWebdriverz1"), SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
                        }
                        catch (TimeoutException e)
                        {
                        }
                        // driver.waitFor(FOLDER_LOAD_TIME);
                    }

                    break;
                }
            }
        }
    }

    /**
     * Click on submit button to submit sync properties.
     * 
     * @return HtmlPage
     */
    public HtmlPage selectSubmitButtonToSync()
    {
        try
        {
            WebElement syncButton = findAndWait(SUBMIT_SYNC_BUTTON);
            if (!syncButton.isEnabled())
            {
                throw new PageOperationException("Sync Button is disabled");
            }
            String saveButtonId = syncButton.getAttribute("id");
            syncButton.click();
            waitUntilElementDisappears(By.id(saveButtonId), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

            if (!driver.getCurrentUrl().contains("workflow"))
            {
                waitUntilElementPresent(By.cssSelector("div#message>div.bd>span"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                waitUntilElementDeletedFromDom(By.cssSelector("div#message>div.bd>span"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            }

            return getCurrentPage();
        }
        catch (TimeoutException toe)
        {
            logger.error("Submit button not found!!", toe);
        }
        throw new PageException();
    }

    /**
     * Click on submit button to submit sync properties.
     * The method does not verify if the page was rendered
     */
    public void clickSyncButton()
    {
        try
        {

            findAndWait(SUBMIT_SYNC_BUTTON).click();

        }
        catch (TimeoutException toe)
        {
            logger.error("Submit button not found!!", toe);
        }

    }

    /**
     * Click cancel button on pop up to cancel cloud synce data selection.
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            WebElement cancelButton = findAndWait(BUTTON_CANCEL);
            String id = cancelButton.getAttribute("id");
            cancelButton.click();
            waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return getCurrentPage();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out finding cancel button");
            }
        }
        throw new PageException("Time out finding cancel button");
    }

    /**
     * Select close button on pop up to cancel cloud sync data selection.
     */
    public HtmlPage selectCloseButton()
    {
        try
        {
            WebElement cancelButton = driver.findElement(CLOSE_BUTTON);
            String id = cancelButton.getAttribute("id");
            cancelButton.click();
            waitUntilElementDisappears(By.id(id), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find Close button", nse);
            }
        }
        throw new PageException("Unable to find Close button");
    }

    /**
     * Method to verify Sync button is enable or not
     * 
     * @return boolean
     */
    public boolean isSyncButtonEnabled()
    {
        try
        {
            return driver.findElement(SUBMIT_SYNC_BUTTON).isEnabled();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Sync\" button", nse);
        }
    }
}
