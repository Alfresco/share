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
package org.alfresco.po.share.site.document;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public abstract class AbstractCommentForm extends PageElement
{
    private final static By FORM_TITLE = By.cssSelector("div[class='comment-form']>h2.thin.dark");
    private TinyMceEditor tinyMceEditor;


    public String getTitle()
    {
        return findAndWait(FORM_TITLE).getText();
    }

    public TinyMceEditor getTinyMceEditor()
    {
        return tinyMceEditor;
    }

    protected void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    public boolean isButtonsEnable(By submit, By cancel)
    {
        try
        {
            return findAndWait(submit).isEnabled() && findAndWait(cancel).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }
}
