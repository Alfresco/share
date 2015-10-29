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
package org.alfresco.po.share.site.document;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.enums.Encoder;
import org.alfresco.po.share.enums.TinyMceColourCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.owasp.esapi.ESAPI;

@FindBy(css="div.mce-tinymce")
/**
 * @author nshah
 */
public class TinyMceEditor extends PageElement
{
    private Log logger = LogFactory.getLog(TinyMceEditor.class);

    private static final String TINY_MCE_SELECT_ALL_COMMAND = "tinyMCE.activeEditor.selection.select(tinyMCE.activeEditor.getBody(),true);";
    // private static final String XPATH_COLOUR_FONT = "//font";
    private static final String CSS_REMOVE_FORMAT = "i.mce-i-removeformat";
    private static final String CSS_STR_BOLD = "i[class$='mce-i-bold']";
    public static final String TINYMCE_CONTENT = "body[id='tinymce']";
    private static final String CSS_STR_ITALIC = "i[class$='mce-i-italic']";
    private static final String CSS_STR_UNDER_LINED = "i[class$='mce-i-underline']";
    private static final String CSS_STR_BULLETS = "i[class$='mce-i-bullist']";
    private static final String CSS_STR_NUMBERS = "i[class$='mce-i-numlist']";
    private static final String CSS_STR_BOLD_FMT_TXT = "#tinymce>p>b";
    private static final String CSS_STR_ITALIC_FMT_TXT = "#tinymce>p>i";
    private static final String CSS_STR_UNDER_LINED_FMT_TXT = "#tinymce>p>u";
    private static final String CSS_STR_BULLET_FMT_TXT = "#tinymce>ul>li";
    private static final String CSS_STR_NUMBER_FMT_TXT = "#tinymce>ol>li";
    private static final String CSS_STR_TEXT_TAG = "#tinymce>p";
    public String CSS_STR_FORE_COLOUR = "div[aria-label^='Text'] button[class$='mce-open']";
    private static final String CSS_COLOR_FONT = "#tinymce>p>font";
    private static final String CSS_EDIT = "button[id$='mce_43-open']";
    private static final String CSS_FORMAT = "button[id$='mce_46-open']";
    private static final String CSS_UNDO = "i[class$='mce-i-undo']";
//    private static final String CSS_UNDO  = ".//form[contains(@id, 'default-add-form')]//i[contains(@class, 'mce-i-undo')]";
    private static final String CSS_REDO = "i[class$='mce-i-redo']";
    private static final String CSS_BULLET_TEXT = "#tinymce>ul>li";
    protected String CSS_STR_BACK_GROUND_COLOUR;
    private String frameId = null;
    private FormatType formatType;

    public enum FormatType
    {
        BOLD,
        ITALIC,
        UNDERLINED,
        NUMBER,
        BULLET,
        BOLD_FMT_TXT,
        ITALIC_FMT_TXT,
        UNDER_LINED_FMT_TXT,
        BULLET_FMT_TXT,
        NUMBER_FMT_TXT,
        COLOR,
        FORMAT,
        EDIT,
        UNDO,
        REDO,
        DEFAULT,
        COLOR_FONT,
        BULLET_TEXT,
        BOLD_EDIT,
        BACK_GROUND_COLOR;
    }
    public String getFrameId()
    {
        if(frameId == null)
        {
            setTinyMce();
        }
        return frameId;
    }

    protected void setFrameId(String frameId)
    {
        this.frameId = frameId;
    }

    public void setFormatType(FormatType formatType)
    {
        this.formatType = formatType;
    }

    public String getCSSOfFormatType()
    {
        switch (formatType)
        {
            case BOLD:
                return CSS_STR_BOLD;
            case ITALIC:
                return CSS_STR_ITALIC;
            case UNDERLINED:
                return CSS_STR_UNDER_LINED;
            case BULLET:
                return CSS_STR_BULLETS;
            case NUMBER:
                return CSS_STR_NUMBERS;
            case COLOR:
                return CSS_STR_FORE_COLOUR;
            case FORMAT:
                return CSS_FORMAT;
            case EDIT:
                return CSS_EDIT;
            case UNDO:
                return CSS_UNDO;
            case REDO:
                return CSS_REDO;
                // temporary solution
            case BOLD_EDIT:
                return "DIV[class='comments-list']>DIV[class='comment-form'] " + CSS_STR_BOLD;
            case BACK_GROUND_COLOR:
                return CSS_STR_BACK_GROUND_COLOUR;
            default:
                throw new PageException();

        }
    }

    public String getCSSOfText(FormatType formatType)
    {
        switch (formatType)
        {
            case BOLD_FMT_TXT:
                return CSS_STR_BOLD_FMT_TXT;
            case ITALIC_FMT_TXT:
                return CSS_STR_ITALIC_FMT_TXT;
            case UNDER_LINED_FMT_TXT:
                return CSS_STR_UNDER_LINED_FMT_TXT;
            case BULLET_FMT_TXT:
                return CSS_STR_BULLET_FMT_TXT;
            case NUMBER_FMT_TXT:
                return CSS_STR_NUMBER_FMT_TXT;
            case COLOR_FONT:
                return CSS_COLOR_FONT;
            case BULLET_TEXT:
                return CSS_BULLET_TEXT;
            default:
                return CSS_STR_TEXT_TAG;
        }
    }

    @FindBy(tagName="iframe") WebElement frame;
    public void setTinyMce()
    {
        setFrameId(frame.getAttribute("id"));
    }

//    /**
//     * Constructor
//     */
//    public TinyMceEditor(WebDriver driver)
//    {
//        try
//        {
//            this.FRAME_ID = driver.findElement(By.cssSelector("iframe[id$='_default-add-content_ifr']")).getAttribute("id");
//            setFrameId(FRAME_ID);
//        }
//        catch (NoSuchElementException nse)
//        {
//        }
//    }

    /**
     * @param txt
     */
    public void addContent(String txt)
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", txt);
            executeJavaScript(setCommentJs);
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element : " + txt + " is not present", noSuchElementExp);
        }
    }

    /**
     * This method sets the given text into Site Content Configure text editor.
     * 
     * @param text
     */

    public void setText(String text)
    {
        if (text == null)
        {
            throw new IllegalArgumentException("Text is required");
        }

        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
            executeJavaScript(setCommentJs);
            setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", text);
            executeJavaScript(setCommentJs);
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            throw new PageException("Unable to find text css in tinyMCE editor.", noSuchElementExp);
        }
    }

    /**
     * This method sets the given text into Site Content Configure text editor.
     * 
     * @param text
     * @param encoder Encode the text before adding it to the text editor.
     */
    public void setText(String text, Encoder encoder)
    {
        String encodedComment = text;
        if (encoder == null)
        {
            // Assume no encoding
            encoder = Encoder.ENCODER_NOENCODER;
        }

        switch (encoder)
        {
            case ENCODER_HTML:
                encodedComment = ESAPI.encoder().encodeForHTML(text);
                logger.info("Text encoded as HTML");
                break;
            case ENCODER_JAVASCRIPT:
                encodedComment = ESAPI.encoder().encodeForJavaScript(text);
                logger.info("Text encoded as JavaScript");
                break;
            default:
                logger.info("Text is not encoded");
        }
        setText(encodedComment);
    }

    /**
     * Click on TinyMCE editor's format option.
     */
    public void clickTextFormatter(FormatType formatType)
    {
        setFormatType(formatType);
        selectTextFromEditor();
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    /**
     * Click to select color code on text.
     */
    public void clickColorCode(TinyMceColourCode colourCode)
    {
        selectTextFromEditor();
        setFormatType(FormatType.COLOR);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
        clickElementOnRichTextFormatter(colourCode.getForeColourLocator());
    }

    /**
     * Click to select color code on text.
     */
    public void clickBackgroundColorCode(TinyMceColourCode bgColourCode)
    {
        selectTextFromEditor();
        setFormatType(FormatType.BACK_GROUND_COLOR);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
        clickElementOnRichTextFormatter(bgColourCode.getBgColourLocator());
    }

    /**
     * click to undo to default format.
     */
    public void clickUndo()
    {
        setFormatType(FormatType.UNDO);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    /**
     * click to edit button
     */
    public void clickEdit()
    {
        setFormatType(FormatType.EDIT);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    /**
     * click to format button
     */
    public void clickFormat()
    {
        setFormatType(FormatType.FORMAT);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    /**
     * Click to Redo the undo operation.
     */
    public void clickRedo()
    {
        setFormatType(FormatType.REDO);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    public String getColourAttribute()
    {
        driver.switchTo().frame(getFrameId());
        WebElement html5Editor = driver.findElement(By.cssSelector("#tinymce>ol>li>span"));
        String colorAttr = html5Editor.getAttribute("style");
        driver.switchTo().defaultContent();
        return colorAttr;
    }

    /**
     * Click to remove formatting from text.
     */
    public void removeFormatting()
    {
        try
        {
            driver.findElement(By.cssSelector(CSS_REMOVE_FORMAT)).click();
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :" + CSS_REMOVE_FORMAT + " does not exist", noSuchElementExp);
        }
    }

    public void selectTextFromEditor()
    {
        // This select all in the edit pane
        /**
         * @author Michael Suzuki Changed to use tinymce directly as its faster to edit with tinymce object instead of using the ui. The script below will
         *         select every thing inside the editing pane.
         */
        executeJavaScript(TINY_MCE_SELECT_ALL_COMMAND);
    }

    /**
     * @param cssString
     */
    protected void clickElementOnRichTextFormatter(String cssString)
    {
        try
        {
            driver.switchTo().defaultContent();
            findFirstDisplayedElement(By.cssSelector(cssString)).click();

        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :" + cssString + " does not exist", noSuchElementExp);
        }
    }

    public String getText()
    {
        try
        {
            driver.switchTo().frame(getFrameId());
            String text = driver.findElement(By.cssSelector(TINYMCE_CONTENT)).getText();
            driver.switchTo().defaultContent();
            return text;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element : does not exist", noSuchElementExp);
            throw new PageException("Unable to find text in tinyMCE editor.", noSuchElementExp);
        }
    }

    public String getContent()
    {
        try
        {
            driver.switchTo().frame(getFrameId());
            WebElement element = driver.findElement(By.cssSelector(TINYMCE_CONTENT));
            String contents = (String) executeJavaScript("return arguments[0].innerHTML;", element);
            driver.switchTo().defaultContent();
            return contents;
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            logger.error("Element :body[id$='tinymce'] does not exist", noSuchElementExp);
            throw new PageException("Unable to find content in tinyMCE editor.", noSuchElementExp);
        }
    }

    /**
     * Click on TinyMCE editor's format option.
     * 
     * @param formatType
     */
    public void clickTextFormatterWithOutSelectingText(FormatType formatType)
    {
        setFormatType(formatType);
        clickElementOnRichTextFormatter(getCSSOfFormatType());
    }

    protected void setBGColorLinkCss(String css)
    {
        this.CSS_STR_BACK_GROUND_COLOUR = css;
    }

    protected void setForeColorLinkCss(String css)
    {
        this.CSS_STR_FORE_COLOUR = css;
    }

    /**
     * This method does the removing of text/image/links and format from the tinymce editor.
     */
    public void clearAll()
    {
        try
        {
            String setCommentJs = String.format("tinyMCE.activeEditor.setContent('%s');", "");
            executeJavaScript(setCommentJs);
        }
        catch (NoSuchElementException noSuchElementExp)
        {
            throw new PageException("Unable to find text css in tinyMCE editor.", noSuchElementExp);
        }
    }
}
