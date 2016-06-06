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

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * Page object holds the elements of Select Image box
 *
 * @author Marina.Nenadovets
 */
public class SelectImageFolderBoxPage extends SharePage
{
    private static final By DESTINATION_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-modeGroup']");
    private static final By SITES_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-sitePicker']");
    private static final By PATH_CONTAINER = By.cssSelector("div[id$='default-rulesPicker-treeview']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='default-rulesPicker-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='default-rulesPicker-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(DESTINATION_CONTAINER),
                getVisibleRenderElement(SITES_CONTAINER),
                getVisibleRenderElement(PATH_CONTAINER),
                getVisibleRenderElement(OK_BUTTON),
                getVisibleRenderElement(CANCEL_BUTTON),
                getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void clickCancel()
    {
        findAndWait(CANCEL_BUTTON).click();
    }
}
