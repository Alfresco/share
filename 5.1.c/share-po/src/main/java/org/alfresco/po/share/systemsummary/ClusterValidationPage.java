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
package org.alfresco.po.share.systemsummary;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author maryia.zaichanka
 */
public class ClusterValidationPage extends ShareDialogue {
    // Cluster Enabled
    private static final By CLOSE_BUTTON = By.cssSelector(".cancel");
    private static final By NODES = By.cssSelector(".success");
    private static final By TITLE = By.cssSelector(".title>h1");
    private static final By VALIDATTION_RESULT = By.cssSelector("#test-result>span");

    private Log logger = LogFactory.getLog(ClusterValidationPage.class);


    @SuppressWarnings("unchecked")
    @Override
    public ClusterValidationPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClusterValidationPage render() {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Checks if Close button present at a page
     *
     * @return boolean
     */
    public boolean isCancelButtonPresent() {
        try {
            WebElement serverName = driver.findElement(CLOSE_BUTTON);
            return serverName.isDisplayed();
        } catch (NoSuchElementException nse) {
            return false;
        }
    }

    /**
     * Checks for validated nodes list
     *
     * @return list of ip address for cluster members
     */
    public List<String> getSucceedNodes() {

        try {
            List<String> SucceedNodes = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(NODES);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    SucceedNodes.add(webElement.getText());
                }
            }
            return SucceedNodes;
        } catch (StaleElementReferenceException e) {
            return getSucceedNodes();
        }

    }

    /**
     * Gets popup title
     *
     * @return String
     */
    public String getTitle() {
        try {
            return findAndWait(TITLE).getText();
        } catch (TimeoutException toe) {
            throw new PageOperationException("Title isn't present", toe);
        }

    }

    /**
     * Close Validate Cluster page
     *
     * @param driver
     * @return
     */
    public RepositoryServerClusteringPage closeValidationPage(WebDriver driver)
    {
        try
        {
            WebElement validateCluster = driver.findElement(CLOSE_BUTTON);
            validateCluster.click();
            return factoryPage.instantiatePage(driver, RepositoryServerClusteringPage.class);
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled()) 
            {
                logger.trace("Button " + CLOSE_BUTTON + " isn't found", nse);
            }
        }
        throw new PageOperationException("Page isn't opened");
    }

    public HtmlPage clickClose()
    {
        try
        {
            findAndWait(CLOSE_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able find the close button " + e);
        }
        catch (StaleElementReferenceException ser)
        {
            return clickClose();
        }
        return getCurrentPage();
    }

    public String getValidationResult() {
        try {
            return findAndWait(VALIDATTION_RESULT).getText();
        } catch (TimeoutException toe) {
            throw new PageOperationException("No result is present", toe);
        }

    }
}
