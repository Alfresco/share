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
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.LinkedList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * This page does the selection of Destination, site and folderPath to Copy/Move the content.
 *
 * @author cbairaajoni
 */
@SuppressWarnings("unchecked")
public class CopyOrMoveContentPage extends ShareDialogue
{
    private static Log logger = LogFactory.getLog(CopyOrMoveContentPage.class);

    private final By folderPathElementId = By
            .cssSelector("div[id$='default-copyMoveTo-treeview']>div.ygtvitem, div[id$='_default-ruleConfigAction-destinationDialog-treeview']>div.ygtvitem");
    private final RenderElement footerElement = getVisibleRenderElement(By
            .cssSelector("div[id$='default-copyMoveTo-wrapper'] div.bdft, div[id$='_default-ruleConfigAction-destinationDialog-wrapper'] div.bdft"));
    private final RenderElement headerElement = getVisibleRenderElement(By
            .cssSelector("div[id$='default-copyMoveTo-title'], div[id$='_default-ruleConfigAction-destinationDialog-title']"));
    private final By destinationListCss = By.cssSelector(".mode.flat-button>div>span[style*='block']>span>button");
    private final By siteListCss = By.cssSelector("div.site>div>div>a>h4");
    private final By siteDescriptionsCss = By.cssSelector("div.site div div");
    private final By defaultDocumentsFolderCss = By
            .cssSelector("div.path>div[id$='default-copyMoveTo-treeview']>div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable>tbody>tr>td>span.ygtvlabel,"
                    + "div.path>div[id$='_default-ruleConfigAction-destinationDialog-treeview']>div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable>tbody>tr>td>span.ygtvlabel");
    private final By folderItemsListCss = By.cssSelector("div.path div.ygtvitem>div.ygtvchildren>div.ygtvitem>table.ygtvtable span.ygtvlabel");
    private final By selectedFolderItemsListCss = By
            .cssSelector("div.path div.ygtvitem>div.ygtvchildren>div.ygtvitem.selected>div.ygtvchildren>div.ygtvitem span.ygtvlabel");
    private final By copyMoveOkButtonCss = By
            .cssSelector("button[id$='default-copyMoveTo-ok-button'], button[id$='_default-ruleConfigAction-destinationDialog-ok-button']");
    private final By copyMoveCancelButtonCss = By
            .cssSelector("button[id$='default-copyMoveTo-cancel-button'], button[id$='_default-ruleConfigAction-destinationDialog-cancel']");
    private final By copyCreateLinkButtonCss = By
            .cssSelector("button[id$='default-copyMoveTo-link-button']");
    private final By copyMoveDialogCloseButtonCss = By
            .cssSelector("div[id$='default-copyMoveTo-dialog'] .container-close, div[id$='_default-ruleConfigAction-destinationDialog-dialog'] .container-close");
    private final By copyMoveDialogTitleCss = By
            .cssSelector("div[id$='default-copyMoveTo-title'], div[id$='_default-ruleConfigAction-destinationDialog-title']");

    private final By rmfolderItemsListCss = By.cssSelector("div#ygtvc7.ygtvchildren");
    private final By selectedDestination = By.xpath("//span[@class='yui-button yui-radio-button yui-button-checked yui-radio-button-checked']");
    private final By siteDocumentsCount = By.cssSelector("div#ygtvc,.ygtvchildren.ygtvitem.selected div#ygtvc,.ygtvchildren");
    
    private final By messageBoxCss = By.cssSelector("span.message");
    private String messageText = "";

    /**
     * Enum used on {@see org.alfresco.po.share.steps.SiteActions}
     * @author pbrodner
     * @author adinap
     */
    public enum ACTION{COPY, CREATE_LINK, MOVE};
    
    /**
     * Enum used on {@see org.alfresco.po.share.steps.SiteActions}
     * @author pbrodner
     */
    public enum DESTINATION
    {
    	RECENT_SITES("Recent Sites"), FAVORITE_SITES("Favorite Sites"), ALL_SITES("All Sites"), REPOSITORY("Repository"), SHARED_FILES("Shared Files"), MY_FILES("My Files");
    	private String value;
    	private DESTINATION(String value){
    		this.value = value;
    	}
    	public String getValue(){
    		return this.value;
    	}
    	
    	public boolean hasSites(){
    		return getValue().contains("Sites");
    	}
    }

    public CopyOrMoveContentPage render()
    {
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        elementRender(timer, headerElement, footerElement);
        return this;
    }

    /**
     * This method returns the Copy/Move Dialog title.
     *
     * @return String
     */

    public String getDialogTitle()
    {
        String title = "";
        try
        {
            title = findAndWait(copyMoveDialogTitleCss).getText();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the Copy/Move Dialog Css : ", e);
            }
        }
        return title;
    }

    /**
     * This method finds the list of destinations and return those as list of
     * string values.
     *
     * @return List<String>
     */
    public List<String> getDestinations()
    {
        List<String> destinations = new LinkedList<String>();
        try
        {
            for (WebElement destination : findAndWaitForElements(destinationListCss))
            {
                destinations.add(destination.getText());
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations : ", e);
            }
        }
        return destinations;
    }

    /**
     * This method finds the list of sites and return those as list of
     * string values.
     *
     * @return List<String>
     */
    public List<String> getSites()
    {
        List<String> sites = new LinkedList<String>();

        try
        {
            for (WebElement site : findAndWaitForElements(siteListCss))
            {
                sites.add(site.getText());
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of sites : ", e);
            }
        }

        return sites;
    }

    /**
     * This method finds the list of folders and return those as list of
     * string values.
     *
     * @return List<String>
     */
    public List<String> getFolders()
    {
        List<String> folders = new LinkedList<String>();
        try
        {
            try
            {
                waitForElement(By.id("AlfrescoWebdriverz1"), SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
            }
            catch (TimeoutException e)
            {
            }

            for (WebElement folder : findAndWaitForElements(folderItemsListCss))
            {
                folders.add(folder.getText());
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of folders : ", e);
            }
        }
        return folders;
    }

    /**
     * This method verifies present or not Copy/Move button
     *
     * @return boolean
     */
    public boolean isOkButtonPresent()
    {
        try
        {
            return driver.findElement(copyMoveOkButtonCss).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            logger.error("OK button is not present");
        }
        return false;
    }

    /**
     * This method verifies present or not Cancel button
     *
     * @return boolean
     */
    public boolean isCancelButtonPresent()
    {
        try
        {
            return driver.findElement(copyMoveCancelButtonCss).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Cancel button is not present");
        }
        return false;
    }

    /**
     * Check if Create Link button is displayed
     *
     * @return boolean
     */
    public boolean isCreateLinkButtonVisible()
    {
        try
        {
            WebElement button = driver.findElement(copyCreateLinkButtonCss);
            return button.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Create Link button is not present", nse);
            return false;
        }
    }

    /**
     * This method finds the clicks on copy/move button.
     *
     * @return HtmlPage Document library page/ Repository Page
     */
    public HtmlPage selectOkButton()
    {
        try
        {
            findAndWait(copyMoveOkButtonCss).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the Copy/Move Button Css : ", e);
            throw new PageException("Unable to find the Copy/Move button on Copy/Move Dialog.",e);
        }
        try
        {
            waitForElement(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            waitUntilElementDisappears(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            waitUntilElementDeletedFromDom(By.cssSelector("div.bd>span.message"), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
        }
        catch (NoSuchElementException | TimeoutException e)
        {
            //ignore exception as this is only used to verify the message dialog disappears. 
        }
        return getCurrentPage();
    }

    /**
     * Check if javascript message about link creation is displayed.
     *
     * @return true if message displayed
     */
    private boolean isMessageDisplayed()
    {
        try
        {
            WebElement messageBox = driver.findElement(messageBoxCss);
            messageText = messageBox.getText();
            return messageBox.isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            logger.error("Link creation message not displayed");
            throw new  NoSuchElementException("Link creation message not displayed", e);
        }
        catch (StaleElementReferenceException ser)
        {
            driver.navigate().refresh();
            return isMessageDisplayed();
        }
    }
    
    /**
     * Check if javascript message about link creation is displayed.
     *
     * @return true if message displayed
     */
    private String getMessageText()
    {
        isMessageDisplayed();
        return messageText;
    }

    /**
     * Check if successful javascript message about link creation is displayed.
     *
     * @return true if successful message displayed, false if link could not be created
     */
    private boolean isLinkCreated()
    {
        try
        {
            String text = getMessageText();
            if (text.contains("Successfully created link"))
            {
                waitUntilAlert();
                return true;
            }
            if (text.contains("could not"))
            {
                waitUntilAlert();
                throw new ShareException(messageText);
            }
        }
        catch (NoSuchElementException e)
        {
            logger.error("Link creation message not displayed", e);
        }
        return false;
    }

    /**
     * This method finds and clicks on Create Link button
     * 
     * @return HtmlPage
     */
    public HtmlPage selectCreateLinkButton()
    {
        try
        {
            WebElement button = driver.findElement(copyCreateLinkButtonCss);
            button.click();
            
            waitUntilAlert();
            return getCurrentPage();
        }
        catch (NoSuchElementException | StaleElementReferenceException nse)
        {
            logger.error("Create Link button not visible. ", nse);
            throw new NoSuchElementException("Create Link button not visible. ", nse);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable to find Create Link element. ", te);
            throw new TimeoutException("Unable to find Create Link element. ", te);
        }
    }

    /**
     * This method finds the clicks on cancel button and
     * control will be on HTML page DocumentLibrary Page/Repository Page
     */
    public HtmlPage selectCancelButton()
    {
        try
        {
            findAndWait(copyMoveCancelButtonCss).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the cancel button Css : ", e);
            throw new PageException("Unable to find the cancel button on Copy/Move Dialog.");
        }
    }

    /**
     * This method finds the clicks on close button and
     * control will be on DocumentLibraryPage only.
     */
    public void selectCloseButton()
    {
        try
        {
            findAndWait(copyMoveDialogCloseButtonCss).click();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the close button Css : ", e);
            throw new PageException("Unable to find the close button on Copy/Move Dialog.");
        }
    }

    /**
     * This method finds and selects the given destination name from the
     * displayed list of destinations.
     *
     * @param destinationName String
     * @return CopyOrMoveContentPage
     */
    public HtmlPage selectDestination(String destinationName)
    {
        if (StringUtils.isEmpty(destinationName))
        {
            throw new IllegalArgumentException("Destination name is required");
        }
        try
        {
            for (WebElement destination : findAndWaitForElements(destinationListCss))
            {
                if (destination.getText() != null)
                {
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("Destination test" + destination.getText());
                    }
                    if (destination.getText().equalsIgnoreCase(destinationName))
                    {
                        destination.click();
                        if (destinationName.contains("Sites"))
                        {
                            waitForElement(siteListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        else if ((destinationName.contains("Repository")) || (destinationName.contains("Shared Files"))
                                || (destinationName.contains("My Files")))
                        {
                            waitForElement(folderPathElementId, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        return getCurrentPage();
                    }
                }
            }
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of destionation", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of destionations", e);
        }

        throw new PageOperationException("Unable to select Destination : " + destinationName);
    }

    /**
     * This method finds and selects the given site name from the
     * displayed list of sites.
     *
     * @param siteName String
     * @return CopyOrMoveContentPage
     */
    public HtmlPage selectSite(String siteName)
    {
        if (StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site name is required");
        }

        try
        {
            for (WebElement site : findAndWaitForElements(siteListCss))
            {
                if (site.getText() != null)
                {
                    if (site.getText().equalsIgnoreCase(siteName))
                    {
                        site.click();
                        waitForElement(defaultDocumentsFolderCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        waitForElement(folderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

                        return getCurrentPage();
                    }
                }
            }
            throw new PageOperationException("Unable to find the site: " + siteName);
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of site", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of sites", e);
        }

        throw new PageOperationException("Unable to select site.");
    }
    
    /**
     * This method finds and selects the given site name from the
     * displayed list of sites.
     *
     * @param siteDescription String
     * @return CopyOrMoveContentPage
     */
    public CopyOrMoveContentPage selectSiteByDescription(String siteName, String siteDescription)
    {
        if (StringUtils.isEmpty(siteDescription))
        {
            throw new IllegalArgumentException("Site description is required");
        }

        try
        {	
            for (WebElement site : findAndWaitForElements(siteDescriptionsCss))
            {
            	String siteFullText = site.getText();
            	String tmpDescription ="<none>";
            	if (siteFullText.split("\n").length>1){
            		tmpDescription = siteFullText.split("\n")[1];
            	}
                if (siteFullText != null)
                {
                    if ((siteFullText.contains(siteName)) && tmpDescription.equalsIgnoreCase(siteDescription))
                    {
                        site.click();                       
                    	waitForElement(defaultDocumentsFolderCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        waitForElement(folderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

                        return factoryPage.instantiatePage(driver, CopyOrMoveContentPage.class);	                            
                    }
                }
            }
            throw new PageOperationException("Unable to find the site: " + siteDescription);
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable to find the inner text of site", ne);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to get the list of sites", e);
        }

        throw new PageOperationException("Unable to select site.");
    }

    /**
     * This method finds and selects the given folder path from the displayed list
     * of folders.
     *
     * @param folderPath
     * @return CopyOrMoveContentPage
     */
    public HtmlPage selectPath(String... folderPath)
    {
        if (folderPath == null || folderPath.length < 1)
        {
            throw new IllegalArgumentException("Invalid Folder path!!");
        }
        int length = folderPath.length;
        List<WebElement> folderNames;
        try
        {
            for (String folder : folderPath)
            {
                try
                {
                    waitForElement(By.id("AlfrescoWebdriverz1"), SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
                }
                catch (TimeoutException e)
                {
                }

                folderNames = findAndWaitForElements(folderItemsListCss);
                boolean selected = false;

                for (WebElement folderName : folderNames)
                {
                    if (folderName.getText().equalsIgnoreCase(folder))
                    {
                        folderName.click();

                        // If repository has more folders, then some times the destination folder will be the last folder in copyTo dialogue, it is not
                        // selecting the destination folder.
                        // The below logic is written to check the destination folder is displayed and not selected, then selecting the destination folder.
                        try
                        {
                            waitForElement(By.id("AlfrescoWebdriverz1"), SECONDS.convert(getDefaultWaitTime(), MILLISECONDS));
                        }
                        catch (TimeoutException e)
                        {
                        }

                        try
                        {
                            if (!findAndWait(By.xpath(String.format("//span[@class='ygtvlabel' and text()='%s']/../../../../..", folderName.getText())))
                                    .getAttribute("class").contains("selected"))
                            {
                                folderName.click();
                            }
                        }
                        catch (StaleElementReferenceException sle)
                        {
                        }

                        selected = true;
                        logger.info("Folder \"" + folder + "\" selected");
                        if (length > 1)
                        {
                            waitForElement(folderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                            waitForElement(selectedFolderItemsListCss, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                        }
                        break;
                    }
                }
                length--;
                if (!selected)
                {
                    throw new PageException("Cannot select the folder metioned in the path");
                }
            }
            return getCurrentPage();
        }
        catch (NoSuchElementException ne)
        {
            logger.error("Unable find the folder name. ", ne);
        }
        catch (TimeoutException te)
        {
            logger.error("Unable find the folders css. ", te);
        }
        throw new PageOperationException("Unable to select the folder path.");
    }

    protected By getCopyMoveOkButtonCss()
    {
        return copyMoveOkButtonCss;
    }

    public void rmSelectFolders(String folderName)
    {
        List<WebElement> folders;
        folders = findAndWaitForElements(rmfolderItemsListCss);
        for (WebElement folder : folders)
        {

            try
            {

                if (folder.getText().equalsIgnoreCase(folderName))
                    if(logger.isTraceEnabled())
                    {
                        logger.trace("the folder is " + folder.getText());
                    }
                    folder.click();
            }
            catch (Exception e)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Unable to get the list of folders : ", e);
                }

            }
        }
    }
    
   /**
   * @return
   */
   public String getSelectedDestination()
   {
	   WebElement destination = findAndWait(selectedDestination);
	   return destination.getText();
   }
   
   /**
	* @return the list of all document paths available for selected site.
	*/
   public int getSiteDocumentPathCount()
   {
	   int size = findAndWaitForElements(siteDocumentsCount).size();
	   if (size>0){
		   return size - 11;
	   }
	   else
	   {
		   return size;   
	   }
   }
}
