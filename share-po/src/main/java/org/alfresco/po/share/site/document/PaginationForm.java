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

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.share.FactorySharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.yandex.qatools.htmlelements.element.HtmlElement;

import com.google.common.base.Predicate;

/**
 * FormObject mimic actions with Pagination on any page.
 *
 * @author Aliaksei Boole
 */
@SuppressWarnings("unused")
public class PaginationForm extends PageElement
{
    private final By FORM_XPATH;
    private final static By NEXT_PAGE_LINK = By.xpath(".//a[@title='Next Page']");
    private final static By PREVIOUS_PAGE_LINK = By.xpath(".//a[@title='Previous Page']");
    private final static By PAGES_SELECT_LINKS = By.xpath(".//span[@class='yui-pg-pages']/*");
    private final static By PAGE_INFO_LABEL = By.xpath(".//span[@class='yui-pg-current']");
    private final static By CURRENT_PAGE_SPAN = By.xpath(".//span[@class='yui-pg-pages']/span");
    private final static By NEXT_PAGE_SPAN = By.xpath(".//span[@class='yui-pg-next']");
    private final static By PREVIOUS_PAGE_SPAN = By.xpath(".//span[@class='yui-pg-previous']");

    /**
     * Constructor for creating FormObject mimic actions with Pagination on any page.
     *
     * @param driver
     * @param formXpath -basic xpath (all xpath's on form building based on him)
     */
    public PaginationForm(WebDriver driver, By formXpath)
    {
        FORM_XPATH = formXpath;
    }

    private WebElement getFormElement()
    {
        return findAndWait(FORM_XPATH);
    }

    /**
     * Return number pages in pagination.
     *
     * @return number of pages.
     */
    public int getCurrentPageNumber()
    {
        WebElement currentPageNumberElem = getFormElement().findElement(CURRENT_PAGE_SPAN);
        return Integer.valueOf(currentPageNumberElem.getText());
    }

    /**
     * Mimic click on button next '>>'.
     *
     * @return Next Page
     */
    public HtmlPage clickNext()
    {
        int beforePageNumber = getCurrentPageNumber();
        getFormElement().findElement(NEXT_PAGE_LINK).click();
        waitUntilPageNumberChanged(beforePageNumber);
        return getCurrentPage();
    }

    /**
     * Mimic click on button Previous '<<'.
     *
     * @return Previous page
     */
    public HtmlPage clickPrevious()
    {
        int beforePageNumber = getCurrentPageNumber();
        getFormElement().findElement(PREVIOUS_PAGE_LINK).click();
        waitUntilPageNumberChanged(beforePageNumber);
        return getCurrentPage();
    }

    /**
     * Check that Previous button '<<' enable(if enable '\<a\>' if not '\<span\>' ).
     *
     * @return true if we can interact with '<<'
     */
    public boolean isPreviousButtonEnable()
    {
        try
        {
            getFormElement().findElement(PREVIOUS_PAGE_LINK);
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Check that Next button '>>' enable(if enable '\<a\>' if not '\<span\>' ).
     *
     * @return true if we can interact with '>>'
     */
    public boolean isNextButtonEnable()
    {
        try
        {

            getFormElement().findElement(NEXT_PAGE_LINK);
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }

    /**
     * Return WebElements for interact with pages links.
     *
     * @return List webElements associated  with pagination pages links
     */
    public List<WebElement> getPaginationLinks()
    {
        return getFormElement().findElements(PAGES_SELECT_LINKS);
    }

    /**
     * Click on link by number and go to selected page.
     *
     * @param linkNumber
     * @return selected page.
     */
    public HtmlPage clickOnPaginationPage(int linkNumber)
    {
        List<WebElement> paginationLinks = getPaginationLinks();
        for (WebElement paginationLink : paginationLinks)
        {
            int currentLinkNumber = Integer.valueOf(paginationLink.getText());
            if (currentLinkNumber == linkNumber)
            {
                int beforePageNumber = getCurrentPageNumber();
                paginationLink.click();
                waitUntilPageNumberChanged(beforePageNumber);
                break;
            }
        }
        return getCurrentPage();
    }

    /**
     * PaginationInfo like a ' 1 - 50 of 60 '
     *
     * @return message about page.
     */
    public String getPaginationInfo()
    {
        return getFormElement().findElement(PAGE_INFO_LABEL).getText();
    }

    /**
     * Check that PaginationForm displayed on page.
     *
     * @return true - if displayed.
     */
    public boolean isDisplay()
    {
        try
        {
            return findAndWait(FORM_XPATH, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    private void waitUntilPageNumberChanged(int beforePageNumber)
    {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(paginationPageChanged(beforePageNumber));
    }

    private Predicate<WebDriver> paginationPageChanged(final int beforePageNumber)
    {
        return new Predicate<WebDriver>()
        {
            @Override
            public boolean apply(WebDriver driver)
            {
                try
                {
                    WebElement currentPageIndicator = driver.findElement(FORM_XPATH).findElement(CURRENT_PAGE_SPAN);
                    return Integer.valueOf(currentPageIndicator.getText()) != beforePageNumber;
                }
                catch (StaleElementReferenceException e)
                {
                    return apply(driver);
                }
            }
        };
    }
}
