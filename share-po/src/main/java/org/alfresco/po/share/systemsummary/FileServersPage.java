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

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Created by olga.lokhach on 9/9/2014.
 */

public class FileServersPage extends AdvancedAdminConsolePage
{
    @RenderWebElement
    private final static By FILE_SYSTEMS = By.cssSelector("input[name$='filesystem.name']");
    @RenderWebElement
    private final static By SAVE_BUTTON = By.xpath("//input[@value='Save']");
    @RenderWebElement
    private final static By CANCEL_BUTTON = By.xpath("//input[@value='Cancel']");

    //FTP
    @RenderWebElement
    private final static By FTP_ENABLED_CHECKBOX = By.cssSelector("input[onchange*='ftp.enabled']");
    @RenderWebElement
    private final static By FTP_PORT_INPUT = By.cssSelector("input[name$='ftp.port']");
    @RenderWebElement
    private final static By FTP_DATAPORT_TO = By.cssSelector("input[name$='dataPortTo']");
    @RenderWebElement
    private final static By FTP_DATAPORT_FROM = By.cssSelector("input[name$='dataPortFrom']");

    //CIFS
    @RenderWebElement
    private final static By CIFS_ENABLED_CHECKBOX = By.cssSelector("input[onchange*='cifs.enabled']");
    @RenderWebElement
    private final static By CIFS_SERVER_NAME = By.cssSelector("input[name$='cifs.serverName']");
    @RenderWebElement
    private final static By CIFS_DOMAIN = By.cssSelector("input[name$='cifs.domain']");
    @RenderWebElement
    private final static By CIFS_HOST_ANNOUNCE = By.cssSelector("input[onchange*='cifs.hostannounce']");
    @RenderWebElement
    private final static By CIFS_SESSION_TIMEOUT = By.cssSelector("input[name$='cifs.sessionTimeout']");

    @SuppressWarnings("unchecked")
    @Override
    public synchronized FileServersPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileServersPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }
    /**
     * Change FTP port
     *
     * @param port String
     *
     */

    public HtmlPage configFtpPort(String port)
    {
        fillField(FTP_PORT_INPUT, port);
        click(SAVE_BUTTON);
        return getCurrentPage();
    }


    /**
     * Method to get value of the FTP port.
     *
     * @return String
     */
    public String getPort()
    {
        return findAndWait(FTP_PORT_INPUT).getAttribute("value");
    }

    /**
     * Method to enable or disable the FTP server
     */

    public HtmlPage selectFtpEnabledCheckbox()
    {
        findAndWait(FTP_ENABLED_CHECKBOX).click();
        click(SAVE_BUTTON);
        return getCurrentPage();
    }

    /**
     * Is check box selected
     *
     * @return - Boolean
     */
    public boolean isFtpEnabledSelected()
    {
        try
        {
            return (driver.findElement(FTP_ENABLED_CHECKBOX).isSelected());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
}
