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
package org.alfresco.po.share;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.util.List;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.Page;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.search.SearchBox;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

/**
 * Abstract of an Alfresco Share page object which contains helper methods
 * that are only applicable when on a share based page.
 * 
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 */
public abstract class SharePage extends Page
{

    private Log logger = LogFactory.getLog(this.getClass());
    private static final By PAGE_TITLE_LINK = By.cssSelector("#HEADER_TITLE span a");
    protected static final By USER_LOGGED_IN_LABEL = By.cssSelector("#HEADER_USER_MENU_POPUP_text");
    protected static final By PROMPT_PANEL_ID = By.id("prompt");
    protected long popupRendertime;
    protected long elementWaitInSeconds = 1;
    protected static By LICENSE_TO = By.cssSelector(".licenseHolder");
    private static String COPYRIGHT_SEARCH_STRING = " All rights reserved.";
    protected static final By CONFIRM_DELETE = By.xpath("//span[@class='button-group']/span[1]/span/button");
    protected static final By CANCEL_DELETE = By.xpath("//span[@class='button-group']/span[2]/span/button");
    private final static By TOP_LOGO = By.xpath("//div[@id='HEADER_LOGO']/img");
    private final static By FOOTER_LOGO = By.xpath("//span[@class='copyright']/a/img");
    private static final By PAGE_TITLE_LABEL = By.cssSelector("span[id^=alfresco_html_Label]");
    private Navigation nav;

    /**
     * Check if javascript message is displayed.
     * The message details the background action taking place.
     * Some possible messages are document being uploaded, site
     * being created.
     * 
     * @return if message displayed
     */
    protected boolean isJSMessageDisplayed()
    {
        try
        {
            return driver.findElement(By.cssSelector("div.bd")).isDisplayed();
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
        catch (StaleElementReferenceException ser)
        {
            driver.navigate().refresh();
            return isJSMessageDisplayed();
        }
    }

    /**
     * Verify if the Alfresco logo is present on the page.
     * 
     * @return true if logo element exists
     */
    public boolean isLogoPresent()
    {
        return isElementDisplayed(By.cssSelector("div.alfresco-logo-Logo img"));
    }

    /**
     * Alfresco share based layout and style page title label element.
     * 
     * @return String page title label
     */
    public String getPageTitle()
    {
        return driver.findElement(By.cssSelector("#HEADER_TITLE")).getText().trim();
    }
    
    /**
     * Page title label
     * 
     * 
     * @return
     */
    public String getPageTitleLabel()
    {
        String pageTitleLabel = "";
        try
        {
            waitForElement(PAGE_TITLE_LABEL, SECONDS.convert(defaultWaitTime, MILLISECONDS));
            pageTitleLabel = findAndWait(PAGE_TITLE_LABEL).getText();
        } catch (TimeoutException toe)
        {
            
        }
        return pageTitleLabel;
    }

    
    /**
     * Verify share page title is present and matches the page
     * 
     * @return true if exists
     */
    public boolean isTitlePresent(final String title)
    {
        boolean titleExists = false;
        try
        {
            titleExists = title.equalsIgnoreCase(getPageTitle());
        }
        catch (NoSuchElementException e)
        {
        }
        return titleExists;
    }
    
    /**
     * Clicks on page title link
     * @return
     */
    public HtmlPage clickOnPageTitle()
    {
        findAndWait(PAGE_TITLE_LINK).click();
        return getCurrentPage();
    }

    /**
     * Verifies if the element is visible on the page.
     * 
     * @param panelName the css location of the element
     * @return true if element is visible
     */
    public boolean panelExists(String panelName)
    {
        return isElementDisplayed(By.cssSelector(panelName));
    }

    /**
     * Gets the {@link LoginPage}
     * 
     * @return LoginPage page object
     */
    public HtmlPage getLogin()
    {
        return getCurrentPage();
    }

    /**
     * Get the {@link Navigation}
     * 
     * @return Navigation page object
     */
    public Navigation getNav()
    {
        return nav;
    }

    /**
     * Perform inputing a search term in to the search box on the main
     * navigation.
     * 
     * @return Search page object
     */
    public SearchBox getSearch()
    {
        return factoryPage.instantiatePage(driver, SearchBox.class).render();
    }

    /**
     * Helper to resolve the delete button from the collection of buttons.
     * 
     * @param button String button name value to find
     * @param elements List<WebElement> collection of buttons
     * @return {@link WebElement} delete button
     */
    public WebElement findButton(final String button, List<WebElement> elements)
    {
        WebElement result = null;
        for (WebElement element : elements)
        {
            String siteTitle = element.getText();
            if (button.equalsIgnoreCase(siteTitle))
            {
                result = element;
            }
        }
        if (result == null)
        {
            throw new NoSuchElementException("Can not find the delete button");
        }
        return result;
    }

    /**
     * Helper method to disable flash on file upload component, as flash is not
     * supported by WebDriver.
     */
    public void disableFileUploadFlash()
    {
        executeJavaScript("Alfresco.util.ComponentManager.findFirst('Alfresco.FileUpload').options.adobeFlashEnabled=false;");
    }

    /**
     * Change share file upload to single mode upload.
     * As selenium is unable to interact with flash we disable the normal file upload mode.
     * In addtion firefox does not display the html5 input element to send data hence
     * we use single mode.
     */
    public void setSingleMode()
    {
        executeJavaScript("var singleMode=Alfresco.util.ComponentManager.findFirst('Alfresco.HtmlUpload'); Alfresco.util.ComponentManager.findFirst('Alfresco.FileUpload').uploader=singleMode;");
    }

    /**
     * Verifies if a user is currently logged in
     * 
     * @return true if user is logged in
     */
    public boolean isLoggedIn()
    {
        return isElementDisplayed(USER_LOGGED_IN_LABEL);
    }

    /**
     * Get copy right text from alfresco footer.
     * 
     * @return String copy right text.
     */
    public String getCopyRight()
    {
        WebElement elemenent = findAndWait(By.cssSelector("span.copyright"));
        return elemenent.getText();
    }

    /**
     * Waits for site pop up message to disappear to allow the driver to resume
     * operations on the page.
     * 
     * @param waitTime timer in milliseconds
     * @return true if message has gone
     */
    protected boolean canResume(final long waitTime)
    {
        RenderTime timer = new RenderTime(waitTime);
        boolean messagePresent = true;
        while (messagePresent)
        {
            try
            {
                timer.start();
                WebElement deletedMessage = findAndWait(By.cssSelector("div.bd"));
                messagePresent = deletedMessage.isDisplayed();
                timer.end();
            }
            catch (TimeoutException te)
            {
                messagePresent = false;
            }
            catch (StaleElementReferenceException se)
            {
                canResume(waitTime);
            }
            catch (NoSuchElementException nse)
            {
                messagePresent = false;
            }
            catch (PageRenderTimeException pe)
            {
                messagePresent = false;
                // if exception was thrown and caught, then the popup message
                // was not on
                // the page.
            }
        }
        return true;
    }

    /**
     * Default wait for site pop up message to disappear. Once the popup disappears
     * driver can resume operations on the page as the focus is
     * off the div.bd and back on the main page.
     * 
     * @return true if message has gone
     */
    protected boolean canResume()
    {
        return canResume(getDefaultWaitTime());
    }


    /**
     * Wait for file to be present given path for maximum page loading time.
     * 
     * @param pathname Absolute Path Name with File Name.
     */
    public void waitForFile(final long time, String pathname)
    {
        waitForFile(new RenderTime(time), pathname);
    }

    /**
     * Wait for file to be present given path for maximum page loading time.
     * 
     * @param pathname Absolute Path Name with File Name.
     */
    public void waitForFile(String pathname)
    {
        waitForFile(new RenderTime(maxPageLoadingTime), pathname);
    }

    /**
     * Wait for file to be present given path.
     * 
     * @param renderTime Render Time
     * @param pathname Absolute Path Name with File Name.
     */
    protected void waitForFile(RenderTime renderTime, String pathname)
    {
        while (true)
        {
            try
            {
                renderTime.start();
                File file = new File(pathname);
                if (file.exists())
                {
                    break;
                }
            }
            finally
            {
                renderTime.end();
            }
        }
    }

    /**
     * <li>Click the element which passed and wait for given ElementState on the same element.</li> <li>If the Element State not changed, then render the
     * {@link SharePopup} Page, if it is rendered the return {@link SharePopup} page.</li>
     * 
     * @param locator By
     * @param elementState ElementState
     * @return {@link HtmlPage}
     */
    protected HtmlPage submit(By locator, ElementState elementState)
    {
        WebElement button = findFirstDisplayedElement(locator);
        String id = button.getAttribute("id");
        button.click();
        By locatorById = By.id(id);
        RenderTime time = new RenderTime(maxPageLoadingTime);
        time.start();
        while (true)
        {
            try
            {
                switch (elementState)
                {
                    case INVISIBLE:
                        waitUntilElementDisappears(locatorById, elementWaitInSeconds);
                        break;
                    case DELETE_FROM_DOM:
                        waitUntilElementDeletedFromDom(locatorById, elementWaitInSeconds);
                        break;
                    default:
                        throw new UnsupportedOperationException(elementState + "is not currently supported by submit.");
                }
            }
            catch (TimeoutException e)
            {
                try
                {
                    SharePopup errorPopup = getCurrentPage().render();
                    errorPopup.render(new RenderTime(popupRendertime));
                    return errorPopup;
                }
                catch (PageRenderTimeException| ClassCastException exception)
                {
                    logger.info("Error Submitting the page:", exception);
                    continue;
                }
            }
            finally
            {
                time.end(locatorById.toString());
            }
            break;
        }
        return getCurrentPage().render();
    }

    /**
     * Method to get element text for given locator.
     * If the element is not found, returns empty string
     * 
     * @param locator By
     * @return String
     */
    public String getElementText(By locator)
    {
        try
        {
            return findAndWait(locator).getText();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Element not found" + locator.toString(), nse);
            }
        }
        return "";
    }

    public long getPopupRendertime()
    {
        return popupRendertime;
    }

    public void setPopupRendertime(long popupRendertime)
    {
        this.popupRendertime = popupRendertime;
    }

    public void setElementWaitInSeconds(long elementWaitInSeconds)
    {
        this.elementWaitInSeconds = elementWaitInSeconds;
    }

    /**
     * Helper to consistently get the Site Short Name.
     * 
     * @param siteName String Name of the test for uniquely identifying / mapping test data with the test
     * @return String site short name
     */
    public static String getSiteShortName(String siteName)
    {
        String siteShortName = "";
        String[] unAllowedCharacters = { "_", "!" };

        for (String removeChar : unAllowedCharacters)
        {
            siteShortName = siteName.replace(removeChar, "");
        }

        return siteShortName.toLowerCase();
    }

    /**
     * Wait until the black message box appear with text then wait until same black message disappear with text.
     * 
     * @param text - Text to be checked in the black message.
     * @param timeInSeconds - Time to wait in seconds.
     */
    protected void waitUntilMessageAppearAndDisappear(String text, long timeInSeconds)
    {
        waitUntilElementDisappears(By.cssSelector("div.bd>span.message"), timeInSeconds);
    }

    /**
     * Return the {@link RenderElement} of the action message.
     * Checks that the black box with the message is not showing
     * on the page or showing pending state passed invisible vs visible.
     * 
     * @param state {@link ElementState} the visiblity state of element
     * @return {@link RenderElement} of action message based on state
     */
    public RenderElement getActionMessageElement(ElementState state)
    {
        String messageSelector = "div#message, div.bd";
        return new RenderElement(By.cssSelector(messageSelector), ElementState.INVISIBLE);
    }

    /**
     * Find the all the elements for given locator and returns the first visible {@link WebElement}.
     * It could be used to elemanate the hidden element with same locators.
     * 
     * @param locator By
     * @return {@link WebElement}
     */
    protected WebElement getVisibleElement(By locator)
    {
        List<WebElement> searchElements = driver.findElements(locator);
        for (WebElement webElement : searchElements)
        {
            if (webElement.isDisplayed())
            {
                return webElement;
            }
        }
        throw new PageOperationException("Not able find the visible element for given locator : " + locator.toString());
    }

    /**
     * Returns the validation message from the validation popup balloon for the web element
     * or an empty string if there is no message or the field is not validated.
     * 
     * @param locator By
     * @return The validation message
     */
    public String getValidationMessage(By locator)
    {
        String message = "";
        try
        {
            message = driver.findElement(locator).getAttribute("alf-validation-msg");
        }
        catch (NoSuchElementException exception)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace(exception + locator.toString());
            }
        }

        return (message == null ? "" : message);
    }

    /**
     * Returns the validation message from the validation popup balloon for the web element
     * or an empty string if there is no message or the field is not validated.
     * 
     * @param webElement WebElement
     * @return The validation message
     */
    public String getValidationMessage(WebElement webElement)
    {
        String message = webElement.getAttribute("alf-validation-msg");

        return (message == null ? "" : message);
    }

    /**
     * Method for wait while balloon message about some changes hide.
     */
    public SharePage waitUntilAlert()
    {
        final long WAIT_DELETE_FROM_DOM = getDefaultWaitTime() / 100;
        return waitUntilAlert(WAIT_DELETE_FROM_DOM);
    }

    /**
     * Method for wait while balloon message about some changes hide.
     * 
     * @param seconds long
     */
    public SharePage waitUntilAlert(long seconds)
    {
        final long WAIT_ALERT_PRESENT = 1; // hardcoded - possible temporary excess in most cases.
        try
        {
            By AlertMessage = By.xpath(".//*[@id='message']/div/span");
            waitUntilElementPresent(AlertMessage, WAIT_ALERT_PRESENT);
            waitUntilElementDeletedFromDom(AlertMessage, seconds);
        }
        catch (TimeoutException ex)
        {
            if (logger.isDebugEnabled())
            {
                logger.error("Alert message hide quickly", ex);
            }
        }
        return this;
    }

    /**
     * Select the given option with text matching the argument in select options list.
     * 
     * @param by the selector of the Select dropdown.
     * @param text The visible text to match against
     */
    public void selectOption(By by, String text)
    {
        Select select = new Select(findAndWait(by));
        select.selectByVisibleText(text);
    }

    /**
     * Accept inputs from keyborad on page level.
     * 
     * @param inputs Keys...
     */
    public void inputFromKeyborad(Keys... inputs)
    {
        Actions actions = new Actions(driver);
        actions.sendKeys(inputs);
        actions.perform();
    }

    /**
     * Get Footer page which has license details.
     * 
     * @return FootersPage
     */
    public FootersPage getFooter()
    {
        try
        {
            driver.findElement(By.cssSelector("div[class^='footer'] span.copyright a")).click();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No footer page exist" + nse.getMessage());
            }
        }

        return factoryPage.instantiatePage(driver, FootersPage.class).render();
    }

    /**
     * @return String
     */
    public String getLicenseHolder()
    {
        try
        {
            return driver.findElement(LICENSE_TO).getText();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("License details are not present" + nse.getMessage());
            }
        }
        throw new PageOperationException("License details are not present");
    }

    /**
     * @return String
     */
    public String getCopyRightDetails()
    {
        try
        {
            for (WebElement element : driver.findElements(By.cssSelector(".copyright>span")))
            {
                if (element.getText().contains(COPYRIGHT_SEARCH_STRING))
                {
                    return element.getText();
                }

            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("CopyRight details are not present" + nse.getMessage());
            }
        }
        throw new PageOperationException("CopyRight details are not present");
    }

    /**
     * Get background color of element or color of element (font color)
     * 
     * @param locator By
     * @param background if needed to find color of element's background - param must be true
     *            if needed to find color of element itself - param must be false
     * @return hex
     *         return color in Hex color model
     */
    public String getColor(By locator, boolean background)
    {
        WebElement element;
        String hex = "";
        String color;
        try
        {
            element = findAndWait(locator);
            if (background)
                color = element.getCssValue("background-color");
            else
                color = element.getCssValue("color");

            String[] numbers = color.replace("rgba(", "").replace(")", "").split(",");
            int number1 = Integer.parseInt(numbers[0]);
            numbers[1] = numbers[1].trim();
            int number2 = Integer.parseInt(numbers[1]);
            numbers[2] = numbers[2].trim();
            int number3 = Integer.parseInt(numbers[2]);
            hex = String.format("#%02x%02x%02x", number1, number2, number3);

        }
        catch (StaleElementReferenceException e)
        {
            getColor(locator, background);
        }

        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find " + locator + " locator");
            }
        }

        return hex;
    }

    /**
     * Open About popUp from footer.
     * 
     * @return AboutPopUp
     */
    public AboutPopUp openAboutPopUp()
    {
        findAndWait(FOOTER_LOGO).click();
        return new AboutPopUp(driver);
    }

    /**
     * Return Top Logo image url.
     * 
     * @return String
     */
    public String getTopLogoUrl()
    {
        return findAndWait(TOP_LOGO).getAttribute("src");
    }

    /**
     * Return Footer Logo image Url
     * 
     * @return String
     */
    public String getFooterLogoUrl()
    {
        return findAndWait(FOOTER_LOGO).getAttribute("src");
    }
    
    public WebElement findByKey(final String id)
    {
        By criteria = By.id(getValue(id));
        return driver.findElement(criteria);
    }
    
    /**
     * Common method to wait for the next solr indexing cycle.
     * 
     * @param driver WebDriver Instance
     * @param waitMiliSec Wait duration in milliseconds
     */
    public HtmlPage webDriverWait(long waitMiliSec)
    {
        synchronized (this)
        {
            try
            {
                this.wait(waitMiliSec);
            }
            catch (InterruptedException e)
            {
                // Discussed not to throw any exception
            }
        }
        return this;
    }
}
