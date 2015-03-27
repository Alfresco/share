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
package org.alfresco.po.thirdparty.flickr;

import org.alfresco.webdrone.*;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * @author Aliaksei Boole
 */
public class YahooSignInPage extends Page
{

    private final static By USERNAME_INPUT = By.xpath("//input[@id='username']");
    private final static By PASSWORD_INPUT = By.xpath("//input[@id='passwd']");
    private final static By LOGIN_BUTTON = By.xpath("//button[@id='.save']");
    private final static By SIGN_IN_CHECKBOX = By.xpath("//span[@id='pLabelC']");

    public YahooSignInPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public YahooSignInPage render(RenderTime timer)
    {
        elementRender(timer,
                getVisibleRenderElement(USERNAME_INPUT),
                getVisibleRenderElement(PASSWORD_INPUT),
                getVisibleRenderElement(LOGIN_BUTTON),
                getVisibleRenderElement(SIGN_IN_CHECKBOX));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public YahooSignInPage render(long time)
    {
        checkArgument(time > 0);
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public YahooSignInPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public FlickrUserPage login(String userName, String password)
    {

        fillField(USERNAME_INPUT, userName);
        fillField(PASSWORD_INPUT, password);
        click(SIGN_IN_CHECKBOX);
        click(LOGIN_BUTTON);
        return new FlickrUserPage(drone).render();
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        inputField.sendKeys(text);
    }


    //TODO Temporary method. Before elementRender from SharePage didn't moved out to Page.
    private void elementRender(RenderTime renderTime, RenderElement... elements)
    {
        for (RenderElement element : elements)
        {
            try
            {
                renderTime.start();
                long waitSeconds = TimeUnit.MILLISECONDS.toSeconds(renderTime.timeLeft());
                element.render(drone, waitSeconds);
            }
            catch (TimeoutException e)
            {
                throw new PageRenderTimeException("element not rendered in time.");
            }
            finally
            {
                renderTime.end(element.getLocator().toString());
            }
        }
    }
}
