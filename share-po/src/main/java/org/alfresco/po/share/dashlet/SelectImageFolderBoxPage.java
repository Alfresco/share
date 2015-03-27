/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

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

    /**
     * Constructor.
     */
    public SelectImageFolderBoxPage(WebDrone drone)
    {
        super(drone);
    }

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
    public SelectImageFolderBoxPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectImageFolderBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public void clickCancel()
    {
        drone.findAndWait(CANCEL_BUTTON).click();
    }
}
