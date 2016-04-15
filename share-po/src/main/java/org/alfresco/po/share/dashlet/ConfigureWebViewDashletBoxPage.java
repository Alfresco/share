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
package org.alfresco.po.share.dashlet;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object to reflect the configure web view box web elements
 *
 * @author Marina.Nenadovets
 */

public class ConfigureWebViewDashletBoxPage extends SharePage
{
    private static final By OK_BUTTON = By.cssSelector("button[id$='configDialog-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='configDialog-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");
    private static final By LINK_TITLE_FIELD = By.cssSelector("input[id$='webviewTitle']");
    private static final By URL_FIELD = By.cssSelector("input[id$='url']");

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureWebViewDashletBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON), getVisibleRenderElement(CLOSE_BUTTON),
                getVisibleRenderElement(LINK_TITLE_FIELD), getVisibleRenderElement(URL_FIELD));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public ConfigureWebViewDashletBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void config(String title, String url)
    {
        fillLinkTitle(title);
        fillUrlField(url);
        clickOkButton();
    }

    /**
     * Mimic fill title field on Box.
     *
     * @param text String
     */
    public void fillLinkTitle(String text)
    {
        checkNotNull(text);
        fillField(LINK_TITLE_FIELD, text);
    }

    /**
     * Mimic fill url field on Box.
     *
     * @param text String
     */
    public void fillUrlField(String text)
    {
        checkNotNull(text);
        fillField(URL_FIELD, text);
    }

    public void clickOkButton()
    {
        click(OK_BUTTON);
    }

    private void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }
}
