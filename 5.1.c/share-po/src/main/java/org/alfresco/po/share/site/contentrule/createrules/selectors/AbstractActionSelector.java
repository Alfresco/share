/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.contentrule.createrules.selectors;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.PageElement;
import org.alfresco.po.share.site.contentrule.createrules.SetPropertyValuePage;
import org.alfresco.po.share.site.document.CopyOrMoveContentRulesPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

/**
 * User: aliaksei.bul Date: 08.07.13 Time: 12:12
 */
@FindBy(tagName="form")
public abstract class AbstractActionSelector extends PageElement
{
    @FindBy(css="ul[id$=ruleConfigAction-configs]>li select[class$='config-name']") WebElement actionsDropdown;
    private static final By SELECT_DESTINATION_BUTTON = By.cssSelector("span[class*='destination-dialog-button'] button");
    private static final By MIMETYPE_SELECT = By.cssSelector("select[title='Mimetype']");
    private static final By RUN_SCRIPTS_SELECT = By.xpath("//select[@class='suppress-validation']");
    private static final By SET_PROPERTY_VALUE_SELECT = By.cssSelector("span[class*='set-property-value'] button");



    protected void selectAction(int actionOptionNumber)
    {
        actionsDropdown.click();
        List<WebElement> options = actionsDropdown.findElements(By.tagName("option"));
        int count = 0;
        for (WebElement option : options)
        {
            if(count == actionOptionNumber)
            {
                option.click();
                return;
            }
            count++;
        }
    }

    protected CopyOrMoveContentRulesPage selectDestination(final String site, final String... folders)
    {
        List<WebElement> selectDestButtons = findAndWaitForElements(SELECT_DESTINATION_BUTTON);
        selectDestButtons.get(selectDestButtons.size() - 1).click();
        CopyOrMoveContentRulesPage copyOrMoveContentPage =
                factoryPage.instantiatePage(driver, CopyOrMoveContentRulesPage.class);
        copyOrMoveContentPage.selectSite(site).render();
        copyOrMoveContentPage.selectPath(folders).render();
        return copyOrMoveContentPage;
    }

    protected void selectTransformContent(String visibleText)
    {
        List<WebElement> mimeTypes = findAndWaitForElements(MIMETYPE_SELECT);
        List<Select> mimeTypesSelects = new ArrayList<Select>();
        for (WebElement mimeTypeElement : mimeTypes)
        {
            mimeTypesSelects.add(new Select(mimeTypeElement));
        }
        mimeTypesSelects.get(mimeTypesSelects.size() - 1).selectByVisibleText(visibleText);
    }

    protected void selectScript(String visibleName)
    {
        List<WebElement> scriptOptions = findAndWaitForElements(RUN_SCRIPTS_SELECT);
        List<Select> scriptSelects = new ArrayList<Select>();
        for (WebElement scriptOption : scriptOptions)
        {
            scriptSelects.add(new Select(scriptOption));
        }
        scriptSelects.get(scriptSelects.size() - 1).selectByVisibleText(visibleName);
    }

    protected CopyOrMoveContentRulesPage selectDestinationName(final String destinationName, final String... folders)
    {
        List<WebElement> selectDestButtons = findAndWaitForElements(SELECT_DESTINATION_BUTTON);
        selectDestButtons.get(selectDestButtons.size() - 1).click();
        CopyOrMoveContentRulesPage copyOrMoveContentPage = factoryPage.instantiatePage(driver, CopyOrMoveContentRulesPage.class);
        copyOrMoveContentPage.selectDestination(destinationName).render();
        copyOrMoveContentPage.selectPath(folders).render();
        return copyOrMoveContentPage;
    }

    protected SetPropertyValuePage selectPropertyValue(final String folderName, final String value)
    {
        List<WebElement> selectSetPropertyValueButtons = findAndWaitForElements(SET_PROPERTY_VALUE_SELECT);
        selectSetPropertyValueButtons.get(selectSetPropertyValueButtons.size() - 1).click();
        SetPropertyValuePage setValuePage = factoryPage.instantiatePage(driver, SetPropertyValuePage.class).render();
        setValuePage.selectPropertyTypeFolder(folderName);
        setValuePage.selectValueFromList(value);
        return factoryPage.instantiatePage(driver, SetPropertyValuePage.class).render();
    }

}
