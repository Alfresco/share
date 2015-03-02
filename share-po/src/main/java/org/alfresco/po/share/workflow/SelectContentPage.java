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

package org.alfresco.po.share.workflow;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.ElementState;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the HTML page to select and add content to workflow.
 * 
 * @author Abhijeet Bharade, Shan Nagarajan, Bogdan Bocancea
 * @since 1.7.0
 */
public class SelectContentPage extends SharePage
{
    private static final Logger logger = Logger.getLogger(SelectContentPage.class);

    private final By folderUpButton = By.cssSelector("div[style*='visibility: visible'] button[id$='-cntrl-picker-folderUp-button']");
    private final By navigatorButton = By.cssSelector("div[style*='visibility: visible'] button[id$='picker-navigator-button']");
    private final By navigateCompanyHome = By.cssSelector("ul[id$='picker-navigatorItems'] li:nth-of-type(1)>a");
    private final By addedContents = By.cssSelector("div[id$='cntrl-picker-right'] .name");
    private final By addedContentsElements = By.cssSelector("div[id$='cntrl-picker-right']>div[id$='-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr");
    @SuppressWarnings("unused")
    private final By availableContentElements = By
            .cssSelector("div[id$='cntrl-picker-right']>div[id$='-cntrl-picker-selectedItems']>table>tbody.yui-dt-data>tr");
    private final String sitesString = "Sites";
    private final String documentLibrary = "documentLibrary";
    private final By header = By.cssSelector("div[style*='visibility: visible'] div[id$='cntrl-picker-head']");
    private final String dashletEmptyPlaceholder = "div[style*='visibility: visible'] table>tbody>tr>td.yui-dt-empty>div";
    private final By okButton = By.cssSelector("div[style*='visibility: visible'] button[id*='cntrl-ok-button']");
    private final By cancelButton = By.cssSelector("div[style*='visibility: visible'] button[id*='cntrl-cancel-button']");
    private final By closeButton = By.cssSelector("div[style*='visibility: visible'] div[id$='cntrl-picker']>a.container-close");

    private final By noItemsSelected = By.cssSelector("div[id$='id1_assoc_packageItems-cntrl-picker-selectedItems']>table>tbody.yui-dt-message>tr>td>div");

    private static final String REPO = "Repository";
    private static final String SLASH = File.separator;

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public SelectContentPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectContentPage render(RenderTime timer)
    {
        try
        {
            elementRender(timer, getVisibleRenderElement(header), getVisibleRenderElement(folderUpButton), getVisibleRenderElement(navigatorButton),
                    getVisibleRenderElement(By.cssSelector("div[style*='visibility: visible'] div[id$='cntrl-picker-left']")),
                    getVisibleRenderElement(By.cssSelector("div[style*='visibility: visible'] div[id$='cntrl-picker-right']")),
                    getVisibleRenderElement(okButton), getVisibleRenderElement(cancelButton), new RenderElement(By.cssSelector(dashletEmptyPlaceholder),
                            ElementState.INVISIBLE_WITH_TEXT, "Loading..."));
        }
        catch (PageRenderTimeException te)
        {
            throw new PageException(this.getClass().getName() + " failed to render in time", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectContentPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectContentPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Add Items set into {@link CompanyHome}.
     * 
     * @param companyHome - {@link CompanyHome}
     */
    public void addItems(CompanyHome companyHome)
    {

        Set<Site> sites = companyHome.getSites();
        Set<Content> theContents = companyHome.getContents();
        if (sites != null)
        {
            navigateToRootDir();
            clickElementOnLeftPanel(sitesString);
            for (Site site : sites)
            {
                clickElementOnLeftPanel(site.getName());
                clickElementOnLeftPanel(documentLibrary);
                Set<Content> contents = site.getContents();
                for (Content content : contents)
                {
                    contentProcessor(content);
                }
            }
        }

        else if (theContents != null)
        {
            navigateToRootDir();
            for (Content content : theContents)
            {
                contentProcessor(content);
            }
        }
    }

    private void contentProcessor(Content content)
    {
        if (!content.isFolder() && (content.getContents() == null || content.getContents().size() == 0))
        {
            addContent(content.getName());
        }
        else if (content.isFolder())
        {
            String name = content.getName();
            clickElementOnLeftPanel(name);
            Set<Content> contents = content.getContents();
            if (contents != null)
            {
                for (Content content2 : contents)
                {
                    contentProcessor(content2);
                }
            }
            clickFolderUpButton();
            renderCurrentAvailableItem(name);
        }
    }

    public void clickFolderUpButton()
    {
        try
        {
            drone.find(By.cssSelector("button[id$='cntrl-picker-folderUp-button']")).click();
            // waitUntilAlert();
        }
        catch (ElementNotVisibleException env)
        {
            drone.findAll(By.cssSelector("button[id$='cntrl-picker-folderUp-button']")).get(1).click();
            // waitUntilAlert();
        }
    }

    public void clickElementOnLeftPanel(String text)
    {
        if (StringUtils.isEmpty(text))
        {
            throw new IllegalArgumentException("Text can't be empty or null");
        }
        List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("div[style*='visibility: visible'] .panel-left table tr h3 a"));

        for (WebElement webElement : elements)
        {
            try
            {
                if (text.equalsIgnoreCase(webElement.getText()))
                {
                    webElement.click();
                    waitUntilAlert(3);
                    break;
                }
            }
            catch (StaleElementReferenceException ser)
            {
                clickElementOnLeftPanel(text);
            }
        }
    }

    /**
     * Method to get elements from left panel
     * 
     * @return list of elements
     */
    public List<String> getDirectoriesLeftPanel()
    {
        List<String> users = new ArrayList<String>();
        try
        {
            List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("div[style*='visibility: visible'] .panel-left table tr h3 a"));
            for (WebElement user : elements)
            {
                users.add(user.getText());
            }
            return users;
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("No elements found", nse);
            }
        }
        return users;
    }

    private void addContent(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name can't be empty or null");
        }
        List<WebElement> elements = drone.findAndWaitForElements(By
                .cssSelector("div[style*='visibility: visible'] div[id*='cntrl-picker-left'] .yui-dt-data tr"));

        for (WebElement webElement : elements)
        {
            if (name.equalsIgnoreCase(webElement.findElement(By.cssSelector(".item-name")).getText()))
            {
                webElement.findElement(By.cssSelector(".addIcon")).click();
                break;
            }
        }
    }

    /**
     * Returns the Added items as Strings.
     * 
     * @return {@link List}
     */
    public List<String> getAddedItems()
    {
        List<String> items = new ArrayList<String>();
        List<WebElement> elements = drone.findAll(addedContents);
        if (elements != null)
        {
            for (WebElement webElement : elements)
            {
                items.add(webElement.getText());
            }
        }
        else
        {
            items = Collections.emptyList();
        }
        return items;
    }

    /**
     * Render the element available in current items, it can be used to wait till element to be loaded into current available items.
     * 
     * @param name - Name of the Content to be rendered
     */
    public void renderCurrentAvailableItem(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("Name can't be empty or null");
        }
        RenderTime timer = new RenderTime(maxPageLoadingTime);
        outerLoop: while (true)
        {
            timer.start();
            try
            {
                List<WebElement> elements = drone.findAll(By.cssSelector("div[style*='visibility: visible'] div[id$='cntrl-picker-left'] .item-name a"));
                if (elements != null)
                {

                    for (WebElement webElement : elements)
                    {
                        if (name.equalsIgnoreCase(webElement.getText()))
                        {
                            break outerLoop;
                        }
                    }
                }
            }
            catch (NoSuchElementException te)
            {
                if (logger.isDebugEnabled())
                {
                    logger.error(te);
                }
            }
            catch (StaleElementReferenceException e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.error(e);
                }
            }
            finally
            {
                timer.end();
            }
        }
    }

    /**
     * Method to click OK button
     * 
     * @return
     */
    public HtmlPage selectOKButton()
    {
        try
        {
            drone.find(okButton).click();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find \"OK\" button", nse);
            }
        }
        catch (ElementNotVisibleException env)
        {
            drone.findAll(okButton).get(1).click();
        }
        return FactorySharePage.resolvePage(drone).render();
    }

    /**
     * Method to verify Folder Up button is Enabled or not
     * 
     * @return True if enabled
     */
    public boolean isFolderUpButtonEnabled()
    {
        try
        {
            List<String> elements = getDirectoriesLeftPanel();
            if (!elements.isEmpty());              
            {
                WebElement webElement = drone.findAndWait(folderUpButton);
                boolean isUp = webElement.isEnabled();
                return isUp;
            }
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find \"Folder Up\" button", nse);
        }
    }

    /**
     * Method to add given file from given site
     * 
     * @param fileName
     * @param siteName
     */
    public void addItemFromSite(String fileName, String siteName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name cannot be null or empty");
        }

        if (StringUtils.isEmpty(siteName))
        {
            throw new IllegalArgumentException("Site Name cannot be null or empty");
        }

        Content content = new Content();
        content.setName(fileName);
        content.setFolder(false);
        Set<Content> contents = new HashSet<Content>();
        contents.add(content);

        Set<Site> sites = new HashSet<Site>();
        Site site = new Site();
        site.setName(getSiteShortName(siteName));
        site.setContents(contents);
        sites.add(site);

        CompanyHome companyHome = new CompanyHome();
        companyHome.setSites(sites);
        addItems(companyHome);
    }

    /**
     * Method to verify Add icon is present
     * 
     * @return True if Add icon is present
     */
    public boolean isAddIconPresent(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be null");
        }
        List<WebElement> elements = drone.findAndWaitForElements(By.cssSelector("div[id$='assoc_packageItems-cntrl-picker-left'] .yui-dt-data tr"));
        if (elements.size() == 0)
        {
            throw new PageOperationException("File Name doesn't exists in the list");
        }
        for (WebElement webElement : elements)
        {
            if (fileName.equalsIgnoreCase(webElement.findElement(By.cssSelector(".item-name")).getText()))
            {
                return webElement.findElement(By.cssSelector(".addIcon")).isDisplayed();
            }
        }
        return false;
    }

    /**
     * Method to remove a user from Selected Users list
     * 
     * @param fileName
     */
    public void removeItem(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("File Name cannot be empty");
        }
        List<WebElement> selectedFiles = drone.findAll(addedContentsElements);
        if (selectedFiles.size() < 1)
        {
            throw new PageOperationException("File is not selected.");
        }
        for (WebElement file : selectedFiles)
        {
            if (file.findElement(By.cssSelector("h3.name")).getText().contains(fileName))
            {
                drone.mouseOverOnElement(file.findElement(By.cssSelector("a.remove-item")));
                file.findElement(By.cssSelector("a.remove-item")).click();
                break;
            }
        }
    }

    /**
     * Method to verify Remove icon is present
     * 
     * @return True if Remove icon is present
     */
    public boolean isRemoveIconPresent(String fileName)
    {
        boolean found;
        if (StringUtils.isEmpty(fileName))
        {
            throw new IllegalArgumentException("FileName cannot be null");
        }
        List<WebElement> selectedFiles = drone.findAll(addedContentsElements);
        if (selectedFiles.size() == 0)
        {
            throw new PageOperationException("File Name doesn't exists in the list");
        }
        for (WebElement file : selectedFiles)
        {
            if (file.findElement(By.cssSelector("h3.name")).getText().contains(fileName))
            {
                drone.mouseOverOnElement(file.findElement(By.cssSelector("a.remove-item")));
                found = file.findElement(By.cssSelector("a.remove-item")).isDisplayed();
                return found;

            }
        }
        return false;
    }

    /**
     * Method to select Close button
     */
    public void selectCloseButton()
    {
        try
        {
            drone.find(closeButton).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Close Button", nse);
        }
    }

    /**
     * Method to select Cancel button
     */
    public void selectCancelButton()
    {
        try
        {
            drone.findAndWait(cancelButton).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find Cancel Button", nse);
        }
    }

    public void navigateToRootDir()
    {
        try
        {
            drone.findAndWait(navigatorButton).click();
            //drone.waitUntilElementClickable(navigateCompanyHome, 5);
            drone.findAndWait(navigateCompanyHome).click();
            // waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find buttons" + te);
        }
    }

    /**
     * @return
     */
    public String getNoItemsSelected()
    {
        try
        {
            String message = drone.findAndWait(noItemsSelected).getText();
            return message;
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding an element");
        }
        throw new PageException();
    }

    /**
     * Method to check if Cancel Button is present
     * 
     * @return boolean
     */
    public boolean isCancelButtonPresent()
    {
        try
        {
            return (drone.findAndWait(cancelButton).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check if Cancel Button is present
     * 
     * @return boolean
     */
    public boolean isOkButtonPresent()
    {
        try
        {
            return (drone.findAndWait(okButton).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to check if navigator Button is present
     * 
     * @return boolean
     */
    public boolean isCompanyHomeButtonPresent()
    {
        try
        {
            return (drone.findAndWait(navigatorButton).isDisplayed());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

}