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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.alfresco.po.PageElement;
import org.alfresco.po.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * @author Marina.Nenadovets
 */
public class LinkDirectoryInfo extends PageElement
{
    private static final By EDIT_LINK = By.cssSelector(".edit-link>a");
    private final static By TAGS_LINK = By.xpath(".//span[@class='tag-item']/span[@class='tag']/a");
    private final static By SELECT = By.xpath(".//input[@type='checkbox']");

    AddLinkForm addLinkForm;
    @FindBy(css=".edit-link>a")Link edit;
    /**
     * Method to click Edit
     *
     * @return AddLink form
     */
    public AddLinkForm clickEdit()
    {
        edit.click();
        return addLinkForm;
    }

    @FindBy(css=".delete-link>a") Link delete;
    /**
     * Method to click Delete
     */
    public void clickDelete()
    {
        delete.click();
    }

    /**
     * Method to verify whether edit link is displayed
     *
     * @return boolean
     */
    public boolean isEditDisplayed()
    {
        return findElement(EDIT_LINK).isDisplayed();
    }

    /**
     * Method to verify whether delete link is displayed
     *
     * @return boolean
     */
    public boolean isDeleteDisplayed()
    {
        return delete.isDisplayed();
    }

    /**
     * Mimic select tag in left filter panel.
     *
     * @param tagName String
     */
    public LinksPage clickOnTag(String tagName)
    {
        checkNotNull(tagName);
        List<WebElement> tagElements = driver.findElements(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            if (tagName.equals(tagElement.getText()))
            {
                tagElement.click();
                return factoryPage.instantiatePage(driver,LinksDetailsPage.class).waitUntilAlert().render();
            }
        }
        throw new PageException(String.format("Tag with name[%s] don't found.", tagName));
    }

    /**
     * Check is selected.
     *
     * @return boolean
     */
    public boolean isSelected()
    {
        WebElement element = findElement(SELECT);
        return element.isSelected();
    }

    /**
     * Mimic click on checkbox.
     */
    public void clickOnCheckBox()
    {
        WebElement element = findElement(SELECT);
        element.click();
    }

}
