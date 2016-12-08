/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.admin.ActionsSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class FacetedSearchResult extends PageElement implements SearchResult
{
    /**
     * Constants.
     */
    private static final By NAME = By.cssSelector("div.nameAndTitleCell span.alfresco-renderers-Property:first-of-type span.inner a");
    private static final By TITLE = By
            .cssSelector("div.nameAndTitleCell span.alfresco-renderers-Property.alfresco-renderers-Property.small>span.inner>span.value");
    private static final By DATE = By.cssSelector("div.dateCell span.inner");
    private static final By DESCRIPTION = By.cssSelector("div.descriptionCell span.value");
    private static final By SITE = By.cssSelector("span[id$='_SITE'] .value");
    private static final By LOCATION = By.cssSelector("div.pathCell span.value");
    private static final By ACTIONS_BUTTON = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS");
    private static final By ACTIONS_DROPDOWN = By.cssSelector("#FCTSRCH_SEARCH_RESULT_ACTIONS_DROPDOWN");
    private static final By ACTIONS = By.cssSelector("tr td.actionsCell");
    private static final By IMAGE = By.cssSelector("tbody[id=FCTSRCH_SEARCH_ADVICE_NO_RESULTS_ITEMS] td.thumbnailCell img");
    private static final By FOLDER_PATH = By.xpath("//div[@class='pathCell']//span[@class='value']");
    private static final By CHECKBOX = By.cssSelector(".alfresco-renderers-Selector");
    private static final By SELECTEDCHECKBOX = By.cssSelector(".alfresco-lists-ItemSelectionMixin--selected");
    private static final By MARK = By.cssSelector("mark");

    Log logger = LogFactory.getLog(this.getClass());
    private WebDriver driver;
    private WebElement link;
    private String name;
    private String title;
    private WebElement dateLink;
    private String date;
    private String description;
    private WebElement siteLink;
    private WebElement locationLink;
    private String site;
    private WebElement actionsButton;
    private ActionsSet actions;
    private WebElement imageLink;
    private final boolean isFolder;
    private String previewUrl;
    private String thumbnailUrl;
    private WebElement contentDetails;
    private String thumbnail;
    private List<String> pathFolders = new LinkedList<String>();
    private WebElement checkBox;
    private final boolean isItemChecked;
    private final WebElement currentElement;

    /**
     * Instantiates a new faceted search result - some items may be null.
     */
    public FacetedSearchResult(WebDriver driver, WebElement result, FactoryPage factoryPage)
    {
        this.driver = driver;
        this.currentElement = result;
        if (result.findElements(NAME).size() > 0)
        {
            link = result.findElement(NAME);
            name = link.getText();
        }
        if (result.findElements(TITLE).size() > 0)
        {
            title = result.findElement(TITLE).getText();
        }
        if (result.findElements(DATE).size() > 0)
        {
            dateLink = result.findElement(DATE);
            date = dateLink.getText();
        }
        if (result.findElements(DESCRIPTION).size() > 0)
        {
            description = result.findElement(DESCRIPTION).getText();
        }
        if (result.findElements(SITE).size() > 0)
        {
            siteLink = result.findElement(SITE);
            site = siteLink.getText();
        }

        if (result.findElements(IMAGE).size() > 0)
        {
            imageLink = result.findElement(IMAGE);
            thumbnail = imageLink.getAttribute("src");

        }
        if (result.findElements(CHECKBOX).size() > 0)
        {
            checkBox = result.findElement(CHECKBOX);
        }
        if (result.findElements(SELECTEDCHECKBOX).size() > 0)
        {
            result.findElement(SELECTEDCHECKBOX);
        }

        if (result.findElements(FOLDER_PATH).size() > 0)
        {
            String fullFolderPath = result.findElement(FOLDER_PATH).getText();
            StringTokenizer tokens = new StringTokenizer(fullFolderPath, "/");
            while (tokens.hasMoreElements())
            {
                pathFolders.add(tokens.nextElement().toString());
            }
        }
        if (result.findElements(LOCATION).size() > 0)
        {
            locationLink = result.findElement(LOCATION);
        }
        this.factoryPage = factoryPage;
        isFolder = checkFolder(result);
        isItemChecked = isItemChecked(result);
        this.actionsButton = driver.findElement(ACTIONS_BUTTON);
        actions = new SearchActionsSet(driver, result.findElement(ACTIONS), factoryPage);

    }

    private boolean checkFolder(WebElement row)
    {
        try
        {
            String source = row.findElement(By.tagName("img")).getAttribute("src");
            if (source != null && source.endsWith("folder.png"))
            {
                return true;
            }
        }
        catch (Exception e)
        {
        }
        return false;
    }

    /**
     * Gets the result link.
     *
     * @return the link
     */
    public WebElement getLink()
    {
        return link;
    }

    /**
     * Click a result link.
     *
     * @return the html page
     */
    public HtmlPage clickLink()
    {
        link.click();
        return factoryPage.getPage(this.driver);
    }

    /**
     * Gets the result name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the result title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets the result dateLink.
     *
     * @return the dateLink
     */
    public WebElement getDateLink()
    {
        return dateLink;
    }

    /**
     * Click a result dateLink.
     *
     * @return the html page
     */
    public HtmlPage clickDateLink()
    {
        dateLink.click();
        return factoryPage.getPage(this.driver);
    }

    /**
     * Gets the result date.
     *
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * Gets the result description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Gets the result siteLink.
     *
     * @return the siteLink
     */
    public WebElement getSiteLink()
    {
        return siteLink;
    }

    /**
     * Click a result siteLink.
     *
     * @return the html page
     */
    public HtmlPage clickSiteLink()
    {
        siteLink.click();
        return factoryPage.getPage(this.driver);
    }

    /**
     * Gets the result site.
     *
     * @return the site
     */
    public String getSite()
    {
        return site;
    }

    /**
     * Gets the actions.
     *
     * @return the actions
     */
    public ActionsSet getActions()
    {
        return actions;
    }

    /**
     * click the result imageLink.
     *
     * @return the preview pop up window
     */
    public PreViewPopUpPage clickImageLink()
    {
        imageLink.click();
        return factoryPage.instantiatePage(driver, PreViewPopUpPage.class);
    }

    @Override
    public boolean isFolder()
    {
        return isFolder;
    }

    @Override
    public HtmlPage clickContentPath()
    {
        contentDetails.click();
        return getCurrentPage();
    }

    @Override
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    @Override
    public String getPreViewUrl()
    {
        return previewUrl;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    @Override
    public void clickOnDownloadIcon()
    {

    }

    @Override
    public HtmlPage clickSiteName()
    {
        return getCurrentPage();
    }

    /**
     * This method finds the selected item's folderpath and returns the main
     * folder and sub folder names as list of string. value. Search Result Item
     * parent folder will be available as the first element in the returned
     * list.
     *
     * @return List<String>
     */
    public List<String> getFolderNamesFromContentPath()
    {
        return pathFolders;
    }

    /**
     * Gets the result link.
     *
     * @return the link
     */
    public WebElement getCheckBox()
    {
        return checkBox;
    }

    /**
     * click the result imageLink.
     *
     * @return the preview pop up window
     */

    public HtmlPage selectItemCheckBox()
    {
        checkBox.click();
        return factoryPage.getPage(this.driver);
    }

    /**
     * Checks if Item Check Box is selected
     *
     * @return true if selected
     */

    private boolean isItemChecked(WebElement row)
    {
        try
        {
            if (row.findElement(SELECTEDCHECKBOX).isDisplayed())
            {
                return true;
            }
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("checkbox not selected. ", nse);
            }
        }
        return false;
    }

    /**
     * @return true if Actions button is displayed and enabled.
     */
    public boolean isActionsButtonEnabled()
    {
        try
        {
            if (actionsButton.isDisplayed() && actionsButton.isEnabled())
            {
                return true;
            }
        }
        catch (WebDriverException we)
        {
            logger.error("Actions button not enabled", we);
        }
        return false;
    }

    /**
     * Click on Actions button.
     */
    private void clickActionsButton()
    {
        try
        {
            actionsButton = driver.findElement(ACTIONS_BUTTON);
            if (isActionsButtonEnabled())
            {
                actionsButton.click();
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.error("Actions button not available : ", nse);
            throw new PageException("Unable to find Actions button", nse);
        }
    }

    /**
     * Select an option from Actions Menu
     *
     * @return HtmlPage
     */
    public HtmlPage selectAction(FacetedSearchResultActionsMenu option)
    {
        try
        {
            clickActionsButton();
            if (driver.findElement(ACTIONS_DROPDOWN).isDisplayed())
            {
                WebElement element = driver.findElement(By.cssSelector(option.getOption()));
                element.click();
                return factoryPage.getPage(driver);
            }
            throw new PageException("Actions drop down not visible");
        }
        catch (NoSuchElementException e)
        {
            String exceptionMessage = "Not able to find the option";
            logger.error(exceptionMessage, e);
            throw new PageException(exceptionMessage, e);
        }
    }

    @Override
    public boolean isItemCheckBoxSelected()
    {
        return isItemChecked;
    }

    public boolean isAnyItemHighlighted()
    {
        return currentElement.findElements(MARK).size() > 0;
    }

    public boolean isItemHighlighted(ItemHighlighted item)
    {
        int foundHighlightCount = currentElement.findElements(By.cssSelector(item.getItem())).size();
        return foundHighlightCount > 0;
    }

    public String getHighlightedText(ItemHighlighted item)
    {
        try
        {
            WebElement highlighted = currentElement.findElement(By.cssSelector(item.getItem()));
            return highlighted.getText();
        }
        catch (NoSuchElementException nse)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Can't get highlighted text.", nse);
            }
        }
        return "";
    }

    public boolean isSiteHighlighted()
    {
        return siteLink.findElements(MARK).size() > 0;
    }

    public boolean isLocationHighlighted()
    {
        return locationLink.findElements(MARK).size() > 0;
    }

    public boolean isUserNameHighlighted()
    {
        return dateLink.findElements(MARK).size() > 0;
    }

}
