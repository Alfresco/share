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
package org.alfresco.po.share.site.links;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class LinksListFilter extends PageElement
{

    private final static By BASE_FILTER_ELEMENT = By.xpath("//ul[@class='filterLink']");
    private final static By TAGS_LINK = By.xpath("//ul[@class='filterLink']//span[@class='tag']/a");


    public enum FilterOption
    {
        ALL_LINKS(".//span[@class='all']/a"),
        MY_LINKS(".//span[@class='user']/a"),
        RECENTLY_ADDED(".//span[@class='recent']/a");

        FilterOption(String xpath)
        {
            this.by = By.xpath(xpath);
        }

        public final By by;
    }

    public LinksListFilter(WebDriver driver)
    {
        setWrappedElement(findAndWait(BASE_FILTER_ELEMENT));
    }


    /**
     * Return List with visible tags name
     *
     * @return List<String>
     */
    public List<String> getTags()
    {
        List<String> tags = new ArrayList<String>();
        List<WebElement> tagElements = driver.findElements(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            tags.add(tagElement.getText());
        }
        return tags;
    }
}
