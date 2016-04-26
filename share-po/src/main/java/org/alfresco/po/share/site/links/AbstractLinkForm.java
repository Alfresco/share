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
package org.alfresco.po.share.site.links;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.exception.ShareException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.TextInput;

/**
 * An abstract of Link form
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractLinkForm extends PageElement
{
    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By URL_FIELD = By.cssSelector("input[id$='default-url']");
    protected static final By DESCRIPTION_FIELD = By.cssSelector("textarea[id$='default-description']");
    protected static final By INTERNAL_CHKBOX = By.cssSelector("input[id$='default-internal']");
    protected static final By CANCEL_BTN = By.cssSelector("button[id$='default-cancel-button']");
    protected static final By TAG_INPUT = By.cssSelector("#template_x002e_linkedit_x002e_links-linkedit_x0023_default-tag-input-field");
    protected static final String LINK_TAG = "//a[@class='taglibrary-action']/span[text()='%s']";
    protected static final By ADD_TAG_BUTTON = By.cssSelector("#template_x002e_linkedit_x002e_links-linkedit_x0023_default-add-tag-button");


    /**
     * Method for setting an input into the field
     *
     * @param input
     * @param value
     */
    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new ShareException("Unable to find " + input);
        }
    }

    @FindBy(css="input[id$='default-title']") TextInput titleInput;
    public void setTitleField(final String title)
    {
        titleInput.clear();
        titleInput.sendKeys(title);
    }

    public void setUrlField(final String title)
    {
        setInput(findAndWait(URL_FIELD), title);
    }

    public void setDescriptionField(final String title)
    {
        setInput(findAndWait(DESCRIPTION_FIELD), title);
    }

    protected void setInternalChkbox()
    {
        findAndWait(INTERNAL_CHKBOX).click();
    }

    protected void addTag(final String tag)
    {
        WebElement tagField = findAndWait(TAG_INPUT);
        tagField.clear();
        tagField.sendKeys(tag);
        driver.findElement(ADD_TAG_BUTTON).click();
    }

    /**
     * Method for removing tag
     * method validate by LinksPageTest.removeTags
     *
     * @param tag
     */
    protected void removeTag(String tag)
    {
        String tagXpath = String.format(LINK_TAG, tag);
        WebElement element;
        try
        {
            element = findAndWait(By.xpath(tagXpath));
            element.click();
            waitUntilElementDisappears(By.xpath(tagXpath), 3000);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find tag " + tag + "");
        }
    }

    /**
     * Method for clicking Cancel button
     *
     * @param title
     */
    protected void clickCancelBtn(final String title)
    {
        try
        {
            findAndWait(CANCEL_BTN).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + CANCEL_BTN);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding " + CANCEL_BTN);
        }
    }

}
