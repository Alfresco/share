/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site.wiki;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.SitePage;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.thirdparty.firefox.RssFeedPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Site wiki main page object, holds all element of the HTML page relating to
 * share's site wiki page.
 *
 * @author Michael Suzuki
 * @since 1.2
 */
public class WikiPage extends SitePage
{

    private Log logger = LogFactory.getLog(this.getClass());

    private static final String WIKI_FORMAT_IFRAME = "template_x002e_createform_x002e_wiki-create_x0023_default-content_ifr";
    private static final String WIKI_EDIT_IFRAME = "template_x002e_wikipage_x002e_wiki-page_x0023_default-content_ifr";
    private static final By CANCEL_BUTTON = By.cssSelector("a[id$='default-cancel-button-button']");
    private static final By DEFAULT_CONTENT_TOOLBAR = By.cssSelector("div[class^='mce']"); //By.cssSelector("div[class^='mce-toolbar']");
    private static final By BUTTON_CREATE = By.cssSelector("button[id$='default-create-button-button']");
    private static final By CREATE_WIKI_TITLE = By.cssSelector("input[id$='createform_x002e_wiki-create_x0023_default-title']");
    private static final By FONT_STYLE_SELECT = By.cssSelector("a[id$='default-content_fontselect_open']");
    private static final By FONT_SIZE_SELECT = By.cssSelector("a[id$='default-content_fontsizeselect_open']");
    private static final By IMAGE_LIB = By.cssSelector("div[aria-label='Insert Library Image'] i.mce-ico.mce-i-image");
    private static final By IMAGE_RSLT = By.cssSelector("#image_results");
    private static final By BUTTON_SAVE = By.cssSelector("button[id$='default-save-button-button']");
    private static final By REMOVE_FORMAT = By.cssSelector(".mceIcon.mce_removeformat");
    private static final By FORMAT_BUTTON = By.xpath("//div[contains(@class,'mce-menubar')]//div[5]//button");
    private static final By FORMATS_BUTTON = By.xpath("//button/span[text()='Formats']");
    private static final By HEADINGS_BUTTON = By.xpath("//div[contains(@class,'expand')]/span[text()='Headings']");
    private static final By HEADING_1 = By.xpath("//span[contains(@style,'22px')]");
    private static final By HEADING_2 = By.xpath("//span[contains(@style,'16.5px')]");
    private static final By HEADING_3 = By.xpath("//span[contains(@style,'12.8667px')]");
    private static final By HEADING_4 = By.xpath("//span[contains(@style,'11px')]");
    private static final By HEADING_5 = By.xpath("//span[contains(@style,'9.1333px')]");
    private static final By HEADING_6 = By.xpath("//span[contains(@style,'7.36667px')]");
    private static final By CLEAR_FORMAT_BUTTON = By.xpath("//div[contains(@class,'mce-menu-item')]/i[contains(@class,'removeformat')]/following-sibling::span");
    private static final By EDIT_BUTTON = By.xpath("//div[contains(@class,'mce-menubar')]//div[2]//button");
    private static final By SELECT_ALL_BUTTON = By.xpath("//div[contains(@class,'mce-menu-item')]/i[contains(@class,'selectall')]/following-sibling::span");
    private static final By DELETE_WIKI = By.cssSelector("button[id$='default-delete-button-button']");
    private static final By EDIT_WIKI = By.cssSelector("a[href*='action=edit']");
    private static final By BACK_LINK = By.xpath(".//*[contains (@id, 'wiki-page')]/div[1]/div/span[1]/a");
    private static final By RENAME_BUTTON = By.cssSelector("button[id$='default-rename-button-button']");
    private static final By VERSION_PLACEHOLDER = By.cssSelector(".meta-section-label");
    private static final By REVERT_BTN = By.cssSelector(".revert>a");
    private static final By RENAME_SAVE_BTN = By.cssSelector("button[id$='rename-save-button-button']");
    private static final By DETAILS_LINK = By.cssSelector(".title-bar .align-right span:last-child, a[href$='details']");
    private static final By VIEW_LINK = By.cssSelector("a[href$='view']");
    private static final By VERSION_HEADER = By.cssSelector("span[id$='default-version-header']");
    @SuppressWarnings("unused")
    private static final By CONFIRM_REVERT_BTN = By.cssSelector("button[id$='ok-button-button']");
    private static final By WIKI_TEXT = By.cssSelector("div[id$='default-page']");
    private static final By WIKI_TITLE = By.cssSelector("div[class*='pageTitle']");
    private static final By WIKI_TAG_INPUT = By.cssSelector("input[id$='tag-input-field']");
    private static final By ADD_TAG_BUTTON = By.cssSelector("button[id$='default-add-tag-button-button']");
    private static final By TAG = By.cssSelector("div[class*='tag']>div");
    private static final By TAG_NONE = By.cssSelector("div[class*='tag']");
    private static final By SELECT_VERSION_BUTTON = By.cssSelector("button[id$='selectVersion-button-button']");
    @SuppressWarnings("unused")
    private static final By RSS_FEED_BUTTON = By.cssSelector("span[id$='default-rssFeed-button']");
    private static final String LATEST_VERSION = " (Latest)";
    protected static final By MAIN_PAGE = By.cssSelector(".forwardLink>a");
    private static final String VERSION_HISTORY_CONTAINER = "//div[@class='yui-gb']//div[contains(@class, 'first')]";
    private static final String TAGS_CONTAINER = "//div[contains(@class, 'tags')]/..";
    private static final String LINKED_PAGES_CONTAINER = "//div[contains(@class, 'links')]/..";

    private TinyMceEditor tinyMCEEditor = new TinyMceEditor(drone);

    public enum ImageType
    {
        JPG, PNG, BMP
    }

    public enum Mode
    {
        ADD, EDIT
    }

    public enum FONT_ATTR
    {
        face, size
    }

    public WikiPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPage render(RenderTime timer)
    {
        elementRender(timer, RenderElement.getVisibleRenderElement(BUTTON_CREATE));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public WikiPage render(long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Check if wiki page is displayed or not.
     *
     * @return
     */
    public boolean isWikiPageDisplayed()
    {
        try
        {
            return drone.findAndWait(DEFAULT_CONTENT_TOOLBAR).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Time out finding " + CANCEL_BUTTON.toString(), toe);
            }
        }
        catch (ElementNotVisibleException visibleException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Element Not Visible: " + CANCEL_BUTTON.toString(), visibleException);
            }
        }
        return false;
    }

    /**
     * Check content tool bar is displayed.
     *
     * @return
     */
    public boolean isTinyMCEDisplayed()
    {
        try
        {
            return drone.findAndWait(DEFAULT_CONTENT_TOOLBAR).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + DEFAULT_CONTENT_TOOLBAR.toString(), toe);
        }
        throw new PageException("Page is not rendered");
    }

    /**
     * click on new wiki page.
     */
    public void clickOnNewPage()
    {
        try
        {
            drone.findAndWait(BUTTON_CREATE).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + BUTTON_CREATE.toString(), toe);
        }

    }

    /**
     * Create wiki page title.
     *
     * @param wikiTitle
     */
    public void createWikiPageTitle(String wikiTitle)
    {
        try
        {
            drone.findAndWait(CREATE_WIKI_TITLE).sendKeys(wikiTitle);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + CREATE_WIKI_TITLE.toString(), toe);
        }
    }

    /**
     * Insert text in wiki text area.
     *
     * @param txtLines
     */
    public void insertText(List<String> txtLines)
    {
        try
        {
            drone.executeJavaScript(String.format("tinyMCE.activeEditor.setContent('%s');", txtLines.get(0)));
//            drone.switchToFrame(WIKI_FORMAT_IFRAME);
//            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));
//            if (!element.getText().isEmpty())
//            {
//                element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
//            }
//            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding #tinymce", toe);
        }
    }

    /*   *//**
 * Click bullet list button on wiki text formatter.
 */
    /*
     * public void clickBulletList()
     * {
     * try
     * {
     * drone.findAndWait(BULLET_LIST).click();
     * }
     * catch (TimeoutException toe)
     * {
     * logger.error("Time out finding " + BULLET_LIST.toString());
     * }
     * }
     */

    /**
     * Click Font Style button on wiki text formatter.
     */
    public void clickFontStyle()
    {
        try
        {
            drone.findAndWait(FONT_STYLE_SELECT).click();
            drone.findAndWait(By.cssSelector("#mce_22")).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + FONT_STYLE_SELECT.toString() + " OR #mce_22");
        }
    }

    /**
     * Click on Font size button on wiki text formatter.
     */
    public void clickFontSize()
    {
        try
        {
            drone.findAndWait(FONT_SIZE_SELECT).click();
            List<WebElement> elements = drone.findAll(By.cssSelector(".mceText"));
            for (WebElement webElement : elements)
            {
                if ("font-size: 12pt;".equals(webElement.getAttribute("style")))
                {
                    webElement.click();
                    break;
                }
            }
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + FONT_SIZE_SELECT.toString() + " OR #mce_22", toe);
        }
    }

    /**
     * Select all test in editor on Wiki Page
     */
    public void selectAllText()
    {
        if (getDrone().getProperties().getVersion() == AlfrescoVersion.Enterprise5)
        {
            drone.findAndWait(EDIT_BUTTON).click();
            drone.findAndWait(SELECT_ALL_BUTTON).click();
        }
        else
        {
            logger.error("Unsupported operation. selectAllText should be updated for specific Alfresco Version");
        }

    }

    /**
     * Select Heading Size on wiki text formatter.
     */
    public void selectHeadingSize(int headingSize)
    {
        if (headingSize < 0 && headingSize > 6)
        {
            logger.error("Unsupported operation: Heading Size should be between 1..5");
        }
        else
        {
            drone.findAndWait(FORMATS_BUTTON).click();
            drone.findAndWait(HEADINGS_BUTTON).click();
            switch (headingSize)
            {
                case (1):
                {
                    drone.findAndWait(HEADING_1).click();
                    break;
                }
                case (2):
                {
                    drone.findAndWait(HEADING_2).click();
                    break;
                }
                case (3):
                {
                    drone.findAndWait(HEADING_3).click();
                    break;
                }
                case (4):
                {
                    drone.findAndWait(HEADING_4).click();
                    break;
                }
                case (5):
                {
                    drone.findAndWait(HEADING_5).click();
                    break;
                }
                case (6):
                {
                    drone.findAndWait(HEADING_6).click();
                    break;
                }
            }
        }
    }
    /**
     * Retrieve formatted wiki text.
     *
     * @param type
     * @return
     */
    public String retrieveWikiText(String type)
    {
        try
        {
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            String richText = drone.findAndWait(getCSSToRetrieveText(type)).getText();
            drone.switchToDefaultContent();
            return richText;
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + type, toe);
        }
        throw new PageException("Wiki Page has no such element");
    }

    /**
     * Check for image library is displayed.
     *
     * @return
     */
    public boolean isImageLibraryDisplayed()
    {
        try
        {
            drone.findAndWait(IMAGE_LIB).click();
            return drone.findAndWait(IMAGE_RSLT).isDisplayed();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding " + IMAGE_LIB + " or " + IMAGE_RSLT, toe);
        }
        return false;
    }

    /**
     * click on save button to save wiki text.
     */
    public WikiPage clickSaveButton()
    {
        try
        {
            drone.waitUntilElementClickable(BUTTON_SAVE, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            WebElement saveButton = drone.findAndWait(BUTTON_SAVE);
            if (saveButton.isEnabled())
            {
                saveButton.click();
                drone.waitUntilElementDeletedFromDom(DEFAULT_CONTENT_TOOLBAR, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            }
            else
            {
                throw new PageException(BUTTON_SAVE + " is not enabled");
            }
                return new WikiPage(drone).render();
            }
        catch (TimeoutException toe)
        {
            throw new PageException("Not able find the Save Button");
        }
    }

    /**
     * Click on remove format button.
     */
    public void clickOnRemoveFormatting()
    {
        if (getDrone().getProperties().getVersion() == AlfrescoVersion.Enterprise5)
        {
            try
            {

                drone.findAndWait(FORMAT_BUTTON).click();
                drone.findAndWait(CLEAR_FORMAT_BUTTON).click();
            }
            catch (TimeoutException toe)
            {
                logger.error("Time out finding " + FORMAT_BUTTON + " or " + CLEAR_FORMAT_BUTTON, toe);
            }
        }
        else
        {
            try
            {
                drone.findAndWait(REMOVE_FORMAT).click();
            }
            catch (TimeoutException toe)
            {
                logger.error("Time out finding " + REMOVE_FORMAT, toe);
            }
        }
    }

    /**
     * Click to view images library.
     */
    public void clickImageOfLibrary()
    {
        try
        {
            drone.findAndWaitForElements(By.cssSelector("#image_results>img")).get(0).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
    }

    /**
     * Check if image is rendered in wiki text area.
     *
     * @return
     */
    public int imageCount(Mode mode)
    {
        String frameId;
        try
        {
            switch (mode)
            {
                case EDIT:
                    frameId = WIKI_EDIT_IFRAME;
                    break;
                default:
                    frameId = WIKI_FORMAT_IFRAME;
            }
            drone.switchToFrame(frameId);
            int totalImage = drone.findAndWaitForElements(By.cssSelector("#tinymce>p>img")).size();
            drone.switchToDefaultContent();
            return totalImage;

        }
        catch (TimeoutException toe)
        {
            logger.error("Time out rendering image", toe);
        }
        throw new PageException("Image is not rendered");
    }

    /**
     * Delete wiki page created.
     */
    public void deleteWiki()
    {

        try
        {
            By popupDeleteButton = By.cssSelector(drone.getElement("delete.wiki.popup"));
            drone.findAndWait(DELETE_WIKI).click();
            drone.findAndWait(popupDeleteButton).click();
        }
        catch (TimeoutException toe)
        {
            logger.error("Unable to find delete wiki button", toe);
        }
    }

    /**
     * @param type
     * @return
     */
    private By getCSSToRetrieveText(String type)
    {

        if ("BULLET".equals(type))
        {
            return By.cssSelector("#tinymce>ul>li");
        }
        else if ("NUMBER".equals(type))
        {
            return By.cssSelector("#tinymce>ol>li");
        }
        else if ("FONT".equals(type))
        {
            String selector = "#tinymce>ul>li>font";

            return By.cssSelector(selector);
        }
        else if ("IMG".equals(type))
        {
            return By.cssSelector("#tinymce>p>img");
        }
        else
        {
            return By.cssSelector("#tinymce");
        }
    }

    /**
     * Get TinyMCEEditor object to navigate TinyMCE functions.
     *
     * @return
     */
    public TinyMceEditor getTinyMCEEditor()
    {
        tinyMCEEditor.setTinyMce(WIKI_FORMAT_IFRAME);
        return tinyMCEEditor;
    }

    /**
     * Copy Image using CTRL+C
     */
    public void copyImageFromLib()
    {
        try
        {
            drone.switchToDefaultContent();
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce>p>img"));
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.chord(Keys.CONTROL, "c"));
            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
    }

    /**
     * Pasting image using CTRL+V
     */
    public void pasteImageOnEditor()
    {
        try
        {
            drone.switchToDefaultContent();
            drone.switchToFrame(WIKI_FORMAT_IFRAME);
            WebElement element = drone.findAndWait(By.cssSelector("#tinymce"));
            element.sendKeys(Keys.chord(Keys.CONTROL, "v"));
            element.sendKeys(Keys.chord(Keys.CONTROL, "v"));
            drone.switchToDefaultContent();
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
    }

    /**
     * Edit Wiki Page.
     *
     * @return
     */
    public WikiPage editWikiPage()
    {
        try
        {
            drone.findAndWait(EDIT_WIKI).click();
            drone.waitUntilElementClickable(DEFAULT_CONTENT_TOOLBAR, SECONDS.convert(maxPageLoadingTime, MILLISECONDS));
            return new WikiPage(drone).render();
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Time out finding image", toe);
        }
    }

    /**
     * @param type
     * @return
     */
    public String verifyEditText(String type)
    {
        try
        {
            drone.switchToFrame(WIKI_EDIT_IFRAME);
            String richText = drone.findAndWait(getCSSToRetrieveText(type)).getText();
            drone.switchToDefaultContent();
            return richText;
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding image", toe);
        }
        throw new PageException();
    }

    /**
     * @param type
     * @return
     */
    public String getFontAttributeValue(FONT_ATTR type)
    {
        try
        {
            drone.switchToFrame(WIKI_EDIT_IFRAME);
            String attrValue = drone.findAndWait(By.cssSelector("#tinymce>ul>li>font")).getAttribute(type.name());

            drone.switchToDefaultContent();
            return attrValue;
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding attribute of font element", toe);
        }
        throw new PageException("Font element not found!");
    }

    /**
     * Method to create a wiki page
     *
     * @param wikiTitle
     * @param txtLines
     * @return WikiPage object
     */

    public WikiPage createWikiPage(String wikiTitle, List<String> txtLines)
    {
        logger.info("Creating wiki page " + wikiTitle);
        try
        {
            WikiPage wikiPage = new WikiPage(drone);
            wikiPage.clickOnNewPage();
            wikiPage.createWikiPageTitle(wikiTitle);
            wikiPage.insertText(txtLines);
            wikiPage.clickSaveButton();
            return drone.getCurrentPage().render();
        }
        catch (TimeoutException e)
        {
            logger.debug("Couldn't create the page due to timeout");
        }
        throw new PageException("Wiki can't be created");
    }

    /**
     * Method to create a wiki page with tags
     *
     * @param wikiTitle
     * @param txtLines
     * @param tagsList
     * @return WikiPage object
     */
    public WikiPage createWikiPage(String wikiTitle, List<String> txtLines, List<String> tagsList)
    {
        logger.info("Creating wiki page " + wikiTitle);
        try
        {
            WikiPage wikiPage = new WikiPage(drone);
            wikiPage.clickOnNewPage();
            wikiPage.createWikiPageTitle(wikiTitle);
            wikiPage.insertText(txtLines);
            wikiPage.addTag(tagsList);
            return wikiPage.clickSaveButton();
        }
        catch (TimeoutException e)
        {
            logger.debug("Couldn't create the page due to timeout");
        }
        throw new PageException("Wiki can't be created");
    }

    /**
     * Edit Wiki Page.
     *
     * @return
     */
    public WikiPage editWikiPage(String txtLines, List<String> tagsList)
    {
        logger.info("Editing wiki page");
        try
        {
            editWikiPage();
            editWikiText(txtLines);
            addTag(tagsList);
            clickSaveButton();
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Time out finding image", toe);
        }
        return drone.getCurrentPage().render();
    }

    /**
     * Method to add tags to the new wiki page
     *
     * @param tagsList
     * @return WikiPage object
     */
    public WikiPage addTag(List<String> tagsList)
    {

        checkNotNull(tagsList);
        WebElement inputTag = drone.findAndWait(WIKI_TAG_INPUT);

        String tagString = "";
        for (String tag : tagsList)
            tagString += tag;

        inputTag.sendKeys(tagString);
        WebElement addButton = drone.findAndWait(ADD_TAG_BUTTON);
        addButton.click();

        return new WikiPage(drone);
    }

    private boolean isDisplayed(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isEnabled()
                && drone.find(locator).isDisplayed();
        }
        catch (TimeoutException te)
        {
            return false;
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Method to verify whether New Page button is displayed
     *
     * @return true if displayed
     */
    public boolean isNewPageDisplayed()
    {
        return isDisplayed(BUTTON_CREATE);
    }

    /**
     * Method to click Wiki Page List button
     *
     * @return WikiPageList
     */
    public WikiPageList clickWikiPageListBtn()
    {
        boolean isClicked = false;
        try
        {
            List<WebElement> elements = drone.findAll(By.cssSelector(".forwardLink>a"));
            for (WebElement webElement : elements)
            {
                if (drone.getValue("wiki.page.list").equals(webElement.getText()))
                //if(!webElement.getAttribute("href").contains("Main_Page"))
                {
                    webElement.click();
                    isClicked = true;
                    break;
                }
            }
            if (!isClicked)
            {
                elements = drone.findAll(By.cssSelector(".backLink>a"));
                for (WebElement webElement : elements)
                {
                    if (drone.getValue("wiki.page.list").equals(webElement.getText()))
                    //if(!webElement.getAttribute("href").contains("Main_Page"))
                    {
                        webElement.click();
                        isClicked = true;
                        break;
                    }
                }
            }

            //drone.findAndWait(BACK_LINK).click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + BACK_LINK);
        }
        catch (ClassCastException e)
        {
            throw new ShareException("Unable to click " + BACK_LINK);
        }
        return new WikiPageList(drone);
    }

    /**
     * Method to rename Wiki Page
     *
     * @param newTitle
     * @return
     */
    public WikiPage renameWikiPage(String newTitle)
    {
        logger.info("Renaming wiki page to" + newTitle);
        try
        {
            drone.findAndWait(RENAME_BUTTON).click();
            WebElement inputField = drone.findAndWait(By.cssSelector("input[id$='default-renameTo']"));
            inputField.clear();
            inputField.sendKeys(newTitle);
            drone.findAndWait(RENAME_SAVE_BTN).click();
            waitUntilAlert();
            logger.info("Renamed Wiki page");
            return new WikiPage(drone).render();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    /**
     * Method to click Details link
     */
    public void clickDetailsLink()
    {
        logger.info("Viewing details for wiki page");
        try
        {
            drone.findAndWait(DETAILS_LINK).click();
            waitUntilAlert();
            logger.info("Opened wiki details page");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DETAILS_LINK);
        }
    }

    /**
     * Method to revert to version provided
     *
     * @param versionNum
     * @return WikiPage
     */
    public WikiPage revertToVersion(Double versionNum)
    {
        List<WebElement> allVersions = drone.findAndWaitForElements(VERSION_PLACEHOLDER);
        if (allVersions.size() == 0)
        {
            throw new ShareException("The wiki page has no versions");
        }
        String versionNumber = Double.toString(versionNum);
        for (WebElement allVersion : allVersions)
        {
            if (allVersion.getText().contains(versionNumber))
            {
                allVersion.click();
            }
        }
        List<WebElement> allReverts = drone.findAll(REVERT_BTN);
        for (WebElement allRevert : allReverts)
        {
            if (allRevert.isDisplayed())
            {
                allRevert.click();
            }
        }
        confirmRevert();
        logger.info("Reverted Wiki page to version " + versionNum);
        return new WikiPage(drone);
    }

    private void confirmRevert()
    {
        try
        {
            drone.findAndWait(By.cssSelector("button[id$='ok-button-button']")).click();
            waitUntilAlert();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find OK button");
        }
    }

    /**
     * Method to retrieve Wiki version
     *
     * @return double
     */
    public Double getCurrentWikiVersion()
    {
        try
        {
            Pattern p1 = Pattern.compile("(\\d{1,3}\\.\\d{1,3})");

            String wikiVersion = drone.findAndWait(VERSION_HEADER).getText();
            Matcher m1 = p1.matcher(wikiVersion);
            if (m1.find())
            {
                return Double.parseDouble(m1.group());
            }
            else
                throw new IllegalArgumentException("Cannot find the version");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to retrieve the version");
        }
    }

    /**
     * Method to edit a wiki text
     *
     * @param txtLines
     */
    public void editWikiText(String txtLines)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txtLines);
            drone.executeJavaScript(setCommentJs);
        }
        catch (TimeoutException toe)
        {
            logger.error("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method to return wiki text
     *
     * @return String
     */
    public String getWikiText()
    {
        try
        {
            return drone.findAndWait(WIKI_TEXT).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + WIKI_TEXT);
        }
    }

    /**
     * Method to return wiki name
     *
     * @return String
     */
    public String getWikiTitle()
    {
        try
        {
            return drone.findAndWait(WIKI_TITLE).getText();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + WIKI_TITLE);
        }
    }

    /**
     * Method to check whether Rename button is displayed
     *
     * @return
     */
    public boolean isRenameEnabled()
    {
        return drone.findAndWait(RENAME_BUTTON).isEnabled();
    }

    public boolean isRevertEnabled()
    {
        return drone.isElementDisplayed(REVERT_BTN);
    }

    /**
     * Method to retrieve tag added to Wiki
     *
     * @return String
     */
    public String getTagName()
    {
        try
        {
            if (!drone.find(TAG).isDisplayed())
                return null;
            else
            {
                String tagName = drone.findAndWait(TAG).getText();
                if (!tagName.isEmpty())
                    return tagName;

                else
                    throw new IllegalArgumentException("Cannot find tag");
            }
        }
        catch (NoSuchElementException te)
        {
            if(!drone.find(TAG_NONE).isDisplayed())
                return null;
            else
                return drone.findAndWait(TAG_NONE).getText();
        }
    }

    /**
     * Method to view the given version of wiki page
     *
     * @param versionNum
     */
    public HtmlPage viewVersion(Double versionNum)
    {
        logger.info("Viewing Wiki Page version");
        if (versionNum == null)
        {
            throw new IllegalArgumentException("Version number is required");
        }
        try
        {
            String versionNumber = Double.toString(versionNum);
            WebElement selectVersionBtn = drone.findAndWait(SELECT_VERSION_BUTTON);
            selectVersionBtn.click();

            List<WebElement> allVerions = drone.findAll(By.cssSelector(".bd>ul>li"));

            List<String> stringValues = new ArrayList<String>();
            for (WebElement allVers : allVerions)
            {
                stringValues.add(allVers.getText());
            }
            if (stringValues.contains(versionNumber) || stringValues.contains(versionNumber + LATEST_VERSION))
            {
                for (WebElement allTheVersions : allVerions)
                {
                    if (allTheVersions.getText().contentEquals(versionNumber) || allTheVersions.getText().contentEquals(versionNumber + LATEST_VERSION))
                    {
                        allTheVersions.click();
                        break;
                    }
                }
            }
            else
            {
                throw new PageException("The version provided isn't present");
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find the button");
        }
        return drone.getCurrentPage();
    }

    /**
     * Method to view Rss Feed for wiki pages
     *
     * @param username
     * @param password
     * @return RssFeedPage
     */
    public RssFeedPage clickRssFeedBtn(String username, String password)
    {
        logger.info("Viewing RSS Feed for wiki page");
        String currentWikiUrl = drone.getCurrentUrl();
        String protocolVar = PageUtils.getProtocol(currentWikiUrl);
        String shareUrlVar = PageUtils.getShareUrl(currentWikiUrl);
        String siteName = PageUtils.getSiteName(currentWikiUrl);
        String rssUrl = String.format("%s%s:%s@%s/feedservice/components/wiki/rss?site=%s", protocolVar, username, password, shareUrlVar, siteName);
        drone.navigateTo(rssUrl);
        return new RssFeedPage(drone).render();
    }

    /**
     * Method to navigate to Main Page
     *
     * @return true if Main Page is open
     */
    public boolean openMainPage()
    {
        boolean isMainPage = false;
        try
        {
            List<WebElement> mainPage = drone.findAll(MAIN_PAGE);
            for (WebElement theMainPage : mainPage)
            {
                if (theMainPage.getAttribute("href").contains("Main_Page"))
                {
                    theMainPage.click();
                    break;
                }
            }
            waitUntilAlert();
            String currentUrl = drone.getCurrentUrl();
            if (currentUrl.contains("filter=main&title=Main_Page"))
            {
                logger.info("Opened Wiki Main Page");
                isMainPage = true;
            }
        }
        catch (TimeoutException te)
        {
            throw new PageException("Unable to find " + MAIN_PAGE);
        }
        return isMainPage;
    }

    /**
     * Method to verify whether wiki details page has all necessary elements present
     *
     * @param wikiTitle
     * @param wikiText
     * @return boolean
     */
    public boolean isWikiDetailsCorrect(String wikiTitle, String wikiText)
    {
        wikiTitle = wikiTitle.replace("_", " ");
        return isTitleBarActionsDisplayed() && isDownPanesDisplayed()
            && isDetailsWrapperDisplayed() && getWikiTitle().contentEquals(wikiTitle)
            && getWikiText().contentEquals(wikiText);
    }

    private boolean isTitleBarActionsDisplayed()
    {
        return isDisplayed(VIEW_LINK) && isDisplayed(EDIT_WIKI)
            && isDisplayed(DETAILS_LINK);
    }

    private boolean isDownPanesDisplayed()
    {
        return isDisplayed(By.xpath(VERSION_HISTORY_CONTAINER)) && isDisplayed(By.xpath(TAGS_CONTAINER))
            && isDisplayed(By.xpath(LINKED_PAGES_CONTAINER));
    }

    private boolean isDetailsWrapperDisplayed()
    {
        return isDisplayed(By.cssSelector(".details-wrapper"));
    }
}
