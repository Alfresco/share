package org.alfresco.po.share.systemsummary;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
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


    public RepositoryServerClusteringPage(WebDrone drone) {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render(RenderTime timer) {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render() {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryServerClusteringPage render(final long time) {
        return render(new RenderTime(time));
    }

    /**
     * @return true if cluster enabled, return false if cluster disabled
     */
    public boolean isClusterEnabled() {
        try {
            WebElement clusterEnabled = drone.find(CLUSTER_ENABLED);
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
            WebElement clusterMembersNumber = drone.findAndWait(CLUSTER_MEMBERS_NUMBER);
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
            List<WebElement> elements = drone.findAndWaitForElements(CLUSTER_MEMBERS_IP);
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
            WebElement serverName = drone.find(CLUSTER_SERVER_NAME);
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
            WebElement ipAddress = drone.find(CLUSTER_IP_ADDRESS);
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
            WebElement clusterId = drone.find(CLUSTER_ID);
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
            List<WebElement> elements = drone.findAndWaitForElements(SERVER);

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
            List<WebElement> elements = drone.findAndWaitForElements(PORT);
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
            List<WebElement> elements = drone.findAndWaitForElements(LAST_REGISTERED);
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
            List<WebElement> elements = drone.findAndWaitForElements(SERVER_OFF);

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
            List<WebElement> elements = drone.findAndWaitForElements(MEMBERS_IP_OFF);
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
            List<WebElement> elements = drone.findAndWaitForElements(PORT_OFF);
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
            List<WebElement> elements = drone.findAndWaitForElements(LAST_REGISTERED_OFF);
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
            List<WebElement> elements = drone.findAndWaitForElements(SERVER_NON);

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
            List<WebElement> elements = drone.findAndWaitForElements(MEMBERS_IP_NON);
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
            WebElement validateButton = drone.find(VALIDATE_CLUSTER);
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
            WebElement addLink = drone.find(ADDITIONAL_DESCRIPTION);
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
            descrInfo = drone.find(DESCRIPTION_INFO);
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
            descrInfo = drone.find(SERVER_DESCRIPTION);
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
            descrInfo = drone.find(IP_ADDRESS_DESCRIPTION);
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
            serverName = drone.find(SERVER_NAME_VALUE);
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
            ipAddress = drone.find(IP_ADDRESS_VALUE);
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
            clusterId = drone.find(CLUSTER_ID_VALUE);
        } catch (NoSuchElementException nse) {
            throw new PageException(String.format("Unable to locate element: " + CLUSTER_ID_VALUE), nse);
        }
        return clusterId.getText();

    }

    /**
     * Opens Validate Cluster page
     *
     * @param drone WebDrone
     * @return ClusterValidationPage
     */
    public ClusterValidationPage getValidationPage(WebDrone drone) {

        try {
            WebElement validateCluster = drone.find(VALIDATE_CLUSTER);
            validateCluster.click();
            return new ClusterValidationPage(drone);
        } catch (NoSuchElementException nse) {
            if (logger.isTraceEnabled()) {
                logger.trace("Button " + VALIDATE_CLUSTER + " isn't found", nse);
            }
        }
        throw new PageOperationException("Page isn't opened");
    }

    public void clickRemove(WebDrone drone) {


            WebElement remove = drone.findAndWait(REMOVE_FROM_LIST);
            remove.click();

    }

}
