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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Button;

import com.sun.jna.platform.unix.X11.XSizeHints.Aspect;

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

    private static final By CANCEL = By.cssSelector("button[id$='aspects-cancel-button']");
    private static Log logger = LogFactory.getLog(SelectAspectsPage.class);
    private static final By TITLE = By.cssSelector("div[id$='aspects-title']");
    private static final By ASPECTS_AVAILABLE = By.xpath("//div[contains(@id,'default-aspects-right')]//td/div[@class='yui-dt-liner']");
    private static final By ASPECTS_SELECTED = By.xpath("//div[contains(@id,'default-aspects-right')]//td/div[@class='yui-dt-liner']");
    private static final By NOTIFICATION = By.cssSelector("div.bd>span.message");

//    private static final String ASPECT_AVAILBLE_XPATH ="//div[contains(@id,'aspects-left')]//[text()='%s']";

    @SuppressWarnings("unchecked")
    @Override
    public SelectAspectsPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(TITLE));
        waitUntilNotVisible(ASPECTS_AVAILABLE, "Loading...", SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        waitUntilNotVisible(ASPECTS_SELECTED, "Loading...", SECONDS.convert(maxPageLoadingTime, TimeUnit.MILLISECONDS));
        return this;
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
        availableElements = findAndWaitForElements(by);

        if (availableElements != null && !availableElements.isEmpty())
        {
            // Convert List into Map
            availableAspectMap = new HashMap<String, ShareLink>();
            for (WebElement webElement : availableElements)
            {
                try
                {
                    WebElement header = webElement.findElement(By.xpath(".//*[@class='name']"));
                    //Some case title appears as Sample (sa:sa) or just as Sample. 
                    String title[] = header.getText().split("\\(");
                    WebElement addLink = webElement.findElement(By.xpath(".//a"));
                    ShareLink addShareLink = new ShareLink(addLink, driver, factoryPage);
                    availableAspectMap.put(title[0].trim(), addShareLink);
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
            return availableAspectMap;
        }
        throw new PageOperationException("No Aspects were found");
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
             for(String s:allAspects)
             {
                 if(aspectName.contains(s)) return true;
             }
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
            //As ket differs between some versions we have to iterate and match.
            for(String s:allAspects)
            {
                if(aspectName.contains(s)) return true;
            }
            return false;
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
        PageUtils.checkMandatoryParam("aspcets", aspects);
        PageUtils.checkMandatoryParam("By selector", by);
        
        Map<String, ShareLink> availableAspectMap = getAllAspectsMap(by);
        if (availableAspectMap != null && !availableAspectMap.isEmpty())
        {
            for (DocumentAspect aspect : aspects)
            {
                String title[] = aspect.getValue().split("\\(");
                ShareLink link = availableAspectMap.get(title[0].trim());
                if (link != null)
                {
                    link.click();
                }
                else
                {
                    logger.error("Not able to find in the available aspects bucket " + aspect.toString());
                }
            }
        }
        return getCurrentPage();
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
            driver.findElement(CANCEL).click();
            return getCurrentPage();
        }
        catch (NoSuchElementException nse)
        {
            throw new PageException("Not able find the cancel button: ", nse);
        }
    }

    @FindBy(css="div[id$='_default-aspects-dialog'] button[id$='_default-aspects-ok-button']") Button applyChanges;
    /**
     * Click on {@code ApplyChanges} in {@code selectAspectsPage}
     * 
     * @return {@link SelectAspectsPage}
     */
    public HtmlPage clickApplyChanges()
    {
        try
        {
            applyChanges.click();
            waitForElement(NOTIFICATION, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));

            if (!isNotificationTextCorrect())
            {
                logger.debug("Aspect Changes were unsuccessful");
            }
            waitUntilNotVisible(NOTIFICATION,"Successfully updated aspects", SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return getCurrentPage();
        }
        catch (NoSuchElementException | TimeoutException nse)
        {
            throw new PageException("Error Applying Changes: Not able find the apply change button or Notification: ", nse);
        }

    }

    private boolean isNotificationTextCorrect()
    {
        WebElement messageText = findAndWait(NOTIFICATION);
        return messageText.getText().equals("Successfully updated aspects");
    }

    @Override
    public String getTitle()
    {
        return driver.findElement(TITLE).getText();
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
                String title[] = aspect.split("\\(");
                ShareLink link = availableAspectMap.get(title[0].trim());
                if (link != null)
                {
                    link.click();
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
