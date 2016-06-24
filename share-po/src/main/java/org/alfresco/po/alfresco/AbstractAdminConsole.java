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

package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * Abstract class to hold all common features in alfresco admin pages.
 * @author Michael Suzuki
 *
 */
public abstract class AbstractAdminConsole extends SharePage
{
    protected final static By SUBMIT_BUTTON = By.cssSelector("input.inline"); 
    private final By CLOSE_BUTTON = By.cssSelector("input[id$='Admin-console-title:_idJsp1']");

    /**
     * Method for click Close Button
     */
    public void clickClose()
    {
        findAndWait(CLOSE_BUTTON).click();
    }

    public String getResult()
    {
        return findAndWait(By.tagName("pre")).getText();
    }

}
