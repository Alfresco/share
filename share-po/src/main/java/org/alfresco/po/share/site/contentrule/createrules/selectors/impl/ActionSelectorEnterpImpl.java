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
package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.site.contentrule.createrules.SetPropertyValuePage;
import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractActionSelector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:07
 */
public class ActionSelectorEnterpImpl extends AbstractActionSelector
{
    private static final By CHECK_IN_OPTIONS_BUTTON = By.cssSelector("span[class*='check-in'] button");
    private static final By LINK_TO_CATEGORY_SELECT_BUTTON = By.cssSelector("span[class*='category'] button");
    private static final By ASPECT_SELECT = By.cssSelector("select[title='aspect-name']");
    private static final By APPROVE_BUTTON = By.cssSelector("span[class*='simple-workflow'] button:not([disabled])");
    private static final By TYPE_SELECTOR = By.cssSelector("span[class*='specialise'] select[param='type-name']");

    private enum PerformActions
    {
        EXECUTE_SCRIPT(1),
        COPY(2),
        MOVE(3),
        CHECK_IN(4),
        CHECK_OUT(5),
        LINK_TO_CATEGORY(6),
        ADD_ASPECT(7),
        REMOVE_ASPECT(8),
        ADD_SIMPLE_WORKFLOW(9),
        SEND_MAIL(10),
        TRANSFORM_AND_COPY_CONTENT(11),
        TRANSFORM_AND_COPY_IMAGE(12),
        EXTRACT_COMMON_METADATA_FIELDS(13),
        IMPORT(14),
        SPECIALISE_TYPE(15),
        INCREMENT_COUNTER(16),
        SET_PROPERTY_VALUE(17);

        private final int numberPosition;

        PerformActions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }


    public void selectExecuteScript(String visibleName)
    {
        super.selectAction(PerformActions.EXECUTE_SCRIPT.numberPosition);
        super.selectScript(visibleName);

    }

    public void selectCopy(String siteName, String... folders)
    {
        super.selectAction(PerformActions.COPY.numberPosition);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectMove(String siteName, String... folders)
    {
        super.selectAction(PerformActions.MOVE.numberPosition);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    @Deprecated
    public void selectCheckIn()
    {
        super.selectAction(PerformActions.CHECK_IN.numberPosition);
        List<WebElement> checkInButtons = findAndWaitForElements(CHECK_IN_OPTIONS_BUTTON);
        checkInButtons.get(checkInButtons.size() - 1).click();
        // todo added logic for work with popUp menu.
    }

    public void selectCheckOut(String siteName, String... folders)
    {
        super.selectAction(PerformActions.CHECK_OUT.numberPosition);
        super.selectDestination(siteName, folders);
    }

    @Deprecated
    public void selectLinkToCategory()
    {
        super.selectAction(PerformActions.LINK_TO_CATEGORY.numberPosition);
        List<WebElement> selectButtons = findAndWaitForElements(LINK_TO_CATEGORY_SELECT_BUTTON);
        selectButtons.get(selectButtons.size() - 1).click();
        // todo added logic for work with popUp menu.
    }

    public void selectAddAspect(String visibleAspectName)
    {
        super.selectAction(PerformActions.ADD_ASPECT.numberPosition);
        selectAspectType(visibleAspectName);
    }

    public void selectRemoveAspect(String visibleAspectName)
    {
        super.selectAction(PerformActions.REMOVE_ASPECT.numberPosition);
        selectAspectType(visibleAspectName);
    }

    private void selectAspectType(String visibleAspectName)
    {
        List<WebElement> aspectElements = findAndWaitForElements(ASPECT_SELECT);
        List<Select> aspectSelects = new ArrayList<Select>();
        for (WebElement aspectElement : aspectElements)
        {
            aspectSelects.add(new Select(aspectElement));
        }
        aspectSelects.get(aspectSelects.size() - 1).selectByVisibleText(visibleAspectName);
    }

    public void selectTransformAndCopy(String visibleTypeText, String siteName, String... folders)
    {
        super.selectAction(PerformActions.TRANSFORM_AND_COPY_CONTENT.numberPosition);
        super.selectTransformContent(visibleTypeText);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectTransformAndCopyImg(String visibleTypeText, String siteName, String... folders)
    {
        super.selectAction(PerformActions.TRANSFORM_AND_COPY_IMAGE.numberPosition);
        super.selectTransformContent(visibleTypeText);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    @Deprecated
    public void selectSimpleWorkFlow()
    {
        super.selectAction(PerformActions.ADD_SIMPLE_WORKFLOW.numberPosition);
        List<WebElement> approveButtons = findAndWaitForElements(APPROVE_BUTTON);
        approveButtons.get(approveButtons.size() - 1).click();
        // todo add logic for work with PopUp menu.
    }

    public void selectExtractMetadata()
    {
        super.selectAction(PerformActions.EXTRACT_COMMON_METADATA_FIELDS.numberPosition);
    }

    public void selectImport(String siteName, String... folders)
    {
        super.selectAction(PerformActions.IMPORT.numberPosition);
        super.selectDestination(siteName, folders);
    }

    public void selectSpecialiseType(String visibleTypeText)
    {
        super.selectAction(PerformActions.SPECIALISE_TYPE.numberPosition);
        List<WebElement> typeElements = findAndWaitForElements(TYPE_SELECTOR);
        List<Select> typeSelects = new ArrayList<Select>();
        for (WebElement typeElement : typeElements)
        {
            typeSelects.add(new Select(typeElement));
        }
        typeSelects.get(typeSelects.size() - 1).selectByVisibleText(visibleTypeText);
    }

    public void selectIncrementCounter()
    {
        super.selectAction(PerformActions.INCREMENT_COUNTER.numberPosition);
    }

    public void selectMoveToDestination(String destinationName, String... folders)
    {
        super.selectAction(PerformActions.MOVE.numberPosition);
        super.selectDestinationName(destinationName, folders).selectOkButton();

    }

    public void selectSetPropertyValue(String folderName,String value)
    {
        selectSetPropertyValue();
        super.selectPropertyValue(folderName, value);
        SetPropertyValuePage selectValuePage = factoryPage.instantiatePage(driver, SetPropertyValuePage.class).render();
        selectValuePage.selectOkButton();

    }

    public void selectSetPropertyValue()
    {
        super.selectAction(PerformActions.SET_PROPERTY_VALUE.numberPosition);
    }

}
