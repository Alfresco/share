/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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

package org.alfresco.po.alfresco;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by ivan.kornilov on 22.04.2014.
 */

public class MyAlfrescoPage extends LoginAlfrescoPage
{

    private final By logoutXPath = By.xpath("//*[@id='logout']");

    protected MyAlfrescoPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyAlfrescoPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyAlfrescoPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyAlfrescoPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to verify My Alfresco Page is opened
     *
     * @return boolean
     */
    public boolean userIsLoggedIn(String userName)
    {
        return drone.findAndWait(logoutXPath, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).getText()
            .equalsIgnoreCase(String.format("Logout (%s)", userName));
    }
}
