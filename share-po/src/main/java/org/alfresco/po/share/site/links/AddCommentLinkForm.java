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

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class AddCommentLinkForm extends HtmlElement
{

    protected final TinyMceEditor tinyMceEditor;
    protected static final By SUBMIT_BTN = By.cssSelector("button[id$='-submit-button']");


    /*
     * Constructor
     */
    public AddCommentLinkForm(WebDrone drone)
    {
        super(drone);
        tinyMceEditor = new TinyMceEditor(drone);
    }

    public TinyMceEditor getTinyMceEditor()
    {
        return tinyMceEditor;
    }

    protected void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    /**
     * Method for inserting text into the Reply form
     *
     * @param txtLines
     */
    public void insertText(String txtLines)
    {
        try
        {
            drone.waitUntilElementClickable(getSubmitBtnBy(), 3000);
            tinyMceEditor.setText(txtLines);
        }
        catch (TimeoutException toe)
        {
            throw new ShareException("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method for clicking Submit button
     *
     * @return Topic view page
     */
    public LinksDetailsPage clickSubmit()
    {
        try
        {
            drone.findAndWait(getSubmitBtnBy()).click();
            return new LinksDetailsPage(drone).waitUntilAlert().render();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Cannot find Submit button");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    protected By getSubmitBtnBy()
    {
        return SUBMIT_BTN;
    }

}
