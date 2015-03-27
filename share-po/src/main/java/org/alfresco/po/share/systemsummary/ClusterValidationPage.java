package org.alfresco.po.share.systemsummary;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
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


    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(this.getClass());


    public ClusterValidationPage(WebDrone drone) {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClusterValidationPage render(RenderTime timer) {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClusterValidationPage render() {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ClusterValidationPage render(final long time) {
        return render(new RenderTime(time));
    }

    /**
     * Checks if Close button present at a page
     *
     * @return
     */
    public boolean isCancelButtonPresent() {
        try {
            WebElement serverName = drone.find(CLOSE_BUTTON);
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
            List<WebElement> elements = drone.findAndWaitForElements(NODES);
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
     * @return
     */
    public String getTitle() {
        try {
            return drone.findAndWait(TITLE).getText();
        } catch (TimeoutException toe) {
            throw new PageOperationException("Title isn't present", toe);
        }

    }

    /**
     * Close Validate Cluster page
     *
     * @param drone
     * @return
     */
    public RepositoryServerClusteringPage closeValidationPage(WebDrone drone) {

        try {
            WebElement validateCluster = drone.find(CLOSE_BUTTON);
            validateCluster.click();
            return new RepositoryServerClusteringPage(drone);
        } catch (NoSuchElementException nse) {
            if (logger.isTraceEnabled()) {
                logger.trace("Button " + CLOSE_BUTTON + " isn't found", nse);
            }
        }
        throw new PageOperationException("Page isn't opened");
    }

    public HtmlPage clickClose()
    {
        try
        {
            drone.findAndWait(CLOSE_BUTTON).click();
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able find the close button " + e);
        }
        catch (StaleElementReferenceException ser)
        {
            return clickClose();
        }
        return FactorySharePage.resolvePage(drone);
    }

    public String getValidationResult() {
        try {
            return drone.findAndWait(VALIDATTION_RESULT).getText();
        } catch (TimeoutException toe) {
            throw new PageOperationException("No result is present", toe);
        }

    }
}
