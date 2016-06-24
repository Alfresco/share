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
package org.alfresco.po.share.systemsummary;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author sergey.kardash on 4/12/14.
 */
public class RepositoryServerClusteringPage extends AdvancedAdminConsolePage {
    // Cluster Enabled
    private static final By CLUSTER_ENABLED = By.cssSelector("div[class$='control status'] span[class$='value'] img[title$='Enabled']");

    // Cluster Members
    private static final By CLUSTER_MEMBERS_IP = By.cssSelector("table[id$='rc-membertable'] tbody tr td:nth-of-type(2)");

    // Cluster Members
    private static final By CLUSTER_MEMBERS_NUMBER = By.cssSelector("div[class$='column-full'] div[class$='control field'] span[class$='value']");


    private static final By CLUSTER_SERVER_NAME = By.cssSelector("div[class$='column-left'] div[class$='control field'] span[class$='label']");
    private static final By CLUSTER_IP_ADDRESS = By.cssSelector("div[class$='column-right'] div[class$='control field']:first-child span[class$='label']");
    private static final By CLUSTER_ID = By.cssSelector("div[class$='column-right'] div[class$='control field']:last-child span[class$='label']");

    private static final By SERVER = By.cssSelector("table[id$='rc-membertable'] tbody tr td:nth-of-type(1)");
    private static final By PORT = By.cssSelector("table[id$='rc-membertable'] tbody tr td:nth-of-type(3)");
    private static final By LAST_REGISTERED = By.cssSelector("table[id$='rc-membertable'] tbody tr td:nth-of-type(4)");

    // Offline Cluster members
    private static final By SERVER_OFF = By.cssSelector("table[id$='rc-offlinetable'] tbody tr td:nth-of-type(1)");
    private static final By MEMBERS_IP_OFF = By.cssSelector("table[id$='rc-offlinetable'] tbody tr td:nth-of-type(2)");
    private static final By PORT_OFF = By.cssSelector("table[id$='rc-offlinetable'] tbody tr td:nth-of-type(3)");
    private static final By LAST_REGISTERED_OFF = By.cssSelector("table[id$='rc-offlinetable'] tbody tr td:nth-of-type(4)");
    private static final By REMOVE_FROM_LIST = By.cssSelector("#rc-offlinetable a");


    // Connected Non-Clustered Server(s)
    private static final By SERVER_NON = By.cssSelector("table[id$='rc-nonmemtable'] tbody tr td:nth-of-type(1)");
    private static final By MEMBERS_IP_NON = By.cssSelector("table[id$='rc-nonmemtable'] tbody tr td:nth-of-type(2)");

    private static final By VALIDATE_CLUSTER = By.cssSelector("#validate-cluster-button");
    private static final By ADDITIONAL_DESCRIPTION = By.cssSelector(".info>a");
    private static final By DESCRIPTION_INFO = By.cssSelector(".intro");
    private static final By SERVER_DESCRIPTION = By.cssSelector("div[class='column-left'] span[class='description']");
    private static final By IP_ADDRESS_DESCRIPTION = By.cssSelector("div[class='column-right'] span[class='description']");

    // values of Host Server
    private static final By SERVER_NAME_VALUE = By.cssSelector("div[class$='column-left'] div[class$='control field'] span[class$='value']");
    private static final By IP_ADDRESS_VALUE = By.cssSelector("div[class$='column-right'] div[class$='control field']:first-child span[class$='value']");
    private static final By CLUSTER_ID_VALUE = By.cssSelector("div[class$='column-right'] div[class$='control field']:last-child span[class$='value']");

    private static Log logger = LogFactory.getLog(RepositoryServerClusteringPage.class);

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * @return true if cluster enabled, return false if cluster disabled
     */
    public boolean isClusterEnabled()
    {
        try
        {
            WebElement clusterEnabled = driver.findElement(CLUSTER_ENABLED);
            return clusterEnabled.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

    /**
     * @return number of cluster members
     */
    public int getClusterMembersNumber() {
        try {
            WebElement clusterMembersNumber = findAndWait(CLUSTER_MEMBERS_NUMBER);
            return Integer.parseInt(clusterMembersNumber.getText());
        } catch (NumberFormatException e) {
            throw new PageOperationException("Unable to parse Cluster members number");
        }
    }

    /**
     * @return list of ip address for cluster members
     */
    public List<String> getClusterMembers() {

        try {
            List<String> clusterMembers = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(CLUSTER_MEMBERS_IP);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterMembers.add(webElement.getText());
                }
            }
            return clusterMembers;
        } catch (StaleElementReferenceException e) {
            return getClusterMembers();
        }

    }

    /**
     * Checks if Server Name field present
     *
     * @return boolean
     */
    public boolean isServerNamePresent() {
        try {
            WebElement serverName = driver.findElement(CLUSTER_SERVER_NAME);
            return serverName.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

    /**
     * Checks if Address ID field present
     *
     * @return boolean
     */
    public boolean isIpAddressPresent() {
        try {
            WebElement ipAddress = driver.findElement(CLUSTER_IP_ADDRESS);
            return ipAddress.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

    /**
     * Checks if Cluster ID field present
     *
     * @return boolean
     */
    public boolean isClusterIdPresent() {
        try {
            WebElement clusterId = driver.findElement(CLUSTER_ID);
            return clusterId.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

    /**
     * Gets all Server names from Server Details table
     *
     * @return List<String>
     */
    public List<String> getServerNames() {

        try {
            List<String> serverNames = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(SERVER);

            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    serverNames.add(webElement.getText());
                }
            }
            return serverNames;
        } catch (StaleElementReferenceException e) {
            return getServerNames();
        }

    }

    /**
     * Gets all Server ports from Server Details table
     *
     * @return List<String>
     */
    public List<String> getPorts() {

        try {
            List<String> clusterPorts = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(PORT);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterPorts.add(webElement.getText());
                }
            }
            return clusterPorts;
        } catch (StaleElementReferenceException e) {
            return getPorts();
        }

    }

    /**
     * Gets all Last Registered info from Server Details table
     *
     * @return List<String>
     */
    public List<String> getLastRegs() {

        try {
            List<String> clusterRegs = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(LAST_REGISTERED);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterRegs.add(webElement.getText());
                }
            }
            return clusterRegs;
        } catch (StaleElementReferenceException e) {
            return getLastRegs();
        }

    }

    /**
     * Gets all Server names from Offline Cluster members table
     *
     * @return List<String>
     */
    public List<String> getOffServerNames() {

        try {
            List<String> serverNames = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(SERVER_OFF);

            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    serverNames.add(webElement.getText());
                }
            }
            return serverNames;
        } catch (StaleElementReferenceException e) {
            return getOffServerNames();
        }

    }

    /**
     * Gets all Server IPs from Offline Cluster members table
     *
     * @return List<String>
     */
    public List<String> getOffClusterIps() {

        try {
            List<String> clusterOffIps = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(MEMBERS_IP_OFF);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterOffIps.add(webElement.getText());
                }
            }
            return clusterOffIps;
        } catch (StaleElementReferenceException e) {
            return getOffClusterIps();
        }

    }

    /**
     * Gets all Server ports from Offline Cluster members table
     *
     * @return List<String>
     */
    public List<String> getOffPorts() {

        try {
            List<String> clusterPorts = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(PORT_OFF);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterPorts.add(webElement.getText());
                }
            }
            return clusterPorts;
        } catch (StaleElementReferenceException e) {
            return getOffPorts();
        }

    }

    /**
     * Gets all Last Registered info from Offline Cluster members table
     *
     * @return List<String>
     */
    public List<String> getOffLastRegs() {

        try {
            List<String> clusterRegs = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(LAST_REGISTERED_OFF);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterRegs.add(webElement.getText());
                }
            }
            return clusterRegs;
        } catch (StaleElementReferenceException e) {
            return getOffLastRegs();
        }

    }

    /**
     * Gets all Server names from Connected Non-Clustered Server(s) table
     *
     * @return List<String>
     */
    public List<String> getNonServerNames() {

        try {
            List<String> serverNames = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(SERVER_NON);

            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    serverNames.add(webElement.getText());
                }
            }
            return serverNames;
        } catch (StaleElementReferenceException e) {
            return getNonServerNames();
        }

    }

    /**
     * Gets all Server IPs from Connected Non-Clustered Server(s) table
     *
     * @return List<String>
     */
    public List<String> getNonClusterIps() {

        try {
            List<String> clusterOffIps = new ArrayList<>();
            List<WebElement> elements = findAndWaitForElements(MEMBERS_IP_NON);
            for (WebElement webElement : elements) {
                if (webElement.isDisplayed()) {
                    clusterOffIps.add(webElement.getText());
                }
            }
            return clusterOffIps;
        } catch (StaleElementReferenceException e) {
            return getNonClusterIps();
        }

    }

    /**
     * Checks if Validate Cluster button is present
     *
     * @return boolean
     */
    public boolean isValidateButtonPresent() {
        try {
            WebElement validateButton = driver.findElement(VALIDATE_CLUSTER);
            return validateButton.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

    /**
     * Checks if additional description is present
     *
     * @return boolean
     */
    public boolean isAdditionalDescriptionLinkPresent() {
        try {
            WebElement addLink = driver.findElement(ADDITIONAL_DESCRIPTION);
            return addLink.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

    /**
     * Checks if description is present
     *
     * @return String
     */
    public String getDescriptionText() {
        WebElement descrInfo;
        try {
            descrInfo = driver.findElement(DESCRIPTION_INFO);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + DESCRIPTION_INFO), nse);
        }
        return descrInfo.getText();

    }

    /**
     * Checks if Server description is present
     *
     * @return String
     */
    public String getServerDescriptionText() {
        WebElement descrInfo;
        try {
            descrInfo = driver.findElement(SERVER_DESCRIPTION);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + SERVER_DESCRIPTION), nse);
        }
        return descrInfo.getText();

    }

    /**
     * Checks if IP description is present
     *
     * @return String
     */
    public String getIpDescriptionText() {
        WebElement descrInfo;
        try {
            descrInfo = driver.findElement(IP_ADDRESS_DESCRIPTION);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + IP_ADDRESS_DESCRIPTION), nse);
        }
        return descrInfo.getText();

    }

    /**
     * Gets value of the Server name
     *
     * @return String
     */
    public String getServerNameText() {
        WebElement serverName;
        try {
            serverName = driver.findElement(SERVER_NAME_VALUE);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + SERVER_NAME_VALUE), nse);
        }
        return serverName.getText();

    }

    /**
     * Gets value of the Address IP name
     *
     * @return String
     */
    public String getIpAddressText() {
        WebElement ipAddress;
        try {
            ipAddress = driver.findElement(IP_ADDRESS_VALUE);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + IP_ADDRESS_VALUE), nse);
        }
        return ipAddress.getText();

    }

    /**
     * Gets value of the Cluster ID
     *
     * @return String
     */
    public String getClusterIdText() {
        WebElement clusterId;
        try {
            clusterId = driver.findElement(CLUSTER_ID_VALUE);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + CLUSTER_ID_VALUE), nse);
        }
        return clusterId.getText();

    }

    /**
     * Opens Validate Cluster page
     *
     * @param driver
     * @return
     */
    public ClusterValidationPage getValidationPage(WebDriver driver) 
    {
        try 
        {
            WebElement validateCluster = driver.findElement(VALIDATE_CLUSTER);
            validateCluster.click();
            return factoryPage.instantiatePage(driver, ClusterValidationPage.class);
        }
        catch (NoSuchElementException nse) 
        {
            if (logger.isTraceEnabled()) 
            {
                logger.trace("Button " + VALIDATE_CLUSTER + " isn't found", nse);
            }
        }
        throw new PageOperationException("Page isn't opened");
    }

    public void clickRemove(WebDriver driver) 
    {
        WebElement remove = findAndWait(REMOVE_FROM_LIST);
        remove.click();
    }

}
