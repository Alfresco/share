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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.ShareDialogue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Tag Page allows the user to manage tags relating to document in view.
 *
 * @author Subashni Prasanna
 * @since 1.6
 */
public class TagPage extends AbstractEditProperties
{
        protected static final By SELECT_HEADER = By.cssSelector("div[id$='cntrl-picker-head']");
        protected static final By ENTER_TAG_VALUE = By.cssSelector("input.create-new-input");
        protected static final By CREATE_TAG = By.cssSelector("span.createNewIcon");
        protected static final By REMOVE_TAG = By.cssSelector("span.removeIcon");
        protected static final By OK_BUTTON = By.cssSelector("span[id$='taggable-cntrl-ok'] button[id$='cntrl-ok-button']");
        protected static final By CANCEL_BUTTON = By.cssSelector("button[id$='cntrl-cancel-button']");
        private static final By ALL_TAG_LINES = By.xpath("//div[contains(@id,'-cntrl-picker-left')]//tr[contains(@class,'yui-dt-rec')]");
        private static final By ADDED_TAG_LINES = By.xpath("//div[contains(@id,'-cntrl-picker-right')]//tr[contains(@class,'yui-dt-rec')]");
        private static final By TAG_NAME_RELATIVE = By.xpath(".//h3[@class='item-name']");
        private static final By NAVIGATION_BUTTON = By.xpath("//button[contains(@id,'picker-navigator-button')]");
        private static final By REFRESH_TAGS_BUTTON = By.xpath("//a[contains(@class,'yuimenuitemlabel')]");
        private Log logger = LogFactory.getLog(this.getClass());


        @SuppressWarnings("unchecked")
        @Override
        public TagPage render(RenderTime timer)
        {
                while (true)
                {
                        timer.start();
                        try
                        {
                                if (isTagPageVisible() && isTagInputVisible())
                                {
                                        break;
                                }
                        }
                        catch (Exception e)
                        {
                        }
                        finally
                        {
                                timer.end();
                        }
                }
                return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public TagPage render()
        {
                return render(new RenderTime(maxPageLoadingTime));
        }

        /**
         * Verify if tagPage is displayed.
         *
         * @return true if displayed
         */
        public boolean isTagPageVisible()
        {
                try
                {
                        List<WebElement> elements = driver.findElements(By.cssSelector("div[id$='cntrl-picker-head']"));
                        if (elements.size() > 0)
                        {
                                return true;
                        }
                        else
                        {
                                return false;
                        }

                }
                catch (NoSuchElementException nse)
                {
                        return false;
                }
        }

        /**
         * Checks if tag input field is visible.
         *
         * @return true if visible
         */
        public boolean isTagInputVisible()
        {
                try
                {
                        return driver.findElement(ENTER_TAG_VALUE).isDisplayed();
                }
                catch (NoSuchElementException nse)
                {
                }
                return false;
        }

        /**
         * Enter the tag name and click to Add tag.
         *
         * @param tagName String
         * @return HtmlPage
         */
        public HtmlPage enterTagValue(String tagName)
        {
                try
                {
                        WebElement input = findAndWait(ENTER_TAG_VALUE);
                        input.clear();
                        input.sendKeys(tagName);

                        WebElement createButton = findAndWait(CREATE_TAG);
                        createButton.click();
                        canResume();
                        HtmlPage page = getCurrentPage();
                        if (page instanceof ShareDialogue)
                        {
                                return getCurrentPage();
                        }
                        return page;
                }
                catch (NoSuchElementException nse)
                {
                        logger.error("Unable to find the EnterTagName or CreateTag css.", nse);
                }

                throw new PageOperationException("Error in finding the Enter tag value css.");
        }

        /**
         * Click on OK button in tag page
         *
         * @return EditDocumentPropertiesPage
         */
        public HtmlPage clickOkButton()
        {
                try
                {
                        WebElement okButton = findAndWait(OK_BUTTON);
                        okButton.click();
                        return getCurrentPage();
                }
                catch (NoSuchElementException nse)
                {
                        logger.error("Unable to find 'ok' button.", nse);
                }
                throw new PageOperationException("Error in finding 'ok' button");
        }

        /**
         * Click on cancel button in tag page
         *
         * @return EditDocumentPropertiesPage
         */
        public HtmlPage clickCancelButton()
        {
                try
                {
                        WebElement cancelButton = driver.findElement(CANCEL_BUTTON);
                        cancelButton.click();
                        return getCurrentPage();
                }
                catch (NoSuchElementException nse)
                {
                        logger.error("Unable to find 'cancel' button.", nse);
                }
                throw new PageOperationException("Error in finding 'cancel' button");
        }

        /**
         * Enter the tag name and click to Remove tag.
         *
         * @param tagName String
         * @return EditDocumentPropertiesPage
         */
        public HtmlPage removeTagValue(String tagName)
        {
                if (StringUtils.isEmpty(tagName))
                {
                        throw new IllegalArgumentException("TagName should not be null.");
                }

                try
                {
                        WebElement tagElement = getSelectedTagElement(tagName);
                        tagElement.findElement(REMOVE_TAG).click();
                        driver.findElement(OK_BUTTON).click();
                        return getCurrentPage();
                }
                catch (NoSuchElementException nse)
                {
                        logger.error("RemoveLink on Tag is not present.", nse);
                }
                catch (PageOperationException pe)
                {
                        logger.error(pe);
                }
                throw new PageOperationException("Error in removing tag on TagPage.");
        }

        /**
         * Return  count tags in left panel
         *
         * @return int
         */
        public int getAllTagsCount()
        {
                List<WebElement> allTags = getAllTagElements();
                return allTags.size();
        }

        /**
         * Return count added to document tags
         *
         * @return int
         */
        public int getAddedTagsCount()
        {
                try
                {
                        List<WebElement> addedTags = findAndWaitForElements(ADDED_TAG_LINES);
                        return addedTags.size();
                }
                catch (TimeoutException e)
                {
                        return 0;
                }
        }

        /**
         * Return List of tag names in left panel
         *
         * @return List<String>
         */
        public List<String> getAllTagsName()
        {
                List<String> allTagsNames = new ArrayList<>();
                List<WebElement> allTags = getAllTagElements();
                for (WebElement tagsElem : allTags)
                {
                        allTagsNames.add(tagsElem.findElement(TAG_NAME_RELATIVE).getText());
                }
                return allTagsNames;
        }

        /**
         * Refresh tags list in left panel.
         */
        public void refreshTags()
        {
                findAndWait(NAVIGATION_BUTTON).click();
                findAndWait(REFRESH_TAGS_BUTTON).click();
                waitUntilAlert();
        }

        private List<WebElement> getAllTagElements()
        {
                List<WebElement> addedTags = findAndWaitForElements(ALL_TAG_LINES);
                addedTags.remove(0);
                return addedTags;
        }

        /**
         * This private method is used to get the selected tag element from the list of tags on tag page.
         *
         * @param tagName String
         * @return WebElement
         */
        private WebElement getSelectedTagElement(String tagName)
        {
                List<WebElement> tags = null;
                String name = null;

                try
                {
                        tags = findAndWaitForElements(By.cssSelector("div[id$='prop_cm_taggable-cntrl-picker-selectedItems'] tbody.yui-dt-data tr"));
                }
                catch (TimeoutException te)
                {
                        logger.error("Exceeded time to find the tags list.", te);
                }

                if (tags != null)
                {
                        for (WebElement tag : tags)
                        {
                                name = tag.findElement(By.cssSelector("td[class$='yui-dt-col-name'] h3.name")).getText();

                                if (name != null && name.equals(tagName))
                                {
                                        return tag;
                                }

                        }
                }

                throw new PageOperationException("Tag is not present.");
        }

}
