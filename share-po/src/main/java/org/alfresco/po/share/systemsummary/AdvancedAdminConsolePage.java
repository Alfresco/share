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
package org.alfresco.po.share.systemsummary;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * @author sergey.kardash on 4/11/14.
 */
public abstract class AdvancedAdminConsolePage extends SharePage
{

    // private Log logger = LogFactory.getLog(this.getClass());
    private final String CHECKBOX = "//span[@class='value']//img";
    private final String VALUE = "//span[@class='value']";

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public HtmlPage openConsolePage(AdminConsoleLink adminConsoleLink)
    {
        findAndWait(adminConsoleLink.contentLocator).click();
        return getCurrentPage();
    }

    /**
     * Checks if tab present at a left column af tabs' list
     * @param adminConsoleLink AdminConsoleLink
     * @return boolean
     */
    public boolean isConsoleLinkPresent(AdminConsoleLink adminConsoleLink)
    {
          try
          {
              return findAndWait(adminConsoleLink.contentLocator).isDisplayed();
          }
          catch (NoSuchElementException nse)
          {
              return false;
          }
    }

    /**
     * gets value of the component
     *
     * @return true if any value present
     */
    public String getValueOfElement(String element)
    {
        try
        {
            WebElement getV = findAndWait(By.xpath(element + VALUE));
            return getV.getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Element isn't present", toe);
        }

    }

    /**
     * Checks if value of the component present
     *
     * @return true if any data is present
     */
    public boolean isDataPresent(String element)
    {
        try
        {
            WebElement data = findAndWait(By.xpath(element));
            return data.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Checks if a radio button of the component present
     *
     * @return true if the ratio button is present
     */
    public boolean isRadioButtonPresent(String element)
    {
        try
        {
            WebElement button = findAndWait(By.xpath(element + CHECKBOX));
            return button.isDisplayed();
        } 
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

}
