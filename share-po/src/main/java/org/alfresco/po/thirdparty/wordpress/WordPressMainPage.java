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
package org.alfresco.po.thirdparty.wordpress;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.Page;
import org.alfresco.po.RenderTime;
import org.alfresco.po.share.exception.ShareException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Marina.Nenadovets
 */

public class WordPressMainPage extends Page
{
    private static final By LOG_IN_BUTTON = By.cssSelector(".login>a");

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(LOG_IN_BUTTON));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WordPressMainPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public WordPressSignInPage clickLogIn()
    {
        try
        {
            WebElement loginBtn = findAndWait(LOG_IN_BUTTON);
            loginBtn.click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find the " + LOG_IN_BUTTON);
        }
        return factoryPage.instantiatePage(driver, WordPressSignInPage.class).render();
    }
}
