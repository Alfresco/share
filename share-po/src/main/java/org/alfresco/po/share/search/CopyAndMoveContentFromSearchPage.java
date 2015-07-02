package org.alfresco.po.share.search;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.ShareDialogue;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.WebDroneUtil;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

public class CopyAndMoveContentFromSearchPage extends ShareDialogue
{

    private static Log logger = LogFactory.getLog(CopyAndMoveContentFromSearchPage.class);

    private final RenderElement FOOTER_ELEMENT = getVisibleRenderElement(By.cssSelector("div[class='footer']"));
    private final RenderElement HEADER_ELEMENT = getVisibleRenderElement(By.cssSelector("div[class='dijitDialogTitleBar']"));
    private final RenderElement DOC_LIST_VIEW = getVisibleRenderElement((By.cssSelector("div[id^='alfresco_documentlibrary_views_AlfDocumentListView']")));
    private final By destinationListCss = By
            .cssSelector("div[class='sub-pickers']>div[class^='alfresco-menus-AlfMenuBar']>div>div[class^='dijitReset dijitInline']>span");
    private final By copyMoveOkOrCancelButtonCss = By.cssSelector("div[class='footer']>span");
    private final By copyMoveDialogCloseButtonCss = By.cssSelector("div[class='dijitDialogTitleBar']>span[class^=dijitDialogCloseIcon ]");
    private final By copyMoveDialogTitleCss = By.cssSelector("div[class='dijitDialogTitleBar']>span[class='dijitDialogTitle']");
    private String PathFolderCss = "//div[starts-with(@id,'alfresco_documentlibrary_views_AlfDocumentListView')] //tr/td/span/span/span[@class='value'][text()='%s']";
    private String adButton = "//div[starts-with(@id,'alfresco_documentlibrary_views_AlfDocumentListView')] //tr/td/span/span/span[@class='value'][text()='%s']/../../../../td/span[starts-with(@id, 'alfresco_renderers_PublishAction')]";

    // private final By disabledBackCss = By.cssSelector("div[class$='dijitMenuItem dijitMenuItemDisabled dijitDisabled']>span[id$='PAGE_BACK_text']");
    // private final By disabledNextCss = By.cssSelector("div[class$='dijitMenuItem dijitMenuItemDisabled dijitDisabled']>span[id$='PAGE_FORWARD_text']");
    private final By nextCss = By.cssSelector("div[class$='dijitReset dijitInline dijitMenuItemLabel dijitMenuItem']>span[id$='PAGE_FORWARD_text']");
    private final By backCss = By.cssSelector("div[class$='dijitReset dijitInline dijitMenuItemLabel dijitMenuItem']>span[id$='PAGE_BACK_text']");

    /**
     * Constructor.
     * 
     * @param drone WebDriver to access page
     */
    public CopyAndMoveContentFromSearchPage(WebDrone drone)
    {
        super(drone);

    }

    @Override
    public CopyAndMoveContentFromSearchPage render(RenderTime timer)
    {
        elementRender(timer, HEADER_ELEMENT, FOOTER_ELEMENT);
        return this;
    }
    
    public CopyAndMoveContentFromSearchPage renderDocumentListView(long timeInMilliSceonds)
    {
        elementRender(new RenderTime(timeInMilliSceonds), DOC_LIST_VIEW);
        return this;
    }

    @Override
    public CopyAndMoveContentFromSearchPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @Override
    public CopyAndMoveContentFromSearchPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
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
            title = drone.findAndWait(copyMoveDialogTitleCss).getText();
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
        List<String> destinations = new ArrayList<String>();
        try
        {
            for (WebElement destination : drone.findAndWaitForElements(destinationListCss))
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
     * This method finds the clicks on copy/move/cancel button.
     * 
     * @param buttonName String
     * @return HtmlPage FacetedSerachResultsPage
     */
    private FacetedSearchPage selectCopyOrMoveOrCancelButton(String buttonName)
    {
        if (StringUtils.isEmpty(buttonName))
        {
            throw new IllegalArgumentException("button name is required");
        }

        try
        {
            for (WebElement button : drone.findAndWaitForElements(copyMoveOkOrCancelButtonCss))
            {
                if (button.getText() != null)
                {
                    if (button.getText().equalsIgnoreCase(buttonName))
                    {
                        button.click();
                        drone.waitForPageLoad(WAIT_TIME_3000);
                        return new FacetedSearchPage(drone);

                    }
                }
            }
            throw new PageOperationException("Unable to find the button: " + buttonName);
        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the inner text of button" + buttonName, ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the button" + buttonName, e);
            }
        }

        throw new PageOperationException("Unable to select button." + buttonName);
    }

    /**
     * This method finds the clicks on 'Copy' button in Copy/Move pop up page
     */
    public FacetedSearchPage selectCopyButton()
    {
        return selectCopyOrMoveOrCancelButton("Copy");
    }

    public FacetedSearchPage selectCancelButton()
    {
        return selectCopyOrMoveOrCancelButton("Cancel");
    }

    public FacetedSearchPage selectMoveButton()
    {
        return selectCopyOrMoveOrCancelButton("Move");
    }

    /**
     * This method finds the clicks on close button in copy and move dialog page
     * 
     * @return FacetedSearchPage
     */
    public FacetedSearchPage selectCloseButton()
    {
        try
        {
            drone.findAndWait(copyMoveDialogCloseButtonCss).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the close button Css : ", e);
            }
            throw new PageException("Unable to find the close button on Copy/Move Dialog.");
        }
        return new FacetedSearchPage(drone);
    }

    /**
     * This method finds and selects the given destination from the
     * displayed list of destinations in CopyAndMoveContentFromSearchPage
     * 
     * @param destinationName String
     * @return CopyOrMoveContentPage
     */
    public CopyAndMoveContentFromSearchPage selectDestination(String destinationName)
    {
        WebDroneUtil.checkMandotaryParam("destinationName", destinationName);
        try
        {
            for (WebElement destination : drone.findAndWaitForElements(destinationListCss))
            {
                if (destination.getText() != null)
                {
                    if (destination.getText().equalsIgnoreCase(destinationName))
                    {
                        destination.click();
                    }

                }

            }

            return this;
        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the inner text of destionation", ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations", e);
            }
        }

        throw new PageOperationException("Unable to select Destination : " + destinationName);
    }

    /**
     * This method finds and selects any folder from repository in CopyAndMoveContentFromSearchPage
     * This method is used when Repository is selected as destination
     * 
     * @param repoFolder String
     * @return CopyOrMoveContentPage
     */
    public CopyAndMoveContentFromSearchPage selectFolderInRepo(String repoFolder)
    {

        WebDroneUtil.checkMandotaryParam("repoFolder", repoFolder);

        try
        {

            By finalFolderElement = By.xpath(String.format(PathFolderCss, repoFolder));
            WebElement element = drone.findAndWait(finalFolderElement);
            if (!element.isDisplayed())
            {
                element.click();
            }
            element.click();

            return this;
        }
        catch (NoSuchElementException ne)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to find the inner text of destionation", ne);
            }
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Unable to get the list of destionations", e);
            }
        }

        throw new PageOperationException("Unable to select sites : ");
    }

    /**
     * This method can be used to select any folder in the site, given destination only from repository/Sites
     * This method is used when Repository/Sites are selected in CopyAndMoveContentFromSearchPage
     * 
     * @param paths String
     * @return CopyOrMoveContentPage
     */
    public CopyAndMoveContentFromSearchPage selectFolder(String... paths)
    {
        WebDroneUtil.checkMandotaryParam("paths", paths);

        try
        {
            int pathsLength = paths.length;
            int pathCount = 1;
            String finalPath = "";
            for (String path : paths)
            {
                finalPath = path;
                if (pathCount >= pathsLength)
                {
                    break;
                }
                By subpath = By.xpath(String.format(PathFolderCss, path));
                scrollDwon();
                if ((!isNextButtonEnabled()) && (!isBackButtonEnabled()))
                {
                    drone.waitForElement(subpath, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    drone.findAndWait(subpath).click();
                }
                else if (!drone.isElementDisplayed(subpath))
                {
                    while (isNextButtonEnabled() || !(drone.isElementDisplayed(subpath)))
                    {
                        scrollDwon();
                        selectNextButton();
                        if (drone.isElementDisplayed(subpath))
                        {
                           break;
                        }
                    }
                }
                else if (!drone.isElementDisplayed(subpath) && (!isNextButtonEnabled()))
                {
                    throw new PageOperationException("Next button enabled when site is displayed");
                }
                if (drone.isElementDisplayed(subpath))
                {
                    drone.waitForElement(subpath, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                    drone.findAndWait(subpath).click();
                }
                pathCount++;
            }

            if (!finalPath.isEmpty())
            {
                By finalFolderElement = By.xpath(String.format(PathFolderCss, finalPath));
                drone.waitForElement(finalFolderElement, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                drone.waitForElement(By.xpath(String.format(adButton, finalPath)), SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
                drone.find(By.xpath(String.format(adButton, finalPath))).click();
            }

            return this;

        }

        catch (NoSuchElementException ne)
        {
                 System.out.println(ne.getMessage());
                logger.trace("Unable to select the final folder", ne);
        }
        catch (TimeoutException e)
        {
                System.out.println(e.getMessage());
                logger.trace("Unable to select the final folder ", e);
        }

        throw new PageOperationException("Unable to select the final folder");
    }

    /**
     * This method finds the clicks on next button in CopyAndMoveContentFromSearchPage
     */
    public CopyAndMoveContentFromSearchPage selectNextButton()
    {
        selectButton(nextCss, "Unable to find the Next button on Copy/Move Dialog.");
        return this;
    }

    /**
     * This method finds the clicks on back button in CopyAndMoveContentFromSearchPage
     */
    public CopyAndMoveContentFromSearchPage selectBackButton()
    {
        selectButton(backCss, "Unable to find the close button on Copy/Move Dialog.");
        return this;
    }

    /**
     * This helper method finds the clicks on next/back button in CopyAndMoveContentFromSearchPage
     */
    private void selectButton(By css, String message)
    {
        try
        {
            if (drone.findAndWait(css).isEnabled())
            {
                drone.findAndWait(css).click();
            }

        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace(message, e);
            }
            throw new PageException(message);
        }
    }

    /**
     * Helper method to return true if Next Button is displayed and enabled
     * 
     * @return boolean <tt>true</tt> is Next Button is displayed and enabled
     */
    public boolean isNextButtonEnabled()
    {
        try
        {
            if (drone.findAndWait(nextCss).isDisplayed() && drone.findAndWait(nextCss).isEnabled())
            {
                return true;
            }

        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Helper method to return true if Back Button is displayed and enabled
     * 
     * @return boolean <tt>true</tt> is Back Button is displayed and enabled
     */
    public boolean isBackButtonEnabled()
    {
        try
        {
            if (drone.findAndWait(backCss).isDisplayed() && drone.findAndWait(backCss).isEnabled())
            {
                return true;
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    /**
     * Helper method to click on the page marker in copy and move dialog page
     * 
     * @return CopyAndMoveContentFromSearchPage
     */
    public CopyAndMoveContentFromSearchPage scrollDwon()
    {              
        By paginatorCss = By.cssSelector("div[id^='alfresco_documentlibrary_AlfDocumentListPaginator'] div[id$='PAGE_MARKER']");
        try
        {
            WebElement paginator = drone.findAndWait(paginatorCss);
            paginator.click();
            return this;
        }
        catch (TimeoutException e)
        {
           throw new PageOperationException("Unable to click on paginator", e);
        }
    }

}
