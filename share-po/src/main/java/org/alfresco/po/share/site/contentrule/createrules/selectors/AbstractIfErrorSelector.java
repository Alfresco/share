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
package org.alfresco.po.share.site.contentrule.createrules.selectors;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Created by sergey.kardash on 2/26/14.
 */
public abstract class AbstractIfErrorSelector extends PageElement
{
    private static final By IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT = By.xpath("//div[@class='form-field scriptRef']/select[contains(@id,'default-scriptRef')]");

    
    protected void selectScript(String visibleName)
    {
        List<WebElement> scriptOptions = findAndWaitForElements(IF_ERRORS_OCCURS_RUN_SCRIPTS_SELECT);
        List<Select> scriptSelects = new ArrayList<Select>();
        for (WebElement scriptOption : scriptOptions)
        {
            scriptSelects.add(new Select(scriptOption));
        }
        scriptSelects.get(scriptSelects.size() - 1).selectByVisibleText(visibleName);
    }
}
