/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Represents a generic link, a warpper for {@link WebElement}
 * 
 * @author Michael Suzuki
 * @since 1.3
 */
public class ShareLink
{
    private final String description;
    private final String href;
    private final WebElement link;
    private WebDriver driver;
    private FactoryPage factoryPage;

    public ShareLink(final WebElement link, WebDriver driver, FactoryPage factoryPage)
    {
        this.driver = driver;
        this.link = link;
        this.href = link.getAttribute("href");
        this.description = link.getText();
        this.factoryPage = factoryPage;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ShareLink [description=");
        builder.append(description);
        builder.append(", href=");
        builder.append(href);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Gets text value of link
     */
    public String getDescription()
    {
        return description;
    }

    public String getHref()
    {
        return href;
    }

    public WebElement getLink()
    {
        return link;
    }

    /**
     * Actions a click on the link element.
     * 
     * @return {@link HtmlPage} page response of clicking the link
     */
    public HtmlPage click()
    {
        link.click();
        //FIXME replace factory static method
        return factoryPage.getPage(driver);
    }
    
    /**
     * Actions a click on the link element without verifying the page.
     * 
     */
    public void openLink()
    {
        link.click();
    }
}
