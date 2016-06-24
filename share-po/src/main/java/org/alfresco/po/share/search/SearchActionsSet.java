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
package org.alfresco.po.share.search;

import org.alfresco.po.share.FactoryPage;
import org.alfresco.po.share.admin.ActionsSet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Representation of an Action Set seen in faceted search result page.
 * 
 * @author Michael Suzuki
 */
public class SearchActionsSet extends ActionsSet
{
    /** Constants */
    private static final By CONTROL_ELEMENT = By.cssSelector("td.actionsCell>div");

    /**
     * Instantiates a new actions set.
     * 
     * @param driver the web driver
     * @param element the element
     */
    public SearchActionsSet(WebDriver driver, WebElement element, FactoryPage factoryPage)
    {
        super(driver,factoryPage);
        this.control = element.findElement(CONTROL_ELEMENT);
        // The dropdown menu has the same id as the control element with '_dropdown' appended
    }
}
