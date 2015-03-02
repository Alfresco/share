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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Page object holds the elements of Select Wiki box page
 *
 * @author Marina.Nenadovets
 */

public class SelectWikiDialogueBoxPage extends SharePage
{
    private static final By SELECT_DROP_DOWN = By.cssSelector("select[name='wikipage']");
    private static final By OK_BUTTON = By.cssSelector("button[id$='configDialog-ok-button']");
    private static final By CANCEL_BUTTON = By.cssSelector("button[id$='configDialog-cancel-button']");
    private static final By CLOSE_BUTTON = By.cssSelector(".container-close");

    /**
     * Constructor.
     */
    protected SelectWikiDialogueBoxPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(OK_BUTTON), getVisibleRenderElement(CANCEL_BUTTON),
                getVisibleRenderElement(CLOSE_BUTTON));
        return this;

    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SelectWikiDialogueBoxPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method for config wikiPages
     * @param visibleText
     */
    public void selectWikiPageBy(String visibleText)
    {
        checkNotNull(visibleText);
        WebElement element = drone.findAndWait(SELECT_DROP_DOWN);
        Select select = new Select(element);
        select.selectByVisibleText(visibleText);
        drone.findAndWait(OK_BUTTON).click();
        waitUntilAlert();
    }
}
