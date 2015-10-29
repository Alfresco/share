/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
 */
package org.alfresco.po.share.site.links;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.PageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class LinkComment extends PageElement
{

    private static Log logger = LogFactory.getLog(LinkComment.class);

    private static final By TEXT = By.xpath(".//div[@class='comment-content']/p");
    private static final By EDIT_BUTTON = By.xpath(".//a[contains(@class,'edit-comment')]");
    private static final By DELETE_BUTTON = By.xpath(".//a[contains(@class,'delete-comment')]");
    private static final By CONFIRM_DELETE_BUTTON = By.xpath("//span[@class='button-group']/span[1]//button");

    public LinkComment(WebElement webElement, WebDriver driver)
    {
        setWrappedElement(webElement);
    }

    private void focusOn()
    {
        mouseOver(getWrappedElement());
    }

    public LinksDetailsPage editComment(String newText)
    {
        checkNotNull(newText);
        focusOn();
        findAndWait(EDIT_BUTTON).click();
        EditCommentLinkForm editCommentLinkForm = new EditCommentLinkForm();
        editCommentLinkForm.insertText(newText);
        editCommentLinkForm.clickSubmit();
        return factoryPage.instantiatePage(driver,LinksDetailsPage.class).waitUntilAlert().render();
    }

    public LinksDetailsPage deleteComment()
    {
        focusOn();
        findAndWait(DELETE_BUTTON).click();
        findAndWait(CONFIRM_DELETE_BUTTON).click();
        return factoryPage.instantiatePage(driver,LinksDetailsPage.class).waitUntilAlert().render();
    }

    public String getText()
    {
        return findAndWait(TEXT).getText();
    }


    public boolean isCorrect()
    {
        try
        {
            focusOn();
            WebElement text = findAndWait(TEXT);
            WebElement editBtn = findAndWait(EDIT_BUTTON);
            WebElement deleteBtn = findAndWait(DELETE_BUTTON);
            boolean isCorrect = editBtn.isDisplayed() && editBtn.isEnabled();
            isCorrect = isCorrect && deleteBtn.isDisplayed() && deleteBtn.isDisplayed();
            isCorrect = isCorrect && text.isDisplayed();
            return isCorrect;
        }
        catch (Exception e)
        {
            logger.error("LinkComment don't correct.");
            return false;
        }
    }


}
