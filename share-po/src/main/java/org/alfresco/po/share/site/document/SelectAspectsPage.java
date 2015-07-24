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
package org.alfresco.po.share.site.document;

import com.sun.jna.platform.unix.X11.XSizeHints.Aspect;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Select Aspects page object, this page comes from Document Detail Page's Manage Aspects.
 * 
 * @author Shan Nagarajan
 * @author mbhave
 * @since 1.6.1
 */
public class SelectAspectsPage extends SharePage
{

    private static final By AVAILABLE_ASPECT_TABLE = By.cssSelector("div[id$='aspects-left']>table>tbody.yui-dt-data>tr");
    private static final By CURRENTLY_ADDED_ASPECT_TABLE = By.cssSelector("div[id$='aspects-right']>table>tbody.yui-dt-data>tr");
    private static final By HEADER_ASPECT_TABLE = By.cssSelector("td>div>h3");
    private static final By ADD_REMOVE_LINK = By.cssSelector("td>div>a");
    private static final By APPLY_CHANGE = By.cssSelector("button[id$='aspects-ok-button']");
    private static final By CANCEL = By.cssSelector("button[id$='aspects-cancel-button']");
    private static Log logger = LogFactory.getLog(SelectAspectsPage.class);
    private static final By TITLE = By.cssSelector("div[id$='aspects-title']");
    private static final By ASPECTS_AVAILABLE = By.xpath("//div[contains(@id,'default-aspects-right')]//td/div[@class='yui-dt-liner']");
    private static final By ASPECTS_SELECTED = By.xpath("//div[contains(@id,'default-aspects-right')]//td/div[@class='yui-dt-liner']");
    private static final By NOTIFICATION = By.cssSelector("div.bd>span.message");

    private static final String ASPECT_AVAILBLE_XPATH ="//div[contains(@id,'aspects-left')]//td/div[@class='yui-dt-liner']//h3[text()='%s']";
    /**
     * Constructor.
     */
    public SelectAspectsPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectAspectsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(TITLE));
        drone.waitUntilNotVisible(ASPECTS_AVAILABLE, "Loading...", SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        drone.waitUntilNotVisible(ASPECTS_SELECTED, "Loading...", SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectAspectsPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectAspectsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * remove the {@link Aspect} if it is available to add.
     * 
     * @param aspects {@link List} of {@link Aspect} to remove.
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage remove(List<DocumentAspect> aspects)
    {
        return addRemoveAspects(aspects, CURRENTLY_ADDED_ASPECT_TABLE);
    }

    /**
     * Add the {@link Aspect} if it is available to add.
     * 
     * @param aspects {@link List} of {@link Aspect} to added.
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage add(List<DocumentAspect> aspects)
    {
        return addRemoveAspects(aspects, AVAILABLE_ASPECT_TABLE);
    }
    
    /**
     * remove the {@link Aspect} if it is available to add.
     * 
     * @param aspects {@link List} of {@link Aspect} to remove.
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage removeDynamicAspects(List<String> aspects)
    {
        return addRemoveAspectsDynamicAspects(aspects, CURRENTLY_ADDED_ASPECT_TABLE);
    }

    /**
     * Add the {@link Aspect} if it is available to add.
     * 
     * @param aspects {@link List} of {@link Aspect} to added.
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage addDynamicAspects(List<String> aspects)
    {
        return addRemoveAspectsDynamicAspects(aspects, AVAILABLE_ASPECT_TABLE);
    }

    public Set<DocumentAspect> getAvailableSystemAspects()
    {
        return getSystemAspectsMap(AVAILABLE_ASPECT_TABLE).keySet();
    }

    public Set<DocumentAspect> getSelectedSystemAspects()
    {              
        return getSystemAspectsMap(CURRENTLY_ADDED_ASPECT_TABLE).keySet();
    }

    private Map<String, ShareLink> getAllAspectsMap(By by)
    {
        List<WebElement> availableElements = null;
        Map<String, ShareLink> availableAspectMap = null;
        try
        {
            availableElements = drone.findAndWaitForElements(by);
        }
        catch (TimeoutException exception)
        {
            return Collections.emptyMap();
        }

        if (availableElements != null && !availableElements.isEmpty())
        {
            // Convert List into Map
            availableAspectMap = new HashMap<String, ShareLink>();
            for (WebElement webElement : availableElements)
            {
                try
                {
                    WebElement header = webElement.findElement(HEADER_ASPECT_TABLE);
                    WebElement addLink = webElement.findElement(ADD_REMOVE_LINK);
                    ShareLink addShareLink = new ShareLink(addLink, drone);
                    availableAspectMap.put(header.getText(), addShareLink);
                }
                catch (NoSuchElementException e)
                {
                    logger.error("Not able to find the header or link element on this row.", e);
                }
                catch (Exception e)
                {
                    logger.error("Exception while finding & adding aspects : ", e);
                }
            }
        }
        return availableAspectMap;
    }
    
    /**
     * Util to check if the aspect appears in the added aspects list
     * @param aspectName String
     * @return true if aspect name is found in the list
     */
    public boolean isAspectAdded(String aspectName)
    {      
         try
         {
             Set<String> allAspects = getAllAspectsMap(CURRENTLY_ADDED_ASPECT_TABLE).keySet();
             return allAspects.contains(aspectName);
         }
         catch (Exception e)
         {

         }
         return false;
    }
    
    /**
     * Util to check if the aspect appears in the available aspects list
     * @param aspectName String
     * @return true if aspect name is found in the list
     */
    public boolean isAspectAvailable(String aspectName)
    {
        try
        {
            Set<String> allAspects = getAllAspectsMap(AVAILABLE_ASPECT_TABLE).keySet();
            return allAspects.contains(aspectName);
        }
        catch (Exception e)
        {

        }
        return false;
    }

    /**
     * Add the {@link Aspect} if it is available to add.
     * 
     * @param aspects {@link List} of {@link Aspect} to added.
     * @return {@link SelectAspectsPage}
     */
    private HtmlPage addRemoveAspects(List<DocumentAspect> aspects, By by)
    {
        if (aspects == null || aspects.isEmpty())
        {
            throw new UnsupportedOperationException("Aspets can't be empty or null.");
        }
        Map<DocumentAspect, ShareLink> availableAspectMap = getSystemAspectsMap(by);

        if (availableAspectMap != null && !availableAspectMap.isEmpty())
        {
            for (DocumentAspect aspect : aspects)
            {
                ShareLink link = availableAspectMap.get(aspect);
                if (link != null)
                {
                    try
                    {
                        if (AVAILABLE_ASPECT_TABLE.equals(by))
                        {
                            WebElement aspectElement = drone.find(By.xpath(String.format(ASPECT_AVAILBLE_XPATH, aspect.getValue())));
                            if (!aspectElement.isSelected())
                            {
                                aspectElement.click();
                            }
                        }
                        link.click();
                        if (logger.isTraceEnabled())
                        {
                            logger.trace(aspect + "Aspect Added.");
                        }
                    }
                    catch (StaleElementReferenceException exception)
                    {
                        drone.find(CANCEL).click();
                        throw new PageException("Unexpected Refresh on Page lost reference to the Aspects.", exception);
                    }
                }
                else
                {
                    logger.error("Not able to find in the available aspects bucket " + aspect.toString());
                }
            }
        }

        return this;
    }

    /**
     * Click on {@code #CANCEL} in {@code selectAspectsPage}
     * 
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage clickCancel()
    {
        try
        {
            drone.find(CANCEL).click();
            return drone.getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not able find the cancel button: ", nse);
        }
    }

    /**
     * Click on {@code ApplyChanges} in {@code selectAspectsPage}
     * 
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage clickApplyChanges()
    {
        String msgText = "Successfully updated aspects";
        
        try
        {
            drone.find(APPLY_CHANGE).click();
            
            // Wait for Notification
            drone.waitForElement(NOTIFICATION, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

            if (!isNotificationTextCorrect())
            {
                // Aspect Changes NOT Successful
                msgText = "Could not update Aspects";
                logger.debug("Aspect Changes were unsuccessful");
            }

            drone.waitUntilNotVisible(NOTIFICATION, msgText, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return drone.getCurrentPage();
        }
        catch (NoSuchElementException | TimeoutException nse)
        {
            throw new PageException("Error Applying Changes: Not able find the apply change button or Notification: ", nse);
        }

    }

    private boolean isNotificationTextCorrect()
    {
        WebElement messageText = drone.findAndWait(NOTIFICATION);
        return messageText.getText().equals("Successfully updated aspects");
    }

    @Override
    public String getTitle()
    {
        return drone.find(TITLE).getText();
    } 
    
    /**
     * Add the {@link Aspect} if it is available to add.
     * 
     * @param aspects {@link List} of {@link Aspect} to added.
     * @return {@link SelectAspectsPage}
     */
    private HtmlPage addRemoveAspectsDynamicAspects(List<String> aspects, By by)
    {
        if (aspects == null || aspects.isEmpty())
        {
            throw new UnsupportedOperationException("Aspets can't be empty or null.");
        }
        
        Map<String, ShareLink> availableAspectMap = getAllAspectsMap(by);

        if (availableAspectMap != null && !availableAspectMap.isEmpty())
        {
            for (String aspect : aspects)
            {
                ShareLink link = availableAspectMap.get(aspect);
                if (link != null)
                {
                    try
                    {
                        if (AVAILABLE_ASPECT_TABLE.equals(by))
                        {
                            WebElement aspectElement = drone.find(By.xpath(String.format(ASPECT_AVAILBLE_XPATH, aspect)));
                            if (!aspectElement.isSelected())
                            {
                                aspectElement.click();
                            }
                        }
                        link.click();
                        if (logger.isTraceEnabled())
                        {
                            logger.trace(aspect + "Aspect Added.");
                        }
                    }
                    catch (StaleElementReferenceException exception)
                    {
                        drone.find(CANCEL).click();
                        throw new PageException("Unexpected Refresh on Page lost reference to the Aspects.", exception);
                    }
                }
                else
                {
                    logger.error("Not able to find in the available aspects bucket " + aspect.toString());
                }
            }
        }

        return this;
    }
    
    /**
     * Util to convert a Map<String, shareLink> to Map<DocumentAspect, sharelink> for system aspects.
     * This util will exclude dynamic aspects
     * @param by By
     * @return Map
     */
    public Map<DocumentAspect, ShareLink> getSystemAspectsMap(By by)
    {
        Map<DocumentAspect, ShareLink> availableAspectMapSystem = Collections.emptyMap();
    
        Map<String, ShareLink> availableAspectMap = getAllAspectsMap(by);
        if (availableAspectMap.isEmpty())
        {
            logger.info("No System Aspect is available to add");
        }
        else
        {
            // Make a list of System Document Aspects
            availableAspectMapSystem = new HashMap<DocumentAspect, ShareLink>();
            for (Map.Entry<String, ShareLink> entry : availableAspectMap.entrySet())
            {
                String aspect = entry.getKey();
                
                try
                {
                    DocumentAspect sysAspect = DocumentAspect.getAspect(aspect);
                    availableAspectMapSystem.put(sysAspect, entry.getValue());
                }
                catch (Exception e)
                {
                    // Skip adding entry: as its not a system aspect
                }
            }            
        }
        return availableAspectMapSystem;
    }

}
