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
package org.alfresco.po;

import com.google.common.base.Predicate;
import org.alfresco.po.exception.ElementExpectedConditions;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.exception.PageRenderTimeException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Abstract that holds all common functions and helper method
 * that applies to components of a page. These building blocks
 * constructs the page and interacts with the dom using selenium.
 * 
 * @author Michael Suzuki
 *
 */
public abstract class PageElement extends HtmlElement implements WebDriverAware
{
    private Log logger = LogFactory.getLog(PageElement.class);
    private static final String LOCATOR_REQUIRED_ERR_MSG = "A locator is required";
	protected long defaultWaitTime = 4000;
    protected long maxPageLoadingTime = 30000;
    @Autowired
    protected FactoryPage factoryPage;
    protected WebDriver driver;
    public void setWebDriver(WebDriver driver) 
    { 
        this.driver = driver; 
    }
    public WebDriver getWebDriver()
    {
        return driver;
    }
    /**
     * Checks if page event action completed. For further information see:
     * https:
     * //dvcs.w3.org/hg/webperf/raw-file/tip/specs/NavigationTiming/Overview
     * .html
     * 
     * @return String time took to complete event
     */
    public String domEventCompleted()
    {
        String js = "try{window.performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {};"
                    + "return(parseInt(window.performance.timing.domContentLoadedEventEnd)-parseInt(window.performance.timing.navigationStart));}catch(e){}";
                        Object val = ((JavascriptExecutor) driver).executeScript(js);
        return val.toString();
    }

    public void navigate(String ...url)
    {
        driver.navigate().to(url[0]);
    }
    
    public boolean isDisplayed(WebElement element)
    {
        try
        {
            return element.isDisplayed();
        }
        catch(Exception e)
        {
            //Ignore.
        }
        return false;
    }
    public boolean isDisplayed(By by)
    {
        try
        {
            return driver.findElement(by).isDisplayed();
        }
        catch(Exception e)
        {
        }
        return false;
    }
    
    public HtmlPage getCurrentPage() 
    {
        return factoryPage.getPage(driver);
    }
    /**
     * Basic render that checks if the page has rendered.
     *
     * @param timer {@link RenderTime}
     */
    public void basicRender(RenderTime timer)
    {
        try
        {
            timer.start();
            isRenderComplete(timer.timeLeft());
        }
        finally
        {
            timer.end();
        }
    }
    /**
     * Verify if loading of the page is complete using java script to validate state of the HTML DOM.
     * 
     * @param waitTime max time to look for an element
     * @return true if page has been loaded
     */
    public boolean isRenderComplete(final long waitTime)
    {
        FluentWait<String> fluentWait = new FluentWait<String>("complete");
        fluentWait.pollingEvery(100, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(waitTime, TimeUnit.MILLISECONDS);
        try
        {
            fluentWait.until(new Predicate<String>()
            {
                @Override
                public boolean apply(String input)
                {
                    String response = (String) executeJavaScript("return document.readyState;");
                    return response.equals(input);
                }
            });
            return true;
        }
        catch (TimeoutException te)
        {
        }
        return false;
    }
    /**
     * Injects java script directly to the page to invoke a change on the page's java script
     * 
     * @param js String script
     */
    public Object executeJavaScript(final String js)
    {
        if(js == null || js.isEmpty())
        {
            throw new IllegalArgumentException("JavaScript is required");
        }
        return ((JavascriptExecutor) driver).executeScript(js);
    }
    /**
     * Execute java script in resepect of object (i.e. WebElement)  
     * @param js String script
     */
    public Object executeJavaScript(final String js, Object...args)
    {
        if(js == null || js.isEmpty())
        {
            throw new IllegalArgumentException("JS script is required");
        }
        return ((JavascriptExecutor) driver).executeScript(js, args);
    }
    /**
     * Helper method to find a {@link WebElement} with a time limit in milliseconds. During the wait period it will check for the element every 100 millisecond.
     * 
     * @param by {@link By} criteria to search by
     * @param limit millisecond time limit
     * @return {@link WebElement} HTML element
     */
    public WebElement findAndWait(final By by, final long limit)
    {
        return findAndWait(by, limit, 100);
    }
    /**
     * Helper method to find a {@link WebElement} with a time limit in milliseconds within the page element. 
     * During the wait period it will check for the element every 100 millisecond.
     * 
     * @param by {@link By} criteria to search by
     * @param limit millisecond time limit
     * @return {@link WebElement} HTML element
     */
    public WebElement findAndWaitInNestedElement(final By by, final long limit)
    {
        return findAndWaitInNestedElement(by, limit, 100);
    }

    /**
     * Helper method to find a {@link WebElement} with a time limit in milliseconds. During the wait period it will check for the element every 100 millisecond.
     * 
     * @param by {@link By} criteria to search by
     * @param limit time limit
     * @param interval polling frequency
     * @return {@link WebElement} HTML element
     */
    public WebElement findAndWait(final By by, final long limit, final long interval)
    {
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.pollingEvery(interval, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(limit, TimeUnit.MILLISECONDS);
        try
        {
            fluentWait.until(new Predicate<By>()
            {
                public boolean apply(By by)
                {
                    try
                    {
                        return driver.findElement(by).isDisplayed();
                    }
                    catch (NoSuchElementException ex)
                    {
                        return false;
                    }
                }
            });
            return driver.findElement(by);
        }
        catch (RuntimeException re)
        {
            throw new TimeoutException("Unable to locate element " + by);
        }

    }
    /**
     * Helper method to find a {@link WebElement} with a time limit in milliseconds. During the wait period it will check for the element every 100 millisecond.
     * 
     * @param by {@link By} criteria to search by
     * @param limit time limit
     * @param interval polling frequency
     * @return {@link WebElement} HTML element
     */
    public WebElement findAndWaitInNestedElement(final By by, final long limit, final long interval)
    {
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.pollingEvery(interval, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(limit, TimeUnit.MILLISECONDS);
        fluentWait.until(new Predicate<By>()
        {
            public boolean apply(By by)
            {
            try
            {
                return getWrappedElement().findElement(by).isDisplayed();
            }
            catch (NoSuchElementException ex)
            {
                return false;
            }
            }
        });
        return getWrappedElement().findElement(by);
    }
    /**
     * Helper method to find and return a slow loading {@link WebElement}.
     * 
     * @param criteria By search criteria
     * @return {@link WebElement} HTML element
     */
    public WebElement findAndWait(final By criteria)
    {
        return findAndWait(criteria, defaultWaitTime);
    }
    
    public long getDefaultWaitTime()
    {
        return defaultWaitTime;
    }

    /**
     * Wait until the element is visible for the specified amount of time.
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitForElement(By locator, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    /**
     * Wait until the Clickable of given Element for given seconds.
     * 
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilElementClickable(By locator, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    /**
     * Wait until the invisibility of given Element for given seconds.
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilElementDisappears(By locator, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
    /**
     * Wait until the invisibility of given Element for given seconds.
     * 
     * @param locator CSS Locator
     * @param text - The Text to find in the Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilNotVisible(By locator, String text, long timeOutInSeconds)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        if(text == null || text.isEmpty())
        {
            throw new IllegalArgumentException("Text value is required");
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.invisibilityOfElementWithText(locator, text));
    }
    /**
     * Wait until the visibility of given Element for given seconds.
     * 
     * @param locator CSS Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilElementPresent(By locator, long timeOutInSeconds)
    {
        if (locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
    /**
     * Wait Until element successfully deleting from DOM.
     * 
     * @param locator - CSS Locator
     * @param timeOutInSeconds - Timeout In Seconds
     */
    public void waitUntilElementDeletedFromDom(By locator, long timeOutInSeconds)
    {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        try
        {
            wait.until(ExpectedConditions.stalenessOf(driver.findElement(locator)));
        }
        catch (NoSuchElementException e) { /* if element already not in DOM! */}
    }
    /**
     * Wait document.readyState to return completed.
     * @param timeOutInSeconds time duration
     */
    public void waitForPageLoad(long timeOutInSeconds)
    {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        try
        {
            new WebDriverWait(driver, timeOutInSeconds).until(expectation);
        }
        catch (TimeoutException exception) { }
    }
    /**
     * Helper method to find and return a slow loading collection of {@link WebElement}.
     * 
     * @param criteria {@link By} search criteria
     * @param waitTime milliseconds to wait
     * @return Collection of {@link WebElement} HTML elements
     */
    public List<WebElement> findAndWaitForElements(final By criteria, final long waitTime)
    {
        if(criteria == null)
        {
            throw new IllegalArgumentException("By criteria is required");
        }
        findAndWait(criteria, waitTime);
        return driver.findElements(criteria);
    }
    /**
     * Helper method to find and return a slow loading collection of {@link WebElement}.
     * 
     * @param criteria {@link By} search criteria
     * @return Collection of {@link WebElement} HTML elements
     */
    public List<WebElement> findAndWaitForElements(final By criteria)
    {
        if(criteria == null)
        {
            throw new IllegalArgumentException("By criteria is required");
        }
        findAndWait(criteria, getDefaultWaitTime());
        return driver.findElements(criteria);
    }
    /**
     * Helper method to find and return a slow loading {@link WebElement} by id.
     * 
     * @param id String identifier
     * @return {@link WebElement} HTML element
     */
    public WebElement findAndWaitById(final String id)
    {
        if(StringUtils.isEmpty(id))
        {
            throw new IllegalArgumentException("element id is required");
        }
        By criteria = By.id(id);
        findAndWait(criteria, defaultWaitTime);
        return driver.findElement(criteria);
    }
    /**
     * Drag the source element and drop into target element.
     * 
     * @param source - Source Element
     * @param target - Target Element
     */
    public void dragAndDrop(WebElement source, WebElement target)
    {
        PageUtils.checkMandatoryParam("source element", source);
        PageUtils.checkMandatoryParam("target element", target);
        Actions builder = new Actions(driver);   
        Action dragAndDrop = builder.clickAndHold(source)
           .moveToElement(target)
           .release(target)
           .build();      
        dragAndDrop.perform();
    }
    /**
     * Recreating the action of hovering over a particular HTML element on a page.
     * 
     * @param element {@link WebElement} target
     */
    public void mouseOver(WebElement element)
    {
        if(element == null)
        {
            throw new IllegalArgumentException("A web element is required");
        }
        new Actions(driver).moveToElement(element).perform();
    }
    /**
     * This function will return list of visible elements found with the specified selector
     * 
     * @param selector {@link By} identifier
     * @return {@link List} of {@link WebElement}
     */
    public List<WebElement> findDisplayedElements(By selector)
    {
        PageUtils.checkMandatoryParam("Locator", selector);
        List<WebElement> elementList = driver.findElements(selector);
        List<WebElement> displayedElementList = new ArrayList<WebElement>();
        for (WebElement elementSelected : elementList)
        {
            if (elementSelected.isDisplayed())
            {
                displayedElementList.add(elementSelected);
            }
        }
        return displayedElementList;
    }
    public WebElement findFirstDisplayedElement(By selector)
    {
        try
        {
            List<WebElement> elementList = findDisplayedElements(selector);
            if(elementList != null && elementList.size() > 0)
            {
                return elementList.get(0);
            }
        } 
        catch (NoSuchElementException e)
        {
           throw new  NoSuchElementException("Not able find element for give locator " + e);
        }
        throw new NoSuchElementException("Not able find any displayed elemment for given locator " + selector.toString());
    }
    public void waitUntilVisible(By locator, String text, long timeOutInSeconds)
    {
        if (locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);
        }
        if(text == null || text.isEmpty())
        {
            throw new IllegalArgumentException("Text value is required");
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
    /**
     * Double click on an element
     * 
     * @param element {@link WebElement}
     */
    public void doubleClickOnElement(WebElement element)
    {
        PageUtils.checkMandatoryParam("doubleclick element", element);
        Actions builder = new Actions(driver);
        Action doubleClick = builder.doubleClick(element).build();
        doubleClick.perform();
    }
    /**
     * Wait until the invisibility of given Element for given seconds.
     * 
     * @param locator CSS Locator
     * @param text - The Text to find in the Locator
     * @param timeOutInSeconds Timeout In Seconds
     */
    public void waitUntilNotVisibleWithParitalText(By locator, String text, long timeOutInSeconds)
    {
        if (locator == null)
        {
            throw new IllegalArgumentException(LOCATOR_REQUIRED_ERR_MSG);   
        }
        if(text == null || text.isEmpty())
        {
            throw new IllegalArgumentException("Text value is required");
        }
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        wait.until(ElementExpectedConditions.invisibilityOfElementWithPartialText(driver, locator, text));
    }
    /**
     * Helper method to find a {@link WebElement} with a time limit in
     * milliseconds. During the wait period it will check for the element every
     * 100 millisecond. If the element is not displayed, refresh the page.
     *
     * @param by {@link By} criteria to search by
     * @return {@link WebElement}
     */
    public WebElement findAndWaitWithRefresh(final By by, final long limit)
    {
        if(null == by)
        {
            throw new IllegalArgumentException("A search By criteria is required");
        }
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.pollingEvery(100, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(limit, TimeUnit.MILLISECONDS);
        fluentWait.ignoring(NoSuchElementException.class);
        fluentWait.until(new Predicate<By>()
        {
            public boolean apply(By by)
            {
                try
                {
                    return driver.findElement(by).isDisplayed();
                }
                catch (NoSuchElementException ex)
                {
                    driver.navigate().refresh();
                    return false;
                }
            }
        });
        return driver.findElement(by);
    }
    /**
     * Returns true if the element is displayed else false.
     * @param locator {@link By} query
     * @return boolean true if displayed
     */
    public boolean isElementDisplayed(By locator)
    {
        if(locator == null)
        {
            throw new IllegalArgumentException("Element locator strategy is required");
        }
        try
        {
            return driver.findElement(locator).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            // no log needed due to negative cases.
        }
        catch (TimeoutException toe)
        {
            // no log needed due to negative cases.
        }
        catch (StaleElementReferenceException ste)
        {
            // no log needed due to negative cases.
        }
        
        return false;
    }
    /**
     * Recreating the action of click on a sub element that should be displayed once parent element is selected.
     * The coordinates provided specify the offset from the top-left corner of these elements.
     *
     * @param parentElement {@link WebElement} parentElement
     * @param parentXOffset {@link int} x coordinate for parentElement
     * @param parentYOffset {@link int} y coordinate for parentElement
     * @param subElement {@link WebElement} subElement
     * @param subXOffset {@link int} x coordinate for subElement
     * @param subYOffset {@link int} y coordinate for subElement
     */
    public void clickOnSubElementOffSet(WebElement parentElement, int parentXOffset, int parentYOffset, WebElement subElement, int subXOffset, int subYOffset)
    {
        if (parentElement == null || subElement == null)
        {
            throw new IllegalArgumentException("A web elements are required");
        }
        else
        {
            (new Actions(this.driver)).moveToElement(parentElement, parentXOffset, parentYOffset).moveToElement(subElement, subXOffset, subYOffset).click()
                    .perform();
        }
    }
    public void clearAndType(By by, String value)
    {
        if(by == null)
        {
            throw new IllegalArgumentException("Element Locator Can't be null."); 
        }
        if(value == null)
        {
            throw new IllegalArgumentException("Value Can't be null.");
        }
        try
        {
            WebElement element = driver.findElement(by);
            element.click();
            element.clear();
            element.sendKeys(value);
        }
        catch (TimeoutException e)
        {
            throw new PageOperationException("Not able to find the element", e);
        }
    }
    public WebElement findByKey(final String id)
    {
        By criteria = By.id(getValue(id));
        return driver.findElement(criteria);
    }
    
    public final String getValue(final String key)
    {
        PageUtils.checkMandatoryParam("key", key);
        return factoryPage.getValue(key);
    }
    /**
     * Opens the new tab in the same browser.
     */
    public void createNewTab()
    {
        driver.findElement(By.cssSelector("body")).sendKeys(getOsKey() +"t");
    }
    /**
     * Returns COMMAND {@link Keys} if the OS is MAC OS X else CONTROL key.
     * @return {@link Keys}
     */
    private Keys getOsKey()
    {
        Keys keys = Keys.CONTROL;
        String osName = System.getProperty("os.name");
        if(osName != null && !osName.isEmpty() && osName.toLowerCase().startsWith("mac"))
        {
            keys = Keys.COMMAND;
        }
        return keys;
    }
    /**
     * Drag and drop element by x,y
     * @param source html element
     * @param x coordinate
     * @param y coordinate
     */
    public void dragAndDrop(WebElement source, int x, int y)
    {
        PageUtils.checkMandatoryParam("source element", source);
        Actions builder = new Actions(driver);   
        Action dragAndDrop = builder.dragAndDropBy(source, x, y).build();
        dragAndDrop.perform();
     }
    /**
     * Closes the recently opened tab in the same browser.
     */
    public void closeTab()
    {
        driver.findElement(By.cssSelector("body")).sendKeys(getOsKey() +"w");
    }
    /**
     * Waits for given {@link ElementState} of all render elements when rendering a page.
     * If the given element not reach element state, it will time out and throw {@link TimeoutException}. If operation to find all elements
     * times out a {@link PageRenderTimeException} is thrown
     *
     * @param renderTime render timer
     * @param elements   collection of {@link RenderElement}
     */
    public void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        if (renderTime == null)
        {
            throw new UnsupportedOperationException("RenderTime is required");
        }
        if (elements == null || elements.length < 1)
        {
            throw new UnsupportedOperationException("RenderElements are required");
        }
        for (RenderElement element : elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(driver, waitSeconds);
            }
            catch (TimeoutException e)
            {
                throw new PageRenderTimeException("element not rendered in time.",e);
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }
    /**
     * Waits for given {@link ElementState} of all render elements when rendering a page.
     * If the given element not reach element state, it will time out and throw {@link TimeoutException}. 
     * If operation to find all elements times out a {@link PageRenderTimeException} is thrown
     * Renderable elements will be scanned from class using {@link RenderWebElement} annotation.
     *
     * @param renderTime render timer
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    public void webElementRender(RenderTime renderTime)
    {
        if (renderTime == null)
        {
            throw new UnsupportedOperationException("RenderTime is required");
        }
        List<RenderElement> elements = new ArrayList<RenderElement>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(RenderWebElement.class))
            {
                Class<?> type = field.getType();
                field.setAccessible(true);
                Object fieldVal = null;
                try
                {
                    fieldVal = field.get(this);
                } 
                catch (IllegalArgumentException | IllegalAccessException e)
                {
                    logger.error("Unable to set field", e);
                }
                
                //Handle page elements, extracts the @FindBy from the component.
                if(PageElement.class.isAssignableFrom(type))
                {
                    Annotation a = type.getAnnotation(FindBy.class);
                    if (a != null)
                    {
                        try 
                        {
                            By by = buildFromFindBy((FindBy)a);
                            elements.add(new RenderElement(by,ElementState.VISIBLE));
                        } 
                        catch (Exception ex)
                        {
                        }
                    }
                }
                if (type.equals(By.class))
                {
                    /*FIXME Below is the old way which we need to remove and use the web element instead.
                     *This is kept until we refactor the sharepo to use webelement instead of
                     *By.class. 
                     * @RenderWebelement By css = By.cssSelector("div.t");
                     * to 
                     * @RenderWebelement WebElement css;
                     * 
                     */
                    RenderWebElement webElement = (RenderWebElement) field.getAnnotation(RenderWebElement.class);
                    elements.add(new RenderElement((By) fieldVal, webElement.state()));
                }
                else
                {
                    Annotation[] list = field.getDeclaredAnnotations();
                    By by = null;
                    for(Annotation a : list)
                    {
                        if(a instanceof FindBy)
                        {
                            by = extractSelector((FindBy)a);
                        }
                    }
                    if(by != null)
                    {
                        elements.add(new RenderElement(by, ElementState.VISIBLE));
                    }
                }
            }
        }
        if(!elements.isEmpty())
        {
            elementRender(renderTime, elements.toArray(new RenderElement[elements.size()]));
        }
    }
    /**
     * Extract the selector query from annotation.
     * @param findBy
     * @return By selector with value from annotation.
     */
    private By extractSelector(FindBy findBy)
    {
        if(!StringUtils.isEmpty(findBy.css()))
        { 
            return By.cssSelector(findBy.css());
        }
        if(!StringUtils.isEmpty(findBy.id()))
        {
            return By.id(findBy.id());
        }
        if(!StringUtils.isEmpty(findBy.xpath()))
        {
            return By.xpath(findBy.xpath());
        }
        throw new PageOperationException("Select by is not supported" + findBy.toString());
    }
    private By buildFromFindBy(FindBy findBy)
    {
        if (!"".equals(findBy.className()))
        {
          return By.className(findBy.className());
        }
        if (!"".equals(findBy.css()))
        {
            return By.cssSelector(findBy.css());
        }
        if (!"".equals(findBy.id()))
        {
            return By.id(findBy.id());
        }
        if (!"".equals(findBy.linkText()))
        {
            return By.linkText(findBy.linkText());
        }

        if (!"".equals(findBy.name()))
        {
            return By.name(findBy.name());
        }

        if (!"".equals(findBy.partialLinkText()))
        {
            return By.partialLinkText(findBy.partialLinkText());
        }

        if (!"".equals(findBy.tagName()))
        {
            return By.tagName(findBy.tagName());
        }

        if (!"".equals(findBy.xpath()))
        {
            return By.xpath(findBy.xpath());
        }

        // Fall through
        return null;
    }
	public void setDefaultWaitTime(long defaultWaitTime) {
		this.defaultWaitTime = defaultWaitTime;
	}
	public void setMaxPageLoadingTime(long maxPageLoadingTime) {
		this.maxPageLoadingTime = maxPageLoadingTime;
	}
    
}
